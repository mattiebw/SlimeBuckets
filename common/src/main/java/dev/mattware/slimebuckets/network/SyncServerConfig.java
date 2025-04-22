package dev.mattware.slimebuckets.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SyncServerConfig(boolean slimeBucketingEnabled,
                               boolean magmaCubeBucketingEnabled,
                               boolean enableSlimeChunkDetection,
                               boolean magmaCubeBucketHurts,
                               int maxBucketableSlime) implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, SyncServerConfig> PACKET_CODEC = StreamCodec.of(SyncServerConfig::staticWrite, SyncServerConfig::staticRead);
    public static final Type<SyncServerConfig> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("slimebuckets", "sync_slimebuckets_config"));

    public void write(FriendlyByteBuf buf) {
        staticWrite(buf, this);
    }

    public static void staticWrite(FriendlyByteBuf buf, SyncServerConfig ssc) {
        buf.writeBoolean(ssc.slimeBucketingEnabled);
        buf.writeBoolean(ssc.magmaCubeBucketingEnabled);
        buf.writeBoolean(ssc.enableSlimeChunkDetection);
        buf.writeBoolean(ssc.magmaCubeBucketHurts);
        buf.writeInt(ssc.maxBucketableSlime);
    }

    public static SyncServerConfig staticRead(FriendlyByteBuf buf) {
        return new SyncServerConfig(buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readInt());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
