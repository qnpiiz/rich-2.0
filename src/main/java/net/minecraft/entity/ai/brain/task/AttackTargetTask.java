package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.item.ShootableItem;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;

public class AttackTargetTask extends Task<MobEntity>
{
    private final int cooldown;

    public AttackTargetTask(int cooldown)
    {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleStatus.VALUE_ABSENT));
        this.cooldown = cooldown;
    }

    protected boolean shouldExecute(ServerWorld worldIn, MobEntity owner)
    {
        LivingEntity livingentity = this.getAttackTarget(owner);
        return !this.isRanged(owner) && BrainUtil.isMobVisible(owner, livingentity) && BrainUtil.canAttackTarget(owner, livingentity);
    }

    private boolean isRanged(MobEntity mob)
    {
        return mob.func_233634_a_((item) ->
        {
            return item instanceof ShootableItem && mob.func_230280_a_((ShootableItem)item);
        });
    }

    protected void startExecuting(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn)
    {
        LivingEntity livingentity = this.getAttackTarget(entityIn);
        BrainUtil.lookAt(entityIn, livingentity);
        entityIn.swingArm(Hand.MAIN_HAND);
        entityIn.attackEntityAsMob(livingentity);
        entityIn.getBrain().replaceMemory(MemoryModuleType.ATTACK_COOLING_DOWN, true, (long)this.cooldown);
    }

    private LivingEntity getAttackTarget(MobEntity mob)
    {
        return mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}
