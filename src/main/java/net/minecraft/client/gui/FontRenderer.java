package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.fonts.EmptyGlyph;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ICharacterConsumer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.CharacterManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextProcessing;
import net.optifine.render.GlBlendState;
import net.optifine.util.GlyphAdvanceFixed;
import net.optifine.util.GuiPoint;
import fun.rich.utils.render.RenderUtils;

public class FontRenderer
{
    private static final Vector3f field_238401_c_ = new Vector3f(0.0F, 0.0F, 0.03F);
    public final int FONT_HEIGHT = 9;
    public final Random random = new Random();
    private final Function<ResourceLocation, Font> font;
    private final CharacterManager field_238402_e_;
    private boolean blend = false;
    private GlBlendState oldBlendState = new GlBlendState();
    private IGlyph glyphAdvanceSpace = new GlyphAdvanceFixed(4.0F);

    public FontRenderer(Function<ResourceLocation, Font> p_i232249_1_)
    {
        this.font = p_i232249_1_;
        this.field_238402_e_ = new CharacterManager((p_lambda$new$0_1_, p_lambda$new$0_2_) ->
        {
            return this.getFont(p_lambda$new$0_2_.getFontId()).func_238557_a_(p_lambda$new$0_1_).getAdvance(p_lambda$new$0_2_.getBold());
        });
    }

    private Font getFont(ResourceLocation fontLocation)
    {
        return this.font.apply(fontLocation);
    }

    public void drawBlurredStringWithShadow(String text, double x, double y, int blurRadius, Color blurColor, int color, MatrixStack matrixStack) {
        RenderUtils.setColor(-1);
        RenderUtils.drawBlurredShadow((int) x, (int) y, (int) getStringWidth(text), (int) FONT_HEIGHT, blurRadius, blurColor);
        drawStringWithShadow(matrixStack, text, (float) x, (float) y, color);
    }

    public void drawBlurredString(String text, double x, double y, int blurRadius, Color blurColor, int color, MatrixStack matrixStack) {
        RenderUtils.setColor(-1);
        RenderUtils.drawBlurredShadow((int) x, (int) y, (int) getStringWidth(text), (int) FONT_HEIGHT, blurRadius, blurColor);
        drawString(matrixStack, text, (float) x, (float) y, color);
    }

    public void drawCenteredBlurredString(String text, double x, double y, int blurRadius, Color blurColor, int color, MatrixStack matrixStack) {
        RenderUtils.setColor(-1);
        RenderUtils.drawBlurredShadow((int) ((int) x - (float)this.getStringWidth(text) / 2.0f), (int) y, (int) getStringWidth(text), (int) FONT_HEIGHT, blurRadius, blurColor);
        drawString(matrixStack, text, (float) (x - this.getStringWidth(text) / 2F), (float) y, color);
    }

    public void drawStringWithOutline(String text, float x, float y, int color, MatrixStack matrixStack) {
        drawString(matrixStack, text, x - 0.5F, y, java.awt.Color.BLACK.getRGB());
        drawString(matrixStack, text, x + 0.5F, y, java.awt.Color.BLACK.getRGB());
        drawString(matrixStack, text, x, y - 0.5F, java.awt.Color.BLACK.getRGB());
        drawString(matrixStack, text, x, y + 0.5F, java.awt.Color.BLACK.getRGB());
        drawString(matrixStack, text, x, y, color);
    }

    public int drawStringWithShadow(MatrixStack p_238405_1_, String p_238405_2_, float p_238405_3_, float p_238405_4_, int p_238405_5_)
    {
        return this.renderString(p_238405_2_, p_238405_3_, p_238405_4_, p_238405_5_, p_238405_1_.getLast().getMatrix(), true, this.getBidiFlag());
    }

