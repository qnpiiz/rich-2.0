package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import javax.annotation.Nullable;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ConfirmBackupScreen extends Screen
{
    @Nullable
    private final Screen parentScreen;
    protected final ConfirmBackupScreen.ICallback callback;
    private final ITextComponent message;
    private final boolean field_212994_d;
    private IBidiRenderer field_243275_q = IBidiRenderer.field_243257_a;
    private CheckboxButton field_212996_j;

    public ConfirmBackupScreen(@Nullable Screen p_i51122_1_, ConfirmBackupScreen.ICallback p_i51122_2_, ITextComponent p_i51122_3_, ITextComponent p_i51122_4_, boolean p_i51122_5_)
    {
        super(p_i51122_3_);
        this.parentScreen = p_i51122_1_;
        this.callback = p_i51122_2_;
        this.message = p_i51122_4_;
        this.field_212994_d = p_i51122_5_;
    }

    protected void init()
    {
        super.init();
        this.field_243275_q = IBidiRenderer.func_243258_a(this.font, this.message, this.width - 50);
        int i = (this.field_243275_q.func_241862_a() + 1) * 9;
        this.addButton(new Button(this.width / 2 - 155, 100 + i, 150, 20, new TranslationTextComponent("selectWorld.backupJoinConfirmButton"), (p_212993_1_) ->
        {
            this.callback.proceed(true, this.field_212996_j.isChecked());
        }));
        this.addButton(new Button(this.width / 2 - 155 + 160, 100 + i, 150, 20, new TranslationTextComponent("selectWorld.backupJoinSkipButton"), (p_212992_1_) ->
        {
            this.callback.proceed(false, this.field_212996_j.isChecked());
        }));
        this.addButton(new Button(this.width / 2 - 155 + 80, 124 + i, 150, 20, DialogTexts.GUI_CANCEL, (p_212991_1_) ->
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }));
        this.field_212996_j = new CheckboxButton(this.width / 2 - 155 + 80, 76 + i, 150, 20, new TranslationTextComponent("selectWorld.backupEraseCache"), false);

        if (this.field_212994_d)
        {
            this.addButton(this.field_212996_j);
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 50, 16777215);
        this.field_243275_q.func_241863_a(matrixStack, this.width / 2, 70);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public boolean shouldCloseOnEsc()
    {
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.mc.displayGuiScreen(this.parentScreen);
            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public interface ICallback
    {
        void proceed(boolean p_proceed_1_, boolean p_proceed_2_);
    }
}
