package org.aussiebox.ccosmo.item.custom;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotAttributes;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.CCOSMOConstants;

import java.util.List;
import java.util.Objects;

public class PyrrhianAnkletItem extends TrinketItem {
    public PyrrhianAnkletItem(Settings settings) {
        super(settings);
    }

    @Override
    public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, Identifier slotIdentifier) {
        var modifiers = super.getModifiers(stack, slot, entity, slotIdentifier);
        if (Objects.equals(entity.getUuidAsString(), "fdf5edf6-f202-47fe-98f0-68a60d68b0d5")) {
            SlotAttributes.addSlotModifier(modifiers, "feet/aglet", slotIdentifier, 1, EntityAttributeModifier.Operation.ADD_VALUE);
        }
        return modifiers;
    }

    public static float getAnkletFlySpeed(PlayerEntity player) {
        if (Objects.equals(player.getUuidAsString(), "fdf5edf6-f202-47fe-98f0-68a60d68b0d5")) return 0.035F;
        return 0.02F;
    }

    public static double getAnkletFlyTime(PlayerEntity player) {
        if (Objects.equals(player.getUuidAsString(), "fdf5edf6-f202-47fe-98f0-68a60d68b0d5")) return CCOSMOConstants.buffedPyrrhianAnkletFlightTimeMaximum;
        return CCOSMOConstants.pyrrhianAnkletFlightTimeMaximum;
    }

    public static double getAnkletGlideTime(PlayerEntity player) {
        if (Objects.equals(player.getUuidAsString(), "fdf5edf6-f202-47fe-98f0-68a60d68b0d5")) return CCOSMOConstants.buffedPyrrhianAnkletGlideTimeMaximum;
        return CCOSMOConstants.pyrrhianAnkletGlideTimeMaximum;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> list, TooltipType type) {
        list.add(1, Text.literal(" "));
        list.add(2, Text.translatable("item.ccosmo.pyrrhian_anklet.tooltip.1").withColor(0xFFAAAAAA));
        list.add(3, Text.translatable("item.ccosmo.pyrrhian_anklet.tooltip.2").withColor(0xFFAAAAAA));
        list.add(4, Text.translatable("item.ccosmo.pyrrhian_anklet.tooltip.3").withColor(0xFFAAAAAA));
        list.add(5, Text.translatable("item.ccosmo.pyrrhian_anklet.tooltip.4").withColor(0xFFAAAAAA));
        list.add(6, Text.literal(" "));
        if (!(stack.getHolder() instanceof LivingEntity)) return;

        CCOSMO.LOGGER.info(String.valueOf(stack.getHolder().getName()));
        if (Objects.equals(stack.getHolder().getUuidAsString(), "fdf5edf6-f202-47fe-98f0-68a60d68b0d5")) {
            list.add(7, Text.translatable("item.ccosmo.pyrrhian_anklet.tooltip.buffed.1").withColor(0xFFAAAAAA));
            list.add(8, Text.translatable("item.ccosmo.pyrrhian_anklet.tooltip.buffed.2").withColor(0xFFAAAAAA));
        } else {
            list.add(7, Text.translatable("item.ccosmo.pyrrhian_anklet.tooltip.normal.1").withColor(0xFFAAAAAA));
            list.add(8, Text.translatable("item.ccosmo.pyrrhian_anklet.tooltip.normal.2").withColor(0xFFAAAAAA));
        }
        list.add(9, Text.literal(" "));
    }
}
