package net.minecraft.data;

import javax.annotation.Nullable;

public final class StockTextureAliases
{
    public static final StockTextureAliases ALL = createTextureAlias("all");
    public static final StockTextureAliases TEXTURE = createTextureAlias("texture", ALL);
    public static final StockTextureAliases PARTICLE = createTextureAlias("particle", TEXTURE);
    public static final StockTextureAliases END = createTextureAlias("end", ALL);
    public static final StockTextureAliases BOTTOM = createTextureAlias("bottom", END);
    public static final StockTextureAliases TOP = createTextureAlias("top", END);
    public static final StockTextureAliases FRONT = createTextureAlias("front", ALL);
    public static final StockTextureAliases BACK = createTextureAlias("back", ALL);
    public static final StockTextureAliases SIDE = createTextureAlias("side", ALL);
    public static final StockTextureAliases NORTH = createTextureAlias("north", SIDE);
    public static final StockTextureAliases SOUTH = createTextureAlias("south", SIDE);
    public static final StockTextureAliases EAST = createTextureAlias("east", SIDE);
    public static final StockTextureAliases WEST = createTextureAlias("west", SIDE);
    public static final StockTextureAliases UP = createTextureAlias("up");
    public static final StockTextureAliases DOWN = createTextureAlias("down");
    public static final StockTextureAliases CROSS = createTextureAlias("cross");
    public static final StockTextureAliases PLANT = createTextureAlias("plant");
    public static final StockTextureAliases WALL = createTextureAlias("wall", ALL);
    public static final StockTextureAliases RAIL = createTextureAlias("rail");
    public static final StockTextureAliases WOOL = createTextureAlias("wool");
    public static final StockTextureAliases PATTERN = createTextureAlias("pattern");
    public static final StockTextureAliases PANE = createTextureAlias("pane");
    public static final StockTextureAliases EDGE = createTextureAlias("edge");
    public static final StockTextureAliases FAN = createTextureAlias("fan");
    public static final StockTextureAliases STEM = createTextureAlias("stem");
    public static final StockTextureAliases UPPERSTEM = createTextureAlias("upperstem");
    public static final StockTextureAliases CROP = createTextureAlias("crop");
    public static final StockTextureAliases DIRT = createTextureAlias("dirt");
    public static final StockTextureAliases FIRE = createTextureAlias("fire");
    public static final StockTextureAliases LANTERN = createTextureAlias("lantern");
    public static final StockTextureAliases PLATFORM = createTextureAlias("platform");
    public static final StockTextureAliases UNSTICKY = createTextureAlias("unsticky");
    public static final StockTextureAliases TORCH = createTextureAlias("torch");
    public static final StockTextureAliases LAYER_ZERO = createTextureAlias("layer0");
    public static final StockTextureAliases LIT_LOG = createTextureAlias("lit_log");
    private final String name;
    @Nullable
    private final StockTextureAliases textureAlias;

    private static StockTextureAliases createTextureAlias(String name)
    {
        return new StockTextureAliases(name, (StockTextureAliases)null);
    }

    private static StockTextureAliases createTextureAlias(String name, StockTextureAliases textureAlias)
    {
        return new StockTextureAliases(name, textureAlias);
    }

    private StockTextureAliases(String name, @Nullable StockTextureAliases textureAlias)
    {
        this.name = name;
        this.textureAlias = textureAlias;
    }

    public String getName()
    {
        return this.name;
    }

    @Nullable
    public StockTextureAliases getAlias()
    {
        return this.textureAlias;
    }

    public String toString()
    {
        return "#" + this.name;
    }
}
