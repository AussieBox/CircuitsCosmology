package org.aussiebox.bitsofbox.cca;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import org.aussiebox.bitsofbox.BOB;
import org.aussiebox.bitsofbox.BOBConstants;
import org.aussiebox.bitsofbox.component.ModDataComponentTypes;
import org.aussiebox.bitsofbox.item.custom.ShimmerToolItem;
import org.aussiebox.bitsofbox.util.BOBUtil;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class ShimmerComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<ShimmerComponent> KEY = ComponentRegistry.getOrCreate(BOB.id("shimmer_component"), ShimmerComponent.class);
    private final PlayerEntity player;
    public int obtainmentsToday = 0;
    public int shimmerseepTicks = 2400;

    public ShimmerComponent(PlayerEntity player) {
        this.player = player;
    }

    public void obtainPowder() {
        obtainmentsToday++;
        this.sync();
    }

    public void setShimmerseepTicks(int ticks) {
        shimmerseepTicks = ticks;
        this.sync();
    }

    @Override
    public void serverTick() {
        if (player.getWorld().getTimeOfDay() == 0) {
            obtainmentsToday = 0;
        }
        shimmerseepTicks--;
        if (shimmerseepTicks <= 0) {
            shimmerseepTicks = 2400;
            PlayerInventory inventory = player.getInventory();
            boolean playSound = false;
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);

                if (BOBUtil.stackHasEnchantment(player.getWorld(), stack, BOBConstants.SHIMMERSEEP_ENCHANT)) {
                    int charges = stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, 0);
                    if (charges < stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_MAX_CHARGES, 5)) {
                        stack.set(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, charges+1);
                        playSound = true;
                    }
                }
            }
            inventory.markDirty();
            if (playSound) player.playSoundToPlayer(BOBConstants.SHIMMERSEEP_CHARGE_SOUND, SoundCategory.PLAYERS, 1.0F, 1.0F);
            if (player.getMainHandStack().getItem() instanceof ShimmerToolItem && BOBUtil.stackHasEnchantment(player.getWorld(), player.getMainHandStack(), BOBConstants.SHIMMERSEEP_ENCHANT))
                player.playSoundToPlayer(BOBConstants.SHIMMER_TOOL_CHARGE_SOUND, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        this.sync();
    }

    public void sync() {
        KEY.sync(this.player);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.obtainmentsToday = tag.contains("obtainmentsToday") ? tag.getInt("obtainmentsToday") : 0;
        this.shimmerseepTicks = tag.contains("shimmerseepTicks") ? tag.getInt("shimmerseepTicks") : 0;
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        tag.putInt("obtainmentsToday", this.obtainmentsToday);
        tag.putInt("shimmerseepTicks", this.shimmerseepTicks);
    }
}
