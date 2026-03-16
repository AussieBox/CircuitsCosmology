package org.aussiebox.ccosmo.mixin;

import moriyashiine.enchancement.common.screenhandlers.EnchantingTableScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.aussiebox.ccosmo.item.ModItems;
import org.aussiebox.ccosmo.item.custom.ShimmerToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantingTableScreenHandler.class)
public class EnchancementRepairMaterialMixin {
    @Inject(method = "getRepairIngredient(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/recipe/Ingredient;", at = @At("RETURN"), cancellable = true)
    private void ccosmo$setEnchancementRepairMaterial(ItemStack stack, CallbackInfoReturnable<Ingredient> cir) {
        if (stack.getItem() instanceof ShimmerToolItem) cir.setReturnValue(Ingredient.ofStacks(new ItemStack(ModItems.SHIMMER_POWDER)));
    }
}
