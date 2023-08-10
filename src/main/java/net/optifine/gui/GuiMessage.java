package net.optifine.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.optifine.Config;

public class GuiMessage extends GuiScreenOF
{
    private Screen parentScreen;
    private ITextComponent messageLine1;
    private ITextComponent messageLine2;
    private final List<IReorderingProcessor> listLines2 = Lists.newArrayList();
    protected String confirmButtonText;
    private int ticksUntilEnable;

    public GuiMessage(Screen parentScreen, String line1, String line2)
    {
        super(new TranslationTextComponent("of.options.detailsTitle"));
        this.parentScreen = parentScreen;
        this.messageLine1 = new StringTextComponent(line1);
        this.messageLine2 = new StringTextComponent(line2);
        this.confirmButtonText = I18n.format("gui.done");
    }

    public void init()
    {
        this.addButton(new GuiButtonOF(0, this.width / 2 - 100, this.height / 6 + 96, this.confirmButtonText));
        this.listLines2.clear();
        this.listLines2.addAll(this.mc.fontRenderer.trimStringToWidth(this.messageLine2, this.width - 50));
    }

    protected void actionPerformed(Widget button)
    {
        Config.getMinecraft().displayGuiScreen(this.parentScreen);
    }

    public void render(MatrixStack matrixStackIn, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStackIn);
        drawCenteredString(matrixStackIn, this.fontRenderer, this.messageLine1, this.width / 2, 70, 16777215);
        int i = 90;

        for (IReorderingProcessor ireorderingprocessor : this.listLines2)
        {
            drawCenteredString(matrixStackIn, this.fontRenderer, ireorderingprocessor, this.width / 2, i, 16777215);
            i += 9;
        }

        super.render(matrixStackIn, mouseX, mouseY, partialTicks);
    }

    public void setButtonDelay(int ticksUntilEnable)
    {
        this.ticksUntilEnable = ticksUntilEnable;

        for (Widget button : this.buttonList)
        {
            button.active = false;
        }
    }

    public void tick()
    {
        super.tick();

        if (--this.ticksUntilEnable == 0)
        {
            for (Widget button : this.buttonList)
            {
                button.active = true;
            }
        }
    }
}
