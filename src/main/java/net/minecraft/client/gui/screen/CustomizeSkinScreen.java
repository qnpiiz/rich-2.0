package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.optifine.Lang;
import net.optifine.gui.GuiScreenCapeOF;

public class CustomizeSkinScreen extends SettingsScreen
{
    public CustomizeSkinScreen(Screen parentScreenIn, GameSettings gameSettingsIn)
    {
        super(parentScreenIn, gameSettingsIn, new TranslationTextComponent("options.skinCustomisation.title"));
    }

    protected void init()
    {
        int i = 0;

        for (PlayerModelPart playermodelpart : PlayerModelPart.values())
        {
            this.addButton(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, this.func_238655_a_(playermodelpart), (p_lambda$init$0_2_) ->
            {
                this.gameSettings.switchModelPartEnabled(playermodelpart);
                p_lambda$init$0_2_.setMessage(this.func_238655_a_(playermodelpart));
            }));
            ++i;
        }

        this.addButton(new OptionButton(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, AbstractOption.MAIN_HAND, AbstractOption.MAIN_HAND.getName(this.gameSettings), (p_lambda$init$1_1_) ->
        {
            AbstractOption.MAIN_HAND.setValueIndex(this.gameSettings, 1);
            this.gameSettings.saveOptions();
            p_lambda$init$1_1_.setMessage(AbstractOption.MAIN_HAND.getName(this.gameSettings));
            this.gameSettings.sendSettingsToServer();
        }));
        ++i;

        if (i % 2 == 1)
        {
            ++i;
        }

        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20, Lang.getComponent("of.options.skinCustomisation.ofCape"), (p_lambda$init$2_1_) ->
        {
            this.mc.displayGuiScreen(new GuiScreenCapeOF(this));
        }));
        i = i + 2;
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20, DialogTexts.GUI_DONE, (p_lambda$init$3_1_) ->
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }));
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 20, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private ITextComponent func_238655_a_(PlayerModelPart p_238655_1_)
    {
        return DialogTexts.getComposedOptionMessage(p_238655_1_.getName(), this.gameSettings.getModelParts().contains(p_238655_1_));
    }
}
