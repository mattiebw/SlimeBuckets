package dev.mattware.slimebuckets.mixin;

import dev.mattware.slimebuckets.accessors.AvatarRendererMixinAccessor;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin implements AvatarRendererMixinAccessor {
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

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
            at = @At("TAIL"))
    public void keepPlayer(Avatar entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        if (entity instanceof AbstractClientPlayer acp)
            slimebuckets$setPlayer(acp);
    }
}
