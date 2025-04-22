package dev.mattware.slimebuckets.neoforge.client;

import dev.mattware.slimebuckets.SlimeBuckets;
import dev.mattware.slimebuckets.particle.BasicDripParticle;
import dev.mattware.slimebuckets.particle.SlimeBucketsParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@Mod(value = SlimeBuckets.MOD_ID, dist = Dist.CLIENT)
public class SlimebucketsNeoForgeClient {
    public SlimebucketsNeoForgeClient(IEventBus bus) {
        bus.addListener(SlimebucketsNeoForgeClient::registerParticleProviders);
    }

    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SlimeBucketsParticles.FALLING_SLIME.get(), BasicDripParticle.Provider::new);
    }
}
