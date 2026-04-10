package org.aussiebox.ccosmo.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.CCOSMOConstants;
import org.aussiebox.ccosmo.block.ModBlocks;
import org.aussiebox.ccosmo.component.ModDataComponentTypes;
import org.aussiebox.ccosmo.item.custom.*;

import java.util.function.Function;

public class ModItems {

    public static final Item DRAGONFLAME_CACTUS = registerItem(
            "dragonflame_cactus",
            DragonflameCactusItem::new,
            new Item.Settings()
    );

    public static final Item SHIMMERFORK = registerItem(
            "shimmerfork",
            ShimmerToolItem::new,
            new Item.Settings()
                    .component(ModDataComponentTypes.SHIMMER_TOOL_TYPE, CCOSMOConstants.ShimmerToolType.TRIDENT)
                    .component(ModDataComponentTypes.SHIMMER_TOOL_MAX_CHARGES, 8)
                    .component(ModDataComponentTypes.SHIMMER_TOOL_SKIN, CCOSMOConstants.ShimmerToolSkin.BASE)
                    .component(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
                            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(
                                    Item.BASE_ATTACK_DAMAGE_MODIFIER_ID,
                                    7.5,
                                    EntityAttributeModifier.Operation.ADD_VALUE
                            ), AttributeModifierSlot.MAINHAND)
                            .add(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(
                                    Item.BASE_ATTACK_SPEED_MODIFIER_ID,
                                    -3.0,
                                    EntityAttributeModifier.Operation.ADD_VALUE
                            ), AttributeModifierSlot.MAINHAND)
                            .build()
                    )
                    .rarity(Rarity.EPIC)
                    .maxDamage(2031)
                    .fireproof()
    );

    public static final Item SHIMMERAXE = registerItem(
            "shimmeraxe",
            ShimmerToolItem::new,
            new Item.Settings()
                    .component(ModDataComponentTypes.SHIMMER_TOOL_TYPE, CCOSMOConstants.ShimmerToolType.AXE)
                    .component(ModDataComponentTypes.SHIMMER_TOOL_MAX_CHARGES, 8)
                    .component(ModDataComponentTypes.SHIMMER_TOOL_SKIN, CCOSMOConstants.ShimmerToolSkin.BASE)
                    .component(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
                            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(
                                    Item.BASE_ATTACK_DAMAGE_MODIFIER_ID,
                                    8.5,
                                    EntityAttributeModifier.Operation.ADD_VALUE
                            ), AttributeModifierSlot.MAINHAND)
                            .add(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(
                                    Item.BASE_ATTACK_SPEED_MODIFIER_ID,
                                    -2.8,
                                    EntityAttributeModifier.Operation.ADD_VALUE
                            ), AttributeModifierSlot.MAINHAND)
                            .build()
                    )
                    .rarity(Rarity.EPIC)
                    .maxDamage(2031)
                    .fireproof()
    );

    public static final Item SHIMMERPICK = registerItem(
            "shimmerpick",
            ShimmerToolItem::new,
            new Item.Settings()
                    .component(ModDataComponentTypes.SHIMMER_TOOL_TYPE, CCOSMOConstants.ShimmerToolType.PICKAXE)
                    .component(ModDataComponentTypes.SHIMMER_TOOL_MAX_CHARGES, 8)
                    .component(ModDataComponentTypes.SHIMMER_TOOL_SKIN, CCOSMOConstants.ShimmerToolSkin.BASE)
                    .component(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
                            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(
                                    Item.BASE_ATTACK_DAMAGE_MODIFIER_ID,
                                    4.5,
                                    EntityAttributeModifier.Operation.ADD_VALUE
                            ), AttributeModifierSlot.MAINHAND)
                            .add(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(
                                    Item.BASE_ATTACK_SPEED_MODIFIER_ID,
                                    -2.6,
                                    EntityAttributeModifier.Operation.ADD_VALUE
                            ), AttributeModifierSlot.MAINHAND)
                            .build()
                    )
                    .rarity(Rarity.EPIC)
                    .maxDamage(2031)
                    .fireproof()
    );

    public static final Item SHIMMER_POWDER = registerItem(
            "shimmer_powder",
            Item::new,
            new Item.Settings()
                    .rarity(Rarity.RARE)
                    .fireproof()
    );

    public static final Item AMETHYST_DISRUPTOR = registerItem(
            "amethyst_disruptor",
            AmethystDisruptorItem::new,
            new Item.Settings()
                    .rarity(Rarity.UNCOMMON)
                    .maxCount(1)
    );

    public static final BlockItem SHIMMERGLASS = registerBlockItem(
            "shimmerglass",
            (settings -> new BlockItem(ModBlocks.SHIMMERGLASS, settings)),
            new Item.Settings()
                    .rarity(Rarity.EPIC)
    );

    public static final BlockItem SHIMMER_POWDER_BLOCK = registerBlockItem(
            "shimmer_powder_block",
            (settings -> new BlockItem(ModBlocks.SHIMMER_POWDER_BLOCK, settings)),
            new Item.Settings()
                    .rarity(Rarity.RARE)
    );

    public static final BlockItem SHIMMERING_ALTAR = registerBlockItem(
            "shimmering_altar",
            (settings -> new BlockItem(ModBlocks.SHIMMERING_ALTAR, settings)),
            new Item.Settings()
                    .rarity(Rarity.EPIC)
    );

    public static final Item PYRRHIAN_ANKLET = registerItem(
            "pyrrhian_anklet",
            PyrrhianAnkletItem::new,
            new Item.Settings()
                    .maxCount(1)
                    .rarity(Rarity.EPIC)
                    .fireproof()
    );

    public static final Item SHIMMER_JAR = registerItem(
            "shimmer_jar",
            ShimmerJarItem::new,
            new Item.Settings()
                    .maxCount(1)
                    .rarity(Rarity.EPIC)
    );

    public static final Item SHIMMERING_CACTUS = registerItem(
            "shimmering_cactus",
            ShimmeringCactusItem::new,
            new Item.Settings()
                    .maxCount(1)
                    .fireproof()
                    .rarity(Rarity.EPIC)
    );

    public static final BlockItem SHIMMERING_LENS = registerBlockItem(
            "shimmering_lens",
            (settings -> new BlockItem(ModBlocks.SHIMMERING_LENS, settings)),
            new Item.Settings()
                    .rarity(Rarity.EPIC)
    );

    public static final RegistryKey<ItemGroup> CCOSMO_ITEMGROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(CCOSMO.MOD_ID, "ccosmo"));
    public static final ItemGroup CCOSMO_ITEMGROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.SHIMMER_POWDER))
            .displayName(Text.translatable("itemGroup.ccosmo.ccosmo"))
            .build();

    public static Item registerItem(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(CCOSMO.MOD_ID, name));
        Item item = itemFactory.apply(settings);
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static BlockItem registerBlockItem(String name, Function<Item.Settings, BlockItem> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(CCOSMO.MOD_ID, name));
        BlockItem item = itemFactory.apply(settings);
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static void init() {
        CCOSMO.LOGGER.info("Registering Items for " + CCOSMO.MOD_ID);

        Registry.register(Registries.ITEM_GROUP, CCOSMO_ITEMGROUP_KEY, CCOSMO_ITEMGROUP);
        ItemGroupEvents.modifyEntriesEvent(CCOSMO_ITEMGROUP_KEY).register(itemGroup -> {
            itemGroup.add(DRAGONFLAME_CACTUS.getDefaultStack());
            itemGroup.add(ModBlocks.DRAGONFLAME_CACTUS_BLOCK.asItem());
            itemGroup.add(SHIMMERING_CACTUS.getDefaultStack());
            itemGroup.add(PYRRHIAN_ANKLET);
            itemGroup.add(AMETHYST_DISRUPTOR);
            itemGroup.add(SHIMMER_POWDER);
            itemGroup.add(SHIMMER_POWDER_BLOCK);
            itemGroup.add(SHIMMERGLASS);
            itemGroup.add(SHIMMERING_ALTAR);
            itemGroup.add(SHIMMER_JAR);
            itemGroup.add(SHIMMERFORK);
            itemGroup.add(SHIMMERAXE);
            itemGroup.add(SHIMMERPICK);
            itemGroup.add(SHIMMERING_LENS);
        });
    }

}
