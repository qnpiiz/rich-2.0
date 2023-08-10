package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;

public class NearestLivingEntitiesSensor extends Sensor<LivingEntity>
{
    protected void update(ServerWorld worldIn, LivingEntity entityIn)
    {
        AxisAlignedBB axisalignedbb = entityIn.getBoundingBox().grow(16.0D, 16.0D, 16.0D);
        List<LivingEntity> list = worldIn.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb, (livingEntity) ->
        {
            return livingEntity != entityIn && livingEntity.isAlive();
        });
        list.sort(Comparator.comparingDouble(entityIn::getDistanceSq));
        Brain<?> brain = entityIn.getBrain();
        brain.setMemory(MemoryModuleType.MOBS, list);
        brain.setMemory(MemoryModuleType.VISIBLE_MOBS, list.stream().filter((visibleEntity) ->
        {
            return canAttackTarget(entityIn, visibleEntity);
        }).collect(Collectors.toList()));
    }

    public Set < MemoryModuleType<? >> getUsedMemories()
    {
        return ImmutableSet.of(MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS);
    }
}
