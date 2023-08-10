package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class GolemLastSeenSensor extends Sensor<LivingEntity>
{
    public GolemLastSeenSensor()
    {
        this(200);
    }

    public GolemLastSeenSensor(int interval)
    {
        super(interval);
    }

    protected void update(ServerWorld worldIn, LivingEntity entityIn)
    {
        update(entityIn);
    }

    public Set < MemoryModuleType<? >> getUsedMemories()
    {
        return ImmutableSet.of(MemoryModuleType.MOBS);
    }

    public static void update(LivingEntity livingEntity)
    {
        Optional<List<LivingEntity>> optional = livingEntity.getBrain().getMemory(MemoryModuleType.MOBS);

        if (optional.isPresent())
        {
            boolean flag = optional.get().stream().anyMatch((entity) ->
            {
                return entity.getType().equals(EntityType.IRON_GOLEM);
            });

            if (flag)
            {
                reset(livingEntity);
            }
        }
    }

    public static void reset(LivingEntity livingEntity)
    {
        livingEntity.getBrain().replaceMemory(MemoryModuleType.GOLEM_DETECTED_RECENTLY, true, 600L);
    }
}
