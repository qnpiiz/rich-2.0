package net.minecraft.entity.projectile;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public final class ProjectileHelper
{
    public static RayTraceResult func_234618_a_(Entity p_234618_0_, Predicate<Entity> p_234618_1_)
    {
        Vector3d vector3d = p_234618_0_.getMotion();
        World world = p_234618_0_.world;
        Vector3d vector3d1 = p_234618_0_.getPositionVec();
        Vector3d vector3d2 = vector3d1.add(vector3d);
        RayTraceResult raytraceresult = world.rayTraceBlocks(new RayTraceContext(vector3d1, vector3d2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, p_234618_0_));

        if (raytraceresult.getType() != RayTraceResult.Type.MISS)
        {
            vector3d2 = raytraceresult.getHitVec();
        }

        RayTraceResult raytraceresult1 = rayTraceEntities(world, p_234618_0_, vector3d1, vector3d2, p_234618_0_.getBoundingBox().expand(p_234618_0_.getMotion()).grow(1.0D), p_234618_1_);

        if (raytraceresult1 != null)
        {
            raytraceresult = raytraceresult1;
        }

        return raytraceresult;
    }

    @Nullable

    /**
     * Gets the EntityRayTraceResult representing the entity hit
     */
    public static EntityRayTraceResult rayTraceEntities(Entity shooter, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distance)
    {
        World world = shooter.world;
        double d0 = distance;
        Entity entity = null;
        Vector3d vector3d = null;

        for (Entity entity1 : world.getEntitiesInAABBexcluding(shooter, boundingBox, filter))
        {
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)entity1.getCollisionBorderSize());
            Optional<Vector3d> optional = axisalignedbb.rayTrace(startVec, endVec);

            if (axisalignedbb.contains(startVec))
            {
                if (d0 >= 0.0D)
                {
                    entity = entity1;
                    vector3d = optional.orElse(startVec);
                    d0 = 0.0D;
                }
            }
            else if (optional.isPresent())
            {
                Vector3d vector3d1 = optional.get();
                double d1 = startVec.squareDistanceTo(vector3d1);

                if (d1 < d0 || d0 == 0.0D)
                {
                    if (entity1.getLowestRidingEntity() == shooter.getLowestRidingEntity())
                    {
                        if (d0 == 0.0D)
                        {
                            entity = entity1;
                            vector3d = vector3d1;
                        }
                    }
                    else
                    {
                        entity = entity1;
                        vector3d = vector3d1;
                        d0 = d1;
                    }
                }
            }
        }

        return entity == null ? null : new EntityRayTraceResult(entity, vector3d);
    }

    @Nullable

    /**
     * Gets the EntityRayTraceResult representing the entity hit
     */
    public static EntityRayTraceResult rayTraceEntities(World worldIn, Entity projectile, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter)
    {
        double d0 = Double.MAX_VALUE;
        Entity entity = null;

        for (Entity entity1 : worldIn.getEntitiesInAABBexcluding(projectile, boundingBox, filter))
        {
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)0.3F);
            Optional<Vector3d> optional = axisalignedbb.rayTrace(startVec, endVec);

            if (optional.isPresent())
            {
                double d1 = startVec.squareDistanceTo(optional.get());

                if (d1 < d0)
                {
                    entity = entity1;
                    d0 = d1;
                }
            }
        }

        return entity == null ? null : new EntityRayTraceResult(entity);
    }

    public static final void rotateTowardsMovement(Entity projectile, float rotationSpeed)
    {
        Vector3d vector3d = projectile.getMotion();

        if (vector3d.lengthSquared() != 0.0D)
        {
            float f = MathHelper.sqrt(Entity.horizontalMag(vector3d));
            projectile.rotationYaw = (float)(MathHelper.atan2(vector3d.z, vector3d.x) * (double)(180F / (float)Math.PI)) + 90.0F;

            for (projectile.rotationPitch = (float)(MathHelper.atan2((double)f, vector3d.y) * (double)(180F / (float)Math.PI)) - 90.0F; projectile.rotationPitch - projectile.prevRotationPitch < -180.0F; projectile.prevRotationPitch -= 360.0F)
            {
            }

            while (projectile.rotationPitch - projectile.prevRotationPitch >= 180.0F)
            {
                projectile.prevRotationPitch += 360.0F;
            }

            while (projectile.rotationYaw - projectile.prevRotationYaw < -180.0F)
            {
                projectile.prevRotationYaw -= 360.0F;
            }

            while (projectile.rotationYaw - projectile.prevRotationYaw >= 180.0F)
            {
                projectile.prevRotationYaw += 360.0F;
            }

            projectile.rotationPitch = MathHelper.lerp(rotationSpeed, projectile.prevRotationPitch, projectile.rotationPitch);
            projectile.rotationYaw = MathHelper.lerp(rotationSpeed, projectile.prevRotationYaw, projectile.rotationYaw);
        }
    }

    public static Hand getHandWith(LivingEntity living, Item itemIn)
    {
        return living.getHeldItemMainhand().getItem() == itemIn ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }

    public static AbstractArrowEntity fireArrow(LivingEntity shooter, ItemStack arrowStack, float distanceFactor)
    {
        ArrowItem arrowitem = (ArrowItem)(arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);
        AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(shooter.world, arrowStack, shooter);
        abstractarrowentity.setEnchantmentEffectsFromEntity(shooter, distanceFactor);

        if (arrowStack.getItem() == Items.TIPPED_ARROW && abstractarrowentity instanceof ArrowEntity)
        {
            ((ArrowEntity)abstractarrowentity).setPotionEffect(arrowStack);
        }

        return abstractarrowentity;
    }
}
