package org.aussiebox.ccosmo.blockentity;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.aussiebox.ccosmo.cca.TrinketComponent;

import java.util.ArrayList;
import java.util.List;

public class ShimmeringLensBlockEntity extends BlockEntity {
    @Getter private int level = 0;

    public ShimmeringLensBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHIMMERING_LENS_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState blockState, ShimmeringLensBlockEntity entity) {
        List<ServerPlayerEntity> players = entity.getPlayersInBox();
        for (ServerPlayerEntity player : players) {
            TrinketComponent trinkets = TrinketComponent.KEY.get(player);
            trinkets.setLensPos(pos);
        }
    }

    public Box getBox() {
        return switch (level) {
            case 1 -> new Box(pos.getX()-5, pos.getY()-12, pos.getZ()-5, pos.getX()+5, pos.getY()+12, pos.getZ()+5);
            case 2 -> new Box(pos.getX()-6, pos.getY()-16, pos.getZ()-6, pos.getX()+6, pos.getY()+16, pos.getZ()+6);
            case 3 -> new Box(pos.getX()-7, pos.getY()-20, pos.getZ()-7, pos.getX()+7, pos.getY()+20, pos.getZ()+7);
            case 4 -> new Box(pos.getX()-8, pos.getY()-24, pos.getZ()-8, pos.getX()+8, pos.getY()+24, pos.getZ()+8);
            default -> null;
        };
    }

    public List<ServerPlayerEntity> getPlayersInBox() {
        if (world == null) return new ArrayList<>();

        Box effectBox = getBox();
        if (effectBox == null) return new ArrayList<>();
        return world.getEntitiesByClass(ServerPlayerEntity.class, effectBox, player -> !player.isSpectator() && !player.isInCreativeMode());
    }

    public void setLevel(int level) {
        this.level = level;
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(tag, registryLookup);

        tag.putInt("level", level);
    }

    @Override
    protected void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(tag, registryLookup);

        if (tag.contains("level")) this.level = tag.getInt("level");
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
