package net.minecraft.world.storage;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class MapDecoration
{
    private final MapDecoration.Type type;
    private byte x;
    private byte y;
    private byte rotation;
    private final ITextComponent customName;

    public MapDecoration(MapDecoration.Type type, byte x, byte y, byte rotation, @Nullable ITextComponent customName)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.customName = customName;
    }

    public byte getImage()
    {
        return this.type.getIcon();
    }

    public MapDecoration.Type getType()
    {
        return this.type;
    }

    public byte getX()
    {
        return this.x;
    }

    public byte getY()
    {
        return this.y;
    }

    public byte getRotation()
    {
        return this.rotation;
    }

    public boolean renderOnFrame()
    {
        return this.type.isRenderedOnFrame();
    }

    @Nullable
    public ITextComponent getCustomName()
    {
        return this.customName;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof MapDecoration))
        {
            return false;
        }
        else
        {
            MapDecoration mapdecoration = (MapDecoration)p_equals_1_;

            if (this.type != mapdecoration.type)
            {
                return false;
            }
            else if (this.rotation != mapdecoration.rotation)
            {
                return false;
            }
            else if (this.x != mapdecoration.x)
            {
                return false;
            }
            else if (this.y != mapdecoration.y)
            {
                return false;
            }
            else
            {
                return Objects.equals(this.customName, mapdecoration.customName);
            }
        }
    }

    public int hashCode()
    {
        int i = this.type.getIcon();
        i = 31 * i + this.x;
        i = 31 * i + this.y;
        i = 31 * i + this.rotation;
        return 31 * i + Objects.hashCode(this.customName);
    }

    public static enum Type
    {
        PLAYER(false),
        FRAME(true),
        RED_MARKER(false),
        BLUE_MARKER(false),
        TARGET_X(true),
        TARGET_POINT(true),
        PLAYER_OFF_MAP(false),
        PLAYER_OFF_LIMITS(false),
        MANSION(true, 5393476),
        MONUMENT(true, 3830373),
        BANNER_WHITE(true),
        BANNER_ORANGE(true),
        BANNER_MAGENTA(true),
        BANNER_LIGHT_BLUE(true),
        BANNER_YELLOW(true),
        BANNER_LIME(true),
        BANNER_PINK(true),
        BANNER_GRAY(true),
        BANNER_LIGHT_GRAY(true),
        BANNER_CYAN(true),
        BANNER_PURPLE(true),
        BANNER_BLUE(true),
        BANNER_BROWN(true),
        BANNER_GREEN(true),
        BANNER_RED(true),
        BANNER_BLACK(true),
        RED_X(true);

        private final byte icon = (byte)this.ordinal();
        private final boolean renderedOnFrame;
        private final int mapColor;

        private Type(boolean renderedOnFrame)
        {
            this(renderedOnFrame, -1);
        }

        private Type(boolean renderedOnFrame, int mapColor)
        {
            this.renderedOnFrame = renderedOnFrame;
            this.mapColor = mapColor;
        }

        public byte getIcon()
        {
            return this.icon;
        }

        public boolean isRenderedOnFrame()
        {
            return this.renderedOnFrame;
        }

        public boolean hasMapColor()
        {
            return this.mapColor >= 0;
        }

        public int getMapColor()
        {
            return this.mapColor;
        }

        public static MapDecoration.Type byIcon(byte iconByte)
        {
            return values()[MathHelper.clamp(iconByte, 0, values().length - 1)];
        }
    }
}
