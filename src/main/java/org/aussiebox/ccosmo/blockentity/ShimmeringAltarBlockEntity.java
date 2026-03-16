package org.aussiebox.ccosmo.blockentity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Clearable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.aussiebox.ccosmo.recipe.inventory.ShimmeringAltarInventory;

import java.util.Collections;


public class ShimmeringAltarBlockEntity extends BlockEntity implements Inventory, Clearable {
    @Getter @Setter private ItemStack affectedStack = ItemStack.EMPTY;
    @Getter private final DefaultedList<ItemStack> inventory = DefaultedList.of();

    public ShimmeringAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHIMMERING_ALTAR_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, ShimmeringAltarBlockEntity entity) {

    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
    }

    public void addStack(ItemStack stack) {
        inventory.add(stack);
    }

    public void fullyRemoveStack(int slot) {
        inventory.remove(slot);
    }

    public void fullyRemoveStack(ItemStack stack) {
        inventory.remove(stack);
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
        this.inventory.clear();
    }

    public ShimmeringAltarInventory toRecipeInventory() {
        return new ShimmeringAltarInventory(affectedStack, inventory);
    }

    @Override
    protected void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        Inventories.writeNbt(tag, inventory, wrapperLookup);
        if (!affectedStack.isEmpty())
            tag.put("affectedStack", affectedStack.encode(wrapperLookup));
    }

    @Override
    protected void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        Inventories.readNbt(tag, inventory, wrapperLookup);
        affectedStack = ItemStack.fromNbtOrEmpty(wrapperLookup, tag);
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
