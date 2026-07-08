package dev.mattware.slimebuckets.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

// Minecraft's DripParticle doesn't have a provider and is all-around a bit fucked up, so let's make our own:
public class BasicDripParticle extends SingleQuadParticle {
    protected BasicDripParticle(ClientLevel clientLevel, double d, double e, double f, TextureAtlasSprite sprite) {
        super(clientLevel, d, e, f, sprite);
        this.setSize(0.1f, 0.1f);
        speedUpWhenYMotionIsBlocked = false;
        lifetime = 60;
        gravity = 0.8f;
        hasPhysics = true;
    }

    @Override
    protected @NonNull Layer getLayer() {
        return Layer.OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(@NonNull SimpleParticleType particleOptions, @NonNull ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, @NonNull RandomSource randomSource) {
            return new BasicDripParticle(clientLevel, d, e, f, this.sprite.get(randomSource));
        }
    }
}
