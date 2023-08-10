package net.minecraft.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public interface IRideable
{
    boolean boost();

    void travelTowards(Vector3d travelVec);

    float getMountedSpeed();

default boolean ride(MobEntity mount, BoostHelper helper, Vector3d p_233622_3_)
    {
        if (!mount.isAlive())
        {
            return false;
        }
        else
        {
            Entity entity = mount.getPassengers().isEmpty() ? null : mount.getPassengers().get(0);

            if (mount.isBeingRidden() && mount.canBeSteered() && entity instanceof PlayerEntity)
            {
                mount.rotationYaw = entity.rotationYaw;
                mount.prevRotationYaw = mount.rotationYaw;
                mount.rotationPitch = entity.rotationPitch * 0.5F;
                mount.setRotation(mount.rotationYaw, mount.rotationPitch);
                mount.renderYawOffset = mount.rotationYaw;
                mount.rotationYawHead = mount.rotationYaw;
                mount.stepHeight = 1.0F;
                mount.jumpMovementFactor = mount.getAIMoveSpeed() * 0.1F;

                if (helper.saddledRaw && helper.field_233611_b_++ > helper.boostTimeRaw)
                {
                    helper.saddledRaw = false;
                }

                if (mount.canPassengerSteer())
                {
                    float f = this.getMountedSpeed();

                    if (helper.saddledRaw)
                    {
                        f += f * 1.15F * MathHelper.sin((float)helper.field_233611_b_ / (float)helper.boostTimeRaw * (float)Math.PI);
                    }

                    mount.setAIMoveSpeed(f);
                    this.travelTowards(new Vector3d(0.0D, 0.0D, 1.0D));
                    mount.newPosRotationIncrements = 0;
                }
                else
                {
                    mount.func_233629_a_(mount, false);
                    mount.setMotion(Vector3d.ZERO);
                }

                return true;
            }
            else
            {
                mount.stepHeight = 0.5F;
                mount.jumpMovementFactor = 0.02F;
                this.travelTowards(p_233622_3_);
                return false;
            }
        }
    }
}
