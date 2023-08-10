package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class DatapackFailureScreen extends Screen
{
    private IBidiRenderer field_243284_a = IBidiRenderer.field_243257_a;
    private final Runnable field_238620_b_;

    public DatapackFailureScreen(Runnable p_i232276_1_)
    {
        super(new TranslationTextComponent("datapackFailure.title"));
        this.field_238620_b_ = p_i232276_1_;
    }

    protected void init()
    {
        super.init();
        this.field_243284_a = IBidiRenderer.func_243258_a(this.font, this.getTitle(), this.width - 50);
        this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96, 150, 20, new TranslationTextComponent("datapackFailure.safeMode"), (p_238622_1_) ->
        {
            this.field_238620_b_.run();
        }));
        this.addButton(new Button(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, new TranslationTextComponent("gui.toTitle"), (p_238621_1_) ->
        {
            this.mc.displayGuiScreen((Screen)null);
        }));
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.field_243284_a.func_241863_a(matrixStack, this.width / 2, 70);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public boolean shouldCloseOnEsc()
    {
        return false;
    }
}
