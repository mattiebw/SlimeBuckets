package dev.mattware.slimebuckets.mixin;

import dev.mattware.slimebuckets.accessors.PlayerRendererMixinAccessor;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin implements PlayerRendererMixinAccessor {
//    @Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V",
//            at = @At("TAIL"))
//    public void doParticles(AbstractClientPlayer abstractClientPlayer, PlayerRenderState playerRenderState, float f, CallbackInfo ci) {
//        if (abstractClientPlayer.clientLevel.getGameTime() % 3 != 0) return;
//        var mainHand = abstractClientPlayer.getItemInHand(InteractionHand.MAIN_HAND);
//        var offHand = abstractClientPlayer.getItemInHand(InteractionHand.OFF_HAND);
//        ParticleOptions mainHandParticle = slimebuckets$getParticleForStack(mainHand);
//        ParticleOptions offHandParticle = slimebuckets$getParticleForStack(offHand);
//
//        if (mainHandParticle == null && offHandParticle == null)
//            return;
//
//        PoseStack poseStack = new PoseStack();
//        PlayerRenderer playerRenderer = (PlayerRenderer) (Object) this;
//        if (playerRenderState.hasPose(Pose.SLEEPING)) {
//            Direction direction = playerRenderState.bedOrientation;
//            if (direction != null) {
//                float fl = playerRenderState.eyeHeight - 0.1F;
//                poseStack.translate((float)(-direction.getStepX()) * fl, 0.0F, (float)(-direction.getStepZ()) * fl);
//            }
//        }
//
//        float g = playerRenderState.scale;
//        poseStack.scale(g, g, g);
//        ((PlayerRendererAccessor)playerRenderer).callSetupRotations(playerRenderState, poseStack, playerRenderState.bodyRot, g);
//        poseStack.scale(-1.0F, -1.0F, 1.0F);
//        ((PlayerRendererAccessor)playerRenderer).callScale(playerRenderState, poseStack);
//        poseStack.translate(0.0F, -1.501F, 0.0F);
//        playerRenderer.getModel().setupAnim(playerRenderState);
//
//        if (mainHandParticle != null) {
//            poseStack.pushPose();
//            (playerRenderer.getModel()).translateToHand(HumanoidArm.LEFT, poseStack);
//            Vector3f pos = poseStack.last().pose().getTranslation(new Vector3f());
//            abstractClientPlayer.clientLevel.addParticle(mainHandParticle, pos.x, pos.y, pos.z, 0, 0, 0);
//            poseStack.popPose();
//        }
//
//        if (offHandParticle != null) {
//
//        }
//    }
//
//    // This should be elsewhere.
//    @Unique
//    public ParticleOptions slimebuckets$getParticleForStack(ItemStack stack) {
//        if (stack.getItem() == SlimeBucketsItems.SLIME_BUCKET.get())
//            return SlimeBucketsParticles.FALLING_SLIME.get();
//
//        if (stack.getItem() == SlimeBucketsItems.MAGMA_CUBE_BUCKET.get())
//            return ParticleTypes.FALLING_LAVA;
//
//        return null;
//    }

    @Unique
    public AbstractClientPlayer slimebuckets$player;

    @Override
    public AbstractClientPlayer slimebuckets$getPlayer() {
        return slimebuckets$player;
    }

    @Override
    public void slimebuckets$setPlayer(AbstractClientPlayer abstractClientPlayer) {
        slimebuckets$player = abstractClientPlayer;
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V",
            at = @At("TAIL"))
    public void keepPlayer(AbstractClientPlayer abstractClientPlayer, PlayerRenderState playerRenderState, float f, CallbackInfo ci) {
        slimebuckets$setPlayer(abstractClientPlayer);
    }
}
