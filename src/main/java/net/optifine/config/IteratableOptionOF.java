package net.optifine.config;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.client.GameSettings;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.util.text.ITextComponent;

public class IteratableOptionOF extends IteratableOption
{
    public IteratableOptionOF(String nameIn)
    {
        super(nameIn, (BiConsumer<GameSettings, Integer>)null, (BiFunction<GameSettings, IteratableOption, ITextComponent>)null);
        super.setter = this::nextOptionValue;
        super.getter = this::getOptionText;
    }

    public void nextOptionValue(GameSettings gameSettings, int increment)
    {
        gameSettings.setOptionValueOF(this, increment);
    }

    public ITextComponent getOptionText(GameSettings gameSettings, IteratableOption option)
    {
        return gameSettings.getKeyComponentOF(option);
    }
}
