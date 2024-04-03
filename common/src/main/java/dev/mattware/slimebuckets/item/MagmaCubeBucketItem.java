package dev.mattware.slimebuckets.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagmaCubeBucketItem extends SlimeBucketItem {
    public MagmaCubeBucketItem() {
        slimeType = EntityType.MAGMA_CUBE;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("itemdesc.slimebuckets.magma_cube_bucket").withStyle(ChatFormatting.RED));
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        if (entity instanceof LivingEntity liver) {
            if (liver instanceof Player player && player.getAbilities().invulnerable) return;
            if (liver.getMainHandItem() == itemStack || liver.getOffhandItem() == itemStack) {
                if (liver.getRemainingFireTicks() <= 1)
                    liver.setRemainingFireTicks(21);
            }
        }
    }
}
