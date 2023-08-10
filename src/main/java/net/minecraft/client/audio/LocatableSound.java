package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public abstract class LocatableSound implements ISound
{
    protected Sound sound;
    protected final SoundCategory category;
    protected final ResourceLocation positionedSoundLocation;
    protected float volume = 1.0F;
    protected float pitch = 1.0F;
    protected double x;
    protected double y;
    protected double z;
    protected boolean repeat;

    /** The number of ticks between repeating the sound */
    protected int repeatDelay;
    protected ISound.AttenuationType attenuationType = ISound.AttenuationType.LINEAR;
    protected boolean priority;
    protected boolean global;

    protected LocatableSound(SoundEvent soundIn, SoundCategory categoryIn)
    {
        this(soundIn.getName(), categoryIn);
    }

    protected LocatableSound(ResourceLocation soundId, SoundCategory categoryIn)
    {
        this.positionedSoundLocation = soundId;
        this.category = categoryIn;
    }

    public ResourceLocation getSoundLocation()
    {
        return this.positionedSoundLocation;
    }

    public SoundEventAccessor createAccessor(SoundHandler handler)
    {
        SoundEventAccessor soundeventaccessor = handler.getAccessor(this.positionedSoundLocation);

        if (soundeventaccessor == null)
        {
            this.sound = SoundHandler.MISSING_SOUND;
        }
        else
        {
            this.sound = soundeventaccessor.cloneEntry();
        }

        return soundeventaccessor;
    }

    public Sound getSound()
    {
        return this.sound;
    }

    public SoundCategory getCategory()
    {
        return this.category;
    }

    public boolean canRepeat()
    {
        return this.repeat;
    }

    public int getRepeatDelay()
    {
        return this.repeatDelay;
    }

    public float getVolume()
    {
        return this.volume * this.sound.getVolume();
    }

    public float getPitch()
    {
        return this.pitch * this.sound.getPitch();
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public double getZ()
    {
        return this.z;
    }

    public ISound.AttenuationType getAttenuationType()
    {
        return this.attenuationType;
    }

    /**
     * True if the sound is not tied to a particular position in world (e.g. BGM)
     */
    public boolean isGlobal()
    {
        return this.global;
    }

    public String toString()
    {
        return "SoundInstance[" + this.positionedSoundLocation + "]";
    }
}
