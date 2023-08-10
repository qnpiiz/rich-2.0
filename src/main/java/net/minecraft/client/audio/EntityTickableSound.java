package net.minecraft.client.audio;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class EntityTickableSound extends TickableSound
{
    private final Entity entity;

    public EntityTickableSound(SoundEvent sound, SoundCategory category, Entity entity)
    {
        this(sound, category, 1.0F, 1.0F, entity);
    }

    public EntityTickableSound(SoundEvent sound, SoundCategory category, float volume, float pitch, Entity entity)
    {
        super(sound, category);
        this.volume = volume;
        this.pitch = pitch;
        this.entity = entity;
        this.x = (double)((float)this.entity.getPosX());
        this.y = (double)((float)this.entity.getPosY());
        this.z = (double)((float)this.entity.getPosZ());
    }

    public boolean shouldPlaySound()
    {
        return !this.entity.isSilent();
    }

    public void tick()
    {
        if (this.entity.removed)
        {
            this.finishPlaying();
        }
        else
        {
            this.x = (double)((float)this.entity.getPosX());
            this.y = (double)((float)this.entity.getPosY());
            this.z = (double)((float)this.entity.getPosZ());
        }
    }
}
