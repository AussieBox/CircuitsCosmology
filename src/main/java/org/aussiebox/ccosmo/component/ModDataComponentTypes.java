package org.aussiebox.ccosmo.component;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.aussiebox.ccosmo.CCOSMO;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {

    public static final ComponentType<Integer> DRAGONFLAME_CACTUS_FUSE =
            register("dragonflame_cactus_fuse", builder -> builder.codec(Codec.INT));

    public static final ComponentType<Integer> SHIMMER_TOOL_CHARGES =
            register("shimmer_tool_charges", builder -> builder.codec(Codec.INT));

    public static final ComponentType<Integer> SHIMMER_TOOL_MAX_CHARGES =
            register("shimmer_tool_max_charges", builder -> builder.codec(Codec.INT));

    public static final ComponentType<Boolean> SHIMMERING_CACTUS_LIT =
            register("shimmering_cactus_lit", builder -> builder.codec(Codec.BOOL));

    public static final ComponentType<Integer> SHIMMERING_CACTUS_FUSE =
            register("shimmering_cactus_fuse", builder -> builder.codec(Codec.INT));

    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(CCOSMO.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());
    }

    public static void init() {
        CCOSMO.LOGGER.info("Registering Component Types for " + CCOSMO.MOD_ID);
    }
}
