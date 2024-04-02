package dev.mattware.slimebuckets.item;

import dev.mattware.slimebuckets.SlimeBuckets;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SlimeBucketItem extends Item {
    public SlimeBucketItem() {
        super(new Item.Properties().arch$tab(SlimeBuckets.SLIME_BUCKETS_TAB).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        list.add(Component.translatable("itemdesc.slimebuckets.slime_bucket").withStyle(ChatFormatting.AQUA));
    }
}
