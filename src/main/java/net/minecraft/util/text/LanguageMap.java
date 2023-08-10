package net.minecraft.util.text;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.JSONUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LanguageMap
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson field_240591_b_ = new Gson();

    /**
     * Pattern that matches numeric variable placeholders in a resource string, such as "%d", "%3$d", "%.2f"
     */
    private static final Pattern NUMERIC_VARIABLE_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
    private static volatile LanguageMap field_240592_d_ = func_240595_c_();

    private static LanguageMap func_240595_c_()
    {
        Builder<String, String> builder = ImmutableMap.builder();
        BiConsumer<String, String> biconsumer = builder::put;

        try (InputStream inputstream = LanguageMap.class.getResourceAsStream("/assets/minecraft/lang/en_us.json"))
        {
            func_240593_a_(inputstream, biconsumer);
        }
        catch (JsonParseException | IOException ioexception)
        {
            LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", (Throwable)ioexception);
        }

        final Map<String, String> map = builder.build();
        return new LanguageMap()
        {
            public String func_230503_a_(String p_230503_1_)
            {
                return map.getOrDefault(p_230503_1_, p_230503_1_);
            }
            public boolean func_230506_b_(String p_230506_1_)
            {
                return map.containsKey(p_230506_1_);
            }
            public boolean func_230505_b_()
            {
                return false;
            }
            public IReorderingProcessor func_241870_a(ITextProperties p_241870_1_)
            {
                return (p_244262_1_) ->
                {
                    return p_241870_1_.getComponentWithStyle((p_244261_1_, p_244261_2_) -> {
                        return TextProcessing.func_238346_c_(p_244261_2_, p_244261_1_, p_244262_1_) ? Optional.empty() : ITextProperties.field_240650_b_;
                    }, Style.EMPTY).isPresent();
                };
            }
        };
    }

    public static void func_240593_a_(InputStream p_240593_0_, BiConsumer<String, String> p_240593_1_)
    {
        JsonObject jsonobject = field_240591_b_.fromJson(new InputStreamReader(p_240593_0_, StandardCharsets.UTF_8), JsonObject.class);

        for (Entry<String, JsonElement> entry : jsonobject.entrySet())
        {
            String s = NUMERIC_VARIABLE_PATTERN.matcher(JSONUtils.getString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
            p_240593_1_.accept(entry.getKey(), s);
        }
    }

    /**
     * Return the StringTranslate singleton instance
     */
    public static LanguageMap getInstance()
    {
        return field_240592_d_;
    }

    public static void func_240594_a_(LanguageMap p_240594_0_)
    {
        field_240592_d_ = p_240594_0_;
    }

    public abstract String func_230503_a_(String p_230503_1_);

    public abstract boolean func_230506_b_(String p_230506_1_);

    public abstract boolean func_230505_b_();

    public abstract IReorderingProcessor func_241870_a(ITextProperties p_241870_1_);

    public List<IReorderingProcessor> func_244260_a(List<ITextProperties> p_244260_1_)
    {
        return p_244260_1_.stream().map(getInstance()::func_241870_a).collect(ImmutableList.toImmutableList());
    }
}
