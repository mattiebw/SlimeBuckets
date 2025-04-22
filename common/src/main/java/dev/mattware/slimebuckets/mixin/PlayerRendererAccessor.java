package dev.mattware.slimebuckets.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerRenderer.class)
public interface PlayerRendererAccessor {
    @Invoker("setupRotations")
    void callSetupRotations(PlayerRenderState playerRenderState, PoseStack poseStack, float f, float g);

    @Invoker("scale")
    void callScale(PlayerRenderState playerRenderState, PoseStack poseStack);
}
