package org.aussiebox.bitsofbox.client.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.aussiebox.bitsofbox.BOB;
import org.aussiebox.bitsofbox.item.ModItems;
import org.aussiebox.bitsofbox.recipe.ModRecipes;
import org.aussiebox.bitsofbox.recipe.ShimmeringRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JEIPlugin implements IModPlugin {

    @Override
    public @NotNull Identifier getPluginUid() {
        return BOB.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new ShimmeringIRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
        IModPlugin.super.registerCategories(registration);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (MinecraftClient.getInstance().world != null) {
            List<ShimmeringRecipe> recipes = MinecraftClient.getInstance().world.getRecipeManager().listAllOfType(ModRecipes.SHIMMERING_TYPE).stream().map(RecipeEntry::value).toList();
            registration.addRecipes(RecipeType.create(BOB.MOD_ID, "shimmering", ShimmeringRecipe.class), recipes);
        }

        registration.addIngredientInfo(
                new ItemStack(ModItems.DRAGONFLAME_CACTUS),
                VanillaTypes.ITEM_STACK,
                Text.translatable("item.bitsofbox.dragonflame_cactus.jei.description")
        );
    }
}
