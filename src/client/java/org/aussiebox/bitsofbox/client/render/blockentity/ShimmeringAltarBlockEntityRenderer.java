package org.aussiebox.bitsofbox.client.render.blockentity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.RotationAxis;
import org.aussiebox.bitsofbox.blockentity.ShimmeringAltarBlockEntity;
import org.aussiebox.bitsofbox.util.BOBUtil;
import org.joml.Vector2f;

import java.util.List;

public class ShimmeringAltarBlockEntityRenderer implements BlockEntityRenderer<ShimmeringAltarBlockEntity> {
    private final ItemRenderer itemRenderer;

    public ShimmeringAltarBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ShimmeringAltarBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack stack = entity.getAffectedStack();
        if (stack.isEmpty()) return;
        if (entity.getWorld() == null) return;

        matrices.push();
        matrices.translate(0.5, 1.2, 0.5);

        float rotation = (entity.getWorld().getTime() + tickDelta);
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

        DefaultedList<ItemStack> ingredients = entity.getInventoryWithoutEmpty();
        List<Vector2f> translations = BOBUtil.calculateCirclePoints(0, 0, 0.75, entity.getWorld().getTime() * 0.005 + (tickDelta * 0.005), ingredients.size());
        for (ItemStack itemStack : ingredients) {
            Vector2f translation = translations.removeFirst();

            matrices.push();
            matrices.translate(0.5, 1.2, 0.5);
            matrices.translate(translation.x, 0, translation.y);
            matrices.scale(0.5F, 0.5F, 0.5F);

            rotation = (float) (entity.getWorld().getTime() * 0.7 + (tickDelta * 0.7));
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
