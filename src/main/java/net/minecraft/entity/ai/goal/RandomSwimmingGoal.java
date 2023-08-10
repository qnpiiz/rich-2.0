package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class RandomSwimmingGoal extends RandomWalkingGoal
{
    public RandomSwimmingGoal(CreatureEntity creature, double speed, int chance)
    {
        super(creature, speed, chance);
    }

    @Nullable
    protected Vector3d getPosition()
    {
        Vector3d vector3d = RandomPositionGenerator.findRandomTarget(this.creature, 10, 7);

        for (int i = 0; vector3d != null && !this.creature.world.getBlockState(new BlockPos(vector3d)).allowsMovement(this.creature.world, new BlockPos(vector3d), PathType.WATER) && i++ < 10; vector3d = RandomPositionGenerator.findRandomTarget(this.creature, 10, 7))
        {
        }

        return vector3d;
    }
}
