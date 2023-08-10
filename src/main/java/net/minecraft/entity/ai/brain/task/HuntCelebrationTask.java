package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Random;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class HuntCelebrationTask<E extends MobEntity> extends Task<E>
{
    private final int field_233897_b_;
    private final float field_233898_c_;

    public HuntCelebrationTask(int p_i231518_1_, float p_i231518_2_)
    {
        super(ImmutableMap.of(MemoryModuleType.CELEBRATE_LOCATION, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED));
        this.field_233897_b_ = p_i231518_1_;
        this.field_233898_c_ = p_i231518_2_;
    }

    protected void startExecuting(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn)
    {
        BlockPos blockpos = func_233899_a_(entityIn);
        boolean flag = blockpos.withinDistance(entityIn.getPosition(), (double)this.field_233897_b_);

        if (!flag)
        {
            BrainUtil.setTargetPosition(entityIn, func_233900_a_(entityIn, blockpos), this.field_233898_c_, this.field_233897_b_);
        }
    }

    private static BlockPos func_233900_a_(MobEntity p_233900_0_, BlockPos p_233900_1_)
    {
        Random random = p_233900_0_.world.rand;
        return p_233900_1_.add(func_233901_a_(random), 0, func_233901_a_(random));
    }

    private static int func_233901_a_(Random p_233901_0_)
    {
        return p_233901_0_.nextInt(3) - 1;
    }

    private static BlockPos func_233899_a_(MobEntity p_233899_0_)
    {
        return p_233899_0_.getBrain().getMemory(MemoryModuleType.CELEBRATE_LOCATION).get();
    }
}
