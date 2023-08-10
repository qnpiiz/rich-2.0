package net.minecraft.client.audio;

import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class GuardianSound extends TickableSound
{
    private final GuardianEntity guardian;

    public GuardianSound(GuardianEntity guardian)
    {
        super(SoundEvents.ENTITY_GUARDIAN_ATTACK, SoundCategory.HOSTILE);
        this.guardian = guardian;
        this.attenuationType = ISound.AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
    }

    public boolean shouldPlaySound()
    {
        return !this.guardian.isSilent();
    }

    public void tick()
    {
        if (!this.guardian.removed && this.guardian.getAttackTarget() == null)
        {
            this.x = (double)((float)this.guardian.getPosX());
            this.y = (double)((float)this.guardian.getPosY());
            this.z = (double)((float)this.guardian.getPosZ());
            float f = this.guardian.getAttackAnimationScale(0.0F);
            this.volume = 0.0F + 1.0F * f * f;
            this.pitch = 0.7F + 0.5F * f;
        }
        else
        {
            this.finishPlaying();
        }
    }
}
