package org.aussiebox.ccosmo.client.render.blockentity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.aussiebox.ccosmo.blockentity.ShimmeringAltarBlockEntity;
import org.aussiebox.ccosmo.util.CCOSMOUtil;
import org.joml.Vector2f;

import java.util.List;

public class ShimmeringAltarBlockEntityRenderer implements BlockEntityRenderer<ShimmeringAltarBlockEntity> {
    private final ItemRenderer itemRenderer;
    private double circleRotation = 0;
    private float rotation = 0;

    public ShimmeringAltarBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ShimmeringAltarBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack stack = entity.getAffectedStack();
        if (stack.isEmpty()) return;
        if (entity.getWorld() == null) return;

        double craftTime = MathHelper.lerp(tickDelta, entity.getLastCraftAnimationTicks(), (double) entity.getCraftAnimationTicks())/40.0;
        double returnTime = MathHelper.lerp(tickDelta, entity.getLastReturnAnimationTicks(), (double) entity.getReturnAnimationTicks())/40.0;

        if (entity.getCraftAnimationTicks() > 0 && entity.getLastCraftAnimationTicks() <= entity.getCraftAnimationTicks()) craftTime = 1;
        if (entity.getReturnAnimationTicks() > 0 && entity.getLastReturnAnimationTicks() <= entity.getReturnAnimationTicks()) returnTime = 1;

        double yOffset = 0;
        if (entity.getCraftAnimationTicks() > 0) yOffset = CCOSMOUtil.smoothInterpolate(1, 0, craftTime, true);
        else if (entity.getReturnAnimationTicks() > 0) yOffset = CCOSMOUtil.smoothInterpolate(0, 1, returnTime, true);

        matrices.push();
        matrices.translate(0.5, yOffset + 1.2, 0.5);

        rotation += 0.5F;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));

        this.itemRenderer.renderItem(
                stack,
                ModelTransformationMode.GROUND,
                light,
                overlay,
                matrices,
                vertexConsumers,
                entity.getWorld(),
                0
        );

        matrices.pop();

        double radius = 0.75;
        if (entity.getCraftAnimationTicks() > 0) {
            circleRotation += CCOSMOUtil.smoothInterpolate(0.2, 0.001, craftTime, true);
            radius = CCOSMOUtil.smoothInterpolate(0.35, 0.75, craftTime, true);
        }
        circleRotation += 0.001;

        List<ItemStack> ingredients = entity.getInventoryWithoutEmpty();
        List<Vector2f> translations = CCOSMOUtil.calculateCirclePoints(0, 0, radius, circleRotation, ingredients.size());
        for (ItemStack itemStack : ingredients) {
            Vector2f translation = translations.removeFirst();

            matrices.push();
            matrices.translate(0.5, 1.2, 0.5);
            matrices.translate(translation.x, yOffset, translation.y);
            matrices.scale(0.5F, 0.5F, 0.5F);

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));

            this.itemRenderer.renderItem(
                    itemStack,
                    ModelTransformationMode.GROUND,
                    light,
                    overlay,
                    matrices,
                    vertexConsumers,
                    entity.getWorld(),
                    0
            );

            matrices.pop();
        }
    }
}
