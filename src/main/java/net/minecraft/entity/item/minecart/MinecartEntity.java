package net.minecraft.entity.item.minecart;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MinecartEntity extends AbstractMinecartEntity
{
    public MinecartEntity(EntityType<?> type, World world)
    {
        super(type, world);
    }

    public MinecartEntity(World worldIn, double x, double y, double z)
    {
        super(EntityType.MINECART, worldIn, x, y, z);
    }

    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand)
    {
        if (player.isSecondaryUseActive())
        {
            return ActionResultType.PASS;
        }
        else if (this.isBeingRidden())
        {
            return ActionResultType.PASS;
        }
        else if (!this.world.isRemote)
        {
            return player.startRiding(this) ? ActionResultType.CONSUME : ActionResultType.PASS;
        }
        else
        {
            return ActionResultType.SUCCESS;
        }
    }

    /**
     * Called every tick the minecart is on an activator rail.
     */
    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower)
    {
        if (receivingPower)
        {
            if (this.isBeingRidden())
            {
                this.removePassengers();
            }

            if (this.getRollingAmplitude() == 0)
            {
                this.setRollingDirection(-this.getRollingDirection());
                this.setRollingAmplitude(10);
                this.setDamage(50.0F);
                this.markVelocityChanged();
            }
        }
    }

    public AbstractMinecartEntity.Type getMinecartType()
    {
        return AbstractMinecartEntity.Type.RIDEABLE;
    }
}
