package dev.mattware.slimebuckets.mixin;

import dev.mattware.slimebuckets.PlayerCustomData;
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
        var chunkPos = player.chunkPosition();
        final RandomSource slimeChunk = WorldgenRandom.seedSlimeChunk(
                chunkPos.x, chunkPos.z, player.serverLevel().getSeed(), 0x3ad8025fL);
        ((PlayerCustomData)player).setIsInSlimeChunk(slimeChunk.nextInt(10) == 0);
    }
}
