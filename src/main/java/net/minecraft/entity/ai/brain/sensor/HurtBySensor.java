package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;

public class HurtBySensor extends Sensor<LivingEntity>
{
    public Set < MemoryModuleType<? >> getUsedMemories()
    {
        return ImmutableSet.of(MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY);
    }

    protected void update(ServerWorld worldIn, LivingEntity entityIn)
    {
        Brain<?> brain = entityIn.getBrain();
        DamageSource damagesource = entityIn.getLastDamageSource();

        if (damagesource != null)
        {
            brain.setMemory(MemoryModuleType.HURT_BY, entityIn.getLastDamageSource());
            Entity entity = damagesource.getTrueSource();

            if (entity instanceof LivingEntity)
            {
                brain.setMemory(MemoryModuleType.HURT_BY_ENTITY, (LivingEntity)entity);
            }
        }
        else
        {
            brain.removeMemory(MemoryModuleType.HURT_BY);
        }

        brain.getMemory(MemoryModuleType.HURT_BY_ENTITY).ifPresent((livingEntity) ->
        {
            if (!livingEntity.isAlive() || livingEntity.world != worldIn)
            {
                brain.removeMemory(MemoryModuleType.HURT_BY_ENTITY);
            }
        });
    }
}
