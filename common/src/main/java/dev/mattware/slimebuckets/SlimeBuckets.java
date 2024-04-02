package dev.mattware.slimebuckets;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.mattware.slimebuckets.item.SlimeBucketsItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class SlimeBuckets
{
	public static final String MOD_ID = "slimebuckets";

	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);
	public static final RegistrySupplier<CreativeModeTab> SLIME_BUCKETS_TAB = TABS.register("slime_buckets_tab", () ->
			CreativeTabRegistry.create(Component.translatable("itemGroup." + MOD_ID + ".slime_buckets_tab"),
					() -> new ItemStack(SlimeBucketsItems.EXAMPLE_ITEM.get())));

	public static void init() {
		// Register creative tabs, items, blocks, etc.
		TABS.register();
		SlimeBucketsItems.register();
	}
}
