package org.aussiebox.ccosmo.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.aussiebox.ccosmo.block.ModBlocks;
import org.aussiebox.ccosmo.item.ModItems;

public class DragonflameCactusPlantBlock extends HorizontalFacingBlock {
    public static final MapCodec<DragonflameCactusPlantBlock> CODEC = createCodec(DragonflameCactusPlantBlock::new);

    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(1, 3, 4, 9, 11, 12);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(4, 3, 7, 12, 11, 15);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(4, 3, 1, 12, 11, 9);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(7, 3, 4, 15, 11, 12);

    public DragonflameCactusPlantBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(FACING) == Direction.EAST) {
            return EAST_SHAPE;
        } else if (state.get(FACING) == Direction.NORTH) {
            return NORTH_SHAPE;
        } else if (state.get(FACING) == Direction.SOUTH) {
            return SOUTH_SHAPE;
        } else if (state.get(FACING) == Direction.WEST) {
            return WEST_SHAPE;
        } else {
            return EAST_SHAPE;
        }
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(FACING) == Direction.EAST) {
            return EAST_SHAPE;
        } else if (state.get(FACING) == Direction.NORTH) {
            return NORTH_SHAPE;
        } else if (state.get(FACING) == Direction.SOUTH) {
            return SOUTH_SHAPE;
        } else if (state.get(FACING) == Direction.WEST) {
            return WEST_SHAPE;
        } else {
            return EAST_SHAPE;
        }
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.offset(state.get(FACING).getOpposite()));
        return blockState.isOf(ModBlocks.DRAGONFLAME_CACTUS_BLOCK);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, false);
            ItemStack itemStack = ModItems.DRAGONFLAME_CACTUS.getDefaultStack();

            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return ModItems.DRAGONFLAME_CACTUS.getDefaultStack();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
