package net.minecraft.client.gui.widget.button;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.optifine.gui.IOptionControl;

public class OptionButton extends Button implements IBidiTooltip, IOptionControl
{
    private final AbstractOption enumOptions;

    public OptionButton(int x, int y, int width, int height, AbstractOption enumOptions, ITextComponent title, Button.IPressable p_i232262_7_)
    {
        super(x, y, width, height, title, p_i232262_7_);
        this.enumOptions = enumOptions;
    }

    public AbstractOption func_238517_a_()
    {
        return this.enumOptions;
    }

    public Optional<List<IReorderingProcessor>> func_241867_d()
    {
        return this.enumOptions.getOptionValues();
    }

    public AbstractOption getControlOption()
    {
        return this.enumOptions;
    }
}
