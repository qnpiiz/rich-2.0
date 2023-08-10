package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.server.ServerWorld;

public class PatrolVillageGoal extends RandomWalkingGoal
{
    public PatrolVillageGoal(CreatureEntity creature, double speed)
    {
        super(creature, speed, 240, false);
    }

    @Nullable
    protected Vector3d getPosition()
    {
        float f = this.creature.world.rand.nextFloat();

        if (this.creature.world.rand.nextFloat() < 0.3F)
        {
            return this.func_234031_j_();
        }
        else
        {
            Vector3d vector3d;

            if (f < 0.7F)
            {
                vector3d = this.func_234032_k_();

                if (vector3d == null)
                {
                    vector3d = this.func_234033_l_();
                }
            }
            else
            {
                vector3d = this.func_234033_l_();

                if (vector3d == null)
                {
                    vector3d = this.func_234032_k_();
                }
            }

            return vector3d == null ? this.func_234031_j_() : vector3d;
        }
    }

    @Nullable
    private Vector3d func_234031_j_()
    {
        return RandomPositionGenerator.getLandPos(this.creature, 10, 7);
    }

    @Nullable
    private Vector3d func_234032_k_()
    {
        ServerWorld serverworld = (ServerWorld)this.creature.world;
        List<VillagerEntity> list = serverworld.getEntitiesWithinAABB(EntityType.VILLAGER, this.creature.getBoundingBox().grow(32.0D), this::canSpawnGolems);

        if (list.isEmpty())
        {
            return null;
        }
        else
        {
            VillagerEntity villagerentity = list.get(this.creature.world.rand.nextInt(list.size()));
            Vector3d vector3d = villagerentity.getPositionVec();
            return RandomPositionGenerator.func_234133_a_(this.creature, 10, 7, vector3d);
        }
    }

    @Nullable
    private Vector3d func_234033_l_()
    {
        SectionPos sectionpos = this.func_234034_m_();

        if (sectionpos == null)
        {
            return null;
        }
        else
        {
            BlockPos blockpos = this.func_234029_a_(sectionpos);
            return blockpos == null ? null : RandomPositionGenerator.func_234133_a_(this.creature, 10, 7, Vector3d.copyCenteredHorizontally(blockpos));
        }
    }

    @Nullable
    private SectionPos func_234034_m_()
    {
        ServerWorld serverworld = (ServerWorld)this.creature.world;
        List<SectionPos> list = SectionPos.getAllInBox(SectionPos.from(this.creature), 2).filter((p_234030_1_) ->
        {
            return serverworld.sectionsToVillage(p_234030_1_) == 0;
        }).collect(Collectors.toList());
        return list.isEmpty() ? null : list.get(serverworld.rand.nextInt(list.size()));
    }

    @Nullable
    private BlockPos func_234029_a_(SectionPos p_234029_1_)
    {
        ServerWorld serverworld = (ServerWorld)this.creature.world;
        PointOfInterestManager pointofinterestmanager = serverworld.getPointOfInterestManager();
        List<BlockPos> list = pointofinterestmanager.func_219146_b((p_234027_0_) ->
        {
            return true;
        }, p_234029_1_.getCenter(), 8, PointOfInterestManager.Status.IS_OCCUPIED).map(PointOfInterest::getPos).collect(Collectors.toList());
        return list.isEmpty() ? null : list.get(serverworld.rand.nextInt(list.size()));
    }

    private boolean canSpawnGolems(VillagerEntity villager)
    {
        return villager.canSpawnGolems(this.creature.world.getGameTime());
    }
}
