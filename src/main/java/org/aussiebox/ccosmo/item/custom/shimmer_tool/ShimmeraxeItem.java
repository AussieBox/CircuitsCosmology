package org.aussiebox.ccosmo.item.custom.shimmer_tool;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.aussiebox.ccosmo.CCOSMOConstants;
import org.aussiebox.ccosmo.cca.ShimmerComponent;
import org.aussiebox.ccosmo.component.ModDataComponentTypes;
import org.aussiebox.ccosmo.item.ModItems;
import org.aussiebox.ccosmo.item.custom.ShimmerToolItem;
import org.aussiebox.ccosmo.util.CCOSMOUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ShimmeraxeItem extends ShimmerToolItem {
    protected static final Map<Block, Block> STRIPPED_BLOCKS;

    public ShimmeraxeItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        PlayerEntity user = context.getPlayer();

        if (user != null && !user.isSneaking()) {
            World world = context.getWorld();
            BlockPos blockPos = context.getBlockPos();
            if (shouldCancelStripAttempt(context)) {
                return ActionResult.PASS;
            } else {
                Optional<BlockState> optional = this.tryStrip(world, blockPos, user, world.getBlockState(blockPos));
                if (optional.isEmpty()) {
                    return ActionResult.PASS;
                } else {
                    ItemStack itemStack = context.getStack();
                    if (user instanceof ServerPlayerEntity) {
                        Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity)user, blockPos, itemStack);
                    }

                    world.setBlockState(blockPos, optional.get(), 11);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(user, optional.get()));

                    boolean hasBorderlinked = CCOSMOUtil.stackHasEnchantment(world, stack, CCOSMOConstants.SHIMMERSEEP_ENCHANT);
                    if (ShimmerComponent.KEY.get(user).obtainmentsToday < 3 || hasBorderlinked) {
                        if (world.getRandom().nextBetween(1, hasBorderlinked ? 100-getAxeChanceModifier(world, stack) : 32-getAxeChanceModifier(world, stack)) == 1) {
                            Vec3d pos = CCOSMOUtil.shiftVecTowardsVec(context.getHitPos(), user.getEyePos(), 0.3);
                            ItemEntity itemEntity = new ItemEntity(world, pos.x, pos.y, pos.z, new ItemStack(ModItems.SHIMMER_POWDER));

                            int charges = stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, 0);
                            if (charges > 0) stack.set(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, charges-1);

                            if (!hasBorderlinked) ShimmerComponent.KEY.get(user).obtainPowder();
                            world.spawnEntity(itemEntity);
                        }
                    }

                    return ActionResult.success(world.isClient);
                }
            }
        }

        return ActionResult.PASS;
    }

    private int getAxeChanceModifier(World world, ItemStack stack) {
        int charges = stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, 0);

        if (CCOSMOUtil.stackHasEnchantment(world, stack, CCOSMOConstants.BORDERLINKED_ENCHANT)) return charges * 3;
        return charges;
    }

    private Optional<BlockState> tryStrip(World world, BlockPos pos, @Nullable PlayerEntity player, BlockState state) {
        Optional<BlockState> optional = this.getStrippedState(state);
        if (optional.isPresent()) {
            world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return optional;
        } else {
            Optional<BlockState> optional2 = Oxidizable.getDecreasedOxidationState(state);
            if (optional2.isPresent()) {
                world.playSound(player, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.syncWorldEvent(player, 3005, pos, 0);
                return optional2;
            } else {
                Optional<BlockState> optional3 = Optional.ofNullable((Block)((BiMap<?, ?>) HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get()).get(state.getBlock())).map((block) -> block.getStateWithProperties(state));
                if (optional3.isPresent()) {
                    world.playSound(player, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.syncWorldEvent(player, 3004, pos, 0);
                    return optional3;
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    private static boolean shouldCancelStripAttempt(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        if (playerEntity == null) return false;
        return context.getHand().equals(Hand.MAIN_HAND) && playerEntity.getOffHandStack().isOf(Items.SHIELD) && !playerEntity.shouldCancelInteraction();
    }

    private Optional<BlockState> getStrippedState(BlockState state) {
        return Optional.ofNullable(STRIPPED_BLOCKS.get(state.getBlock())).map((block) -> block.getDefaultState().with(PillarBlock.AXIS, state.get(PillarBlock.AXIS)));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> list, TooltipType type) {
        if (stack.getHolder() != null && CCOSMOUtil.stackHasEnchantment(stack.getHolder().getWorld(), stack, CCOSMOConstants.BORDERLINKED_ENCHANT)) {
            int modifier = stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, 0) * 3;
            list.add(1, Text.translatable("item.ccosmo.shimmeraxe.tooltip.borderlinked.1", (100-modifier)).withColor(0xAAAAAA));
            list.add(2, Text.translatable("item.ccosmo.shimmeraxe.tooltip.borderlinked.2").withColor(0xAAAAAA));
            list.add(3, Text.translatable("item.ccosmo.shimmeraxe.tooltip.borderlinked.3").withColor(0xAAAAAA));
            list.add(4, Text.translatable("item.ccosmo.shimmeraxe.tooltip.borderlinked.4").withColor(0xAAAAAA));
        } else {
            int modifier = stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, 0);
            list.add(1, Text.translatable("item.ccosmo.shimmeraxe.tooltip.1", (32-modifier)).withColor(0xAAAAAA));
            list.add(2, Text.translatable("item.ccosmo.shimmeraxe.tooltip.2").withColor(0xAAAAAA));
            list.add(3, Text.translatable("item.ccosmo.shimmeraxe.tooltip.3").withColor(0xAAAAAA));
            list.add(4, Text.translatable("item.ccosmo.shimmeraxe.tooltip.4").withColor(0xAAAAAA));
        }
        list.add(5, Text.literal(" "));

        super.appendTooltip(stack, context, list, type);
    }

    static {
        STRIPPED_BLOCKS = (new ImmutableMap.Builder()).put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD).put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD).put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD).put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG).put(Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_WOOD).put(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG).put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD).put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD).put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD).put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG).put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM).put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE).put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM).put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE).put(Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD).put(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG).put(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK).build();
    }
}
