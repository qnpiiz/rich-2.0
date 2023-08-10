package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.server.ServerWorld;

public class AnimalBreedTask extends Task<AnimalEntity>
{
    private final EntityType <? extends AnimalEntity > breedTarget;
    private final float speed;
    private long breedTime;

    public AnimalBreedTask(EntityType <? extends AnimalEntity > breedTarget, float speed)
    {
        super(ImmutableMap.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.BREED_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED), 325);
        this.breedTarget = breedTarget;
        this.speed = speed;
    }

    protected boolean shouldExecute(ServerWorld worldIn, AnimalEntity owner)
    {
        return owner.isInLove() && this.getNearestMate(owner).isPresent();
    }

    protected void startExecuting(ServerWorld worldIn, AnimalEntity entityIn, long gameTimeIn)
    {
        AnimalEntity animalentity = this.getNearestMate(entityIn).get();
        entityIn.getBrain().setMemory(MemoryModuleType.BREED_TARGET, animalentity);
        animalentity.getBrain().setMemory(MemoryModuleType.BREED_TARGET, entityIn);
        BrainUtil.lookApproachEachOther(entityIn, animalentity, this.speed);
        int i = 275 + entityIn.getRNG().nextInt(50);
        this.breedTime = gameTimeIn + (long)i;
    }

    protected boolean shouldContinueExecuting(ServerWorld worldIn, AnimalEntity entityIn, long gameTimeIn)
    {
        if (!this.canBreed(entityIn))
        {
            return false;
        }
        else
        {
            AnimalEntity animalentity = this.getBreedTarget(entityIn);
            return animalentity.isAlive() && entityIn.canMateWith(animalentity) && BrainUtil.canSee(entityIn.getBrain(), animalentity) && gameTimeIn <= this.breedTime;
        }
    }

    protected void updateTask(ServerWorld worldIn, AnimalEntity owner, long gameTime)
    {
        AnimalEntity animalentity = this.getBreedTarget(owner);
        BrainUtil.lookApproachEachOther(owner, animalentity, this.speed);

        if (owner.isEntityInRange(animalentity, 3.0D))
        {
            if (gameTime >= this.breedTime)
            {
                owner.func_234177_a_(worldIn, animalentity);
                owner.getBrain().removeMemory(MemoryModuleType.BREED_TARGET);
                animalentity.getBrain().removeMemory(MemoryModuleType.BREED_TARGET);
            }
        }
    }

    protected void resetTask(ServerWorld worldIn, AnimalEntity entityIn, long gameTimeIn)
    {
        entityIn.getBrain().removeMemory(MemoryModuleType.BREED_TARGET);
        entityIn.getBrain().removeMemory(MemoryModuleType.WALK_TARGET);
        entityIn.getBrain().removeMemory(MemoryModuleType.LOOK_TARGET);
        this.breedTime = 0L;
    }

    private AnimalEntity getBreedTarget(AnimalEntity animal)
    {
        return (AnimalEntity)animal.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
    }

    private boolean canBreed(AnimalEntity animal)
    {
        Brain<?> brain = animal.getBrain();
        return brain.hasMemory(MemoryModuleType.BREED_TARGET) && brain.getMemory(MemoryModuleType.BREED_TARGET).get().getType() == this.breedTarget;
    }

    private Optional <? extends AnimalEntity > getNearestMate(AnimalEntity animal)
    {
        return animal.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS).get().stream().filter((livingEntity) ->
        {
            return livingEntity.getType() == this.breedTarget;
        }).map((breedableEntities) ->
        {
            return (AnimalEntity)breedableEntities;
        }).filter(animal::canMateWith).findFirst();
    }
}
