package net.minecraft.entity;

import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;

public class BoostHelper
{
    private final EntityDataManager manager;
    private final DataParameter<Integer> boostTime;
    private final DataParameter<Boolean> saddled;
    public boolean saddledRaw;
    public int field_233611_b_;
    public int boostTimeRaw;

    public BoostHelper(EntityDataManager manager, DataParameter<Integer> boostTime, DataParameter<Boolean> saddled)
    {
        this.manager = manager;
        this.boostTime = boostTime;
        this.saddled = saddled;
    }

    public void updateData()
    {
        this.saddledRaw = true;
        this.field_233611_b_ = 0;
        this.boostTimeRaw = this.manager.get(this.boostTime);
    }

    public boolean boost(Random rand)
    {
        if (this.saddledRaw)
        {
            return false;
        }
        else
        {
            this.saddledRaw = true;
            this.field_233611_b_ = 0;
            this.boostTimeRaw = rand.nextInt(841) + 140;
            this.manager.set(this.boostTime, this.boostTimeRaw);
            return true;
        }
    }

    public void setSaddledToNBT(CompoundNBT nbt)
    {
        nbt.putBoolean("Saddle", this.getSaddled());
    }

    public void setSaddledFromNBT(CompoundNBT nbt)
    {
        this.setSaddledFromBoolean(nbt.getBoolean("Saddle"));
    }

    public void setSaddledFromBoolean(boolean saddled)
    {
        this.manager.set(this.saddled, saddled);
    }

    public boolean getSaddled()
    {
        return this.manager.get(this.saddled);
    }
}
