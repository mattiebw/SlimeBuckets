package dev.mattware.slimebuckets.neoforge;

import dev.architectury.utils.EnvExecutor;
import dev.mattware.slimebuckets.SlimeBuckets;
import dev.mattware.slimebuckets.config.SlimeBucketsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;

@Mod(SlimeBuckets.MOD_ID)
public final class SlimebucketsNeoForge {
    public SlimebucketsNeoForge(IEventBus eventBus) {
        EnvExecutor.runInEnv(Dist.CLIENT, () -> () -> ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> new IConfigScreenFactory() {
            @Override
            public @NotNull Screen createScreen(@NotNull ModContainer modContainer, @NotNull Screen screen) {
                return AutoConfig.getConfigScreen(SlimeBucketsConfig.class, screen).get();
            }
        }));

        SlimeBuckets.init();
    }
}
