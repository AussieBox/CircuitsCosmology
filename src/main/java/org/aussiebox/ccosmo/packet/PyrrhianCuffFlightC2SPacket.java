package org.aussiebox.ccosmo.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.aussiebox.ccosmo.CCOSMO;

public record PyrrhianCuffFlightC2SPacket(boolean flightMode) implements CustomPayload {
    public static final Identifier PACKET_ID = CCOSMO.id("pyrrhian_cuff_flight");
    public static final CustomPayload.Id<PyrrhianCuffFlightC2SPacket> ID = new CustomPayload.Id<>(PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, PyrrhianCuffFlightC2SPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, PyrrhianCuffFlightC2SPacket::flightMode,
            PyrrhianCuffFlightC2SPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
