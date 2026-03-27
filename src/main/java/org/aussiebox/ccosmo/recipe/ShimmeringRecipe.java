package org.aussiebox.ccosmo.recipe;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableInt;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.recipe.inventory.ShimmeringAltarInventory;
import org.aussiebox.ccosmo.recipe.serializer.ShimmeringRecipeSerializer;
import org.aussiebox.ccosmo.util.CCOSMOUtil;

import java.util.List;
import java.util.UUID;

public class ShimmeringRecipe implements Recipe<ShimmeringAltarInventory> {
    public static final Identifier ID = CCOSMO.id("shimmering");

    @Getter private final Ingredient affectedIngredient;
    private final DefaultedList<Ingredient> ingredients;
    @Getter private final int borderProximity;
    @Getter private final int dragonProximity;
    @Getter private final ItemStack output;

    public ShimmeringRecipe(DefaultedList<Ingredient> ingredients, Ingredient affectedStack, int borderProximity, int dragonProximity, ItemStack output) {
        this.affectedIngredient = affectedStack;
        this.ingredients = ingredients;
        this.borderProximity = borderProximity;
        this.dragonProximity = dragonProximity;
        this.output = output;
    }

    @Override
    public boolean matches(ShimmeringAltarInventory input, World world) {
        double proximity = world.getWorldBorder().getDistanceInsideBorder(input.getBlockPos().getX(), input.getBlockPos().getZ());
        if (borderProximity != -1 && proximity > borderProximity) return false;
        if (dragonProximity != -1) {
            MinecraftServer server = world.getServer();
            if (server == null) return false;
            ServerPlayerEntity circEntity = server.getPlayerManager().getPlayer(UUID.fromString("fdf5edf6-f202-47fe-98f0-68a60d68b0d5"));
            if (circEntity == null) return false;
            if (!input.getBlockPos().isWithinDistance(circEntity.getPos(), dragonProximity)) return false;
        }
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
        return ShimmeringRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<ShimmeringRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
    }
}
