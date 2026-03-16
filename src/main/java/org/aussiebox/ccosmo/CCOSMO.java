package org.aussiebox.ccosmo;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.recipe.RecipeType;
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
import org.aussiebox.ccosmo.packet.PyrrhianBeltFlightC2SPacket;
import org.aussiebox.ccosmo.recipe.ModRecipes;
import org.aussiebox.ccosmo.recipe.ShimmeringRecipe;
import org.aussiebox.ccosmo.recipe.serializer.ShimmeringRecipeSerializer;
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
        CCOSMO.LOGGER.info("i mean at least you know the mod should actually work now");

        DragonflameCactusGeneration.generateCacti();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ShimmerComponent.KEY.get(handler.getPlayer()).setShimmerseepTicks(2400);
        });

        PayloadTypeRegistry.playC2S().register(PyrrhianBeltFlightC2SPacket.ID, PyrrhianBeltFlightC2SPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PyrrhianBeltFlightC2SPacket.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            TrinketComponent trinkets = TrinketComponent.KEY.get(player);

            trinkets.setFlying(payload.flightMode());
            trinkets.setGliding(!payload.flightMode());
        });

        Registry.register(Registries.SOUND_EVENT, CCOSMOConstants.SHIMMER_TOOL_CHARGE_SOUND.getId(), CCOSMOConstants.SHIMMER_TOOL_CHARGE_SOUND);
        Registry.register(Registries.SOUND_EVENT, CCOSMOConstants.SHIMMERSEEP_CHARGE_SOUND.getId(), CCOSMOConstants.SHIMMERSEEP_CHARGE_SOUND);
        Registry.register(Registries.RECIPE_TYPE, id("shimmering"), new RecipeType<ShimmeringRecipe>() {});
        Registry.register(Registries.RECIPE_SERIALIZER, id("shimmering"), new ShimmeringRecipeSerializer());
    }
}
