package net.minecraft.client.util;

import java.util.Locale;

public enum UploadSpeed
{
    B,
    KB,
    MB,
    GB;

    public static UploadSpeed func_237682_a_(long p_237682_0_)
    {
        if (p_237682_0_ < 1024L)
        {
            return B;
        }
        else
        {
            try
            {
                int i = (int)(Math.log((double)p_237682_0_) / Math.log(1024.0D));
                String s = String.valueOf("KMGTPE".charAt(i - 1));
                return valueOf(s + "B");
            }
            catch (Exception exception)
            {
                return GB;
            }
        }
    }

    public static double func_237683_a_(long p_237683_0_, UploadSpeed p_237683_2_)
    {
        return p_237683_2_ == B ? (double)p_237683_0_ : (double)p_237683_0_ / Math.pow(1024.0D, (double)p_237683_2_.ordinal());
    }

    public static String func_237684_b_(long p_237684_0_)
    {
        int i = 1024;

        if (p_237684_0_ < 1024L)
        {
            return p_237684_0_ + " B";
        }
        else
        {
            int j = (int)(Math.log((double)p_237684_0_) / Math.log(1024.0D));
            String s = "KMGTPE".charAt(j - 1) + "";
            return String.format(Locale.ROOT, "%.1f %sB", (double)p_237684_0_ / Math.pow(1024.0D, (double)j), s);
        }
    }

    public static String func_237685_b_(long p_237685_0_, UploadSpeed p_237685_2_)
    {
        return String.format("%." + (p_237685_2_ == GB ? "1" : "0") + "f %s", func_237683_a_(p_237685_0_, p_237685_2_), p_237685_2_.name());
    }
}
