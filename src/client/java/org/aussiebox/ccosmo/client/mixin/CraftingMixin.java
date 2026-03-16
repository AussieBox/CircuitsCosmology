package org.aussiebox.ccosmo.client.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.component.ModDataComponentTypes;
import org.aussiebox.ccosmo.item.ModItems;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;

@Mixin(CraftingScreenHandler.class)
public class CraftingMixin {

    @Shadow
    @Final
    private RecipeInputInventory input;

    @Inject(method = "updateResult", at = @At(value = "TAIL"))
    private static void ccosmo$updateCraftingResult(ScreenHandler handler, World world, PlayerEntity player, RecipeInputInventory craftingInventory, CraftingResultInventory resultInventory, @Nullable RecipeEntry<CraftingRecipe> recipe, CallbackInfo ci) {
        ItemStack original = resultInventory.getStack(0);

        CraftingRecipeInput craftingRecipeInput = craftingInventory.createRecipeInput();
        Optional<RecipeEntry<CraftingRecipe>> optional = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingRecipeInput, world, recipe);
        if (optional.isEmpty()) return;


        RecipeEntry<CraftingRecipe> recipeEntry = optional.get();
        if (resultInventory.shouldCraftRecipe(world, (ServerPlayerEntity) player, recipeEntry)) {
            if (Objects.equals(recipeEntry.id(), CCOSMO.id("dragonflame_cactus_add"))) {
                for (ItemStack stack : craftingInventory.getHeldStacks()) {
                    if (stack.isOf(ModItems.DRAGONFLAME_CACTUS)) {
                        int fuse = stack.getOrDefault(ModDataComponentTypes.DRAGONFLAME_CACTUS_FUSE, 20);
                        original.set(ModDataComponentTypes.DRAGONFLAME_CACTUS_FUSE, fuse + 10);

                        resultInventory.setStack(0, original);
                    }
                }
            } else if (Objects.equals(recipeEntry.id(), CCOSMO.id("dragonflame_cactus_subtract"))) {
                for (ItemStack stack : craftingInventory.getHeldStacks()) {
                    if (stack.isOf(ModItems.DRAGONFLAME_CACTUS)) {
                        int fuse = stack.getOrDefault(ModDataComponentTypes.DRAGONFLAME_CACTUS_FUSE, 20);
                        original.set(ModDataComponentTypes.DRAGONFLAME_CACTUS_FUSE, Math.max(fuse - 2, 1));

                        resultInventory.setStack(0, original);
                    }
                }
            }
        }
    }
}
