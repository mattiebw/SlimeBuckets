package dev.mattware.slimebuckets.accessors;

import net.minecraft.client.player.AbstractClientPlayer;

public interface AvatarRendererMixinAccessor {
    AbstractClientPlayer slimebuckets$getPlayer();
    void slimebuckets$setPlayer(AbstractClientPlayer abstractClientPlayer);
}
