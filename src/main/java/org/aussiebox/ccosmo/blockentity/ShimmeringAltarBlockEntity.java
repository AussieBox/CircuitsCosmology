package org.aussiebox.ccosmo.blockentity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Clearable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.recipe.ShimmeringRecipe;
import org.aussiebox.ccosmo.recipe.inventory.ShimmeringAltarInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ShimmeringAltarBlockEntity extends BlockEntity implements Inventory, Clearable {
    @Getter private static ItemStack affectedStack = ItemStack.EMPTY;
    @Getter private static final DefaultedList<ItemStack> inventory = DefaultedList.of();
    @Getter @Setter private static ShimmeringRecipe recipeBeingCrafted = null;
    @Getter @Setter private static int craftAnimationTicks;
    @Getter @Setter private static int lastCraftAnimationTicks;
    @Getter @Setter private static int returnAnimationTicks;
    @Getter @Setter private static int lastReturnAnimationTicks;

    public ShimmeringAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHIMMERING_ALTAR_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, ShimmeringAltarBlockEntity entity) {
        if (world != null && !world.isClient) {
            setLastCraftAnimationTicks(craftAnimationTicks);
            setLastReturnAnimationTicks(returnAnimationTicks);

            if (getCraftAnimationTicks() > 0) setCraftAnimationTicks(getCraftAnimationTicks()-1);
            if (getCraftAnimationTicks() == 0 && getReturnAnimationTicks() > 0) setReturnAnimationTicks(getReturnAnimationTicks()-1);

            if (getCraftAnimationTicks() == 0 && getLastCraftAnimationTicks() == 1 && getRecipeBeingCrafted() != null) {
                entity.setAffectedStack(getRecipeBeingCrafted().getOutput().copy());
                setReturnAnimationTicks(40);
                entity.clear();
            }

            if (affectedStack.isOf(Items.AIR)) affectedStack = ItemStack.EMPTY;
            inventory.removeIf(stack -> stack.isOf(Items.AIR));

            entity.markDirty();
        }
    }

    public void startCrafting(ShimmeringRecipe recipe) {
        setRecipeBeingCrafted(recipe);
        setCraftAnimationTicks(40);
        setReturnAnimationTicks(0);
    }

    @Override
    public int size() {
        if (world != null && !world.isClient)
            return inventory.size();
        return 0;
    }

    @Override
    public boolean isEmpty() {
        if (world != null && !world.isClient)
            return getInventoryWithoutEmpty().isEmpty();
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (world != null && !world.isClient)
                return inventory.get(slot);
        return null;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (world != null && !world.isClient) {
            ItemStack stack = Inventories.splitStack(inventory, slot, amount);
            markDirty();
            return stack;
        }
        return null;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (world != null && !world.isClient) {
            ItemStack stack = Inventories.removeStack(inventory, slot);
            markDirty();
            return stack;
        }
        return null;
    }

    public void setAffectedStack(ItemStack stack) {
        if (world != null && !world.isClient) {
            affectedStack = stack;
            markDirty();
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (world != null && !world.isClient) {
            inventory.set(slot, stack);
            markDirty();
        }
    }

    public void addStack(ItemStack stack) {
        if (world != null && !world.isClient) {
            inventory.add(stack);
            markDirty();
        }
    }

    public void fullyRemoveStack(ItemStack stack) {
        if (world != null && !world.isClient && inventory.contains(stack)) {
            inventory.remove(stack);
            markDirty();
        }
    }

    public List<ItemStack> fullyRemoveStacks(ItemStack stack, int max) {
        List<ItemStack> removedList = new ArrayList<>();
        int removedStacks = 0;
        for (ItemStack ingredient : inventory) {
            if (ingredient.isOf(stack.getItem()) && inventory.contains(ingredient) && removedStacks < max) {
                fullyRemoveStack(ingredient);
                markDirty();

                removedList.add(ingredient);
                removedStacks++;
            }
        }
        return removedList;
    }

    public DefaultedList<ItemStack> getInventoryWithoutEmpty() {
        DefaultedList<ItemStack> withoutEmpty = inventory;
        withoutEmpty.removeAll(Collections.singleton(ItemStack.EMPTY));
        return withoutEmpty;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        if (world != null && !world.isClient) {
            inventory.clear();
            markDirty();
        }
    }

    public ShimmeringAltarInventory toRecipeInventory() {
        return new ShimmeringAltarInventory(affectedStack, inventory, this.pos);
    }

    @Override
    protected void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        Inventories.writeNbt(tag, inventory, wrapperLookup);
        if (!affectedStack.isEmpty())
            tag.put("affectedStack", affectedStack.encode(wrapperLookup));
        tag.putInt("craftAnimationTicks", craftAnimationTicks);
        tag.putInt("lastCraftAnimationTicks", lastCraftAnimationTicks);
        tag.putInt("returnAnimationTicks", returnAnimationTicks);
        tag.putInt("lastReturnAnimationTicks", lastReturnAnimationTicks);
        if (recipeBeingCrafted != null)
            tag.put("recipeBeingCrafted", ShimmeringRecipe.CODEC.encodeStart(NbtOps.INSTANCE, recipeBeingCrafted).getOrThrow());
    }

    @Override
    protected void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        Inventories.readNbt(tag, inventory, wrapperLookup);
        affectedStack = ItemStack.fromNbtOrEmpty(wrapperLookup, tag);
        if (tag.contains("craftAnimationTicks")) craftAnimationTicks = tag.getInt("craftAnimationTicks");
        if (tag.contains("lastCraftAnimationTicks")) lastCraftAnimationTicks = tag.getInt("lastCraftAnimationTicks");
        if (tag.contains("returnAnimationTicks")) returnAnimationTicks = tag.getInt("returnAnimationTicks");
        if (tag.contains("lastReturnAnimationTicks")) lastReturnAnimationTicks = tag.getInt("lastReturnAnimationTicks");
        if (tag.contains("recipeBeingCrafted")) {
            Recipe<?> recipe = ShimmeringRecipe.CODEC.parse(NbtOps.INSTANCE, tag.get("recipeBeingCrafted"))
                    .getPartialOrThrow(error -> {
                        CCOSMO.LOGGER.error("Recipe parsing failed: {}", error);
                        return null;
                    });
            recipeBeingCrafted = (ShimmeringRecipe) recipe;
        }
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
