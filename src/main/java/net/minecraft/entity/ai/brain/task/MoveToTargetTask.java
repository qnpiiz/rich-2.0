package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class MoveToTargetTask extends Task<MobEntity>
{
    private final float speed;

    public MoveToTargetTask(float speed)
    {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.REGISTERED));
        this.speed = speed;
    }

    protected void startExecuting(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn)
    {
        LivingEntity livingentity = entityIn.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();

        if (BrainUtil.isMobVisible(entityIn, livingentity) && BrainUtil.canFireAtTarget(entityIn, livingentity, 1))
        {
            this.clearTargetMemory(entityIn);
        }
        else
        {
            this.setTargetMemory(entityIn, livingentity);
        }
    }

    private void setTargetMemory(LivingEntity p_233968_1_, LivingEntity target)
    {
        Brain brain = p_233968_1_.getBrain();
        brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(target, true));
        WalkTarget walktarget = new WalkTarget(new EntityPosWrapper(target, false), this.speed, 0);
        brain.setMemory(MemoryModuleType.WALK_TARGET, walktarget);
    }

    private void clearTargetMemory(LivingEntity p_233967_1_)
    {
        p_233967_1_.getBrain().removeMemory(MemoryModuleType.WALK_TARGET);
    }
}
