package net.minecraft.client.audio;

import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class BeeFlightSound extends BeeSound
{
    public BeeFlightSound(BeeEntity entity)
    {
        super(entity, SoundEvents.ENTITY_BEE_LOOP, SoundCategory.NEUTRAL);
    }

    protected TickableSound getNextSound()
    {
        return new BeeAngrySound(this.beeInstance);
    }

    protected boolean shouldSwitchSound()
    {
        return this.beeInstance.func_233678_J__();
    }
}
