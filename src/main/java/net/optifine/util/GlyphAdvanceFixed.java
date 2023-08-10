package net.optifine.util;

import net.minecraft.client.gui.fonts.IGlyph;

public class GlyphAdvanceFixed implements IGlyph
{
    private float advanceWidth;

    public GlyphAdvanceFixed(float advanceWidth)
    {
        this.advanceWidth = advanceWidth;
    }

    public float getAdvance()
    {
        return this.advanceWidth;
    }
}
