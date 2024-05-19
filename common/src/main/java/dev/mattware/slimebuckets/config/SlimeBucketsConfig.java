package dev.mattware.slimebuckets.config;

import dev.mattware.slimebuckets.SlimeBuckets;
import dev.mattware.slimebuckets.network.SyncServerConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.minecraft.network.FriendlyByteBuf;

@Config(name = SlimeBuckets.MOD_ID)
public class SlimeBucketsConfig extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("server")
    @ConfigEntry.Gui.TransitiveObject
    public SlimeBucketsServerConfig serverConfig = new SlimeBucketsServerConfig();

    // Client config is still created on the server, even though it's never used.
    // TODO: Change?
    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject
    public SlimeBucketsClientConfig clientConfig = new SlimeBucketsClientConfig();
    
    @Config(name = SlimeBuckets.MOD_ID + "-server")
    public static class SlimeBucketsServerConfig implements ConfigData {
        // REMEMBER TO ADD TO writeToBuf and readFromBuf when adding new config!
        // TODO: make this automatic somehow.. serialisation, reflection?
        public boolean slimeBucketingEnabled = true;
        public boolean magmaCubeBucketingEnabled = true;
        public boolean enableSlimeChunkDetection = true;
        public boolean magmaCubeBucketHurts = true;

        public SyncServerConfig writeToPacket() {
            return new SyncServerConfig(
                    slimeBucketingEnabled,
                    magmaCubeBucketingEnabled,
                    enableSlimeChunkDetection,
                    magmaCubeBucketHurts
            );
        }

        public void readFromPacket(SyncServerConfig packet) {
            slimeBucketingEnabled = packet.slimeBucketingEnabled();
            magmaCubeBucketingEnabled = packet.magmaCubeBucketingEnabled();
            enableSlimeChunkDetection = packet.enableSlimeChunkDetection();
            magmaCubeBucketHurts = packet.magmaCubeBucketHurts();
        }
    }

    @Config(name = SlimeBuckets.MOD_ID + "-client")
    public static class SlimeBucketsClientConfig implements ConfigData {
        public boolean enableTrails = true; // Doesn't need sync as its client sided
    }
}
