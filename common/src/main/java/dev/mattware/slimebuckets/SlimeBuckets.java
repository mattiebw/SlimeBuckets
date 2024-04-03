package dev.mattware.slimebuckets;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.mattware.slimebuckets.config.SlimeBucketsConfig;
import dev.mattware.slimebuckets.item.SlimeBucketsItems;
import dev.mattware.slimebuckets.particle.SlimeBucketsParticles;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlimeBuckets
{
	public static final String MOD_ID = "slimebuckets";

	public static SlimeBucketsConfig CONFIG = new SlimeBucketsConfig();
	public static SlimeBucketsConfig LOCAL_CONFIG = new SlimeBucketsConfig();

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);
	public static final RegistrySupplier<CreativeModeTab> SLIME_BUCKETS_TAB = TABS.register("slime_buckets_tab", () ->
			CreativeTabRegistry.create(Component.translatable("itemGroup." + MOD_ID + ".slime_buckets_tab"),
					() -> new ItemStack(SlimeBucketsItems.SLIME_BUCKET.get())));

	public static void init() {
		LOGGER.info("Initialising Slime Buckets ^-^");

		AutoConfig.register(SlimeBucketsConfig.class, Toml4jConfigSerializer::new);
		LOCAL_CONFIG = AutoConfig.getConfigHolder(SlimeBucketsConfig.class).getConfig();
		CONFIG = LOCAL_CONFIG;

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
