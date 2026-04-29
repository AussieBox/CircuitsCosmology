package org.aussiebox.ccosmo;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import org.aussiebox.ccosmo.block.ModBlocks;
import org.aussiebox.ccosmo.blockentity.ModBlockEntities;
import org.aussiebox.ccosmo.cca.ShimmerComponent;
import org.aussiebox.ccosmo.cca.TrinketComponent;
import org.aussiebox.ccosmo.component.ModDataComponentTypes;
import org.aussiebox.ccosmo.entity.ModEntities;
import org.aussiebox.ccosmo.item.ModItems;
import org.aussiebox.ccosmo.mixin.TreeDecoratorTypeInvoker;
import org.aussiebox.ccosmo.packet.PyrrhianCuffFlightC2SPacket;
import org.aussiebox.ccosmo.recipe.ModRecipes;
import org.aussiebox.ccosmo.world.DragonflameCactusGeneration;
import org.aussiebox.ccosmo.world.gen.AttachedToLogsTreeDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

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

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!FabricLoader.getInstance().isModLoaded("bewitchery")) return ActionResult.PASS;
            CCOSMO.LOGGER.info(entity.getUuidAsString());

            ItemStack stack = player.getStackInHand(hand);
            ItemCooldownManager cooldowns = player.getItemCooldownManager();
            if (cooldowns.isCoolingDown(duckamoly.bewitchery.item.ModItems.PICK)) return ActionResult.PASS;

            if (stack.isOf(duckamoly.bewitchery.item.ModItems.PICK)) {
                if (!world.isClient) {
                    if (entity.isPlayer() && Objects.equals(entity.getUuidAsString(), "fdf5edf6-f202-47fe-98f0-68a60d68b0d5") && world.getWorldBorder().getDistanceInsideBorder(entity) <= 1000) {
                        Vec3d pos = entity.getPos();

                        ItemEntity itemEntity = new ItemEntity(world, pos.x, pos.y+0.5, pos.z, new ItemStack(ModItems.DRAGON_SCALES));
                        itemEntity.setToDefaultPickupDelay();

                        world.spawnEntity(itemEntity);
                        cooldowns.set(duckamoly.bewitchery.item.ModItems.PICK, 12000);

                        DamageSource damageSource = entity.getDamageSources().create(CCOSMOConstants.DRAGON_SCALES_HARVEST_DAMAGE, player);
                        entity.damage(damageSource, 5);

                        return ActionResult.SUCCESS;
                    }
                }
            }

            return ActionResult.PASS;
        });

        PayloadTypeRegistry.playC2S().register(PyrrhianCuffFlightC2SPacket.ID, PyrrhianCuffFlightC2SPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PyrrhianCuffFlightC2SPacket.ID, (payload, context) -> {
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
