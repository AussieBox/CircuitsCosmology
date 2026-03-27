package org.aussiebox.ccosmo;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import org.aussiebox.ccosmo.block.ModBlocks;
import org.aussiebox.ccosmo.blockentity.ModBlockEntities;
import org.aussiebox.ccosmo.cca.ShimmerComponent;
import org.aussiebox.ccosmo.cca.TrinketComponent;
import org.aussiebox.ccosmo.component.ModDataComponentTypes;
import org.aussiebox.ccosmo.entity.ModEntities;
import org.aussiebox.ccosmo.item.ModItems;
import org.aussiebox.ccosmo.mixin.TreeDecoratorTypeInvoker;
import org.aussiebox.ccosmo.packet.PyrrhianAnkletFlightC2SPacket;
import org.aussiebox.ccosmo.recipe.ModRecipes;
import org.aussiebox.ccosmo.world.DragonflameCactusGeneration;
import org.aussiebox.ccosmo.world.gen.AttachedToLogsTreeDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CCOSMO implements ModInitializer {

    public static final String MOD_ID = "ccosmo";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static final TreeDecoratorType<AttachedToLogsTreeDecorator> ATTACHED_TO_LOGS_TREE_DECORATOR = TreeDecoratorTypeInvoker.ccosmo$callRegister("ccosmo:attached_to_logs", AttachedToLogsTreeDecorator.CODEC);

    public static final SimpleParticleType SHIMMERING_ALTAR_SMALL = FabricParticleTypes.simple();
    public static final SimpleParticleType SHIMMERING_ALTAR_LARGE = FabricParticleTypes.simple();

    @Override
    public void onInitialize() {

        CCOSMO.LOGGER.info("OH GOD LOOK OUT THERE'S LOGGER SPAM COMING UP AAA");
        CCOSMO.LOGGER.info("-------------------------------------------------");
        ModBlocks.init();
        ModItems.init();
        ModEntities.init();
        ModDataComponentTypes.init();
        ModBlockEntities.init();
        ModRecipes.init();
        CCOSMO.LOGGER.info("-------------------------------------------------");
        CCOSMO.LOGGER.info("phew hopefully that wasn't too bad lol");
        CCOSMO.LOGGER.info("i went through so much pain to make this work so you better like the mod bro");

        DragonflameCactusGeneration.generateCacti();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ShimmerComponent.KEY.get(handler.getPlayer()).setShimmerseepTicks(2400);
        });

        PayloadTypeRegistry.playC2S().register(PyrrhianAnkletFlightC2SPacket.ID, PyrrhianAnkletFlightC2SPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PyrrhianAnkletFlightC2SPacket.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            TrinketComponent trinkets = TrinketComponent.KEY.get(player);

            trinkets.setFlying(payload.flightMode());
            trinkets.setGliding(!payload.flightMode());
        });

        Registry.register(Registries.SOUND_EVENT, CCOSMOConstants.SHIMMER_TOOL_CHARGE_SOUND.getId(), CCOSMOConstants.SHIMMER_TOOL_CHARGE_SOUND);
        Registry.register(Registries.SOUND_EVENT, CCOSMOConstants.SHIMMERSEEP_CHARGE_SOUND.getId(), CCOSMOConstants.SHIMMERSEEP_CHARGE_SOUND);

        Registry.register(Registries.PARTICLE_TYPE, id("shimmering_altar_small"), SHIMMERING_ALTAR_SMALL);
        Registry.register(Registries.PARTICLE_TYPE, id("shimmering_altar_large"), SHIMMERING_ALTAR_LARGE);
    }
}
