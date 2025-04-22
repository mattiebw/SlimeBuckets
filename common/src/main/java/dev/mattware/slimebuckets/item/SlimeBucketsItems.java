package dev.mattware.slimebuckets.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.mattware.slimebuckets.SlimeBuckets;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class SlimeBucketsItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(SlimeBuckets.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> SLIME_BUCKET = registerItem("slime_bucket", SlimeBucketItem.class);
    public static final RegistrySupplier<Item> MAGMA_CUBE_BUCKET = registerItem("magma_cube_bucket", MagmaCubeBucketItem.class);

    public static void register() {
        ITEMS.register();
    }

    public static RegistrySupplier<Item> registerItem(String id, Class<? extends Item> clazz) {
        return ITEMS.register(id, () -> {
            try {
                return clazz.getConstructor(Item.Properties.class).newInstance(new Item.Properties()
                        .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(SlimeBuckets.MOD_ID, id))));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
