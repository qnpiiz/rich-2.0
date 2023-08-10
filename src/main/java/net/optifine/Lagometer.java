package net.optifine;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.profiler.IProfiler;
import net.optifine.util.MemoryMonitor;
import org.lwjgl.opengl.GL11;

public class Lagometer
{
    private static Minecraft mc;
    private static GameSettings gameSettings;
    private static IProfiler profiler;
    public static boolean active = false;
    public static Lagometer.TimerNano timerTick = new Lagometer.TimerNano();
    public static Lagometer.TimerNano timerScheduledExecutables = new Lagometer.TimerNano();
    public static Lagometer.TimerNano timerChunkUpload = new Lagometer.TimerNano();
    public static Lagometer.TimerNano timerChunkUpdate = new Lagometer.TimerNano();
    public static Lagometer.TimerNano timerVisibility = new Lagometer.TimerNano();
    public static Lagometer.TimerNano timerTerrain = new Lagometer.TimerNano();
    public static Lagometer.TimerNano timerServer = new Lagometer.TimerNano();
    private static long[] timesFrame = new long[512];
    private static long[] timesTick = new long[512];
    private static long[] timesScheduledExecutables = new long[512];
    private static long[] timesChunkUpload = new long[512];
    private static long[] timesChunkUpdate = new long[512];
    private static long[] timesVisibility = new long[512];
    private static long[] timesTerrain = new long[512];
    private static long[] timesServer = new long[512];
    private static boolean[] gcs = new boolean[512];
    private static int numRecordedFrameTimes = 0;
    private static long prevFrameTimeNano = -1L;
    private static long renderTimeNano = 0L;

    public static void updateLagometer()
    {
        if (mc == null)
        {
            mc = Minecraft.getInstance();
            gameSettings = mc.gameSettings;
            profiler = mc.getProfiler();
        }

        if (gameSettings.showDebugInfo && (gameSettings.ofLagometer || gameSettings.showLagometer))
        {
            active = true;
            long timeNowNano = System.nanoTime();

            if (prevFrameTimeNano == -1L)
            {
                prevFrameTimeNano = timeNowNano;
            }
            else
            {
                int j = numRecordedFrameTimes & timesFrame.length - 1;
                ++numRecordedFrameTimes;
                boolean flag = MemoryMonitor.isGcEvent();
                timesFrame[j] = timeNowNano - prevFrameTimeNano - renderTimeNano;
                timesTick[j] = timerTick.timeNano;
                timesScheduledExecutables[j] = timerScheduledExecutables.timeNano;
                timesChunkUpload[j] = timerChunkUpload.timeNano;
                timesChunkUpdate[j] = timerChunkUpdate.timeNano;
                timesVisibility[j] = timerVisibility.timeNano;
                timesTerrain[j] = timerTerrain.timeNano;
                timesServer[j] = timerServer.timeNano;
                gcs[j] = flag;
                timerTick.reset();
                timerScheduledExecutables.reset();
                timerVisibility.reset();
                timerChunkUpdate.reset();
                timerChunkUpload.reset();
                timerTerrain.reset();
                timerServer.reset();
                prevFrameTimeNano = System.nanoTime();
            }
        }
        else
        {
            active = false;
            prevFrameTimeNano = -1L;
        }
    }

