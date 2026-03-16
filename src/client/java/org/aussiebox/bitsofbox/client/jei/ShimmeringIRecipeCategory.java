package org.aussiebox.bitsofbox.client.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.aussiebox.bitsofbox.BOB;
import org.aussiebox.bitsofbox.block.ModBlocks;
import org.aussiebox.bitsofbox.recipe.ShimmeringRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShimmeringIRecipeCategory implements IRecipeCategory<ShimmeringRecipe> {
    private final IDrawable icon;

    public ShimmeringIRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.SHIMMERING_ALTAR));
    }

    @Override
    public @NotNull RecipeType<ShimmeringRecipe> getRecipeType() {
        return RecipeType.create(BOB.MOD_ID, "shimmering", ShimmeringRecipe.class);
    }

    @Override
    public @NotNull Text getTitle() {
        return Text.translatable("jei.category.bitsofbox.shimmering");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, ShimmeringRecipe shimmeringRecipe, IFocusGroup iFocusGroup) {

    }
}
