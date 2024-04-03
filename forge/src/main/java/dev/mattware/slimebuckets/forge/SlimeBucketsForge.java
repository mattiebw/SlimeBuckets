package dev.mattware.slimebuckets.forge;

import com.google.common.eventbus.EventBus;
import dev.architectury.platform.forge.EventBuses;
import dev.mattware.slimebuckets.SlimeBuckets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SlimeBuckets.MOD_ID)
public class SlimeBucketsForge {
    public SlimeBucketsForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(SlimeBuckets.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        SlimeBuckets.init();
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(SlimeBuckets::clientInit);
    }
}
