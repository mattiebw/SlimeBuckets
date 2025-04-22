package dev.mattware.slimebuckets.item;

import dev.mattware.slimebuckets.SlimeBuckets;
import dev.mattware.slimebuckets.particle.SlimeBucketsParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SlimeBucketItem extends Item {
    protected EntityType slimeType = EntityType.SLIME;
    protected Component descriptionComponent;
    protected ParticleOptions heldParticle;

    protected boolean enableSlimeChunkExcitement = true;

    public SlimeBucketItem() {
        super(new Item.Properties().arch$tab(SlimeBuckets.SLIME_BUCKETS_TAB).stacksTo(1).craftRemainder(Items.BUCKET));
        descriptionComponent = Component.translatable("itemdesc.slimebuckets.slime_bucket").withStyle(ChatFormatting.AQUA);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemStack);
        } else if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemStack);
        } else {
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos offsetBlockPos = blockPos.relative(direction);

            if (level.mayInteract(player, blockPos) && player.mayUseItemAt(offsetBlockPos, direction, itemStack)) {
                player.awardStat(Stats.ITEM_USED.get(this));
                level.playSound(player, offsetBlockPos, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0f, 1.0f);

                if (level instanceof ServerLevel serverLevel)
                {
                    Slime slime = (Slime) slimeType.create(serverLevel);
                    if (slime != null) {
                        var entity = itemStack.get(DataComponents.BUCKET_ENTITY_DATA);
                        if (entity != null) {
                            entity.loadInto(slime);
                        } else {
                            slime.setSize(1, true);
                        }
                        slime.moveTo(offsetBlockPos, 0, 0);
                        serverLevel.gameEvent(player, GameEvent.ENTITY_PLACE, slime.position());
                        serverLevel.addFreshEntity(slime);
                        player.swing(interactionHand);
                    }
                }

                return InteractionResultHolder.sidedSuccess(!player.getAbilities().instabuild ? new ItemStack(Items.BUCKET) : itemStack, level.isClientSide);
            } else {
                return InteractionResultHolder.fail(itemStack);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);

        var entity = itemStack.get(DataComponents.BUCKET_ENTITY_DATA);
        if(entity != null) {
            CompoundTag cmp = entity.copyTag(); // TODO: This seems like a shitty unnecessary copy
            if(cmp.contains("CustomName")) {
                // If the slime has a name, set the first element (the item name) to reflect that.
                // TODO: This feels like a hack? Previously, we extended getName to achieve this, but now we need a provider
                // for Component fromJson, so we do it here instead where we can get tooltipContext.registries().
                list.set(0, Component.translatable("item.slimebuckets.slime_bucket_named",
                        Component.Serializer.fromJson(cmp.getString("CustomName"), tooltipContext.registries())));
            }
        }

        list.add(descriptionComponent);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        if (entity instanceof LivingEntity liver) {
            if (liver.getMainHandItem() == itemStack || liver.getOffhandItem() == itemStack)
                onHeld(liver);

            if (enableSlimeChunkExcitement)
                checkSlimeChunk(itemStack, entity);
        }
    }

    public void onHeld(LivingEntity entity) {
        // We need to find a better place to put this.
        if (heldParticle == null)
            heldParticle = SlimeBucketsParticles.FALLING_SLIME.get();

        if (entity.level().isClientSide && SlimeBuckets.CLIENT_CONFIG.enableTrails && entity.level().getGameTime() % 3 == 0) {
            entity.level().addParticle(heldParticle, entity.position().x, entity.position().y + 1, entity.position().z, 0, 0, 0);
        }
    }

    protected void checkSlimeChunk(ItemStack itemStack, Entity entity) {
        boolean currentIsInSlimeChunk = Boolean.TRUE.equals(itemStack.get(SlimeBucketsItemComponents.HOLDER_IN_SLIME_CHUNK.get()));

        if (enableSlimeChunkExcitement && entity instanceof ServerPlayer player) {
            // Check the config to see if slime chunk detection is enabled
            if (!SlimeBuckets.SERVER_CONFIG.enableSlimeChunkDetection) {
                // It's not, so just say we're not in one and return
                if (currentIsInSlimeChunk)
                    itemStack.set(SlimeBucketsItemComponents.HOLDER_IN_SLIME_CHUNK.get(), false);
                return;
            }

            // Check if we're in one and update the player
            var chunkPos = player.chunkPosition();
            final RandomSource slimeChunk = WorldgenRandom.seedSlimeChunk(
                    chunkPos.x, chunkPos.z, player.serverLevel().getSeed(), 0x3ad8025fL);
            boolean inSlimeChunk = slimeChunk.nextInt(10) == 0;
            if (currentIsInSlimeChunk != inSlimeChunk)
                itemStack.set(SlimeBucketsItemComponents.HOLDER_IN_SLIME_CHUNK.get(), inSlimeChunk);
        }
    }
}
