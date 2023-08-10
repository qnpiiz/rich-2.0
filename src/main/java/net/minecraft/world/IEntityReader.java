package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public interface IEntityReader
{
    List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate <? super Entity > predicate);

    <T extends Entity> List<T> getEntitiesWithinAABB(Class <? extends T > clazz, AxisAlignedBB aabb, @Nullable Predicate <? super T > filter);

default <T extends Entity> List<T> getLoadedEntitiesWithinAABB(Class<? extends T> p_225316_1_, AxisAlignedBB p_225316_2_, @Nullable Predicate<? super T> p_225316_3_)
    {
        return this.getEntitiesWithinAABB(p_225316_1_, p_225316_2_, p_225316_3_);
    }

    List <? extends PlayerEntity > getPlayers();

default List<Entity> getEntitiesWithinAABBExcludingEntity(@Nullable Entity entityIn, AxisAlignedBB bb)
    {
        return this.getEntitiesInAABBexcluding(entityIn, bb, EntityPredicates.NOT_SPECTATING);
    }

default boolean checkNoEntityCollision(@Nullable Entity entityIn, VoxelShape shape)
    {
        if (shape.isEmpty())
        {
            return true;
        }
        else
        {
            for (Entity entity : this.getEntitiesWithinAABBExcludingEntity(entityIn, shape.getBoundingBox()))
            {
                if (!entity.removed && entity.preventEntitySpawning && (entityIn == null || !entity.isRidingSameEntity(entityIn)) && VoxelShapes.compare(shape, VoxelShapes.create(entity.getBoundingBox()), IBooleanFunction.AND))
                {
                    return false;
                }
            }

            return true;
        }
    }

default <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> p_217357_1_, AxisAlignedBB p_217357_2_)
    {
        return this.getEntitiesWithinAABB(p_217357_1_, p_217357_2_, EntityPredicates.NOT_SPECTATING);
    }

default <T extends Entity> List<T> getLoadedEntitiesWithinAABB(Class<? extends T> p_225317_1_, AxisAlignedBB p_225317_2_)
    {
        return this.getLoadedEntitiesWithinAABB(p_225317_1_, p_225317_2_, EntityPredicates.NOT_SPECTATING);
    }

default Stream<VoxelShape> func_230318_c_(@Nullable Entity p_230318_1_, AxisAlignedBB p_230318_2_, Predicate<Entity> p_230318_3_)
    {
        if (p_230318_2_.getAverageEdgeLength() < 1.0E-7D)
        {
            return Stream.empty();
        }
        else
        {
            AxisAlignedBB axisalignedbb = p_230318_2_.grow(1.0E-7D);
            return this.getEntitiesInAABBexcluding(p_230318_1_, axisalignedbb, p_230318_3_.and((p_234892_2_) ->
            {
                if (p_234892_2_.getBoundingBox().intersects(axisalignedbb))
                {
                    if (p_230318_1_ == null)
                    {
                        if (p_234892_2_.func_241845_aY())
                        {
                            return true;
                        }
                    }
                    else if (p_230318_1_.canCollide(p_234892_2_))
                    {
                        return true;
                    }
                }

                return false;
            })).stream().map(Entity::getBoundingBox).map(VoxelShapes::create);
        }
    }

    @Nullable

default PlayerEntity getClosestPlayer(double x, double y, double z, double distance, @Nullable Predicate<Entity> predicate)
    {
        double d0 = -1.0D;
        PlayerEntity playerentity = null;

        for (PlayerEntity playerentity1 : this.getPlayers())
        {
            if (predicate == null || predicate.test(playerentity1))
            {
                double d1 = playerentity1.getDistanceSq(x, y, z);

                if ((distance < 0.0D || d1 < distance * distance) && (d0 == -1.0D || d1 < d0))
                {
                    d0 = d1;
                    playerentity = playerentity1;
                }
            }
        }

        return playerentity;
    }

    @Nullable

default PlayerEntity getClosestPlayer(Entity entityIn, double distance)
    {
        return this.getClosestPlayer(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), distance, false);
    }

    @Nullable

default PlayerEntity getClosestPlayer(double x, double y, double z, double distance, boolean creativePlayers)
    {
        Predicate<Entity> predicate = creativePlayers ? EntityPredicates.CAN_AI_TARGET : EntityPredicates.NOT_SPECTATING;
        return this.getClosestPlayer(x, y, z, distance, predicate);
    }

