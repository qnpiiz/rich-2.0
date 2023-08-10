package net.minecraft.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;

public abstract class BeeSound extends TickableSound
{
    protected final BeeEntity beeInstance;
    private boolean hasSwitchedSound;

    public BeeSound(BeeEntity entity, SoundEvent event, SoundCategory category)
    {
        super(event, category);
        this.beeInstance = entity;
        this.x = (double)((float)entity.getPosX());
        this.y = (double)((float)entity.getPosY());
        this.z = (double)((float)entity.getPosZ());
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0F;
    }

    public void tick()
    {
        boolean flag = this.shouldSwitchSound();

        if (flag && !this.isDonePlaying())
        {
            Minecraft.getInstance().getSoundHandler().playOnNextTick(this.getNextSound());
            this.hasSwitchedSound = true;
        }

        if (!this.beeInstance.removed && !this.hasSwitchedSound)
        {
            this.x = (double)((float)this.beeInstance.getPosX());
            this.y = (double)((float)this.beeInstance.getPosY());
            this.z = (double)((float)this.beeInstance.getPosZ());
            float f = MathHelper.sqrt(Entity.horizontalMag(this.beeInstance.getMotion()));

            if ((double)f >= 0.01D)
            {
                this.pitch = MathHelper.lerp(MathHelper.clamp(f, this.getMinPitch(), this.getMaxPitch()), this.getMinPitch(), this.getMaxPitch());
                this.volume = MathHelper.lerp(MathHelper.clamp(f, 0.0F, 0.5F), 0.0F, 1.2F);
            }
            else
            {
                this.pitch = 0.0F;
                this.volume = 0.0F;
            }
        }
        else
        {
            this.finishPlaying();
        }
    }

    private float getMinPitch()
    {
        return this.beeInstance.isChild() ? 1.1F : 0.7F;
    }

    private float getMaxPitch()
    {
        return this.beeInstance.isChild() ? 1.5F : 1.1F;
    }

    public boolean canBeSilent()
    {
        return true;
    }

    public boolean shouldPlaySound()
    {
        return !this.beeInstance.isSilent();
    }

    protected abstract TickableSound getNextSound();

    protected abstract boolean shouldSwitchSound();
}
