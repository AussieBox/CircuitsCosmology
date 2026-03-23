package org.aussiebox.ccosmo.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableInt;
import org.aussiebox.ccosmo.blockentity.ModBlockEntities;
import org.aussiebox.ccosmo.blockentity.ShimmeringAltarBlockEntity;
import org.aussiebox.ccosmo.item.ModItems;
import org.aussiebox.ccosmo.recipe.ModRecipes;
import org.aussiebox.ccosmo.recipe.ShimmeringRecipe;
import org.aussiebox.ccosmo.recipe.inventory.ShimmeringAltarInventory;
import org.aussiebox.ccosmo.util.CCOSMOUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ShimmeringAltarBlock extends BlockWithEntity {
    public static final MapCodec<ShimmeringAltarBlock> CODEC = createCodec(ShimmeringAltarBlock::new);
    private static final VoxelShape COLLISION_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 2.0F, 16.0F),
            Block.createCuboidShape(1.0F, 2.0F, 1.0F, 15.0F, 10.0F, 15.0F),
            Block.createCuboidShape(0.0F, 10.0F, 0.0F, 16.0F, 12.0F, 16.0F)
    );

    public ShimmeringAltarBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShimmeringAltarBlockEntity(pos, state);
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
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
        if (!(blockEntity instanceof ShimmeringAltarBlockEntity shimmeringBlockEntity)) return;

        if (!ShimmeringAltarBlockEntity.getAffectedStack().isEmpty()) {
            ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY()+0.5, pos.getZ(), ShimmeringAltarBlockEntity.getAffectedStack());
            entity.setToDefaultPickupDelay();
            world.spawnEntity(entity);
        }
        List<Pair<ItemStack, MutableInt>> inventory = CCOSMOUtil.condenseStacks(shimmeringBlockEntity.getInventoryWithoutEmpty());
        for (Pair<ItemStack, MutableInt> pair : inventory) {
            ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY()+0.5, pos.getZ(), pair.getLeft().copyWithCount(pair.getRight().toInteger()));
            entity.setToDefaultPickupDelay();
            world.spawnEntity(entity);
        }
        shimmeringBlockEntity.setAffectedStack(ItemStack.EMPTY);
        shimmeringBlockEntity.clear();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.OFF_HAND) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ShimmeringAltarBlockEntity shimmeringBlockEntity)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (ShimmeringAltarBlockEntity.getCraftAnimationTicks() > 0 || ShimmeringAltarBlockEntity.getReturnAnimationTicks() > 0) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (stack.isOf(ModItems.SHIMMER_POWDER)) {
            ShimmeringAltarInventory inventory = shimmeringBlockEntity.toRecipeInventory();

            List<RecipeEntry<ShimmeringRecipe>> recipes = world.getRecipeManager().listAllOfType(ModRecipes.SHIMMERING_TYPE);
            for (RecipeEntry<ShimmeringRecipe> recipe : recipes) {
                if (!recipe.value().matches(inventory, world)) continue;
                shimmeringBlockEntity.startCrafting(recipe.value());

                stack.decrement(1);

                player.getInventory().markDirty();
                player.playerScreenHandler.sendContentUpdates();
                return ItemActionResult.SUCCESS;
            }

            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (stack.isEmpty()) {
            if (!shimmeringBlockEntity.getInventoryWithoutEmpty().isEmpty()) {
                if (player.isSneaking()) {
                    ItemStack checkStack = shimmeringBlockEntity.getInventoryWithoutEmpty().getLast();
                    List<ItemStack> removedStacks = shimmeringBlockEntity.fullyRemoveStacks(checkStack, checkStack.getMaxCount());

                    shimmeringBlockEntity.fullyRemoveStacks(Items.AIR.getDefaultStack(), Collections.frequency(shimmeringBlockEntity.getInventoryWithoutEmpty(), Items.AIR.getDefaultStack())+1);

                    for (ItemStack removedStack : removedStacks) {
                        player.getInventory().offerOrDrop(removedStack.copy());
                        player.getInventory().markDirty();
                        player.playerScreenHandler.sendContentUpdates();
                    }
                    if (!removedStacks.isEmpty()) return ItemActionResult.SUCCESS;
                } else {
                    player.getInventory().offerOrDrop(shimmeringBlockEntity.getInventoryWithoutEmpty().getLast().copy());
                    shimmeringBlockEntity.fullyRemoveStack(shimmeringBlockEntity.getInventoryWithoutEmpty().getLast());
                    player.getInventory().markDirty();
                    player.playerScreenHandler.sendContentUpdates();
                    return ItemActionResult.SUCCESS;
                }
            } else if (!ShimmeringAltarBlockEntity.getAffectedStack().copy().isEmpty()) {
                player.getInventory().setStack(player.getInventory().selectedSlot, ShimmeringAltarBlockEntity.getAffectedStack().copy());
                shimmeringBlockEntity.setAffectedStack(ItemStack.EMPTY);

                player.getInventory().markDirty();
                player.playerScreenHandler.sendContentUpdates();
                return ItemActionResult.SUCCESS;
            }
        } else {
            if (shimmeringBlockEntity.size() > 64) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (ShimmeringAltarBlockEntity.getAffectedStack().copy() == ItemStack.EMPTY) shimmeringBlockEntity.setAffectedStack(stack.copyWithCount(1));
            else shimmeringBlockEntity.addStack(stack.copyWithCount(1));
            stack.decrement(1);

            player.getInventory().markDirty();
            player.playerScreenHandler.sendContentUpdates();
            return ItemActionResult.SUCCESS;
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.SHIMMERING_ALTAR_BLOCK_ENTITY, ShimmeringAltarBlockEntity::tick);
    }
}
