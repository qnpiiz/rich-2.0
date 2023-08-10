package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.SoundSlider;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TranslationTextComponent;

public class OptionsSoundsScreen extends SettingsScreen
{
    public OptionsSoundsScreen(Screen parentIn, GameSettings settingsIn)
    {
        super(parentIn, settingsIn, new TranslationTextComponent("options.sounds.title"));
    }

    protected void init()
    {
        int i = 0;
        this.addButton(new SoundSlider(this.mc, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.MASTER, 310));
        i = i + 2;

        for (SoundCategory soundcategory : SoundCategory.values())
        {
            if (soundcategory != SoundCategory.MASTER)
            {
                this.addButton(new SoundSlider(this.mc, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundcategory, 150));
                ++i;
            }
        }

        int j = this.width / 2 - 75;
        int k = this.height / 6 - 12;
        ++i;
        this.addButton(new OptionButton(j, k + 24 * (i >> 1), 150, 20, AbstractOption.SHOW_SUBTITLES, AbstractOption.SHOW_SUBTITLES.func_238152_c_(this.gameSettings), (p_213105_1_) ->
        {
            AbstractOption.SHOW_SUBTITLES.nextValue(this.mc.gameSettings);
            p_213105_1_.setMessage(AbstractOption.SHOW_SUBTITLES.func_238152_c_(this.mc.gameSettings));
            this.mc.gameSettings.saveOptions();
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, DialogTexts.GUI_DONE, (p_213104_1_) ->
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }));
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 15, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