default boolean isPlayerWithin(double x, double y, double z, double distance)
    {
        for (PlayerEntity playerentity : this.getPlayers())
        {
            if (EntityPredicates.NOT_SPECTATING.test(playerentity) && EntityPredicates.IS_LIVING_ALIVE.test(playerentity))
            {
                double d0 = playerentity.getDistanceSq(x, y, z);

                if (distance < 0.0D || d0 < distance * distance)
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Nullable

default PlayerEntity getClosestPlayer(EntityPredicate predicate, LivingEntity target)
    {
        return this.getClosestEntity(this.getPlayers(), predicate, target, target.getPosX(), target.getPosY(), target.getPosZ());
    }

    @Nullable

default PlayerEntity getClosestPlayer(EntityPredicate predicate, LivingEntity target, double p_217372_3_, double p_217372_5_, double p_217372_7_)
    {
        return this.getClosestEntity(this.getPlayers(), predicate, target, p_217372_3_, p_217372_5_, p_217372_7_);
    }

    @Nullable

default PlayerEntity getClosestPlayer(EntityPredicate predicate, double x, double y, double z)
    {
        return this.getClosestEntity(this.getPlayers(), predicate, (LivingEntity)null, x, y, z);
    }

    @Nullable

default <T extends LivingEntity> T getClosestEntityWithinAABB(Class<? extends T> entityClazz, EntityPredicate p_217360_2_, @Nullable LivingEntity target, double x, double y, double z, AxisAlignedBB boundingBox)
    {
        return this.getClosestEntity(this.getEntitiesWithinAABB(entityClazz, boundingBox, (Predicate <? super T >)null), p_217360_2_, target, x, y, z);
    }

    @Nullable

default <T extends LivingEntity> T func_225318_b(Class<? extends T> p_225318_1_, EntityPredicate p_225318_2_, @Nullable LivingEntity p_225318_3_, double p_225318_4_, double p_225318_6_, double p_225318_8_, AxisAlignedBB p_225318_10_)
    {
        return this.getClosestEntity(this.getLoadedEntitiesWithinAABB(p_225318_1_, p_225318_10_, (Predicate <? super T >)null), p_225318_2_, p_225318_3_, p_225318_4_, p_225318_6_, p_225318_8_);
    }

    @Nullable

default <T extends LivingEntity> T getClosestEntity(List<? extends T> entities, EntityPredicate predicate, @Nullable LivingEntity target, double x, double y, double z)
    {
        double d0 = -1.0D;
        T t = null;

        for (T t1 : entities)
        {
            if (predicate.canTarget(target, t1))
            {
                double d1 = t1.getDistanceSq(x, y, z);

                if (d0 == -1.0D || d1 < d0)
                {
                    d0 = d1;
                    t = t1;
                }
            }
        }

        return t;
    }

default List<PlayerEntity> getTargettablePlayersWithinAABB(EntityPredicate predicate, LivingEntity target, AxisAlignedBB box)
    {
        List<PlayerEntity> list = Lists.newArrayList();

        for (PlayerEntity playerentity : this.getPlayers())
        {
            if (box.contains(playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ()) && predicate.canTarget(target, playerentity))
            {
                list.add(playerentity);
            }
        }

        return list;
    }

default <T extends LivingEntity> List<T> getTargettableEntitiesWithinAABB(Class<? extends T> p_217374_1_, EntityPredicate p_217374_2_, LivingEntity p_217374_3_, AxisAlignedBB p_217374_4_)
    {
        List<T> list = this.getEntitiesWithinAABB(p_217374_1_, p_217374_4_, (Predicate <? super T >)null);
        List<T> list1 = Lists.newArrayList();

        for (T t : list)
        {
            if (p_217374_2_.canTarget(p_217374_3_, t))
            {
                list1.add(t);
            }
        }

        return list1;
    }

    @Nullable

default PlayerEntity getPlayerByUuid(UUID uniqueIdIn)
    {
        for (int i = 0; i < this.getPlayers().size(); ++i)
        {
            PlayerEntity playerentity = this.getPlayers().get(i);

            if (uniqueIdIn.equals(playerentity.getUniqueID()))
            {
                return playerentity;
            }
        }

        return null;
    }
}
