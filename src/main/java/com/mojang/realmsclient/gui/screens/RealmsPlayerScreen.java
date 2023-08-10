package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsTextureManager;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsPlayerScreen extends RealmsScreen
{
    private static final Logger field_224300_a = LogManager.getLogger();
    private static final ResourceLocation field_237895_b_ = new ResourceLocation("realms", "textures/gui/realms/op_icon.png");
    private static final ResourceLocation field_237896_c_ = new ResourceLocation("realms", "textures/gui/realms/user_icon.png");
    private static final ResourceLocation field_237897_p_ = new ResourceLocation("realms", "textures/gui/realms/cross_player_icon.png");
    private static final ResourceLocation field_237898_q_ = new ResourceLocation("minecraft", "textures/gui/options_background.png");
    private static final ITextComponent field_243138_r = new TranslationTextComponent("mco.configure.world.invites.normal.tooltip");
    private static final ITextComponent field_243139_s = new TranslationTextComponent("mco.configure.world.invites.ops.tooltip");
    private static final ITextComponent field_243140_t = new TranslationTextComponent("mco.configure.world.invites.remove.tooltip");
    private static final ITextComponent field_243141_u = new TranslationTextComponent("mco.configure.world.invited");
    private ITextComponent field_224301_b;
    private final RealmsConfigureWorldScreen field_224302_c;
    private final RealmsServer field_224303_d;
    private RealmsPlayerScreen.InvitedList field_224304_e;
    private int field_224305_f;
    private int field_224306_g;
    private int field_224307_h;
    private Button field_224308_i;
    private Button field_224309_j;
    private int field_224310_k = -1;
    private String field_224311_l;
    private int field_224312_m = -1;
    private boolean field_224313_n;
    private RealmsLabel field_224314_o;
    private RealmsPlayerScreen.GuestAction field_243137_J = RealmsPlayerScreen.GuestAction.NONE;

    public RealmsPlayerScreen(RealmsConfigureWorldScreen p_i51760_1_, RealmsServer p_i51760_2_)
    {
        this.field_224302_c = p_i51760_1_;
        this.field_224303_d = p_i51760_2_;
    }

    public void init()
    {
        this.field_224305_f = this.width / 2 - 160;
        this.field_224306_g = 150;
        this.field_224307_h = this.width / 2 + 12;
        this.mc.keyboardListener.enableRepeatEvents(true);
        this.field_224304_e = new RealmsPlayerScreen.InvitedList();
        this.field_224304_e.setLeftPos(this.field_224305_f);
        this.addListener(this.field_224304_e);

        for (PlayerInfo playerinfo : this.field_224303_d.field_230589_h_)
        {
            this.field_224304_e.func_223870_a(playerinfo);
        }

        this.addButton(new Button(this.field_224307_h, func_239562_k_(1), this.field_224306_g + 10, 20, new TranslationTextComponent("mco.configure.world.buttons.invite"), (p_237924_1_) ->
        {
            this.mc.displayGuiScreen(new RealmsInviteScreen(this.field_224302_c, this, this.field_224303_d));
        }));
        this.field_224308_i = this.addButton(new Button(this.field_224307_h, func_239562_k_(7), this.field_224306_g + 10, 20, new TranslationTextComponent("mco.configure.world.invites.remove.tooltip"), (p_237918_1_) ->
        {
            this.func_224274_d(this.field_224312_m);
        }));
        this.field_224309_j = this.addButton(new Button(this.field_224307_h, func_239562_k_(9), this.field_224306_g + 10, 20, new TranslationTextComponent("mco.configure.world.invites.ops.tooltip"), (p_237912_1_) ->
        {
            if (this.field_224303_d.field_230589_h_.get(this.field_224312_m).func_230763_c_())
            {
                this.func_224279_c(this.field_224312_m);
            }
            else {
                this.func_224289_b(this.field_224312_m);
            }
        }));
        this.addButton(new Button(this.field_224307_h + this.field_224306_g / 2 + 2, func_239562_k_(12), this.field_224306_g / 2 + 10 - 2, 20, DialogTexts.GUI_BACK, (p_237907_1_) ->
        {
            this.func_224298_b();
        }));
        this.field_224314_o = this.addListener(new RealmsLabel(new TranslationTextComponent("mco.configure.world.players.title"), this.width / 2, 17, 16777215));
        this.func_231411_u_();
        this.func_224280_a();
    }

    private void func_224280_a()
    {
        this.field_224308_i.visible = this.func_224296_a(this.field_224312_m);
        this.field_224309_j.visible = this.func_224296_a(this.field_224312_m);
    }

    private boolean func_224296_a(int p_224296_1_)
    {
        return p_224296_1_ != -1;
    }

    public void onClose()
    {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.func_224298_b();
            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    private void func_224298_b()
    {
        if (this.field_224313_n)
        {
            this.mc.displayGuiScreen(this.field_224302_c.func_224407_b());
        }
        else
        {
            this.mc.displayGuiScreen(this.field_224302_c);
        }
    }

    private void func_224289_b(int p_224289_1_)
    {
        this.func_224280_a();
        RealmsClient realmsclient = RealmsClient.func_224911_a();
        String s = this.field_224303_d.field_230589_h_.get(p_224289_1_).func_230760_b_();

        try
        {
            this.func_224283_a(realmsclient.func_224906_e(this.field_224303_d.field_230582_a_, s));
        }
        catch (RealmsServiceException realmsserviceexception)
        {
            field_224300_a.error("Couldn't op the user");
        }
    }

    private void func_224279_c(int p_224279_1_)
    {
        this.func_224280_a();
        RealmsClient realmsclient = RealmsClient.func_224911_a();
        String s = this.field_224303_d.field_230589_h_.get(p_224279_1_).func_230760_b_();

        try
        {
            this.func_224283_a(realmsclient.func_224929_f(this.field_224303_d.field_230582_a_, s));
        }
        catch (RealmsServiceException realmsserviceexception)
        {
            field_224300_a.error("Couldn't deop the user");
        }
    }

    private void func_224283_a(Ops p_224283_1_)
    {
        for (PlayerInfo playerinfo : this.field_224303_d.field_230589_h_)
        {
            playerinfo.func_230759_a_(p_224283_1_.field_230562_a_.contains(playerinfo.func_230757_a_()));
        }
    }

    private void func_224274_d(int p_224274_1_)
    {
        this.func_224280_a();

        if (p_224274_1_ >= 0 && p_224274_1_ < this.field_224303_d.field_230589_h_.size())
        {
            PlayerInfo playerinfo = this.field_224303_d.field_230589_h_.get(p_224274_1_);
            this.field_224311_l = playerinfo.func_230760_b_();
            this.field_224310_k = p_224274_1_;
            RealmsConfirmScreen realmsconfirmscreen = new RealmsConfirmScreen((p_237919_1_) ->
            {
                if (p_237919_1_)
                {
                    RealmsClient realmsclient = RealmsClient.func_224911_a();

                    try
                    {
                        realmsclient.func_224908_a(this.field_224303_d.field_230582_a_, this.field_224311_l);
                    }
                    catch (RealmsServiceException realmsserviceexception)
                    {
                        field_224300_a.error("Couldn't uninvite user");
                    }

                    this.func_224292_e(this.field_224310_k);
                    this.field_224312_m = -1;
                    this.func_224280_a();
                }

                this.field_224313_n = true;
                this.mc.displayGuiScreen(this);
            }, new StringTextComponent("Question"), (new TranslationTextComponent("mco.configure.world.uninvite.question")).appendString(" '").appendString(playerinfo.func_230757_a_()).appendString("' ?"));
            this.mc.displayGuiScreen(realmsconfirmscreen);
        }
    }

    private void func_224292_e(int p_224292_1_)
    {
        this.field_224303_d.field_230589_h_.remove(p_224292_1_);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.field_224301_b = null;
        this.field_243137_J = RealmsPlayerScreen.GuestAction.NONE;
        this.renderBackground(matrixStack);

        if (this.field_224304_e != null)
        {
            this.field_224304_e.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        int i = func_239562_k_(12) + 20;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.mc.getTextureManager().bindTexture(field_237898_q_);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0D, (double)this.height, 0.0D).tex(0.0F, (float)(this.height - i) / 32.0F + 0.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos((double)this.width, (double)this.height, 0.0D).tex((float)this.width / 32.0F, (float)(this.height - i) / 32.0F + 0.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos((double)this.width, (double)i, 0.0D).tex((float)this.width / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos(0.0D, (double)i, 0.0D).tex(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
        this.field_224314_o.func_239560_a_(this, matrixStack);

        if (this.field_224303_d != null && this.field_224303_d.field_230589_h_ != null)
        {
            this.font.func_243248_b(matrixStack, (new StringTextComponent("")).append(field_243141_u).appendString(" (").appendString(Integer.toString(this.field_224303_d.field_230589_h_.size())).appendString(")"), (float)this.field_224305_f, (float)func_239562_k_(0), 10526880);
        }
        else
        {
            this.font.func_243248_b(matrixStack, field_243141_u, (float)this.field_224305_f, (float)func_239562_k_(0), 10526880);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (this.field_224303_d != null)
        {
            this.func_237903_a_(matrixStack, this.field_224301_b, mouseX, mouseY);
        }
    }

    protected void func_237903_a_(MatrixStack p_237903_1_, @Nullable ITextComponent p_237903_2_, int p_237903_3_, int p_237903_4_)
    {
        if (p_237903_2_ != null)
        {
            int i = p_237903_3_ + 12;
            int j = p_237903_4_ - 12;
            int k = this.font.getStringPropertyWidth(p_237903_2_);
            this.fillGradient(p_237903_1_, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
            this.font.func_243246_a(p_237903_1_, p_237903_2_, (float)i, (float)j, 16777215);
        }
    }

    private void func_237914_c_(MatrixStack p_237914_1_, int p_237914_2_, int p_237914_3_, int p_237914_4_, int p_237914_5_)
    {
        boolean flag = p_237914_4_ >= p_237914_2_ && p_237914_4_ <= p_237914_2_ + 9 && p_237914_5_ >= p_237914_3_ && p_237914_5_ <= p_237914_3_ + 9 && p_237914_5_ < func_239562_k_(12) + 20 && p_237914_5_ > func_239562_k_(1);
        this.mc.getTextureManager().bindTexture(field_237897_p_);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = flag ? 7.0F : 0.0F;
        AbstractGui.blit(p_237914_1_, p_237914_2_, p_237914_3_, 0.0F, f, 8, 7, 8, 14);

        if (flag)
        {
            this.field_224301_b = field_243140_t;
            this.field_243137_J = RealmsPlayerScreen.GuestAction.REMOVE;
        }
    }

    private void func_237921_d_(MatrixStack p_237921_1_, int p_237921_2_, int p_237921_3_, int p_237921_4_, int p_237921_5_)
    {
        boolean flag = p_237921_4_ >= p_237921_2_ && p_237921_4_ <= p_237921_2_ + 9 && p_237921_5_ >= p_237921_3_ && p_237921_5_ <= p_237921_3_ + 9 && p_237921_5_ < func_239562_k_(12) + 20 && p_237921_5_ > func_239562_k_(1);
        this.mc.getTextureManager().bindTexture(field_237895_b_);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = flag ? 8.0F : 0.0F;
        AbstractGui.blit(p_237921_1_, p_237921_2_, p_237921_3_, 0.0F, f, 8, 8, 8, 16);

        if (flag)
        {
            this.field_224301_b = field_243139_s;
            this.field_243137_J = RealmsPlayerScreen.GuestAction.TOGGLE_OP;
        }
    }

    private void func_237925_e_(MatrixStack p_237925_1_, int p_237925_2_, int p_237925_3_, int p_237925_4_, int p_237925_5_)
    {
        boolean flag = p_237925_4_ >= p_237925_2_ && p_237925_4_ <= p_237925_2_ + 9 && p_237925_5_ >= p_237925_3_ && p_237925_5_ <= p_237925_3_ + 9 && p_237925_5_ < func_239562_k_(12) + 20 && p_237925_5_ > func_239562_k_(1);
        this.mc.getTextureManager().bindTexture(field_237896_c_);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = flag ? 8.0F : 0.0F;
        AbstractGui.blit(p_237925_1_, p_237925_2_, p_237925_3_, 0.0F, f, 8, 8, 8, 16);

        if (flag)
        {
            this.field_224301_b = field_243138_r;
            this.field_243137_J = RealmsPlayerScreen.GuestAction.TOGGLE_OP;
        }
    }

    static enum GuestAction
    {
        TOGGLE_OP,
        REMOVE,
        NONE;
    }

    class InvitedEntry extends ExtendedList.AbstractListEntry<RealmsPlayerScreen.InvitedEntry>
    {
        private final PlayerInfo field_237930_b_;

        public InvitedEntry(PlayerInfo p_i51614_2_)
        {
            this.field_237930_b_ = p_i51614_2_;
        }

        public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_)
        {
            this.func_237932_a_(p_230432_1_, this.field_237930_b_, p_230432_4_, p_230432_3_, p_230432_7_, p_230432_8_);
        }

        private void func_237932_a_(MatrixStack p_237932_1_, PlayerInfo p_237932_2_, int p_237932_3_, int p_237932_4_, int p_237932_5_, int p_237932_6_)
        {
            int i;

            if (!p_237932_2_.func_230765_d_())
            {
                i = 10526880;
            }
            else if (p_237932_2_.func_230766_e_())
            {
                i = 8388479;
            }
            else
            {
                i = 16777215;
            }

            RealmsPlayerScreen.this.font.drawString(p_237932_1_, p_237932_2_.func_230757_a_(), (float)(RealmsPlayerScreen.this.field_224305_f + 3 + 12), (float)(p_237932_4_ + 1), i);

            if (p_237932_2_.func_230763_c_())
            {
                RealmsPlayerScreen.this.func_237921_d_(p_237932_1_, RealmsPlayerScreen.this.field_224305_f + RealmsPlayerScreen.this.field_224306_g - 10, p_237932_4_ + 1, p_237932_5_, p_237932_6_);
            }
            else
            {
                RealmsPlayerScreen.this.func_237925_e_(p_237932_1_, RealmsPlayerScreen.this.field_224305_f + RealmsPlayerScreen.this.field_224306_g - 10, p_237932_4_ + 1, p_237932_5_, p_237932_6_);
            }

            RealmsPlayerScreen.this.func_237914_c_(p_237932_1_, RealmsPlayerScreen.this.field_224305_f + RealmsPlayerScreen.this.field_224306_g - 22, p_237932_4_ + 2, p_237932_5_, p_237932_6_);
            RealmsTextureManager.func_225205_a(p_237932_2_.func_230760_b_(), () ->
            {
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                AbstractGui.blit(p_237932_1_, RealmsPlayerScreen.this.field_224305_f + 2 + 2, p_237932_4_ + 1, 8, 8, 8.0F, 8.0F, 8, 8, 64, 64);
                AbstractGui.blit(p_237932_1_, RealmsPlayerScreen.this.field_224305_f + 2 + 2, p_237932_4_ + 1, 8, 8, 40.0F, 8.0F, 8, 8, 64, 64);
            });
        }
    }

    class InvitedList extends RealmsObjectSelectionList<RealmsPlayerScreen.InvitedEntry>
    {
        public InvitedList()
        {
            super(RealmsPlayerScreen.this.field_224306_g + 10, RealmsPlayerScreen.func_239562_k_(12) + 20, RealmsPlayerScreen.func_239562_k_(1), RealmsPlayerScreen.func_239562_k_(12) + 20, 13);
        }

        public void func_223870_a(PlayerInfo p_223870_1_)
        {
            this.addEntry(RealmsPlayerScreen.this.new InvitedEntry(p_223870_1_));
        }

        public int getRowWidth()
        {
            return (int)((double)this.width * 1.0D);
        }

        public boolean isFocused()
        {
            return RealmsPlayerScreen.this.getListener() == this;
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            if (button == 0 && mouseX < (double)this.getScrollbarPosition() && mouseY >= (double)this.y0 && mouseY <= (double)this.y1)
            {
                int i = RealmsPlayerScreen.this.field_224305_f;
                int j = RealmsPlayerScreen.this.field_224305_f + RealmsPlayerScreen.this.field_224306_g;
                int k = (int)Math.floor(mouseY - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
                int l = k / this.itemHeight;

                if (mouseX >= (double)i && mouseX <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount())
                {
                    this.func_231400_a_(l);
                    this.func_231401_a_(k, l, mouseX, mouseY, this.width);
                }

                return true;
            }
            else
            {
                return super.mouseClicked(mouseX, mouseY, button);
            }
        }

        public void func_231401_a_(int p_231401_1_, int p_231401_2_, double p_231401_3_, double p_231401_5_, int p_231401_7_)
        {
            if (p_231401_2_ >= 0 && p_231401_2_ <= RealmsPlayerScreen.this.field_224303_d.field_230589_h_.size() && RealmsPlayerScreen.this.field_243137_J != RealmsPlayerScreen.GuestAction.NONE)
            {
                if (RealmsPlayerScreen.this.field_243137_J == RealmsPlayerScreen.GuestAction.TOGGLE_OP)
                {
                    if (RealmsPlayerScreen.this.field_224303_d.field_230589_h_.get(p_231401_2_).func_230763_c_())
                    {
                        RealmsPlayerScreen.this.func_224279_c(p_231401_2_);
                    }
                    else
                    {
                        RealmsPlayerScreen.this.func_224289_b(p_231401_2_);
                    }
                }
                else if (RealmsPlayerScreen.this.field_243137_J == RealmsPlayerScreen.GuestAction.REMOVE)
                {
                    RealmsPlayerScreen.this.func_224274_d(p_231401_2_);
                }
            }
        }

        public void func_231400_a_(int p_231400_1_)
        {
            this.func_239561_k_(p_231400_1_);

            if (p_231400_1_ != -1)
            {
                RealmsNarratorHelper.func_239550_a_(I18n.format("narrator.select", RealmsPlayerScreen.this.field_224303_d.field_230589_h_.get(p_231400_1_).func_230757_a_()));
            }

            this.func_223869_a(p_231400_1_);
        }

        public void func_223869_a(int p_223869_1_)
        {
            RealmsPlayerScreen.this.field_224312_m = p_223869_1_;
            RealmsPlayerScreen.this.func_224280_a();
        }

        public void setSelected(@Nullable RealmsPlayerScreen.InvitedEntry entry)
        {
            super.setSelected(entry);
            RealmsPlayerScreen.this.field_224312_m = this.getEventListeners().indexOf(entry);
            RealmsPlayerScreen.this.func_224280_a();
        }

        public void renderBackground(MatrixStack p_230433_1_)
        {
            RealmsPlayerScreen.this.renderBackground(p_230433_1_);
        }

        public int getScrollbarPosition()
        {
            return RealmsPlayerScreen.this.field_224305_f + this.width - 5;
        }

        public int getMaxPosition()
        {
            return this.getItemCount() * 13;
        }
    }
}
