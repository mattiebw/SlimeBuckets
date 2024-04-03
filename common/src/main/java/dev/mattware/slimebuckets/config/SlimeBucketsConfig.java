package dev.mattware.slimebuckets.config;

import dev.mattware.slimebuckets.SlimeBuckets;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = SlimeBuckets.MOD_ID)
public class SlimeBucketsConfig implements ConfigData {
    public boolean slimeBucketingEnabled = true; // TODO: sync
    public boolean magmaCubeBucketingEnabled = true; // TODO: sync
    public boolean enableSlimeChunkDetection = true; // Doesn't need sync as it only effects server
    public boolean enableTrails = true; // Doesn't need sync as its client sided
    public boolean magmaCubeBucketHurts = true; // TODO: sync
}
