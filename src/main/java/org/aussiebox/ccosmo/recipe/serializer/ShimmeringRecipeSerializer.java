package org.aussiebox.ccosmo.recipe.serializer;

import com.mojang.serialization.Codec;
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
    public static final ShimmeringRecipeSerializer INSTANCE = new ShimmeringRecipeSerializer();

    public static final MapCodec<ShimmeringRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients")
                    .xmap(list -> {
                        DefaultedList<Ingredient> def = DefaultedList.of();
                        def.addAll(list);
                        return def;
                    }, list -> list)
                    .forGetter(ShimmeringRecipe::getIngredients),
            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("affected").forGetter(ShimmeringRecipe::getAffectedIngredient),
            Codec.INT.optionalFieldOf("border_proximity", -1).forGetter(ShimmeringRecipe::getBorderProximity),
            Codec.INT.optionalFieldOf("dragon_proximity", -1).forGetter(ShimmeringRecipe::getDragonProximity),
            ItemStack.OPTIONAL_CODEC.fieldOf("result").forGetter(r -> r.getResult(null))
    ).apply(instance, ShimmeringRecipe::new));

    public static final PacketCodec<RegistryByteBuf, ShimmeringRecipe> PACKET_CODEC = PacketCodec.tuple(
            Ingredient.PACKET_CODEC.collect(PacketCodecs.toList()),
            recipe -> recipe.getIngredients().stream().toList(),

            Ingredient.PACKET_CODEC,
            ShimmeringRecipe::getAffectedIngredient,

            PacketCodecs.INTEGER,
            ShimmeringRecipe::getBorderProximity,

            PacketCodecs.INTEGER,
            ShimmeringRecipe::getDragonProximity,

            ItemStack.PACKET_CODEC,
            ShimmeringRecipe::getOutput,

            (ingredientsList, affected, borderProximity, dragonProximity, result) -> {
                DefaultedList<Ingredient> ingredients = DefaultedList.of();
                ingredients.addAll(ingredientsList);
                return new ShimmeringRecipe(ingredients, affected, borderProximity, dragonProximity, result);
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