    public int func_238406_a_(MatrixStack p_238406_1_, String p_238406_2_, float p_238406_3_, float p_238406_4_, int p_238406_5_, boolean p_238406_6_)
    {
        RenderSystem.enableAlphaTest();
        return this.renderString(p_238406_2_, p_238406_3_, p_238406_4_, p_238406_5_, p_238406_1_.getLast().getMatrix(), true, p_238406_6_);
    }

    public int drawString(MatrixStack matrixStack, String text, float x, float y, int color)
    {
        RenderSystem.enableAlphaTest();
        return this.renderString(text, x, y, color, matrixStack.getLast().getMatrix(), false, this.getBidiFlag());
    }

    public int func_238407_a_(MatrixStack p_238407_1_, IReorderingProcessor p_238407_2_, float p_238407_3_, float p_238407_4_, int p_238407_5_)
    {
        RenderSystem.enableAlphaTest();
        return this.func_238415_a_(p_238407_2_, p_238407_3_, p_238407_4_, p_238407_5_, p_238407_1_.getLast().getMatrix(), true);
    }

    public int func_243246_a(MatrixStack p_243246_1_, ITextComponent p_243246_2_, float p_243246_3_, float p_243246_4_, int p_243246_5_)
    {
        RenderSystem.enableAlphaTest();
        return this.func_238415_a_(p_243246_2_.func_241878_f(), p_243246_3_, p_243246_4_, p_243246_5_, p_243246_1_.getLast().getMatrix(), true);
    }

    public int func_238422_b_(MatrixStack matrixStack, IReorderingProcessor properties, float x, float y, int color)
    {
        RenderSystem.enableAlphaTest();
        return this.func_238415_a_(properties, x, y, color, matrixStack.getLast().getMatrix(), false);
    }

    public int func_243248_b(MatrixStack p_243248_1_, ITextComponent p_243248_2_, float p_243248_3_, float p_243248_4_, int p_243248_5_)
    {
        RenderSystem.enableAlphaTest();
        return this.func_238415_a_(p_243248_2_.func_241878_f(), p_243248_3_, p_243248_4_, p_243248_5_, p_243248_1_.getLast().getMatrix(), false);
    }

    /**
     * Apply Unicode Bidirectional Algorithm to string and return a new possibly reordered string for visual rendering.
     */
    public String bidiReorder(String text)
    {
        try
        {
            Bidi bidi = new Bidi((new ArabicShaping(8)).shape(text), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        }
        catch (ArabicShapingException arabicshapingexception1)
        {
            return text;
        }
    }

    private int renderString(String text, float x, float y, int color, Matrix4f matrix, boolean dropShadow, boolean p_228078_7_)
    {
        if (text == null)
        {
            return 0;
        }
        else
        {
            IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
            int i = this.func_238411_a_(text, x, y, color, dropShadow, matrix, irendertypebuffer$impl, false, 0, 15728880, p_228078_7_);
            irendertypebuffer$impl.finish();
            return i;
        }
    }

    public void renderStrings(List<String> p_renderStrings_1_, GuiPoint[] p_renderStrings_2_, int p_renderStrings_3_, Matrix4f p_renderStrings_4_, boolean p_renderStrings_5_, boolean p_renderStrings_6_)
    {
        IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());

        for (int i = 0; i < p_renderStrings_1_.size(); ++i)
        {
            String s = p_renderStrings_1_.get(i);

            if (s != null && !s.isEmpty())
            {
                GuiPoint guipoint = p_renderStrings_2_[i];

                if (guipoint != null)
                {
                    float f = (float)guipoint.getX();
                    float f1 = (float)guipoint.getY();
                    this.func_238411_a_(s, f, f1, p_renderStrings_3_, p_renderStrings_5_, p_renderStrings_4_, irendertypebuffer$impl, false, 0, 15728880, p_renderStrings_6_);
                }
            }
        }

        irendertypebuffer$impl.finish();
    }

    private int func_238415_a_(IReorderingProcessor reorderingProcessor, float x, float y, int color, Matrix4f matrix, boolean p_238415_6_)
    {
        IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        int i = this.func_238416_a_(reorderingProcessor, x, y, color, p_238415_6_, matrix, irendertypebuffer$impl, false, 0, 15728880);
        irendertypebuffer$impl.finish();
        return i;
    }

