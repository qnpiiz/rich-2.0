package net.minecraft.client.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextProcessing;

public class BidiReorder
{
    private final String field_244283_a;
    private final List<Style> field_244284_b;
    private final Int2IntFunction field_244285_c;

    private BidiReorder(String p_i242079_1_, List<Style> p_i242079_2_, Int2IntFunction p_i242079_3_)
    {
        this.field_244283_a = p_i242079_1_;
        this.field_244284_b = ImmutableList.copyOf(p_i242079_2_);
        this.field_244285_c = p_i242079_3_;
    }

    public String func_244286_a()
    {
        return this.field_244283_a;
    }

    public List<IReorderingProcessor> func_244287_a(int p_244287_1_, int p_244287_2_, boolean p_244287_3_)
    {
        if (p_244287_2_ == 0)
        {
            return ImmutableList.of();
        }
        else
        {
            List<IReorderingProcessor> list = Lists.newArrayList();
            Style style = this.field_244284_b.get(p_244287_1_);
            int i = p_244287_1_;

            for (int j = 1; j < p_244287_2_; ++j)
            {
                int k = p_244287_1_ + j;
                Style style1 = this.field_244284_b.get(k);

                if (!style1.equals(style))
                {
                    String s = this.field_244283_a.substring(i, k);
                    list.add(p_244287_3_ ? IReorderingProcessor.func_242246_b(s, style, this.field_244285_c) : IReorderingProcessor.fromString(s, style));
                    style = style1;
                    i = k;
                }
            }

            if (i < p_244287_1_ + p_244287_2_)
            {
                String s1 = this.field_244283_a.substring(i, p_244287_1_ + p_244287_2_);
                list.add(p_244287_3_ ? IReorderingProcessor.func_242246_b(s1, style, this.field_244285_c) : IReorderingProcessor.fromString(s1, style));
            }

            return p_244287_3_ ? Lists.reverse(list) : list;
        }
    }

    public static BidiReorder func_244290_a(ITextProperties p_244290_0_, Int2IntFunction p_244290_1_, UnaryOperator<String> p_244290_2_)
    {
        StringBuilder stringbuilder = new StringBuilder();
        List<Style> list = Lists.newArrayList();
        p_244290_0_.getComponentWithStyle((p_244289_2_, p_244289_3_) ->
        {
            TextProcessing.func_238346_c_(p_244289_3_, p_244289_2_, (p_244288_2_, p_244288_3_, p_244288_4_) -> {
                stringbuilder.appendCodePoint(p_244288_4_);
                int i = Character.charCount(p_244288_4_);

                for (int j = 0; j < i; ++j)
                {
                    list.add(p_244288_3_);
                }

                return true;
            });
            return Optional.empty();
        }, Style.EMPTY);
        return new BidiReorder(p_244290_2_.apply(stringbuilder.toString()), list, p_244290_1_);
    }
}
