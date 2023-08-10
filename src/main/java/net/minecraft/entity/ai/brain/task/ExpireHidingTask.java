package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class ExpireHidingTask extends Task<LivingEntity>
{
    private final int hidingDistance;
    private final int field_220538_b;
    private int hidingDuration;

    public ExpireHidingTask(int p_i50349_1_, int hidingDistance)
    {
        super(ImmutableMap.of(MemoryModuleType.HIDING_PLACE, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleStatus.VALUE_PRESENT));
        this.field_220538_b = p_i50349_1_ * 20;
        this.hidingDuration = 0;
        this.hidingDistance = hidingDistance;
    }

    protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn)
    {
        Brain<?> brain = entityIn.getBrain();
        Optional<Long> optional = brain.getMemory(MemoryModuleType.HEARD_BELL_TIME);
        boolean flag = optional.get() + 300L <= gameTimeIn;

        if (this.hidingDuration <= this.field_220538_b && !flag)
        {
            BlockPos blockpos = brain.getMemory(MemoryModuleType.HIDING_PLACE).get().getPos();

            if (blockpos.withinDistance(entityIn.getPosition(), (double)this.hidingDistance))
            {
                ++this.hidingDuration;
            }
        }
        else
        {
            brain.removeMemory(MemoryModuleType.HEARD_BELL_TIME);
            brain.removeMemory(MemoryModuleType.HIDING_PLACE);
            brain.updateActivity(worldIn.getDayTime(), worldIn.getGameTime());
            this.hidingDuration = 0;
        }
    }
}
