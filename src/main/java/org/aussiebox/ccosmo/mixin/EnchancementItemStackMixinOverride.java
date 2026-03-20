package org.aussiebox.ccosmo.mixin;

import moriyashiine.enchancement.common.init.ModComponentTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;
import org.aussiebox.ccosmo.item.custom.ShimmerToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class, priority = 1001)
public class EnchancementItemStackMixinOverride {

    @Inject(method = "onClicked", at = @At("HEAD"), cancellable = true)
    private void ccosmo$enchancementToggleableOverridesOverride(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (!FabricLoader.getInstance().isModLoaded("enchancement")) return;
        if (clickType == ClickType.RIGHT && stack.contains(ModComponentTypes.TOGGLEABLE_PASSIVE)) {
            if (stack.getItem() instanceof ShimmerToolItem && !player.isSneaking()) return;
            stack.set(ModComponentTypes.TOGGLEABLE_PASSIVE, !stack.getOrDefault(ModComponentTypes.TOGGLEABLE_PASSIVE, true));
            if (player.getEntityWorld().isClient()) {
                player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
            }
            cir.setReturnValue(true);
        }
    }
}
