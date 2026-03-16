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
import org.aussiebox.ccosmo.CCOSMOConstants;

import java.util.List;
import java.util.Objects;

public class PyrrhianBeltItem extends TrinketItem {
    public PyrrhianBeltItem(Settings settings) {
        super(settings);
    }

    @Override
    public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, Identifier slotIdentifier) {
        var modifiers = super.getModifiers(stack, slot, entity, slotIdentifier);
        if (Objects.equals(entity.getUuidAsString(), "fdf5edf6-f202-47fe-98f0-68a60d68b0d5")) {
            SlotAttributes.addSlotModifier(modifiers, "legs/belt", slotIdentifier, 1, EntityAttributeModifier.Operation.ADD_VALUE);
        }
        return modifiers;
    }

    public static float getBeltFlySpeed(PlayerEntity player) {
        if (Objects.equals(player.getUuidAsString(), "fdf5edf6-f202-47fe-98f0-68a60d68b0d5")) return 0.035F;
        return 0.02F;
    }

    public static double getBeltFlyTime(PlayerEntity player) {
        if (Objects.equals(player.getUuidAsString(), "fdf5edf6-f202-47fe-98f0-68a60d68b0d5")) return CCOSMOConstants.buffedPyrrhianBeltFlightTimeMaximum;
        return CCOSMOConstants.pyrrhianBeltFlightTimeMaximum;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> list, TooltipType type) {
        super.appendTooltip(stack, context, list, type);
        // TODO: Add pyrrhian belt tooltip
    }
}
