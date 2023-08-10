package net.minecraft.client.gui.fonts.providers;

import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Closeable;
import javax.annotation.Nullable;
import net.minecraft.client.gui.fonts.IGlyphInfo;

public interface IGlyphProvider extends Closeable
{
default void close()
    {
    }

    @Nullable

default IGlyphInfo getGlyphInfo(int character)
    {
        return null;
    }

    IntSet func_230428_a_();
}
