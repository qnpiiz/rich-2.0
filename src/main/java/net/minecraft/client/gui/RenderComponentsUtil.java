package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextPropertiesManager;

public class RenderComponentsUtil
{
    private static final IReorderingProcessor field_238502_a_ = IReorderingProcessor.fromCodePoint(32, Style.EMPTY);

    private static String func_238504_a_(String p_238504_0_)
    {
        return Minecraft.getInstance().gameSettings.chatColor ? p_238504_0_ : TextFormatting.getTextWithoutFormattingCodes(p_238504_0_);
    }

    public static List<IReorderingProcessor> func_238505_a_(ITextProperties p_238505_0_, int p_238505_1_, FontRenderer p_238505_2_)
    {
        TextPropertiesManager textpropertiesmanager = new TextPropertiesManager();
        p_238505_0_.getComponentWithStyle((p_238503_1_, p_238503_2_) ->
        {
            textpropertiesmanager.func_238155_a_(ITextProperties.func_240653_a_(func_238504_a_(p_238503_2_), p_238503_1_));
            return Optional.empty();
        }, Style.EMPTY);
        List<IReorderingProcessor> list = Lists.newArrayList();
        p_238505_2_.getCharacterManager().func_243242_a(textpropertiesmanager.func_238156_b_(), p_238505_1_, Style.EMPTY, (p_243256_1_, p_243256_2_) ->
        {
            IReorderingProcessor ireorderingprocessor = LanguageMap.getInstance().func_241870_a(p_243256_1_);
            list.add(p_243256_2_ ? IReorderingProcessor.func_242234_a(field_238502_a_, ireorderingprocessor) : ireorderingprocessor);
        });
        return (List<IReorderingProcessor>)(list.isEmpty() ? Lists.newArrayList(IReorderingProcessor.field_242232_a) : list);
    }
}
