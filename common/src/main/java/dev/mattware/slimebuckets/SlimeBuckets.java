package dev.mattware.slimebuckets;

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
import dev.mattware.slimebuckets.item.SlimeBucketsItems;
import dev.mattware.slimebuckets.particle.SlimeBucketsParticles;
import io.netty.buffer.Unpooled;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlimeBuckets
{
	public static final String MOD_ID = "slimebuckets";
	public static final short MOD_VERSION = 1;

	private static SlimeBucketsConfig CONFIG = new SlimeBucketsConfig();
	public static SlimeBucketsConfig.SlimeBucketsServerConfig SERVER_CONFIG;
	// Don't create CLIENT_CONFIG on the server, so we get an error if we try to use it.
	@Environment(EnvType.CLIENT)
	public static SlimeBucketsConfig.SlimeBucketsClientConfig CLIENT_CONFIG;

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ResourceLocation SYNC_SERVER_CONFIG_PACKET = new ResourceLocation("slimebuckets", "sync_server_config");

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
		TABS.register();
		SlimeBucketsItems.register();
		SlimeBucketsParticles.register();
		if (Platform.getEnvironment() == Env.CLIENT)
			SlimeBucketsParticles.registerClient();
	}

	public static void clientInit() {
		EnvExecutor.runInEnv(Env.CLIENT, () -> SlimeBuckets::clientInitInternal);
	}

	private static void resendConfigIfServer() {
		LOGGER.info("Resending SlimeBuckets config to all players");
		MinecraftServer server = GameInstance.getServer();
		if (server != null) {
			FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
			writeServerConfigPacket(buf);
			NetworkManager.sendToPlayers(server.getPlayerList().getPlayers(), SYNC_SERVER_CONFIG_PACKET, buf);
		}
	}

	private static void sendConfigToPlayer(ServerPlayer player) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		writeServerConfigPacket(buf);
		NetworkManager.sendToPlayer(player, SYNC_SERVER_CONFIG_PACKET, buf);
	}

	private static void writeServerConfigPacket(FriendlyByteBuf buf) {
		buf.writeShort(MOD_VERSION);
		CONFIG.serverConfig.writeToBuf(buf);
	}

	private static void clientInitInternal() {
		SlimeBucketsItems.registerProperties();
		ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
			if (Minecraft.getInstance().player == player) {
				// We just quit, reset config to client's server config
				SERVER_CONFIG = CONFIG.serverConfig;
			}
		});

		// Config synchronisation
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, SYNC_SERVER_CONFIG_PACKET, (buf, context) -> {
			// We've received the servers config
			LOGGER.info("The server has sent their config over");
			short serverVersion = buf.readShort();
			if (serverVersion != MOD_VERSION) {
				LOGGER.error("Server version " + serverVersion + " doesn't match client version " + MOD_VERSION + "!");
			}
			SlimeBucketsConfig newConfig = new SlimeBucketsConfig();
			newConfig.serverConfig.readFromBuf(buf);
			SERVER_CONFIG = newConfig.serverConfig;
		});
	}
}
