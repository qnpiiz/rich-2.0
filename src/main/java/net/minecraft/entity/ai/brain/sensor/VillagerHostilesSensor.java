package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

public class VillagerHostilesSensor extends Sensor<LivingEntity>
{
    private static final ImmutableMap < EntityType<?>, Float > enemyPresenceRange = ImmutableMap. < EntityType<?>, Float > builder().put(EntityType.DROWNED, 8.0F).put(EntityType.EVOKER, 12.0F).put(EntityType.HUSK, 8.0F).put(EntityType.ILLUSIONER, 12.0F).put(EntityType.PILLAGER, 15.0F).put(EntityType.RAVAGER, 12.0F).put(EntityType.VEX, 8.0F).put(EntityType.VINDICATOR, 10.0F).put(EntityType.ZOGLIN, 10.0F).put(EntityType.ZOMBIE, 8.0F).put(EntityType.ZOMBIE_VILLAGER, 8.0F).build();

    public Set < MemoryModuleType<? >> getUsedMemories()
    {
        return ImmutableSet.of(MemoryModuleType.NEAREST_HOSTILE);
    }

    protected void update(ServerWorld worldIn, LivingEntity entityIn)
    {
        entityIn.getBrain().setMemory(MemoryModuleType.NEAREST_HOSTILE, this.findNearestHostile(entityIn));
    }

    private Optional<LivingEntity> findNearestHostile(LivingEntity livingEntity)
    {
        return this.getVisibleEntities(livingEntity).flatMap((entities) ->
        {
            return entities.stream().filter(this::hasPresence).filter((enemy) -> {
                return this.canNoticePresence(livingEntity, enemy);
            }).min((enemy1, enemy2) -> {
                return this.compareHostileDistances(livingEntity, enemy1, enemy2);
            });
        });
    }

    private Optional<List<LivingEntity>> getVisibleEntities(LivingEntity livingEntity)
    {
        return livingEntity.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
    }

    private int compareHostileDistances(LivingEntity livingEntity, LivingEntity target1, LivingEntity target2)
    {
        return MathHelper.floor(target1.getDistanceSq(livingEntity) - target2.getDistanceSq(livingEntity));
    }

    private boolean canNoticePresence(LivingEntity livingEntity, LivingEntity target)
    {
        float f = enemyPresenceRange.get(target.getType());
        return target.getDistanceSq(livingEntity) <= (double)(f * f);
    }

    private boolean hasPresence(LivingEntity livingEntity)
    {
        return enemyPresenceRange.containsKey(livingEntity.getType());
    }
}
