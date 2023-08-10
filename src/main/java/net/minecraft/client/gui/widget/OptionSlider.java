package net.minecraft.client.gui.widget;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.optifine.config.FloatOptions;
import net.optifine.gui.IOptionControl;

public class OptionSlider extends GameSettingsSlider implements IBidiTooltip, IOptionControl
{
    private final SliderPercentageOption option;
    private boolean supportAdjusting;
    private boolean adjusting;

    public OptionSlider(GameSettings settings, int xIn, int yIn, int widthIn, int heightIn, SliderPercentageOption optionIn)
    {
        super(settings, xIn, yIn, widthIn, heightIn, (double)((float)optionIn.normalizeValue(optionIn.get(settings))));
        this.option = optionIn;
        this.func_230979_b_();
        this.supportAdjusting = FloatOptions.supportAdjusting(this.option);
        this.adjusting = false;
    }

    protected void func_230972_a_()
    {
        if (!this.adjusting)
        {
            double d0 = this.option.get(this.settings);
            double d1 = this.option.denormalizeValue(this.sliderValue);

            if (d1 != d0)
            {
                this.option.set(this.settings, this.option.denormalizeValue(this.sliderValue));
                this.settings.saveOptions();
            }
        }
    }

    protected void func_230979_b_()
    {
        if (this.adjusting)
        {
            double d0 = this.option.denormalizeValue(this.sliderValue);
            ITextComponent itextcomponent = FloatOptions.getTextComponent(this.option, d0);

            if (itextcomponent != null)
            {
                this.setMessage(itextcomponent);
            }
        }
        else
        {
            this.setMessage(this.option.func_238334_c_(this.settings));
        }
    }

    public Optional<List<IReorderingProcessor>> func_241867_d()
    {
        return this.option.getOptionValues();
    }

    public void onClick(double mouseX, double mouseY)
    {
        if (this.supportAdjusting)
        {
            this.adjusting = true;
        }

        super.onClick(mouseX, mouseY);
    }

    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY)
    {
        if (this.supportAdjusting)
        {
            this.adjusting = true;
        }

        super.onDrag(mouseX, mouseY, dragX, dragY);
    }

    public void onRelease(double mouseX, double mouseY)
    {
        if (this.adjusting)
        {
            this.adjusting = false;
            this.func_230972_a_();
            this.func_230979_b_();
        }

        super.onRelease(mouseX, mouseY);
    }

    public static int getWidth(Widget p_getWidth_0_)
    {
        return p_getWidth_0_.width;
    }

    public static int getHeight(Widget p_getHeight_0_)
    {
        return p_getHeight_0_.height;
    }

    public AbstractOption getControlOption()
    {
        return this.option;
    }
}
