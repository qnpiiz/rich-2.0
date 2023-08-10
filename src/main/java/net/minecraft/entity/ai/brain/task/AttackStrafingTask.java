package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

public class AttackStrafingTask<E extends MobEntity> extends Task<E>
{
    private final int distance;
    private final float speed;

    public AttackStrafingTask(int distance, float speed)
    {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT));
        this.distance = distance;
        this.speed = speed;
    }

    protected boolean shouldExecute(ServerWorld worldIn, E owner)
    {
        return this.hasSeen(owner) && this.isTargetWithinDistance(owner);
    }

    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        entityIn.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(this.getAttackTarget(entityIn), true));
        entityIn.getMoveHelper().strafe(-this.speed, 0.0F);
        entityIn.rotationYaw = MathHelper.func_219800_b(entityIn.rotationYaw, entityIn.rotationYawHead, 0.0F);
    }

    private boolean hasSeen(E mob)
    {
        return mob.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS).get().contains(this.getAttackTarget(mob));
    }

    private boolean isTargetWithinDistance(E mob)
    {
        return this.getAttackTarget(mob).isEntityInRange(mob, (double)this.distance);
    }

    private LivingEntity getAttackTarget(E mob)
    {
        return mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}
