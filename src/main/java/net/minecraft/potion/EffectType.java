package net.minecraft.potion;

import net.minecraft.util.text.TextFormatting;

public enum EffectType
{
    BENEFICIAL(TextFormatting.BLUE),
    HARMFUL(TextFormatting.RED),
    NEUTRAL(TextFormatting.BLUE);

    private final TextFormatting color;

    private EffectType(TextFormatting color)
    {
        this.color = color;
    }

    public TextFormatting getColor()
    {
        return this.color;
    }
}
