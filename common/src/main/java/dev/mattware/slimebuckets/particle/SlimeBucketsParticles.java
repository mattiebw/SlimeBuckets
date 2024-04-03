package dev.mattware.slimebuckets.particle;

import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.mattware.slimebuckets.mixin.SimpleParticleTypeAccessor;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

import static dev.mattware.slimebuckets.SlimeBuckets.MOD_ID;

public class SlimeBucketsParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(MOD_ID, Registries.PARTICLE_TYPE);
    public static final RegistrySupplier<SimpleParticleType> FALLING_SLIME = PARTICLES.register("falling_slime", () -> SimpleParticleTypeAccessor.newSimpleParticleType(false));

    public static void register() {
        PARTICLES.register();
    }

    public static void registerClient() {
        ParticleProviderRegistry.register(FALLING_SLIME, BasicDripParticle.Provider::new);
    }
}
