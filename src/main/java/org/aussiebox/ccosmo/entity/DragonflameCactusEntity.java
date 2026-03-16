package org.aussiebox.ccosmo.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.aussiebox.ccosmo.cca.DragonflameCactusComponent;
import org.aussiebox.ccosmo.item.ModItems;
import org.aussiebox.ccosmo.util.CCOSMOUtil;

public class DragonflameCactusEntity extends PersistentProjectileEntity {
    public static final float explosionPower = 1.0F;

    public DragonflameCactusEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public DragonflameCactusEntity(World world, PlayerEntity player) {
        super(ModEntities.DragonflameCactusEntityType, player, world, ModItems.DRAGONFLAME_CACTUS.getDefaultStack(), null);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        World world = entity.getEntityWorld();
        if (!world.isClient()) {
            if (entity instanceof LivingEntity) {
                Vec3d pos = entityHitResult.getPos();
                world.createExplosion(this, pos.x, pos.y, pos.z, explosionPower, true, World.ExplosionSourceType.NONE);
                if (this.getOwner() instanceof ServerPlayerEntity player) {
                    if (player.distanceTo(entity) >= 25) {
                        CCOSMOUtil.grantAdvancement(player, "hit_dragonflame_cactus");
                    }
                }
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.getEntityWorld().isClient()) {
            this.setOnGround(true);
        }
    }

    @Override
    public void tick() {
        super.tick();
        DragonflameCactusComponent component = DragonflameCactusComponent.KEY.get(this);
        World world = this.getEntityWorld();
        if (!world.isClient()) {
            if (component.getTimer() <= 0) {
                world.createExplosion(this, this.prevX, this.prevY, this.prevZ, 2, true, World.ExplosionSourceType.NONE);
                this.kill();
            }
            if (component.getTimer() == 20) {
                world.playSound(this, this.getBlockPos(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.HOSTILE, 1.0F, 1.5F);
            }
            if (component.getTimer() <= 20) {
                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.SMOKE, this.prevX, this.prevY+0.45, this.prevZ, 1, 0, 0.1, 0, 0);
                }
            }
            if (component.getTimer() == 10) {
                if (this.getEntityWorld().getServer() != null) {
                    for (ServerPlayerEntity player : PlayerLookup.all(this.getEntityWorld().getServer())) {
                        if (player.distanceTo(this) <= 5) {
                            CCOSMOUtil.grantAdvancement(player, "witness_dragonflame_cactus");
                        }
                    }
                }
            }
            if (this.isOnFire()) {
                world.createExplosion(this, this.prevX, this.prevY, this.prevZ, explosionPower, true, World.ExplosionSourceType.NONE);
                this.kill();
            }
        }
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return ModItems.DRAGONFLAME_CACTUS.getDefaultStack();
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        return false;
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.ENCHANT_THORNS_HIT;
    }
}
