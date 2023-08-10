package net.optifine.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public class GuiPerformanceSettingsOF extends GuiScreenOF
{
    private Screen prevScreen;
    private GameSettings settings;
    private static AbstractOption[] enumOptions = new AbstractOption[] {AbstractOption.RENDER_REGIONS, AbstractOption.FAST_RENDER, AbstractOption.SMART_ANIMATIONS, AbstractOption.FAST_MATH, AbstractOption.SMOOTH_FPS, AbstractOption.SMOOTH_WORLD, AbstractOption.CHUNK_UPDATES, AbstractOption.CHUNK_UPDATES_DYNAMIC, AbstractOption.LAZY_CHUNK_LOADING};
    private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());

    public GuiPerformanceSettingsOF(Screen guiscreen, GameSettings gamesettings)
    {
        super(new StringTextComponent(I18n.format("of.options.performanceTitle")));
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
