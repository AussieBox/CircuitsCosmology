package org.aussiebox.bitsofbox.item;

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
import org.aussiebox.bitsofbox.BOB;
import org.aussiebox.bitsofbox.BOBConstants;
import org.aussiebox.bitsofbox.block.ModBlocks;
import org.aussiebox.bitsofbox.component.ModDataComponentTypes;
import org.aussiebox.bitsofbox.item.custom.*;

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
                    .component(ModDataComponentTypes.SHIMMER_TOOL_TYPE, BOBConstants.ShimmerToolType.TRIDENT)
                    .component(ModDataComponentTypes.SHIMMER_TOOL_MAX_CHARGES, 8)
                    .component(ModDataComponentTypes.SHIMMER_TOOL_SKIN, BOBConstants.ShimmerToolSkin.BASE)
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
                    .component(ModDataComponentTypes.SHIMMER_TOOL_TYPE, BOBConstants.ShimmerToolType.AXE)
                    .component(ModDataComponentTypes.SHIMMER_TOOL_MAX_CHARGES, 8)
                    .component(ModDataComponentTypes.SHIMMER_TOOL_SKIN, BOBConstants.ShimmerToolSkin.BASE)
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
                    .component(ModDataComponentTypes.SHIMMER_TOOL_TYPE, BOBConstants.ShimmerToolType.PICKAXE)
                    .component(ModDataComponentTypes.SHIMMER_TOOL_MAX_CHARGES, 8)
                    .component(ModDataComponentTypes.SHIMMER_TOOL_SKIN, BOBConstants.ShimmerToolSkin.BASE)
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

    public static final BlockItem SHIMMERING_TABLE = registerBlockItem(
            "shimmering_table",
            (settings -> new BlockItem(ModBlocks.SHIMMERING_ALTAR, settings)),
            new Item.Settings()
                    .rarity(Rarity.EPIC)
    );

    public static final Item PYRRHIAN_BELT = registerItem(
            "pyrrhian_belt",
            PyrrhianBeltItem::new,
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

    public static final RegistryKey<ItemGroup> BOB_ITEMGROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(BOB.MOD_ID, "bitsofbox"));
    public static final ItemGroup BOB_ITEMGROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.SHIMMER_POWDER))
            .displayName(Text.translatable("itemGroup.bitsofbox.bitsofbox"))
            .build();

    public static Item registerItem(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(BOB.MOD_ID, name));
        Item item = itemFactory.apply(settings);
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static BlockItem registerBlockItem(String name, Function<Item.Settings, BlockItem> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(BOB.MOD_ID, name));
        BlockItem item = itemFactory.apply(settings);
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static void init() {
        BOB.LOGGER.info("Registering Items for " + BOB.MOD_ID);

        Registry.register(Registries.ITEM_GROUP, BOB_ITEMGROUP_KEY, BOB_ITEMGROUP);
        ItemGroupEvents.modifyEntriesEvent(BOB_ITEMGROUP_KEY).register(itemGroup -> {
            itemGroup.add(DRAGONFLAME_CACTUS.getDefaultStack());
            itemGroup.add(ModBlocks.DRAGONFLAME_CACTUS_BLOCK.asItem());
            itemGroup.add(PYRRHIAN_BELT);
            itemGroup.add(AMETHYST_DISRUPTOR);
            itemGroup.add(SHIMMER_POWDER);
            itemGroup.add(SHIMMER_POWDER_BLOCK);
            itemGroup.add(SHIMMERGLASS);
            itemGroup.add(SHIMMERING_TABLE);
            itemGroup.add(SHIMMER_JAR);
            itemGroup.add(SHIMMERFORK);
            itemGroup.add(SHIMMERAXE);
            itemGroup.add(SHIMMERPICK);
        });
    }

}