    public int renderString(String text, float x, float y, int color, boolean dropShadow, Matrix4f matrix, IRenderTypeBuffer buffer, boolean transparentIn, int colorBackgroundIn, int packedLight)
    {
        return this.func_238411_a_(text, x, y, color, dropShadow, matrix, buffer, transparentIn, colorBackgroundIn, packedLight, this.getBidiFlag());
    }

    public int func_238411_a_(String p_238411_1_, float p_238411_2_, float p_238411_3_, int p_238411_4_, boolean p_238411_5_, Matrix4f p_238411_6_, IRenderTypeBuffer p_238411_7_, boolean p_238411_8_, int p_238411_9_, int p_238411_10_, boolean p_238411_11_)
    {
        return this.func_238423_b_(p_238411_1_, p_238411_2_, p_238411_3_, p_238411_4_, p_238411_5_, p_238411_6_, p_238411_7_, p_238411_8_, p_238411_9_, p_238411_10_, p_238411_11_);
    }

    public int func_243247_a(ITextComponent p_243247_1_, float p_243247_2_, float p_243247_3_, int p_243247_4_, boolean p_243247_5_, Matrix4f p_243247_6_, IRenderTypeBuffer p_243247_7_, boolean p_243247_8_, int p_243247_9_, int p_243247_10_)
    {
        return this.func_238416_a_(p_243247_1_.func_241878_f(), p_243247_2_, p_243247_3_, p_243247_4_, p_243247_5_, p_243247_6_, p_243247_7_, p_243247_8_, p_243247_9_, p_243247_10_);
    }

    public int func_238416_a_(IReorderingProcessor processor, float x, float y, int color, boolean dropShadow, Matrix4f matrix, IRenderTypeBuffer buffer, boolean transparent, int colorBackground, int packedLight)
    {
        return this.func_238424_b_(processor, x, y, color, dropShadow, matrix, buffer, transparent, colorBackground, packedLight);
    }

    private static int func_238403_a_(int p_238403_0_)
    {
        return (p_238403_0_ & -67108864) == 0 ? p_238403_0_ | -16777216 : p_238403_0_;
    }

    private int func_238423_b_(String text, float x, float y, int color, boolean p_238423_5_, Matrix4f matrix, IRenderTypeBuffer buffer, boolean transparent, int p_238423_9_, int p_238423_10_, boolean p_238423_11_)
    {
        if (p_238423_11_)
        {
            text = this.bidiReorder(text);
        }

        color = func_238403_a_(color);
        Matrix4f matrix4f = matrix.copy();

        if (p_238423_5_)
        {
            this.renderStringAtPos(text, x, y, color, true, matrix, buffer, transparent, p_238423_9_, p_238423_10_);
            matrix4f.translate(field_238401_c_);
        }

        x = this.renderStringAtPos(text, x, y, color, false, matrix4f, buffer, transparent, p_238423_9_, p_238423_10_);
        return (int)x + (p_238423_5_ ? 1 : 0);
    }

    private int func_238424_b_(IReorderingProcessor p_238424_1_, float x, float y, int color, boolean p_238424_5_, Matrix4f matrix, IRenderTypeBuffer buffer, boolean p_238424_8_, int p_238424_9_, int p_238424_10_)
    {
        color = func_238403_a_(color);
        Matrix4f matrix4f = matrix.copy();

        if (p_238424_5_)
        {
            this.func_238426_c_(p_238424_1_, x, y, color, true, matrix, buffer, p_238424_8_, p_238424_9_, p_238424_10_);
            matrix4f.translate(field_238401_c_);
        }

        x = this.func_238426_c_(p_238424_1_, x, y, color, false, matrix4f, buffer, p_238424_8_, p_238424_9_, p_238424_10_);
        return (int)x + (p_238424_5_ ? 1 : 0);
    }

