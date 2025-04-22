package dev.mattware.slimebuckets.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mattware.slimebuckets.accessors.AbstractClientPlayerMixinAccess;
import dev.mattware.slimebuckets.accessors.PlayerRendererMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin<S extends ArmedEntityRenderState, M extends EntityModel<S> & ArmedModel> {
    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"))
    public void setHandPos(S armedEntityRenderState, ItemStackRenderState itemStackRenderState, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (armedEntityRenderState instanceof PlayerRenderState) {
            ItemInHandLayer layer = (ItemInHandLayer) (Object) this;
            var rend = ((RenderLayerAccessor)layer).getRenderer();
            var access = ((AbstractClientPlayerMixinAccess)((PlayerRendererMixinAccessor)rend).slimebuckets$getPlayer());
            var pos = poseStack.last().pose().getTranslation(new Vector3f());
            var cam = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition();
            pos.x += (float) cam.x;
            pos.y += (float) cam.y;
            pos.z += (float) cam.z;

            if (humanoidArm == HumanoidArm.RIGHT)
                access.slimebuckets$setLastMainHandPos(pos);
            else if (humanoidArm == HumanoidArm.LEFT)
                access.slimebuckets$setLastOffHandPos(pos);
        }
    }
}
