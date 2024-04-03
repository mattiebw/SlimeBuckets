package dev.mattware.slimebuckets;

import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.mattware.slimebuckets.item.SlimeBucketsItems;
import dev.mattware.slimebuckets.particle.SlimeBucketsParticles;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlimeBuckets
{
	public static final String MOD_ID = "slimebuckets";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);
	public static final RegistrySupplier<CreativeModeTab> SLIME_BUCKETS_TAB = TABS.register("slime_buckets_tab", () ->
			CreativeTabRegistry.create(Component.translatable("itemGroup." + MOD_ID + ".slime_buckets_tab"),
					() -> new ItemStack(SlimeBucketsItems.SLIME_BUCKET.get())));

	public static void init() {
		LOGGER.info("Initialising Slime Buckets ^-^");
		// Register creative tabs, items, blocks, etc.
		TABS.register();

		SlimeBucketsItems.register();
		SlimeBucketsParticles.register();
		if (Platform.getEnvironment() == Env.CLIENT)
			SlimeBucketsParticles.registerClient();
	}

	public static void clientInit() {
		EnvExecutor.runInEnv(Env.CLIENT, () -> SlimeBuckets::clientInitInternal);
	}

	private static void clientInitInternal() {
		SlimeBucketsItems.registerProperties();
	}
}
