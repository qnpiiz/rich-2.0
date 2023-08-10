package net.minecraft.entity.monster;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.AnimalBreedTask;
import net.minecraft.entity.ai.brain.task.AttackTargetTask;
import net.minecraft.entity.ai.brain.task.ChildFollowNearestAdultTask;
import net.minecraft.entity.ai.brain.task.DummyTask;
import net.minecraft.entity.ai.brain.task.FindNewAttackTargetTask;
import net.minecraft.entity.ai.brain.task.FirstShuffledTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.LookAtEntityTask;
import net.minecraft.entity.ai.brain.task.LookTask;
import net.minecraft.entity.ai.brain.task.MoveToTargetTask;
import net.minecraft.entity.ai.brain.task.PredicateTask;
import net.minecraft.entity.ai.brain.task.RandomlyStopAttackingTask;
import net.minecraft.entity.ai.brain.task.RunAwayTask;
import net.minecraft.entity.ai.brain.task.RunSometimesTask;
import net.minecraft.entity.ai.brain.task.SupplementedTask;
import net.minecraft.entity.ai.brain.task.WalkRandomlyTask;
import net.minecraft.entity.ai.brain.task.WalkToTargetTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsLookTargetTask;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;

public class HoglinTasks
{
    private static final RangedInteger field_234372_a_ = TickRangeConverter.convertRange(5, 20);
    private static final RangedInteger field_234373_b_ = RangedInteger.createRangedInteger(5, 16);

    protected static Brain<?> func_234376_a_(Brain<HoglinEntity> p_234376_0_)
    {
        func_234382_b_(p_234376_0_);
        func_234385_c_(p_234376_0_);
        func_234388_d_(p_234376_0_);
        func_234391_e_(p_234376_0_);
        p_234376_0_.setDefaultActivities(ImmutableSet.of(Activity.CORE));
        p_234376_0_.setFallbackActivity(Activity.IDLE);
        p_234376_0_.switchToFallbackActivity();
        return p_234376_0_;
    }

    private static void func_234382_b_(Brain<HoglinEntity> p_234382_0_)
    {
        p_234382_0_.registerActivity(Activity.CORE, 0, ImmutableList.of(new LookTask(45, 90), new WalkToTargetTask()));
    }

    private static void func_234385_c_(Brain<HoglinEntity> p_234385_0_)
    {
        p_234385_0_.registerActivity(Activity.IDLE, 10, ImmutableList. < net.minecraft.entity.ai.brain.task.Task <? super HoglinEntity >> of(new RandomlyStopAttackingTask(MemoryModuleType.NEAREST_REPELLENT, 200), new AnimalBreedTask(EntityType.HOGLIN, 0.6F), RunAwayTask.func_233963_a_(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, true), new ForgetAttackTargetTask<HoglinEntity>(HoglinTasks::func_234392_e_), new SupplementedTask<HoglinEntity>(HoglinEntity::func_234363_eJ_, RunAwayTask.func_233965_b_(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, 0.4F, 8, false)), new RunSometimesTask<LivingEntity>(new LookAtEntityTask(8.0F), RangedInteger.createRangedInteger(30, 60)), new ChildFollowNearestAdultTask(field_234373_b_, 0.6F), func_234374_a_()));
    }