    private float renderStringAtPos(String text, float x, float y, int color, boolean isShadow, Matrix4f matrix, IRenderTypeBuffer buffer, boolean isTransparent, int colorBackgroundIn, int packedLight)
    {
        FontRenderer.CharacterRenderer fontrenderer$characterrenderer = new FontRenderer.CharacterRenderer(buffer, x, y, color, isShadow, matrix, isTransparent, packedLight);
        TextProcessing.func_238346_c_(text, Style.EMPTY, fontrenderer$characterrenderer);
        return fontrenderer$characterrenderer.func_238441_a_(colorBackgroundIn, x);
    }

    private float func_238426_c_(IReorderingProcessor p_238426_1_, float p_238426_2_, float p_238426_3_, int p_238426_4_, boolean p_238426_5_, Matrix4f p_238426_6_, IRenderTypeBuffer p_238426_7_, boolean p_238426_8_, int p_238426_9_, int p_238426_10_)
    {
        FontRenderer.CharacterRenderer fontrenderer$characterrenderer = new FontRenderer.CharacterRenderer(p_238426_7_, p_238426_2_, p_238426_3_, p_238426_4_, p_238426_5_, p_238426_6_, p_238426_8_, p_238426_10_);
        p_238426_1_.accept(fontrenderer$characterrenderer);
        return fontrenderer$characterrenderer.func_238441_a_(p_238426_9_, p_238426_2_);
    }

    private void drawGlyph(TexturedGlyph glyphIn, boolean boldIn, boolean italicIn, float boldOffsetIn, float xIn, float yIn, Matrix4f matrix, IVertexBuilder bufferIn, float redIn, float greenIn, float blueIn, float alphaIn, int packedLight)
    {
        glyphIn.render(italicIn, xIn, yIn, matrix, bufferIn, redIn, greenIn, blueIn, alphaIn, packedLight);

        if (boldIn)
        {
            glyphIn.render(italicIn, xIn + boldOffsetIn, yIn, matrix, bufferIn, redIn, greenIn, blueIn, alphaIn, packedLight);
        }
    }

    /**
     * Returns the width of this string. Equivalent of FontMetrics.stringWidth(String s).
     */
    public int getStringWidth(String text)
    {
        return MathHelper.ceil(this.field_238402_e_.func_238350_a_(text));
    }

    public int getStringPropertyWidth(ITextProperties properties)
    {
        return MathHelper.ceil(this.field_238402_e_.func_238356_a_(properties));
    }

    public int func_243245_a(IReorderingProcessor processor)
    {
        return MathHelper.ceil(this.field_238402_e_.func_243238_a(processor));
    }

    public String func_238413_a_(String p_238413_1_, int p_238413_2_, boolean p_238413_3_)
    {
        return p_238413_3_ ? this.field_238402_e_.func_238364_c_(p_238413_1_, p_238413_2_, Style.EMPTY) : this.field_238402_e_.func_238361_b_(p_238413_1_, p_238413_2_, Style.EMPTY);
    }

    public String func_238412_a_(String p_238412_1_, int p_238412_2_)
    {
        return this.field_238402_e_.func_238361_b_(p_238412_1_, p_238412_2_, Style.EMPTY);
    }

    public ITextProperties func_238417_a_(ITextProperties properties, int maxLength)
    {
        return this.field_238402_e_.func_238358_a_(properties, maxLength, Style.EMPTY);
    }

    public void func_238418_a_(ITextProperties text, int x, int y, int maxLength, int color)
    {
        Matrix4f matrix4f = TransformationMatrix.identity().getMatrix();

        for (IReorderingProcessor ireorderingprocessor : this.trimStringToWidth(text, maxLength))
        {
            this.func_238415_a_(ireorderingprocessor, (float)x, (float)y, color, matrix4f, false);
            y += 9;
        }
    }

