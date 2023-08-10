package net.minecraft.potion;

public class InstantEffect extends Effect
{
    public InstantEffect(EffectType type, int liquidColor)
    {
        super(type, liquidColor);
    }

    /**
     * Returns true if the potion has an instant effect instead of a continuous one (eg Harming)
     */
    public boolean isInstant()
    {
        return true;
    }

    /**
     * checks if Potion effect is ready to be applied this tick.
     */
    public boolean isReady(int duration, int amplifier)
    {
        return duration >= 1;
    }
}
