package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DisconnectedScreen extends Screen
{
    private final ITextComponent message;
    private IBidiRenderer field_243289_b = IBidiRenderer.field_243257_a;
    private final Screen nextScreen;
    private int textHeight;

    public DisconnectedScreen(Screen p_i242056_1_, ITextComponent p_i242056_2_, ITextComponent p_i242056_3_)
    {
        super(p_i242056_2_);
        this.nextScreen = p_i242056_1_;
        this.message = p_i242056_3_;
    }

    public boolean shouldCloseOnEsc()
    {
        return false;
    }

    protected void init()
    {
        this.field_243289_b = IBidiRenderer.func_243258_a(this.font, this.message, this.width - 50);
        this.textHeight = this.field_243289_b.func_241862_a() * 9;
        this.addButton(new Button(this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + 9, this.height - 30), 200, 20, new TranslationTextComponent("gui.toMenu"), (p_213033_1_) ->
        {
            this.mc.displayGuiScreen(this.nextScreen);
        }));
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, this.height / 2 - this.textHeight / 2 - 9 * 2, 11184810);
        this.field_243289_b.func_241863_a(matrixStack, this.width / 2, this.height / 2 - this.textHeight / 2);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
