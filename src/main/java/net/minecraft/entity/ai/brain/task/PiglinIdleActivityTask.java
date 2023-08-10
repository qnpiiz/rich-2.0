package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.RangedInteger;
import net.minecraft.world.server.ServerWorld;

public class PiglinIdleActivityTask<E extends MobEntity, T> extends Task<E>
{
    private final Predicate<E> field_233881_b_;
    private final MemoryModuleType <? extends T > field_233882_c_;
    private final MemoryModuleType<T> field_233883_d_;
    private final RangedInteger field_233884_e_;

    public PiglinIdleActivityTask(Predicate<E> p_i231513_1_, MemoryModuleType <? extends T > p_i231513_2_, MemoryModuleType<T> p_i231513_3_, RangedInteger p_i231513_4_)
    {
        super(ImmutableMap.of(p_i231513_2_, MemoryModuleStatus.VALUE_PRESENT, p_i231513_3_, MemoryModuleStatus.VALUE_ABSENT));
        this.field_233881_b_ = p_i231513_1_;
        this.field_233882_c_ = p_i231513_2_;
        this.field_233883_d_ = p_i231513_3_;
        this.field_233884_e_ = p_i231513_4_;
    }

    protected boolean shouldExecute(ServerWorld worldIn, E owner)
    {
        return this.field_233881_b_.test(owner);
    }

    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        Brain<?> brain = entityIn.getBrain();
        brain.replaceMemory(this.field_233883_d_, brain.getMemory(this.field_233882_c_).get(), (long)this.field_233884_e_.getRandomWithinRange(worldIn.rand));
    }
}
