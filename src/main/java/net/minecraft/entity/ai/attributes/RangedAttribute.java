package net.minecraft.entity.ai.attributes;

import net.minecraft.util.math.MathHelper;

public class RangedAttribute extends Attribute
{
    private final double minimumValue;
    private final double maximumValue;

    public RangedAttribute(String attributeName, double defaultValue, double minimumValue, double maximumValue)
    {
        super(attributeName, defaultValue);
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;

        if (minimumValue > maximumValue)
        {
            throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
        }
        else if (defaultValue < minimumValue)
        {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        }
        else if (defaultValue > maximumValue)
        {
            throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
        }
    }

    public double clampValue(double value)
    {
        return MathHelper.clamp(value, this.minimumValue, this.maximumValue);
    }
}