    /**
     * Returns the height (in pixels) of the given string if it is wordwrapped to the given max width.
     */
    public int getWordWrappedHeight(String str, int maxLength)
    {
        return 9 * this.field_238402_e_.func_238365_g_(str, maxLength, Style.EMPTY).size();
    }

    public List<IReorderingProcessor> trimStringToWidth(ITextProperties p_238425_1_, int p_238425_2_)
    {
        return LanguageMap.getInstance().func_244260_a(this.field_238402_e_.func_238362_b_(p_238425_1_, p_238425_2_, Style.EMPTY));
    }

    /**
     * Get bidiFlag that controls if the Unicode Bidirectional Algorithm should be run before rendering any string
     */
    public boolean getBidiFlag()
    {
        return LanguageMap.getInstance().func_230505_b_();
    }

    public CharacterManager getCharacterManager()
    {
        return this.field_238402_e_;
    }

    public void drawCenteredString(String text, float x, float y, int color, MatrixStack matrixStack) {
        this.drawStringWithShadow(matrixStack, text, x - this.getStringWidth(text) / 2F, y, color);
    }

    class CharacterRenderer implements ICharacterConsumer
    {
        final IRenderTypeBuffer field_238427_a_;
        private final boolean field_238429_c_;
        private final float field_238430_d_;
        private final float field_238431_e_;
        private final float field_238432_f_;
        private final float field_238433_g_;
        private final float field_238434_h_;
        private final Matrix4f field_238435_i_;
        private final boolean field_238436_j_;
        private final int field_238437_k_;
        private float field_238438_l_;
        private float field_238439_m_;
        @Nullable
        private List<TexturedGlyph.Effect> field_238440_n_;
        private Style lastStyle;
        private Font lastStyleFont;

        private void func_238442_a_(TexturedGlyph.Effect p_238442_1_)
        {
            if (this.field_238440_n_ == null)
            {
                this.field_238440_n_ = Lists.newArrayList();
            }

            this.field_238440_n_.add(p_238442_1_);
        }

        public CharacterRenderer(IRenderTypeBuffer p_i232250_2_, float p_i232250_3_, float p_i232250_4_, int p_i232250_5_, boolean p_i232250_6_, Matrix4f p_i232250_7_, boolean p_i232250_8_, int p_i232250_9_)
        {
            this.field_238427_a_ = p_i232250_2_;
            this.field_238438_l_ = p_i232250_3_;
            this.field_238439_m_ = p_i232250_4_;
            this.field_238429_c_ = p_i232250_6_;
            this.field_238430_d_ = p_i232250_6_ ? 0.25F : 1.0F;
            this.field_238431_e_ = (float)(p_i232250_5_ >> 16 & 255) / 255.0F * this.field_238430_d_;
            this.field_238432_f_ = (float)(p_i232250_5_ >> 8 & 255) / 255.0F * this.field_238430_d_;
            this.field_238433_g_ = (float)(p_i232250_5_ & 255) / 255.0F * this.field_238430_d_;
            this.field_238434_h_ = (float)(p_i232250_5_ >> 24 & 255) / 255.0F;
            this.field_238435_i_ = p_i232250_7_.isIdentity() ? TexturedGlyph.MATRIX_IDENTITY : p_i232250_7_;
            this.field_238436_j_ = p_i232250_8_;
            this.field_238437_k_ = p_i232250_9_;
        }

