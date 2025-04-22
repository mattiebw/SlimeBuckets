package dev.mattware.slimebuckets.item;

import dev.mattware.slimebuckets.SlimeBuckets;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SlimeBucketItem extends Item {
    protected EntityType slimeType = EntityType.SLIME;
    protected Component descriptionComponent;

    protected boolean enableSlimeChunkExcitement = true;

    public SlimeBucketItem(Item.Properties properties) {
        super(properties
                .arch$tab(SlimeBuckets.SLIME_BUCKETS_TAB)
                .stacksTo(1)
                .craftRemainder(Items.BUCKET));
        descriptionComponent = Component.translatable("itemdesc.slimebuckets.slime_bucket").withStyle(ChatFormatting.AQUA);
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return InteractionResult.PASS;
        } else if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        } else {
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos offsetBlockPos = blockPos.relative(direction);

            if (level.mayInteract(player, blockPos) && player.mayUseItemAt(offsetBlockPos, direction, itemStack)) {
                player.awardStat(Stats.ITEM_USED.get(this));
                level.playSound(player, offsetBlockPos, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0f, 1.0f);

                if (level instanceof ServerLevel serverLevel) {
                    Slime slime = (Slime) slimeType.create(serverLevel, EntitySpawnReason.BUCKET);
                    if (slime != null) {
                        var entity = itemStack.get(DataComponents.BUCKET_ENTITY_DATA);
                        if (entity != null) {
                            entity.loadInto(slime);
                        } else {
                            slime.setSize(1, true);
                        }
                        var loc = offsetBlockPos.getCenter();
                        slime.teleportTo(loc.x, loc.y, loc.z);
                        serverLevel.gameEvent(player, GameEvent.ENTITY_PLACE, slime.position());
                        serverLevel.addFreshEntity(slime);
                        player.swing(interactionHand);
                    }
                }

                if (player.getAbilities().instabuild)
                    return InteractionResult.SUCCESS;
                else
                    return InteractionResult.SUCCESS.heldItemTransformedTo(new ItemStack(Items.BUCKET));
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
        consumer.accept(descriptionComponent);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        var entity = itemStack.get(DataComponents.BUCKET_ENTITY_DATA);
        if (entity != null) {
            CompoundTag cmp = entity.copyTag();
            var name = cmp.getString("CustomName");
            if (name.isPresent()) {
                var comp = Component.Serializer.fromJsonLenient(name.get(), RegistryAccess.EMPTY);
                return Component.translatable("item.slimebuckets.slime_bucket_named", comp);
            }
        }

        return super.getName(itemStack);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel serverLevel, Entity entity, @Nullable EquipmentSlot equipmentSlot) {
        if (entity instanceof LivingEntity liver) {
            if (liver.getMainHandItem() == itemStack || liver.getOffhandItem() == itemStack)
                serverOnHeld(liver);

            if (enableSlimeChunkExcitement)
                checkSlimeChunk(itemStack, entity);
        }
    }

    public void serverOnHeld(LivingEntity entity) {
    }

    protected void checkSlimeChunk(ItemStack itemStack, Entity entity) {
        boolean currentIsInSlimeChunk = itemStack.has(SlimeBucketsItemComponents.HOLDER_IN_SLIME_CHUNK.get());

        if (enableSlimeChunkExcitement && entity instanceof ServerPlayer player) {
            // Check the config to see if slime chunk detection is enabled
            if (!SlimeBuckets.SERVER_CONFIG.enableSlimeChunkDetection) {
                // It's not, so return
                return;
            }

            // Check if we're in one and update the player
            var chunkPos = player.chunkPosition();
            final RandomSource slimeChunk = WorldgenRandom.seedSlimeChunk(
                    chunkPos.x, chunkPos.z, player.serverLevel().getSeed(), 0x3ad8025fL);
            boolean inSlimeChunk = slimeChunk.nextInt(10) == 0;
            if (inSlimeChunk)
                itemStack.set(SlimeBucketsItemComponents.HOLDER_IN_SLIME_CHUNK.get(), true);
            else if (currentIsInSlimeChunk)
                itemStack.remove(SlimeBucketsItemComponents.HOLDER_IN_SLIME_CHUNK.get());
        }
    }
}
