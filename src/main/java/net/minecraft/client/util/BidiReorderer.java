package net.minecraft.client.util;

import com.google.common.collect.Lists;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.Bidi;
import com.ibm.icu.text.BidiRun;
import java.util.List;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextProperties;

public class BidiReorderer
{
    public static IReorderingProcessor func_243508_a(ITextProperties p_243508_0_, boolean p_243508_1_)
    {
        BidiReorder bidireorder = BidiReorder.func_244290_a(p_243508_0_, UCharacter::getMirror, BidiReorderer::func_243507_a);
        Bidi bidi = new Bidi(bidireorder.func_244286_a(), p_243508_1_ ? 127 : 126);
        bidi.setReorderingMode(0);
        List<IReorderingProcessor> list = Lists.newArrayList();
        int i = bidi.countRuns();

        for (int j = 0; j < i; ++j)
        {
            BidiRun bidirun = bidi.getVisualRun(j);
            list.addAll(bidireorder.func_244287_a(bidirun.getStart(), bidirun.getLength(), bidirun.isOddRun()));
        }

        return IReorderingProcessor.func_242241_a(list);
    }

    private static String func_243507_a(String p_243507_0_)
    {
        try
        {
            return (new ArabicShaping(8)).shape(p_243507_0_);
        }
        catch (Exception exception)
        {
            return p_243507_0_;
        }
    }
}
