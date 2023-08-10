package net.minecraft.client.settings;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.util.text.ITextComponent;

public class IteratableOption extends AbstractOption
{
    protected BiConsumer<GameSettings, Integer> setter;
    protected BiFunction<GameSettings, IteratableOption, ITextComponent> getter;

    public IteratableOption(String translationKeyIn, BiConsumer<GameSettings, Integer> setterIn, BiFunction<GameSettings, IteratableOption, ITextComponent> getterIn)
    {
        super(translationKeyIn);
        this.setter = setterIn;
        this.getter = getterIn;
    }

    public void setValueIndex(GameSettings options, int valueIn)
    {
        this.setter.accept(options, valueIn);
        options.saveOptions();
    }

    public Widget createWidget(GameSettings options, int xIn, int yIn, int widthIn)
    {
        return new OptionButton(xIn, yIn, widthIn, 20, this, this.getName(options), (p_lambda$createWidget$0_2_) ->
        {
            this.setValueIndex(options, 1);
            p_lambda$createWidget$0_2_.setMessage(this.getName(options));
        });
    }

    public ITextComponent getName(GameSettings settings)
    {
        return this.getter.apply(settings, this);
    }
}
