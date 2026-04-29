package org.aussiebox.ccosmo.client.render.blockentity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.aussiebox.ccosmo.blockentity.PlushieBlockEntity;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class PlushieBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private final BlockRenderManager renderManager;

    public PlushieBlockEntityRenderer(BlockEntityRendererFactory.@NotNull Context ctx) {
        this.renderManager = ctx.getRenderManager();
    }

    public void render(@NotNull T entity, float tickDelta, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        double var10000;
        if (entity instanceof PlushieBlockEntity plushie) {
            var10000 = plushie.squash;
        } else {
            var10000 = 0.0F;
        }

        double squish = var10000;
        double lastSquish = squish * (double)3.0F;
        float squash = (float)Math.pow((double)1.0F - (double)1.0F / ((double)1.0F + MathHelper.lerp(tickDelta, lastSquish, squish)), 2.0F);
        matrices.scale(1.0F, 1.0F - squash, 1.0F);
        matrices.translate(0.5F, 0.0F, (double)0.5F);
        matrices.scale(1.0F + squash / 2.0F, 1.0F, 1.0F + squash / 2.0F);
        matrices.translate(-0.5F, 0.0F, (double)-0.5F);
        BlockState state = entity.getCachedState();
        BakedModel bakedModel = this.renderManager.getModel(state);
        this.renderManager.getModelRenderer().render(matrices.peek(), vertexConsumers.getBuffer(RenderLayers.getEntityBlockLayer(state, false)), state, bakedModel, 255.0F, 255.0F, 255.0F, light, overlay);
        matrices.pop();
    }
}
