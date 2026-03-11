package org.aussiebox.bitsofbox;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import org.aussiebox.bitsofbox.attach.ModAttachmentTypes;
import org.aussiebox.bitsofbox.block.ModBlocks;
import org.aussiebox.bitsofbox.blockentity.ModBlockEntities;
import org.aussiebox.bitsofbox.cca.ShimmerComponent;
import org.aussiebox.bitsofbox.component.ModDataComponentTypes;
import org.aussiebox.bitsofbox.entity.ModEntities;
import org.aussiebox.bitsofbox.item.ModItems;
import org.aussiebox.bitsofbox.mixin.TreeDecoratorTypeInvoker;
import org.aussiebox.bitsofbox.packet.PyrrhianBeltFlightC2SPacket;
import org.aussiebox.bitsofbox.world.DragonflameCactusGeneration;
import org.aussiebox.bitsofbox.world.gen.AttachedToLogsTreeDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BOB implements ModInitializer {

    public static final String MOD_ID = "bitsofbox";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static final TreeDecoratorType<AttachedToLogsTreeDecorator> ATTACHED_TO_LOGS_TREE_DECORATOR = TreeDecoratorTypeInvoker.bitsofbox$callRegister("bitsofbox:attached_to_logs", AttachedToLogsTreeDecorator.CODEC);

    @Override
    public void onInitialize() {

        BOB.LOGGER.info("OH GOD LOOK OUT THERE'S LOGGER SPAM COMING UP AAA");
        BOB.LOGGER.info("-------------------------------------------------");
        ModBlocks.init();
        ModItems.init();
        ModEntities.init();
        ModAttachmentTypes.init();
        ModDataComponentTypes.init();
        ModBlockEntities.registerModBlockEntities();
        BOB.LOGGER.info("-------------------------------------------------");
        BOB.LOGGER.info("phew hopefully that wasn't too bad lol");
        BOB.LOGGER.info("i mean at least you know the mod should actually work now");

        DragonflameCactusGeneration.generateCacti();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ShimmerComponent.KEY.get(handler.getPlayer()).setShimmerseepTicks(2400);
        });

        PayloadTypeRegistry.playC2S().register(PyrrhianBeltFlightC2SPacket.ID, PyrrhianBeltFlightC2SPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PyrrhianBeltFlightC2SPacket.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            PlayerAbilities abilities = player.getAbilities();

            if (payload.flightMode()) {
                abilities.flying = true;
                player.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(abilities));
            } else {
                abilities.flying = false;
                player.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(abilities));
            }
        });

        Registry.register(Registries.SOUND_EVENT, BOBConstants.SHIMMER_TOOL_CHARGE_SOUND.getId(), BOBConstants.SHIMMER_TOOL_CHARGE_SOUND);
        Registry.register(Registries.SOUND_EVENT, BOBConstants.SHIMMERSEEP_CHARGE_SOUND.getId(), BOBConstants.SHIMMERSEEP_CHARGE_SOUND);
    }
}
