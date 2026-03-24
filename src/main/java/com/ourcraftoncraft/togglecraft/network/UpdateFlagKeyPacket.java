package com.ourcraftoncraft.togglecraft.network;

import com.ourcraftoncraft.togglecraft.blocks.FeatureFlagBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateFlagKeyPacket {
    private final BlockPos pos;
    private final String flagKey;

    public UpdateFlagKeyPacket(BlockPos pos, String flagKey) {
        this.pos = pos;
        this.flagKey = flagKey;
    }

    public UpdateFlagKeyPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.flagKey = buf.readUtf(256);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(flagKey);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.level() instanceof ServerLevel level) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof FeatureFlagBlockEntity flagBE) {
                    flagBE.setFlagKey(flagKey);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
