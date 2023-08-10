package net.minecraft.util.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectorTextComponent extends TextComponent implements ITargetedTextComponent
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * The selector used to find the matching entities of this text component
     */
    private final String selector;
    @Nullable
    private final EntitySelector field_197670_d;

    public SelectorTextComponent(String selectorIn)
    {
        this.selector = selectorIn;
        EntitySelector entityselector = null;

        try
        {
            EntitySelectorParser entityselectorparser = new EntitySelectorParser(new StringReader(selectorIn));
            entityselector = entityselectorparser.parse();
        }
        catch (CommandSyntaxException commandsyntaxexception)
        {
            LOGGER.warn("Invalid selector component: {}", selectorIn, commandsyntaxexception.getMessage());
        }

        this.field_197670_d = entityselector;
    }

    /**
     * Gets the selector of this component, in plain text.
     */
    public String getSelector()
    {
        return this.selector;
    }

    public IFormattableTextComponent func_230535_a_(@Nullable CommandSource p_230535_1_, @Nullable Entity p_230535_2_, int p_230535_3_) throws CommandSyntaxException
    {
        return (IFormattableTextComponent)(p_230535_1_ != null && this.field_197670_d != null ? EntitySelector.joinNames(this.field_197670_d.select(p_230535_1_)) : new StringTextComponent(""));
    }

    /**
     * Gets the raw content of this component (but not its sibling components), without any formatting codes. For
     * example, this is the raw text in a {@link TextComponentString}, but it's the translated text for a {@link
     * TextComponentTranslation} and it's the score value for a {@link TextComponentScore}.
     */
    public String getUnformattedComponentText()
    {
        return this.selector;
    }

    public SelectorTextComponent copyRaw()
    {
        return new SelectorTextComponent(this.selector);
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof SelectorTextComponent))
        {
            return false;
        }
        else
        {
            SelectorTextComponent selectortextcomponent = (SelectorTextComponent)p_equals_1_;
            return this.selector.equals(selectortextcomponent.selector) && super.equals(p_equals_1_);
        }
    }

    public String toString()
    {
        return "SelectorComponent{pattern='" + this.selector + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }
}
