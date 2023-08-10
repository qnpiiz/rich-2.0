package net.minecraft.profiler;

import java.util.function.Supplier;

public class EmptyProfiler implements IResultableProfiler
{
    public static final EmptyProfiler INSTANCE = new EmptyProfiler();

    private EmptyProfiler()
    {
    }

    public void startTick()
    {
    }

    public void endTick()
    {
    }

    /**
     * Start section
     */
    public void startSection(String name)
    {
    }

    public void startSection(Supplier<String> nameSupplier)
    {
    }

    /**
     * End section
     */
    public void endSection()
    {
    }

    public void endStartSection(String name)
    {
    }

    public void endStartSection(Supplier<String> nameSupplier)
    {
    }

    public void func_230035_c_(String p_230035_1_)
    {
    }

    public void func_230036_c_(Supplier<String> p_230036_1_)
    {
    }

    public IProfileResult getResults()
    {
        return EmptyProfileResult.INSTANCE;
    }
}
