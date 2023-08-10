package net.optifine.util;

import net.minecraft.world.chunk.Chunk;
import net.optifine.ChunkOF;

public class ChunkUtils
{
    public static boolean hasEntities(Chunk chunk)
    {
        if (chunk instanceof ChunkOF)
        {
            ChunkOF chunkof = (ChunkOF)chunk;
            return chunkof.hasEntities();
        }
        else
        {
            return true;
        }
    }

    public static boolean isLoaded(Chunk chunk)
    {
        if (chunk instanceof ChunkOF)
        {
            ChunkOF chunkof = (ChunkOF)chunk;
            return chunkof.isLoaded();
        }
        else
        {
            return false;
        }
    }
}
