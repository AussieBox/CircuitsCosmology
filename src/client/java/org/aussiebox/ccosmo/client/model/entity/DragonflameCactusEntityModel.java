package org.aussiebox.ccosmo.client.model.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.entity.DragonflameCactusEntity;

public class DragonflameCactusEntityModel extends EntityModel<DragonflameCactusEntity> {
	public static final EntityModelLayer CACTUS = new EntityModelLayer(Identifier.of(CCOSMO.MOD_ID, "dragonflame_cactus_entity"), "main");
	private final ModelPart bone;
	public DragonflameCactusEntityModel(ModelPart root) {
		super();
		this.bone = root.getChild("bone");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 0).cuboid(-12.0F, -12.0F, 4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
		.uv(0, 16).cuboid(-9.0F, -12.0F, 2.0F, 0.0F, 8.0F, 12.0F, new Dilation(0.0F))
		.uv(32, 0).cuboid(-14.0F, -12.0F, 5.0F, 12.0F, 8.0F, 0.0F, new Dilation(0.0F))
		.uv(48, 32).cuboid(-12.0F, -14.0F, 9.0F, 8.0F, 12.0F, 0.0F, new Dilation(0.0F))
		.uv(48, 44).cuboid(-12.0F, -14.0F, 7.0F, 8.0F, 12.0F, 0.0F, new Dilation(0.0F))
		.uv(0, 56).cuboid(-12.0F, -14.0F, 11.0F, 8.0F, 12.0F, 0.0F, new Dilation(0.0F))
		.uv(56, 0).cuboid(-12.0F, -14.0F, 5.0F, 8.0F, 12.0F, 0.0F, new Dilation(0.0F))
		.uv(32, 8).cuboid(-14.0F, -12.0F, 11.0F, 12.0F, 8.0F, 0.0F, new Dilation(0.0F))
		.uv(48, 16).cuboid(-14.0F, -12.0F, 7.0F, 12.0F, 8.0F, 0.0F, new Dilation(0.0F))
		.uv(48, 24).cuboid(-14.0F, -12.0F, 9.0F, 12.0F, 8.0F, 0.0F, new Dilation(0.0F))
		.uv(24, 16).cuboid(-7.0F, -12.0F, 2.0F, 0.0F, 8.0F, 12.0F, new Dilation(0.0F))
		.uv(0, 36).cuboid(-11.0F, -12.0F, 2.0F, 0.0F, 8.0F, 12.0F, new Dilation(0.0F))
		.uv(24, 36).cuboid(-5.0F, -12.0F, 2.0F, 0.0F, 8.0F, 12.0F, new Dilation(0.0F)), ModelTransform.rotation(8.0F, 24.0F, -8.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void setAngles(DragonflameCactusEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		bone.render(matrices, vertices, light, overlay);
	}

}