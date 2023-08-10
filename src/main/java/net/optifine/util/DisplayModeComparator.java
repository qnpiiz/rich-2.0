package net.optifine.util;

import java.util.Comparator;
import net.minecraft.client.renderer.VideoMode;

public class DisplayModeComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        VideoMode videomode = (VideoMode)o1;
        VideoMode videomode1 = (VideoMode)o2;

        if (videomode.getWidth() != videomode1.getWidth())
        {
            return videomode.getWidth() - videomode1.getWidth();
        }
        else if (videomode.getHeight() != videomode1.getHeight())
        {
            return videomode.getHeight() - videomode1.getHeight();
        }
        else
        {
            int i = videomode.getRedBits() + videomode.getGreenBits() + videomode.getBlueBits();
            int j = videomode1.getRedBits() + videomode1.getGreenBits() + videomode1.getBlueBits();

            if (i != j)
            {
                return i - j;
            }
            else
            {
                return videomode.getRefreshRate() != videomode1.getRefreshRate() ? videomode.getRefreshRate() - videomode1.getRefreshRate() : 0;
            }
        }
    }
}
