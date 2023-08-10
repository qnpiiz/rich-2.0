package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.IScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RealmsServerSlotButton extends Button implements IScreen
{
    public static final ResourceLocation field_237712_a_ = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
    public static final ResourceLocation field_237713_b_ = new ResourceLocation("realms", "textures/gui/realms/empty_frame.png");
    public static final ResourceLocation field_237714_c_ = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_0.png");
    public static final ResourceLocation field_237715_d_ = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_2.png");
    public static final ResourceLocation field_237716_e_ = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_3.png");
    private static final ITextComponent field_243091_v = new TranslationTextComponent("mco.configure.world.slot.tooltip.active");
    private static final ITextComponent field_243092_w = new TranslationTextComponent("mco.configure.world.slot.tooltip.minigame");
    private static final ITextComponent field_243093_x = new TranslationTextComponent("mco.configure.world.slot.tooltip");
    private final Supplier<RealmsServer> field_223773_a;
    private final Consumer<ITextComponent> field_223774_b;
    private final int field_223776_d;
    private int field_223777_e;
    @Nullable
    private RealmsServerSlotButton.ServerData field_223778_f;

    public RealmsServerSlotButton(int p_i232195_1_, int p_i232195_2_, int p_i232195_3_, int p_i232195_4_, Supplier<RealmsServer> p_i232195_5_, Consumer<ITextComponent> p_i232195_6_, int p_i232195_7_, Button.IPressable p_i232195_8_)
    {
        super(p_i232195_1_, p_i232195_2_, p_i232195_3_, p_i232195_4_, StringTextComponent.EMPTY, p_i232195_8_);
        this.field_223773_a = p_i232195_5_;
        this.field_223776_d = p_i232195_7_;
        this.field_223774_b = p_i232195_6_;
    }

    @Nullable
    public RealmsServerSlotButton.ServerData func_237717_a_()
    {
        return this.field_223778_f;
    }

    public void tick()
    {
        ++this.field_223777_e;
        RealmsServer realmsserver = this.field_223773_a.get();

        if (realmsserver != null)
        {
            RealmsWorldOptions realmsworldoptions = realmsserver.field_230590_i_.get(this.field_223776_d);
            boolean flag2 = this.field_223776_d == 4;
            boolean flag;
            String s;
            long i;
            String s1;
            boolean flag1;

            if (flag2)
            {
                flag = realmsserver.field_230594_m_ == RealmsServer.ServerType.MINIGAME;
                s = "Minigame";
                i = (long)realmsserver.field_230597_p_;
                s1 = realmsserver.field_230598_q_;
                flag1 = realmsserver.field_230597_p_ == -1;
            }
            else
            {
                flag = realmsserver.field_230595_n_ == this.field_223776_d && realmsserver.field_230594_m_ != RealmsServer.ServerType.MINIGAME;
                s = realmsworldoptions.func_230787_a_(this.field_223776_d);
                i = realmsworldoptions.field_230624_k_;
                s1 = realmsworldoptions.field_230625_l_;
                flag1 = realmsworldoptions.field_230627_n_;
            }

            RealmsServerSlotButton.Action realmsserverslotbutton$action = func_237720_a_(realmsserver, flag, flag2);
            Pair<ITextComponent, ITextComponent> pair = this.func_237719_a_(realmsserver, s, flag1, flag2, realmsserverslotbutton$action);
            this.field_223778_f = new RealmsServerSlotButton.ServerData(flag, s, i, s1, flag1, flag2, realmsserverslotbutton$action, pair.getFirst());
            this.setMessage(pair.getSecond());
        }
    }

    private static RealmsServerSlotButton.Action func_237720_a_(RealmsServer p_237720_0_, boolean p_237720_1_, boolean p_237720_2_)
    {
        if (p_237720_1_)
        {
            if (!p_237720_0_.field_230591_j_ && p_237720_0_.field_230586_e_ != RealmsServer.Status.UNINITIALIZED)
            {
                return RealmsServerSlotButton.Action.JOIN;
            }
        }
        else
        {
            if (!p_237720_2_)
            {
                return RealmsServerSlotButton.Action.SWITCH_SLOT;
            }

            if (!p_237720_0_.field_230591_j_)
            {
                return RealmsServerSlotButton.Action.SWITCH_SLOT;
            }
        }

        return RealmsServerSlotButton.Action.NOTHING;
    }

    private Pair<ITextComponent, ITextComponent> func_237719_a_(RealmsServer p_237719_1_, String p_237719_2_, boolean p_237719_3_, boolean p_237719_4_, RealmsServerSlotButton.Action p_237719_5_)
    {
        if (p_237719_5_ == RealmsServerSlotButton.Action.NOTHING)
        {
            return Pair.of((ITextComponent)null, new StringTextComponent(p_237719_2_));
        }
        else
        {
            ITextComponent itextcomponent;

            if (p_237719_4_)
            {
                if (p_237719_3_)
                {
                    itextcomponent = StringTextComponent.EMPTY;
                }
                else
                {
                    itextcomponent = (new StringTextComponent(" ")).appendString(p_237719_2_).appendString(" ").appendString(p_237719_1_.field_230596_o_);
                }
            }
            else
            {
                itextcomponent = (new StringTextComponent(" ")).appendString(p_237719_2_);
            }

            ITextComponent itextcomponent1;

            if (p_237719_5_ == RealmsServerSlotButton.Action.JOIN)
            {
                itextcomponent1 = field_243091_v;
            }
            else
            {
                itextcomponent1 = p_237719_4_ ? field_243092_w : field_243093_x;
            }

            ITextComponent itextcomponent2 = itextcomponent1.deepCopy().append(itextcomponent);
            return Pair.of(itextcomponent1, itextcomponent2);
        }
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (this.field_223778_f != null)
        {
            this.func_237718_a_(matrixStack, this.x, this.y, mouseX, mouseY, this.field_223778_f.field_225110_a, this.field_223778_f.field_225111_b, this.field_223776_d, this.field_223778_f.field_225112_c, this.field_223778_f.field_225113_d, this.field_223778_f.field_225114_e, this.field_223778_f.field_225115_f, this.field_223778_f.field_225116_g, this.field_223778_f.field_225117_h);
        }
    }

    private void func_237718_a_(MatrixStack p_237718_1_, int p_237718_2_, int p_237718_3_, int p_237718_4_, int p_237718_5_, boolean p_237718_6_, String p_237718_7_, int p_237718_8_, long p_237718_9_, @Nullable String p_237718_11_, boolean p_237718_12_, boolean p_237718_13_, RealmsServerSlotButton.Action p_237718_14_, @Nullable ITextComponent p_237718_15_)
    {
        boolean flag = this.isHovered();

        if (this.isMouseOver((double)p_237718_4_, (double)p_237718_5_) && p_237718_15_ != null)
        {
            this.field_223774_b.accept(p_237718_15_);
        }

        Minecraft minecraft = Minecraft.getInstance();
        TextureManager texturemanager = minecraft.getTextureManager();

        if (p_237718_13_)
        {
            RealmsTextureManager.func_225202_a(String.valueOf(p_237718_9_), p_237718_11_);
        }
        else if (p_237718_12_)
        {
            texturemanager.bindTexture(field_237713_b_);
        }
        else if (p_237718_11_ != null && p_237718_9_ != -1L)
        {
            RealmsTextureManager.func_225202_a(String.valueOf(p_237718_9_), p_237718_11_);
        }
        else if (p_237718_8_ == 1)
        {
            texturemanager.bindTexture(field_237714_c_);
        }
        else if (p_237718_8_ == 2)
        {
            texturemanager.bindTexture(field_237715_d_);
        }
        else if (p_237718_8_ == 3)
        {
            texturemanager.bindTexture(field_237716_e_);
        }

        if (p_237718_6_)
        {
            float f = 0.85F + 0.15F * MathHelper.cos((float)this.field_223777_e * 0.2F);
            RenderSystem.color4f(f, f, f, 1.0F);
        }
        else
        {
            RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
        }

        blit(p_237718_1_, p_237718_2_ + 3, p_237718_3_ + 3, 0.0F, 0.0F, 74, 74, 74, 74);
        texturemanager.bindTexture(field_237712_a_);
        boolean flag1 = flag && p_237718_14_ != RealmsServerSlotButton.Action.NOTHING;

        if (flag1)
        {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
        else if (p_237718_6_)
        {
            RenderSystem.color4f(0.8F, 0.8F, 0.8F, 1.0F);
        }
        else
        {
            RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
        }

        blit(p_237718_1_, p_237718_2_, p_237718_3_, 0.0F, 0.0F, 80, 80, 80, 80);
        drawCenteredString(p_237718_1_, minecraft.fontRenderer, p_237718_7_, p_237718_2_ + 40, p_237718_3_ + 66, 16777215);
    }

    public static enum Action
    {
        NOTHING,
        SWITCH_SLOT,
        JOIN;
    }

    public static class ServerData
    {
        private final boolean field_225110_a;
        private final String field_225111_b;
        private final long field_225112_c;
        private final String field_225113_d;
        public final boolean field_225114_e;
        public final boolean field_225115_f;
        public final RealmsServerSlotButton.Action field_225116_g;
        @Nullable
        private final ITextComponent field_225117_h;

        ServerData(boolean p_i232196_1_, String p_i232196_2_, long p_i232196_3_, @Nullable String p_i232196_5_, boolean p_i232196_6_, boolean p_i232196_7_, RealmsServerSlotButton.Action p_i232196_8_, @Nullable ITextComponent p_i232196_9_)
        {
            this.field_225110_a = p_i232196_1_;
            this.field_225111_b = p_i232196_2_;
            this.field_225112_c = p_i232196_3_;
            this.field_225113_d = p_i232196_5_;
            this.field_225114_e = p_i232196_6_;
            this.field_225115_f = p_i232196_7_;
            this.field_225116_g = p_i232196_8_;
            this.field_225117_h = p_i232196_9_;
        }
    }
}
