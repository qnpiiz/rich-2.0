package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

public enum BannerPattern
{
    BASE("base", "b", false),
    SQUARE_BOTTOM_LEFT("square_bottom_left", "bl"),
    SQUARE_BOTTOM_RIGHT("square_bottom_right", "br"),
    SQUARE_TOP_LEFT("square_top_left", "tl"),
    SQUARE_TOP_RIGHT("square_top_right", "tr"),
    STRIPE_BOTTOM("stripe_bottom", "bs"),
    STRIPE_TOP("stripe_top", "ts"),
    STRIPE_LEFT("stripe_left", "ls"),
    STRIPE_RIGHT("stripe_right", "rs"),
    STRIPE_CENTER("stripe_center", "cs"),
    STRIPE_MIDDLE("stripe_middle", "ms"),
    STRIPE_DOWNRIGHT("stripe_downright", "drs"),
    STRIPE_DOWNLEFT("stripe_downleft", "dls"),
    STRIPE_SMALL("small_stripes", "ss"),
    CROSS("cross", "cr"),
    STRAIGHT_CROSS("straight_cross", "sc"),
    TRIANGLE_BOTTOM("triangle_bottom", "bt"),
    TRIANGLE_TOP("triangle_top", "tt"),
    TRIANGLES_BOTTOM("triangles_bottom", "bts"),
    TRIANGLES_TOP("triangles_top", "tts"),
    DIAGONAL_LEFT("diagonal_left", "ld"),
    DIAGONAL_RIGHT("diagonal_up_right", "rd"),
    DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud"),
    DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud"),
    CIRCLE_MIDDLE("circle", "mc"),
    RHOMBUS_MIDDLE("rhombus", "mr"),
    HALF_VERTICAL("half_vertical", "vh"),
    HALF_HORIZONTAL("half_horizontal", "hh"),
    HALF_VERTICAL_MIRROR("half_vertical_right", "vhr"),
    HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb"),
    BORDER("border", "bo"),
    CURLY_BORDER("curly_border", "cbo"),
    GRADIENT("gradient", "gra"),
    GRADIENT_UP("gradient_up", "gru"),
    BRICKS("bricks", "bri"),
    GLOBE("globe", "glb", true),
    CREEPER("creeper", "cre", true),
    SKULL("skull", "sku", true),
    FLOWER("flower", "flo", true),
    MOJANG("mojang", "moj", true),
    PIGLIN("piglin", "pig", true);

    private static final BannerPattern[] BANNER_PATTERNS = values();
    public static final int BANNER_PATTERNS_COUNT = BANNER_PATTERNS.length;
    public static final int BANNERS_WITH_ITEMS = (int)Arrays.stream(BANNER_PATTERNS).filter((pattern) -> {
        return pattern.hasPatternItem;
    }).count();
    public static final int PATTERN_ITEM_INDEX = BANNER_PATTERNS_COUNT - BANNERS_WITH_ITEMS - 1;
    private final boolean hasPatternItem;
    private final String fileName;
    private final String hashname;

    private BannerPattern(String fileNameIn, String hashNameIn)
    {
        this(fileNameIn, hashNameIn, false);
    }

    private BannerPattern(String fileName, String hashname, boolean hasPatternItem)
    {
        this.fileName = fileName;
        this.hashname = hashname;
        this.hasPatternItem = hasPatternItem;
    }

    public ResourceLocation getTextureLocation(boolean isBanner)
    {
        String s = isBanner ? "banner" : "shield";
        return new ResourceLocation("entity/" + s + "/" + this.getFileName());
    }

    public String getFileName()
    {
        return this.fileName;
    }

    public String getHashname()
    {
        return this.hashname;
    }

    @Nullable
    public static BannerPattern byHash(String hash)
    {
        for (BannerPattern bannerpattern : values())
        {
            if (bannerpattern.hashname.equals(hash))
            {
                return bannerpattern;
            }
        }

        return null;
    }

    public static class Builder {
        private final List<Pair<BannerPattern, DyeColor>> patternColors = Lists.newArrayList();

        public BannerPattern.Builder setPatternWithColor(BannerPattern pattern, DyeColor color)
        {
            this.patternColors.add(Pair.of(pattern, color));
            return this;
        }

        public ListNBT buildNBT()
        {
            ListNBT listnbt = new ListNBT();

            for (Pair<BannerPattern, DyeColor> pair : this.patternColors)
            {
                CompoundNBT compoundnbt = new CompoundNBT();
                compoundnbt.putString("Pattern", (pair.getLeft()).hashname);
                compoundnbt.putInt("Color", pair.getRight().getId());
                listnbt.add(compoundnbt);
            }

            return listnbt;
        }
    }
}
