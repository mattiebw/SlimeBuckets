package dev.mattware.slimebuckets.mixin;

import dev.mattware.slimebuckets.SlimeBuckets;
import dev.mattware.slimebuckets.accessors.AbstractClientPlayerMixinAccess;
import dev.mattware.slimebuckets.item.SlimeBucketsItemComponents;
import dev.mattware.slimebuckets.item.SlimeBucketsItems;
import dev.mattware.slimebuckets.particle.SlimeBucketsParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin implements AbstractClientPlayerMixinAccess {
    @Shadow @Final public ClientLevel clientLevel;
    @Unique
    public Vector3f slimebuckets$lastMainHandPos = new Vector3f();
    @Unique
    public Vector3f slimebuckets$lastOffHandPos = new Vector3f();

    @Override
    public Vector3f slimebuckets$getLastMainHandPos() {
        return slimebuckets$lastMainHandPos;
    }

    @Override
    public Vector3f slimebuckets$getLastOffHandPos() {
        return slimebuckets$lastOffHandPos;
    }

    @Override
    public void slimebuckets$setLastMainHandPos(Vector3f pos) {
        slimebuckets$lastMainHandPos = pos;
    }

    @Override
    public void slimebuckets$setLastOffHandPos(Vector3f pos) {
        slimebuckets$lastOffHandPos = pos;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (!SlimeBuckets.CLIENT_CONFIG.enableTrails)
            return;

        AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer) (Object) this;
        var mainHandParticle = slimebuckets$getParticleForItemStack(abstractClientPlayer.getMainHandItem(), abstractClientPlayer.clientLevel);
        var offHandParticle = slimebuckets$getParticleForItemStack(abstractClientPlayer.getOffhandItem(), abstractClientPlayer.clientLevel);
        var cam = Minecraft.getInstance().getEntityRenderDispatcher().camera;

        if (mainHandParticle != null) {
            Vec3 pos = new Vec3(slimebuckets$getLastMainHandPos());
            if (abstractClientPlayer == Minecraft.getInstance().player && !cam.isDetached()) {
                pos = cam.getPosition();
                var left = new Vec3(cam.getLeftVector());
                pos = pos.add(left.x * -0.5f, (left.y * -0.5f) - .4f, left.z * -0.5f);
            }
            clientLevel.addParticle(mainHandParticle, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
        }

        if (offHandParticle != null) {
            Vec3 pos = new Vec3(slimebuckets$getLastOffHandPos());
            if (abstractClientPlayer == Minecraft.getInstance().player && !cam.isDetached()) {
                pos = cam.getPosition();
                var left = new Vec3(cam.getLeftVector());
                pos = pos.add(left.x * 0.5f, (left.y * 0.5f) - .4f, left.z * 0.5f);
            }
            clientLevel.addParticle(offHandParticle, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Unique
    private ParticleOptions slimebuckets$getParticleForItemStack(ItemStack stack, ClientLevel level) {
        if (stack.getItem() == SlimeBucketsItems.SLIME_BUCKET.get()
                && (stack.has(SlimeBucketsItemComponents.HOLDER_IN_SLIME_CHUNK.get()) || level.getGameTime() % 3 == 0)) {
            return SlimeBucketsParticles.FALLING_SLIME.get();
        }

        if (stack.getItem() == SlimeBucketsItems.MAGMA_CUBE_BUCKET.get() && level.getGameTime() % 3 == 0) {
            return ParticleTypes.FALLING_LAVA;
        }

        return null;
    }
}