        public boolean accept(int p_accept_1_, Style p_accept_2_, int p_accept_3_)
        {
            Font font = this.getFont(p_accept_2_);
            IGlyph iglyph = font.func_238557_a_(p_accept_3_);
            TexturedGlyph texturedglyph = p_accept_2_.getObfuscated() && p_accept_3_ != 32 ? font.obfuscate(iglyph) : font.func_238559_b_(p_accept_3_);
            boolean flag = p_accept_2_.getBold();
            float f = this.field_238434_h_;
            net.minecraft.util.text.Color color = p_accept_2_.getColor();
            float f1;
            float f2;
            float f3;

            if (color != null)
            {
                int i = color.getColor();
                f1 = (float)(i >> 16 & 255) / 255.0F * this.field_238430_d_;
                f2 = (float)(i >> 8 & 255) / 255.0F * this.field_238430_d_;
                f3 = (float)(i & 255) / 255.0F * this.field_238430_d_;
            }
            else
            {
                f1 = this.field_238431_e_;
                f2 = this.field_238432_f_;
                f3 = this.field_238433_g_;
            }

            if (!(texturedglyph instanceof EmptyGlyph))
            {
                float f5 = flag ? iglyph.getBoldOffset() : 0.0F;
                float f4 = this.field_238429_c_ ? iglyph.getShadowOffset() : 0.0F;
                IVertexBuilder ivertexbuilder = this.field_238427_a_.getBuffer(texturedglyph.getRenderType(this.field_238436_j_));
                FontRenderer.this.drawGlyph(texturedglyph, flag, p_accept_2_.getItalic(), f5, this.field_238438_l_ + f4, this.field_238439_m_ + f4, this.field_238435_i_, ivertexbuilder, f1, f2, f3, f, this.field_238437_k_);
            }

            float f6 = iglyph.getAdvance(flag);
            float f7 = this.field_238429_c_ ? 1.0F : 0.0F;

            if (p_accept_2_.getStrikethrough())
            {
                this.func_238442_a_(new TexturedGlyph.Effect(this.field_238438_l_ + f7 - 1.0F, this.field_238439_m_ + f7 + 4.5F, this.field_238438_l_ + f7 + f6, this.field_238439_m_ + f7 + 4.5F - 1.0F, 0.01F, f1, f2, f3, f));
            }

            if (p_accept_2_.getUnderlined())
            {
                this.func_238442_a_(new TexturedGlyph.Effect(this.field_238438_l_ + f7 - 1.0F, this.field_238439_m_ + f7 + 9.0F, this.field_238438_l_ + f7 + f6, this.field_238439_m_ + f7 + 9.0F - 1.0F, 0.01F, f1, f2, f3, f));
            }

            this.field_238438_l_ += f6;
            return true;
        }

        public float func_238441_a_(int p_238441_1_, float p_238441_2_)
        {
            if (p_238441_1_ != 0)
            {
                float f = (float)(p_238441_1_ >> 24 & 255) / 255.0F;
                float f1 = (float)(p_238441_1_ >> 16 & 255) / 255.0F;
                float f2 = (float)(p_238441_1_ >> 8 & 255) / 255.0F;
                float f3 = (float)(p_238441_1_ & 255) / 255.0F;
                this.func_238442_a_(new TexturedGlyph.Effect(p_238441_2_ - 1.0F, this.field_238439_m_ + 9.0F, this.field_238438_l_ + 1.0F, this.field_238439_m_ - 1.0F, 0.01F, f1, f2, f3, f));
            }

            if (this.field_238440_n_ != null)
            {
                TexturedGlyph texturedglyph = FontRenderer.this.getFont(Style.DEFAULT_FONT).getWhiteGlyph();
                IVertexBuilder ivertexbuilder = this.field_238427_a_.getBuffer(texturedglyph.getRenderType(this.field_238436_j_));

                for (TexturedGlyph.Effect texturedglyph$effect : this.field_238440_n_)
                {
                    texturedglyph.renderEffect(texturedglyph$effect, this.field_238435_i_, ivertexbuilder, this.field_238437_k_);
                }
            }

            return this.field_238438_l_;
        }

        private Font getFont(Style p_getFont_1_)
        {
            if (p_getFont_1_ == this.lastStyle)
            {
                return this.lastStyleFont;
            }
            else
            {
                this.lastStyle = p_getFont_1_;
                this.lastStyleFont = FontRenderer.this.getFont(p_getFont_1_.getFontId());
                return this.lastStyleFont;
            }
        }
    }
}
