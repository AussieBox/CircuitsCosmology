package org.aussiebox.ccosmo.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlushieBlockEntity extends BlockEntity {
    public double squash;

    public PlushieBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLUSHIE_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, @NotNull PlushieBlockEntity spark) {
        if (spark.squash > (double)0.0F) {
            spark.squash /= 3.0F;
            if (spark.squash < (double)0.01F) {
                spark.squash = 0.0F;
                if (world != null) {
                    world.updateListeners(pos, state, state, 2);
                }
            }
        }

    }

    public void squish(int squash) {
        this.squash += squash;
        if (this.world != null) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 2);
        }

        this.markDirty();
    }

    protected void writeNbt(NbtCompound nbt, RegistryWrapper.@NotNull WrapperLookup registries) {
        nbt.putDouble("squash", this.squash);
    }

    protected void readNbt(NbtCompound nbt, RegistryWrapper.@NotNull WrapperLookup registries) {
        this.squash = nbt.getDouble("squash");
    }

    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public @NotNull NbtCompound toInitialChunkDataNbt(RegistryWrapper.@NotNull WrapperLookup registries) {
        return this.createNbt(registries);
    }
}

