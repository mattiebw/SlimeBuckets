package dev.mattware.slimebuckets;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.architectury.utils.GameInstance;
import dev.mattware.slimebuckets.config.SlimeBucketsConfig;
import dev.mattware.slimebuckets.item.SlimeBucketsItemComponents;
import dev.mattware.slimebuckets.item.SlimeBucketsItems;
import dev.mattware.slimebuckets.network.SyncServerConfig;
import dev.mattware.slimebuckets.particle.SlimeBucketsParticles;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlimeBuckets
{
	public static final String MOD_ID = "slimebuckets";
	
	private static SlimeBucketsConfig CONFIG = new SlimeBucketsConfig();
	public static SlimeBucketsConfig.SlimeBucketsServerConfig SERVER_CONFIG;
	// Don't create CLIENT_CONFIG on the server, so we get an error if we try to use it.
	@Environment(EnvType.CLIENT)
	public static SlimeBucketsConfig.SlimeBucketsClientConfig CLIENT_CONFIG;

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);
	public static final RegistrySupplier<CreativeModeTab> SLIME_BUCKETS_TAB = TABS.register("slime_buckets_tab", () ->
			CreativeTabRegistry.create(Component.translatable("itemGroup." + MOD_ID + ".slime_buckets_tab"),
					() -> new ItemStack(SlimeBucketsItems.SLIME_BUCKET.get())));

	public static void init() {
		LOGGER.info("Initialising Slime Buckets ^-^");

		// Register config class and load it up
		AutoConfig.register(SlimeBucketsConfig.class, Toml4jConfigSerializer::new);
		var configHolder = AutoConfig.getConfigHolder(SlimeBucketsConfig.class);

		// Setup config variables
		CONFIG = configHolder.getConfig();
		if (Platform.getEnvironment() == Env.CLIENT)
			CLIENT_CONFIG = CONFIG.clientConfig;
		SERVER_CONFIG = CONFIG.serverConfig;

		// Register config sync events
		PlayerEvent.PLAYER_JOIN.register(SlimeBuckets::sendConfigToPlayer);
		configHolder.registerLoadListener((manager, newData) -> {
			resendConfigIfServer();
            return InteractionResult.PASS;
        });
		configHolder.registerSaveListener((manager, newData) -> {
			resendConfigIfServer();
			return InteractionResult.PASS;
		});

		// Register creative tabs, items, blocks, etc.
		SlimeBucketsParticles.register();
		TABS.register();
		SlimeBucketsItemComponents.register();
		SlimeBucketsItems.register();

		// Register network stuff on the server.
		if (Platform.getEnvironment() == Env.SERVER) {
			NetworkManager.registerS2CPayloadType(SyncServerConfig.TYPE, SyncServerConfig.PACKET_CODEC);
		}

		EnvExecutor.runInEnv(Env.CLIENT, () -> SlimeBuckets.Client::initClient);
	}

	private static void resendConfigIfServer() {
		LOGGER.info("Resending SlimeBuckets config to all players");
		MinecraftServer server = GameInstance.getServer();
		if (server != null) {
			NetworkManager.sendToPlayers(server.getPlayerList().getPlayers(), CONFIG.serverConfig.writeToPacket());
		}
	}

	private static void sendConfigToPlayer(ServerPlayer player) {
		NetworkManager.sendToPlayer(player, CONFIG.serverConfig.writeToPacket());
	}

	@Environment(EnvType.CLIENT)
	public static class Client
	{
		public static void initClient() {
			ClientLifecycleEvent.CLIENT_SETUP.register(client -> {
				SlimeBucketsItems.registerProperties();
			});

			ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
				if (Minecraft.getInstance().player == player) {
					// We just quit, reset config to client's server config
					SERVER_CONFIG = CONFIG.serverConfig;
				}
			});

			NetworkManager.registerReceiver(NetworkManager.Side.S2C, SyncServerConfig.TYPE, SyncServerConfig.PACKET_CODEC, (value, context) -> {
				LOGGER.info("The server has sent their config over");
				SlimeBucketsConfig newConfig = new SlimeBucketsConfig();
				newConfig.serverConfig.readFromPacket(value);
				SERVER_CONFIG = newConfig.serverConfig;
			});
		}
	}
}
