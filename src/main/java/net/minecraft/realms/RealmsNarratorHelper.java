package net.minecraft.realms;

import java.time.Duration;
import java.util.Arrays;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;

public class RealmsNarratorHelper
{
    private static final RepeatedNarrator field_239548_a_ = new RepeatedNarrator(Duration.ofSeconds(5L));

    public static void func_239550_a_(String p_239550_0_)
    {
        NarratorChatListener narratorchatlistener = NarratorChatListener.INSTANCE;
        narratorchatlistener.clear();
        narratorchatlistener.say(ChatType.SYSTEM, new StringTextComponent(func_239554_c_(p_239550_0_)), Util.DUMMY_UUID);
    }

    private static String func_239554_c_(String p_239554_0_)
    {
        return p_239554_0_.replace("\\n", System.lineSeparator());
    }

    public static void func_239551_a_(String... p_239551_0_)
    {
        func_239549_a_(Arrays.asList(p_239551_0_));
    }

    public static void func_239549_a_(Iterable<String> p_239549_0_)
    {
        func_239550_a_(func_239552_b_(p_239549_0_));
    }

    public static String func_239552_b_(Iterable<String> p_239552_0_)
    {
        return String.join(System.lineSeparator(), p_239552_0_);
    }

    public static void func_239553_b_(String p_239553_0_)
    {
        field_239548_a_.func_231415_a_(func_239554_c_(p_239553_0_));
    }
}
