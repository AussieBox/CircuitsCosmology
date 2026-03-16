package org.aussiebox.ccosmo.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.aussiebox.ccosmo.CCOSMOConstants;
import org.aussiebox.ccosmo.block.ModBlocks;
import org.aussiebox.ccosmo.blockentity.ShimmerglassBlockEntity;
import org.aussiebox.ccosmo.item.ModItems;
import org.aussiebox.ccosmo.util.CCOSMOUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ShimmerforkEntity extends PersistentProjectileEntity {
    private static final TrackedData<Byte> LOYALTY;
    private static final TrackedData<Boolean> ENCHANTED;
    private static final TrackedData<Integer> BLOCK_CHANGES_REMAINING;
    private static final TrackedData<Integer> LIFETIME_REMAINING;
    private boolean dealtDamage;
    private boolean returning;
    public int returnTimer;
    float damage = 7.0F;

    public ShimmerforkEntity(EntityType<? extends ShimmerforkEntity> entityType, World world) {
        super(entityType, world);
    }

    public ShimmerforkEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.FluidityTridentEntityType, owner, world, stack, null);
        this.setStack(stack.copy());
        this.dataTracker.set(LOYALTY, (byte)5);
        this.dataTracker.set(ENCHANTED, true);
        this.dataTracker.set(BLOCK_CHANGES_REMAINING, CCOSMOConstants.shimmerforkBlockChangeMaximum);
        this.dataTracker.set(LIFETIME_REMAINING, 200);
    }

    public ShimmerforkEntity(World world, double x, double y, double z, ItemStack stack) {
        super(ModEntities.FluidityTridentEntityType, x, y, z, world, stack, stack);
        this.setStack(stack.copy());
        this.dataTracker.set(LOYALTY, (byte)5);
        this.dataTracker.set(ENCHANTED, true);
        this.dataTracker.set(BLOCK_CHANGES_REMAINING, CCOSMOConstants.shimmerforkBlockChangeMaximum);
        this.dataTracker.set(LIFETIME_REMAINING, 200);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(LOYALTY, (byte)5);
        builder.add(ENCHANTED, true);
        builder.add(BLOCK_CHANGES_REMAINING, 10);
        builder.add(LIFETIME_REMAINING, 200);
    }

    @Override
    public void tick() {

        if (this.dataTracker.get(BLOCK_CHANGES_REMAINING) == 0)
            this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, this.getBlockPos(), GameEvent.Emitter.of(this, this.getWorld().getBlockState(this.getBlockPos())));

        this.dataTracker.set(LIFETIME_REMAINING, this.dataTracker.get(LIFETIME_REMAINING)-1);
        if (this.dataTracker.get(LIFETIME_REMAINING) == 0)
            this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, this.getBlockPos(), GameEvent.Emitter.of(this, this.getWorld().getBlockState(this.getBlockPos())));

        if (this.getWorld().isOutOfHeightLimit(this.getBlockPos()))
            this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, this.getBlockPos(), GameEvent.Emitter.of(this, this.getWorld().getBlockState(this.getBlockPos())));

        Entity entity = this.getOwner();
        int i = this.dataTracker.get(LOYALTY);
        if (i > 0 && (this.dealtDamage || this.returning) && entity != null) {
            if (!this.isOwnerAlive()) {
                if (!this.getWorld().isClient && this.pickupType == PickupPermission.ALLOWED) {
                    this.dropStack(this.asItemStack(), 0.1F);
                }

                this.discard();
            } else {
                Vec3d vec3d = entity.getEyePos().subtract(this.getPos());
                this.setPos(this.getX(), this.getY() + vec3d.y * 0.015 * (double)i, this.getZ());
                if (this.getWorld().isClient) {
                    this.lastRenderY = this.getY();
                }

                double d = 0.05 * (double)i;
                this.setVelocity(this.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(d)));
                if (this.returnTimer == 0) {
                    this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
                    onLand();
                }

                ++this.returnTimer;
            }
        }

        if (!this.dealtDamage) {
            List<Entity> entityList = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(0.5, 0.5, 0.5));
            if (entityList != null && !entityList.isEmpty()) {
                entity = entityList.getFirst();
                if (entity != null && entity != this.getOwner()) {
                    DamageSource damageSource = this.getDamageSources().create(CCOSMOConstants.SHIMMERFORK_DAMAGE, this.getOwner());

                    this.dealtDamage = true;
                    if (entity.damage(damageSource, damage)) {
                        if (entity.getType() == EntityType.ENDERMAN) {
                            return;
                        }

                        World world = this.getWorld();
                        if (world instanceof ServerWorld serverWorld) {
                            EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource, this.getWeaponStack());
                        }

                        if (entity instanceof LivingEntity livingEntity) {
                            this.knockback(livingEntity, damageSource);
                            this.onHit(livingEntity);
                        }
                    }

                    this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
                    this.playSound(SoundEvents.ITEM_TRIDENT_HIT, 1.0F, 1.0F);
                }
            }
        }

        Map<BlockPos, BlockState> blocksInCollision = CCOSMOUtil.getAllBlocksInBox(this.getWorld(), this.getBoundingBox().expand(
                0.9,
                (this.getVelocity().getY() > 0.7 || this.getVelocity().getY() < -0.7) ? 0.9 : 0.5,
                0.9
        ), false);
        if (blocksInCollision.isEmpty())
            super.applyGravity();

        super.tick();
    }

    private boolean isOwnerAlive() {
        Entity entity = this.getOwner();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
        } else {
            return false;
        }
    }

    public boolean isEnchanted() {
        return this.dataTracker.get(ENCHANTED);
    }

    public void onLand() {

        World world = this.getWorld();
        double range = Arrays.stream(CCOSMOConstants.shimmerforkLandEffectData).toList().getFirst();
        double blocksAffected = Arrays.stream(CCOSMOConstants.shimmerforkLandEffectData).toList().getLast();

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0);
            serverWorld.spawnParticles(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), (int) blocksAffected*3, range, range, range, 0);
        }

        List<BlockPos> blockPosList = CCOSMOUtil.getAllBlockPosInBox(new Box(this.getX()-range, this.getY()-range, this.getZ()-range, this.getX()+range, this.getY()+range, this.getZ()+range));
        Collections.shuffle(blockPosList);

        for (int i = 0; i < Math.min(blocksAffected, blockPosList.size()); i++) {
            BlockPos blockPos = blockPosList.get(i);

            if (this.getEntityWorld().getBlockEntity(blockPos) != null) continue;

            BlockState previousBlockState = this.getEntityWorld().getBlockState(blockPos);
            this.getEntityWorld().setBlockState(blockPos, ModBlocks.SHIMMERGLASS.getDefaultState());

            if (this.getEntityWorld().getBlockEntity(blockPos) instanceof ShimmerglassBlockEntity blockEntity) {
                blockEntity.setPreviousBlockState(previousBlockState);
                if (this.getOwner() != null) blockEntity.setOwner(this.getOwner());
                blockEntity.setTicksAliveLeft(this.getRandom().nextBetween(190, 210));
            }
        }
    }

    @Override
    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return (this.dealtDamage || this.returning) ? null : super.getEntityCollision(currentPosition, nextPosition);
    }

    @Override
    protected void onBlockHitEnchantmentEffects(ServerWorld world, BlockHitResult blockHitResult, ItemStack weaponStack) {
        Vec3d vec3d = blockHitResult.getBlockPos().clampToWithin(blockHitResult.getPos());
        Entity var6 = this.getOwner();
        LivingEntity var10002;
        if (var6 instanceof LivingEntity livingEntity) {
            var10002 = livingEntity;
        } else {
            var10002 = null;
        }

        EnchantmentHelper.onHitBlock(world, weaponStack, var10002, this, null, vec3d, world.getBlockState(blockHitResult.getBlockPos()), (item) -> this.kill());
    }

    @Override
    public ItemStack getWeaponStack() {
        return this.getItemStack();
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        return (this.dealtDamage || this.returning) && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack());
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.SHIMMERFORK);
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.isOwner(player) || this.getOwner() == null) {
            super.onPlayerCollision(player);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        this.dealtDamage = tag.contains("dealtDamage") && tag.getBoolean("dealtDamage");
        this.returning = tag.contains("returning") && tag.getBoolean("returning");
        this.dataTracker.set(LOYALTY, this.getLoyalty(this.getItemStack()));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.putBoolean("dealtDamage", this.dealtDamage);
        tag.putBoolean("returning", this.returning);
    }

    private byte getLoyalty(ItemStack stack) {
        World var3 = this.getWorld();
        if (var3 instanceof ServerWorld serverWorld) {
            return (byte) MathHelper.clamp(EnchantmentHelper.getTridentReturnAcceleration(serverWorld, stack, this), 0, 127);
        } else {
            return 0;
        }
    }

    @Override
    public void age() {
        int i = this.dataTracker.get(LOYALTY);
        if (this.pickupType != PickupPermission.ALLOWED || i <= 0) {
            super.age();
        }
    }

    @Override
    protected float getDragInWater() {
        return 0.99F;
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        super.onBlockCollision(state);
        if (this.dealtDamage || this.returning) return;
        int chargeCost = 0;

        List<BlockPos> blockPosList = CCOSMOUtil.getAllBlockPosInBox(this.getBoundingBox().expand(
                0.9,
                (this.getVelocity().getY() > 0.7 || this.getVelocity().getY() < -0.7) ? 0.9 : 0.5,
                0.9
        ));
        for (BlockPos blockPos : blockPosList) {
            if (!(this.getWorld().getBlockState(blockPos).isOf(Blocks.AIR)) && !(this.getWorld().getBlockState(blockPos).isOf(ModBlocks.SHIMMERGLASS))) {
                if (this.getWorld().getBlockState(blockPos).getCollisionShape(getWorld(), blockPos) != VoxelShapes.empty()) {
                    if (this.dataTracker.get(BLOCK_CHANGES_REMAINING) > 0) {
                        BlockState previousBlockState = this.getEntityWorld().getBlockState(blockPos);

                        if (this.getEntityWorld().getBlockEntity(blockPos) != null) continue;

                        this.getEntityWorld().setBlockState(blockPos, ModBlocks.SHIMMERGLASS.getDefaultState());

                        if (this.getEntityWorld().getBlockEntity(blockPos) instanceof ShimmerglassBlockEntity blockEntity) {
                            blockEntity.setPreviousBlockState(previousBlockState);
                            if (this.getOwner() != null) blockEntity.setOwner(this.getOwner());
                            blockEntity.resetTicksAliveLeft();
                        }

                        chargeCost = 1;
                    } else {
                        this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Emitter.of(this, this.getWorld().getBlockState(blockPos)));
                    }
                }
            }
        }
        this.dataTracker.set(BLOCK_CHANGES_REMAINING, this.dataTracker.get(BLOCK_CHANGES_REMAINING)-chargeCost);
    }

    @Override
    public boolean isNoClip() {
        BlockPos blockPos = this.getBlockPos();
        if (this.getWorld().getBlockState(blockPos).isOf(ModBlocks.SHIMMERGLASS) && this.dataTracker.get(BLOCK_CHANGES_REMAINING) == 0) {
            this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Emitter.of(this, this.getWorld().getBlockState(blockPos)));
            this.returning = true;
        }

        return (this.dealtDamage || this.returning) || this.dataTracker.get(BLOCK_CHANGES_REMAINING) > 0;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos blockPos = blockHitResult.getBlockPos();
            this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Emitter.of(this, this.getWorld().getBlockState(blockPos)));

            this.returning = true;
        }
    }

    static {
        LOYALTY = DataTracker.registerData(ShimmerforkEntity.class, TrackedDataHandlerRegistry.BYTE);
        ENCHANTED = DataTracker.registerData(ShimmerforkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        BLOCK_CHANGES_REMAINING = DataTracker.registerData(ShimmerforkEntity.class, TrackedDataHandlerRegistry.INTEGER);
        LIFETIME_REMAINING = DataTracker.registerData(ShimmerforkEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
}
