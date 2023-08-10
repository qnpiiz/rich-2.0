package net.optifine.util;

import java.util.Comparator;
import net.minecraft.client.renderer.VideoMode;

public class VideoModeComparator implements Comparator<VideoMode>
{
    public int compare(VideoMode vm1, VideoMode vm2)
    {
        if (vm1.getWidth() != vm2.getWidth())
        {
            return vm1.getWidth() - vm2.getWidth();
        }
        else if (vm1.getHeight() != vm2.getHeight())
        {
            return vm1.getHeight() - vm2.getHeight();
        }
        else if (vm1.getRefreshRate() != vm2.getRefreshRate())
        {
            return vm1.getRefreshRate() - vm2.getRefreshRate();
        }
        else
        {
            int i = vm1.getRedBits() + vm1.getGreenBits() + vm1.getBlueBits();
            int j = vm2.getRedBits() + vm2.getGreenBits() + vm2.getBlueBits();
            return i != j ? i - j : 0;
        }
    }
}
