package org.aussiebox.ccosmo.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.Colors;
import org.jetbrains.annotations.NotNull;

public class ShimmeringAltarParticle extends AnimatedParticle {
    private final SpriteProvider sprites;

    protected ShimmeringAltarParticle(ClientWorld clientWorld, double x, double y, double z, SpriteProvider sprites, float scale) {
        super(clientWorld, x, y, z, sprites, 0.0125F);
        this.sprites = sprites;
        this.scale = scale;
        this.velocityMultiplier = 0.9F;
        this.maxAge = 20 + random.nextBetween(0, 20);
        this.angle = random.nextBetween(0, 360);
        this.setTargetColor(Colors.WHITE);
        this.setSprite(spriteProvider.getSprite(random));
    }

    @Override
    public void tick() {
        if (this.age++ >= this.maxAge)
            this.markDead();
        this.prevAngle = this.angle;
        this.angle += 0.05F;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider sprites;
        private final float scale;

        public Factory(SpriteProvider sprites, float scale) {
            this.sprites = sprites;
            this.scale = scale;
        }

        public Particle createParticle(@NotNull SimpleParticleType simpleParticleType, @NotNull ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new ShimmeringAltarParticle(clientWorld, d, e, f, this.sprites, scale);
        }
    }
}