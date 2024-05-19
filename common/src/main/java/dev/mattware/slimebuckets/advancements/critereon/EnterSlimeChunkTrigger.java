//package dev.mattware.slimebuckets.advancements.critereon;
//
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import dev.mattware.slimebuckets.advancements.SlimeBucketsTriggers;
//import net.minecraft.advancements.Criterion;
//import net.minecraft.advancements.critereon.*;
//
//import java.util.Optional;
//
//public class EnterSlimeChunkTrigger extends SimpleCriterionTrigger<EnterSlimeChunkTrigger.TriggerInstance> {
//    public EnterSlimeChunkTrigger() {
//    }
//
//    public Codec<EnterSlimeChunkTrigger.TriggerInstance> codec() {
//        return EnterSlimeChunkTrigger.TriggerInstance.CODEC;
//    }
//
//    public static record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
//        public static final Codec<EnterSlimeChunkTrigger.TriggerInstance> CODEC =
//                RecordCodecBuilder.create((instance) -> instance.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player")
//                        .forGetter(TriggerInstance::player)).apply(instance, TriggerInstance::new));
//
//        public TriggerInstance(Optional<ContextAwarePredicate> player) {
//            this.player = player;
//        }
//
//        public static Criterion<EnterSlimeChunkTrigger.TriggerInstance> enteredSlimeChunk() {
//            return SlimeBucketsTriggers.ENTER_SLIME_CHUNK.createCriterion(new EnterSlimeChunkTrigger.TriggerInstance(Optional.empty()));
//        }
//
//        public static Criterion<EnterSlimeChunkTrigger.TriggerInstance> enteredSlimeChunk(EntityPredicate.Builder builder) {
//            return SlimeBucketsTriggers.ENTER_SLIME_CHUNK.createCriterion(new EnterSlimeChunkTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(builder))));
//        }
//
////        public void validate(CriterionValidator criterionValidator) {
////            super.validate(criterionValidator);
////        }
//
//        public Optional<ContextAwarePredicate> player() {
//            return this.player;
//        }
//    }
//}
