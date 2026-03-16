package org.aussiebox.ccosmo.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.blockentity.ModBlockEntities;
import org.aussiebox.ccosmo.blockentity.ShimmerglassBlockEntity;
import org.aussiebox.ccosmo.entity.PickarangEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class ShimmerglassBlock extends BlockWithEntity {
    public static final MapCodec<ShimmerglassBlock> CODEC = createCodec(ShimmerglassBlock::new);

    public ShimmerglassBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState adjacentState, Direction direction) {
        if (adjacentState.isIn(TagKey.of(RegistryKeys.BLOCK, CCOSMO.id("glass")))) return true;
        else return adjacentState.isOpaque();
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!(world.getBlockEntity(pos) instanceof ShimmerglassBlockEntity shimmerglass)) return;
        if (!Objects.equals(entity.getUuid().toString(), shimmerglass.getOwner().toString())) {
            entity.handleFallDamage(fallDistance, 1.2F, entity.getDamageSources().fall());
            return;
        }

        if (entity.isSneaking()) entity.handleFallDamage(fallDistance, 0.0F, entity.getDamageSources().fall());
        else entity.handleFallDamage(fallDistance, 0.5F, entity.getDamageSources().fall());
    }

    public static int getLuminance(BlockState state) {
        return 3;
    }

    public static boolean getEmissive(BlockState state, BlockView world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof ShimmerglassBlockEntity entity) {
            return entity.getTicksAliveLeft() != -1;
        }

        return false;
    }

    @Override
    protected float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof ShimmerglassBlockEntity blockEntity && blockEntity.getTicksAliveLeft() != -1) return 0.0F;
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShimmerglassBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!(world.getBlockEntity(pos) instanceof ShimmerglassBlockEntity blockEntity)) return;
        if (placer != null) blockEntity.setOwner(placer);
        blockEntity.makePermanent();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        EntityShapeContext entityContext = (EntityShapeContext) context;

        if (entityContext == null) return VoxelShapes.fullCube();
        if (entityContext.getEntity() == null) return VoxelShapes.fullCube();
        if (!(world.getBlockEntity(pos) instanceof ShimmerglassBlockEntity shimmerglass)) return VoxelShapes.fullCube();

        if (shimmerglass.getOwner() == null || shimmerglass.getOwner() == UUID.fromString("00000000-0000-0000-0000-000000000000")) return VoxelShapes.fullCube();

        if (entityContext.getEntity() instanceof PickarangEntity) return VoxelShapes.empty();

        if (entityContext.getEntity() != null && shimmerglass.getOwner() != null)
            if (Objects.equals(entityContext.getEntity().getUuid().toString(), shimmerglass.getOwner().toString())) {
                if (entityContext.getEntity().getY() >= pos.getY()+1 && !entityContext.getEntity().isSneaking()) return VoxelShapes.fullCube();
                return VoxelShapes.empty();
            }

        return VoxelShapes.fullCube();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.SHIMMERGLASS_BLOCK_ENTITY, ShimmerglassBlockEntity::tick);
    }
}
