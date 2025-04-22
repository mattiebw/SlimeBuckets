package dev.mattware.slimebuckets.particle;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

import static dev.mattware.slimebuckets.SlimeBuckets.MOD_ID;

public class SlimeBucketsParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(MOD_ID, Registries.PARTICLE_TYPE);
    public static final RegistrySupplier<SimpleParticleType> FALLING_SLIME = PARTICLES.register("falling_slime", () ->
            new SimpleParticleType(false) {});

    public static void register() {
        PARTICLES.register();
    }
}
