package org.aussiebox.ccosmo.blockentity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Clearable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.recipe.ShimmeringRecipe;
import org.aussiebox.ccosmo.recipe.inventory.ShimmeringAltarInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ShimmeringAltarBlockEntity extends BlockEntity implements Inventory, Clearable {
    @Getter private ItemStack affectedStack = ItemStack.EMPTY;
    @Getter private final DefaultedList<ItemStack> inventory = DefaultedList.of();
    @Getter @Setter private ShimmeringRecipe recipeBeingCrafted = null;
    @Getter @Setter private int craftAnimationTicks;
    @Getter @Setter private int lastCraftAnimationTicks;
    @Getter @Setter private int returnAnimationTicks;
    @Getter @Setter private int lastReturnAnimationTicks;

    public ShimmeringAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHIMMERING_ALTAR_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, ShimmeringAltarBlockEntity entity) {
        if (world != null && !world.isClient) {
            entity.setLastCraftAnimationTicks(entity.getLastCraftAnimationTicks());
            entity.setLastReturnAnimationTicks(entity.getLastReturnAnimationTicks());

            if (entity.getCraftAnimationTicks() > 0) entity.setCraftAnimationTicks(entity.getLastCraftAnimationTicks()-1);
            if (entity.getCraftAnimationTicks() == 0 && entity.getReturnAnimationTicks() > 0) entity.setReturnAnimationTicks(entity.getReturnAnimationTicks()-1);

            if (entity.getCraftAnimationTicks() == 0 && entity.getLastCraftAnimationTicks() == 1 && entity.getRecipeBeingCrafted() != null) {
                ItemStack stack = entity.getRecipeBeingCrafted().getOutput().copy();

                for (RegistryEntry<Enchantment> enchantment : entity.getAffectedStack().getEnchantments().getEnchantments())
                    stack.addEnchantment(enchantment, entity.getAffectedStack().getEnchantments().getLevel(enchantment));

                entity.setAffectedStack(stack);
                entity.setReturnAnimationTicks(40);
                entity.clear();
            }

            if (entity.getAffectedStack().isOf(Items.AIR)) entity.setAffectedStack(ItemStack.EMPTY);
            entity.inventory.removeIf(stack -> stack.isOf(Items.AIR));

            entity.markDirty();
        }
    }

    public void startCrafting(ShimmeringRecipe recipe) {
        setRecipeBeingCrafted(recipe);
        setCraftAnimationTicks(40);
        setReturnAnimationTicks(0);
        markDirty();
    }

    @Override
    public int size() {
        return inventory.size();
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
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(world.getBlockState(pos)));
            return stack;
        }
        return null;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (world != null && !world.isClient) {
            ItemStack stack = Inventories.removeStack(inventory, slot);
            markDirty();
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(world.getBlockState(pos)));
            return stack;
        }
        return null;
    }

    public void setAffectedStack(ItemStack stack) {
        if (world != null && !world.isClient) {
            affectedStack = stack;
            markDirty();
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(world.getBlockState(pos)));
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (world != null && !world.isClient) {
            inventory.set(slot, stack);
            markDirty();
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(world.getBlockState(pos)));
        }
    }

    public void addStack(ItemStack stack) {
        if (world != null && !world.isClient) {
            inventory.add(stack);
            markDirty();
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(world.getBlockState(pos)));
        }
    }

    public void fullyRemoveStack(ItemStack stack) {
        if (world != null && !world.isClient && inventory.contains(stack)) {
            inventory.remove(stack);
            markDirty();
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(world.getBlockState(pos)));
        }
    }

    public List<ItemStack> fullyRemoveStacks(ItemStack stack) {
        List<ItemStack> removedList = new ArrayList<>();
        for (ItemStack ingredient : inventory) {
            if (ingredient.isOf(stack.getItem()) && inventory.contains(ingredient)) {
                removedList.add(ingredient.copy());
                fullyRemoveStack(ingredient);
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
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(world.getBlockState(pos)));
        }
    }

    public ShimmeringAltarInventory toRecipeInventory() {
        return new ShimmeringAltarInventory(affectedStack, inventory, this.pos);
    }

    @Override
    public void markDirty() {
        if (this.world != null && !world.isClient) {
            markDirty(this.world, this.pos, this.getCachedState());
            world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
        }
    }

    @Override
    protected void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        super.writeNbt(tag, wrapperLookup);

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
        super.readNbt(tag, wrapperLookup);

        Inventories.readNbt(tag, inventory, wrapperLookup);
        affectedStack = ItemStack.fromNbt(wrapperLookup, tag.getCompound("affectedStack")).orElse(ItemStack.EMPTY);
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
