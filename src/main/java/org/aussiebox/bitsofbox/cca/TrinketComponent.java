package org.aussiebox.bitsofbox.cca;

import lombok.Getter;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.aussiebox.bitsofbox.BOB;
import org.aussiebox.bitsofbox.BOBConstants;
import org.aussiebox.bitsofbox.item.ModItems;
import org.aussiebox.bitsofbox.util.BOBUtil;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class TrinketComponent implements AutoSyncedComponent, ClientTickingComponent, ServerTickingComponent {
    public static final ComponentKey<TrinketComponent> KEY = ComponentRegistry.getOrCreate(BOB.id("trinket_component"), TrinketComponent.class);
    private final PlayerEntity player;

    @Getter
    private boolean wasFlyingLastTick = false;
    @Getter
    private boolean gliding = false;

    @Getter
    public double pyrrhianBeltFlightTime;

    private boolean shouldBeFlying = false;

    public TrinketComponent(PlayerEntity player) {
        this.player = player;
    }

    public void setGliding(boolean state) {
        this.gliding = state;
        this.sync();
    }

    public void setWasFlyingLastTick(boolean state) {
        this.wasFlyingLastTick = state;
        this.sync();
    }

    public void setPyrrhianBeltFlightTime(double time) {
        this.pyrrhianBeltFlightTime = Math.clamp(time, 0, BOBConstants.pyrrhianBeltFlightTimeMaximum);
        this.sync();
    }

    public void changePyrrhianBeltFlightTime(double time) {
        this.pyrrhianBeltFlightTime = Math.clamp(this.pyrrhianBeltFlightTime + time, 0, BOBConstants.pyrrhianBeltFlightTimeMaximum);
        this.sync();
    }

    @Override
    public void serverTick() {
        if (this.player instanceof ServerPlayerEntity serverPlayer) {
            PlayerAbilities abilities = serverPlayer.getAbilities();
            PlayerAbilities newAbilities = serverPlayer.getAbilities();

            if (BOBUtil.playerHasTrinket(this.player, ModItems.PYRRHIAN_BELT)) {
                if (this.pyrrhianBeltFlightTime > 0 && !abilities.allowFlying) {
                    newAbilities.allowFlying = true;
                    serverPlayer.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(newAbilities));
                } else if ((!player.isInCreativeMode() && !player.isSpectator()) && abilities.allowFlying) {
                    newAbilities.allowFlying = false;
                    serverPlayer.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(newAbilities));
                }

                if (shouldBeFlying) {
                    newAbilities.flying = true;
                    serverPlayer.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(newAbilities));

                    shouldBeFlying = false;
                }

                if (this.pyrrhianBeltFlightTime <= 0 && (!player.isInCreativeMode() && !player.isSpectator())) {
                    newAbilities.flying = false;
                    serverPlayer.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(newAbilities));
                }

                if (abilities.flying) {
                    Vec3d movement = serverPlayer.getMovement();
                    this.changePyrrhianBeltFlightTime(-Math.abs(movement.length()));

                    setGliding(false);
                    setWasFlyingLastTick(true);
                } else {
                    this.changePyrrhianBeltFlightTime(BOBConstants.pyrrhianBeltFlightTimeMaximum/100);

                    if (wasFlyingLastTick) setGliding(true);
                    setWasFlyingLastTick(false);
                }

            } else if ((!player.isInCreativeMode() && !player.isSpectator()) && abilities.allowFlying) {
                newAbilities.allowFlying = false;
                newAbilities.flying = false;
                serverPlayer.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(newAbilities));
            }

            if (serverPlayer.isOnGround()) setGliding(false);
        }
    }

    @Override
    public void clientTick() {
//        if (BOBUtil.playerHasTrinket(this.player, ModItems.PYRRHIAN_BELT)) {
//            if (player.jumping && this.pyrrhianBeltFlightTime > 0 && player.getAbilities().allowFlying && !player.getAbilities().flying) {
//                shouldBeFlying = true;
//            }
//        }
    }

    public void sync() {
        KEY.sync(this.player);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.pyrrhianBeltFlightTime = tag.contains("pyrrhianBeltFlightTime") ? tag.getDouble("pyrrhianBeltFlightTime") : 0;
        this.wasFlyingLastTick = tag.contains("wasFlyingLastTick") && tag.getBoolean("wasFlyingLastTick");
        this.gliding = tag.contains("gliding") && tag.getBoolean("gliding");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        tag.putDouble("pyrrhianBeltFlightTime", this.pyrrhianBeltFlightTime);
        tag.putBoolean("wasFlyingLastTick", this.wasFlyingLastTick);
        tag.putBoolean("gliding", this.gliding);
    }
}
