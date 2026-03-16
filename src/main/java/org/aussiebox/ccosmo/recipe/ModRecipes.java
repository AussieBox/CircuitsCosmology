package org.aussiebox.ccosmo.recipe;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.recipe.serializer.ShimmeringRecipeSerializer;

public class ModRecipes {
    public static final RecipeType<ShimmeringRecipe> SHIMMERING_TYPE = Registry.register(
            Registries.RECIPE_TYPE,
            CCOSMO.id("shimmering"),
            new RecipeType<ShimmeringRecipe>() {
                @Override
                public String toString() { return "shimmering"; }
            }
    );

    public static final RecipeSerializer<ShimmeringRecipe> SHIMMERING_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER,
            CCOSMO.id("shimmering"),
            new ShimmeringRecipeSerializer()
    );

    public static void init() {
        CCOSMO.LOGGER.info("Registering Recipes for " + CCOSMO.MOD_ID);
    }
}
