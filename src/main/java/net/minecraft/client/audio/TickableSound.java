package net.minecraft.client.audio;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public abstract class TickableSound extends LocatableSound implements ITickableSound
{
    private boolean donePlaying;

    protected TickableSound(SoundEvent soundIn, SoundCategory categoryIn)
    {
        super(soundIn, categoryIn);
    }

    public boolean isDonePlaying()
    {
        return this.donePlaying;
    }

    protected final void finishPlaying()
    {
        this.donePlaying = true;
        this.repeat = false;
    }
}
