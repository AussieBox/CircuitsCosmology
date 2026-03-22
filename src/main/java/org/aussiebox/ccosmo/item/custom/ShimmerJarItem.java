package org.aussiebox.ccosmo.item.custom;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotAttributes;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ShimmerJarItem extends TrinketItem {
    public ShimmerJarItem(Settings settings) {
        super(settings);
    }

    @Override
    public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, Identifier slotIdentifier) {
        var modifiers = super.getModifiers(stack, slot, entity, slotIdentifier);
        SlotAttributes.addSlotModifier(modifiers, "legs/belt", slotIdentifier, 1, EntityAttributeModifier.Operation.ADD_VALUE);
        return modifiers;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> list, TooltipType type) {
        list.add(1, Text.literal(" "));
        list.add(2, Text.translatable("item.ccosmo.shimmer_jar.tooltip.1").withColor(0xFF555555));
        list.add(3, Text.translatable("item.ccosmo.shimmer_jar.tooltip.2").withColor(0xFF555555));
        list.add(4, Text.literal(" "));
    }
}
