package dev.mattware.slimebuckets.item;

import dev.mattware.slimebuckets.SlimeBuckets;
import net.minecraft.world.item.Item;

public class SlimeBucketItem extends Item {
    public SlimeBucketItem() {
        super(new Item.Properties().arch$tab(SlimeBuckets.SLIME_BUCKETS_TAB).stacksTo(1));
    }
}
