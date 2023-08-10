package net.optifine;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.optifine.http.FileUploadThread;
import net.optifine.http.IFileUploadListener;
import net.optifine.shaders.Shaders;

public class CrashReporter
{
    public static void onCrashReport(CrashReport crashReport, CrashReportCategory category)
    {
        try
        {
            Throwable throwable = crashReport.getCrashCause();

            if (throwable == null)
            {
                return;
            }

            if (throwable.getClass().getName().contains(".fml.client.SplashProgress"))
            {
                return;
            }

            if (throwable.getClass() == Throwable.class)
            {
                return;
            }

            extendCrashReport(category);
            GameSettings gamesettings = Config.getGameSettings();

            if (gamesettings == null)
            {
                return;
            }

            if (!gamesettings.snooper)
            {
                return;
            }

            String s = "http://optifine.net/crashReport";
            String s1 = makeReport(crashReport);
            byte[] abyte = s1.getBytes("ASCII");
            IFileUploadListener ifileuploadlistener = new IFileUploadListener()
            {
                public void fileUploadFinished(String url, byte[] content, Throwable exception)
                {
                }
            };
            Map map = new HashMap();
            map.put("OF-Version", Config.getVersion());
            map.put("OF-Summary", makeSummary(crashReport));
            FileUploadThread fileuploadthread = new FileUploadThread(s, map, abyte, ifileuploadlistener);
            fileuploadthread.setPriority(10);
            fileuploadthread.start();
            Thread.sleep(1000L);
        }
        catch (Exception exception)
        {
            Config.dbg(exception.getClass().getName() + ": " + exception.getMessage());
        }
    }

    private static String makeReport(CrashReport crashReport)
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("OptiFineVersion: " + Config.getVersion() + "\n");
        stringbuffer.append("Summary: " + makeSummary(crashReport) + "\n");
        stringbuffer.append("\n");
        stringbuffer.append(crashReport.getCompleteReport());
        stringbuffer.append("\n");
        return stringbuffer.toString();
    }

    private static String makeSummary(CrashReport crashReport)
    {
        Throwable throwable = crashReport.getCrashCause();

        if (throwable == null)
        {
            return "Unknown";
        }
        else
        {
            StackTraceElement[] astacktraceelement = throwable.getStackTrace();
            String s = "unknown";

            if (astacktraceelement.length > 0)
            {
                s = astacktraceelement[0].toString().trim();
            }

            return throwable.getClass().getName() + ": " + throwable.getMessage() + " (" + crashReport.getDescription() + ") [" + s + "]";
        }
    }

    public static void extendCrashReport(CrashReportCategory cat)
    {
        cat.addDetail("OptiFine Version", Config.getVersion());
        cat.addDetail("OptiFine Build", Config.getBuild());

        if (Config.getGameSettings() != null)
        {
            cat.addDetail("Render Distance Chunks", "" + Config.getChunkViewDistance());
            cat.addDetail("Mipmaps", "" + Config.getMipmapLevels());
            cat.addDetail("Anisotropic Filtering", "" + Config.getAnisotropicFilterLevel());
            cat.addDetail("Antialiasing", "" + Config.getAntialiasingLevel());
            cat.addDetail("Multitexture", "" + Config.isMultiTexture());
        }

        cat.addDetail("Shaders", "" + Shaders.getShaderPackName());
        cat.addDetail("OpenGlVersion", "" + Config.openGlVersion);
        cat.addDetail("OpenGlRenderer", "" + Config.openGlRenderer);
        cat.addDetail("OpenGlVendor", "" + Config.openGlVendor);
        cat.addDetail("CpuCount", "" + Config.getAvailableProcessors());
    }
}
