package dev.mattware.slimebuckets.util;

import dev.architectury.registry.registries.RegistrySupplier;
import dev.mattware.slimebuckets.item.SlimeBucketsItems;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import net.minecraft.world.entity.monster.cubemob.MagmaCube;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.item.Item;

public class SlimeBucketsUtil {
    /// @return The item that should be used as a bucket for this cube mob, or null if it's incompatible.
    public static RegistrySupplier<Item> getBucketItemForCubeMob(AbstractCubeMob cubeMob)
    {
        if (cubeMob.getClass() == Slime.class)
            return SlimeBucketsItems.SLIME_BUCKET;
        else if (cubeMob.getClass() == MagmaCube.class)
            return SlimeBucketsItems.MAGMA_CUBE_BUCKET;
        else
            return null;
    }
}
