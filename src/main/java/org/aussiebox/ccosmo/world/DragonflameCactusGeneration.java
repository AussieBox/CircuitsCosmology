package org.aussiebox.ccosmo.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.aussiebox.ccosmo.CCOSMO;

public class DragonflameCactusGeneration {
    public static void generateCacti() {
        RegistryKey<PlacedFeature> key = RegistryKey.of(RegistryKeys.PLACED_FEATURE, CCOSMO.id("place_dragonflame_cactus"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.DESERT, BiomeKeys.BADLANDS),
                GenerationStep.Feature.VEGETAL_DECORATION, key);
    }
}
