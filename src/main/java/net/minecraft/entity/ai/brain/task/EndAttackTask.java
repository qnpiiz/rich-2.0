package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

public class EndAttackTask extends Task<LivingEntity>
{
    private final int field_233978_b_;
    private final BiPredicate<LivingEntity, LivingEntity> field_233979_c_;

    public EndAttackTask(int p_i231538_1_, BiPredicate<LivingEntity, LivingEntity> p_i231538_2_)
    {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.ANGRY_AT, MemoryModuleStatus.REGISTERED, MemoryModuleType.CELEBRATE_LOCATION, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.DANCING, MemoryModuleStatus.REGISTERED));
        this.field_233978_b_ = p_i231538_1_;
        this.field_233979_c_ = p_i231538_2_;
    }

    protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner)
    {
        return this.getAttackTarget(owner).getShouldBeDead();
    }

    protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn)
    {
        LivingEntity livingentity = this.getAttackTarget(entityIn);

        if (this.field_233979_c_.test(entityIn, livingentity))
        {
            entityIn.getBrain().replaceMemory(MemoryModuleType.DANCING, true, (long)this.field_233978_b_);
        }

        entityIn.getBrain().replaceMemory(MemoryModuleType.CELEBRATE_LOCATION, livingentity.getPosition(), (long)this.field_233978_b_);

        if (livingentity.getType() != EntityType.PLAYER || worldIn.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS))
        {
            entityIn.getBrain().removeMemory(MemoryModuleType.ATTACK_TARGET);
            entityIn.getBrain().removeMemory(MemoryModuleType.ANGRY_AT);
        }
    }

    private LivingEntity getAttackTarget(LivingEntity livingEntity)
    {
        return livingEntity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}
