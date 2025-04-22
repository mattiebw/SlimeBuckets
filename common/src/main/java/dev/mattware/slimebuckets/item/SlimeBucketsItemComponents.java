package dev.mattware.slimebuckets.item;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.mattware.slimebuckets.SlimeBuckets;
import dev.mattware.slimebuckets.mixin.DataComponentsAccessor;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;

public class SlimeBucketsItemComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(SlimeBuckets.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static RegistrySupplier<DataComponentType<Boolean>> HOLDER_IN_SLIME_CHUNK = DATA_COMPONENT_TYPES.register("holder_in_slime_chunk",
            () ->
                DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build()
    );

    public static void register() {
        DATA_COMPONENT_TYPES.register();
    }
}
