package org.aussiebox.ccosmo;

import com.mojang.serialization.Codec;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.StringIdentifiable;

public interface CCOSMOConstants {

    RegistryKey<DamageType> PICKARANG_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, CCOSMO.id("pickarang"));
    RegistryKey<DamageType> SHIMMERFORK_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, CCOSMO.id("shimmerfork"));

    RegistryKey<Enchantment> BORDERLINKED_ENCHANT = RegistryKey.of(RegistryKeys.ENCHANTMENT, CCOSMO.id("borderlinked"));
    RegistryKey<Enchantment> SHIMMERSEEP_ENCHANT = RegistryKey.of(RegistryKeys.ENCHANTMENT, CCOSMO.id("shimmerseep"));

    SoundEvent SHIMMER_TOOL_CHARGE_SOUND = SoundEvent.of(CCOSMO.id("item.shimmer_tool.charge"));
    SoundEvent SHIMMERSEEP_CHARGE_SOUND = SoundEvent.of(CCOSMO.id("item.shimmer_tool.shimmerseep_charge"));

    enum ShimmerToolType implements StringIdentifiable {
        AXE("axe"),
        PICKAXE("pickaxe"),
        TRIDENT("trident");

        private final String key;
        public static final Codec<ShimmerToolType> CODEC = StringIdentifiable.createCodec(ShimmerToolType::values);

        ShimmerToolType(String key) {
            this.key = key;
        }

        @Override
        public String asString() {
            return this.key;
        }
    }

    enum ShimmerToolSkin implements StringIdentifiable {
        BASE("base"),
        WOODEN_BINDING("wooden_binding"),
        STONE_BINDING("stone_binding"),
        COPPER_BINDING("copper_binding"),
        GOLD_BINDING("gold_binding"),
        IRON_BINDING("iron_binding"),
        DIAMOND_BINDING("diamond_binding"),
        NETHERITE_BINDING("netherite_binding");

        private final String key;
        public static final Codec<ShimmerToolSkin> CODEC = StringIdentifiable.createCodec(ShimmerToolSkin::values);

        ShimmerToolSkin(String key) {
            this.key = key;
        }

        @Override
        public String asString() {
            return this.key;
        }
    }

    int shimmerforkBlockChangeMaximum = 12;

    // Array Order: {Effect Range, Blocks Affected}
    Double[] shimmerforkLandEffectData = new Double[]{5.0, 50.0};

    int shimmerpickReturnTime = 30;

    double pyrrhianBeltFlightTimeMaximum = 75;
    double buffedPyrrhianBeltFlightTimeMaximum = 150;
    double pyrrhianBeltGlideTimeMaximum = 200;
    double buffedPyrrhianBeltGlideTimeMaximum = 300;
}
