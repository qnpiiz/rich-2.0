package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class ForgetAttackTargetTask<E extends MobEntity> extends Task<E>
{
    private final Predicate<E> field_233973_b_;
    private final Function < E, Optional <? extends LivingEntity >> field_233974_c_;

    public ForgetAttackTargetTask(Predicate<E> p_i231537_1_, Function < E, Optional <? extends LivingEntity >> p_i231537_2_)
    {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleStatus.REGISTERED));
        this.field_233973_b_ = p_i231537_1_;
        this.field_233974_c_ = p_i231537_2_;
    }

    public ForgetAttackTargetTask(Function < E, Optional <? extends LivingEntity >> p_i231536_1_)
    {
        this((p_233975_0_) ->
        {
            return true;
        }, p_i231536_1_);
    }

    protected boolean shouldExecute(ServerWorld worldIn, E owner)
    {
        if (!this.field_233973_b_.test(owner))
        {
            return false;
        }
        else
        {
            Optional <? extends LivingEntity > optional = this.field_233974_c_.apply(owner);
            return optional.isPresent() && optional.get().isAlive();
        }
    }

    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        this.field_233974_c_.apply(entityIn).ifPresent((p_233977_2_) ->
        {
            this.func_233976_a_(entityIn, p_233977_2_);
        });
    }

    private void func_233976_a_(E p_233976_1_, LivingEntity p_233976_2_)
    {
        p_233976_1_.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, p_233976_2_);
        p_233976_1_.getBrain().removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }
}
