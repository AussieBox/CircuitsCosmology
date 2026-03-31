package org.aussiebox.ccosmo.cca;

import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.blockentity.ShimmeringLensBlockEntity;
import org.aussiebox.ccosmo.component.ModDataComponentTypes;
import org.aussiebox.ccosmo.item.ModItems;
import org.aussiebox.ccosmo.item.custom.PyrrhianAnkletItem;
import org.aussiebox.ccosmo.item.custom.ShimmerToolItem;
import org.aussiebox.ccosmo.util.CCOSMOUtil;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TrinketComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<TrinketComponent> KEY = ComponentRegistry.getOrCreate(CCOSMO.id("trinket_component"), TrinketComponent.class);
    private final PlayerEntity player;

    @Getter private boolean gliding = false;
    @Getter private boolean flying = false;
    @Getter private boolean canGlide = false;
    @Getter private boolean canFly = false;
    @Getter private int glideDamageCooldown;
    @Getter private int flightDamageCooldown;
    @Getter private double pyrrhianAnkletFlightTime;
    @Getter private double pyrrhianAnkletGlideTime;
    @Getter private boolean wasLastFlying = false;
    @Getter private int nonGroundedTime;
    @Getter private int enchancementAirJumpsLeft = 0;
    @Getter private boolean enchancementHasAirJump = false;
    @Getter private BlockPos lensPos;

    public TrinketComponent(PlayerEntity player) {
        this.player = player;
    }

    public void setEnchancementData(int airJumpsLeft, boolean hasAirJump) {
        enchancementAirJumpsLeft = airJumpsLeft;
        enchancementHasAirJump = hasAirJump;
        sync();
    }

    public void setGliding(boolean state) {
        this.gliding = state;
        this.sync();
    }

    public void setCanGlide(boolean state) {
        this.canGlide = state;
        this.sync();
    }

    public void changeGlideTime(double time) {
        this.pyrrhianAnkletGlideTime = Math.clamp(this.pyrrhianAnkletGlideTime + time, 0, PyrrhianAnkletItem.getAnkletGlideTime(player));
        this.sync();
    }

    public void setGlideDamageCooldown(int time) {
        this.glideDamageCooldown = Math.max(0, time);
        this.sync();
    }

    public void changeGlideDamageCooldown(int time) {
        this.glideDamageCooldown = Math.max(0, this.glideDamageCooldown + time);
        this.sync();
    }

    public void setFlying(boolean state) {
        this.wasLastFlying = this.flying;
        this.flying = state;
        this.sync();
    }

    public void setCanFly(boolean state) {
        this.canFly = state;
        this.sync();
    }

    public void changeFlightTime(double time) {
        this.pyrrhianAnkletFlightTime = Math.clamp(this.pyrrhianAnkletFlightTime + time, 0, PyrrhianAnkletItem.getAnkletFlyTime(player));
        this.sync();
    }

    public void setFlightDamageCooldown(int time) {
        this.flightDamageCooldown = Math.max(0, time);
        this.sync();
    }

    public void changeFlightDamageCooldown(int time) {
        this.flightDamageCooldown = Math.max(0, this.flightDamageCooldown + time);
        this.sync();
    }

    public void setLensPos(BlockPos pos) {
        this.lensPos = pos;
        this.sync();
    }

    @Override
    public void serverTick() {
        ///  -[PYRRHIAN ANKLET & SHIMMERING LENS]- ///

        if (!CCOSMOUtil.playerHasTrinket(player, ModItems.PYRRHIAN_ANKLET)) {
            if (lensPos == null) {
                setFlying(false);
                setCanFly(false);
            }
            setGliding(false);
            setCanGlide(false);
        }

        if (lensPos == null) {
            if (pyrrhianAnkletFlightTime <= 0 || flightDamageCooldown > 0) setCanFly(false);
            else setCanFly(true);

            if (pyrrhianAnkletGlideTime <= 0 || glideDamageCooldown > 0) setCanGlide(false);
            else setCanGlide(true);
        } else setCanFly(flightDamageCooldown <= 0);

        // Switch to gliding when cooldown runs out
        if (!canFly && flying) {
            setFlying(false);
            if (wasLastFlying && canGlide) setGliding(true);
        }

        if (!canGlide && gliding) {
            setGliding(false);
        }

        if (player.isOnGround() || player.isSwimming() || player.hasVehicle() || player.isInCreativeMode() || player.isSpectator()) {
            setFlying(false);
            setGliding(false);
        }

        // Keep track of time off ground
        if (!player.isOnGround()) nonGroundedTime++;
        else nonGroundedTime = 0;

        // Manage flight time
        if (flying && lensPos == null) {
            Vec3d movement = player.getMovement();
            changeFlightTime(-(Math.abs(movement.x)+Math.abs(movement.y)+Math.abs(movement.z)));

            setGliding(false);
        } else if (flightDamageCooldown <= 0) changeFlightTime(PyrrhianAnkletItem.getAnkletFlyTime(player)/250);

        if (gliding) changeGlideTime(-1);
        else if (glideDamageCooldown <= 0) changeGlideTime(PyrrhianAnkletItem.getAnkletGlideTime(player)/250);

        if (flightDamageCooldown > 0) changeFlightDamageCooldown(-1);
        if (glideDamageCooldown > 0) changeGlideDamageCooldown(-1);

        ///  -[SHIMMERING LENS]- //

        // Reset lens position if outside of effect area
        if (player.getWorld() != null && lensPos != null) {
            World world = player.getWorld();
            BlockEntity blockEntity = world.getBlockEntity(lensPos);
            if (blockEntity instanceof ShimmeringLensBlockEntity lensEntity) {
                List<UUID> uuids = new ArrayList<>();
                lensEntity.getPlayersInBox().forEach(serverPlayerEntity -> uuids.add(serverPlayerEntity.getUuid()));

                if (!uuids.contains(player.getUuid())) setLensPos(null);
            } else {
                setLensPos(null);
            }
        }

        ///  -[SHIMMER JAR]- ///

        if (CCOSMOUtil.playerHasTrinket(player, ModItems.SHIMMER_JAR) && player.getInventory().containsAny(Set.of(ModItems.SHIMMER_POWDER))) {
            ItemStack stack = null;
            ItemStack powderStack = null;
            if (player.getInventory().getMainHandStack().getItem() instanceof ShimmerToolItem)
                stack = player.getInventory().getMainHandStack();
            if (player.getInventory().getStack(41).getItem() instanceof ShimmerToolItem)
                stack = player.getInventory().getStack(41);
            if (stack == null) return;

            if (stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, 0) >= stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_MAX_CHARGES, 8)) return;

            for (ItemStack powder : player.getInventory().main) {
                if (!powder.isOf(ModItems.SHIMMER_POWDER)) continue;

                powderStack = powder;
                break;
            }
            if (powderStack == null) return;

            powderStack.setCount(powderStack.getCount()-1);
            stack.set(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, 0)+1);

            player.getInventory().markDirty();
        }
    }

    public void sync() {
        KEY.sync(this.player);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.pyrrhianAnkletFlightTime = tag.contains("pyrrhianAnkletFlightTime") ? tag.getDouble("pyrrhianAnkletFlightTime") : 0;
        this.flightDamageCooldown = tag.contains("flightDamageCooldown") ? tag.getInt("flightDamageCooldown") : 0;
        this.canFly = tag.contains("canFly") && tag.getBoolean("canFly");
        this.flying = tag.contains("flying") && tag.getBoolean("flying");
        this.pyrrhianAnkletGlideTime = tag.contains("pyrrhianAnkletGlideTime") ? tag.getDouble("pyrrhianAnkletGlideTime") : 0;
        this.glideDamageCooldown = tag.contains("glideDamageCooldown") ? tag.getInt("glideDamageCooldown") : 0;
        this.canGlide = tag.contains("canGlide") && tag.getBoolean("canGlide");
        this.gliding = tag.contains("gliding") && tag.getBoolean("gliding");
        this.wasLastFlying = tag.contains("wasLastFlying") && tag.getBoolean("wasLastFlying");
        this.nonGroundedTime = tag.contains("nonGroundedTime") ? tag.getInt("nonGroundedTime") : 0;
        if (FabricLoader.getInstance().isModLoaded("enchancement")) {
            this.enchancementAirJumpsLeft = tag.contains("enchancementAirJumpsLeft") ? tag.getInt("enchancementAirJumpsLeft") : 0;
            this.enchancementHasAirJump = tag.contains("enchancementHasAirJump") && tag.getBoolean("enchancementHasAirJump");
        }
        this.lensPos = NbtHelper.toBlockPos(tag, "lensPos").isPresent() ? NbtHelper.toBlockPos(tag, "lensPos").get() : null;
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        tag.putDouble("pyrrhianAnkletFlightTime", this.pyrrhianAnkletFlightTime);
        tag.putInt("flightDamageCooldown", this.flightDamageCooldown);
        tag.putBoolean("canFly", this.canFly);
        tag.putBoolean("flying", this.flying);
        tag.putDouble("pyrrhianAnkletGlideTime", this.pyrrhianAnkletGlideTime);
        tag.putInt("glideDamageCooldown", this.glideDamageCooldown);
        tag.putBoolean("canGlide", this.canGlide);
        tag.putBoolean("gliding", this.gliding);
        tag.putBoolean("wasLastFlying", this.wasLastFlying);
        tag.putInt("nonGroundedTime", this.nonGroundedTime);
        if (FabricLoader.getInstance().isModLoaded("enchancement")) {
            tag.putInt("enchancementAirJumpsLeft", this.enchancementAirJumpsLeft);
            tag.putBoolean("enchancementHasAirJump", this.enchancementHasAirJump);
        }
        if (this.lensPos != null) tag.put("lensPos", NbtHelper.fromBlockPos(this.lensPos));
    }
}
