package net.minecraft.server.dedicated;

import com.google.common.collect.Streams;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Bootstrap;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerHangWatchdog implements Runnable
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final DedicatedServer server;
    private final long maxTickTime;

    public ServerHangWatchdog(DedicatedServer server)
    {
        this.server = server;
        this.maxTickTime = server.getMaxTickTime();
    }

    public void run()
    {
        while (this.server.isServerRunning())
        {
            long i = this.server.getServerTime();
            long j = Util.milliTime();
            long k = j - i;

            if (k > this.maxTickTime)
            {
                LOGGER.fatal("A single server tick took {} seconds (should be max {})", String.format(Locale.ROOT, "%.2f", (float)k / 1000.0F), String.format(Locale.ROOT, "%.2f", 0.05F));
                LOGGER.fatal("Considering it to be crashed, server will forcibly shutdown.");
                ThreadMXBean threadmxbean = ManagementFactory.getThreadMXBean();
                ThreadInfo[] athreadinfo = threadmxbean.dumpAllThreads(true, true);
                StringBuilder stringbuilder = new StringBuilder();
                Error error = new Error("Watchdog");

                for (ThreadInfo threadinfo : athreadinfo)
                {
                    if (threadinfo.getThreadId() == this.server.getExecutionThread().getId())
                    {
                        error.setStackTrace(threadinfo.getStackTrace());
                    }

                    stringbuilder.append((Object)threadinfo);
                    stringbuilder.append("\n");
                }

                CrashReport crashreport = new CrashReport("Watching Server", error);
                this.server.addServerInfoToCrashReport(crashreport);
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Thread Dump");
                crashreportcategory.addDetail("Threads", stringbuilder);
                CrashReportCategory crashreportcategory1 = crashreport.makeCategory("Performance stats");
                crashreportcategory1.addDetail("Random tick rate", () ->
                {
                    return this.server.func_240793_aU_().getGameRulesInstance().get(GameRules.RANDOM_TICK_SPEED).toString();
                });
                crashreportcategory1.addDetail("Level stats", () ->
                {
                    return Streams.stream(this.server.getWorlds()).map((p_244716_0_) -> {
                        return p_244716_0_.getDimensionKey() + ": " + p_244716_0_.func_244521_F();
                    }).collect(Collectors.joining(",\n"));
                });
                Bootstrap.printToSYSOUT("Crash report:\n" + crashreport.getCompleteReport());
                File file1 = new File(new File(this.server.getDataDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");

                if (crashreport.saveToFile(file1))
                {
                    LOGGER.error("This crash report has been saved to: {}", (Object)file1.getAbsolutePath());
                }
                else
                {
                    LOGGER.error("We were unable to save this crash report to disk.");
                }

                this.scheduleHalt();
            }

            try
            {
                Thread.sleep(i + this.maxTickTime - j);
            }
            catch (InterruptedException interruptedexception)
            {
            }
        }
    }

    private void scheduleHalt()
    {
        try
        {
            Timer timer = new Timer();
            timer.schedule(new TimerTask()
            {
                public void run()
                {
                    Runtime.getRuntime().halt(1);
                }
            }, 10000L);
            System.exit(1);
        }
        catch (Throwable throwable)
        {
            Runtime.getRuntime().halt(1);
        }
    }
}
