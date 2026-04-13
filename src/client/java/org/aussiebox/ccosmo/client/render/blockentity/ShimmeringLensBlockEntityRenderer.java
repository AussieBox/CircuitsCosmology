package org.aussiebox.ccosmo.client.render.blockentity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.blockentity.ShimmeringLensBlockEntity;
import org.joml.Matrix4f;

public class ShimmeringLensBlockEntityRenderer implements BlockEntityRenderer<ShimmeringLensBlockEntity> {
    public static final Identifier TEXTURE = CCOSMO.id("textures/entity/shimmering_lens_wall.png");

    public ShimmeringLensBlockEntityRenderer(BlockEntityRendererFactory.Context context) {}

    @Override
    public void render(ShimmeringLensBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getWorld() == null) return;
        Box box = entity.getBox();
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (box == null || player == null) return;
        
        float minX = (float) box.minX-entity.getPos().getX()-0.001F;
        float minY = (float) box.minY-entity.getPos().getY()-0.001F;
        float minZ = (float) box.minZ-entity.getPos().getZ()-0.001F;
        float maxX = (float) box.maxX-entity.getPos().getX()+0.001F;
        float maxY = (float) box.maxY-entity.getPos().getY()+0.001F;
        float maxZ = (float) box.maxZ-entity.getPos().getZ()+0.001F;

        float textureScale = 0.5f;
        float scrollSpeed = 0.01f;
        float time = (entity.getWorld().getTime() + tickDelta) * scrollSpeed;

        float sizeX = maxX - minX;
        float sizeY = maxY - minY;
        float sizeZ = maxZ - minZ;

        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(TEXTURE));

        // Front face
        consumer.vertex(positionMatrix, minX, minY, maxZ).color(-1).texture(time, time + (sizeY / textureScale)).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 0, 0, 1);
        consumer.vertex(positionMatrix, maxX, minY, maxZ).color(-1).texture(time + (sizeX / textureScale), time + (sizeY / textureScale)).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 0, 0, 1);
        consumer.vertex(positionMatrix, maxX, maxY, maxZ).color(-1).texture(time + (sizeX / textureScale), time).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 0, 0, 1);
        consumer.vertex(positionMatrix, minX, maxY, maxZ).color(-1).texture(time, time).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 0, 0, 1);

        // Back face
        consumer.vertex(positionMatrix, maxX, minY, minZ).color(-1).texture(time, time + (sizeY / textureScale)).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 0, 0, -1);
        consumer.vertex(positionMatrix, minX, minY, minZ).color(-1).texture(time + (sizeX / textureScale), time + (sizeY / textureScale)).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 0, 0, -1);
        consumer.vertex(positionMatrix, minX, maxY, minZ).color(-1).texture(time + (sizeX / textureScale), time).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 0, 0, -1);
        consumer.vertex(positionMatrix, maxX, maxY, minZ).color(-1).texture(time, time).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 0, 0, -1);
        
        // Left face
        consumer.vertex(positionMatrix, minX, minY, minZ).color(-1).texture(time, time + (sizeY / textureScale)).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), -1, 0, 0);
        consumer.vertex(positionMatrix, minX, minY, maxZ).color(-1).texture(time + (sizeZ / textureScale), time + (sizeY / textureScale)).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), -1, 0, 0);
        consumer.vertex(positionMatrix, minX, maxY, maxZ).color(-1).texture(time + (sizeZ / textureScale), time).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), -1, 0, 0);
        consumer.vertex(positionMatrix, minX, maxY, minZ).color(-1).texture(time, time).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), -1, 0, 0);

        // Right face
        consumer.vertex(positionMatrix, maxX, minY, maxZ).color(-1).texture(time, time + (sizeY / textureScale)).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 1, 0, 0);
        consumer.vertex(positionMatrix, maxX, minY, minZ).color(-1).texture(time + (sizeZ / textureScale), time + (sizeY / textureScale)).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 1, 0, 0);
        consumer.vertex(positionMatrix, maxX, maxY, minZ).color(-1).texture(time + (sizeZ / textureScale), time).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 1, 0, 0);
        consumer.vertex(positionMatrix, maxX, maxY, maxZ).color(-1).texture(time, time).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 1, 0, 0);

        // Top face
        consumer.vertex(positionMatrix, minX, maxY, maxZ).color(-1).texture(time, time + (sizeZ / textureScale)).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 0, 1, 0);
        consumer.vertex(positionMatrix, maxX, maxY, maxZ).color(-1).texture(time + (sizeX / textureScale), time + (sizeZ / textureScale)).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 0, 1, 0);
        consumer.vertex(positionMatrix, maxX, maxY, minZ).color(-1).texture(time + (sizeX / textureScale), time).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 0, 1, 0);
        consumer.vertex(positionMatrix, minX, maxY, minZ).color(-1).texture(time, time).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 0, 1, 0);

        // Bottom face
        consumer.vertex(positionMatrix, minX, minY, minZ).color(-1).texture(time, time + (sizeZ / textureScale)).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 0, -1, 0);
        consumer.vertex(positionMatrix, maxX, minY, minZ).color(-1).texture(time + (sizeX / textureScale), time + (sizeZ / textureScale)).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 0, -1, 0);
        consumer.vertex(positionMatrix, maxX, minY, maxZ).color(-1).texture(time + (sizeX / textureScale), time).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 0, -1, 0);
        consumer.vertex(positionMatrix, minX, minY, maxZ).color(-1).texture(time, time).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrices.peek(), 0, -1, 0);
    }

    @Override
    public boolean rendersOutsideBoundingBox(ShimmeringLensBlockEntity blockEntity) {
        return true;
    }
}
