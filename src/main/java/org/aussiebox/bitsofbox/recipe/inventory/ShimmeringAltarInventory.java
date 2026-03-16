package org.aussiebox.bitsofbox.recipe.inventory;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.collection.DefaultedList;

public class ShimmeringAltarInventory implements RecipeInput {
    @Getter private final ItemStack affectedStack;
    @Getter private final DefaultedList<ItemStack> ingredients;
    @Getter private final RecipeMatcher recipeMatcher = new RecipeMatcher();

    public ShimmeringAltarInventory(ItemStack affectedStack, DefaultedList<ItemStack> ingredients) {
        this.affectedStack = affectedStack;
        this.ingredients = ingredients;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return ingredients.get(slot);
    }

    @Override
    public int getSize() {
        return ingredients.size()+1;
    }
}
