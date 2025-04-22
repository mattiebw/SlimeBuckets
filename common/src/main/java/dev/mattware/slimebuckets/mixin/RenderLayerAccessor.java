package dev.mattware.slimebuckets.mixin;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderLayer.class)
public interface RenderLayerAccessor<S extends EntityRenderState, M extends EntityModel<? super S>> {
    @Accessor
    public RenderLayerParent<S, M> getRenderer();
}
