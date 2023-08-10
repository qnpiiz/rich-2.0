package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class PredicateTask<E extends LivingEntity> extends Task<E>
{
    private final Predicate<E> field_233895_b_;
    private final MemoryModuleType<?> field_233896_c_;

    public PredicateTask(Predicate<E> p_i231517_1_, MemoryModuleType<?> p_i231517_2_)
    {
        super(ImmutableMap.of(p_i231517_2_, MemoryModuleStatus.VALUE_PRESENT));
        this.field_233895_b_ = p_i231517_1_;
        this.field_233896_c_ = p_i231517_2_;
    }

    protected boolean shouldExecute(ServerWorld worldIn, E owner)
    {
        return this.field_233895_b_.test(owner);
    }

    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        entityIn.getBrain().removeMemory(this.field_233896_c_);
    }
}
