package net.minecraft.util.text;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class KeybindTextComponent extends TextComponent
{
    private static Function<String, Supplier<ITextComponent>> displaySupplierFunction = (p_193635_0_) ->
    {
        return () -> {
            return new StringTextComponent(p_193635_0_);
        };
    };
    private final String keybind;
    private Supplier<ITextComponent> displaySupplier;

    public KeybindTextComponent(String keybind)
    {
        this.keybind = keybind;
    }

    public static void func_240696_a_(Function<String, Supplier<ITextComponent>> p_240696_0_)
    {
        displaySupplierFunction = p_240696_0_;
    }

    private ITextComponent func_240698_i_()
    {
        if (this.displaySupplier == null)
        {
            this.displaySupplier = displaySupplierFunction.apply(this.keybind);
        }

        return this.displaySupplier.get();
    }

    public <T> Optional<T> func_230533_b_(ITextProperties.ITextAcceptor<T> acceptor)
    {
        return this.func_240698_i_().getComponent(acceptor);
    }

    public <T> Optional<T> func_230534_b_(ITextProperties.IStyledTextAcceptor<T> acceptor, Style style)
    {
        return this.func_240698_i_().getComponentWithStyle(acceptor, style);
    }

    public KeybindTextComponent copyRaw()
    {
        return new KeybindTextComponent(this.keybind);
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof KeybindTextComponent))
        {
            return false;
        }
        else
        {
            KeybindTextComponent keybindtextcomponent = (KeybindTextComponent)p_equals_1_;
            return this.keybind.equals(keybindtextcomponent.keybind) && super.equals(p_equals_1_);
        }
    }

    public String toString()
    {
        return "KeybindComponent{keybind='" + this.keybind + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }

    public String getKeybind()
    {
        return this.keybind;
    }
}
