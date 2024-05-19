package dev.mattware.slimebuckets.item;

import com.mojang.serialization.Codec;
import dev.mattware.slimebuckets.mixin.DataComponentsAccessor;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;

public class SlimeBucketsItemComponents {
    public static DataComponentType<Boolean> HOLDER_IN_SLIME_CHUNK;

    public static void registerComponents() {
        HOLDER_IN_SLIME_CHUNK = DataComponentsAccessor.register("slimebuckets:holder_in_slime_chunk", (builder) ->
                builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    }
}
