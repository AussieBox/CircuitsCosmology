package org.aussiebox.bitsofbox.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.aussiebox.bitsofbox.BOB;
import org.aussiebox.bitsofbox.BOBConstants;
import org.aussiebox.bitsofbox.component.ModDataComponentTypes;
import org.aussiebox.bitsofbox.item.ModItems;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow
    @Final
    private ItemModels models;

    @Shadow
    public abstract ItemModels getModels();

    @ModifyVariable(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    public BakedModel renderItem(BakedModel bakedModel, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) ModelTransformationMode renderMode) {

        ClientWorld world = MinecraftClient.getInstance().world;
        LivingEntity entity = stack.getHolder() instanceof LivingEntity ? (LivingEntity) stack.getHolder() : null;
        BakedModel newModel = null;

        if (stack.getItem() == ModItems.SHIMMERFORK && (renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.GROUND || renderMode == ModelTransformationMode.FIXED)) {
            newModel = getModels().getModelManager().getModel(ModelIdentifier.ofInventoryVariant(BOB.id("shimmerfork")));
        }

        if (newModel == null) return bakedModel;

        ClientWorld clientWorld = world instanceof ClientWorld ? world : null;
        BakedModel newModel2 = newModel.getOverrides().apply(newModel, stack, clientWorld, entity, 0);

        return newModel2 == null ? this.models.getModelManager().getMissingModel() : newModel2;
    }

    @ModifyVariable(
            method = "getModel",
            at = @At(value = "STORE"),
            ordinal = 1
    )
    public BakedModel getHeldItemModelMixin(BakedModel bakedModel, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) @Nullable World world, @Local(argsOnly = true) @Nullable LivingEntity entity, @Local(argsOnly = true) int seed) {

        BakedModel newModel = null;

        if (stack.getItem() == ModItems.SHIMMERFORK) {
            BOBConstants.ShimmerToolSkin skin = stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_SKIN, BOBConstants.ShimmerToolSkin.BASE);

            if (skin == BOBConstants.ShimmerToolSkin.BASE)
                newModel = getModels().getModelManager().getModel(ModelIdentifier.ofInventoryVariant(BOB.id("base_shimmerfork_hand")));
            if (skin == BOBConstants.ShimmerToolSkin.WOODEN_BINDING)
                newModel = getModels().getModelManager().getModel(ModelIdentifier.ofInventoryVariant(BOB.id("wooden_shimmerfork_hand")));
            if (skin == BOBConstants.ShimmerToolSkin.STONE_BINDING)
                newModel = getModels().getModelManager().getModel(ModelIdentifier.ofInventoryVariant(BOB.id("stone_shimmerfork_hand")));
            if (skin == BOBConstants.ShimmerToolSkin.COPPER_BINDING)
                newModel = getModels().getModelManager().getModel(ModelIdentifier.ofInventoryVariant(BOB.id("copper_shimmerfork_hand")));
            if (skin == BOBConstants.ShimmerToolSkin.GOLD_BINDING)
                newModel = getModels().getModelManager().getModel(ModelIdentifier.ofInventoryVariant(BOB.id("gold_shimmerfork_hand")));
            if (skin == BOBConstants.ShimmerToolSkin.IRON_BINDING)
                newModel = getModels().getModelManager().getModel(ModelIdentifier.ofInventoryVariant(BOB.id("iron_shimmerfork_hand")));
            if (skin == BOBConstants.ShimmerToolSkin.DIAMOND_BINDING)
                newModel = getModels().getModelManager().getModel(ModelIdentifier.ofInventoryVariant(BOB.id("diamond_shimmerfork_hand")));
            if (skin == BOBConstants.ShimmerToolSkin.NETHERITE_BINDING)
                newModel = getModels().getModelManager().getModel(ModelIdentifier.ofInventoryVariant(BOB.id("netherite_shimmerfork_hand")));
        }

        if (newModel == null) return bakedModel;

        ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld)world : null;
        BakedModel newModel2 = newModel.getOverrides().apply(newModel, stack, clientWorld, entity, seed);

        return newModel2 == null ? this.models.getModelManager().getMissingModel() : newModel2;
    }
}
