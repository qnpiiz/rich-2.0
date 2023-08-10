package net.minecraft.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.world.server.ServerWorld;

public class StartHuntTask<E extends PiglinEntity> extends Task<E>
{
    public StartHuntTask()
    {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.ANGRY_AT, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.HUNTED_RECENTLY, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleStatus.REGISTERED));
    }

    protected boolean shouldExecute(ServerWorld worldIn, PiglinEntity owner)
    {
        return !owner.isChild() && !PiglinTasks.func_234508_e_(owner);
    }

    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        HoglinEntity hoglinentity = entityIn.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN).get();
        PiglinTasks.func_234497_c_(entityIn, hoglinentity);
        PiglinTasks.func_234518_h_(entityIn);
        PiglinTasks.func_234487_b_(entityIn, hoglinentity);
        PiglinTasks.func_234512_f_(entityIn);
    }
}
