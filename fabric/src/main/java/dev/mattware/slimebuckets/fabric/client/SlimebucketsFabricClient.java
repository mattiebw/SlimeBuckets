package dev.mattware.slimebuckets.fabric.client;

import dev.mattware.slimebuckets.particle.BasicDripParticle;
import dev.mattware.slimebuckets.particle.SlimeBucketsParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class SlimebucketsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance().register(SlimeBucketsParticles.FALLING_SLIME.get(), BasicDripParticle.Provider::new);
    }
}
