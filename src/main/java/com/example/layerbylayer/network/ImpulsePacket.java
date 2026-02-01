package com.example.layerbylayer.network;

import com.example.layerbylayer.LayerByLayer;
import com.example.layerbylayer.progression.ProgressionManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ImpulsePacket() implements CustomPacketPayload {

    public static final Type<ImpulsePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(LayerByLayer.MOD_ID, "impulse"));

    public static final StreamCodec<ByteBuf, ImpulsePacket> STREAM_CODEC =
            StreamCodec.unit(new ImpulsePacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ImpulsePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ProgressionManager.get(player).addImpulse(player);
            }
        });
    }
}