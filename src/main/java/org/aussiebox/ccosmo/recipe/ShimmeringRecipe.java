package org.aussiebox.ccosmo.recipe;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableInt;
import org.aussiebox.ccosmo.recipe.inventory.ShimmeringAltarInventory;
import org.aussiebox.ccosmo.util.CCOSMOUtil;

import java.util.List;

public class ShimmeringRecipe implements Recipe<ShimmeringAltarInventory> {
    @Getter private final Ingredient affectedIngredient;
    private final DefaultedList<Ingredient> ingredients;
    @Getter private final int borderProximity;
    @Getter private final ItemStack output;

    public ShimmeringRecipe(DefaultedList<Ingredient> ingredients, Ingredient affectedStack, int borderProximity, ItemStack output) {
        this.affectedIngredient = affectedStack;
        this.ingredients = ingredients;
        this.borderProximity = borderProximity;
        this.output = output;
    }

    @Override
    public boolean matches(ShimmeringAltarInventory input, World world) {
        double proximity = world.getWorldBorder().getDistanceInsideBorder(input.getBlockPos().getX(), input.getBlockPos().getZ());
        if (borderProximity != -1 && proximity > borderProximity) return false;
        if (!affectedIngredient.test(input.getAffectedStack())) return false;

        List<Pair<Ingredient, MutableInt>> condensedIngredients = CCOSMOUtil.condenseIngredients(ingredients);
        for (Pair<Ingredient, MutableInt> ingredientPair : condensedIngredients) {
            Ingredient ingredient = ingredientPair.getLeft();
            int targetCount = ingredientPair.getRight().toInteger();
            boolean testPass = false;
            boolean amountPass = false;
            for (Pair<ItemStack, MutableInt> stackPair : CCOSMOUtil.condenseStacks(input.getStacks())) {
                ItemStack stack = stackPair.getLeft();
                int stackCount = stackPair.getRight().toInteger();

                testPass = ingredient.test(stack);
                if (testPass)
                    amountPass = stackCount == targetCount;
                else continue;

                if (amountPass) break;
            }
            if (!testPass) return false;
            else if (!amountPass) return false;
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
