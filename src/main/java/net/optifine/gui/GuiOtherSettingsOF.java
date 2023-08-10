package net.optifine.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.FullscreenResolutionOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public class GuiOtherSettingsOF extends GuiScreenOF
{
    private Screen prevScreen;
    private GameSettings settings;
    private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());

    public GuiOtherSettingsOF(Screen guiscreen, GameSettings gamesettings)
    {
        super(new StringTextComponent(I18n.format("of.options.otherTitle")));
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }

    public void init()
    {
        this.buttonList.clear();
        AbstractOption abstractoption = new FullscreenResolutionOption(this.mc.getMainWindow());
        AbstractOption[] aabstractoption = new AbstractOption[] {AbstractOption.LAGOMETER, AbstractOption.PROFILER, AbstractOption.SHOW_FPS, AbstractOption.ADVANCED_TOOLTIPS, AbstractOption.WEATHER, AbstractOption.TIME, AbstractOption.FULLSCREEN, AbstractOption.AUTOSAVE_TICKS, AbstractOption.SCREENSHOT_SIZE, AbstractOption.SHOW_GL_ERRORS, abstractoption, null};

        for (int i = 0; i < aabstractoption.length; ++i)
        {
            AbstractOption abstractoption1 = aabstractoption[i];
            int j = this.width / 2 - 155 + i % 2 * 160;
            int k = this.height / 6 + 21 * (i / 2) - 12;
            Widget widget = this.addButton(abstractoption1.createWidget(this.mc.gameSettings, j, k, 150));

            if (abstractoption1 == abstractoption)
            {
                widget.setWidth(310);
                ++i;
            }
        }

        this.addButton(new GuiButtonOF(210, this.width / 2 - 100, this.height / 6 + 168 + 11 - 44, I18n.format("of.options.other.reset")));
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
                    this.mc.getMainWindow().update();
                    this.mc.displayGuiScreen(this.prevScreen);
                }

                if (guibuttonof.id == 210)
                {
                    this.mc.gameSettings.saveOptions();
                    String s = I18n.format("of.message.other.reset");
                    ConfirmScreen confirmscreen = new ConfirmScreen(this::confirmResult, new StringTextComponent(s), new StringTextComponent(""));
                    this.mc.displayGuiScreen(confirmscreen);
                }
            }
        }
    }

    public void onClose()
    {
        this.mc.gameSettings.saveOptions();
        this.mc.getMainWindow().update();
        super.onClose();
    }

    public void confirmResult(boolean flag)
    {
        if (flag)
        {
            this.mc.gameSettings.resetSettings();
        }

        this.mc.displayGuiScreen(this);
    }

    public void render(MatrixStack matrixStackIn, int x, int y, float partialTicks)
    {
        this.renderBackground(matrixStackIn);
        drawCenteredString(matrixStackIn, this.fontRenderer, this.title, this.width / 2, 15, 16777215);
        super.render(matrixStackIn, x, y, partialTicks);
        this.tooltipManager.drawTooltips(matrixStackIn, x, y, this.buttonList);
    }
}
