package net.minecraft.entity.ai.brain.schedule;

public class DutyTime
{
    private final int duration;
    private final float active;

    public DutyTime(int durationIn, float active)
    {
        this.duration = durationIn;
        this.active = active;
    }

    public int getDuration()
    {
        return this.duration;
    }

    public float getActive()
    {
        return this.active;
    }
}
