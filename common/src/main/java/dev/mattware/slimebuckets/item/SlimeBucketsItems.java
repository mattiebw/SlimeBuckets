package dev.mattware.slimebuckets.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.mattware.slimebuckets.SlimeBuckets;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class SlimeBucketsItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(SlimeBuckets.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> EXAMPLE_ITEM = ITEMS.register("example_item",
            () -> new Item(new Item.Properties().arch$tab(SlimeBuckets.SLIME_BUCKETS_TAB)));
    public static final RegistrySupplier<Item> SLIME_BUCKET = ITEMS.register("slime_bucket",
            () -> new SlimeBucketItem(new Item.Properties().arch$tab(SlimeBuckets.SLIME_BUCKETS_TAB)));

    public static void register() {
        ITEMS.register();
    }
}
