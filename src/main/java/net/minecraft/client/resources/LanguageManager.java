package net.minecraft.client.resources;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Stream;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.resources.IResourcePack;
import net.minecraft.util.text.LanguageMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageManager implements IResourceManagerReloadListener
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Language field_239503_b_ = new Language("en_us", "US", "English", false);
    private Map<String, Language> languageMap = ImmutableMap.of("en_us", field_239503_b_);
    private String currentLanguage;
    private Language field_239504_e_ = field_239503_b_;

    public LanguageManager(String p_i48112_1_)
    {
        this.currentLanguage = p_i48112_1_;
    }

    private static Map<String, Language> func_239506_a_(Stream<IResourcePack> p_239506_0_)
    {
        Map<String, Language> map = Maps.newHashMap();
        p_239506_0_.forEach((p_239505_1_) ->
        {
            try {
                LanguageMetadataSection languagemetadatasection = p_239505_1_.getMetadata(LanguageMetadataSection.field_195818_a);

                if (languagemetadatasection != null)
                {
                    for (Language language : languagemetadatasection.getLanguages())
                    {
                        map.putIfAbsent(language.getCode(), language);
                    }
                }
            }
            catch (IOException | RuntimeException runtimeexception)
            {
                LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", p_239505_1_.getName(), runtimeexception);
            }
        });
        return ImmutableMap.copyOf(map);
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        this.languageMap = func_239506_a_(resourceManager.getResourcePackStream());
        Language language = this.languageMap.getOrDefault("en_us", field_239503_b_);
        this.field_239504_e_ = this.languageMap.getOrDefault(this.currentLanguage, language);
        List<Language> list = Lists.newArrayList(language);

        if (this.field_239504_e_ != language)
        {
            list.add(this.field_239504_e_);
        }

        ClientLanguageMap clientlanguagemap = ClientLanguageMap.func_239497_a_(resourceManager, list);
        I18n.func_239502_a_(clientlanguagemap);
        LanguageMap.func_240594_a_(clientlanguagemap);
    }

    public void setCurrentLanguage(Language currentLanguageIn)
    {
        this.currentLanguage = currentLanguageIn.getCode();
        this.field_239504_e_ = currentLanguageIn;
    }

    public Language getCurrentLanguage()
    {
        return this.field_239504_e_;
    }

    public SortedSet<Language> getLanguages()
    {
        return Sets.newTreeSet(this.languageMap.values());
    }

    public Language getLanguage(String p_191960_1_)
    {
        return this.languageMap.get(p_191960_1_);
    }
}
