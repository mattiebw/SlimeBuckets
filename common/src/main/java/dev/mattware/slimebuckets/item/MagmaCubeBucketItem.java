package dev.mattware.slimebuckets.item;

import dev.mattware.slimebuckets.SlimeBuckets;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class MagmaCubeBucketItem extends SlimeBucketItem {
    public MagmaCubeBucketItem(Item.Properties itemProperties) {
        super(itemProperties);
        slimeType = EntityType.MAGMA_CUBE;
        enableSlimeChunkExcitement = false;
        descriptionComponent = Component.translatable("itemdesc.slimebuckets.magma_cube_bucket").withStyle(ChatFormatting.RED);
    }

    @Override
    public void serverOnHeld(LivingEntity entity) {
        super.serverOnHeld(entity);
        if (!SlimeBuckets.SERVER_CONFIG.magmaCubeBucketHurts) return;
        if (entity.isInWaterOrRain()) return;
        if (entity instanceof Player player && player.getAbilities().invulnerable) return;
        if (entity.getRemainingFireTicks() <= 1)
                entity.setRemainingFireTicks(21);
    }
}
