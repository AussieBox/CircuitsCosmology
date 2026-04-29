package org.aussiebox.ccosmo.client.mixin;

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
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.item.ModItems;
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
    public BakedModel ccosmo$renderItem(BakedModel bakedModel, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) ModelTransformationMode renderMode) {

        ClientWorld world = MinecraftClient.getInstance().world;
        LivingEntity entity = stack.getHolder() instanceof LivingEntity ? (LivingEntity) stack.getHolder() : null;
        BakedModel newModel = null;

        if (stack.isOf(ModItems.SHIMMERFORK) && (renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.GROUND || renderMode == ModelTransformationMode.FIXED))
            newModel = getModels().getModelManager().getModel(ModelIdentifier.ofInventoryVariant(CCOSMO.id("shimmerfork")));

        if (stack.isOf(ModItems.pyrrhian_cuff) && renderMode == ModelTransformationMode.FIXED)
            newModel = getModels().getModelManager().getModel(ModelIdentifier.ofInventoryVariant(CCOSMO.id("pyrrhian_cuff_body")));

        if (stack.isOf(ModItems.SHIMMER_JAR) && renderMode == ModelTransformationMode.FIXED)
            newModel = getModels().getModelManager().getModel(ModelIdentifier.ofInventoryVariant(CCOSMO.id("shimmer_jar_body")));

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
    public BakedModel ccosmo$getModel(BakedModel bakedModel, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) @Nullable World world, @Local(argsOnly = true) @Nullable LivingEntity entity, @Local(argsOnly = true) int seed) {
        BakedModel newModel = null;

        if (stack.isOf(ModItems.SHIMMERFORK))
            newModel = getModels().getModelManager().getModel(ModelIdentifier.ofInventoryVariant(CCOSMO.id("base_shimmerfork_hand")));

        if (newModel == null) return bakedModel;

        ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld)world : null;
        BakedModel newModel2 = newModel.getOverrides().apply(newModel, stack, clientWorld, entity, seed);

        return newModel2 == null ? this.models.getModelManager().getMissingModel() : newModel2;
    }
}
