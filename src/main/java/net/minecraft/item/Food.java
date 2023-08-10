package net.minecraft.item;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.potion.EffectInstance;

public class Food
{
    private final int value;
    private final float saturation;
    private final boolean meat;
    private final boolean canEatWhenFull;
    private final boolean fastToEat;
    private final List<Pair<EffectInstance, Float>> effects;

    private Food(int healing, float saturationIn, boolean isMeat, boolean alwaysEdible, boolean fastEdible, List<Pair<EffectInstance, Float>> effectsIn)
    {
        this.value = healing;
        this.saturation = saturationIn;
        this.meat = isMeat;
        this.canEatWhenFull = alwaysEdible;
        this.fastToEat = fastEdible;
        this.effects = effectsIn;
    }

    public int getHealing()
    {
        return this.value;
    }

    public float getSaturation()
    {
        return this.saturation;
    }

    public boolean isMeat()
    {
        return this.meat;
    }

    public boolean canEatWhenFull()
    {
        return this.canEatWhenFull;
    }

    public boolean isFastEating()
    {
        return this.fastToEat;
    }

    public List<Pair<EffectInstance, Float>> getEffects()
    {
        return this.effects;
    }

    public static class Builder
    {
        private int value;
        private float saturation;
        private boolean meat;
        private boolean alwaysEdible;
        private boolean fastToEat;
        private final List<Pair<EffectInstance, Float>> effects = Lists.newArrayList();

        public Food.Builder hunger(int hungerIn)
        {
            this.value = hungerIn;
            return this;
        }

        public Food.Builder saturation(float saturationIn)
        {
            this.saturation = saturationIn;
            return this;
        }

        public Food.Builder meat()
        {
            this.meat = true;
            return this;
        }

        public Food.Builder setAlwaysEdible()
        {
            this.alwaysEdible = true;
            return this;
        }

        public Food.Builder fastToEat()
        {
            this.fastToEat = true;
            return this;
        }

        public Food.Builder effect(EffectInstance effectIn, float probability)
        {
            this.effects.add(Pair.of(effectIn, probability));
            return this;
        }

        public Food build()
        {
            return new Food(this.value, this.saturation, this.meat, this.alwaysEdible, this.fastToEat, this.effects);
        }
    }
}
