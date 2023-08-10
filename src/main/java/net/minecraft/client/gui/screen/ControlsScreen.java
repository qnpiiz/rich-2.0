package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

public class ControlsScreen extends SettingsScreen
{
    /** The ID of the button that has been pressed. */
    public KeyBinding buttonId;
    public long time;
    private KeyBindingList keyBindingList;
    private Button buttonReset;

    public ControlsScreen(Screen screen, GameSettings settings)
    {
        super(screen, settings, new TranslationTextComponent("controls.title"));
    }

    protected void init()
    {
        this.addButton(new Button(this.width / 2 - 155, 18, 150, 20, new TranslationTextComponent("options.mouse_settings"), (p_213126_1_) ->
        {
            this.mc.displayGuiScreen(new MouseSettingsScreen(this, this.gameSettings));
        }));
        this.addButton(AbstractOption.AUTO_JUMP.createWidget(this.gameSettings, this.width / 2 - 155 + 160, 18, 150));
        this.keyBindingList = new KeyBindingList(this, this.mc);
        this.children.add(this.keyBindingList);
        this.buttonReset = this.addButton(new Button(this.width / 2 - 155, this.height - 29, 150, 20, new TranslationTextComponent("controls.resetAll"), (p_213125_1_) ->
        {
            for (KeyBinding keybinding : this.gameSettings.keyBindings)
            {
                keybinding.bind(keybinding.getDefault());
            }

            KeyBinding.resetKeyBindingArrayAndHash();
        }));
        this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, DialogTexts.GUI_DONE, (p_213124_1_) ->
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.buttonId != null)
        {
            this.gameSettings.setKeyBindingCode(this.buttonId, InputMappings.Type.MOUSE.getOrMakeInput(button));
            this.buttonId = null;
            KeyBinding.resetKeyBindingArrayAndHash();
            return true;
        }
        else
        {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (this.buttonId != null)
        {
            if (keyCode == 256)
            {
                this.gameSettings.setKeyBindingCode(this.buttonId, InputMappings.INPUT_INVALID);
            }
            else
            {
                this.gameSettings.setKeyBindingCode(this.buttonId, InputMappings.getInputByCode(keyCode, scanCode));
            }

            this.buttonId = null;
            this.time = Util.milliTime();
            KeyBinding.resetKeyBindingArrayAndHash();
            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.keyBindingList.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 8, 16777215);
        boolean flag = false;

        for (KeyBinding keybinding : this.gameSettings.keyBindings)
        {
            if (!keybinding.isDefault())
            {
                flag = true;
                break;
            }
        }

        this.buttonReset.active = flag;
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
