package net.optifine.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public class GuiQualitySettingsOF extends GuiScreenOF
{
    private Screen prevScreen;
    private GameSettings settings;
    private static AbstractOption[] enumOptions = new AbstractOption[] {AbstractOption.MIPMAP_LEVELS, AbstractOption.MIPMAP_TYPE, AbstractOption.AF_LEVEL, AbstractOption.AA_LEVEL, AbstractOption.EMISSIVE_TEXTURES, AbstractOption.RANDOM_ENTITIES, AbstractOption.BETTER_GRASS, AbstractOption.BETTER_SNOW, AbstractOption.CUSTOM_FONTS, AbstractOption.CUSTOM_COLORS, AbstractOption.CONNECTED_TEXTURES, AbstractOption.NATURAL_TEXTURES, AbstractOption.CUSTOM_SKY, AbstractOption.CUSTOM_ITEMS, AbstractOption.CUSTOM_ENTITY_MODELS, AbstractOption.CUSTOM_GUIS, AbstractOption.SCREEN_EFFECT_SCALE_SLIDER, AbstractOption.FOV_EFFECT_SCALE_SLIDER};
    private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());

    public GuiQualitySettingsOF(Screen guiscreen, GameSettings gamesettings)
    {
        super(new StringTextComponent(I18n.format("of.options.qualityTitle")));
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }

    public void init()
    {
        this.buttonList.clear();

        for (int i = 0; i < enumOptions.length; ++i)
        {
            AbstractOption abstractoption = enumOptions[i];
            int j = this.width / 2 - 155 + i % 2 * 160;
            int k = this.height / 6 + 21 * (i / 2) - 12;
            this.addButton(abstractoption.createWidget(this.mc.gameSettings, j, k, 150));
        }

        this.addButton(new GuiButtonOF(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done")));
    }

    protected void actionPerformed(Widget guiElement)
    {
        if (guiElement instanceof GuiButtonOF)
        {
            GuiButtonOF guibuttonof = (GuiButtonOF)guiElement;

            if (guibuttonof.active)
            {
                if (guibuttonof.id == 200)
                {
                    this.mc.gameSettings.saveOptions();
                    this.mc.displayGuiScreen(this.prevScreen);
                }
            }
        }
    }

    public void onClose()
    {
        this.mc.gameSettings.saveOptions();
        super.onClose();
    }

    public void render(MatrixStack matrixStackIn, int x, int y, float partialTicks)
    {
        this.renderBackground(matrixStackIn);
        drawCenteredString(matrixStackIn, this.fontRenderer, this.title, this.width / 2, 15, 16777215);
        super.render(matrixStackIn, x, y, partialTicks);
        this.tooltipManager.drawTooltips(matrixStackIn, x, y, this.buttonList);
    }
}
