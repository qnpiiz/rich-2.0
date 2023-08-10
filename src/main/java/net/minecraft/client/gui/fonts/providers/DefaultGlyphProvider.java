package net.minecraft.client.gui.fonts.providers;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import javax.annotation.Nullable;
import net.minecraft.client.gui.fonts.DefaultGlyph;
import net.minecraft.client.gui.fonts.IGlyphInfo;

public class DefaultGlyphProvider implements IGlyphProvider
{
    @Nullable
    public IGlyphInfo getGlyphInfo(int character)
    {
        return DefaultGlyph.INSTANCE;
    }

    public IntSet func_230428_a_()
    {
        return IntSets.EMPTY_SET;
    }
}
