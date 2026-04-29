package org.aussiebox.ccosmo.client.render.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;

public class PyrrhianCuffRenderer implements TrinketRenderer {
    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> entityModel, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float v, float v1, float v2, float v3, float v4, float v5) {
        if (!(entityModel instanceof PlayerEntityModel<? extends LivingEntity> playerModel)) return;

        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        BakedModel bakedModel = itemRenderer.getModel(stack, livingEntity.getWorld(), livingEntity, livingEntity.getId());
        int light = WorldRenderer.getLightmapCoordinates(livingEntity.getWorld(), livingEntity.getBlockPos());

        matrices.push();
        if (livingEntity.getMainArm() == Arm.LEFT) playerModel.leftArm.rotate(matrices);
        else playerModel.rightArm.rotate(matrices);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180F));
        matrices.translate(0.065, +0.1, 0);

        itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, livingEntity.getMainArm() == Arm.LEFT, matrices, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV, bakedModel);

        matrices.pop();
    }
}
