package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.world.server.ServerWorld;

public class WantedItemsSensor extends Sensor<MobEntity>
{
    public Set < MemoryModuleType<? >> getUsedMemories()
    {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
    }

    protected void update(ServerWorld worldIn, MobEntity entityIn)
    {
        Brain<?> brain = entityIn.getBrain();
        List<ItemEntity> list = worldIn.getEntitiesWithinAABB(ItemEntity.class, entityIn.getBoundingBox().grow(8.0D, 4.0D, 8.0D), (itemEntity) ->
        {
            return true;
        });
        list.sort(Comparator.comparingDouble(entityIn::getDistanceSq));
        Optional<ItemEntity> optional = list.stream().filter((itemEntity) ->
        {
            return entityIn.func_230293_i_(itemEntity.getItem());
        }).filter((wantedItemEntity) ->
        {
            return wantedItemEntity.isEntityInRange(entityIn, 9.0D);
        }).filter(entityIn::canEntityBeSeen).findFirst();
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, optional);
    }
}
