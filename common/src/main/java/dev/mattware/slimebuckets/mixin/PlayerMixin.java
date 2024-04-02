package dev.mattware.slimebuckets.mixin;

import dev.mattware.slimebuckets.PlayerCustomData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerCustomData {
    @Unique
    private static final EntityDataAccessor<Boolean> DATA_PLAYER_IN_SLIME_CHUNK;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void addSyncedData(CallbackInfo ci) {
        this.entityData.define(DATA_PLAYER_IN_SLIME_CHUNK, false);
    }

    @Override
    public boolean isInSlimeChunk() {
        return this.entityData.get(DATA_PLAYER_IN_SLIME_CHUNK);
    }

    @Override
    public void setIsInSlimeChunk(boolean newValue) {
        this.entityData.set(DATA_PLAYER_IN_SLIME_CHUNK, newValue);
    }

    static {
        DATA_PLAYER_IN_SLIME_CHUNK = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
    }
}
