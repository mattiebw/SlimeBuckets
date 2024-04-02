package dev.mattware.slimebuckets.item;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.mattware.slimebuckets.SlimeBuckets;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class SlimeBucketsItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(SlimeBuckets.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> SLIME_BUCKET = ITEMS.register("slime_bucket",
            SlimeBucketItem::new);

    public static void register() {
        ITEMS.register();
        EnvExecutor.runInEnv(Env.CLIENT, () -> SlimeBucketsItems::registerProperties);
    }

    private static void registerProperties() {
        ItemPropertiesRegistry.register(SLIME_BUCKET.get(),
                new ResourceLocation("slime_chunk"),
                (stack, clientWorld, livingEntity, i) -> (livingEntity != null && (int)livingEntity.position().x % 3 == 0) ? 1 : 0);
    }
}
