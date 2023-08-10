package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.TextRenderingUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsSelectWorldTemplateScreen extends RealmsScreen
{
    private static final Logger field_224515_a = LogManager.getLogger();
    private static final ResourceLocation field_237987_b_ = new ResourceLocation("realms", "textures/gui/realms/link_icons.png");
    private static final ResourceLocation field_237988_c_ = new ResourceLocation("realms", "textures/gui/realms/trailer_icons.png");
    private static final ResourceLocation field_237989_p_ = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
    private static final ITextComponent field_243163_q = new TranslationTextComponent("mco.template.info.tooltip");
    private static final ITextComponent field_243164_r = new TranslationTextComponent("mco.template.trailer.tooltip");
    private final NotifableRealmsScreen field_224516_b;
    private RealmsSelectWorldTemplateScreen.WorldTemplateSelectionList field_224517_c;
    private int field_224518_d = -1;
    private ITextComponent field_224519_e;
    private Button field_224520_f;
    private Button field_224521_g;
    private Button field_224522_h;
    @Nullable
    private ITextComponent field_224523_i;
    private String field_224524_j;
    private final RealmsServer.ServerType field_224525_k;
    private int field_224526_l;
    @Nullable
    private ITextComponent[] field_224527_m;
    private String field_224528_n;
    private boolean field_224529_o;
    private boolean field_224530_p;
    @Nullable
    private List<TextRenderingUtils.Line> field_224531_q;

    public RealmsSelectWorldTemplateScreen(NotifableRealmsScreen p_i51752_1_, RealmsServer.ServerType p_i51752_2_)
    {
        this(p_i51752_1_, p_i51752_2_, (WorldTemplatePaginatedList)null);
    }

    public RealmsSelectWorldTemplateScreen(NotifableRealmsScreen p_i51753_1_, RealmsServer.ServerType p_i51753_2_, @Nullable WorldTemplatePaginatedList p_i51753_3_)
    {
        this.field_224516_b = p_i51753_1_;
        this.field_224525_k = p_i51753_2_;

        if (p_i51753_3_ == null)
        {
            this.field_224517_c = new RealmsSelectWorldTemplateScreen.WorldTemplateSelectionList();
            this.func_224497_a(new WorldTemplatePaginatedList(10));
        }
        else
        {
            this.field_224517_c = new RealmsSelectWorldTemplateScreen.WorldTemplateSelectionList(Lists.newArrayList(p_i51753_3_.field_230657_a_));
            this.func_224497_a(p_i51753_3_);
        }

        this.field_224519_e = new TranslationTextComponent("mco.template.title");
    }

    public void func_238001_a_(ITextComponent p_238001_1_)
    {
        this.field_224519_e = p_238001_1_;
    }

    public void func_238002_a_(ITextComponent... p_238002_1_)
    {
        this.field_224527_m = p_238002_1_;
        this.field_224529_o = true;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.field_224530_p && this.field_224528_n != null)
        {
            Util.getOSType().openURI("https://www.minecraft.net/realms/adventure-maps-in-1-9");
            return true;
        }
        else
        {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void init()
    {
        this.mc.keyboardListener.enableRepeatEvents(true);
        this.field_224517_c = new RealmsSelectWorldTemplateScreen.WorldTemplateSelectionList(this.field_224517_c.func_223879_b());
        this.field_224521_g = this.addButton(new Button(this.width / 2 - 206, this.height - 32, 100, 20, new TranslationTextComponent("mco.template.button.trailer"), (p_238011_1_) ->
        {
            this.func_224496_i();
        }));
        this.field_224520_f = this.addButton(new Button(this.width / 2 - 100, this.height - 32, 100, 20, new TranslationTextComponent("mco.template.button.select"), (p_238008_1_) ->
        {
            this.func_224500_h();
        }));
        ITextComponent itextcomponent = this.field_224525_k == RealmsServer.ServerType.MINIGAME ? DialogTexts.GUI_CANCEL : DialogTexts.GUI_BACK;
        Button button = new Button(this.width / 2 + 6, this.height - 32, 100, 20, itextcomponent, (p_238006_1_) ->
        {
            this.func_224484_g();
        });
        this.addButton(button);
        this.field_224522_h = this.addButton(new Button(this.width / 2 + 112, this.height - 32, 100, 20, new TranslationTextComponent("mco.template.button.publisher"), (p_238000_1_) ->
        {
            this.func_224511_j();
        }));
        this.field_224520_f.active = false;
        this.field_224521_g.visible = false;
        this.field_224522_h.visible = false;
        this.addListener(this.field_224517_c);
        this.setListenerDefault(this.field_224517_c);
        Stream<ITextComponent> stream = Stream.of(this.field_224519_e);

        if (this.field_224527_m != null)
        {
            stream = Stream.concat(Stream.of(this.field_224527_m), stream);
        }

        RealmsNarratorHelper.func_239549_a_(stream.filter(Objects::nonNull).map(ITextComponent::getString).collect(Collectors.toList()));
    }

    private void func_224514_b()
    {
        this.field_224522_h.visible = this.func_224510_d();
        this.field_224521_g.visible = this.func_224512_f();
        this.field_224520_f.active = this.func_224495_c();
    }

    private boolean func_224495_c()
    {
        return this.field_224518_d != -1;
    }

    private boolean func_224510_d()
    {
        return this.field_224518_d != -1 && !this.func_224487_e().field_230651_e_.isEmpty();
    }

    private WorldTemplate func_224487_e()
    {
        return this.field_224517_c.func_223877_a(this.field_224518_d);
    }

    private boolean func_224512_f()
    {
        return this.field_224518_d != -1 && !this.func_224487_e().field_230653_g_.isEmpty();
    }

    public void tick()
    {
        super.tick();
        --this.field_224526_l;

        if (this.field_224526_l < 0)
        {
            this.field_224526_l = 0;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.func_224484_g();
            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    private void func_224484_g()
    {
        this.field_224516_b.func_223627_a_((WorldTemplate)null);
        this.mc.displayGuiScreen(this.field_224516_b);
    }

    private void func_224500_h()
    {
        if (this.func_238024_y_())
        {
            this.field_224516_b.func_223627_a_(this.func_224487_e());
        }
    }

    private boolean func_238024_y_()
    {
        return this.field_224518_d >= 0 && this.field_224518_d < this.field_224517_c.getItemCount();
    }

    private void func_224496_i()
    {
        if (this.func_238024_y_())
        {
            WorldTemplate worldtemplate = this.func_224487_e();

            if (!"".equals(worldtemplate.field_230653_g_))
            {
                Util.getOSType().openURI(worldtemplate.field_230653_g_);
            }
        }
    }

    private void func_224511_j()
    {
        if (this.func_238024_y_())
        {
            WorldTemplate worldtemplate = this.func_224487_e();

            if (!"".equals(worldtemplate.field_230651_e_))
            {
                Util.getOSType().openURI(worldtemplate.field_230651_e_);
            }
        }
    }

    private void func_224497_a(final WorldTemplatePaginatedList p_224497_1_)
    {
        (new Thread("realms-template-fetcher")
        {
            public void run()
            {
                WorldTemplatePaginatedList worldtemplatepaginatedlist = p_224497_1_;
                RealmsClient realmsclient = RealmsClient.func_224911_a();

                while (worldtemplatepaginatedlist != null)
                {
                    Either<WorldTemplatePaginatedList, String> either = RealmsSelectWorldTemplateScreen.this.func_224509_a(worldtemplatepaginatedlist, realmsclient);
                    worldtemplatepaginatedlist = RealmsSelectWorldTemplateScreen.this.mc.supplyAsync(() ->
                    {
                        if (either.right().isPresent())
                        {
                            RealmsSelectWorldTemplateScreen.field_224515_a.error("Couldn't fetch templates: {}", either.right().get());

                            if (RealmsSelectWorldTemplateScreen.this.field_224517_c.func_223878_a())
                            {
                                RealmsSelectWorldTemplateScreen.this.field_224531_q = TextRenderingUtils.func_225224_a(I18n.format("mco.template.select.failure"));
                            }

                            return null;
                        }
                        else {
                            WorldTemplatePaginatedList worldtemplatepaginatedlist1 = either.left().get();

                            for (WorldTemplate worldtemplate : worldtemplatepaginatedlist1.field_230657_a_)
                            {
                                RealmsSelectWorldTemplateScreen.this.field_224517_c.func_223876_a(worldtemplate);
                            }

                            if (worldtemplatepaginatedlist1.field_230657_a_.isEmpty())
                            {
                                if (RealmsSelectWorldTemplateScreen.this.field_224517_c.func_223878_a())
                                {
                                    String s = I18n.format("mco.template.select.none", "%link");
                                    TextRenderingUtils.LineSegment textrenderingutils$linesegment = TextRenderingUtils.LineSegment.func_225214_a(I18n.format("mco.template.select.none.linkTitle"), "https://aka.ms/MinecraftRealmsContentCreator");
                                    RealmsSelectWorldTemplateScreen.this.field_224531_q = TextRenderingUtils.func_225224_a(s, textrenderingutils$linesegment);
                                }

                                return null;
                            }
                            else {
                                return worldtemplatepaginatedlist1;
                            }
                        }
                    }).join();
                }
            }
        }).start();
    }

    private Either<WorldTemplatePaginatedList, String> func_224509_a(WorldTemplatePaginatedList p_224509_1_, RealmsClient p_224509_2_)
    {
        try
        {
            return Either.left(p_224509_2_.func_224930_a(p_224509_1_.field_230658_b_ + 1, p_224509_1_.field_230659_c_, this.field_224525_k));
        }
        catch (RealmsServiceException realmsserviceexception)
        {
            return Either.right(realmsserviceexception.getMessage());
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.field_224523_i = null;
        this.field_224524_j = null;
        this.field_224530_p = false;
        this.renderBackground(matrixStack);
        this.field_224517_c.render(matrixStack, mouseX, mouseY, partialTicks);

        if (this.field_224531_q != null)
        {
            this.func_237992_a_(matrixStack, mouseX, mouseY, this.field_224531_q);
        }

        drawCenteredString(matrixStack, this.font, this.field_224519_e, this.width / 2, 13, 16777215);

        if (this.field_224529_o)
        {
            ITextComponent[] aitextcomponent = this.field_224527_m;

            for (int i = 0; i < aitextcomponent.length; ++i)
            {
                int j = this.font.getStringPropertyWidth(aitextcomponent[i]);
                int k = this.width / 2 - j / 2;
                int l = func_239562_k_(-1 + i);

                if (mouseX >= k && mouseX <= k + j && mouseY >= l && mouseY <= l + 9)
                {
                    this.field_224530_p = true;
                }
            }

            for (int i1 = 0; i1 < aitextcomponent.length; ++i1)
            {
                ITextComponent itextcomponent = aitextcomponent[i1];
                int j1 = 10526880;

                if (this.field_224528_n != null)
                {
                    if (this.field_224530_p)
                    {
                        j1 = 7107012;
                        itextcomponent = itextcomponent.deepCopy().mergeStyle(TextFormatting.STRIKETHROUGH);
                    }
                    else
                    {
                        j1 = 3368635;
                    }
                }

                drawCenteredString(matrixStack, this.font, itextcomponent, this.width / 2, func_239562_k_(-1 + i1), j1);
            }
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.func_237993_a_(matrixStack, this.field_224523_i, mouseX, mouseY);
    }

    private void func_237992_a_(MatrixStack p_237992_1_, int p_237992_2_, int p_237992_3_, List<TextRenderingUtils.Line> p_237992_4_)
    {
        for (int i = 0; i < p_237992_4_.size(); ++i)
        {
            TextRenderingUtils.Line textrenderingutils$line = p_237992_4_.get(i);
            int j = func_239562_k_(4 + i);
            int k = textrenderingutils$line.field_225213_a.stream().mapToInt((p_237999_1_) ->
            {
                return this.font.getStringWidth(p_237999_1_.func_225215_a());
            }).sum();
            int l = this.width / 2 - k / 2;

            for (TextRenderingUtils.LineSegment textrenderingutils$linesegment : textrenderingutils$line.field_225213_a)
            {
                int i1 = textrenderingutils$linesegment.func_225217_b() ? 3368635 : 16777215;
                int j1 = this.font.drawStringWithShadow(p_237992_1_, textrenderingutils$linesegment.func_225215_a(), (float)l, (float)j, i1);

                if (textrenderingutils$linesegment.func_225217_b() && p_237992_2_ > l && p_237992_2_ < j1 && p_237992_3_ > j - 3 && p_237992_3_ < j + 8)
                {
                    this.field_224523_i = new StringTextComponent(textrenderingutils$linesegment.func_225216_c());
                    this.field_224524_j = textrenderingutils$linesegment.func_225216_c();
                }

                l = j1;
            }
        }
    }

    protected void func_237993_a_(MatrixStack p_237993_1_, @Nullable ITextComponent p_237993_2_, int p_237993_3_, int p_237993_4_)
    {
        if (p_237993_2_ != null)
        {
            int i = p_237993_3_ + 12;
            int j = p_237993_4_ - 12;
            int k = this.font.getStringPropertyWidth(p_237993_2_);
            this.fillGradient(p_237993_1_, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
            this.font.func_243246_a(p_237993_1_, p_237993_2_, (float)i, (float)j, 16777215);
        }
    }

    class WorldTemplateSelectionEntry extends ExtendedList.AbstractListEntry<RealmsSelectWorldTemplateScreen.WorldTemplateSelectionEntry>
    {
        private final WorldTemplate field_223756_a;

        public WorldTemplateSelectionEntry(WorldTemplate p_i51724_2_)
        {
            this.field_223756_a = p_i51724_2_;
        }

        public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_)
        {
            this.func_238029_a_(p_230432_1_, this.field_223756_a, p_230432_4_, p_230432_3_, p_230432_7_, p_230432_8_);
        }

        private void func_238029_a_(MatrixStack p_238029_1_, WorldTemplate p_238029_2_, int p_238029_3_, int p_238029_4_, int p_238029_5_, int p_238029_6_)
        {
            int i = p_238029_3_ + 45 + 20;
            RealmsSelectWorldTemplateScreen.this.font.drawString(p_238029_1_, p_238029_2_.field_230648_b_, (float)i, (float)(p_238029_4_ + 2), 16777215);
            RealmsSelectWorldTemplateScreen.this.font.drawString(p_238029_1_, p_238029_2_.field_230650_d_, (float)i, (float)(p_238029_4_ + 15), 7105644);
            RealmsSelectWorldTemplateScreen.this.font.drawString(p_238029_1_, p_238029_2_.field_230649_c_, (float)(i + 227 - RealmsSelectWorldTemplateScreen.this.font.getStringWidth(p_238029_2_.field_230649_c_)), (float)(p_238029_4_ + 1), 7105644);

            if (!"".equals(p_238029_2_.field_230651_e_) || !"".equals(p_238029_2_.field_230653_g_) || !"".equals(p_238029_2_.field_230654_h_))
            {
                this.func_238028_a_(p_238029_1_, i - 1, p_238029_4_ + 25, p_238029_5_, p_238029_6_, p_238029_2_.field_230651_e_, p_238029_2_.field_230653_g_, p_238029_2_.field_230654_h_);
            }

            this.func_238027_a_(p_238029_1_, p_238029_3_, p_238029_4_ + 1, p_238029_5_, p_238029_6_, p_238029_2_);
        }

        private void func_238027_a_(MatrixStack p_238027_1_, int p_238027_2_, int p_238027_3_, int p_238027_4_, int p_238027_5_, WorldTemplate p_238027_6_)
        {
            RealmsTextureManager.func_225202_a(p_238027_6_.field_230647_a_, p_238027_6_.field_230652_f_);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            AbstractGui.blit(p_238027_1_, p_238027_2_ + 1, p_238027_3_ + 1, 0.0F, 0.0F, 38, 38, 38, 38);
            RealmsSelectWorldTemplateScreen.this.mc.getTextureManager().bindTexture(RealmsSelectWorldTemplateScreen.field_237989_p_);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            AbstractGui.blit(p_238027_1_, p_238027_2_, p_238027_3_, 0.0F, 0.0F, 40, 40, 40, 40);
        }

        private void func_238028_a_(MatrixStack p_238028_1_, int p_238028_2_, int p_238028_3_, int p_238028_4_, int p_238028_5_, String p_238028_6_, String p_238028_7_, String p_238028_8_)
        {
            if (!"".equals(p_238028_8_))
            {
                RealmsSelectWorldTemplateScreen.this.font.drawString(p_238028_1_, p_238028_8_, (float)p_238028_2_, (float)(p_238028_3_ + 4), 5000268);
            }

            int i = "".equals(p_238028_8_) ? 0 : RealmsSelectWorldTemplateScreen.this.font.getStringWidth(p_238028_8_) + 2;
            boolean flag = false;
            boolean flag1 = false;
            boolean flag2 = "".equals(p_238028_6_);

            if (p_238028_4_ >= p_238028_2_ + i && p_238028_4_ <= p_238028_2_ + i + 32 && p_238028_5_ >= p_238028_3_ && p_238028_5_ <= p_238028_3_ + 15 && p_238028_5_ < RealmsSelectWorldTemplateScreen.this.height - 15 && p_238028_5_ > 32)
            {
                if (p_238028_4_ <= p_238028_2_ + 15 + i && p_238028_4_ > i)
                {
                    if (flag2)
                    {
                        flag1 = true;
                    }
                    else
                    {
                        flag = true;
                    }
                }
                else if (!flag2)
                {
                    flag1 = true;
                }
            }

            if (!flag2)
            {
                RealmsSelectWorldTemplateScreen.this.mc.getTextureManager().bindTexture(RealmsSelectWorldTemplateScreen.field_237987_b_);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.pushMatrix();
                RenderSystem.scalef(1.0F, 1.0F, 1.0F);
                float f = flag ? 15.0F : 0.0F;
                AbstractGui.blit(p_238028_1_, p_238028_2_ + i, p_238028_3_, f, 0.0F, 15, 15, 30, 15);
                RenderSystem.popMatrix();
            }

            if (!"".equals(p_238028_7_))
            {
                RealmsSelectWorldTemplateScreen.this.mc.getTextureManager().bindTexture(RealmsSelectWorldTemplateScreen.field_237988_c_);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.pushMatrix();
                RenderSystem.scalef(1.0F, 1.0F, 1.0F);
                int j = p_238028_2_ + i + (flag2 ? 0 : 17);
                float f1 = flag1 ? 15.0F : 0.0F;
                AbstractGui.blit(p_238028_1_, j, p_238028_3_, f1, 0.0F, 15, 15, 30, 15);
                RenderSystem.popMatrix();
            }

            if (flag)
            {
                RealmsSelectWorldTemplateScreen.this.field_224523_i = RealmsSelectWorldTemplateScreen.field_243163_q;
                RealmsSelectWorldTemplateScreen.this.field_224524_j = p_238028_6_;
            }
            else if (flag1 && !"".equals(p_238028_7_))
            {
                RealmsSelectWorldTemplateScreen.this.field_224523_i = RealmsSelectWorldTemplateScreen.field_243164_r;
                RealmsSelectWorldTemplateScreen.this.field_224524_j = p_238028_7_;
            }
        }
    }

    class WorldTemplateSelectionList extends RealmsObjectSelectionList<RealmsSelectWorldTemplateScreen.WorldTemplateSelectionEntry>
    {
        public WorldTemplateSelectionList()
        {
            this(Collections.emptyList());
        }

        public WorldTemplateSelectionList(Iterable<WorldTemplate> p_i51726_2_)
        {
            super(RealmsSelectWorldTemplateScreen.this.width, RealmsSelectWorldTemplateScreen.this.height, RealmsSelectWorldTemplateScreen.this.field_224529_o ? RealmsSelectWorldTemplateScreen.func_239562_k_(1) : 32, RealmsSelectWorldTemplateScreen.this.height - 40, 46);
            p_i51726_2_.forEach(this::func_223876_a);
        }

        public void func_223876_a(WorldTemplate p_223876_1_)
        {
            this.addEntry(RealmsSelectWorldTemplateScreen.this.new WorldTemplateSelectionEntry(p_223876_1_));
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            if (button == 0 && mouseY >= (double)this.y0 && mouseY <= (double)this.y1)
            {
                int i = this.width / 2 - 150;

                if (RealmsSelectWorldTemplateScreen.this.field_224524_j != null)
                {
                    Util.getOSType().openURI(RealmsSelectWorldTemplateScreen.this.field_224524_j);
                }

                int j = (int)Math.floor(mouseY - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
                int k = j / this.itemHeight;

                if (mouseX >= (double)i && mouseX < (double)this.getScrollbarPosition() && k >= 0 && j >= 0 && k < this.getItemCount())
                {
                    this.func_231400_a_(k);
                    this.func_231401_a_(j, k, mouseX, mouseY, this.width);

                    if (k >= RealmsSelectWorldTemplateScreen.this.field_224517_c.getItemCount())
                    {
                        return super.mouseClicked(mouseX, mouseY, button);
                    }

                    RealmsSelectWorldTemplateScreen.this.field_224526_l = RealmsSelectWorldTemplateScreen.this.field_224526_l + 7;

                    if (RealmsSelectWorldTemplateScreen.this.field_224526_l >= 10)
                    {
                        RealmsSelectWorldTemplateScreen.this.func_224500_h();
                    }

                    return true;
                }
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        public void func_231400_a_(int p_231400_1_)
        {
            this.func_239561_k_(p_231400_1_);

            if (p_231400_1_ != -1)
            {
                WorldTemplate worldtemplate = RealmsSelectWorldTemplateScreen.this.field_224517_c.func_223877_a(p_231400_1_);
                String s = I18n.format("narrator.select.list.position", p_231400_1_ + 1, RealmsSelectWorldTemplateScreen.this.field_224517_c.getItemCount());
                String s1 = I18n.format("mco.template.select.narrate.version", worldtemplate.field_230649_c_);
                String s2 = I18n.format("mco.template.select.narrate.authors", worldtemplate.field_230650_d_);
                String s3 = RealmsNarratorHelper.func_239552_b_(Arrays.asList(worldtemplate.field_230648_b_, s2, worldtemplate.field_230654_h_, s1, s));
                RealmsNarratorHelper.func_239550_a_(I18n.format("narrator.select", s3));
            }
        }

        public void setSelected(@Nullable RealmsSelectWorldTemplateScreen.WorldTemplateSelectionEntry entry)
        {
            super.setSelected(entry);
            RealmsSelectWorldTemplateScreen.this.field_224518_d = this.getEventListeners().indexOf(entry);
            RealmsSelectWorldTemplateScreen.this.func_224514_b();
        }

        public int getMaxPosition()
        {
            return this.getItemCount() * 46;
        }

        public int getRowWidth()
        {
            return 300;
        }

        public void renderBackground(MatrixStack p_230433_1_)
        {
            RealmsSelectWorldTemplateScreen.this.renderBackground(p_230433_1_);
        }

        public boolean isFocused()
        {
            return RealmsSelectWorldTemplateScreen.this.getListener() == this;
        }

        public boolean func_223878_a()
        {
            return this.getItemCount() == 0;
        }

        public WorldTemplate func_223877_a(int p_223877_1_)
        {
            return (this.getEventListeners().get(p_223877_1_)).field_223756_a;
        }

        public List<WorldTemplate> func_223879_b()
        {
            return this.getEventListeners().stream().map((p_223875_0_) ->
            {
                return p_223875_0_.field_223756_a;
            }).collect(Collectors.toList());
        }
    }
}
