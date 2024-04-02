package dev.mattware.slimebuckets.fabric;

import dev.mattware.slimebuckets.SlimeBuckets;
import net.fabricmc.api.ModInitializer;

public class SlimeBucketsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SlimeBuckets.init();
        SlimeBuckets.clientInit(); // The function itself handles sided stuff
    }
}
