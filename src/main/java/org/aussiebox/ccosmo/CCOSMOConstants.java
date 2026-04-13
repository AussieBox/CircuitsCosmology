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
    RegistryKey<DamageType> SHIMMERING_CACTUS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, CCOSMO.id("shimmering_cactus"));

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

    int shimmerforkBlockChangeMaximum = 12;

    // Array Order: {Effect Range, Blocks Affected}
    Double[] shimmerforkLandEffectData = new Double[]{5.0, 50.0};

    int shimmerpickReturnTime = 20;

    double pyrrhianAnkletFlightTimeMaximum = 100;
    double pyrrhianAnkletGlideTimeMaximum = 150;
}
