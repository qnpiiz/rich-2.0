package net.minecraft.util.math;

import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.vector.Vector3d;

public class EntityPosWrapper implements IPosWrapper
{
    private final Entity entity;
    private final boolean eyePos;

    public EntityPosWrapper(Entity entity, boolean eyePos)
    {
        this.entity = entity;
        this.eyePos = eyePos;
    }

    public Vector3d getPos()
    {
        return this.eyePos ? this.entity.getPositionVec().add(0.0D, (double)this.entity.getEyeHeight(), 0.0D) : this.entity.getPositionVec();
    }

    public BlockPos getBlockPos()
    {
        return this.entity.getPosition();
    }

    public boolean isVisibleTo(LivingEntity entity)
    {
        if (!(this.entity instanceof LivingEntity))
        {
            return true;
        }
        else
        {
            Optional<List<LivingEntity>> optional = entity.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
            return this.entity.isAlive() && optional.isPresent() && optional.get().contains(this.entity);
        }
    }

    public String toString()
    {
        return "EntityTracker for " + this.entity;
    }
}
