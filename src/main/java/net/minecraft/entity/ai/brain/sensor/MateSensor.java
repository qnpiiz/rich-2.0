package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class MateSensor extends Sensor<AgeableEntity>
{
    public Set < MemoryModuleType<? >> getUsedMemories()
    {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.VISIBLE_MOBS);
    }

    protected void update(ServerWorld worldIn, AgeableEntity entityIn)
    {
        entityIn.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent((entities) ->
        {
            this.addMemory(entityIn, entities);
        });
    }

    private void addMemory(AgeableEntity entity, List<LivingEntity> entities)
    {
        Optional<AgeableEntity> optional = entities.stream().filter((livingEntity) ->
        {
            return livingEntity.getType() == entity.getType();
        }).map((livingEntity) ->
        {
            return (AgeableEntity)livingEntity;
        }).filter((ageable) ->
        {
            return !ageable.isChild();
        }).findFirst();
        entity.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT, optional);
    }
}
