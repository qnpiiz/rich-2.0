package net.minecraft.util.text;

import java.util.Optional;
import net.minecraft.util.ICharacterConsumer;
import net.minecraft.util.Unit;

public class TextProcessing
{
    private static final Optional<Object> field_238336_a_ = Optional.of(Unit.INSTANCE);

    private static boolean func_238344_a_(Style p_238344_0_, ICharacterConsumer p_238344_1_, int p_238344_2_, char p_238344_3_)
    {
        return Character.isSurrogate(p_238344_3_) ? p_238344_1_.accept(p_238344_2_, p_238344_0_, 65533) : p_238344_1_.accept(p_238344_2_, p_238344_0_, p_238344_3_);
    }

    public static boolean func_238341_a_(String p_238341_0_, Style p_238341_1_, ICharacterConsumer p_238341_2_)
    {
        int i = p_238341_0_.length();

        for (int j = 0; j < i; ++j)
        {
            char c0 = p_238341_0_.charAt(j);

            if (Character.isHighSurrogate(c0))
            {
                if (j + 1 >= i)
                {
                    if (!p_238341_2_.accept(j, p_238341_1_, 65533))
                    {
                        return false;
                    }

                    break;
                }

                char c1 = p_238341_0_.charAt(j + 1);

                if (Character.isLowSurrogate(c1))
                {
                    if (!p_238341_2_.accept(j, p_238341_1_, Character.toCodePoint(c0, c1)))
                    {
                        return false;
                    }

                    ++j;
                }
                else if (!p_238341_2_.accept(j, p_238341_1_, 65533))
                {
                    return false;
                }
            }
            else if (!func_238344_a_(p_238341_1_, p_238341_2_, j, c0))
            {
                return false;
            }
        }

        return true;
    }

    public static boolean func_238345_b_(String p_238345_0_, Style p_238345_1_, ICharacterConsumer p_238345_2_)
    {
        int i = p_238345_0_.length();

        for (int j = i - 1; j >= 0; --j)
        {
            char c0 = p_238345_0_.charAt(j);

            if (Character.isLowSurrogate(c0))
            {
                if (j - 1 < 0)
                {
                    if (!p_238345_2_.accept(0, p_238345_1_, 65533))
                    {
                        return false;
                    }

                    break;
                }

                char c1 = p_238345_0_.charAt(j - 1);

                if (Character.isHighSurrogate(c1))
                {
                    --j;

                    if (!p_238345_2_.accept(j, p_238345_1_, Character.toCodePoint(c1, c0)))
                    {
                        return false;
                    }
                }
                else if (!p_238345_2_.accept(j, p_238345_1_, 65533))
                {
                    return false;
                }
            }
            else if (!func_238344_a_(p_238345_1_, p_238345_2_, j, c0))
            {
                return false;
            }
        }

        return true;
    }

    public static boolean func_238346_c_(String p_238346_0_, Style p_238346_1_, ICharacterConsumer p_238346_2_)
    {
        return func_238339_a_(p_238346_0_, 0, p_238346_1_, p_238346_2_);
    }

    public static boolean func_238339_a_(String p_238339_0_, int p_238339_1_, Style p_238339_2_, ICharacterConsumer p_238339_3_)
    {
        return func_238340_a_(p_238339_0_, p_238339_1_, p_238339_2_, p_238339_2_, p_238339_3_);
    }

    public static boolean func_238340_a_(String p_238340_0_, int p_238340_1_, Style p_238340_2_, Style p_238340_3_, ICharacterConsumer p_238340_4_)
    {
        int i = p_238340_0_.length();
        Style style = p_238340_2_;

        for (int j = p_238340_1_; j < i; ++j)
        {
            char c0 = p_238340_0_.charAt(j);

            if (c0 == 167)
            {
                if (j + 1 >= i)
                {
                    break;
                }

                char c1 = p_238340_0_.charAt(j + 1);
                TextFormatting textformatting = TextFormatting.fromFormattingCode(c1);

                if (textformatting != null)
                {
                    style = textformatting == TextFormatting.RESET ? p_238340_3_ : style.forceFormatting(textformatting);
                }

                ++j;
            }
            else if (Character.isHighSurrogate(c0))
            {
                if (j + 1 >= i)
                {
                    if (!p_238340_4_.accept(j, style, 65533))
                    {
                        return false;
                    }

                    break;
                }

                char c2 = p_238340_0_.charAt(j + 1);

                if (Character.isLowSurrogate(c2))
                {
                    if (!p_238340_4_.accept(j, style, Character.toCodePoint(c0, c2)))
                    {
                        return false;
                    }

                    ++j;
                }
                else if (!p_238340_4_.accept(j, style, 65533))
                {
                    return false;
                }
            }
            else if (!func_238344_a_(style, p_238340_4_, j, c0))
            {
                return false;
            }
        }

        return true;
    }

    public static boolean func_238343_a_(ITextProperties p_238343_0_, Style p_238343_1_, ICharacterConsumer p_238343_2_)
    {
        return !p_238343_0_.getComponentWithStyle((p_238337_1_, p_238337_2_) ->
        {
            return func_238339_a_(p_238337_2_, 0, p_238337_1_, p_238343_2_) ? Optional.empty() : field_238336_a_;
        }, p_238343_1_).isPresent();
    }

    public static String func_238338_a_(String p_238338_0_)
    {
        StringBuilder stringbuilder = new StringBuilder();
        func_238341_a_(p_238338_0_, Style.EMPTY, (p_238342_1_, p_238342_2_, p_238342_3_) ->
        {
            stringbuilder.appendCodePoint(p_238342_3_);
            return true;
        });
        return stringbuilder.toString();
    }

    public static String func_244782_a(ITextProperties p_244782_0_)
    {
        StringBuilder stringbuilder = new StringBuilder();
        func_238343_a_(p_244782_0_, Style.EMPTY, (p_244781_1_, p_244781_2_, p_244781_3_) ->
        {
            stringbuilder.appendCodePoint(p_244781_3_);
            return true;
        });
        return stringbuilder.toString();
    }
}
