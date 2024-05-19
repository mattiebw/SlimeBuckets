package dev.mattware.slimebuckets.advancements;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.PlayerTrigger;

public class SlimeBucketsTriggers {
    public static PlayerTrigger ENTER_SLIME_CHUNK;

    public static void register() {
        ENTER_SLIME_CHUNK = CriteriaTriggers.register("slimebuckets:enter_slime_chunk", new PlayerTrigger());
    }
}
