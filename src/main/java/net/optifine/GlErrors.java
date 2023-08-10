package net.optifine;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class GlErrors
{
    private static boolean frameStarted = false;
    private static long timeCheckStartMs = -1L;
    private static Int2ObjectOpenHashMap<GlErrors.GlError> glErrors = new Int2ObjectOpenHashMap<>();
    private static final long CHECK_INTERVAL_MS = 3000L;
    private static final int CHECK_ERROR_MAX = 10;

    public static void frameStart()
    {
        frameStarted = true;

        if (!glErrors.isEmpty())
        {
            if (timeCheckStartMs < 0L)
            {
                timeCheckStartMs = System.currentTimeMillis();
            }

            if (System.currentTimeMillis() > timeCheckStartMs + 3000L)
            {
                for (GlErrors.GlError glerrors$glerror : glErrors.values())
                {
                    glerrors$glerror.onFrameStart();
                }

                timeCheckStartMs = System.currentTimeMillis();
            }
        }
    }

    public static boolean isEnabled(int error)
    {
        if (!frameStarted)
        {
            return true;
        }
        else
        {
            GlErrors.GlError glerrors$glerror = getGlError(error);
            return glerrors$glerror.isEnabled();
        }
    }

    private static GlErrors.GlError getGlError(int error)
    {
        GlErrors.GlError glerrors$glerror = glErrors.get(error);

        if (glerrors$glerror == null)
        {
            glerrors$glerror = new GlErrors.GlError(error);
            glErrors.put(error, glerrors$glerror);
        }

        return glerrors$glerror;
    }

    public static class GlError
    {
        private int id;
        private int countErrors = 0;
        private int countErrorsSuppressed = 0;
        private boolean suppressed = false;
        private boolean oneErrorEnabled = false;

        public GlError(int id)
        {
            this.id = id;
        }

        public void onFrameStart()
        {
            if (this.countErrorsSuppressed > 0)
            {
                Config.error("Suppressed " + this.countErrors + " OpenGL errors: " + this.id);
            }

            this.suppressed = this.countErrors > 10;
            this.countErrors = 0;
            this.countErrorsSuppressed = 0;
            this.oneErrorEnabled = true;
        }

        public boolean isEnabled()
        {
            ++this.countErrors;

            if (this.oneErrorEnabled)
            {
                this.oneErrorEnabled = false;
                return true;
            }
            else
            {
                if (this.suppressed)
                {
                    ++this.countErrorsSuppressed;
                }

                return !this.suppressed;
            }
        }
    }
}
