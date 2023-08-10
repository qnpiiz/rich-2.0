package net.minecraft.profiler;

import java.util.function.Supplier;

public interface IProfiler
{
    void startTick();

    void endTick();

    /**
     * Start section
     */
    void startSection(String name);

    void startSection(Supplier<String> nameSupplier);

    /**
     * End section
     */
    void endSection();

    void endStartSection(String name);

    void endStartSection(Supplier<String> nameSupplier);

    void func_230035_c_(String p_230035_1_);

    void func_230036_c_(Supplier<String> p_230036_1_);

    static IProfiler func_233513_a_(final IProfiler p_233513_0_, final IProfiler p_233513_1_)
    {
        if (p_233513_0_ == EmptyProfiler.INSTANCE)
        {
            return p_233513_1_;
        }
        else
        {
            return p_233513_1_ == EmptyProfiler.INSTANCE ? p_233513_0_ : new IProfiler()
            {
                public void startTick()
                {
                    p_233513_0_.startTick();
                    p_233513_1_.startTick();
                }
                public void endTick()
                {
                    p_233513_0_.endTick();
                    p_233513_1_.endTick();
                }
                public void startSection(String name)
                {
                    p_233513_0_.startSection(name);
                    p_233513_1_.startSection(name);
                }
                public void startSection(Supplier<String> nameSupplier)
                {
                    p_233513_0_.startSection(nameSupplier);
                    p_233513_1_.startSection(nameSupplier);
                }
                public void endSection()
                {
                    p_233513_0_.endSection();
                    p_233513_1_.endSection();
                }
                public void endStartSection(String name)
                {
                    p_233513_0_.endStartSection(name);
                    p_233513_1_.endStartSection(name);
                }
                public void endStartSection(Supplier<String> nameSupplier)
                {
                    p_233513_0_.endStartSection(nameSupplier);
                    p_233513_1_.endStartSection(nameSupplier);
                }
                public void func_230035_c_(String p_230035_1_)
                {
                    p_233513_0_.func_230035_c_(p_230035_1_);
                    p_233513_1_.func_230035_c_(p_230035_1_);
                }
                public void func_230036_c_(Supplier<String> p_230036_1_)
                {
                    p_233513_0_.func_230036_c_(p_230036_1_);
                    p_233513_1_.func_230036_c_(p_230036_1_);
                }
            };
        }
    }
}
