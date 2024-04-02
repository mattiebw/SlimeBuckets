package dev.mattware.slimebuckets.mixin;

import dev.mattware.slimebuckets.SlimeBuckets;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class SlimeMixin {
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    public void onInteract(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack item = player.getItemInHand(interactionHand);
        if (item.getItem() == Items.BUCKET &&
                (Mob)((Object)this) instanceof Slime slime)
        {
            Level level = slime.level();
            if (slime.getSize() == 1) { // Can only pick up the smallest slimes
                SlimeBuckets.LOGGER.info("Slime should be picked up now");
                slime.playSound(SoundEvents.BUCKET_FILL_FISH, 1.0f, 1.0f);
                slime.discard();
                cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
            } else {
                if (!level.isClientSide)
                    player.displayClientMessage(Component.literal("THIS SLIME IS TOO FUCKING FAT"), true);
                SlimeBuckets.LOGGER.info("Slime is too big :(");
                cir.setReturnValue(InteractionResult.PASS);
            }
        }

        cir.setReturnValue(InteractionResult.PASS);
    }
}
