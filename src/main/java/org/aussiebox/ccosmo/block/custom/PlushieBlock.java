package org.aussiebox.ccosmo.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.block.ModBlocks;
import org.aussiebox.ccosmo.blockentity.ModBlockEntities;
import org.aussiebox.ccosmo.blockentity.PlushieBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlushieBlock extends BlockWithEntity implements Waterloggable {
    private static final MapCodec<PlushieBlock> CODEC = createCodec(PlushieBlock::new);
    public static final BooleanProperty WATERLOGGED;
    public static final EnumProperty<Direction> FACING;
    private static final VoxelShape SHAPE;

    public PlushieBlock(Settings settings) {
        super(settings);
    }

    protected @NotNull MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public static SoundEvent getSound(BlockState state) {
        SoundEvent ret = SoundEvents.BLOCK_WOOL_HIT;
        if (state.getBlock() == ModBlocks.CIRCUITWEAVER_PLUSHIE) {
            ret = SoundEvent.of(CCOSMO.id("block.circuitweaver_plushie.honk"));
        }

        return ret;
    }

    public @NotNull BlockRenderType getRenderType(@NotNull BlockState state) {
        return super.getRenderType(state);
    }

    public void onBlockBreakStart(@NotNull BlockState state, World world, @NotNull BlockPos pos, @NotNull PlayerEntity player) {
        if (!world.isClient) {
            Vec3d mid = Vec3d.ofCenter(pos);
            float pitch = 1.2F + world.random.nextFloat() * 0.4F;
            BlockState note = world.getBlockState(pos.down());
            if (note.contains(Properties.NOTE)) {
                pitch = (float)Math.pow(2.0F, (double)(note.get(Properties.NOTE) - 12) / (double)12.0F);
            }

            BlockEntity var9 = world.getBlockEntity(pos);
            if (var9 instanceof PlushieBlockEntity plushie) {
                plushie.squish(24);
            }
        }

    }

    protected void spawnBreakParticles(World world, @NotNull PlayerEntity player, @NotNull BlockPos pos, @NotNull BlockState state) {
        BlockEntity var6 = world.getBlockEntity(pos);
        if (var6 instanceof PlushieBlockEntity plushie) {
            plushie.squish(4);
        }

        super.spawnBreakParticles(world, player, pos, state);
    }

    protected @NotNull ActionResult onUse(@NotNull BlockState state, World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull BlockHitResult hit) {
        if (!world.isClient) {
            Vec3d mid = Vec3d.ofCenter(pos);
            float pitch = 0.8F + world.random.nextFloat() * 0.4F;
            BlockState note = world.getBlockState(pos.down());
            if (note.contains(Properties.NOTE)) {
                pitch = (float)Math.pow(2.0F, (double)(note.get(Properties.NOTE) - 12) / (double)12.0F);
            }

            world.playSound(null, mid.getX(), mid.getY(), mid.getZ(), getSound(state), SoundCategory.BLOCKS, 1.0F, 1.0F);
            BlockEntity var10 = world.getBlockEntity(pos);
            if (var10 instanceof PlushieBlockEntity plushie) {
                plushie.squish(1);
            }
        }

        return ActionResult.SUCCESS;
    }

    public @NotNull VoxelShape getOutlineShape(@NotNull BlockState state, @NotNull BlockView world, @NotNull BlockPos pos, @NotNull ShapeContext context) {
        return SHAPE;
    }

    public boolean hasSidedTransparency(@NotNull BlockState state) {
        return true;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull World world, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.PLUSHIE_BLOCK_ENTITY, PlushieBlockEntity::tick);
    }

    public @Nullable BlockEntity createBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new PlushieBlockEntity(pos, state);
    }

    public BlockState getPlacementState(@NotNull ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(WATERLOGGED, fluidState.isOf(Fluids.WATER));
    }

    public @NotNull BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public @NotNull BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    protected void appendProperties(StateManager.@NotNull Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    public @NotNull BlockState getStateForNeighborUpdate(BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull WorldAccess world, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public @NotNull FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    static {
        WATERLOGGED = Properties.WATERLOGGED;
        FACING = Properties.HORIZONTAL_FACING;
        SHAPE = createCuboidShape(3.0F, 0.0F, 3.0F, 13.0F, 15.0F, 13.0F);
    }
}

