package org.aussiebox.ccosmo.cca;

import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.Vec3d;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.component.ModDataComponentTypes;
import org.aussiebox.ccosmo.item.ModItems;
import org.aussiebox.ccosmo.item.custom.PyrrhianBeltItem;
import org.aussiebox.ccosmo.item.custom.ShimmerToolItem;
import org.aussiebox.ccosmo.util.CCOSMOUtil;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.Set;

public class TrinketComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<TrinketComponent> KEY = ComponentRegistry.getOrCreate(CCOSMO.id("trinket_component"), TrinketComponent.class);
    private final PlayerEntity player;

    @Getter
    private boolean gliding = false;
    @Getter
    private boolean flying = false;
    @Getter
    private boolean canFly = false;
    @Getter
    private double pyrrhianBeltFlightTime;
    @Getter
    private boolean wasLastFlying = false;
    @Getter
    private int nonGroundedTime;

    public TrinketComponent(PlayerEntity player) {
        this.player = player;
    }

    public void setGliding(boolean state) {
        this.gliding = state;
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
        this.pyrrhianBeltFlightTime = Math.clamp(this.pyrrhianBeltFlightTime + time, 0, PyrrhianBeltItem.getBeltFlyTime(player));
        this.sync();
    }

    @Override
    public void serverTick() {
        ///  -[PYRRHIAN BELT]- ///

        if (pyrrhianBeltFlightTime <= 0) setCanFly(false);
        else setCanFly(true);

        // Switch to gliding when cooldown runs out
        if (!canFly && flying) {
            setFlying(false);
            if (wasLastFlying) setGliding(true);
        }

        if (player.isOnGround() || player.isSwimming() || player.hasVehicle() || player.isInCreativeMode() || player.isSpectator()) {
            setFlying(false);
            setGliding(false);
        }

        if (!CCOSMOUtil.playerHasTrinket(player, ModItems.PYRRHIAN_BELT)) {
            setFlying(false);
            setCanFly(false);
            setGliding(false);
        }

        // Keep track of time off ground
        if (!player.isOnGround()) nonGroundedTime++;
        else nonGroundedTime = 0;

        // Manage flight time
        if (flying) {
            Vec3d movement = player.getMovement();
            changeFlightTime(-Math.abs(movement.length()));

            setGliding(false);
        } else {
            changeFlightTime(PyrrhianBeltItem.getBeltFlyTime(player)/100);
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
        this.pyrrhianBeltFlightTime = tag.contains("pyrrhianBeltFlightTime") ? tag.getDouble("pyrrhianBeltFlightTime") : 0;
        this.canFly = tag.contains("canFly") && tag.getBoolean("canFly");
        this.flying = tag.contains("flying") && tag.getBoolean("flying");
        this.gliding = tag.contains("gliding") && tag.getBoolean("gliding");
        this.wasLastFlying = tag.contains("wasLastFlying") && tag.getBoolean("wasLastFlying");
        this.nonGroundedTime = tag.contains("nonGroundedTime") ? tag.getInt("nonGroundedTime") : 0;
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        tag.putDouble("pyrrhianBeltFlightTime", this.pyrrhianBeltFlightTime);
        tag.putBoolean("canFly", this.canFly);
        tag.putBoolean("flying", this.flying);
        tag.putBoolean("gliding", this.gliding);
        tag.putBoolean("wasLastFlying", this.wasLastFlying);
        tag.putInt("nonGroundedTime", this.nonGroundedTime);
    }
}
