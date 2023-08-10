package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ConfirmOpenLinkScreen extends ConfirmScreen
{
    /** Text to warn players from opening unsafe links. */
    private final ITextComponent openLinkWarning;

    /** Label for the Copy to Clipboard button. */
    private final ITextComponent copyLinkButtonText;
    private final String linkText;
    private final boolean showSecurityWarning;

    public ConfirmOpenLinkScreen(BooleanConsumer p_i51121_1_, String p_i51121_2_, boolean p_i51121_3_)
    {
        super(p_i51121_1_, new TranslationTextComponent(p_i51121_3_ ? "chat.link.confirmTrusted" : "chat.link.confirm"), new StringTextComponent(p_i51121_2_));
        this.confirmButtonText = (ITextComponent)(p_i51121_3_ ? new TranslationTextComponent("chat.link.open") : DialogTexts.GUI_YES);
        this.cancelButtonText = p_i51121_3_ ? DialogTexts.GUI_CANCEL : DialogTexts.GUI_NO;
        this.copyLinkButtonText = new TranslationTextComponent("chat.copy");
        this.openLinkWarning = new TranslationTextComponent("chat.link.warning");
        this.showSecurityWarning = !p_i51121_3_;
        this.linkText = p_i51121_2_;
    }

    protected void init()
    {
        super.init();
        this.buttons.clear();
        this.children.clear();
        this.addButton(new Button(this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.confirmButtonText, (p_213006_1_) ->
        {
            this.callbackFunction.accept(true);
        }));
        this.addButton(new Button(this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copyLinkButtonText, (p_213005_1_) ->
        {
            this.copyLinkToClipboard();
            this.callbackFunction.accept(false);
        }));
        this.addButton(new Button(this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.cancelButtonText, (p_213004_1_) ->
        {
            this.callbackFunction.accept(false);
        }));
    }

    /**
     * Copies the link to the system clipboard.
     */
    public void copyLinkToClipboard()
    {
        this.mc.keyboardListener.setClipboardString(this.linkText);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (this.showSecurityWarning)
        {
            drawCenteredString(matrixStack, this.font, this.openLinkWarning, this.width / 2, 110, 16764108);
        }
    }
}
