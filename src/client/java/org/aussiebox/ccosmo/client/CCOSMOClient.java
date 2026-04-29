package org.aussiebox.ccosmo.client;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.block.ModBlocks;
import org.aussiebox.ccosmo.blockentity.ModBlockEntities;
import org.aussiebox.ccosmo.cca.TrinketComponent;
import org.aussiebox.ccosmo.client.model.entity.DragonflameCactusEntityModel;
import org.aussiebox.ccosmo.client.particle.ShimmeringAltarParticle;
import org.aussiebox.ccosmo.client.render.blockentity.PlushieBlockEntityRenderer;
import org.aussiebox.ccosmo.client.render.blockentity.ShimmeringAltarBlockEntityRenderer;
import org.aussiebox.ccosmo.client.render.blockentity.ShimmeringLensBlockEntityRenderer;
import org.aussiebox.ccosmo.client.render.entity.DragonflameCactusEntityRenderer;
import org.aussiebox.ccosmo.client.render.entity.PickarangEntityRenderer;
import org.aussiebox.ccosmo.client.render.entity.ShimmerforkEntityRenderer;
import org.aussiebox.ccosmo.client.render.hud.PyrrhianCuffFlightRenderer;
import org.aussiebox.ccosmo.client.render.hud.ShimmerToolChargeRenderer;
import org.aussiebox.ccosmo.client.render.trinkets.PyrrhianCuffRenderer;
import org.aussiebox.ccosmo.client.render.trinkets.ShimmerJarRenderer;
import org.aussiebox.ccosmo.entity.ModEntities;
import org.aussiebox.ccosmo.item.ModItems;
import org.aussiebox.ccosmo.packet.PyrrhianCuffFlightC2SPacket;
import org.aussiebox.ccosmo.util.CCOSMOUtil;
import org.lwjgl.glfw.GLFW;

public class CCOSMOClient implements ClientModInitializer {

    public static KeyBinding toggleFlightKeybind;

    public static int flightToggleCooldown = 0;

    @Override
    public void onInitializeClient() {

        HudRenderCallback.EVENT.register((ShimmerToolChargeRenderer::render));
        HudRenderCallback.EVENT.register((PyrrhianCuffFlightRenderer::render));

        EntityModelLayerRegistry.registerModelLayer(DragonflameCactusEntityModel.CACTUS, DragonflameCactusEntityModel::getTexturedModelData);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DRAGONFLAME_CACTUS_PLANT, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DRAGONFLAME_CACTUS_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHIMMERGLASS, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHIMMERING_ALTAR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHIMMERING_LENS, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CIRCUITWEAVER_PLUSHIE, RenderLayer.getCutout());

        EntityRendererRegistry.register(ModEntities.DragonflameCactusEntityType, DragonflameCactusEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.PickarangEntityType, (context) -> new PickarangEntityRenderer<>(context, 2.0F, true));
        EntityRendererRegistry.register(ModEntities.FluidityTridentEntityType, ShimmerforkEntityRenderer::new);

        BlockEntityRendererFactories.register(ModBlockEntities.SHIMMERING_ALTAR_BLOCK_ENTITY, ShimmeringAltarBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SHIMMERING_LENS_BLOCK_ENTITY, ShimmeringLensBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.PLUSHIE_BLOCK_ENTITY, PlushieBlockEntityRenderer::new);

        TrinketRendererRegistry.registerRenderer(ModItems.pyrrhian_cuff, new PyrrhianCuffRenderer());
        TrinketRendererRegistry.registerRenderer(ModItems.SHIMMER_JAR, new ShimmerJarRenderer());

        ParticleFactoryRegistry.getInstance().register(CCOSMO.SHIMMERING_ALTAR_SMALL, sprites -> new ShimmeringAltarParticle.Factory(sprites, 0.05F));
        ParticleFactoryRegistry.getInstance().register(CCOSMO.SHIMMERING_ALTAR_LARGE, sprites -> new ShimmeringAltarParticle.Factory(sprites, 0.1F));

        registerModelPredicates();
        registerKeybinds();
        registerEventListeners();

    }

    public static void registerModelPredicates() {
        ModelPredicateProviderRegistry.register(
                ModItems.SHIMMERFORK,
                CCOSMO.id("throwing"),
                (stack, world, entity, seed) -> {
                    if (entity == null) return 0.0F;
                    if (entity.getActiveItem() == stack) return 1.0F;
                    return 0.0F;
                }
        );
    }

    public static void registerKeybinds() {
        String CATEGORY = "key.ccosmo.category";
        toggleFlightKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ccosmo.toggle_flight",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                CATEGORY
        ));
    }

    public static void registerEventListeners() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if ((toggleFlightKeybind.wasPressed() && flightToggleCooldown == 0)) {
                if ((CCOSMOUtil.playerHasTrinket(client.player, ModItems.pyrrhian_cuff) || TrinketComponent.KEY.get(client.player).getLensPos() != null) && TrinketComponent.KEY.get(client.player).isCanFly()) {
                    if (client.player.isOnGround()) client.player.jump();
                    ClientPlayNetworking.send(new PyrrhianCuffFlightC2SPacket(!TrinketComponent.KEY.get(client.player).isFlying()));
                    flightToggleCooldown = 7;
                }
            }

            if (flightToggleCooldown > 0) flightToggleCooldown--;
        });

        ItemTooltipCallback.EVENT.register((stack, context, type, list) -> {
            if (stack.isOf(ModItems.CIRCUITWEAVER_PLUSHIE))
                list.add(1, Text.translatable("tip.ccosmo.good_enough").withColor(0xAAAAAA));
            if (!FabricLoader.getInstance().isModLoaded("bewitchery")) return;
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null || player.getEntityWorld() == null) return;
            if (stack.isOf(duckamoly.bewitchery.item.ModItems.PICK)) {
                if (player.getEntityWorld().getWorldBorder().getDistanceInsideBorder(player) <= 1000)
                    list.add(1, Text.translatable("tip.ccosmo.pick_near_border").withColor(0xAAAAAA));
            }
        });
    }
}
