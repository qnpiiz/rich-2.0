package net.minecraft.client.gui;

import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WithNarratorSettingsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

public class AccessibilityScreen extends WithNarratorSettingsScreen
{
    private static final AbstractOption[] OPTIONS = new AbstractOption[] {AbstractOption.NARRATOR, AbstractOption.SHOW_SUBTITLES, AbstractOption.ACCESSIBILITY_TEXT_BACKGROUND_OPACITY, AbstractOption.ACCESSIBILITY_TEXT_BACKGROUND, AbstractOption.CHAT_OPACITY, AbstractOption.LINE_SPACING, AbstractOption.DELAY_INSTANT, AbstractOption.AUTO_JUMP, AbstractOption.SNEAK, AbstractOption.SPRINT, AbstractOption.SCREEN_EFFECT_SCALE_SLIDER, AbstractOption.FOV_EFFECT_SCALE_SLIDER};

    public AccessibilityScreen(Screen parentScreen, GameSettings settings)
    {
        super(parentScreen, settings, new TranslationTextComponent("options.accessibility.title"), OPTIONS);
    }

    protected void func_244718_c()
    {
        this.addButton(new Button(this.width / 2 - 155, this.height - 27, 150, 20, new TranslationTextComponent("options.accessibility.link"), (p_244738_1_) ->
        {
            this.mc.displayGuiScreen(new ConfirmOpenLinkScreen((p_244739_1_) -> {
                if (p_244739_1_)
                {
                    Util.getOSType().openURI("https://aka.ms/MinecraftJavaAccessibility");
                }

                this.mc.displayGuiScreen(this);
            }, "https://aka.ms/MinecraftJavaAccessibility", true));
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height - 27, 150, 20, DialogTexts.GUI_DONE, (p_244737_1_) ->
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }));
    }
}
