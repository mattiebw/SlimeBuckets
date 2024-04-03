package dev.mattware.slimebuckets.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

// Minecraft's DripParticle doesn't have a provider and is all-around a bit fucked up, so let's make our own:
@Environment(EnvType.CLIENT)
public class BasicDripParticle extends TextureSheetParticle {
    protected BasicDripParticle(ClientLevel clientLevel, double d, double e, double f) {
        super(clientLevel, d, e, f);
        speedUpWhenYMotionIsBlocked = false;
        lifetime = 60;
        gravity = 0.8f;
        hasPhysics = true;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            BasicDripParticle dripParticle = new BasicDripParticle(clientLevel, d, e, f);
            dripParticle.pickSprite(this.sprite);
            return dripParticle;
        }
    }
}
