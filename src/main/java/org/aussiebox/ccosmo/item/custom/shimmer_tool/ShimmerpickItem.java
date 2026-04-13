package org.aussiebox.ccosmo.item.custom.shimmer_tool;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.aussiebox.ccosmo.component.ModDataComponentTypes;
import org.aussiebox.ccosmo.entity.ModEntities;
import org.aussiebox.ccosmo.entity.PickarangEntity;
import org.aussiebox.ccosmo.item.custom.ShimmerToolItem;

import java.util.List;

public class ShimmerpickItem extends ShimmerToolItem {
    public ShimmerpickItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!user.isSneaking()) {
            if (stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, 0) <= 0) return TypedActionResult.fail(stack);
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            stack.set(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, 1) - 1);

            PickarangEntity entity = new PickarangEntity(ModEntities.PickarangEntityType, user, world);
            entity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 0.0F);
            entity.setItem(stack);

            world.spawnEntity(entity);
            stack.decrement(1);

            return TypedActionResult.success(stack);
        }

        return TypedActionResult.pass(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> list, TooltipType type) {
        list.add(1, Text.translatable("item.ccosmo.shimmer_tool.tooltip.ability").withColor(0xFF55FF));
        list.add(2, Text.translatable("item.ccosmo.shimmerpick.tooltip.1").withColor(0xAAAAAA));
        list.add(3, Text.translatable("item.ccosmo.shimmerpick.tooltip.2").withColor(0xAAAAAA));
        list.add(4, Text.translatable("item.ccosmo.shimmerpick.tooltip.3").withColor(0xAAAAAA));
        list.add(5, Text.translatable("item.ccosmo.shimmerpick.tooltip.4").withColor(0xAAAAAA));
        list.add(6, Text.literal(" "));

        super.appendTooltip(stack, context, list, type);
    }
}
