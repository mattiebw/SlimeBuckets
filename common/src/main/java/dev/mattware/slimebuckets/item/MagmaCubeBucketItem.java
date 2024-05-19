package dev.mattware.slimebuckets.item;

import dev.mattware.slimebuckets.SlimeBuckets;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class MagmaCubeBucketItem extends SlimeBucketItem {
    public MagmaCubeBucketItem() {
        slimeType = EntityType.MAGMA_CUBE;
        heldParticle = ParticleTypes.FALLING_LAVA;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
        list.add(Component.translatable("itemdesc.slimebuckets.magma_cube_bucket").withStyle(ChatFormatting.RED));
    }

    @Override
    public void onHeld(LivingEntity entity) {
        super.onHeld(entity);
        if (!SlimeBuckets.SERVER_CONFIG.magmaCubeBucketHurts) return;
        if (entity.isInWaterOrRain()) return;
        if (entity instanceof Player player && player.getAbilities().invulnerable) return;
        if (entity.getRemainingFireTicks() <= 1)
                entity.setRemainingFireTicks(21);
    }
}
