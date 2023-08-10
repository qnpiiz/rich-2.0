package net.minecraft.client.gui.social;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class FilterListEntry extends AbstractOptionList.Entry<FilterListEntry>
{
    private final Minecraft field_244620_e;
    private final List<IGuiEventListener> field_244621_f;
    private final UUID field_244622_g;
    private final String field_244623_h;
    private final Supplier<ResourceLocation> field_244742_j;
    private boolean field_244625_j;
    @Nullable
    private Button field_244626_k;
    @Nullable
    private Button field_244627_l;
    private final List<IReorderingProcessor> field_244628_m;
    private final List<IReorderingProcessor> field_244629_n;
    private float field_244630_o;
    private static final ITextComponent field_244743_q = (new TranslationTextComponent("gui.socialInteractions.status_hidden")).mergeStyle(TextFormatting.ITALIC);
    private static final ITextComponent field_244744_r = (new TranslationTextComponent("gui.socialInteractions.status_blocked")).mergeStyle(TextFormatting.ITALIC);
    private static final ITextComponent field_244745_s = (new TranslationTextComponent("gui.socialInteractions.status_offline")).mergeStyle(TextFormatting.ITALIC);
    private static final ITextComponent field_244746_t = (new TranslationTextComponent("gui.socialInteractions.status_hidden_offline")).mergeStyle(TextFormatting.ITALIC);
    private static final ITextComponent field_244747_u = (new TranslationTextComponent("gui.socialInteractions.status_blocked_offline")).mergeStyle(TextFormatting.ITALIC);
    public static final int field_244616_a = ColorHelper.PackedColor.packColor(190, 0, 0, 0);
    public static final int field_244617_b = ColorHelper.PackedColor.packColor(255, 74, 74, 74);
    public static final int field_244618_c = ColorHelper.PackedColor.packColor(255, 48, 48, 48);
    public static final int field_244619_d = ColorHelper.PackedColor.packColor(255, 255, 255, 255);
    public static final int field_244741_e = ColorHelper.PackedColor.packColor(140, 255, 255, 255);

    public FilterListEntry(Minecraft p_i242129_1_, SocialInteractionsScreen p_i242129_2_, UUID p_i242129_3_, String p_i242129_4_, Supplier<ResourceLocation> p_i242129_5_)
    {
        this.field_244620_e = p_i242129_1_;
        this.field_244622_g = p_i242129_3_;
        this.field_244623_h = p_i242129_4_;
        this.field_244742_j = p_i242129_5_;
        this.field_244628_m = p_i242129_1_.fontRenderer.trimStringToWidth(new TranslationTextComponent("gui.socialInteractions.tooltip.hide", p_i242129_4_), 150);
        this.field_244629_n = p_i242129_1_.fontRenderer.trimStringToWidth(new TranslationTextComponent("gui.socialInteractions.tooltip.show", p_i242129_4_), 150);
        FilterManager filtermanager = p_i242129_1_.func_244599_aA();

        if (!p_i242129_1_.player.getGameProfile().getId().equals(p_i242129_3_) && !filtermanager.func_244757_e(p_i242129_3_))
        {
            this.field_244626_k = new ImageButton(0, 0, 20, 20, 0, 38, 20, SocialInteractionsScreen.field_244666_a, 256, 256, (p_244751_4_) ->
            {
                filtermanager.func_244646_a(p_i242129_3_);
                this.func_244635_a(true, new TranslationTextComponent("gui.socialInteractions.hidden_in_chat", p_i242129_4_));
            }, (p_244637_3_, p_244637_4_, p_244637_5_, p_244637_6_) ->
            {
                this.field_244630_o += p_i242129_1_.getTickLength();

                if (this.field_244630_o >= 10.0F)
                {
                    p_i242129_2_.func_244684_a(() ->
                    {
                        func_244634_a(p_i242129_2_, p_244637_4_, this.field_244628_m, p_244637_5_, p_244637_6_);
                    });
                }
            }, new TranslationTextComponent("gui.socialInteractions.hide"))
            {
                protected IFormattableTextComponent getNarrationMessage()
                {
                    return FilterListEntry.this.func_244750_a(super.getNarrationMessage());
                }
            };
            this.field_244627_l = new ImageButton(0, 0, 20, 20, 20, 38, 20, SocialInteractionsScreen.field_244666_a, 256, 256, (p_244749_4_) ->
            {
                filtermanager.func_244647_b(p_i242129_3_);
                this.func_244635_a(false, new TranslationTextComponent("gui.socialInteractions.shown_in_chat", p_i242129_4_));
            }, (p_244631_3_, p_244631_4_, p_244631_5_, p_244631_6_) ->
            {
                this.field_244630_o += p_i242129_1_.getTickLength();

                if (this.field_244630_o >= 10.0F)
                {
                    p_i242129_2_.func_244684_a(() ->
                    {
                        func_244634_a(p_i242129_2_, p_244631_4_, this.field_244629_n, p_244631_5_, p_244631_6_);
                    });
                }
            }, new TranslationTextComponent("gui.socialInteractions.show"))
            {
                protected IFormattableTextComponent getNarrationMessage()
                {
                    return FilterListEntry.this.func_244750_a(super.getNarrationMessage());
                }
            };
            this.field_244627_l.visible = filtermanager.func_244648_c(p_i242129_3_);
            this.field_244626_k.visible = !this.field_244627_l.visible;
            this.field_244621_f = ImmutableList.of(this.field_244626_k, this.field_244627_l);
        }
        else
        {
            this.field_244621_f = ImmutableList.of();
        }
    }

    public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_)
    {
        int i = p_230432_4_ + 4;
        int j = p_230432_3_ + (p_230432_6_ - 24) / 2;
        int k = i + 24 + 4;
        ITextComponent itextcomponent = this.func_244752_d();
        int l;

        if (itextcomponent == StringTextComponent.EMPTY)
        {
            AbstractGui.fill(p_230432_1_, p_230432_4_, p_230432_3_, p_230432_4_ + p_230432_5_, p_230432_3_ + p_230432_6_, field_244617_b);
            l = p_230432_3_ + (p_230432_6_ - 9) / 2;
        }
        else
        {
            AbstractGui.fill(p_230432_1_, p_230432_4_, p_230432_3_, p_230432_4_ + p_230432_5_, p_230432_3_ + p_230432_6_, field_244618_c);
            l = p_230432_3_ + (p_230432_6_ - (9 + 9)) / 2;
            this.field_244620_e.fontRenderer.func_243248_b(p_230432_1_, itextcomponent, (float)k, (float)(l + 12), field_244741_e);
        }

        this.field_244620_e.getTextureManager().bindTexture(this.field_244742_j.get());
        AbstractGui.blit(p_230432_1_, i, j, 24, 24, 8.0F, 8.0F, 8, 8, 64, 64);
        RenderSystem.enableBlend();
        AbstractGui.blit(p_230432_1_, i, j, 24, 24, 40.0F, 8.0F, 8, 8, 64, 64);
        RenderSystem.disableBlend();
        this.field_244620_e.fontRenderer.drawString(p_230432_1_, this.field_244623_h, (float)k, (float)l, field_244619_d);

        if (this.field_244625_j)
        {
            AbstractGui.fill(p_230432_1_, i, j, i + 24, j + 24, field_244616_a);
        }

        if (this.field_244626_k != null && this.field_244627_l != null)
        {
            float f = this.field_244630_o;
            this.field_244626_k.x = p_230432_4_ + (p_230432_5_ - this.field_244626_k.getWidth() - 4);
            this.field_244626_k.y = p_230432_3_ + (p_230432_6_ - this.field_244626_k.getHeightRealms()) / 2;
            this.field_244626_k.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
            this.field_244627_l.x = p_230432_4_ + (p_230432_5_ - this.field_244627_l.getWidth() - 4);
            this.field_244627_l.y = p_230432_3_ + (p_230432_6_ - this.field_244627_l.getHeightRealms()) / 2;
            this.field_244627_l.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);

            if (f == this.field_244630_o)
            {
                this.field_244630_o = 0.0F;
            }
        }
    }

    public List <? extends IGuiEventListener > getEventListeners()
    {
        return this.field_244621_f;
    }

    public String func_244636_b()
    {
        return this.field_244623_h;
    }

    public UUID func_244640_c()
    {
        return this.field_244622_g;
    }

    public void func_244641_c(boolean p_244641_1_)
    {
        this.field_244625_j = p_244641_1_;
    }

    private void func_244635_a(boolean p_244635_1_, ITextComponent p_244635_2_)
    {
        this.field_244627_l.visible = p_244635_1_;
        this.field_244626_k.visible = !p_244635_1_;
        this.field_244620_e.ingameGUI.getChatGUI().printChatMessage(p_244635_2_);
        NarratorChatListener.INSTANCE.say(p_244635_2_.getString());
    }

    private IFormattableTextComponent func_244750_a(IFormattableTextComponent p_244750_1_)
    {
        ITextComponent itextcomponent = this.func_244752_d();
        return itextcomponent == StringTextComponent.EMPTY ? (new StringTextComponent(this.field_244623_h)).appendString(", ").append(p_244750_1_) : (new StringTextComponent(this.field_244623_h)).appendString(", ").append(itextcomponent).appendString(", ").append(p_244750_1_);
    }

    private ITextComponent func_244752_d()
    {
        boolean flag = this.field_244620_e.func_244599_aA().func_244648_c(this.field_244622_g);
        boolean flag1 = this.field_244620_e.func_244599_aA().func_244757_e(this.field_244622_g);

        if (flag1 && this.field_244625_j)
        {
            return field_244747_u;
        }
        else if (flag && this.field_244625_j)
        {
            return field_244746_t;
        }
        else if (flag1)
        {
            return field_244744_r;
        }
        else if (flag)
        {
            return field_244743_q;
        }
        else
        {
            return this.field_244625_j ? field_244745_s : StringTextComponent.EMPTY;
        }
    }

    private static void func_244634_a(SocialInteractionsScreen p_244634_0_, MatrixStack p_244634_1_, List<IReorderingProcessor> p_244634_2_, int p_244634_3_, int p_244634_4_)
    {
        p_244634_0_.renderTooltip(p_244634_1_, p_244634_2_, p_244634_3_, p_244634_4_);
        p_244634_0_.func_244684_a((Runnable)null);
    }
}
