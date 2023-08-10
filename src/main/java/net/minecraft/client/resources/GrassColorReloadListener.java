package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GrassColors;

public class GrassColorReloadListener extends ReloadListener<int[]>
{
    private static final ResourceLocation GRASS_LOCATION = new ResourceLocation("textures/colormap/grass.png");

    /**
     * Performs any reloading that can be done off-thread, such as file IO
     */
    protected int[] prepare(IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        try
        {
            return ColorMapLoader.loadColors(resourceManagerIn, GRASS_LOCATION);
        }
        catch (IOException ioexception)
        {
            throw new IllegalStateException("Failed to load grass color texture", ioexception);
        }
    }

    protected void apply(int[] objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        GrassColors.setGrassBiomeColorizer(objectIn);
    }
}
