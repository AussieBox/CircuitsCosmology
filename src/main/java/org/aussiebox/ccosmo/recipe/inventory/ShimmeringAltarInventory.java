package org.aussiebox.ccosmo.recipe.inventory;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class ShimmeringAltarInventory implements RecipeInput {
    @Getter private final ItemStack affectedStack;
    @Getter private final DefaultedList<ItemStack> stacks;
    @Getter private final BlockPos blockPos;
    @Getter private final RecipeMatcher recipeMatcher = new RecipeMatcher();

    public ShimmeringAltarInventory(ItemStack affectedStack, DefaultedList<ItemStack> ingredients, BlockPos blockPos) {
        this.affectedStack = affectedStack;
        this.stacks = ingredients;
        this.blockPos = blockPos;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return stacks.get(slot);
    }

    @Override
    public int getSize() {
        return stacks.size()+1;
    }
}
