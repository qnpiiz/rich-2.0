package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class WaterAvoidingRandomFlyingGoal extends WaterAvoidingRandomWalkingGoal
{
    public WaterAvoidingRandomFlyingGoal(CreatureEntity creature, double speed)
    {
        super(creature, speed);
    }

    @Nullable
    protected Vector3d getPosition()
    {
        Vector3d vector3d = null;

        if (this.creature.isInWater())
        {
            vector3d = RandomPositionGenerator.getLandPos(this.creature, 15, 15);
        }

        if (this.creature.getRNG().nextFloat() >= this.probability)
        {
            vector3d = this.getTreePos();
        }

        return vector3d == null ? super.getPosition() : vector3d;
    }

    @Nullable
    private Vector3d getTreePos()
    {
        BlockPos blockpos = this.creature.getPosition();
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();

        for (BlockPos blockpos1 : BlockPos.getAllInBoxMutable(MathHelper.floor(this.creature.getPosX() - 3.0D), MathHelper.floor(this.creature.getPosY() - 6.0D), MathHelper.floor(this.creature.getPosZ() - 3.0D), MathHelper.floor(this.creature.getPosX() + 3.0D), MathHelper.floor(this.creature.getPosY() + 6.0D), MathHelper.floor(this.creature.getPosZ() + 3.0D)))
        {
            if (!blockpos.equals(blockpos1))
            {
                Block block = this.creature.world.getBlockState(blockpos$mutable1.setAndMove(blockpos1, Direction.DOWN)).getBlock();
                boolean flag = block instanceof LeavesBlock || block.isIn(BlockTags.LOGS);

                if (flag && this.creature.world.isAirBlock(blockpos1) && this.creature.world.isAirBlock(blockpos$mutable.setAndMove(blockpos1, Direction.UP)))
                {
                    return Vector3d.copyCenteredHorizontally(blockpos1);
                }
            }
        }

        return null;
    }
}
