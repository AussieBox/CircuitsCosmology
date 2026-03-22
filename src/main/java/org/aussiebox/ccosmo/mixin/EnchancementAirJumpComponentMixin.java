package org.aussiebox.ccosmo.mixin;

import moriyashiine.enchancement.common.component.entity.AirJumpComponent;
import net.minecraft.entity.player.PlayerEntity;
import org.aussiebox.ccosmo.cca.TrinketComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AirJumpComponent.class)
public class EnchancementAirJumpComponentMixin {
    @Final @Mutable @Shadow private PlayerEntity obj;
    @Shadow private int jumpsLeft = 0;
    @Shadow private boolean hasAirJump = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void ccosmo$getAirJumpDetails(CallbackInfo ci) {
        TrinketComponent.KEY.get(obj).setEnchancementData(jumpsLeft, hasAirJump);
    }
}