    private static void func_234388_d_(Brain<HoglinEntity> p_234388_0_)
    {
        p_234388_0_.registerActivity(Activity.FIGHT, 10, ImmutableList. < net.minecraft.entity.ai.brain.task.Task <? super HoglinEntity >> of(new RandomlyStopAttackingTask(MemoryModuleType.NEAREST_REPELLENT, 200), new AnimalBreedTask(EntityType.HOGLIN, 0.6F), new MoveToTargetTask(1.0F), new SupplementedTask<>(HoglinEntity::func_234363_eJ_, new AttackTargetTask(40)), new SupplementedTask<>(AgeableEntity::isChild, new AttackTargetTask(15)), new FindNewAttackTargetTask(), new PredicateTask<>(HoglinTasks::func_234402_j_, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
    }

    private static void func_234391_e_(Brain<HoglinEntity> p_234391_0_)
    {
        p_234391_0_.registerActivity(Activity.AVOID, 10, ImmutableList. < net.minecraft.entity.ai.brain.task.Task <? super HoglinEntity >> of(RunAwayTask.func_233965_b_(MemoryModuleType.AVOID_TARGET, 1.3F, 15, false), func_234374_a_(), new RunSometimesTask<LivingEntity>(new LookAtEntityTask(8.0F), RangedInteger.createRangedInteger(30, 60)), new PredicateTask<HoglinEntity>(HoglinTasks::func_234394_f_, MemoryModuleType.AVOID_TARGET)), MemoryModuleType.AVOID_TARGET);
    }

    private static FirstShuffledTask<HoglinEntity> func_234374_a_()
    {
        return new FirstShuffledTask<>(ImmutableList.of(Pair.of(new WalkRandomlyTask(0.4F), 2), Pair.of(new WalkTowardsLookTargetTask(0.4F, 3), 2), Pair.of(new DummyTask(30, 60), 1)));
    }

    protected static void func_234377_a_(HoglinEntity p_234377_0_)
    {
        Brain<HoglinEntity> brain = p_234377_0_.getBrain();
        Activity activity = brain.getTemporaryActivity().orElse((Activity)null);
        brain.switchActivities(ImmutableList.of(Activity.FIGHT, Activity.AVOID, Activity.IDLE));
        Activity activity1 = brain.getTemporaryActivity().orElse((Activity)null);

        if (activity != activity1)
        {
            func_234398_h_(p_234377_0_).ifPresent(p_234377_0_::func_241412_a_);
        }

        p_234377_0_.setAggroed(brain.hasMemory(MemoryModuleType.ATTACK_TARGET));
    }

    protected static void func_234378_a_(HoglinEntity p_234378_0_, LivingEntity p_234378_1_)
    {
        if (!p_234378_0_.isChild())
        {
            if (p_234378_1_.getType() == EntityType.PIGLIN && func_234396_g_(p_234378_0_))
            {
                func_234393_e_(p_234378_0_, p_234378_1_);
                func_234387_c_(p_234378_0_, p_234378_1_);
            }
            else
            {
                func_234399_h_(p_234378_0_, p_234378_1_);
            }
        }
    }

    private static void func_234387_c_(HoglinEntity p_234387_0_, LivingEntity p_234387_1_)
    {
        func_234400_i_(p_234387_0_).forEach((p_234381_1_) ->
        {
            func_234390_d_(p_234381_1_, p_234387_1_);
        });
    }

    private static void func_234390_d_(HoglinEntity p_234390_0_, LivingEntity p_234390_1_)
    {
        Brain<HoglinEntity> brain = p_234390_0_.getBrain();
        LivingEntity lvt_2_1_ = BrainUtil.getNearestEntity(p_234390_0_, brain.getMemory(MemoryModuleType.AVOID_TARGET), p_234390_1_);
        lvt_2_1_ = BrainUtil.getNearestEntity(p_234390_0_, brain.getMemory(MemoryModuleType.ATTACK_TARGET), lvt_2_1_);
        func_234393_e_(p_234390_0_, lvt_2_1_);
    }

    private static void func_234393_e_(HoglinEntity p_234393_0_, LivingEntity p_234393_1_)
    {
        p_234393_0_.getBrain().removeMemory(MemoryModuleType.ATTACK_TARGET);
        p_234393_0_.getBrain().removeMemory(MemoryModuleType.WALK_TARGET);
        p_234393_0_.getBrain().replaceMemory(MemoryModuleType.AVOID_TARGET, p_234393_1_, (long)field_234372_a_.getRandomWithinRange(p_234393_0_.world.rand));
    }

    private static Optional <? extends LivingEntity > func_234392_e_(HoglinEntity p_234392_0_)
    {
        return !func_234386_c_(p_234392_0_) && !func_234402_j_(p_234392_0_) ? p_234392_0_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER) : Optional.empty();
    }

    static boolean func_234380_a_(HoglinEntity p_234380_0_, BlockPos p_234380_1_)
    {
        Optional<BlockPos> optional = p_234380_0_.getBrain().getMemory(MemoryModuleType.NEAREST_REPELLENT);
        return optional.isPresent() && optional.get().withinDistance(p_234380_1_, 8.0D);
    }

    private static boolean func_234394_f_(HoglinEntity p_234394_0_)
    {
        return p_234394_0_.func_234363_eJ_() && !func_234396_g_(p_234394_0_);
    }

    private static boolean func_234396_g_(HoglinEntity p_234396_0_)
    {
        if (p_234396_0_.isChild())
        {
            return false;
        }
        else
        {
            int i = p_234396_0_.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0);
            int j = p_234396_0_.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0) + 1;
            return i > j;
        }
    }

