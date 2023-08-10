package net.minecraft.client.settings;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.GameSettings;
import net.minecraft.util.text.ITextComponent;

public class SliderMultiplierOption extends SliderPercentageOption
{
    public SliderMultiplierOption(String translationKey, double minValueIn, double maxValueIn, float stepSizeIn, Function<GameSettings, Double> getterIn, BiConsumer<GameSettings, Double> setterIn, BiFunction<GameSettings, SliderPercentageOption, ITextComponent> getterDisplayString)
    {
        super(translationKey, minValueIn, maxValueIn, stepSizeIn, getterIn, setterIn, getterDisplayString);
    }

    public double normalizeValue(double value)
    {
        return Math.log(value / this.minValue) / Math.log(this.maxValue / this.minValue);
    }

    public double denormalizeValue(double value)
    {
        return this.minValue * Math.pow(Math.E, Math.log(this.maxValue / this.minValue) * value);
    }
}
