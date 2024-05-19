package dev.mattware.slimebuckets.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record SyncServerConfig(boolean slimeBucketingEnabled,
                               boolean magmaCubeBucketingEnabled,
                               boolean enableSlimeChunkDetection,
                               boolean magmaCubeBucketHurts,
                               int maxBucketableSlime) implements CustomPacketPayload {

    public static StreamCodec<RegistryFriendlyByteBuf, SyncServerConfig> PACKET_CODEC = StreamCodec.of(SyncServerConfig::staticWrite, SyncServerConfig::new);

    public SyncServerConfig(RegistryFriendlyByteBuf buf) {
        this(buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readInt());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        staticWrite(buf, this);
    }

    public static void staticWrite(RegistryFriendlyByteBuf buf, SyncServerConfig ssc) {
        buf.writeBoolean(ssc.slimeBucketingEnabled);
        buf.writeBoolean(ssc.magmaCubeBucketingEnabled);
        buf.writeBoolean(ssc.enableSlimeChunkDetection);
        buf.writeBoolean(ssc.magmaCubeBucketHurts);
        buf.writeInt(ssc.maxBucketableSlime);
    }

    public static @NotNull Type<SyncServerConfig> staticType() {
        return CustomPacketPayload.createType("sync_slimebuckets_config");
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return staticType();
    }
}
