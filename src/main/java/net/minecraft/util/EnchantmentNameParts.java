package net.minecraft.util;

import java.util.Random;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

public class EnchantmentNameParts
{
    private static final ResourceLocation GALACTIC_ALT_FONT = new ResourceLocation("minecraft", "alt");
    private static final Style GALACTIC_STYLE = Style.EMPTY.setFontId(GALACTIC_ALT_FONT);
    private static final EnchantmentNameParts INSTANCE = new EnchantmentNameParts();
    private final Random rand = new Random();
    private final String[] namePartsArray = new String[] {"the", "elder", "scrolls", "klaatu", "berata", "niktu", "xyzzy", "bless", "curse", "light", "darkness", "fire", "air", "earth", "water", "hot", "dry", "cold", "wet", "ignite", "snuff", "embiggen", "twist", "shorten", "stretch", "fiddle", "destroy", "imbue", "galvanize", "enchant", "free", "limited", "range", "of", "towards", "inside", "sphere", "cube", "self", "other", "ball", "mental", "physical", "grow", "shrink", "demon", "elemental", "spirit", "animal", "creature", "beast", "humanoid", "undead", "fresh", "stale", "phnglui", "mglwnafh", "cthulhu", "rlyeh", "wgahnagl", "fhtagn", "baguette"};

    private EnchantmentNameParts()
    {
    }

    public static EnchantmentNameParts getInstance()
    {
        return INSTANCE;
    }

    public ITextProperties getGalacticEnchantmentName(FontRenderer fontRenderer, int maxWidth)
    {
        StringBuilder stringbuilder = new StringBuilder();
        int i = this.rand.nextInt(2) + 3;

        for (int j = 0; j < i; ++j)
        {
            if (j != 0)
            {
                stringbuilder.append(" ");
            }

            stringbuilder.append(Util.getRandomObject(this.namePartsArray, this.rand));
        }

        return fontRenderer.getCharacterManager().func_238358_a_((new StringTextComponent(stringbuilder.toString())).mergeStyle(GALACTIC_STYLE), maxWidth, Style.EMPTY);
    }

    /**
     * Resets the underlying random number generator using a given seed.
     */
    public void reseedRandomGenerator(long seed)
    {
        this.rand.setSeed(seed);
    }
}
