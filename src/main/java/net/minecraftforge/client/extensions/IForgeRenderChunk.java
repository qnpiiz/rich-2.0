package net.minecraftforge.client.extensions;

import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IForgeRenderChunk
{
default ChunkRenderCache createRegionRenderCache(World world, BlockPos from, BlockPos to, int subtract)
    {
        return ChunkRenderCache.generateCache(world, from, to, subtract);
    }
}
