package org.aussiebox.bitsofbox.client.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.aussiebox.bitsofbox.BOB;
import org.aussiebox.bitsofbox.entity.ShimmerforkEntity;

public class ShimmerforkEntityModel extends EntityModel<ShimmerforkEntity> {
    public static final Identifier TEXTURE = BOB.id("textures/item/shimmer_tools/base/trident_hand.png");
    private final ModelPart root;

    public ShimmerforkEntityModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("pole", ModelPartBuilder.create().uv(0, 6).cuboid(-0.5F, 2.0F, -0.5F, 1.0F, 25.0F, 1.0F), ModelTransform.NONE);
        modelPartData2.addChild("base", ModelPartBuilder.create().uv(4, 0).cuboid(-1.5F, 0.0F, -0.5F, 3.0F, 2.0F, 1.0F), ModelTransform.NONE);
        modelPartData2.addChild("left_spike", ModelPartBuilder.create().uv(4, 3).cuboid(-2.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F), ModelTransform.NONE);
        modelPartData2.addChild("middle_spike", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F), ModelTransform.NONE);
        modelPartData2.addChild("right_spike", ModelPartBuilder.create().uv(4, 3).mirrored().cuboid(1.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(ShimmerforkEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}
