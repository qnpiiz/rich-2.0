package net.minecraft.client.gui.toasts;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SystemToast implements IToast
{
    private final SystemToast.Type type;
    private ITextComponent title;
    private List<IReorderingProcessor> field_238531_e_;
    private long firstDrawTime;
    private boolean newDisplay;
    private final int field_238532_h_;

    public SystemToast(SystemToast.Type typeIn, ITextComponent titleComponent, @Nullable ITextComponent subtitleComponent)
    {
        this(typeIn, titleComponent, func_238537_a_(subtitleComponent), 160);
    }

    public static SystemToast func_238534_a_(Minecraft p_238534_0_, SystemToast.Type p_238534_1_, ITextComponent p_238534_2_, ITextComponent p_238534_3_)
    {
        FontRenderer fontrenderer = p_238534_0_.fontRenderer;
        List<IReorderingProcessor> list = fontrenderer.trimStringToWidth(p_238534_3_, 200);
        int i = Math.max(200, list.stream().mapToInt(fontrenderer::func_243245_a).max().orElse(200));
        return new SystemToast(p_238534_1_, p_238534_2_, list, i + 30);
    }

    private SystemToast(SystemToast.Type p_i232264_1_, ITextComponent p_i232264_2_, List<IReorderingProcessor> p_i232264_3_, int p_i232264_4_)
    {
        this.type = p_i232264_1_;
        this.title = p_i232264_2_;
        this.field_238531_e_ = p_i232264_3_;
        this.field_238532_h_ = p_i232264_4_;
    }

    private static ImmutableList<IReorderingProcessor> func_238537_a_(@Nullable ITextComponent p_238537_0_)
    {
        return p_238537_0_ == null ? ImmutableList.of() : ImmutableList.of(p_238537_0_.func_241878_f());
    }

    public int func_230445_a_()
    {
        return this.field_238532_h_;
    }

    public IToast.Visibility func_230444_a_(MatrixStack p_230444_1_, ToastGui p_230444_2_, long p_230444_3_)
    {
        if (this.newDisplay)
        {
            this.firstDrawTime = p_230444_3_;
            this.newDisplay = false;
        }

        p_230444_2_.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        int i = this.func_230445_a_();
        int j = 12;

        if (i == 160 && this.field_238531_e_.size() <= 1)
        {
            p_230444_2_.blit(p_230444_1_, 0, 0, 0, 64, i, this.func_238540_d_());
        }
        else
        {
            int k = this.func_238540_d_() + Math.max(0, this.field_238531_e_.size() - 1) * 12;
            int l = 28;
            int i1 = Math.min(4, k - 28);
            this.func_238533_a_(p_230444_1_, p_230444_2_, i, 0, 0, 28);

            for (int j1 = 28; j1 < k - i1; j1 += 10)
            {
                this.func_238533_a_(p_230444_1_, p_230444_2_, i, 16, j1, Math.min(16, k - j1 - i1));
            }

            this.func_238533_a_(p_230444_1_, p_230444_2_, i, 32 - i1, k - i1, i1);
        }

        if (this.field_238531_e_ == null)
        {
            p_230444_2_.getMinecraft().fontRenderer.func_243248_b(p_230444_1_, this.title, 18.0F, 12.0F, -256);
        }
        else
        {
            p_230444_2_.getMinecraft().fontRenderer.func_243248_b(p_230444_1_, this.title, 18.0F, 7.0F, -256);

            for (int k1 = 0; k1 < this.field_238531_e_.size(); ++k1)
            {
                p_230444_2_.getMinecraft().fontRenderer.func_238422_b_(p_230444_1_, this.field_238531_e_.get(k1), 18.0F, (float)(18 + k1 * 12), -1);
            }
        }

        return p_230444_3_ - this.firstDrawTime < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
    }

    private void func_238533_a_(MatrixStack p_238533_1_, ToastGui p_238533_2_, int p_238533_3_, int p_238533_4_, int p_238533_5_, int p_238533_6_)
    {
        int i = p_238533_4_ == 0 ? 20 : 5;
        int j = Math.min(60, p_238533_3_ - i);
        p_238533_2_.blit(p_238533_1_, 0, p_238533_5_, 0, 64 + p_238533_4_, i, p_238533_6_);

        for (int k = i; k < p_238533_3_ - j; k += 64)
        {
            p_238533_2_.blit(p_238533_1_, k, p_238533_5_, 32, 64 + p_238533_4_, Math.min(64, p_238533_3_ - k - j), p_238533_6_);
        }

        p_238533_2_.blit(p_238533_1_, p_238533_3_ - j, p_238533_5_, 160 - j, 64 + p_238533_4_, j, p_238533_6_);
    }

    public void setDisplayedText(ITextComponent titleComponent, @Nullable ITextComponent subtitleComponent)
    {
        this.title = titleComponent;
        this.field_238531_e_ = func_238537_a_(subtitleComponent);
        this.newDisplay = true;
    }

    public SystemToast.Type getType()
    {
        return this.type;
    }

    public static void func_238536_a_(ToastGui p_238536_0_, SystemToast.Type p_238536_1_, ITextComponent p_238536_2_, @Nullable ITextComponent p_238536_3_)
    {
        p_238536_0_.add(new SystemToast(p_238536_1_, p_238536_2_, p_238536_3_));
    }

    public static void addOrUpdate(ToastGui p_193657_0_, SystemToast.Type p_193657_1_, ITextComponent p_193657_2_, @Nullable ITextComponent p_193657_3_)
    {
        SystemToast systemtoast = p_193657_0_.getToast(SystemToast.class, p_193657_1_);

        if (systemtoast == null)
        {
            func_238536_a_(p_193657_0_, p_193657_1_, p_193657_2_, p_193657_3_);
        }
        else
        {
            systemtoast.setDisplayedText(p_193657_2_, p_193657_3_);
        }
    }

    public static void func_238535_a_(Minecraft p_238535_0_, String p_238535_1_)
    {
        func_238536_a_(p_238535_0_.getToastGui(), SystemToast.Type.WORLD_ACCESS_FAILURE, new TranslationTextComponent("selectWorld.access_failure"), new StringTextComponent(p_238535_1_));
    }

    public static void func_238538_b_(Minecraft p_238538_0_, String p_238538_1_)
    {
        func_238536_a_(p_238538_0_.getToastGui(), SystemToast.Type.WORLD_ACCESS_FAILURE, new TranslationTextComponent("selectWorld.delete_failure"), new StringTextComponent(p_238538_1_));
    }

    public static void func_238539_c_(Minecraft p_238539_0_, String p_238539_1_)
    {
        func_238536_a_(p_238539_0_.getToastGui(), SystemToast.Type.PACK_COPY_FAILURE, new TranslationTextComponent("pack.copyFailure"), new StringTextComponent(p_238539_1_));
    }

    public static enum Type
    {
        TUTORIAL_HINT,
        NARRATOR_TOGGLE,
        WORLD_BACKUP,
        WORLD_GEN_SETTINGS_TRANSFER,
        PACK_LOAD_FAILURE,
        WORLD_ACCESS_FAILURE,
        PACK_COPY_FAILURE;
    }
}
