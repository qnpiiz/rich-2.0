package net.minecraft.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.world.server.ServerWorld;

public class AdmireItemTask<E extends PiglinEntity> extends Task<E>
{
    private final int field_234540_b_;

    public AdmireItemTask(int p_i231573_1_)
    {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.ADMIRING_ITEM, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.ADMIRING_DISABLED, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryModuleStatus.VALUE_ABSENT));
        this.field_234540_b_ = p_i231573_1_;
    }

    protected boolean shouldExecute(ServerWorld worldIn, E owner)
    {
        ItemEntity itementity = owner.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
        return PiglinTasks.func_234480_a_(itementity.getItem().getItem());
    }

    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        entityIn.getBrain().replaceMemory(MemoryModuleType.ADMIRING_ITEM, true, (long)this.field_234540_b_);
    }
}
