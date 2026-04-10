package org.aussiebox.ccosmo.entity;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.CCOSMOConstants;
import org.aussiebox.ccosmo.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PickarangEntity extends ThrownEntity {
    private static final TrackedData<ItemStack> ITEM;
    private static final TrackedData<Integer> RETURN_TICKS;
    private static final TrackedData<Integer> returnTime;

    public PickarangEntity(EntityType<? extends PickarangEntity> entityType, World world) {
        super(entityType, world);
    }

    public PickarangEntity(EntityType<? extends PickarangEntity> entityType, LivingEntity livingEntity, World world) {
        super(entityType, livingEntity, world);
    }

    public void setItem(ItemStack stack) {
        this.getDataTracker().set(ITEM, stack.copyWithCount(1));
    }

    protected Item getDefaultItem() {
        return ModItems.SHIMMERPICK;
    }

    public ItemStack getStack() {
        return this.getDataTracker().get(ITEM);
    }

    public void setReturnTicks(int ticks) {
        this.getDataTracker().set(RETURN_TICKS, ticks);
    }

    public Integer getReturnTicks() {
        return this.getDataTracker().get(RETURN_TICKS);
    }

    public void changeReturnTicks(int change) {
        this.getDataTracker().set(RETURN_TICKS, this.getDataTracker().get(RETURN_TICKS) + change);
    }

    public void setReturnTime(int ticks) {
        this.getDataTracker().set(returnTime, ticks);
    }

    public Integer getReturnTime() {
        return this.getDataTracker().get(returnTime);
    }

    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(ITEM, new ItemStack(this.getDefaultItem()));
        builder.add(RETURN_TICKS, 1000000);
        builder.add(returnTime, CCOSMOConstants.shimmerpickReturnTime);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("item", this.getStack().encode(this.getRegistryManager()));
        nbt.put("returnTicks", NbtInt.of(this.getReturnTicks()));
        nbt.put("returnTime", NbtInt.of(this.getReturnTime()));
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains("item", 10)) {
            this.setItem(ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("item")).orElseGet(() -> new ItemStack(this.getDefaultItem())));
        } else {
            this.setItem(new ItemStack(this.getDefaultItem()));
        }

        if (nbt.contains("returnTicks", 3)) {
            this.setReturnTicks(nbt.getInt("returnTicks"));
        } else {
            this.setReturnTicks(1000000);
        }

        if (nbt.contains("returnTime", 3)) {
            this.setReturnTime(nbt.getInt("returnTime"));
        } else {
            this.setReturnTime(CCOSMOConstants.shimmerpickReturnTime);
        }

    }

    static {
        ITEM = DataTracker.registerData(PickarangEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
        RETURN_TICKS = DataTracker.registerData(PickarangEntity.class, TrackedDataHandlerRegistry.INTEGER);
        returnTime = DataTracker.registerData(PickarangEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

    @Override
    protected double getGravity() {
        return 0.0;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (hitResult instanceof BlockHitResult blockHit) {
            BlockPos pos = blockHit.getBlockPos();
            World world = this.getEntityWorld();

            if (world.getBlockState(pos).isIn(getInverseBreakableTag()) || world.getBlockState(pos).isIn(TagKey.of(RegistryKeys.BLOCK, CCOSMO.id("pickarang_cannot_break")))) {
                setReturnTime(getReturnTicks());
                setReturnTicks(1);
                return;
            }
            breakBlock(world, pos, true, this.getOwner(), 100);
        }
        if (hitResult instanceof EntityHitResult entityHit) {
            DamageSource damageSource = this.getDamageSources().create(CCOSMOConstants.PICKARANG_DAMAGE, this.getOwner());
            entityHit.getEntity().damage(damageSource, 4.0F);
        }
    }

    public void breakBlock(World world, BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
        BlockState blockState = world.getBlockState(pos);
        if (!blockState.isAir()) {
            FluidState fluidState = world.getFluidState(pos);
            if (!(blockState.getBlock() instanceof AbstractFireBlock)) {
                world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(blockState));
            }

            if (drop) {
                BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(pos) : null;
                Block.dropStacks(blockState, world, pos, blockEntity, breakingEntity, getStack());
            }

            boolean bl = world.setBlockState(pos, fluidState.getBlockState(), Block.NOTIFY_ALL, maxUpdateDepth);
            if (bl) {
                world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(breakingEntity, blockState));
            }
        }
    }

    @Override
    public void tick() {
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.hitOrDeflect(hitResult);
        }

        if (getReturnTicks() >= 1000000) setReturnTicks(CCOSMOConstants.shimmerpickReturnTime);

        changeReturnTicks(-1);

        this.checkBlockCollision();
        Vec3d vec3d = this.getVelocity();
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;
        this.updateRotation();

        if (this.isTouchingWater()) {
            for(int i = 0; i < 4; ++i) {
                this.getWorld().addParticle(ParticleTypes.BUBBLE, d - vec3d.x * (double)0.25F, e - vec3d.y * (double)0.25F, f - vec3d.z * (double)0.25F, vec3d.x, vec3d.y, vec3d.z);
            }
        }

        if (getReturnTicks() == 0) {
            Vec3d invertedVec3d = new Vec3d(-this.getVelocity().getX(), -this.getVelocity().getY(), -this.getVelocity().getZ());
            d = this.getX() - vec3d.x;
            e = this.getY() - vec3d.y;
            f = this.getZ() - vec3d.z;

            this.setVelocity(invertedVec3d);
        } else {
            this.setVelocity(vec3d);
        }

        if (this.getReturnTicks() <= 0 && Objects.requireNonNull(this.getOwner()).distanceTo(this) < 2.5F) {
            drop();
            return;
        }

        if (this.getReturnTicks() <= -(getReturnTime()-2)) {
            drop();
            return;
        }

        this.applyGravity();
        this.setPosition(d, e, f);
    }

    public void drop() {
        World world = this.getEntityWorld();

        ItemEntity itemEntity = new ItemEntity(world, getX(), getY(), getZ(), getStack());
        world.spawnEntity(itemEntity);

        if (this.getOwner() instanceof PlayerEntity player)
            if (player.distanceTo(itemEntity) <= 8) {
                if (player.getInventory().insertStack(itemEntity.getStack())) {
                    player.sendPickup(itemEntity, 1);
                    itemEntity.discard();
                }
            }
        this.discard();
    }

    private TagKey<Block> getInverseBreakableTag() {
        return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
    }
}
