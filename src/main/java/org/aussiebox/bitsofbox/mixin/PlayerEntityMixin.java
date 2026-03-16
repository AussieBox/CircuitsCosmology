package org.aussiebox.bitsofbox.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.aussiebox.bitsofbox.cca.TrinketComponent;
import org.aussiebox.bitsofbox.item.custom.PyrrhianBeltItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;travel(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 1), cancellable = true)
    private void bitsofbox$travel(Vec3d movementInput, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object) this;
        if (player == null) return;
        if (player.hasVehicle()) return;

        TrinketComponent trinkets = TrinketComponent.KEY.get(player);

        if (trinkets.isFlying()) {
            double y = this.getVelocity().y;
            super.travel(movementInput);
            Vec3d velocity = this.getVelocity();
            this.setVelocity(velocity.x, y * 0.6, velocity.z);
            this.onLanding();
            this.setFlag(Entity.FALL_FLYING_FLAG_INDEX, false);

            ci.cancel();
        }
    }

    @ModifyReturnValue(method = "getOffGroundSpeed", at = @At(value = "RETURN"))
    private float bitsofbox$getOffGroundSpeed(float original) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player == null) return original;

        TrinketComponent trinkets = TrinketComponent.KEY.get(player);

        if (trinkets.isFlying() && !player.hasVehicle())
            return this.isSprinting() ? PyrrhianBeltItem.getBeltFlySpeed(player) * 2.0F : PyrrhianBeltItem.getBeltFlySpeed(player);

        return original;
    }

    @Inject(method = "updatePose", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setPose(Lnet/minecraft/entity/EntityPose;)V", by = -1))
    private void bitsofbox$updateCrouchPose(CallbackInfo ci, @Local(ordinal = 0) LocalRef<EntityPose> entityPose) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player == null) return;

        TrinketComponent trinkets = TrinketComponent.KEY.get(player);

        if (trinkets.isFlying()) entityPose.set(EntityPose.STANDING);
    }
}
