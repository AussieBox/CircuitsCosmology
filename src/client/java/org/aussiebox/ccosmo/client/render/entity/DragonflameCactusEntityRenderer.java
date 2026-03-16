package org.aussiebox.ccosmo.client.render.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.client.model.entity.DragonflameCactusEntityModel;
import org.aussiebox.ccosmo.entity.DragonflameCactusEntity;

public class DragonflameCactusEntityRenderer extends EntityRenderer<DragonflameCactusEntity> {
    protected DragonflameCactusEntityModel model;

    public DragonflameCactusEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new DragonflameCactusEntityModel(context.getPart(DragonflameCactusEntityModel.CACTUS));
    }

    @Override
    public Identifier getTexture(DragonflameCactusEntity entity) {
        return null;
    }

    @Override
    public void render(DragonflameCactusEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(3.5F));
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(25.25F));
        matrices.translate(0.3, 0.34, 0.525);
        matrices.scale(0.65F, 0.65F, 0.65F);

        VertexConsumer vertexconsumer = ItemRenderer.getItemGlintConsumer(vertexConsumers,
                 this.model.getLayer(Identifier.of(CCOSMO.MOD_ID, "textures/entity/dragonflame_cactus_entity.png")), false, false);
        this.model.render(matrices, vertexconsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }

}
