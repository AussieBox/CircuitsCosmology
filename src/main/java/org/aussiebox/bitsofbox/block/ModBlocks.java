package org.aussiebox.bitsofbox.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.aussiebox.bitsofbox.BOB;
import org.aussiebox.bitsofbox.block.custom.DragonflameCactusBlock;
import org.aussiebox.bitsofbox.block.custom.DragonflameCactusPlantBlock;
import org.aussiebox.bitsofbox.block.custom.ShimmerglassBlock;
import org.aussiebox.bitsofbox.block.custom.ShimmeringAltarBlock;

import java.util.function.Function;

public class ModBlocks {

    public static final Block DRAGONFLAME_CACTUS_PLANT = register(
            "dragonflame_cactus_plant",
            DragonflameCactusPlantBlock::new,
            AbstractBlock.Settings.create()
                    .sounds(BlockSoundGroup.WOOD)
                    .ticksRandomly()
                    .nonOpaque(),
            false
    );

    public static final Block DRAGONFLAME_CACTUS_BLOCK = register(
            "dragonflame_cactus_block",
            DragonflameCactusBlock::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_RED)
                    .ticksRandomly()
                    .strength(0.4F)
                    .sounds(BlockSoundGroup.WOOL)
                    .pistonBehavior(PistonBehavior.DESTROY)
                    .nonOpaque(),
            true
    );

    public static final Block SHIMMERGLASS = register(
            "shimmerglass",
            ShimmerglassBlock::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.PINK)
                    .strength(0.3F)
                    .sounds(BlockSoundGroup.GLASS)
                    .instrument(NoteBlockInstrument.HAT)
                    .luminance(ShimmerglassBlock::getLuminance)
                    .emissiveLighting(ShimmerglassBlock::getEmissive)
                    .allowsSpawning(Blocks::never)
                    .solidBlock(Blocks::never)
                    .suffocates(Blocks::never)
                    .blockVision(Blocks::never)
                    .noCollision() // Fun fact! .noCollision() actually FORCES .nonOpaque()!
                    .nonOpaque(), // ...I still did it, just in case.
            false
    );

    public static final Block SHIMMER_POWDER_BLOCK = register(
            "shimmer_powder_block",
            Block::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.PINK)
                    .strength(0.5F)
                    .instrument(NoteBlockInstrument.SNARE)
                    .sounds(BlockSoundGroup.SAND),
            false
    );

    public static final Block SHIMMERING_ALTAR = register(
            "shimmering_altar",
            ShimmeringAltarBlock::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.PINK)
                    .strength(0.3F)
                    .sounds(BlockSoundGroup.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .allowsSpawning(Blocks::never)
                    .luminance((state) -> 7)
                    .strength(5.0F, 1200.0F)
                    .requiresTool()
                    .nonOpaque(),
            false
    );

    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        RegistryKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings);

        if (shouldRegisterItem) {
            RegistryKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(BOB.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(BOB.MOD_ID, name));
    }

    public static void init() {
        BOB.LOGGER.info("Registering Blocks for " + BOB.MOD_ID);
    }
}
