package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.server.ServerWorld;

public class FindNewAttackTargetTask<E extends MobEntity> extends Task<E>
{
    private final Predicate<LivingEntity> field_233981_b_;

    public FindNewAttackTargetTask(Predicate<LivingEntity> p_i231539_1_)
    {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleStatus.REGISTERED));
        this.field_233981_b_ = p_i231539_1_;
    }

    public FindNewAttackTargetTask()
    {
        this((p_233984_0_) ->
        {
            return false;
        });
    }

    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        if (func_233982_a_(entityIn))
        {
            this.func_233987_d_(entityIn);
        }
        else if (this.func_233986_c_(entityIn))
        {
            this.func_233987_d_(entityIn);
        }
        else if (this.func_233983_a_(entityIn))
        {
            this.func_233987_d_(entityIn);
        }
        else if (!EntityPredicates.CAN_HOSTILE_AI_TARGET.test(this.func_233985_b_(entityIn)))
        {
            this.func_233987_d_(entityIn);
        }
        else if (this.field_233981_b_.test(this.func_233985_b_(entityIn)))
        {
            this.func_233987_d_(entityIn);
        }
    }

    private boolean func_233983_a_(E p_233983_1_)
    {
        return this.func_233985_b_(p_233983_1_).world != p_233983_1_.world;
    }

    private LivingEntity func_233985_b_(E p_233985_1_)
    {
        return p_233985_1_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    private static <E extends LivingEntity> boolean func_233982_a_(E p_233982_0_)
    {
        Optional<Long> optional = p_233982_0_.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        return optional.isPresent() && p_233982_0_.world.getGameTime() - optional.get() > 200L;
    }

    private boolean func_233986_c_(E p_233986_1_)
    {
        Optional<LivingEntity> optional = p_233986_1_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        return optional.isPresent() && !optional.get().isAlive();
    }

    private void func_233987_d_(E p_233987_1_)
    {
        p_233987_1_.getBrain().removeMemory(MemoryModuleType.ATTACK_TARGET);
    }
}
