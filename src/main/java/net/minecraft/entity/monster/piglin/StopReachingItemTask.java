package net.minecraft.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

public class StopReachingItemTask<E extends PiglinEntity> extends Task<E>
{
    private final int field_242365_b;
    private final int field_242366_c;

    public StopReachingItemTask(int p_i241918_1_, int p_i241918_2_)
    {
        super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, MemoryModuleStatus.REGISTERED, MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryModuleStatus.REGISTERED));
        this.field_242365_b = p_i241918_1_;
        this.field_242366_c = p_i241918_2_;
    }

    protected boolean shouldExecute(ServerWorld worldIn, E owner)
    {
        return owner.getHeldItemOffhand().isEmpty();
    }

    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        Brain<PiglinEntity> brain = entityIn.getBrain();
        Optional<Integer> optional = brain.getMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);

        if (!optional.isPresent())
        {
            brain.setMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, 0);
        }
        else
        {
            int i = optional.get();

            if (i > this.field_242365_b)
            {
                brain.removeMemory(MemoryModuleType.ADMIRING_ITEM);
                brain.removeMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
                brain.replaceMemory(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, true, (long)this.field_242366_c);
            }
            else
            {
                brain.setMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, i + 1);
            }
        }
    }
}
