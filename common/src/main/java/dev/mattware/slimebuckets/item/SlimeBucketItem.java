package dev.mattware.slimebuckets.item;

import com.google.gson.JsonElement;
import com.google.gson.Strictness;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.JsonOps;
import dev.mattware.slimebuckets.SlimeBuckets;
import dev.mattware.slimebuckets.item.component.SlimeBucketsItemComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.io.StringReader;
import java.util.List;
import java.util.UUID;

public class SlimeBucketItem extends Item {
    protected EntityType<?> slimeType = EntityType.SLIME;

    protected boolean enableSlimeChunkExcitement = true;

    public SlimeBucketItem(Item.Properties properties) {
        this(properties, Component.translatable("itemdesc.slimebuckets.slime_bucket").withStyle(ChatFormatting.AQUA));
    }

    public SlimeBucketItem(Item.Properties properties, Component description) {
        super(properties
                .arch$tab(SlimeBuckets.SLIME_BUCKETS_TAB)
                .stacksTo(1)
                .craftRemainder(Items.BUCKET)
                .component(DataComponents.LORE, new ItemLore(List.of(), List.of(description))));
    }

    @Override
    public @NotNull InteractionResult use(@NonNull Level level, Player player, @NonNull InteractionHand interactionHand) {
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
                            try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(slime.problemPath(), SlimeBuckets.LOGGER)) {
                                TagValueOutput tagValueOutput = TagValueOutput.createWithContext(scopedCollector, slime.registryAccess());
                                slime.saveWithoutId(tagValueOutput);
                                CompoundTag compoundTag = tagValueOutput.buildResult();
                                UUID uUID = slime.getUUID();
                                compoundTag.merge(entity.copyTag());
                                slime.load(TagValueInput.create(scopedCollector, slime.registryAccess(), compoundTag));
                                slime.setUUID(uUID);
                            }
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
    public @NonNull Component getName(ItemStack itemStack) {
        var entity = itemStack.get(DataComponents.BUCKET_ENTITY_DATA);
        if (entity != null) {
            CompoundTag cmp = entity.copyTag();
            var name = cmp.getString("CustomName");
            if (name.isPresent()) {
                JsonReader reader = new JsonReader(new StringReader(name.get()));
                reader.setStrictness(Strictness.LENIENT);
                JsonElement json = Streams.parse(reader);
                var comp = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
                return Component.translatable("item.slimebuckets.slime_bucket_named", comp);
            }
        }

        return super.getName(itemStack);
    }

    @Override
    public void inventoryTick(@NonNull ItemStack itemStack, @NonNull ServerLevel serverLevel, @NonNull Entity entity, @Nullable EquipmentSlot equipmentSlot) {
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
                // It's not, so remove the component if it exists and return.
                if (currentIsInSlimeChunk)
                    itemStack.remove(SlimeBucketsItemComponents.HOLDER_IN_SLIME_CHUNK.get());
                return;
            }

            // Check if we're in one and update the player
            var chunkPos = player.chunkPosition();
            final RandomSource slimeChunk = WorldgenRandom.seedSlimeChunk(
                    chunkPos.x, chunkPos.z, player.level().getSeed(), 0x3ad8025fL);
            boolean inSlimeChunk = slimeChunk.nextInt(10) == 0;
            if (inSlimeChunk)
                itemStack.set(SlimeBucketsItemComponents.HOLDER_IN_SLIME_CHUNK.get(), true);
            else if (currentIsInSlimeChunk)
                itemStack.remove(SlimeBucketsItemComponents.HOLDER_IN_SLIME_CHUNK.get());
        }
    }
}
