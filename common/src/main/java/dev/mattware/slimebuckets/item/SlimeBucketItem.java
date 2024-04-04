package dev.mattware.slimebuckets.item;

import dev.mattware.slimebuckets.SlimeBuckets;
import dev.mattware.slimebuckets.config.SlimeBucketsConfig;
import dev.mattware.slimebuckets.particle.SlimeBucketsParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SlimeBucketItem extends Item {
    protected EntityType slimeType = EntityType.SLIME;
    protected ParticleOptions heldParticle;

    public static final String TAG_ENTITY_DATA = "slime_nbt";

    public SlimeBucketItem() {
        super(new Item.Properties().arch$tab(SlimeBuckets.SLIME_BUCKETS_TAB).stacksTo(1).craftRemainder(Items.BUCKET));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
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
                        if (itemStack.hasTag()) {
                            var entityData = itemStack.getTag().getCompound(TAG_ENTITY_DATA);
                            if (entityData != null)
                                slime.load(entityData);
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
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        list.add(Component.translatable("itemdesc.slimebuckets.slime_bucket").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        if(stack.hasTag()) {
            CompoundTag cmp = stack.getTag().getCompound(TAG_ENTITY_DATA);
            if(cmp != null && cmp.contains("CustomName")) {
                return Component.translatable("item.slimebuckets.slime_bucket_named", Component.Serializer.fromJson(cmp.getString("CustomName")));
            }
        }

        return super.getName(stack);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        if (entity instanceof LivingEntity liver) {
            if (liver.getMainHandItem() == itemStack || liver.getOffhandItem() == itemStack)
                onHeld(liver);
        }
    }

    public void onHeld(LivingEntity entity) {
        if (entity.level().isClientSide && SlimeBuckets.CLIENT_CONFIG.enableTrails && entity.level().getGameTime() % 3 == 0) {
            if (heldParticle == null)
                heldParticle = SlimeBucketsParticles.FALLING_SLIME.get();
            entity.level().addParticle(heldParticle, entity.position().x, entity.position().y + 1, entity.position().z, 0, 0, 0);
        }
    }
}
