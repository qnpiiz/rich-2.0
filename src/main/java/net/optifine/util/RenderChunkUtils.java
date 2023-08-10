package net.optifine.util;

import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkSection;

public class RenderChunkUtils
{
    public static int getCountBlocks(ChunkRenderDispatcher.ChunkRender renderChunk)
    {
        ChunkSection[] achunksection = renderChunk.getChunk().getSections();

        if (achunksection == null)
        {
            return 0;
        }
        else
        {
            int i = renderChunk.getPosition().getY() >> 4;
            ChunkSection chunksection = achunksection[i];
            return chunksection == null ? 0 : chunksection.getBlockRefCount();
        }
    }

    public static double getRelativeBufferSize(ChunkRenderDispatcher.ChunkRender renderChunk)
    {
        int i = getCountBlocks(renderChunk);
        return getRelativeBufferSize(i);
    }

    public static double getRelativeBufferSize(int blockCount)
    {
        double d0 = (double)blockCount / 4096.0D;
        d0 = d0 * 0.995D;
        double d1 = d0 * 2.0D - 1.0D;
        d1 = MathHelper.clamp(d1, -1.0D, 1.0D);
        return (double)MathHelper.sqrt(1.0D - d1 * d1);
    }
}
