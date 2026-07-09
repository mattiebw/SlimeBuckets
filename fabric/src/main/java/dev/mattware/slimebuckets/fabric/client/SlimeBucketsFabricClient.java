package dev.mattware.slimebuckets.fabric.client;

import dev.mattware.slimebuckets.particle.BasicDripParticle;
import dev.mattware.slimebuckets.particle.SlimeBucketsParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;

public class SlimeBucketsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParticleProviderRegistry.getInstance().register(SlimeBucketsParticles.FALLING_SLIME.get(), BasicDripParticle.Provider::new);
    }
}
