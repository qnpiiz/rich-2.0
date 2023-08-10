package net.minecraft.client.audio;

import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class BeeAngrySound extends BeeSound
{
    public BeeAngrySound(BeeEntity entity)
    {
        super(entity, SoundEvents.ENTITY_BEE_LOOP_AGGRESSIVE, SoundCategory.NEUTRAL);
        this.repeatDelay = 0;
    }

    protected TickableSound getNextSound()
    {
        return new BeeFlightSound(this.beeInstance);
    }

    protected boolean shouldSwitchSound()
    {
        return !this.beeInstance.func_233678_J__();
    }
}
