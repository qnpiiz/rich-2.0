package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.action.ResetWorldRealmsAction;
import net.minecraft.realms.action.SwitchMinigameRealmsAction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsResetWorldScreen extends NotifableRealmsScreen
{
    private static final Logger field_224456_b = LogManager.getLogger();
    private final Screen field_224457_c;
    private final RealmsServer field_224458_d;
    private RealmsLabel field_224460_f;
    private RealmsLabel field_224461_g;
    private ITextComponent field_224462_h = new TranslationTextComponent("mco.reset.world.title");
    private ITextComponent field_224463_i = new TranslationTextComponent("mco.reset.world.warning");
    private ITextComponent field_224464_j = DialogTexts.GUI_CANCEL;
    private int field_224465_k = 16711680;
    private static final ResourceLocation field_237944_w_ = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
    private static final ResourceLocation field_237945_x_ = new ResourceLocation("realms", "textures/gui/realms/upload.png");
    private static final ResourceLocation field_237946_y_ = new ResourceLocation("realms", "textures/gui/realms/adventure.png");
    private static final ResourceLocation field_237947_z_ = new ResourceLocation("realms", "textures/gui/realms/survival_spawn.png");
    private static final ResourceLocation field_237939_A_ = new ResourceLocation("realms", "textures/gui/realms/new_world.png");
    private static final ResourceLocation field_237940_B_ = new ResourceLocation("realms", "textures/gui/realms/experience.png");
    private static final ResourceLocation field_237941_C_ = new ResourceLocation("realms", "textures/gui/realms/inspiration.png");
    private WorldTemplatePaginatedList field_224468_n;
    private WorldTemplatePaginatedList field_224469_o;
    private WorldTemplatePaginatedList field_224470_p;
    private WorldTemplatePaginatedList field_224471_q;
    public int field_224455_a = -1;
    private RealmsResetWorldScreen.ResetType field_224472_r = RealmsResetWorldScreen.ResetType.NONE;
    private RealmsResetWorldScreen.ResetWorldInfo field_224473_s;
    private WorldTemplate field_224474_t;
    @Nullable
    private ITextComponent field_224475_u;
    private final Runnable field_237942_L_;
    private final Runnable field_237943_M_;

    public RealmsResetWorldScreen(Screen p_i232215_1_, RealmsServer p_i232215_2_, Runnable p_i232215_3_, Runnable p_i232215_4_)
    {
        this.field_224457_c = p_i232215_1_;
        this.field_224458_d = p_i232215_2_;
        this.field_237942_L_ = p_i232215_3_;
        this.field_237943_M_ = p_i232215_4_;
    }

    public RealmsResetWorldScreen(Screen p_i232216_1_, RealmsServer p_i232216_2_, ITextComponent p_i232216_3_, ITextComponent p_i232216_4_, int p_i232216_5_, ITextComponent p_i232216_6_, Runnable p_i232216_7_, Runnable p_i232216_8_)
    {
        this(p_i232216_1_, p_i232216_2_, p_i232216_7_, p_i232216_8_);
        this.field_224462_h = p_i232216_3_;
        this.field_224463_i = p_i232216_4_;
        this.field_224465_k = p_i232216_5_;
        this.field_224464_j = p_i232216_6_;
    }

    public void func_224445_b(int p_224445_1_)
    {
        this.field_224455_a = p_224445_1_;
    }

    public void func_224432_a(ITextComponent p_224432_1_)
    {
        this.field_224475_u = p_224432_1_;
    }

    public void init()
    {
        this.addButton(new Button(this.width / 2 - 40, func_239562_k_(14) - 10, 80, 20, this.field_224464_j, (p_237959_1_) ->
        {
            this.mc.displayGuiScreen(this.field_224457_c);
        }));
        (new Thread("Realms-reset-world-fetcher")
        {
            public void run()
            {
                RealmsClient realmsclient = RealmsClient.func_224911_a();

                try
                {
                    WorldTemplatePaginatedList worldtemplatepaginatedlist = realmsclient.func_224930_a(1, 10, RealmsServer.ServerType.NORMAL);
                    WorldTemplatePaginatedList worldtemplatepaginatedlist1 = realmsclient.func_224930_a(1, 10, RealmsServer.ServerType.ADVENTUREMAP);
                    WorldTemplatePaginatedList worldtemplatepaginatedlist2 = realmsclient.func_224930_a(1, 10, RealmsServer.ServerType.EXPERIENCE);
                    WorldTemplatePaginatedList worldtemplatepaginatedlist3 = realmsclient.func_224930_a(1, 10, RealmsServer.ServerType.INSPIRATION);
                    RealmsResetWorldScreen.this.mc.execute(() ->
                    {
                        RealmsResetWorldScreen.this.field_224468_n = worldtemplatepaginatedlist;
                        RealmsResetWorldScreen.this.field_224469_o = worldtemplatepaginatedlist1;
                        RealmsResetWorldScreen.this.field_224470_p = worldtemplatepaginatedlist2;
                        RealmsResetWorldScreen.this.field_224471_q = worldtemplatepaginatedlist3;
                    });
                }
                catch (RealmsServiceException realmsserviceexception)
                {
                    RealmsResetWorldScreen.field_224456_b.error("Couldn't fetch templates in reset world", (Throwable)realmsserviceexception);
                }
            }
        }).start();
        this.field_224460_f = this.addListener(new RealmsLabel(this.field_224462_h, this.width / 2, 7, 16777215));
        this.field_224461_g = this.addListener(new RealmsLabel(this.field_224463_i, this.width / 2, 22, this.field_224465_k));
        this.addButton(new RealmsResetWorldScreen.TexturedButton(this.func_224434_c(1), func_239562_k_(0) + 10, new TranslationTextComponent("mco.reset.world.generate"), field_237939_A_, (p_237958_1_) ->
        {
            this.mc.displayGuiScreen(new RealmsResetNormalWorldScreen(this, this.field_224462_h));
        }));
        this.addButton(new RealmsResetWorldScreen.TexturedButton(this.func_224434_c(2), func_239562_k_(0) + 10, new TranslationTextComponent("mco.reset.world.upload"), field_237945_x_, (p_237957_1_) ->
        {
            Screen screen = new RealmsSelectFileToUploadScreen(this.field_224458_d.field_230582_a_, this.field_224455_a != -1 ? this.field_224455_a : this.field_224458_d.field_230595_n_, this, this.field_237943_M_);
            this.mc.displayGuiScreen(screen);
        }));
        this.addButton(new RealmsResetWorldScreen.TexturedButton(this.func_224434_c(3), func_239562_k_(0) + 10, new TranslationTextComponent("mco.reset.world.template"), field_237947_z_, (p_237956_1_) ->
        {
            RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.ServerType.NORMAL, this.field_224468_n);
            realmsselectworldtemplatescreen.func_238001_a_(new TranslationTextComponent("mco.reset.world.template"));
            this.mc.displayGuiScreen(realmsselectworldtemplatescreen);
        }));
        this.addButton(new RealmsResetWorldScreen.TexturedButton(this.func_224434_c(1), func_239562_k_(6) + 20, new TranslationTextComponent("mco.reset.world.adventure"), field_237946_y_, (p_237955_1_) ->
        {
            RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.ServerType.ADVENTUREMAP, this.field_224469_o);
            realmsselectworldtemplatescreen.func_238001_a_(new TranslationTextComponent("mco.reset.world.adventure"));
            this.mc.displayGuiScreen(realmsselectworldtemplatescreen);
        }));
        this.addButton(new RealmsResetWorldScreen.TexturedButton(this.func_224434_c(2), func_239562_k_(6) + 20, new TranslationTextComponent("mco.reset.world.experience"), field_237940_B_, (p_237954_1_) ->
        {
            RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.ServerType.EXPERIENCE, this.field_224470_p);
            realmsselectworldtemplatescreen.func_238001_a_(new TranslationTextComponent("mco.reset.world.experience"));
            this.mc.displayGuiScreen(realmsselectworldtemplatescreen);
        }));
        this.addButton(new RealmsResetWorldScreen.TexturedButton(this.func_224434_c(3), func_239562_k_(6) + 20, new TranslationTextComponent("mco.reset.world.inspiration"), field_237941_C_, (p_237951_1_) ->
        {
            RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.ServerType.INSPIRATION, this.field_224471_q);
            realmsselectworldtemplatescreen.func_238001_a_(new TranslationTextComponent("mco.reset.world.inspiration"));
            this.mc.displayGuiScreen(realmsselectworldtemplatescreen);
        }));
        this.func_231411_u_();
    }

    public void onClose()
    {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.mc.displayGuiScreen(this.field_224457_c);
            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    private int func_224434_c(int p_224434_1_)
    {
        return this.width / 2 - 130 + (p_224434_1_ - 1) * 100;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.field_224460_f.func_239560_a_(this, matrixStack);
        this.field_224461_g.func_239560_a_(this, matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void func_237948_a_(MatrixStack p_237948_1_, int p_237948_2_, int p_237948_3_, ITextComponent p_237948_4_, ResourceLocation p_237948_5_, boolean p_237948_6_, boolean p_237948_7_)
    {
        this.mc.getTextureManager().bindTexture(p_237948_5_);

        if (p_237948_6_)
        {
            RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
        }
        else
        {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        AbstractGui.blit(p_237948_1_, p_237948_2_ + 2, p_237948_3_ + 14, 0.0F, 0.0F, 56, 56, 56, 56);
        this.mc.getTextureManager().bindTexture(field_237944_w_);

        if (p_237948_6_)
        {
            RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
        }
        else
        {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        AbstractGui.blit(p_237948_1_, p_237948_2_, p_237948_3_ + 12, 0.0F, 0.0F, 60, 60, 60, 60);
        int i = p_237948_6_ ? 10526880 : 16777215;
        drawCenteredString(p_237948_1_, this.font, p_237948_4_, p_237948_2_ + 30, p_237948_3_, i);
    }

    protected void func_223627_a_(@Nullable WorldTemplate p_223627_1_)
    {
        if (p_223627_1_ != null)
        {
            if (this.field_224455_a == -1)
            {
                this.func_224435_b(p_223627_1_);
            }
            else
            {
                switch (p_223627_1_.field_230655_i_)
                {
                    case WORLD_TEMPLATE:
                        this.field_224472_r = RealmsResetWorldScreen.ResetType.SURVIVAL_SPAWN;
                        break;

                    case ADVENTUREMAP:
                        this.field_224472_r = RealmsResetWorldScreen.ResetType.ADVENTURE;
                        break;

                    case EXPERIENCE:
                        this.field_224472_r = RealmsResetWorldScreen.ResetType.EXPERIENCE;
                        break;

                    case INSPIRATION:
                        this.field_224472_r = RealmsResetWorldScreen.ResetType.INSPIRATION;
                }

                this.field_224474_t = p_223627_1_;
                this.func_224454_b();
            }
        }
    }

    private void func_224454_b()
    {
        this.func_237952_a_(() ->
        {
            switch (this.field_224472_r)
            {
                case ADVENTURE:
                case SURVIVAL_SPAWN:
                case EXPERIENCE:
                case INSPIRATION:
                    if (this.field_224474_t != null)
                    {
                        this.func_224435_b(this.field_224474_t);
                    }

                    break;

                case GENERATE:
                    if (this.field_224473_s != null)
                    {
                        this.func_224437_b(this.field_224473_s);
                    }
            }
        });
    }

    public void func_237952_a_(Runnable p_237952_1_)
    {
        this.mc.displayGuiScreen(new RealmsLongRunningMcoTaskScreen(this.field_224457_c, new SwitchMinigameRealmsAction(this.field_224458_d.field_230582_a_, this.field_224455_a, p_237952_1_)));
    }

    public void func_224435_b(WorldTemplate p_224435_1_)
    {
        this.func_237953_a_((String)null, p_224435_1_, -1, true);
    }

    private void func_224437_b(RealmsResetWorldScreen.ResetWorldInfo p_224437_1_)
    {
        this.func_237953_a_(p_224437_1_.field_225157_a, (WorldTemplate)null, p_224437_1_.field_225158_b, p_224437_1_.field_225159_c);
    }

    private void func_237953_a_(@Nullable String p_237953_1_, @Nullable WorldTemplate p_237953_2_, int p_237953_3_, boolean p_237953_4_)
    {
        this.mc.displayGuiScreen(new RealmsLongRunningMcoTaskScreen(this.field_224457_c, new ResetWorldRealmsAction(p_237953_1_, p_237953_2_, p_237953_3_, p_237953_4_, this.field_224458_d.field_230582_a_, this.field_224475_u, this.field_237942_L_)));
    }

    public void func_224438_a(RealmsResetWorldScreen.ResetWorldInfo p_224438_1_)
    {
        if (this.field_224455_a == -1)
        {
            this.func_224437_b(p_224438_1_);
        }
        else
        {
            this.field_224472_r = RealmsResetWorldScreen.ResetType.GENERATE;
            this.field_224473_s = p_224438_1_;
            this.func_224454_b();
        }
    }

    static enum ResetType
    {
        NONE,
        GENERATE,
        UPLOAD,
        ADVENTURE,
        SURVIVAL_SPAWN,
        EXPERIENCE,
        INSPIRATION;
    }

    public static class ResetWorldInfo
    {
        private final String field_225157_a;
        private final int field_225158_b;
        private final boolean field_225159_c;

        public ResetWorldInfo(String p_i51560_1_, int p_i51560_2_, boolean p_i51560_3_)
        {
            this.field_225157_a = p_i51560_1_;
            this.field_225158_b = p_i51560_2_;
            this.field_225159_c = p_i51560_3_;
        }
    }

    class TexturedButton extends Button
    {
        private final ResourceLocation field_223824_c;

        public TexturedButton(int p_i232218_2_, int p_i232218_3_, ITextComponent p_i232218_4_, ResourceLocation p_i232218_5_, Button.IPressable p_i232218_6_)
        {
            super(p_i232218_2_, p_i232218_3_, 60, 72, p_i232218_4_, p_i232218_6_);
            this.field_223824_c = p_i232218_5_;
        }

        public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
        {
            RealmsResetWorldScreen.this.func_237948_a_(matrixStack, this.x, this.y, this.getMessage(), this.field_223824_c, this.isHovered(), this.isMouseOver((double)mouseX, (double)mouseY));
        }
    }
}
