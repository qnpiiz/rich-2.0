package net.minecraft.client.gui.screen;

import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.util.text.TranslationTextComponent;
import net.optifine.gui.TooltipManager;
import net.optifine.gui.TooltipProviderOptions;

public class ChatOptionsScreen extends WithNarratorSettingsScreen
{
    private static final AbstractOption[] CHAT_OPTIONS = new AbstractOption[] {AbstractOption.CHAT_VISIBILITY, AbstractOption.CHAT_COLOR, AbstractOption.CHAT_LINKS, AbstractOption.CHAT_LINKS_PROMPT, AbstractOption.CHAT_OPACITY, AbstractOption.ACCESSIBILITY_TEXT_BACKGROUND_OPACITY, AbstractOption.CHAT_SCALE, AbstractOption.LINE_SPACING, AbstractOption.DELAY_INSTANT, AbstractOption.CHAT_WIDTH, AbstractOption.CHAT_HEIGHT_FOCUSED, AbstractOption.CHAT_HEIGHT_UNFOCUSED, AbstractOption.CHAT_BACKGROUND, AbstractOption.CHAT_SHADOW, AbstractOption.NARRATOR, AbstractOption.AUTO_SUGGEST_COMMANDS, AbstractOption.field_244786_G, AbstractOption.REDUCED_DEBUG_INFO};
    private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());

    public ChatOptionsScreen(Screen parentScreenIn, GameSettings gameSettingsIn)
    {
        super(parentScreenIn, gameSettingsIn, new TranslationTextComponent("options.chat.title"), CHAT_OPTIONS);
    }
}
