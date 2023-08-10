package net.minecraft.profiler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LongTickDetector
{
    private static final Logger field_233516_a_ = LogManager.getLogger();
    private final LongSupplier field_233517_b_ = null;
    private final long field_233518_c_ = 0L;
    private int field_233519_d_;
    private final File field_233520_e_ = null;
    private IResultableProfiler field_233521_f_;

    public IProfiler func_233522_a_()
    {
        this.field_233521_f_ = new Profiler(this.field_233517_b_, () ->
        {
            return this.field_233519_d_;
        }, false);
        ++this.field_233519_d_;
        return this.field_233521_f_;
    }

    public void func_233525_b_()
    {
        if (this.field_233521_f_ != EmptyProfiler.INSTANCE)
        {
            IProfileResult iprofileresult = this.field_233521_f_.getResults();
            this.field_233521_f_ = EmptyProfiler.INSTANCE;

            if (iprofileresult.nanoTime() >= this.field_233518_c_)
            {
                File file1 = new File(this.field_233520_e_, "tick-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
                iprofileresult.writeToFile(file1);
                field_233516_a_.info("Recorded long tick -- wrote info to: {}", (Object)file1.getAbsolutePath());
            }
        }
    }

    @Nullable
    public static LongTickDetector func_233524_a_(String p_233524_0_)
    {
        return null;
    }

    public static IProfiler func_233523_a_(IProfiler p_233523_0_, @Nullable LongTickDetector p_233523_1_)
    {
        return p_233523_1_ != null ? IProfiler.func_233513_a_(p_233523_1_.func_233522_a_(), p_233523_0_) : p_233523_0_;
    }

    private LongTickDetector()
    {
        throw new RuntimeException("Synthetic constructor added by MCP, do not call");
    }
}
