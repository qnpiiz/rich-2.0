package net.optifine.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;
import net.minecraft.client.renderer.texture.NativeImage;
import net.optifine.Config;

public class NativeMemory
{
    private static long imageAllocated = 0L;
    private static LongSupplier bufferAllocatedSupplier = makeLongSupplier(new String[][] {{"sun.misc.SharedSecrets", "getJavaNioAccess", "getDirectBufferPool", "getMemoryUsed"}, {"jdk.internal.misc.SharedSecrets", "getJavaNioAccess", "getDirectBufferPool", "getMemoryUsed"}});
    private static LongSupplier bufferMaximumSupplier = makeLongSupplier(new String[][] {{"sun.misc.VM", "maxDirectMemory"}, {"jdk.internal.misc.VM", "maxDirectMemory"}});

    public static long getBufferAllocated()
    {
        return bufferAllocatedSupplier == null ? -1L : bufferAllocatedSupplier.getAsLong();
    }

    public static long getBufferMaximum()
    {
        return bufferMaximumSupplier == null ? -1L : bufferMaximumSupplier.getAsLong();
    }

    public static synchronized void imageAllocated(NativeImage nativeImage)
    {
        imageAllocated += nativeImage.getSize();
    }

    public static synchronized void imageFreed(NativeImage nativeImage)
    {
        imageAllocated -= nativeImage.getSize();
    }

    public static long getImageAllocated()
    {
        return imageAllocated;
    }

    private static LongSupplier makeLongSupplier(String[][] paths)
    {
        List<Throwable> list = new ArrayList<>();

        for (int i = 0; i < paths.length; ++i)
        {
            String[] astring = paths[i];

            try
            {
                return makeLongSupplier(astring);
            }
            catch (Throwable throwable)
            {
                list.add(throwable);
            }
        }

        for (Throwable throwable1 : list)
        {
            Config.warn("" + throwable1.getClass().getName() + ": " + throwable1.getMessage());
        }

        return null;
    }

    private static LongSupplier makeLongSupplier(String[] path) throws Exception
    {
        if (path.length < 2)
        {
            return null;
        }
        else
        {
            Class oclass = Class.forName(path[0]);
            Method method = oclass.getMethod(path[1]);
            method.setAccessible(true);
            Object object = null;

            for (int i = 2; i < path.length; ++i)
            {
                String s = path[i];
                object = method.invoke(object);
                method = object.getClass().getMethod(s);
                method.setAccessible(true);
            }

            final Method method1 = method;
            final Object object1 = object;
            return new LongSupplier()
            {
                private boolean disabled = false;
                public long getAsLong()
                {
                    if (this.disabled)
                    {
                        return -1L;
                    }
                    else
                    {
                        try
                        {
                    
                            return (long) method1.invoke(object1);
                        }
                        catch (Throwable throwable)
                        {
                            Config.warn("" + throwable.getClass().getName() + ": " + throwable.getMessage());
                            this.disabled = true;
                            return -1L;
                        }
                    }
                }
            };
        }
    }
}
