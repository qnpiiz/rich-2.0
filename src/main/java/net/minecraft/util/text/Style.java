package net.minecraft.util.text;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class Style
{
    public static final Style EMPTY = new Style((Color)null, (Boolean)null, (Boolean)null, (Boolean)null, (Boolean)null, (Boolean)null, (ClickEvent)null, (HoverEvent)null, (String)null, (ResourceLocation)null);
    public static final ResourceLocation DEFAULT_FONT = new ResourceLocation("minecraft", "default");
    @Nullable
    private final Color color;
    @Nullable
    private final Boolean bold;
    @Nullable
    private final Boolean italic;
    @Nullable
    private final Boolean underlined;
    @Nullable
    private final Boolean strikethrough;
    @Nullable
    private final Boolean obfuscated;
    @Nullable
    private final ClickEvent clickEvent;
    @Nullable
    private final HoverEvent hoverEvent;
    @Nullable
    private final String insertion;
    @Nullable
    private final ResourceLocation fontId;

    private Style(@Nullable Color color, @Nullable Boolean bold, @Nullable Boolean italic, @Nullable Boolean underlined, @Nullable Boolean strikethrough, @Nullable Boolean obfuscated, @Nullable ClickEvent clickEvent, @Nullable HoverEvent hoverEvent, @Nullable String insertion, @Nullable ResourceLocation fontId)
    {
        this.color = color;
        this.bold = bold;
        this.italic = italic;
        this.underlined = underlined;
        this.strikethrough = strikethrough;
        this.obfuscated = obfuscated;
        this.clickEvent = clickEvent;
        this.hoverEvent = hoverEvent;
        this.insertion = insertion;
        this.fontId = fontId;
    }

    @Nullable
    public Color getColor()
    {
        return this.color;
    }

    /**
     * Whether or not text of this ChatStyle should be in bold.
     */
    public boolean getBold()
    {
        return this.bold == Boolean.TRUE;
    }

    /**
     * Whether or not text of this ChatStyle should be italicized.
     */
    public boolean getItalic()
    {
        return this.italic == Boolean.TRUE;
    }

    /**
     * Whether or not to format text of this ChatStyle using strikethrough.
     */
    public boolean getStrikethrough()
    {
        return this.strikethrough == Boolean.TRUE;
    }

    /**
     * Whether or not text of this ChatStyle should be underlined.
     */
    public boolean getUnderlined()
    {
        return this.underlined == Boolean.TRUE;
    }

    /**
     * Whether or not text of this ChatStyle should be obfuscated.
     */
    public boolean getObfuscated()
    {
        return this.obfuscated == Boolean.TRUE;
    }

    /**
     * Whether or not this style is empty (inherits everything from the parent).
     */
    public boolean isEmpty()
    {
        return this == EMPTY;
    }

    @Nullable

    /**
     * The effective chat click event.
     */
    public ClickEvent getClickEvent()
    {
        return this.clickEvent;
    }

    @Nullable

    /**
     * The effective chat hover event.
     */
    public HoverEvent getHoverEvent()
    {
        return this.hoverEvent;
    }

    @Nullable

    /**
     * Get the text to be inserted into Chat when the component is shift-clicked
     */
    public String getInsertion()
    {
        return this.insertion;
    }

    public ResourceLocation getFontId()
    {
        return this.fontId != null ? this.fontId : DEFAULT_FONT;
    }

    public Style setColor(@Nullable Color color)
    {
        return new Style(color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.fontId);
    }

    public Style setFormatting(@Nullable TextFormatting formatting)
    {
        return this.setColor(formatting != null ? Color.fromTextFormatting(formatting) : null);
    }

    public Style setBold(@Nullable Boolean bold)
    {
        return new Style(this.color, bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.fontId);
    }

    public Style setItalic(@Nullable Boolean italic)
    {
        return new Style(this.color, this.bold, italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.fontId);
    }

    public Style func_244282_c(@Nullable Boolean p_244282_1_)
    {
        return new Style(this.color, this.bold, this.italic, p_244282_1_, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.fontId);
    }

    public Style setClickEvent(@Nullable ClickEvent clickEvent)
    {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, clickEvent, this.hoverEvent, this.insertion, this.fontId);
    }

    public Style setHoverEvent(@Nullable HoverEvent hoverEvent)
    {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, hoverEvent, this.insertion, this.fontId);
    }

    public Style setInsertion(@Nullable String insertion)
    {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, insertion, this.fontId);
    }

    public Style setFontId(@Nullable ResourceLocation fontId)
    {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, fontId);
    }

    public Style applyFormatting(TextFormatting formatting)
    {
        Color color = this.color;
        Boolean obool = this.bold;
        Boolean obool1 = this.italic;
        Boolean obool2 = this.strikethrough;
        Boolean obool3 = this.underlined;
        Boolean obool4 = this.obfuscated;

        switch (formatting)
        {
            case OBFUSCATED:
                obool4 = true;
                break;

            case BOLD:
                obool = true;
                break;

            case STRIKETHROUGH:
                obool2 = true;
                break;

            case UNDERLINE:
                obool3 = true;
                break;

            case ITALIC:
                obool1 = true;
                break;

            case RESET:
                return EMPTY;

            default:
                color = Color.fromTextFormatting(formatting);
        }

        return new Style(color, obool, obool1, obool3, obool2, obool4, this.clickEvent, this.hoverEvent, this.insertion, this.fontId);
    }

    public Style forceFormatting(TextFormatting formatting)
    {
        Color color = this.color;
        Boolean obool = this.bold;
        Boolean obool1 = this.italic;
        Boolean obool2 = this.strikethrough;
        Boolean obool3 = this.underlined;
        Boolean obool4 = this.obfuscated;

        switch (formatting)
        {
            case OBFUSCATED:
                obool4 = true;
                break;

            case BOLD:
                obool = true;
                break;

            case STRIKETHROUGH:
                obool2 = true;
                break;

            case UNDERLINE:
                obool3 = true;
                break;

            case ITALIC:
                obool1 = true;
                break;

            case RESET:
                return EMPTY;

            default:
                obool4 = false;
                obool = false;
                obool2 = false;
                obool3 = false;
                obool1 = false;
                color = Color.fromTextFormatting(formatting);
        }

        return new Style(color, obool, obool1, obool3, obool2, obool4, this.clickEvent, this.hoverEvent, this.insertion, this.fontId);
    }

    public Style createStyleFromFormattings(TextFormatting... formatings)
    {
        Color color = this.color;
        Boolean obool = this.bold;
        Boolean obool1 = this.italic;
        Boolean obool2 = this.strikethrough;
        Boolean obool3 = this.underlined;
        Boolean obool4 = this.obfuscated;

        for (TextFormatting textformatting : formatings)
        {
            switch (textformatting)
            {
                case OBFUSCATED:
                    obool4 = true;
                    break;

                case BOLD:
                    obool = true;
                    break;

                case STRIKETHROUGH:
                    obool2 = true;
                    break;

                case UNDERLINE:
                    obool3 = true;
                    break;

                case ITALIC:
                    obool1 = true;
                    break;

                case RESET:
                    return EMPTY;

                default:
                    color = Color.fromTextFormatting(textformatting);
            }
        }

        return new Style(color, obool, obool1, obool3, obool2, obool4, this.clickEvent, this.hoverEvent, this.insertion, this.fontId);
    }

    /**
     * Merges the style with another one. If either styles are empty the other will be returned. If a value already
     * exists on the current style it will not be overriden.
     */
    public Style mergeStyle(Style style)
    {
        if (this == EMPTY)
        {
            return style;
        }
        else
        {
            return style == EMPTY ? this : new Style(this.color != null ? this.color : style.color, this.bold != null ? this.bold : style.bold, this.italic != null ? this.italic : style.italic, this.underlined != null ? this.underlined : style.underlined, this.strikethrough != null ? this.strikethrough : style.strikethrough, this.obfuscated != null ? this.obfuscated : style.obfuscated, this.clickEvent != null ? this.clickEvent : style.clickEvent, this.hoverEvent != null ? this.hoverEvent : style.hoverEvent, this.insertion != null ? this.insertion : style.insertion, this.fontId != null ? this.fontId : style.fontId);
        }
    }

    public String toString()
    {
        return "Style{ color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", strikethrough=" + this.strikethrough + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.getClickEvent() + ", hoverEvent=" + this.getHoverEvent() + ", insertion=" + this.getInsertion() + ", font=" + this.getFontId() + '}';
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof Style))
        {
            return false;
        }
        else
        {
            Style style = (Style)p_equals_1_;
            return this.getBold() == style.getBold() && Objects.equals(this.getColor(), style.getColor()) && this.getItalic() == style.getItalic() && this.getObfuscated() == style.getObfuscated() && this.getStrikethrough() == style.getStrikethrough() && this.getUnderlined() == style.getUnderlined() && Objects.equals(this.getClickEvent(), style.getClickEvent()) && Objects.equals(this.getHoverEvent(), style.getHoverEvent()) && Objects.equals(this.getInsertion(), style.getInsertion()) && Objects.equals(this.getFontId(), style.getFontId());
        }
    }

    public int hashCode()
    {
        return Objects.hash(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion);
    }

    public static class Serializer implements JsonDeserializer<Style>, JsonSerializer<Style>
    {
        @Nullable
        public Style deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            if (p_deserialize_1_.isJsonObject())
            {
                JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();

                if (jsonobject == null)
                {
                    return null;
                }
                else
                {
                    Boolean obool = deserializeBooleanValue(jsonobject, "bold");
                    Boolean obool1 = deserializeBooleanValue(jsonobject, "italic");
                    Boolean obool2 = deserializeBooleanValue(jsonobject, "underlined");
                    Boolean obool3 = deserializeBooleanValue(jsonobject, "strikethrough");
                    Boolean obool4 = deserializeBooleanValue(jsonobject, "obfuscated");
                    Color color = deserializeColor(jsonobject);
                    String s = deserializeInsertion(jsonobject);
                    ClickEvent clickevent = deserializeClickEvent(jsonobject);
                    HoverEvent hoverevent = deserializeHoverEvent(jsonobject);
                    ResourceLocation resourcelocation = deserializeFont(jsonobject);
                    return new Style(color, obool, obool1, obool2, obool3, obool4, clickevent, hoverevent, s, resourcelocation);
                }
            }
            else
            {
                return null;
            }
        }

        @Nullable
        private static ResourceLocation deserializeFont(JsonObject json)
        {
            if (json.has("font"))
            {
                String s = JSONUtils.getString(json, "font");

                try
                {
                    return new ResourceLocation(s);
                }
                catch (ResourceLocationException resourcelocationexception)
                {
                    throw new JsonSyntaxException("Invalid font name: " + s);
                }
            }
            else
            {
                return null;
            }
        }

        @Nullable
        private static HoverEvent deserializeHoverEvent(JsonObject json)
        {
            if (json.has("hoverEvent"))
            {
                JsonObject jsonobject = JSONUtils.getJsonObject(json, "hoverEvent");
                HoverEvent hoverevent = HoverEvent.deserialize(jsonobject);

                if (hoverevent != null && hoverevent.getAction().shouldAllowInChat())
                {
                    return hoverevent;
                }
            }

            return null;
        }

        @Nullable
        private static ClickEvent deserializeClickEvent(JsonObject json)
        {
            if (json.has("clickEvent"))
            {
                JsonObject jsonobject = JSONUtils.getJsonObject(json, "clickEvent");
                String s = JSONUtils.getString(jsonobject, "action", (String)null);
                ClickEvent.Action clickevent$action = s == null ? null : ClickEvent.Action.getValueByCanonicalName(s);
                String s1 = JSONUtils.getString(jsonobject, "value", (String)null);

                if (clickevent$action != null && s1 != null && clickevent$action.shouldAllowInChat())
                {
                    return new ClickEvent(clickevent$action, s1);
                }
            }

            return null;
        }

        @Nullable
        private static String deserializeInsertion(JsonObject json)
        {
            return JSONUtils.getString(json, "insertion", (String)null);
        }

        @Nullable
        private static Color deserializeColor(JsonObject json)
        {
            if (json.has("color"))
            {
                String s = JSONUtils.getString(json, "color");
                return Color.fromHex(s);
            }
            else
            {
                return null;
            }
        }

        @Nullable
        private static Boolean deserializeBooleanValue(JsonObject json, String memberName)
        {
            return json.has(memberName) ? json.get(memberName).getAsBoolean() : null;
        }

        @Nullable
        public JsonElement serialize(Style p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            if (p_serialize_1_.isEmpty())
            {
                return null;
            }
            else
            {
                JsonObject jsonobject = new JsonObject();

                if (p_serialize_1_.bold != null)
                {
                    jsonobject.addProperty("bold", p_serialize_1_.bold);
                }

                if (p_serialize_1_.italic != null)
                {
                    jsonobject.addProperty("italic", p_serialize_1_.italic);
                }

                if (p_serialize_1_.underlined != null)
                {
                    jsonobject.addProperty("underlined", p_serialize_1_.underlined);
                }

                if (p_serialize_1_.strikethrough != null)
                {
                    jsonobject.addProperty("strikethrough", p_serialize_1_.strikethrough);
                }

                if (p_serialize_1_.obfuscated != null)
                {
                    jsonobject.addProperty("obfuscated", p_serialize_1_.obfuscated);
                }

                if (p_serialize_1_.color != null)
                {
                    jsonobject.addProperty("color", p_serialize_1_.color.getName());
                }

                if (p_serialize_1_.insertion != null)
                {
                    jsonobject.add("insertion", p_serialize_3_.serialize(p_serialize_1_.insertion));
                }

                if (p_serialize_1_.clickEvent != null)
                {
                    JsonObject jsonobject1 = new JsonObject();
                    jsonobject1.addProperty("action", p_serialize_1_.clickEvent.getAction().getCanonicalName());
                    jsonobject1.addProperty("value", p_serialize_1_.clickEvent.getValue());
                    jsonobject.add("clickEvent", jsonobject1);
                }

                if (p_serialize_1_.hoverEvent != null)
                {
                    jsonobject.add("hoverEvent", p_serialize_1_.hoverEvent.serialize());
                }

                if (p_serialize_1_.fontId != null)
                {
                    jsonobject.addProperty("font", p_serialize_1_.fontId.toString());
                }

                return jsonobject;
            }
        }
    }
}
