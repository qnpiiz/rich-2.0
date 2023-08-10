package net.minecraft.util.text;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.util.IReorderingProcessor;

public abstract class TextComponent implements IFormattableTextComponent
{
    protected final List<ITextComponent> siblings = Lists.newArrayList();
    private IReorderingProcessor field_244278_d = IReorderingProcessor.field_242232_a;
    @Nullable
    private LanguageMap field_244279_e;
    private Style style = Style.EMPTY;

    public IFormattableTextComponent append(ITextComponent sibling)
    {
        this.siblings.add(sibling);
        return this;
    }

    /**
     * Gets the raw content of this component (but not its sibling components), without any formatting codes. For
     * example, this is the raw text in a {@link TextComponentString}, but it's the translated text for a {@link
     * TextComponentTranslation} and it's the score value for a {@link TextComponentScore}.
     */
    public String getUnformattedComponentText()
    {
        return "";
    }

    public List<ITextComponent> getSiblings()
    {
        return this.siblings;
    }

    public IFormattableTextComponent setStyle(Style style)
    {
        this.style = style;
        return this;
    }

    /**
     * Gets the style of this component. Returns a direct reference; changes to this style will modify the style of this
     * component (IE, there is no need to call {@link #setStyle(Style)} again after modifying it).
     *  
     * If this component's style is currently <code>null</code>, it will be initialized to the default style, and the
     * parent style of all sibling components will be set to that style. (IE, changes to this style will also be
     * reflected in sibling components.)
     *  
     * This method never returns <code>null</code>.
     */
    public Style getStyle()
    {
        return this.style;
    }

    public abstract TextComponent copyRaw();

    public final IFormattableTextComponent deepCopy()
    {
        TextComponent textcomponent = this.copyRaw();
        textcomponent.siblings.addAll(this.siblings);
        textcomponent.setStyle(this.style);
        return textcomponent;
    }

    public IReorderingProcessor func_241878_f()
    {
        LanguageMap languagemap = LanguageMap.getInstance();

        if (this.field_244279_e != languagemap)
        {
            this.field_244278_d = languagemap.func_241870_a(this);
            this.field_244279_e = languagemap;
        }

        return this.field_244278_d;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof TextComponent))
        {
            return false;
        }
        else
        {
            TextComponent textcomponent = (TextComponent)p_equals_1_;
            return this.siblings.equals(textcomponent.siblings) && Objects.equals(this.getStyle(), textcomponent.getStyle());
        }
    }

    public int hashCode()
    {
        return Objects.hash(this.getStyle(), this.siblings);
    }

    public String toString()
    {
        return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
    }
}
