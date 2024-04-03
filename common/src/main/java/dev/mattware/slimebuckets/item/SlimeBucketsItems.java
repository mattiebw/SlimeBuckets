package dev.mattware.slimebuckets.item;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.mattware.slimebuckets.PlayerCustomData;
import dev.mattware.slimebuckets.SlimeBuckets;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
                new ResourceLocation("slime_chunk"),
                (stack, clientWorld, livingEntity, i) -> {
                    if (livingEntity instanceof Player player) {
                        // TODO: This is probably super slow, but I want to prevent the slime chunk functionality
                        // from working in NEI likes when you don't actually have the item.
                        return player.getInventory().contains(new ItemStack(SlimeBucketsItems.SLIME_BUCKET.get()))
                                && ((PlayerCustomData) player).isInSlimeChunk() ? 1 : 0;
                    } else {
                        return 0;
                    }
                });
    }
}
