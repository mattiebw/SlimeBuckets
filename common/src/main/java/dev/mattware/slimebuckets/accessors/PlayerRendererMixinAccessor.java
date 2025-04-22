package dev.mattware.slimebuckets.accessors;

import net.minecraft.client.player.AbstractClientPlayer;

public interface PlayerRendererMixinAccessor {
    public AbstractClientPlayer slimebuckets$getPlayer();
    void slimebuckets$setPlayer(AbstractClientPlayer abstractClientPlayer);
}
