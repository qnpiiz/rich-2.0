package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;

public class GPUWarningScreen extends Screen
{
    private final ITextProperties warnings;
    private final ImmutableList<GPUWarningScreen.Option> options;
    private IBidiRenderer warningRenderer = IBidiRenderer.field_243257_a;
    private int field_241588_p_;
    private int field_241589_q_;

    protected GPUWarningScreen(ITextComponent title, List<ITextProperties> warnings, ImmutableList<GPUWarningScreen.Option> options)
    {
        super(title);
        this.warnings = ITextProperties.func_240654_a_(warnings);
        this.options = options;
    }

    public String getNarrationMessage()
    {
        return super.getNarrationMessage() + ". " + this.warnings.getString();
    }

    public void init(Minecraft minecraft, int width, int height)
    {
        super.init(minecraft, width, height);

        for (GPUWarningScreen.Option gpuwarningscreen$option : this.options)
        {
            this.field_241589_q_ = Math.max(this.field_241589_q_, 20 + this.font.getStringPropertyWidth(gpuwarningscreen$option.field_241590_a_) + 20);
        }

        int l = 5 + this.field_241589_q_ + 5;
        int i1 = l * this.options.size();
        this.warningRenderer = IBidiRenderer.func_243258_a(this.font, this.warnings, i1);
        int i = this.warningRenderer.func_241862_a() * 9;
        this.field_241588_p_ = (int)((double)height / 2.0D - (double)i / 2.0D);
        int j = this.field_241588_p_ + i + 9 * 2;
        int k = (int)((double)width / 2.0D - (double)i1 / 2.0D);

        for (GPUWarningScreen.Option gpuwarningscreen$option1 : this.options)
        {
            this.addButton(new Button(k, j, this.field_241589_q_, 20, gpuwarningscreen$option1.field_241590_a_, gpuwarningscreen$option1.field_241591_b_));
            k += l;
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderDirtBackground(0);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, this.field_241588_p_ - 9 * 2, -1);
        this.warningRenderer.func_241863_a(matrixStack, this.width / 2, this.field_241588_p_);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public boolean shouldCloseOnEsc()
    {
        return false;
    }

    public static final class Option
    {
        private final ITextComponent field_241590_a_;
        private final Button.IPressable field_241591_b_;

        public Option(ITextComponent p_i241251_1_, Button.IPressable p_i241251_2_)
        {
            this.field_241590_a_ = p_i241251_1_;
            this.field_241591_b_ = p_i241251_2_;
        }
    }
}
