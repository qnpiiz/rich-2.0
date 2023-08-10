package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Arrays;
import java.util.stream.Stream;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.TranslationTextComponent;

public class MouseSettingsScreen extends SettingsScreen
{
    private OptionsRowList field_213045_b;
    private static final AbstractOption[] OPTIONS = new AbstractOption[] {AbstractOption.SENSITIVITY, AbstractOption.INVERT_MOUSE, AbstractOption.MOUSE_WHEEL_SENSITIVITY, AbstractOption.DISCRETE_MOUSE_SCROLL, AbstractOption.TOUCHSCREEN};

    public MouseSettingsScreen(Screen p_i225929_1_, GameSettings p_i225929_2_)
    {
        super(p_i225929_1_, p_i225929_2_, new TranslationTextComponent("options.mouse_settings.title"));
    }

    protected void init()
    {
        this.field_213045_b = new OptionsRowList(this.mc, this.width, this.height, 32, this.height - 32, 25);

        if (InputMappings.func_224790_a())
        {
            this.field_213045_b.addOptions(Stream.concat(Arrays.stream(OPTIONS), Stream.of(AbstractOption.RAW_MOUSE_INPUT)).toArray((p_223702_0_) ->
            {
                return new AbstractOption[p_223702_0_];
            }));
        }
        else
        {
            this.field_213045_b.addOptions(OPTIONS);
        }

        this.children.add(this.field_213045_b);
        this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, DialogTexts.GUI_DONE, (p_223703_1_) ->
        {
            this.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parentScreen);
        }));
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.field_213045_b.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 5, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
