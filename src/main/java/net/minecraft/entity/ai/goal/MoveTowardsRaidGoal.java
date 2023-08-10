package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.RaidManager;
import net.minecraft.world.server.ServerWorld;

public class MoveTowardsRaidGoal<T extends AbstractRaiderEntity> extends Goal
{
    private final T raider;

    public MoveTowardsRaidGoal(T raider)
    {
        this.raider = raider;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute()
    {
        return this.raider.getAttackTarget() == null && !this.raider.isBeingRidden() && this.raider.isRaidActive() && !this.raider.getRaid().isOver() && !((ServerWorld)this.raider.world).isVillage(this.raider.getPosition());
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return this.raider.isRaidActive() && !this.raider.getRaid().isOver() && this.raider.world instanceof ServerWorld && !((ServerWorld)this.raider.world).isVillage(this.raider.getPosition());
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick()
    {
        if (this.raider.isRaidActive())
        {
            Raid raid = this.raider.getRaid();

            if (this.raider.ticksExisted % 20 == 0)
            {
                this.func_220743_a(raid);
            }

            if (!this.raider.hasPath())
            {
                Vector3d vector3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.raider, 15, 4, Vector3d.copyCenteredHorizontally(raid.getCenter()));

                if (vector3d != null)
                {
                    this.raider.getNavigator().tryMoveToXYZ(vector3d.x, vector3d.y, vector3d.z, 1.0D);
                }
            }
        }
    }

    private void func_220743_a(Raid raid)
    {
        if (raid.isActive())
        {
            Set<AbstractRaiderEntity> set = Sets.newHashSet();
            List<AbstractRaiderEntity> list = this.raider.world.getEntitiesWithinAABB(AbstractRaiderEntity.class, this.raider.getBoundingBox().grow(16.0D), (raider) ->
            {
                return !raider.isRaidActive() && RaidManager.canJoinRaid(raider, raid);
            });
            set.addAll(list);

            for (AbstractRaiderEntity abstractraiderentity : set)
            {
                raid.joinRaid(raid.getGroupsSpawned(), abstractraiderentity, (BlockPos)null, true);
            }
        }
    }
}
