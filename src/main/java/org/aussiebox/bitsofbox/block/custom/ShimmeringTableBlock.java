package org.aussiebox.bitsofbox.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.aussiebox.bitsofbox.blockentity.ModBlockEntities;
import org.aussiebox.bitsofbox.blockentity.ShimmeringTableBlockEntity;
import org.aussiebox.bitsofbox.item.ModItems;
import org.aussiebox.bitsofbox.recipe.ModRecipes;
import org.aussiebox.bitsofbox.recipe.ShimmeringRecipe;
import org.aussiebox.bitsofbox.recipe.inventory.ShimmeringTableInventory;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ShimmeringTableBlock extends BlockWithEntity {
    public static final MapCodec<ShimmeringTableBlock> CODEC = createCodec(ShimmeringTableBlock::new);
    private static final VoxelShape COLLISION_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 2.0F, 16.0F),
            Block.createCuboidShape(1.0F, 2.0F, 1.0F, 15.0F, 10.0F, 15.0F),
            Block.createCuboidShape(0.0F, 10.0F, 0.0F, 16.0F, 12.0F, 16.0F)
    );

    public ShimmeringTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShimmeringTableBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }
    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.OFF_HAND) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ShimmeringTableBlockEntity shimmeringBlockEntity)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        // TODO: Make multiple stacks combine as one, OR allow for item count requirements to be spread across stacks
        if (stack.isOf(ModItems.SHIMMER_POWDER)) {
            ShimmeringTableInventory inventory = shimmeringBlockEntity.toRecipeInventory();

            Optional<RecipeEntry<ShimmeringRecipe>> match = world.getRecipeManager().getFirstMatch(ModRecipes.SHIMMERING_TYPE, inventory, world);
            if (match.isEmpty()) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

            stack.decrement(1);
            shimmeringBlockEntity.setAffectedStack(match.get().value().getOutput());
            shimmeringBlockEntity.clear();
            return ItemActionResult.SUCCESS;
        }

        if (stack.isEmpty()) {
            if (!shimmeringBlockEntity.getInventoryWithoutEmpty().isEmpty()) {
                player.getInventory().setStack(player.getInventory().selectedSlot, shimmeringBlockEntity.getInventoryWithoutEmpty().getLast());
                shimmeringBlockEntity.fullyRemoveStack(shimmeringBlockEntity.getInventoryWithoutEmpty().getLast());
                return ItemActionResult.SUCCESS;
            } else if (!shimmeringBlockEntity.getAffectedStack().isEmpty()) {
                player.getInventory().setStack(player.getInventory().selectedSlot, shimmeringBlockEntity.getAffectedStack());
                shimmeringBlockEntity.setAffectedStack(ItemStack.EMPTY);
                return ItemActionResult.SUCCESS;
            }
        } else {
            if (shimmeringBlockEntity.getAffectedStack() == ItemStack.EMPTY) shimmeringBlockEntity.setAffectedStack(stack.copyWithCount(1));
            else shimmeringBlockEntity.addStack(stack.copyWithCount(1));

            stack.decrement(1);
            return ItemActionResult.SUCCESS;
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.SHIMMERING_TABLE_BLOCK_ENTITY, ShimmeringTableBlockEntity::tick);
    }
}
