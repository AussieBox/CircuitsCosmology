package org.aussiebox.ccosmo.component;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.CCOSMOConstants;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {

    public static final ComponentType<Integer> DRAGONFLAME_CACTUS_FUSE =
            register("dragonflame_cactus_fuse", builder -> builder.codec(Codec.INT));

    public static final ComponentType<CCOSMOConstants.ShimmerToolType> SHIMMER_TOOL_TYPE =
            register("shimmer_tool_mode", builder -> builder.codec(CCOSMOConstants.ShimmerToolType.CODEC));

    public static final ComponentType<CCOSMOConstants.ShimmerToolSkin> SHIMMER_TOOL_SKIN =
            register("shimmer_tool_skin", builder -> builder.codec(CCOSMOConstants.ShimmerToolSkin.CODEC));

    public static final ComponentType<Integer> SHIMMER_TOOL_CHARGES =
            register("shimmer_tool_charges", builder -> builder.codec(Codec.INT));

    public static final ComponentType<Integer> SHIMMER_TOOL_MAX_CHARGES =
            register("shimmer_tool_max_charges", builder -> builder.codec(Codec.INT));

    public static final ComponentType<Boolean> HAS_BORDERLINKED =
            register("has_borderlinked", builder -> builder.codec(Codec.BOOL));


    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(CCOSMO.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());
    }

    public static void init() {
        CCOSMO.LOGGER.info("Registering Component Types for " + CCOSMO.MOD_ID);
    }
}
