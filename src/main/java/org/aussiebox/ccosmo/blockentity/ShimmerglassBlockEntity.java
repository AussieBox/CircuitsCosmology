package org.aussiebox.ccosmo.blockentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class ShimmerglassBlockEntity extends BlockEntity {
    private BlockState previousBlockState;
    private int ticksAliveLeft;
    private UUID owner;

    public ShimmerglassBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHIMMERGLASS_BLOCK_ENTITY, pos, state);
    }

    public BlockState getPreviousBlockState() {
        if (previousBlockState == null) return Blocks.AIR.getDefaultState();
        return previousBlockState;
    }

    public void setPreviousBlockState(BlockState blockState) {
        previousBlockState = blockState;
        markDirty();
    }

    public int getTicksAliveLeft() {
        return ticksAliveLeft;
    }

    public void setTicksAliveLeft(int ticks) {
        ticksAliveLeft = ticks;
        markDirty();
    }

    public void resetTicksAliveLeft() {
        ticksAliveLeft = 200;
        markDirty();
    }

    public void makePermanent() {
        ticksAliveLeft = -1;
        markDirty();
    }

    public void setOwner(Entity entity) {
        owner = entity.getUuid();
        markDirty();
    }

    public UUID getOwner() {
        if (owner == null) return UUID.fromString("00000000-0000-0000-0000-000000000000");
        return owner;
    }

    public void revert(World world, BlockPos blockPos, ShimmerglassBlockEntity entity) {
        world.setBlockState(blockPos, entity.getPreviousBlockState());
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, ShimmerglassBlockEntity entity) {
        if (entity.ticksAliveLeft > 0) entity.ticksAliveLeft--;
        if (entity.ticksAliveLeft == 0) entity.revert(world, blockPos, entity);
    }

    @Override
    protected void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        tag.putInt("ticksAliveLeft", ticksAliveLeft);
        if (previousBlockState != null) tag.put("previousBlockState", NbtHelper.fromBlockState(previousBlockState));
        if (owner != null) tag.putUuid("owner", owner);
    }

    @Override
    protected void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        ticksAliveLeft = tag.contains("ticksAliveLeft") ? tag.getInt("ticksAliveLeft") : 200;
        owner = tag.contains("owner") ? tag.getUuid("owner") : UUID.fromString("00000000-0000-0000-0000-000000000000");

        if (world == null) {
            previousBlockState = Blocks.AIR.getDefaultState();
            return;
        }

        RegistryEntryLookup<Block> lookup = world.createCommandRegistryWrapper(RegistryKeys.BLOCK);
        previousBlockState = tag.contains("previousBlockState") ? NbtHelper.toBlockState(lookup, tag.getCompound("previousBlockState")) : Blocks.AIR.getDefaultState();
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
