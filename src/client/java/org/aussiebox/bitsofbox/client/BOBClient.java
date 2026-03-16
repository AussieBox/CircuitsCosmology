package org.aussiebox.bitsofbox.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import org.aussiebox.bitsofbox.BOB;
import org.aussiebox.bitsofbox.BOBConstants;
import org.aussiebox.bitsofbox.block.ModBlocks;
import org.aussiebox.bitsofbox.blockentity.ModBlockEntities;
import org.aussiebox.bitsofbox.cca.TrinketComponent;
import org.aussiebox.bitsofbox.client.hud.PyrrhianBeltFlightRenderer;
import org.aussiebox.bitsofbox.client.hud.ShimmerToolChargeRenderer;
import org.aussiebox.bitsofbox.client.model.entity.DragonflameCactusEntityModel;
import org.aussiebox.bitsofbox.client.render.blockentity.ShimmeringAltarBlockEntityRenderer;
import org.aussiebox.bitsofbox.client.render.entity.DragonflameCactusEntityRenderer;
import org.aussiebox.bitsofbox.client.render.entity.PickarangEntityRenderer;
import org.aussiebox.bitsofbox.client.render.entity.ShimmerforkEntityRenderer;
import org.aussiebox.bitsofbox.component.ModDataComponentTypes;
import org.aussiebox.bitsofbox.entity.ModEntities;
import org.aussiebox.bitsofbox.item.ModItems;
import org.aussiebox.bitsofbox.packet.PyrrhianBeltFlightC2SPacket;
import org.aussiebox.bitsofbox.util.BOBUtil;
import org.lwjgl.glfw.GLFW;

public class BOBClient implements ClientModInitializer {

    public static KeyBinding toggleFlightKeybind;

    public static int flightToggleCooldown = 0;

    @Override
    public void onInitializeClient() {

        HudRenderCallback.EVENT.register((ShimmerToolChargeRenderer::render));
        HudRenderCallback.EVENT.register((PyrrhianBeltFlightRenderer::render));

        EntityModelLayerRegistry.registerModelLayer(DragonflameCactusEntityModel.CACTUS, DragonflameCactusEntityModel::getTexturedModelData);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DRAGONFLAME_CACTUS_PLANT, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DRAGONFLAME_CACTUS_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHIMMERGLASS, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHIMMERING_ALTAR, RenderLayer.getCutout());

        EntityRendererRegistry.register(ModEntities.DragonflameCactusEntityType, DragonflameCactusEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.PickarangEntityType, (context) -> new PickarangEntityRenderer<>(context, 2.0F, true));
        EntityRendererRegistry.register(ModEntities.FluidityTridentEntityType, ShimmerforkEntityRenderer::new);

        BlockEntityRendererFactories.register(ModBlockEntities.SHIMMERING_ALTAR_BLOCK_ENTITY, ShimmeringAltarBlockEntityRenderer::new);

        registerModelPredicates();
        registerKeybinds();
        registerEventListeners();

    }

    public static void registerModelPredicates() {
        Item[] fluidityItems = new Item[]{
                ModItems.SHIMMERFORK,
                ModItems.SHIMMERAXE,
                ModItems.SHIMMERPICK,
        };
        for (Item item : fluidityItems) {
            for (BOBConstants.ShimmerToolSkin skin : BOBConstants.ShimmerToolSkin.values()) {
                ModelPredicateProviderRegistry.register(
                        item,
                        BOB.id("skin_" + skin.asString()),
                        (stack, world, entity, seed) -> {
                            BOBConstants.ShimmerToolSkin activeSkin = stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_SKIN, BOBConstants.ShimmerToolSkin.BASE);
                            if (activeSkin == skin) return 1.0F;
                            return 0.0F;
                        }
                );
            }
        }

        ModelPredicateProviderRegistry.register(
                ModItems.SHIMMERFORK,
                BOB.id("throwing"),
                (stack, world, entity, seed) -> {

                    if (entity == null) return 0.0F;
                    if (entity.getActiveItem() == stack) return 1.0F;

                    return 0.0F;
                }
        );
    }

    public static void registerKeybinds() {
        String CATEGORY = "key.bitsofbox.category";
        toggleFlightKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bitsofbox.toggle_flight",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                CATEGORY
        ));
    }

    public static void registerEventListeners() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if ((toggleFlightKeybind.wasPressed() && flightToggleCooldown == 0)) {
                if (BOBUtil.playerHasTrinket(client.player, ModItems.PYRRHIAN_BELT) && TrinketComponent.KEY.get(client.player).isCanFly()) {
                    ClientPlayNetworking.send(new PyrrhianBeltFlightC2SPacket(!TrinketComponent.KEY.get(client.player).isFlying()));
                    flightToggleCooldown = 20;
                }
            }

            if (flightToggleCooldown > 0) flightToggleCooldown--;
        });
    }
}
