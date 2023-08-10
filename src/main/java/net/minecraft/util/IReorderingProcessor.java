package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.List;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextProcessing;

@FunctionalInterface
public interface IReorderingProcessor
{
    IReorderingProcessor field_242232_a = (p_242236_0_) ->
    {
        return true;
    };

    boolean accept(ICharacterConsumer p_accept_1_);

    static IReorderingProcessor fromCodePoint(int codePoint, Style style)
    {
        return (p_242243_2_) ->
        {
            return p_242243_2_.accept(0, style, codePoint);
        };
    }

    static IReorderingProcessor fromString(String string, Style style)
    {
        return string.isEmpty() ? field_242232_a : (p_242245_2_) ->
        {
            return TextProcessing.func_238341_a_(string, style, p_242245_2_);
        };
    }

    static IReorderingProcessor func_242246_b(String p_242246_0_, Style p_242246_1_, Int2IntFunction p_242246_2_)
    {
        return p_242246_0_.isEmpty() ? field_242232_a : (p_242240_3_) ->
        {
            return TextProcessing.func_238345_b_(p_242246_0_, p_242246_1_, func_242237_a(p_242240_3_, p_242246_2_));
        };
    }

    static ICharacterConsumer func_242237_a(ICharacterConsumer consumer, Int2IntFunction p_242237_1_)
    {
        return (p_242238_2_, p_242238_3_, p_242238_4_) ->
        {
            return consumer.accept(p_242238_2_, p_242238_3_, p_242237_1_.apply(Integer.valueOf(p_242238_4_)));
        };
    }

    static IReorderingProcessor func_242234_a(IReorderingProcessor p_242234_0_, IReorderingProcessor p_242234_1_)
    {
        return func_242244_b(p_242234_0_, p_242234_1_);
    }

    static IReorderingProcessor func_242241_a(List<IReorderingProcessor> p_242241_0_)
    {
        int i = p_242241_0_.size();

        switch (i)
        {
            case 0:
                return field_242232_a;

            case 1:
                return p_242241_0_.get(0);

            case 2:
                return func_242244_b(p_242241_0_.get(0), p_242241_0_.get(1));

            default:
                return func_242247_b(ImmutableList.copyOf(p_242241_0_));
        }
    }

    static IReorderingProcessor func_242244_b(IReorderingProcessor p_242244_0_, IReorderingProcessor p_242244_1_)
    {
        return (p_242235_2_) ->
        {
            return p_242244_0_.accept(p_242235_2_) && p_242244_1_.accept(p_242235_2_);
        };
    }

    static IReorderingProcessor func_242247_b(List<IReorderingProcessor> p_242247_0_)
    {
        return (p_242242_1_) ->
        {
            for (IReorderingProcessor ireorderingprocessor : p_242247_0_)
            {
                if (!ireorderingprocessor.accept(p_242242_1_))
                {
                    return false;
                }
            }

            return true;
        };
    }
}
