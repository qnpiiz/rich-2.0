package net.minecraft.client.gui.widget.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.PackLoadingManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class ResourcePackList extends ExtendedList<ResourcePackList.ResourcePackEntry>
{
    private static final ResourceLocation field_214367_b = new ResourceLocation("textures/gui/resource_packs.png");
    private static final ITextComponent field_214368_c = new TranslationTextComponent("pack.incompatible");
    private static final ITextComponent field_214369_d = new TranslationTextComponent("pack.incompatible.confirm.title");
    private final ITextComponent field_214370_e;

    public ResourcePackList(Minecraft p_i241200_1_, int p_i241200_2_, int p_i241200_3_, ITextComponent p_i241200_4_)
    {
        super(p_i241200_1_, p_i241200_2_, p_i241200_3_, 32, p_i241200_3_ - 55 + 4, 36);
        this.field_214370_e = p_i241200_4_;
        this.centerListVertically = false;
        this.setRenderHeader(true, (int)(9.0F * 1.5F));
    }

    protected void renderHeader(MatrixStack p_230448_1_, int p_230448_2_, int p_230448_3_, Tessellator p_230448_4_)
    {
        ITextComponent itextcomponent = (new StringTextComponent("")).append(this.field_214370_e).mergeStyle(TextFormatting.UNDERLINE, TextFormatting.BOLD);
        this.minecraft.fontRenderer.func_243248_b(p_230448_1_, itextcomponent, (float)(p_230448_2_ + this.width / 2 - this.minecraft.fontRenderer.getStringPropertyWidth(itextcomponent) / 2), (float)Math.min(this.y0 + 3, p_230448_3_), 16777215);
    }

    public int getRowWidth()
    {
        return this.width;
    }

    protected int getScrollbarPosition()
    {
        return this.x1 - 6;
    }

    public static class ResourcePackEntry extends ExtendedList.AbstractListEntry<ResourcePackList.ResourcePackEntry>
    {
        private ResourcePackList field_214430_c;
        protected final Minecraft field_214428_a;
        protected final Screen field_214429_b;
        private final PackLoadingManager.IPack field_214431_d;
        private final IReorderingProcessor field_243407_e;
        private final IBidiRenderer field_243408_f;
        private final IReorderingProcessor field_244422_g;
        private final IBidiRenderer field_244423_h;

        public ResourcePackEntry(Minecraft p_i241201_1_, ResourcePackList p_i241201_2_, Screen p_i241201_3_, PackLoadingManager.IPack p_i241201_4_)
        {
            this.field_214428_a = p_i241201_1_;
            this.field_214429_b = p_i241201_3_;
            this.field_214431_d = p_i241201_4_;
            this.field_214430_c = p_i241201_2_;
            this.field_243407_e = func_244424_a(p_i241201_1_, p_i241201_4_.func_230462_b_());
            this.field_243408_f = func_244425_b(p_i241201_1_, p_i241201_4_.func_243390_f());
            this.field_244422_g = func_244424_a(p_i241201_1_, ResourcePackList.field_214368_c);
            this.field_244423_h = func_244425_b(p_i241201_1_, p_i241201_4_.func_230460_a_().getDescription());
        }

        private static IReorderingProcessor func_244424_a(Minecraft p_244424_0_, ITextComponent p_244424_1_)
        {
            int i = p_244424_0_.fontRenderer.getStringPropertyWidth(p_244424_1_);

            if (i > 157)
            {
                ITextProperties itextproperties = ITextProperties.func_240655_a_(p_244424_0_.fontRenderer.func_238417_a_(p_244424_1_, 157 - p_244424_0_.fontRenderer.getStringWidth("...")), ITextProperties.func_240652_a_("..."));
                return LanguageMap.getInstance().func_241870_a(itextproperties);
            }
            else
            {
                return p_244424_1_.func_241878_f();
            }
        }

        private static IBidiRenderer func_244425_b(Minecraft p_244425_0_, ITextComponent p_244425_1_)
        {
            return IBidiRenderer.func_243259_a(p_244425_0_.fontRenderer, p_244425_1_, 157, 2);
        }

        public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_)
        {
            PackCompatibility packcompatibility = this.field_214431_d.func_230460_a_();

            if (!packcompatibility.isCompatible())
            {
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                AbstractGui.fill(p_230432_1_, p_230432_4_ - 1, p_230432_3_ - 1, p_230432_4_ + p_230432_5_ - 9, p_230432_3_ + p_230432_6_ + 1, -8978432);
            }

            this.field_214428_a.getTextureManager().bindTexture(this.field_214431_d.func_241868_a());
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 0.0F, 0.0F, 32, 32, 32, 32);
            IReorderingProcessor ireorderingprocessor = this.field_243407_e;
            IBidiRenderer ibidirenderer = this.field_243408_f;

            if (this.func_238920_a_() && (this.field_214428_a.gameSettings.touchscreen || p_230432_9_))
            {
                this.field_214428_a.getTextureManager().bindTexture(ResourcePackList.field_214367_b);
                AbstractGui.fill(p_230432_1_, p_230432_4_, p_230432_3_, p_230432_4_ + 32, p_230432_3_ + 32, -1601138544);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                int i = p_230432_7_ - p_230432_4_;
                int j = p_230432_8_ - p_230432_3_;

                if (!this.field_214431_d.func_230460_a_().isCompatible())
                {
                    ireorderingprocessor = this.field_244422_g;
                    ibidirenderer = this.field_244423_h;
                }

                if (this.field_214431_d.func_238875_m_())
                {
                    if (i < 32)
                    {
                        AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 0.0F, 32.0F, 32, 32, 256, 256);
                    }
                    else
                    {
                        AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 0.0F, 0.0F, 32, 32, 256, 256);
                    }
                }
                else
                {
                    if (this.field_214431_d.func_238876_n_())
                    {
                        if (i < 16)
                        {
                            AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 32.0F, 32.0F, 32, 32, 256, 256);
                        }
                        else
                        {
                            AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 32.0F, 0.0F, 32, 32, 256, 256);
                        }
                    }

                    if (this.field_214431_d.func_230469_o_())
                    {
                        if (i < 32 && i > 16 && j < 16)
                        {
                            AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 96.0F, 32.0F, 32, 32, 256, 256);
                        }
                        else
                        {
                            AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 96.0F, 0.0F, 32, 32, 256, 256);
                        }
                    }

                    if (this.field_214431_d.func_230470_p_())
                    {
                        if (i < 32 && i > 16 && j > 16)
                        {
                            AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 64.0F, 32.0F, 32, 32, 256, 256);
                        }
                        else
                        {
                            AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 64.0F, 0.0F, 32, 32, 256, 256);
                        }
                    }
                }
            }

            this.field_214428_a.fontRenderer.func_238407_a_(p_230432_1_, ireorderingprocessor, (float)(p_230432_4_ + 32 + 2), (float)(p_230432_3_ + 1), 16777215);
            ibidirenderer.func_241865_b(p_230432_1_, p_230432_4_ + 32 + 2, p_230432_3_ + 12, 10, 8421504);
        }

        private boolean func_238920_a_()
        {
            return !this.field_214431_d.func_230465_f_() || !this.field_214431_d.func_230466_g_();
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            double d0 = mouseX - (double)this.field_214430_c.getRowLeft();
            double d1 = mouseY - (double)this.field_214430_c.getRowTop(this.field_214430_c.getEventListeners().indexOf(this));

            if (this.func_238920_a_() && d0 <= 32.0D)
            {
                if (this.field_214431_d.func_238875_m_())
                {
                    PackCompatibility packcompatibility = this.field_214431_d.func_230460_a_();

                    if (packcompatibility.isCompatible())
                    {
                        this.field_214431_d.func_230471_h_();
                    }
                    else
                    {
                        ITextComponent itextcomponent = packcompatibility.getConfirmMessage();
                        this.field_214428_a.displayGuiScreen(new ConfirmScreen((p_238921_1_) ->
                        {
                            this.field_214428_a.displayGuiScreen(this.field_214429_b);

                            if (p_238921_1_)
                            {
                                this.field_214431_d.func_230471_h_();
                            }
                        }, ResourcePackList.field_214369_d, itextcomponent));
                    }

                    return true;
                }

                if (d0 < 16.0D && this.field_214431_d.func_238876_n_())
                {
                    this.field_214431_d.func_230472_i_();
                    return true;
                }

                if (d0 > 16.0D && d1 < 16.0D && this.field_214431_d.func_230469_o_())
                {
                    this.field_214431_d.func_230467_j_();
                    return true;
                }

                if (d0 > 16.0D && d1 > 16.0D && this.field_214431_d.func_230470_p_())
                {
                    this.field_214431_d.func_230468_k_();
                    return true;
                }
            }

            return false;
        }
    }
}
