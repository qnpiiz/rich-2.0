package net.minecraft.client.settings;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.util.text.ITextComponent;

public class BooleanOption extends AbstractOption
{
    private final Predicate<GameSettings> getter;
    private final BiConsumer<GameSettings, Boolean> setter;
    @Nullable
    private final ITextComponent field_244785_aa;

    public BooleanOption(String translationKeyIn, Predicate<GameSettings> getter, BiConsumer<GameSettings, Boolean> setter)
    {
        this(translationKeyIn, (ITextComponent)null, getter, setter);
    }

    public BooleanOption(String p_i242130_1_, @Nullable ITextComponent p_i242130_2_, Predicate<GameSettings> p_i242130_3_, BiConsumer<GameSettings, Boolean> p_i242130_4_)
    {
        super(p_i242130_1_);
        this.getter = p_i242130_3_;
        this.setter = p_i242130_4_;
        this.field_244785_aa = p_i242130_2_;
    }

    public void set(GameSettings options, String valueIn)
    {
        this.set(options, "true".equals(valueIn));
    }

    public void nextValue(GameSettings options)
    {
        this.set(options, !this.get(options));
        options.saveOptions();
    }

    private void set(GameSettings options, boolean valueIn)
    {
        this.setter.accept(options, valueIn);
    }

    public boolean get(GameSettings options)
    {
        return this.getter.test(options);
    }

    public Widget createWidget(GameSettings options, int xIn, int yIn, int widthIn)
    {
        if (this.field_244785_aa != null)
        {
            this.setOptionValues(Minecraft.getInstance().fontRenderer.trimStringToWidth(this.field_244785_aa, 200));
        }

        return new OptionButton(xIn, yIn, widthIn, 20, this, this.func_238152_c_(options), (p_216745_2_) ->
        {
            this.nextValue(options);
            p_216745_2_.setMessage(this.func_238152_c_(options));
        });
    }

    public ITextComponent func_238152_c_(GameSettings p_238152_1_)
    {
        return DialogTexts.getComposedOptionMessage(this.getBaseMessageTranslation(), this.get(p_238152_1_));
    }
}