    protected static void func_234384_b_(HoglinEntity p_234384_0_, LivingEntity p_234384_1_)
    {
        Brain<HoglinEntity> brain = p_234384_0_.getBrain();
        brain.removeMemory(MemoryModuleType.PACIFIED);
        brain.removeMemory(MemoryModuleType.BREED_TARGET);

        if (p_234384_0_.isChild())
        {
            func_234390_d_(p_234384_0_, p_234384_1_);
        }
        else
        {
            func_234395_f_(p_234384_0_, p_234384_1_);
        }
    }

    private static void func_234395_f_(HoglinEntity p_234395_0_, LivingEntity p_234395_1_)
    {
        if (!p_234395_0_.getBrain().hasActivity(Activity.AVOID) || p_234395_1_.getType() != EntityType.PIGLIN)
        {
            if (EntityPredicates.CAN_HOSTILE_AI_TARGET.test(p_234395_1_))
            {
                if (p_234395_1_.getType() != EntityType.HOGLIN)
                {
                    if (!BrainUtil.isTargetWithinDistance(p_234395_0_, p_234395_1_, 4.0D))
                    {
                        func_234397_g_(p_234395_0_, p_234395_1_);
                        func_234399_h_(p_234395_0_, p_234395_1_);
                    }
                }
            }
        }
    }

    private static void func_234397_g_(HoglinEntity p_234397_0_, LivingEntity p_234397_1_)
    {
        Brain<HoglinEntity> brain = p_234397_0_.getBrain();
        brain.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        brain.removeMemory(MemoryModuleType.BREED_TARGET);
        brain.replaceMemory(MemoryModuleType.ATTACK_TARGET, p_234397_1_, 200L);
    }

    private static void func_234399_h_(HoglinEntity p_234399_0_, LivingEntity p_234399_1_)
    {
        func_234400_i_(p_234399_0_).forEach((p_234375_1_) ->
        {
            func_234401_i_(p_234375_1_, p_234399_1_);
        });
    }

    private static void func_234401_i_(HoglinEntity p_234401_0_, LivingEntity p_234401_1_)
    {
        if (!func_234386_c_(p_234401_0_))
        {
            Optional<LivingEntity> optional = p_234401_0_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
            LivingEntity livingentity = BrainUtil.getNearestEntity(p_234401_0_, optional, p_234401_1_);
            func_234397_g_(p_234401_0_, livingentity);
        }
    }

    public static Optional<SoundEvent> func_234398_h_(HoglinEntity p_234398_0_)
    {
        return p_234398_0_.getBrain().getTemporaryActivity().map((p_234379_1_) ->
        {
            return func_241413_a_(p_234398_0_, p_234379_1_);
        });
    }

    private static SoundEvent func_241413_a_(HoglinEntity p_241413_0_, Activity p_241413_1_)
    {
        if (p_241413_1_ != Activity.AVOID && !p_241413_0_.func_234364_eK_())
        {
            if (p_241413_1_ == Activity.FIGHT)
            {
                return SoundEvents.ENTITY_HOGLIN_ANGRY;
            }
            else
            {
                return func_241416_h_(p_241413_0_) ? SoundEvents.ENTITY_HOGLIN_RETREAT : SoundEvents.ENTITY_HOGLIN_AMBIENT;
            }
        }
        else
        {
            return SoundEvents.ENTITY_HOGLIN_RETREAT;
        }
    }

    private static List<HoglinEntity> func_234400_i_(HoglinEntity p_234400_0_)
    {
        return p_234400_0_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS).orElse(ImmutableList.of());
    }

    private static boolean func_241416_h_(HoglinEntity p_241416_0_)
    {
        return p_241416_0_.getBrain().hasMemory(MemoryModuleType.NEAREST_REPELLENT);
    }

    private static boolean func_234402_j_(HoglinEntity p_234402_0_)
    {
        return p_234402_0_.getBrain().hasMemory(MemoryModuleType.BREED_TARGET);
    }

    protected static boolean func_234386_c_(HoglinEntity p_234386_0_)
    {
        return p_234386_0_.getBrain().hasMemory(MemoryModuleType.PACIFIED);
    }
}
