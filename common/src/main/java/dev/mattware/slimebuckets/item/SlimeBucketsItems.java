package dev.mattware.slimebuckets.item;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.mattware.slimebuckets.SlimeBuckets;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class SlimeBucketsItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(SlimeBuckets.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> SLIME_BUCKET = ITEMS.register("slime_bucket",
            SlimeBucketItem::new);
    public static final RegistrySupplier<Item> MAGMA_CUBE_BUCKET = ITEMS.register("magma_cube_bucket",
            MagmaCubeBucketItem::new);

    public static void register() {
        ITEMS.register();
    }

    public static void registerProperties() {
        ItemPropertiesRegistry.register(SLIME_BUCKET.get(),
                ResourceLocation.fromNamespaceAndPath("slimebuckets", "slime_chunk"),
                (stack, clientWorld, livingEntity, i) -> {
                    if (livingEntity instanceof Player player) {
                        boolean inSlimeChunk = Boolean.TRUE.equals(stack.get(SlimeBucketsItemComponents.HOLDER_IN_SLIME_CHUNK.get()));
                        return inSlimeChunk ? 1 : 0;
                    } else {
                        return 0;
                    }
                });
    }
}
