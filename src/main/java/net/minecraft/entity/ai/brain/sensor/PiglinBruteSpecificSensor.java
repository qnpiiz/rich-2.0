package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.world.server.ServerWorld;

public class PiglinBruteSpecificSensor extends Sensor<LivingEntity>
{
    public Set < MemoryModuleType<? >> getUsedMemories()
    {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEAREST_ADULT_PIGLINS);
    }

    protected void update(ServerWorld worldIn, LivingEntity entityIn)
    {
        Brain<?> brain = entityIn.getBrain();
        Optional<MobEntity> optional = Optional.empty();
        List<AbstractPiglinEntity> list = Lists.newArrayList();

        for (LivingEntity livingentity : brain.getMemory(MemoryModuleType.VISIBLE_MOBS).orElse(ImmutableList.of()))
        {
            if (livingentity instanceof WitherSkeletonEntity || livingentity instanceof WitherEntity)
            {
                optional = Optional.of((MobEntity)livingentity);
                break;
            }
        }

        for (LivingEntity livingentity1 : brain.getMemory(MemoryModuleType.MOBS).orElse(ImmutableList.of()))
        {
            if (livingentity1 instanceof AbstractPiglinEntity && ((AbstractPiglinEntity)livingentity1).func_242337_eM())
            {
                list.add((AbstractPiglinEntity)livingentity1);
            }
        }

        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional);
        brain.setMemory(MemoryModuleType.NEAREST_ADULT_PIGLINS, list);
    }
}
