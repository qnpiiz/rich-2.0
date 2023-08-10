package net.optifine.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.optifine.Lang;

public class GuiAnimationSettingsOF extends GuiScreenOF
{
    private Screen prevScreen;
    private GameSettings settings;
    private static AbstractOption[] enumOptions = new AbstractOption[] {AbstractOption.ANIMATED_WATER, AbstractOption.ANIMATED_LAVA, AbstractOption.ANIMATED_FIRE, AbstractOption.ANIMATED_PORTAL, AbstractOption.ANIMATED_REDSTONE, AbstractOption.ANIMATED_EXPLOSION, AbstractOption.ANIMATED_FLAME, AbstractOption.ANIMATED_SMOKE, AbstractOption.VOID_PARTICLES, AbstractOption.WATER_PARTICLES, AbstractOption.RAIN_SPLASH, AbstractOption.PORTAL_PARTICLES, AbstractOption.POTION_PARTICLES, AbstractOption.DRIPPING_WATER_LAVA, AbstractOption.ANIMATED_TERRAIN, AbstractOption.ANIMATED_TEXTURES, AbstractOption.FIREWORK_PARTICLES, AbstractOption.PARTICLES};

    public GuiAnimationSettingsOF(Screen guiscreen, GameSettings gamesettings)
    {
        super(new StringTextComponent(I18n.format("of.options.animationsTitle")));
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

        this.addButton(new GuiButtonOF(210, this.width / 2 - 155, this.height / 6 + 168 + 11, 70, 20, Lang.get("of.options.animation.allOn")));
        this.addButton(new GuiButtonOF(211, this.width / 2 - 155 + 80, this.height / 6 + 168 + 11, 70, 20, Lang.get("of.options.animation.allOff")));
        this.addButton(new GuiScreenButtonOF(200, this.width / 2 + 5, this.height / 6 + 168 + 11, I18n.format("gui.done")));
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

                if (guibuttonof.id == 210)
                {
                    this.mc.gameSettings.setAllAnimations(true);
                }

                if (guibuttonof.id == 211)
                {
                    this.mc.gameSettings.setAllAnimations(false);
                }

                this.mc.updateWindowSize();
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
        drawCenteredString(matrixStackIn, this.mc.fontRenderer, this.title, this.width / 2, 15, 16777215);
        super.render(matrixStackIn, x, y, partialTicks);
    }
}
