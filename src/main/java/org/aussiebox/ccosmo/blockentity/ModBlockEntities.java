package org.aussiebox.ccosmo.blockentity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.block.ModBlocks;

public class ModBlockEntities {
    public static final BlockEntityType<ShimmerglassBlockEntity> SHIMMERGLASS_BLOCK_ENTITY =
            register("shimmerglass", ShimmerglassBlockEntity::new, ModBlocks.SHIMMERGLASS);
    public static final BlockEntityType<ShimmeringAltarBlockEntity> SHIMMERING_ALTAR_BLOCK_ENTITY =
            register("shimmering_altar", ShimmeringAltarBlockEntity::new, ModBlocks.SHIMMERING_ALTAR);

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.BlockEntityFactory<? extends T> entityFactory, Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, CCOSMO.id(name), BlockEntityType.Builder.<T>create(entityFactory, blocks).build());
    }

    public static void init() {
        CCOSMO.LOGGER.info("Registering Block Entities for " + CCOSMO.MOD_ID);
    }

}
