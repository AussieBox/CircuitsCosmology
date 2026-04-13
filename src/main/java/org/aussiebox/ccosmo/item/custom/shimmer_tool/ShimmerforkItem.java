package org.aussiebox.ccosmo.item.custom.shimmer_tool;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.aussiebox.ccosmo.component.ModDataComponentTypes;
import org.aussiebox.ccosmo.entity.ShimmerforkEntity;
import org.aussiebox.ccosmo.item.custom.ShimmerToolItem;

import java.util.List;

public class ShimmerforkItem extends ShimmerToolItem {
    public ShimmerforkItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return miner.isInCreativeMode();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!user.isSneaking()) {
            if (stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, 0) <= 0) return TypedActionResult.fail(stack);
            user.setCurrentHand(hand);
            return TypedActionResult.consume(stack);
        }

        return TypedActionResult.pass(stack);
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return true;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, 0) <= 0 ? UseAction.NONE : UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, 0) <= 0 ? 0 : 72000;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(stack, world, user, remainingUseTicks);

        if (!(user instanceof PlayerEntity playerEntity)) return;

        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        if (i < 10) return;

        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        stack.set(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, 1) - 1);

        ShimmerforkEntity entity = new ShimmerforkEntity(world, playerEntity, stack);
        entity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, 2.5F, 1.0F);
        if (playerEntity.isInCreativeMode()) {
            entity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
        }

        world.spawnEntity(entity);
        world.playSoundFromEntity(null, entity, SoundEvents.ITEM_TRIDENT_THROW.value(), SoundCategory.PLAYERS, 1.0F, 1.0F);
        if (!playerEntity.isInCreativeMode()) {
            playerEntity.getInventory().removeOne(stack);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> list, TooltipType type) {
        list.add(1, Text.translatable("item.ccosmo.shimmer_tool.tooltip.ability").withColor(0xFF55FF));
        list.add(2, Text.translatable("item.ccosmo.shimmerfork.tooltip.1").withColor(0xAAAAAA));
        list.add(3, Text.translatable("item.ccosmo.shimmerfork.tooltip.2").withColor(0xAAAAAA));
        list.add(4, Text.translatable("item.ccosmo.shimmerfork.tooltip.3").withColor(0xAAAAAA));
        list.add(5, Text.translatable("item.ccosmo.shimmerfork.tooltip.4").withColor(0xAAAAAA));
        list.add(6, Text.literal(" "));

        super.appendTooltip(stack, context, list, type);
    }
}
