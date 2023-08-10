package net.minecraft.client.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;

public interface IBidiRenderer
{
    IBidiRenderer field_243257_a = new IBidiRenderer()
    {
        public int func_241863_a(MatrixStack p_241863_1_, int p_241863_2_, int p_241863_3_)
        {
            return p_241863_3_;
        }
        public int func_241864_a(MatrixStack p_241864_1_, int p_241864_2_, int p_241864_3_, int p_241864_4_, int p_241864_5_)
        {
            return p_241864_3_;
        }
        public int func_241865_b(MatrixStack p_241865_1_, int p_241865_2_, int p_241865_3_, int p_241865_4_, int p_241865_5_)
        {
            return p_241865_3_;
        }
        public int func_241866_c(MatrixStack p_241866_1_, int p_241866_2_, int p_241866_3_, int p_241866_4_, int p_241866_5_)
        {
            return p_241866_3_;
        }
        public int func_241862_a()
        {
            return 0;
        }
    };

    static IBidiRenderer func_243258_a(FontRenderer p_243258_0_, ITextProperties p_243258_1_, int p_243258_2_)
    {
        return func_243262_b(p_243258_0_, p_243258_0_.trimStringToWidth(p_243258_1_, p_243258_2_).stream().map((p_243264_1_) ->
        {
            return new IBidiRenderer.Entry(p_243264_1_, p_243258_0_.func_243245_a(p_243264_1_));
        }).collect(ImmutableList.toImmutableList()));
    }

    static IBidiRenderer func_243259_a(FontRenderer p_243259_0_, ITextProperties p_243259_1_, int p_243259_2_, int p_243259_3_)
    {
        return func_243262_b(p_243259_0_, p_243259_0_.trimStringToWidth(p_243259_1_, p_243259_2_).stream().limit((long)p_243259_3_).map((p_243263_1_) ->
        {
            return new IBidiRenderer.Entry(p_243263_1_, p_243259_0_.func_243245_a(p_243263_1_));
        }).collect(ImmutableList.toImmutableList()));
    }

    static IBidiRenderer func_243260_a(FontRenderer p_243260_0_, ITextComponent... p_243260_1_)
    {
        return func_243262_b(p_243260_0_, Arrays.stream(p_243260_1_).map(ITextComponent::func_241878_f).map((p_243261_1_) ->
        {
            return new IBidiRenderer.Entry(p_243261_1_, p_243260_0_.func_243245_a(p_243261_1_));
        }).collect(ImmutableList.toImmutableList()));
    }

    static IBidiRenderer func_243262_b(final FontRenderer p_243262_0_, final List<IBidiRenderer.Entry> p_243262_1_)
    {
        return p_243262_1_.isEmpty() ? field_243257_a : new IBidiRenderer()
        {
            public int func_241863_a(MatrixStack p_241863_1_, int p_241863_2_, int p_241863_3_)
            {
                return this.func_241864_a(p_241863_1_, p_241863_2_, p_241863_3_, 9, 16777215);
            }
            public int func_241864_a(MatrixStack p_241864_1_, int p_241864_2_, int p_241864_3_, int p_241864_4_, int p_241864_5_)
            {
                int i = p_241864_3_;

                for (IBidiRenderer.Entry ibidirenderer$entry : p_243262_1_)
                {
                    p_243262_0_.func_238407_a_(p_241864_1_, ibidirenderer$entry.field_243267_a, (float)(p_241864_2_ - ibidirenderer$entry.field_243268_b / 2), (float)i, p_241864_5_);
                    i += p_241864_4_;
                }

                return i;
            }
            public int func_241865_b(MatrixStack p_241865_1_, int p_241865_2_, int p_241865_3_, int p_241865_4_, int p_241865_5_)
            {
                int i = p_241865_3_;

                for (IBidiRenderer.Entry ibidirenderer$entry : p_243262_1_)
                {
                    p_243262_0_.func_238407_a_(p_241865_1_, ibidirenderer$entry.field_243267_a, (float)p_241865_2_, (float)i, p_241865_5_);
                    i += p_241865_4_;
                }

                return i;
            }
            public int func_241866_c(MatrixStack p_241866_1_, int p_241866_2_, int p_241866_3_, int p_241866_4_, int p_241866_5_)
            {
                int i = p_241866_3_;

                for (IBidiRenderer.Entry ibidirenderer$entry : p_243262_1_)
                {
                    p_243262_0_.func_238422_b_(p_241866_1_, ibidirenderer$entry.field_243267_a, (float)p_241866_2_, (float)i, p_241866_5_);
                    i += p_241866_4_;
                }

                return i;
            }
            public int func_241862_a()
            {
                return p_243262_1_.size();
            }
        };
    }

    int func_241863_a(MatrixStack p_241863_1_, int p_241863_2_, int p_241863_3_);

    int func_241864_a(MatrixStack p_241864_1_, int p_241864_2_, int p_241864_3_, int p_241864_4_, int p_241864_5_);

    int func_241865_b(MatrixStack p_241865_1_, int p_241865_2_, int p_241865_3_, int p_241865_4_, int p_241865_5_);

    int func_241866_c(MatrixStack p_241866_1_, int p_241866_2_, int p_241866_3_, int p_241866_4_, int p_241866_5_);

    int func_241862_a();

    public static class Entry
    {
        private final IReorderingProcessor field_243267_a;
        private final int field_243268_b;

        private Entry(IReorderingProcessor p_i242052_1_, int p_i242052_2_)
        {
            this.field_243267_a = p_i242052_1_;
            this.field_243268_b = p_i242052_2_;
        }
    }
}
