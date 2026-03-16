package org.aussiebox.ccosmo.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.aussiebox.ccosmo.cca.TrinketComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tickFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setFlag(IZ)V", shift = At.Shift.AFTER))
    private void ccosmo$allowContinueGlidingWithPermissions(CallbackInfo ci) {
        if (!this.isPlayer()) return;

        if (TrinketComponent.KEY.get(this).isGliding() && !this.isSubmergedInWater())
            this.setFlag(7, true);
    }
}
