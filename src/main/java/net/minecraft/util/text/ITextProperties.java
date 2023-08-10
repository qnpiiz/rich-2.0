package net.minecraft.util.text;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.Unit;

public interface ITextProperties
{
    Optional<Unit> field_240650_b_ = Optional.of(Unit.INSTANCE);
    ITextProperties field_240651_c_ = new ITextProperties()
    {
        public <T> Optional<T> getComponent(ITextProperties.ITextAcceptor<T> acceptor)
        {
            return Optional.empty();
        }
        public <T> Optional<T> getComponentWithStyle(ITextProperties.IStyledTextAcceptor<T> acceptor, Style styleIn)
        {
            return Optional.empty();
        }
    };

    <T> Optional<T> getComponent(ITextProperties.ITextAcceptor<T> acceptor);

    <T> Optional<T> getComponentWithStyle(ITextProperties.IStyledTextAcceptor<T> acceptor, Style styleIn);

    static ITextProperties func_240652_a_(final String p_240652_0_)
    {
        return new ITextProperties()
        {
            public <T> Optional<T> getComponent(ITextProperties.ITextAcceptor<T> acceptor)
            {
                return acceptor.accept(p_240652_0_);
            }
            public <T> Optional<T> getComponentWithStyle(ITextProperties.IStyledTextAcceptor<T> acceptor, Style styleIn)
            {
                return acceptor.accept(styleIn, p_240652_0_);
            }
        };
    }

    static ITextProperties func_240653_a_(final String p_240653_0_, final Style p_240653_1_)
    {
        return new ITextProperties()
        {
            public <T> Optional<T> getComponent(ITextProperties.ITextAcceptor<T> acceptor)
            {
                return acceptor.accept(p_240653_0_);
            }
            public <T> Optional<T> getComponentWithStyle(ITextProperties.IStyledTextAcceptor<T> acceptor, Style styleIn)
            {
                return acceptor.accept(p_240653_1_.mergeStyle(styleIn), p_240653_0_);
            }
        };
    }

    static ITextProperties func_240655_a_(ITextProperties... p_240655_0_)
    {
        return func_240654_a_(ImmutableList.copyOf(p_240655_0_));
    }

    static ITextProperties func_240654_a_(final List<ITextProperties> p_240654_0_)
    {
        return new ITextProperties()
        {
            public <T> Optional<T> getComponent(ITextProperties.ITextAcceptor<T> acceptor)
            {
                for (ITextProperties itextproperties : p_240654_0_)
                {
                    Optional<T> optional = itextproperties.getComponent(acceptor);

                    if (optional.isPresent())
                    {
                        return optional;
                    }
                }

                return Optional.empty();
            }
            public <T> Optional<T> getComponentWithStyle(ITextProperties.IStyledTextAcceptor<T> acceptor, Style styleIn)
            {
                for (ITextProperties itextproperties : p_240654_0_)
                {
                    Optional<T> optional = itextproperties.getComponentWithStyle(acceptor, styleIn);

                    if (optional.isPresent())
                    {
                        return optional;
                    }
                }

                return Optional.empty();
            }
        };
    }

default String getString()
    {
        StringBuilder stringbuilder = new StringBuilder();
        this.getComponent((p_241754_1_) ->
        {
            stringbuilder.append(p_241754_1_);
            return Optional.empty();
        });
        return stringbuilder.toString();
    }

    public interface IStyledTextAcceptor<T>
    {
        Optional<T> accept(Style p_accept_1_, String p_accept_2_);
    }

    public interface ITextAcceptor<T>
    {
        Optional<T> accept(String p_accept_1_);
    }
}