    public static void showLagometer(MatrixStack matrixStackIn, int scaleFactor)
    {
        if (gameSettings != null)
        {
            if (gameSettings.ofLagometer || gameSettings.showLagometer)
            {
                long i = System.nanoTime();
                GlStateManager.clear(256);
                GlStateManager.matrixMode(5889);
                GlStateManager.pushMatrix();
                int j = mc.getMainWindow().getFramebufferWidth();
                int k = mc.getMainWindow().getFramebufferHeight();
                GlStateManager.enableColorMaterial();
                GlStateManager.loadIdentity();
                GlStateManager.ortho(0.0D, (double)j, (double)k, 0.0D, 1000.0D, 3000.0D);
                GlStateManager.matrixMode(5888);
                GlStateManager.pushMatrix();
                GlStateManager.loadIdentity();
                GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
                GL11.glLineWidth(1.0F);
                GlStateManager.disableTexture();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);

                for (int l = 0; l < timesFrame.length; ++l)
                {
                    int i1 = (l - numRecordedFrameTimes & timesFrame.length - 1) * 100 / timesFrame.length;
                    i1 = i1 + 155;
                    float f = (float)k;
                    long j1 = 0L;

                    if (gcs[l])
                    {
                        renderTime(l, timesFrame[l], i1, i1 / 2, 0, f, bufferbuilder);
                    }
                    else
                    {
                        renderTime(l, timesFrame[l], i1, i1, i1, f, bufferbuilder);
                        f = f - (float)renderTime(l, timesServer[l], i1 / 2, i1 / 2, i1 / 2, f, bufferbuilder);
                        f = f - (float)renderTime(l, timesTerrain[l], 0, i1, 0, f, bufferbuilder);
                        f = f - (float)renderTime(l, timesVisibility[l], i1, i1, 0, f, bufferbuilder);
                        f = f - (float)renderTime(l, timesChunkUpdate[l], i1, 0, 0, f, bufferbuilder);
                        f = f - (float)renderTime(l, timesChunkUpload[l], i1, 0, i1, f, bufferbuilder);
                        f = f - (float)renderTime(l, timesScheduledExecutables[l], 0, 0, i1, f, bufferbuilder);
                        float f2 = f - (float)renderTime(l, timesTick[l], 0, i1, i1, f, bufferbuilder);
                    }
                }

                renderTimeDivider(0, timesFrame.length, 33333333L, 196, 196, 196, (float)k, bufferbuilder);
                renderTimeDivider(0, timesFrame.length, 16666666L, 196, 196, 196, (float)k, bufferbuilder);
                tessellator.draw();
                GlStateManager.enableTexture();
                int i3 = k - 80;
                int j3 = k - 160;
                String s = Config.isShowFrameTime() ? "33" : "30";
                String s1 = Config.isShowFrameTime() ? "17" : "60";
                mc.fontRenderer.drawString(matrixStackIn, s, 2.0F, (float)(j3 + 1), -8947849);
                mc.fontRenderer.drawString(matrixStackIn, s, 1.0F, (float)j3, -3881788);
                mc.fontRenderer.drawString(matrixStackIn, s1, 2.0F, (float)(i3 + 1), -8947849);
                mc.fontRenderer.drawString(matrixStackIn, s1, 1.0F, (float)i3, -3881788);
                GlStateManager.matrixMode(5889);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
                GlStateManager.popMatrix();
                GlStateManager.enableTexture();
                float f1 = 1.0F - (float)((double)(System.currentTimeMillis() - MemoryMonitor.getStartTimeMs()) / 1000.0D);
                f1 = Config.limit(f1, 0.0F, 1.0F);
                int k1 = (int)(170.0F + f1 * 85.0F);
                int l1 = (int)(100.0F + f1 * 55.0F);
                int i2 = (int)(10.0F + f1 * 10.0F);
                int j2 = k1 << 16 | l1 << 8 | i2;
                int k2 = 512 / scaleFactor + 2;
                int l2 = k / scaleFactor - 8;
                IngameGui ingamegui = mc.ingameGUI;
                IngameGui.fill(matrixStackIn, k2 - 1, l2 - 1, k2 + 50, l2 + 10, -1605349296);
                mc.fontRenderer.drawString(matrixStackIn, " " + MemoryMonitor.getGcRateMb() + " MB/s", (float)k2, (float)l2, j2);
                renderTimeNano = System.nanoTime() - i;
            }
        }
    }

    private static long renderTime(int frameNum, long time, int r, int g, int b, float baseHeight, BufferBuilder tessellator)
    {
        long i = time / 200000L;

        if (i < 3L)
        {
            return 0L;
        }
        else
        {
            tessellator.pos((double)((float)frameNum + 0.5F), (double)(baseHeight - (float)i + 0.5F), 0.0D).color(r, g, b, 255).endVertex();
            tessellator.pos((double)((float)frameNum + 0.5F), (double)(baseHeight + 0.5F), 0.0D).color(r, g, b, 255).endVertex();
            return i;
        }
    }

    private static long renderTimeDivider(int frameStart, int frameEnd, long time, int r, int g, int b, float baseHeight, BufferBuilder tessellator)
    {
        long i = time / 200000L;

        if (i < 3L)
        {
            return 0L;
        }
        else
        {
            tessellator.pos((double)((float)frameStart + 0.5F), (double)(baseHeight - (float)i + 0.5F), 0.0D).color(r, g, b, 255).endVertex();
            tessellator.pos((double)((float)frameEnd + 0.5F), (double)(baseHeight - (float)i + 0.5F), 0.0D).color(r, g, b, 255).endVertex();
            return i;
        }
    }

    public static boolean isActive()
    {
        return active;
    }

    public static class TimerNano
    {
        public long timeStartNano = 0L;
        public long timeNano = 0L;

        public void start()
        {
            if (Lagometer.active)
            {
                if (this.timeStartNano == 0L)
                {
                    this.timeStartNano = System.nanoTime();
                }
            }
        }

        public void end()
        {
            if (Lagometer.active)
            {
                if (this.timeStartNano != 0L)
                {
                    this.timeNano += System.nanoTime() - this.timeStartNano;
                    this.timeStartNano = 0L;
                }
            }
        }

        private void reset()
        {
            this.timeNano = 0L;
            this.timeStartNano = 0L;
        }
    }
}
