package net.minecraft.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;

public class RepeatedNarrator
{
    private final float field_230729_a_;
    private final AtomicReference<RepeatedNarrator.Parameter> field_230730_b_ = new AtomicReference<>();

    public RepeatedNarrator(Duration p_i49961_1_)
    {
        this.field_230729_a_ = 1000.0F / (float)p_i49961_1_.toMillis();
    }

    public void func_231415_a_(String p_231415_1_)
    {
        RepeatedNarrator.Parameter repeatednarrator$parameter = this.field_230730_b_.updateAndGet((p_229956_2_) ->
        {
            return p_229956_2_ != null && p_231415_1_.equals(p_229956_2_.field_214462_a) ? p_229956_2_ : new RepeatedNarrator.Parameter(p_231415_1_, RateLimiter.create((double)this.field_230729_a_));
        });

        if (repeatednarrator$parameter.field_214463_b.tryAcquire(1))
        {
            NarratorChatListener.INSTANCE.say(ChatType.SYSTEM, new StringTextComponent(p_231415_1_), Util.DUMMY_UUID);
        }
    }

    static class Parameter
    {
        private final String field_214462_a;
        private final RateLimiter field_214463_b;

        Parameter(String p_i50913_1_, RateLimiter p_i50913_2_)
        {
            this.field_214462_a = p_i50913_1_;
            this.field_214463_b = p_i50913_2_;
        }
    }
}
