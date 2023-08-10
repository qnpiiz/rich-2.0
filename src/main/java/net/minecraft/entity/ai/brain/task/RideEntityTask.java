package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class RideEntityTask<E extends LivingEntity> extends Task<E>
{
    private final float field_233924_b_;

    public RideEntityTask(float p_i231524_1_)
    {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.RIDE_TARGET, MemoryModuleStatus.VALUE_PRESENT));
        this.field_233924_b_ = p_i231524_1_;
    }

    protected boolean shouldExecute(ServerWorld worldIn, E owner)
    {
        return !owner.isPassenger();
    }

    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        if (this.func_233925_a_(entityIn))
        {
            entityIn.startRiding(this.func_233926_b_(entityIn));
        }
        else
        {
            BrainUtil.setTargetEntity(entityIn, this.func_233926_b_(entityIn), this.field_233924_b_, 1);
        }
    }

    private boolean func_233925_a_(E p_233925_1_)
    {
        return this.func_233926_b_(p_233925_1_).isEntityInRange(p_233925_1_, 1.0D);
    }

    private Entity func_233926_b_(E p_233926_1_)
    {
        return p_233926_1_.getBrain().getMemory(MemoryModuleType.RIDE_TARGET).get();
    }
}
