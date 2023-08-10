package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class ReturnToVillageGoal extends RandomWalkingGoal
{
    public ReturnToVillageGoal(CreatureEntity creature, double speed, boolean p_i231548_4_)
    {
        super(creature, speed, 10, p_i231548_4_);
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute()
    {
        ServerWorld serverworld = (ServerWorld)this.creature.world;
        BlockPos blockpos = this.creature.getPosition();
        return serverworld.isVillage(blockpos) ? false : super.shouldExecute();
    }

    @Nullable
    protected Vector3d getPosition()
    {
        ServerWorld serverworld = (ServerWorld)this.creature.world;
        BlockPos blockpos = this.creature.getPosition();
        SectionPos sectionpos = SectionPos.from(blockpos);
        SectionPos sectionpos1 = BrainUtil.getClosestVillageSection(serverworld, sectionpos, 2);
        return sectionpos1 != sectionpos ? RandomPositionGenerator.findRandomTargetBlockTowards(this.creature, 10, 7, Vector3d.copyCenteredHorizontally(sectionpos1.getCenter())) : null;
    }
}
