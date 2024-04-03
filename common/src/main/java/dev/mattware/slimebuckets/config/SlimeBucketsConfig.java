package dev.mattware.slimebuckets.config;

import dev.mattware.slimebuckets.SlimeBuckets;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = SlimeBuckets.MOD_ID)
public class SlimeBucketsConfig implements ConfigData {
    public boolean enableSlimeChunkDetection = true;
}
