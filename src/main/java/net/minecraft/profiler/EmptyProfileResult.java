package net.minecraft.profiler;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class EmptyProfileResult implements IProfileResult
{
    public static final EmptyProfileResult INSTANCE = new EmptyProfileResult();

    private EmptyProfileResult()
    {
    }

    public List<DataPoint> getDataPoints(String sectionPath)
    {
        return Collections.emptyList();
    }

    public boolean writeToFile(File p_219919_1_)
    {
        return false;
    }

    public long timeStop()
    {
        return 0L;
    }

    public int ticksStop()
    {
        return 0;
    }

    public long timeStart()
    {
        return 0L;
    }

    public int ticksStart()
    {
        return 0;
    }
}
