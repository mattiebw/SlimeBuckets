package dev.mattware.slimebuckets.mixin;

import dev.mattware.slimebuckets.SlimeBuckets;
import dev.mattware.slimebuckets.advancements.SlimeBucketsTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer)((Object)this);

        // Check the config to see if slime chunk detection is enabled
        if (!SlimeBuckets.SERVER_CONFIG.enableSlimeChunkDetection) {
            // It's not, so just say we're not in one and return
            player.slimeBuckets$setIsInSlimeChunk(false);
            return;
        }

        // Check if we're in one and update the player
        var chunkPos = player.chunkPosition();
        final RandomSource slimeChunk = WorldgenRandom.seedSlimeChunk(
                chunkPos.x, chunkPos.z, player.serverLevel().getSeed(), 0x3ad8025fL);
        boolean inSlimeChunk = slimeChunk.nextInt(10) == 0;
        player.slimeBuckets$setIsInSlimeChunk(inSlimeChunk);
        if (inSlimeChunk) {
            // Trigger "in slime chunk" advancement criterion
            SlimeBucketsTriggers.ENTER_SLIME_CHUNK.trigger(player);
        }
    }
}
