package dev.mattware.slimebuckets.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mattware.slimebuckets.accessors.AbstractClientPlayerMixinAccess;
import dev.mattware.slimebuckets.accessors.AvatarRendererMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin<S extends ArmedEntityRenderState> {
    @Inject(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"))
    public void setHandPos(S armedEntityRenderState, ItemStackRenderState itemStackRenderState, ItemStack itemStack, HumanoidArm humanoidArm, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, CallbackInfo ci) {
        if (armedEntityRenderState instanceof ArmedEntityRenderState) {
            ItemInHandLayer<?, ?> layer = (ItemInHandLayer<?, ?>) (Object) this;
            var rend = ((RenderLayerAccessor<?, ?>)layer).getRenderer();
            if (!(rend instanceof AvatarRenderer<?>))
                return;
            var access = ((AbstractClientPlayerMixinAccess)((AvatarRendererMixinAccessor)rend).slimebuckets$getPlayer());
            var pos = poseStack.last().pose().getTranslation(new Vector3f());
            var cam = Minecraft.getInstance().getEntityRenderDispatcher().camera;
            if (cam == null)
                return;
            var camPos = cam.position();
            pos.x += (float) camPos.x;
            pos.y += (float) camPos.y;
            pos.z += (float) camPos.z;

            if (humanoidArm == HumanoidArm.RIGHT)
                access.slimebuckets$setLastMainHandPos(pos);
            else if (humanoidArm == HumanoidArm.LEFT)
                access.slimebuckets$setLastOffHandPos(pos);
        }
    }
}
