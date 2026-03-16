package org.aussiebox.ccosmo.recipe.serializer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.collection.DefaultedList;
import org.aussiebox.ccosmo.recipe.ShimmeringRecipe;

public class ShimmeringRecipeSerializer implements RecipeSerializer<ShimmeringRecipe> {

    public static final MapCodec<ShimmeringRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients")
                    .xmap(list -> {
                        DefaultedList<Ingredient> def = DefaultedList.of();
                        def.addAll(list);
                        return def;
                    }, list -> list)
                    .forGetter(ShimmeringRecipe::getIngredients),
            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("affected").forGetter(ShimmeringRecipe::getAffectedIngredient),
            ItemStack.OPTIONAL_CODEC.fieldOf("result").forGetter(r -> r.getResult(null))
    ).apply(instance, ShimmeringRecipe::new));

    public static final PacketCodec<RegistryByteBuf, ShimmeringRecipe> PACKET_CODEC = PacketCodec.tuple(
            Ingredient.PACKET_CODEC.collect(PacketCodecs.toList()),
            recipe -> recipe.getIngredients().stream().toList(),

            Ingredient.PACKET_CODEC,
            ShimmeringRecipe::getAffectedIngredient,

            ItemStack.PACKET_CODEC,
            ShimmeringRecipe::getOutput,

            (ingredientsList, affected, result) -> {
                DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(ingredientsList.size(), Ingredient.EMPTY);
                for (int i = 0; i < ingredientsList.size(); i++) {
                    ingredients.set(i, ingredientsList.get(i));
                }
                return new ShimmeringRecipe(ingredients, affected, result);
            }
    );

    @Override
    public MapCodec<ShimmeringRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ShimmeringRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
