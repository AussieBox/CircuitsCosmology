package org.aussiebox.bitsofbox.recipe;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.aussiebox.bitsofbox.recipe.inventory.ShimmeringAltarInventory;

import java.util.Collections;

public class ShimmeringRecipe implements Recipe<ShimmeringAltarInventory> {
    @Getter
    private final Ingredient affectedIngredient;
    private final DefaultedList<Ingredient> ingredients;
    @Getter private final ItemStack output;

    public ShimmeringRecipe(DefaultedList<Ingredient> ingredients,Ingredient affectedStack, ItemStack output) {
        this.affectedIngredient = affectedStack;
        this.ingredients = ingredients;
        this.output = output;
    }

    @Override
    public boolean matches(ShimmeringAltarInventory input, World world) {
        if (!affectedIngredient.test(input.getAffectedStack())) return false;
        for (Ingredient ingredient : ingredients) {
            int count = Collections.frequency(ingredients, ingredient);
            boolean testPass = false;
            boolean amountPass = false;
            for (ItemStack stack : input.getIngredients()) {
                testPass = ingredient.test(stack);
                if (testPass)
                    amountPass = Collections.frequency(input.getIngredients(), stack) >= count;

                if (!testPass) continue;
                if (amountPass) break;
            }
            if (!testPass) return false;
            if (!amountPass) return false;
        }
        return true;
    }

    @Override
    public ItemStack craft(ShimmeringAltarInventory input, RegistryWrapper.WrapperLookup lookup) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return output.copy();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.addAll(ingredients);
        return defaultedList;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SHIMMERING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SHIMMERING_TYPE;
    }
}
