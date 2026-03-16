package org.aussiebox.ccosmo.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.aussiebox.ccosmo.cca.TrinketComponent;
import org.aussiebox.ccosmo.item.ModItems;
import org.aussiebox.ccosmo.item.custom.PyrrhianBeltItem;
import org.aussiebox.ccosmo.packet.PyrrhianBeltFlightC2SPacket;
import org.aussiebox.ccosmo.util.CCOSMOUtil;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow protected abstract boolean isCamera();

    @Shadow public abstract void tick();

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;allowFlying:Z", shift = At.Shift.BEFORE, opcode = Opcodes.GETFIELD))
    private void ccosmo$tickMovement(CallbackInfo ci, @Local(name = "bl") boolean bl) {
        ClientPlayerEntity player = (ClientPlayerEntity)(Object) this;
        if (player == null) return;

        if (player.isSwimming() || player.getAbilities().flying || player.isInCreativeMode() || player.isSpectator() || !CCOSMOUtil.playerHasTrinket(player, ModItems.PYRRHIAN_BELT)) return;

        TrinketComponent trinkets = TrinketComponent.KEY.get(player);

        if (trinkets.isFlying() && isCamera()) {
            int i = 0;

            if (player.input.sneaking) --i;
            if (player.input.jumping) ++i;

            if (i != 0) {
                player.setVelocity(player.getVelocity().add(0.0F, ((float)i * PyrrhianBeltItem.getBeltFlySpeed(player) * 3.0F), 0.0F));
            }
        }

        if (trinkets.isCanFly() && !bl && player.input.jumping) {
            if (this.abilityResyncCountdown == 0) {
                this.abilityResyncCountdown = 7;
            } else {
                ClientPlayNetworking.send(new PyrrhianBeltFlightC2SPacket(!TrinketComponent.KEY.get(player).isFlying()));
                if (trinkets.isFlying() && player.isOnGround()) {
                    player.jump();
                }
                this.abilityResyncCountdown = 0;
            }
        }
    }
}
