package org.aussiebox.ccosmo.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.aussiebox.ccosmo.cca.DragonflameCactusComponent;
import org.aussiebox.ccosmo.component.ModDataComponentTypes;
import org.aussiebox.ccosmo.entity.DragonflameCactusEntity;
import org.aussiebox.ccosmo.item.ModItems;

import java.text.DecimalFormat;
import java.util.List;

public class DragonflameCactusItem extends Item {
    public DragonflameCactusItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack itemStack = super.getDefaultStack();
        itemStack.set(ModDataComponentTypes.DRAGONFLAME_CACTUS_FUSE, 20);
        return itemStack;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (user.getItemCooldownManager().isCoolingDown(ModItems.DRAGONFLAME_CACTUS)) return TypedActionResult.pass(itemStack);

        if (!world.isClient()) {
            DragonflameCactusEntity dragonflameCactusEntity = new DragonflameCactusEntity(world, user);
            int fuse = itemStack.getOrDefault(ModDataComponentTypes.DRAGONFLAME_CACTUS_FUSE, 20);
            dragonflameCactusEntity.setOwner(user);
            DragonflameCactusComponent.KEY.get(dragonflameCactusEntity).setTimer(fuse);
            dragonflameCactusEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 0F);
            world.spawnEntity(dragonflameCactusEntity);

            user.getItemCooldownManager().set(ModItems.DRAGONFLAME_CACTUS, fuse/2);
        }

        itemStack.decrementUnlessCreative(1, user);
        return TypedActionResult.success(itemStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> list, TooltipType type) {
        if (stack.contains(ModDataComponentTypes.DRAGONFLAME_CACTUS_FUSE)) {
            int fuseTicks = stack.getOrDefault(ModDataComponentTypes.DRAGONFLAME_CACTUS_FUSE, 20);
            String fuseSeconds = new DecimalFormat("0.00").format((double) fuseTicks/20);
            list.add(1, Text.translatable("item.ccosmo.dragonflame_cactus.tooltip.fuse.1").withColor(0xAAAAAA)
                    .append(Text.literal(String.valueOf(fuseTicks)).withColor(0xFFAAAAAA))
                    .append(Text.translatable("item.ccosmo.dragonflame_cactus.tooltip.fuse.2").withColor(0xFFAAAAAA))
                    .append(Text.literal(fuseSeconds).withColor(0xFFAAAAAA))
                    .append(Text.translatable("item.ccosmo.dragonflame_cactus.tooltip.fuse.3").withColor(0xFFAAAAAA)));
        }
    }
}
