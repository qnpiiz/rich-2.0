package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class FindInteractionAndLookTargetTask extends Task<LivingEntity>
{
    private final EntityType<?> targetType;
    private final int field_220534_b;
    private final Predicate<LivingEntity> field_220535_c;
    private final Predicate<LivingEntity> field_220536_d;

    public FindInteractionAndLookTargetTask(EntityType<?> p_i50347_1_, int distance, Predicate<LivingEntity> p_i50347_3_, Predicate<LivingEntity> p_i50347_4_)
    {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT));
        this.targetType = p_i50347_1_;
        this.field_220534_b = distance * distance;
        this.field_220535_c = p_i50347_4_;
        this.field_220536_d = p_i50347_3_;
    }

    public FindInteractionAndLookTargetTask(EntityType<?> p_i50348_1_, int distance)
    {
        this(p_i50348_1_, distance, (p_220528_0_) ->
        {
            return true;
        }, (p_220531_0_) ->
        {
            return true;
        });
    }

    public boolean shouldExecute(ServerWorld worldIn, LivingEntity owner)
    {
        return this.field_220536_d.test(owner) && this.getVisibleMobs(owner).stream().anyMatch(this::isNearInteractableEntity);
    }

    public void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn)
    {
        super.startExecuting(worldIn, entityIn, gameTimeIn);
        Brain<?> brain = entityIn.getBrain();
        brain.getMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent((visibleMobs) ->
        {
            visibleMobs.stream().filter((mob) -> {
                return mob.getDistanceSq(entityIn) <= (double)this.field_220534_b;
            }).filter(this::isNearInteractableEntity).findFirst().ifPresent((p_220527_1_) -> {
                brain.setMemory(MemoryModuleType.INTERACTION_TARGET, p_220527_1_);
                brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(p_220527_1_, true));
            });
        });
    }

    private boolean isNearInteractableEntity(LivingEntity livingEntity)
    {
        return this.targetType.equals(livingEntity.getType()) && this.field_220535_c.test(livingEntity);
    }

    private List<LivingEntity> getVisibleMobs(LivingEntity livingEntity)
    {
        return livingEntity.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS).get();
    }
}
