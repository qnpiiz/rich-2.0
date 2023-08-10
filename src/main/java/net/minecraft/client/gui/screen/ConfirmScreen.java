package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class ConfirmScreen extends Screen
{
    private final ITextComponent messageLine2;
    private IBidiRenderer field_243276_q = IBidiRenderer.field_243257_a;

    /** The text shown for the first button in GuiYesNo */
    protected ITextComponent confirmButtonText;

    /** The text shown for the second button in GuiYesNo */
    protected ITextComponent cancelButtonText;
    private int ticksUntilEnable;
    protected final BooleanConsumer callbackFunction;

    public ConfirmScreen(BooleanConsumer _callbackFunction, ITextComponent _title, ITextComponent _messageLine2)
    {
        this(_callbackFunction, _title, _messageLine2, DialogTexts.GUI_YES, DialogTexts.GUI_NO);
    }

    public ConfirmScreen(BooleanConsumer p_i232270_1_, ITextComponent p_i232270_2_, ITextComponent p_i232270_3_, ITextComponent p_i232270_4_, ITextComponent p_i232270_5_)
    {
        super(p_i232270_2_);
        this.callbackFunction = p_i232270_1_;
        this.messageLine2 = p_i232270_3_;
        this.confirmButtonText = p_i232270_4_;
        this.cancelButtonText = p_i232270_5_;
    }

    public String getNarrationMessage()
    {
        return super.getNarrationMessage() + ". " + this.messageLine2.getString();
    }

    protected void init()
    {
        super.init();
        this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96, 150, 20, this.confirmButtonText, (p_213002_1_) ->
        {
            this.callbackFunction.accept(true);
        }));
        this.addButton(new Button(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, this.cancelButtonText, (p_213001_1_) ->
        {
            this.callbackFunction.accept(false);
        }));
        this.field_243276_q = IBidiRenderer.func_243258_a(this.font, this.messageLine2, this.width - 50);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 70, 16777215);
        this.field_243276_q.func_241863_a(matrixStack, this.width / 2, 90);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    /**
     * Sets the number of ticks to wait before enabling the buttons.
     */
    public void setButtonDelay(int ticksUntilEnableIn)
    {
        this.ticksUntilEnable = ticksUntilEnableIn;

        for (Widget widget : this.buttons)
        {
            widget.active = false;
        }
    }

    public void tick()
    {
        super.tick();

        if (--this.ticksUntilEnable == 0)
        {
            for (Widget widget : this.buttons)
            {
                widget.active = true;
            }
        }
    }

    public boolean shouldCloseOnEsc()
    {
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.callbackFunction.accept(false);
            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }
}
