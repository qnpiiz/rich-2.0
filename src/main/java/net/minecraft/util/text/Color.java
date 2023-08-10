package net.minecraft.util.text;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public final class Color
{
    private static final Map<TextFormatting, Color> FORMATTING_TO_COLOR_MAP = Stream.of(TextFormatting.values()).filter(TextFormatting::isColor).collect(ImmutableMap.toImmutableMap(Function.identity(), (formatting) ->
    {
        return new Color(formatting.getColor(), formatting.getFriendlyName());
    }));
    private static final Map<String, Color> NAME_TO_COLOR_MAP = FORMATTING_TO_COLOR_MAP.values().stream().collect(ImmutableMap.toImmutableMap((color) ->
    {
        return color.name;
    }, Function.identity()));
    private final int color;
    @Nullable
    private final String name;

    private Color(int color, String name)
    {
        this.color = color;
        this.name = name;
    }

    private Color(int color)
    {
        this.color = color;
        this.name = null;
    }

    public int getColor()
    {
        return this.color;
    }

    public String getName()
    {
        return this.name != null ? this.name : this.getHex();
    }

    private String getHex()
    {
        return String.format("#%06X", this.color);
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
        {
            Color color = (Color)p_equals_1_;
            return this.color == color.color;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return Objects.hash(this.color, this.name);
    }

    public String toString()
    {
        return this.name != null ? this.name : this.getHex();
    }

    @Nullable
    public static Color fromTextFormatting(TextFormatting formatting)
    {
        return FORMATTING_TO_COLOR_MAP.get(formatting);
    }

    public static Color fromInt(int color)
    {
        return new Color(color);
    }

    @Nullable
    public static Color fromHex(String hexString)
    {
        if (hexString.startsWith("#"))
        {
            try
            {
                int i = Integer.parseInt(hexString.substring(1), 16);
                return fromInt(i);
            }
            catch (NumberFormatException numberformatexception)
            {
                return null;
            }
        }
        else
        {
            return NAME_TO_COLOR_MAP.get(hexString);
        }
    }
}
