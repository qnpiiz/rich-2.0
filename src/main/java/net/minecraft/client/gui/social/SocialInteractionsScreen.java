package net.minecraft.client.gui.social;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class SocialInteractionsScreen extends Screen
{
    protected static final ResourceLocation field_244666_a = new ResourceLocation("textures/gui/social_interactions.png");
    private static final ITextComponent field_244667_b = new TranslationTextComponent("gui.socialInteractions.tab_all");
    private static final ITextComponent field_244668_c = new TranslationTextComponent("gui.socialInteractions.tab_hidden");
    private static final ITextComponent field_244762_p = new TranslationTextComponent("gui.socialInteractions.tab_blocked");
    private static final ITextComponent field_244669_p = field_244667_b.copyRaw().mergeStyle(TextFormatting.UNDERLINE);
    private static final ITextComponent field_244670_q = field_244668_c.copyRaw().mergeStyle(TextFormatting.UNDERLINE);
    private static final ITextComponent field_244763_s = field_244762_p.copyRaw().mergeStyle(TextFormatting.UNDERLINE);
    private static final ITextComponent field_244671_r = (new TranslationTextComponent("gui.socialInteractions.search_hint")).mergeStyle(TextFormatting.ITALIC).mergeStyle(TextFormatting.GRAY);
    private static final ITextComponent field_244764_u = (new TranslationTextComponent("gui.socialInteractions.search_empty")).mergeStyle(TextFormatting.GRAY);
    private static final ITextComponent field_244672_s = (new TranslationTextComponent("gui.socialInteractions.empty_hidden")).mergeStyle(TextFormatting.GRAY);
    private static final ITextComponent field_244765_w = (new TranslationTextComponent("gui.socialInteractions.empty_blocked")).mergeStyle(TextFormatting.GRAY);
    private static final ITextComponent field_244766_x = new TranslationTextComponent("gui.socialInteractions.blocking_hint");
    private FilterList field_244673_t;
    private TextFieldWidget field_244674_u;
    private String field_244675_v = "";
    private SocialInteractionsScreen.Mode field_244676_w = SocialInteractionsScreen.Mode.ALL;
    private Button field_244677_x;
    private Button field_244678_y;
    private Button field_244760_E;
    private Button field_244761_F;
    @Nullable
    private ITextComponent field_244679_z;
    private int field_244662_A;
    private boolean field_244664_C;
    @Nullable
    private Runnable field_244665_D;

    public SocialInteractionsScreen()
    {
        super(new TranslationTextComponent("gui.socialInteractions.title"));
        this.func_244680_a(Minecraft.getInstance());
    }

    private int func_244689_k()
    {
        return Math.max(52, this.height - 128 - 16);
    }

    private int func_244690_l()
    {
        return this.func_244689_k() / 16;
    }

    private int func_244691_m()
    {
        return 80 + this.func_244690_l() * 16 - 8;
    }

    private int func_244692_n()
    {
        return (this.width - 238) / 2;
    }

    public String getNarrationMessage()
    {
        return super.getNarrationMessage() + ". " + this.field_244679_z.getString();
    }

    public void tick()
    {
        super.tick();
        this.field_244674_u.tick();
    }

    protected void init()
    {
        this.mc.keyboardListener.enableRepeatEvents(true);

        if (this.field_244664_C)
        {
            this.field_244673_t.updateSize(this.width, this.height, 88, this.func_244691_m());
        }
        else
        {
            this.field_244673_t = new FilterList(this, this.mc, this.width, this.height, 88, this.func_244691_m(), 36);
        }

        int i = this.field_244673_t.getRowWidth() / 3;
        int j = this.field_244673_t.getRowLeft();
        int k = this.field_244673_t.func_244736_r();
        int l = this.font.getStringPropertyWidth(field_244766_x) + 40;
        int i1 = 64 + 16 * this.func_244690_l();
        int j1 = (this.width - l) / 2;
        this.field_244677_x = this.addButton(new Button(j, 45, i, 20, field_244667_b, (p_244686_1_) ->
        {
            this.func_244682_a(SocialInteractionsScreen.Mode.ALL);
        }));
        this.field_244678_y = this.addButton(new Button((j + k - i) / 2 + 1, 45, i, 20, field_244668_c, (p_244681_1_) ->
        {
            this.func_244682_a(SocialInteractionsScreen.Mode.HIDDEN);
        }));
        this.field_244760_E = this.addButton(new Button(k - i + 1, 45, i, 20, field_244762_p, (p_244769_1_) ->
        {
            this.func_244682_a(SocialInteractionsScreen.Mode.BLOCKED);
        }));
        this.field_244761_F = this.addButton(new Button(j1, i1, l, 20, field_244766_x, (p_244767_1_) ->
        {
            this.mc.displayGuiScreen(new ConfirmOpenLinkScreen((p_244771_1_) -> {
                if (p_244771_1_)
                {
                    Util.getOSType().openURI("https://aka.ms/javablocking");
                }

                this.mc.displayGuiScreen(this);
            }, "https://aka.ms/javablocking", true));
        }));
        String s = this.field_244674_u != null ? this.field_244674_u.getText() : "";
        this.field_244674_u = new TextFieldWidget(this.font, this.func_244692_n() + 28, 78, 196, 16, field_244671_r)
        {
            protected IFormattableTextComponent getNarrationMessage()
            {
                return !SocialInteractionsScreen.this.field_244674_u.getText().isEmpty() && SocialInteractionsScreen.this.field_244673_t.func_244660_f() ? super.getNarrationMessage().appendString(", ").append(SocialInteractionsScreen.field_244764_u) : super.getNarrationMessage();
            }
        };
        this.field_244674_u.setMaxStringLength(16);
        this.field_244674_u.setEnableBackgroundDrawing(false);
        this.field_244674_u.setVisible(true);
        this.field_244674_u.setTextColor(16777215);
        this.field_244674_u.setText(s);
        this.field_244674_u.setResponder(this::func_244687_b);
        this.children.add(this.field_244674_u);
        this.children.add(this.field_244673_t);
        this.field_244664_C = true;
        this.func_244682_a(this.field_244676_w);
    }

    private void func_244682_a(SocialInteractionsScreen.Mode p_244682_1_)
    {
        this.field_244676_w = p_244682_1_;
        this.field_244677_x.setMessage(field_244667_b);
        this.field_244678_y.setMessage(field_244668_c);
        this.field_244760_E.setMessage(field_244762_p);
        Collection<UUID> collection;

        switch (p_244682_1_)
        {
            case ALL:
                this.field_244677_x.setMessage(field_244669_p);
                collection = this.mc.player.connection.func_244695_f();
                break;

            case HIDDEN:
                this.field_244678_y.setMessage(field_244670_q);
                collection = this.mc.func_244599_aA().func_244644_a();
                break;

            case BLOCKED:
                this.field_244760_E.setMessage(field_244763_s);
                FilterManager filtermanager = this.mc.func_244599_aA();
                collection = this.mc.player.connection.func_244695_f().stream().filter(filtermanager::func_244757_e).collect(Collectors.toSet());
                break;

            default:
                collection = ImmutableList.of();
        }

        this.field_244676_w = p_244682_1_;
        this.field_244673_t.func_244759_a(collection, this.field_244673_t.getScrollAmount());

        if (!this.field_244674_u.getText().isEmpty() && this.field_244673_t.func_244660_f() && !this.field_244674_u.isFocused())
        {
            NarratorChatListener.INSTANCE.say(field_244764_u.getString());
        }
        else if (collection.isEmpty())
        {
            if (p_244682_1_ == SocialInteractionsScreen.Mode.HIDDEN)
            {
                NarratorChatListener.INSTANCE.say(field_244672_s.getString());
            }
            else if (p_244682_1_ == SocialInteractionsScreen.Mode.BLOCKED)
            {
                NarratorChatListener.INSTANCE.say(field_244765_w.getString());
            }
        }
    }

    public void onClose()
    {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    public void renderBackground(MatrixStack matrixStack)
    {
        int i = this.func_244692_n() + 3;
        super.renderBackground(matrixStack);
        this.mc.getTextureManager().bindTexture(field_244666_a);
        this.blit(matrixStack, i, 64, 1, 1, 236, 8);
        int j = this.func_244690_l();

        for (int k = 0; k < j; ++k)
        {
            this.blit(matrixStack, i, 72 + 16 * k, 1, 10, 236, 16);
        }

        this.blit(matrixStack, i, 72 + 16 * j, 1, 27, 236, 8);
        this.blit(matrixStack, i + 10, 76, 243, 1, 12, 12);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.func_244680_a(this.mc);
        this.renderBackground(matrixStack);

        if (this.field_244679_z != null)
        {
            drawString(matrixStack, this.mc.fontRenderer, this.field_244679_z, this.func_244692_n() + 8, 35, -1);
        }

        if (!this.field_244673_t.func_244660_f())
        {
            this.field_244673_t.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        else if (!this.field_244674_u.getText().isEmpty())
        {
            drawCenteredString(matrixStack, this.mc.fontRenderer, field_244764_u, this.width / 2, (78 + this.func_244691_m()) / 2, -1);
        }
        else
        {
            switch (this.field_244676_w)
            {
                case HIDDEN:
                    drawCenteredString(matrixStack, this.mc.fontRenderer, field_244672_s, this.width / 2, (78 + this.func_244691_m()) / 2, -1);
                    break;

                case BLOCKED:
                    drawCenteredString(matrixStack, this.mc.fontRenderer, field_244765_w, this.width / 2, (78 + this.func_244691_m()) / 2, -1);
            }
        }

        if (!this.field_244674_u.isFocused() && this.field_244674_u.getText().isEmpty())
        {
            drawString(matrixStack, this.mc.fontRenderer, field_244671_r, this.field_244674_u.x, this.field_244674_u.y, -1);
        }
        else
        {
            this.field_244674_u.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        this.field_244761_F.visible = this.field_244676_w == SocialInteractionsScreen.Mode.BLOCKED;
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (this.field_244665_D != null)
        {
            this.field_244665_D.run();
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.field_244674_u.isFocused())
        {
            this.field_244674_u.mouseClicked(mouseX, mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button) || this.field_244673_t.mouseClicked(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (!this.field_244674_u.isFocused() && this.mc.gameSettings.field_244602_au.matchesKey(keyCode, scanCode))
        {
            this.mc.displayGuiScreen((Screen)null);
            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public boolean isPauseScreen()
    {
        return false;
    }

    private void func_244687_b(String p_244687_1_)
    {
        p_244687_1_ = p_244687_1_.toLowerCase(Locale.ROOT);

        if (!p_244687_1_.equals(this.field_244675_v))
        {
            this.field_244673_t.func_244658_a(p_244687_1_);
            this.field_244675_v = p_244687_1_;
            this.func_244682_a(this.field_244676_w);
        }
    }

    private void func_244680_a(Minecraft p_244680_1_)
    {
        int i = p_244680_1_.getConnection().getPlayerInfoMap().size();

        if (this.field_244662_A != i)
        {
            String s = "";
            ServerData serverdata = p_244680_1_.getCurrentServerData();

            if (p_244680_1_.isIntegratedServerRunning())
            {
                s = p_244680_1_.getIntegratedServer().getMOTD();
            }
            else if (serverdata != null)
            {
                s = serverdata.serverName;
            }

            if (i > 1)
            {
                this.field_244679_z = new TranslationTextComponent("gui.socialInteractions.server_label.multiple", s, i);
            }
            else
            {
                this.field_244679_z = new TranslationTextComponent("gui.socialInteractions.server_label.single", s, i);
            }

            this.field_244662_A = i;
        }
    }

    public void func_244683_a(NetworkPlayerInfo p_244683_1_)
    {
        this.field_244673_t.func_244657_a(p_244683_1_, this.field_244676_w);
    }

    public void func_244685_a(UUID p_244685_1_)
    {
        this.field_244673_t.func_244659_a(p_244685_1_);
    }

    public void func_244684_a(@Nullable Runnable p_244684_1_)
    {
        this.field_244665_D = p_244684_1_;
    }

    public static enum Mode
    {
        ALL,
        HIDDEN,
        BLOCKED;
    }
}
