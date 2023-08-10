package net.minecraft.client.settings;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.optifine.Config;

public class SliderPercentageOption extends AbstractOption
{
    protected final float stepSize;
    protected final double minValue;
    protected double maxValue;
    protected Function<GameSettings, Double> getter;
    protected BiConsumer<GameSettings, Double> setter;
    protected BiFunction<GameSettings, SliderPercentageOption, ITextComponent> getDisplayStringFunc;
    protected double[] stepValues;

    public SliderPercentageOption(String translationKey, double minValueIn, double maxValueIn, float stepSizeIn, Function<GameSettings, Double> getter, BiConsumer<GameSettings, Double> setter, BiFunction<GameSettings, SliderPercentageOption, ITextComponent> getDisplayString)
    {
        super(translationKey);
        this.minValue = minValueIn;
        this.maxValue = maxValueIn;
        this.stepSize = stepSizeIn;
        this.getter = getter;
        this.setter = setter;
        this.getDisplayStringFunc = getDisplayString;
    }

    public SliderPercentageOption(String p_i242103_1_, double p_i242103_2_, double p_i242103_4_, double[] p_i242103_6_, Function<GameSettings, Double> p_i242103_7_, BiConsumer<GameSettings, Double> p_i242103_8_, BiFunction<GameSettings, SliderPercentageOption, ITextComponent> p_i242103_9_)
    {
        super(p_i242103_1_);
        this.minValue = p_i242103_2_;
        this.maxValue = p_i242103_4_;
        this.stepSize = 0.0F;
        this.getter = p_i242103_7_;
        this.setter = p_i242103_8_;
        this.getDisplayStringFunc = p_i242103_9_;
        this.stepValues = p_i242103_6_;

        if (p_i242103_6_ != null)
        {
            p_i242103_6_ = (double[])p_i242103_6_.clone();
            Arrays.sort(p_i242103_6_);
        }
    }

    public Widget createWidget(GameSettings options, int xIn, int yIn, int widthIn)
    {
        return new OptionSlider(options, xIn, yIn, widthIn, 20, this);
    }

    public double normalizeValue(double value)
    {
        return MathHelper.clamp((this.snapToStepClamp(value) - this.minValue) / (this.maxValue - this.minValue), 0.0D, 1.0D);
    }

    public double denormalizeValue(double value)
    {
        return this.snapToStepClamp(MathHelper.lerp(MathHelper.clamp(value, 0.0D, 1.0D), this.minValue, this.maxValue));
    }

    private double snapToStepClamp(double valueIn)
    {
        if (this.stepSize > 0.0F)
        {
            valueIn = (double)(this.stepSize * (float)Math.round(valueIn / (double)this.stepSize));
        }

        if (this.stepValues != null)
        {
            for (int i = 0; i < this.stepValues.length; ++i)
            {
                double d0 = i <= 0 ? -Double.MAX_VALUE : (this.stepValues[i - 1] + this.stepValues[i]) / 2.0D;
                double d1 = i >= this.stepValues.length - 1 ? Double.MAX_VALUE : (this.stepValues[i] + this.stepValues[i + 1]) / 2.0D;

                if (Config.between(valueIn, d0, d1))
                {
                    valueIn = this.stepValues[i];
                    break;
                }
            }
        }

        return MathHelper.clamp(valueIn, this.minValue, this.maxValue);
    }

    public double getMinValue()
    {
        return this.minValue;
    }

    public double getMaxValue()
    {
        return this.maxValue;
    }

    public void setMaxValue(float valueIn)
    {
        this.maxValue = (double)valueIn;
    }

    public void set(GameSettings options, double valueIn)
    {
        this.setter.accept(options, valueIn);
    }

    public double get(GameSettings options)
    {
        return this.getter.apply(options);
    }

    public ITextComponent func_238334_c_(GameSettings p_238334_1_)
    {
        return this.getDisplayStringFunc.apply(p_238334_1_, this);
    }
}
