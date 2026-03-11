package org.aussiebox.bitsofbox.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.aussiebox.bitsofbox.cca.ShimmerComponent;
import org.aussiebox.bitsofbox.item.ModItems;

public class AmethystDisruptorItem extends Item {
    public AmethystDisruptorItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        HitResult raycast = user.raycast(user.getBlockInteractionRange(), 0, false);
        if (!world.getWorldBorder().contains(raycast.getPos())) {

            if (user.getItemCooldownManager().isCoolingDown(ModItems.AMETHYST_DISRUPTOR)) return TypedActionResult.pass(user.getStackInHand(hand));

            if (ShimmerComponent.KEY.get(user).obtainmentsToday >= 3) {
                user.sendMessage(
                        Text.translatable("tip.bitsofbox.wait_to_collect_shimmer_powder").withColor(0xFF55FF),
                        true
                );
                return TypedActionResult.fail(user.getStackInHand(hand));
            }

            ShimmerComponent.KEY.get(user).obtainPowder();
            ItemStack stack = new ItemStack(ModItems.SHIMMER_POWDER);
            user.giveItemStack(stack);

            user.getItemCooldownManager().set(ModItems.AMETHYST_DISRUPTOR, 2500);

            return TypedActionResult.success(user.getStackInHand(hand));
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
