package dev.mattware.slimebuckets.mixin;

import dev.mattware.slimebuckets.SlimeBuckets;
import dev.mattware.slimebuckets.item.SlimeBucketsItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class SlimeMixin {
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    public void onInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if ((Mob)((Object)this) instanceof Slime slime) {
            ItemStack heldItem = player.getItemInHand(hand);
            if (heldItem.getItem() == Items.BUCKET)
            {
                Level level = slime.level();

                if (slime.getClass() == Slime.class && !SlimeBuckets.SERVER_CONFIG.slimeBucketingEnabled) {
                    if (!level.isClientSide())
                        player.sendOverlayMessage(Component.translatable("text.slimebuckets.slime_bucketing_disabled"));
                    return;
                } else if (slime.getClass() == MagmaCube.class && !SlimeBuckets.SERVER_CONFIG.magmaCubeBucketingEnabled) {
                    if (!level.isClientSide())
                        player.sendOverlayMessage(Component.translatable("text.slimebuckets.magma_cube_bucketing_disabled"));
                    return;
                }

                if (slime.getSize() <= SlimeBuckets.SERVER_CONFIG.maxBucketableSlimeSize) { // Can only pick up the smallest slimes
                    slime.playSound(SoundEvents.BUCKET_FILL_FISH, 1.0f, 1.0f);
                    ItemStack bucket = new ItemStack(slime instanceof MagmaCube ?
                            SlimeBucketsItems.MAGMA_CUBE_BUCKET.get() : SlimeBucketsItems.SLIME_BUCKET.get());
                    if (!level.isClientSide()) {
                        // Save slime data to item NBT
                        ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(slime.problemPath(), SlimeBuckets.LOGGER);
                        TagValueOutput output = TagValueOutput.createWithContext(reporter, level.registryAccess());
                        slime.saveWithoutId(output);
                        CompoundTag entityData = output.buildResult();
                        reporter.close();

                        entityData.remove("UUID"); // Remove the UUID, otherwise we'll cause an error
                        bucket.set(DataComponents.BUCKET_ENTITY_DATA, CustomData.of(entityData));

                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, bucket);
                    }
                    ItemStack newHeldResult = ItemUtils.createFilledResult(heldItem, player, bucket, false);
                    player.setItemInHand(hand, newHeldResult);
                    slime.discard();
                    cir.setReturnValue(InteractionResult.SUCCESS);
                } else {
                    if (!level.isClientSide())
                    {
                        if (SlimeBuckets.SERVER_CONFIG.maxBucketableSlimeSize == 1)
                            player.sendOverlayMessage(Component.translatable("text.slimebuckets.only_smallest_slimes"));
                        else
                            player.sendOverlayMessage(Component.translatable("text.slimebuckets.max_slime_size",
                                    String.valueOf(SlimeBuckets.SERVER_CONFIG.maxBucketableSlimeSize)));
                    }
                }
            }
        }
    }
}
