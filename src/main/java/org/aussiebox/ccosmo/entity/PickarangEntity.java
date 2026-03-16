package org.aussiebox.ccosmo.entity;

import net.minecraft.block.Block;
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
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.CCOSMOConstants;
import org.aussiebox.ccosmo.item.ModItems;

import java.util.Objects;

public class PickarangEntity extends ThrownEntity {
    private static final TrackedData<ItemStack> ITEM;
    private static final TrackedData<Integer> RETURN_TICKS;

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

    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(ITEM, new ItemStack(this.getDefaultItem()));
        builder.add(RETURN_TICKS, 1000000);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("Item", this.getStack().encode(this.getRegistryManager()));
        nbt.put("ReturnTicks", NbtInt.of(this.getReturnTicks()));
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains("Item", 10)) {
            this.setItem(ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("Item")).orElseGet(() -> new ItemStack(this.getDefaultItem())));
        } else {
            this.setItem(new ItemStack(this.getDefaultItem()));
        }

        if (nbt.contains("ReturnTicks", 3)) {
            this.setReturnTicks(nbt.getInt("ReturnTicks"));
        } else {
            this.setReturnTicks(1000000);
        }

    }

    static {
        ITEM = DataTracker.registerData(PickarangEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
        RETURN_TICKS = DataTracker.registerData(PickarangEntity.class, TrackedDataHandlerRegistry.INTEGER);
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
                drop();
                return;
            }
            world.breakBlock(pos, true, this.getOwner(), 100);
        }
        if (hitResult instanceof EntityHitResult entityHit) {
            DamageSource damageSource = this.getDamageSources().create(CCOSMOConstants.PICKARANG_DAMAGE, this.getOwner());
            float damage = 4.0F;
            entityHit.getEntity().damage(damageSource, damage);
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
                float g = 0.25F;
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

        if (this.getReturnTicks() <= -(CCOSMOConstants.shimmerpickReturnTime)-2) {
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
                player.sendPickup(itemEntity, 1);
                player.getInventory().insertStack(itemEntity.getStack());
                itemEntity.discard();
            }

        this.discard();
    }

    private TagKey<Block> getInverseBreakableTag() {
        return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
    }
}
