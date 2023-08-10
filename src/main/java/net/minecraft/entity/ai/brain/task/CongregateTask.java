package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class CongregateTask extends Task<LivingEntity>
{
    public CongregateTask()
    {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.MEETING_POINT, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.INTERACTION_TARGET, MemoryModuleStatus.VALUE_ABSENT));
    }

    protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner)
    {
        Brain<?> brain = owner.getBrain();
        Optional<GlobalPos> optional = brain.getMemory(MemoryModuleType.MEETING_POINT);
        return worldIn.getRandom().nextInt(100) == 0 && optional.isPresent() && worldIn.getDimensionKey() == optional.get().getDimension() && optional.get().getPos().withinDistance(owner.getPositionVec(), 4.0D) && brain.getMemory(MemoryModuleType.VISIBLE_MOBS).get().stream().anyMatch((mob) ->
        {
            return EntityType.VILLAGER.equals(mob.getType());
        });
    }

    protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn)
    {
        Brain<?> brain = entityIn.getBrain();
        brain.getMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent((visibleMobs) ->
        {
            visibleMobs.stream().filter((mob) -> {
                return EntityType.VILLAGER.equals(mob.getType());
            }).filter((villager) -> {
                return villager.getDistanceSq(entityIn) <= 32.0D;
            }).findFirst().ifPresent((villagerInDistance) -> {
                brain.setMemory(MemoryModuleType.INTERACTION_TARGET, villagerInDistance);
                brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(villagerInDistance, true));
                brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper(villagerInDistance, false), 0.3F, 1));
            });
        });
    }
}
