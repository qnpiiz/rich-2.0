package net.minecraft.world.gen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class NoopSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig>
{
    public NoopSurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232133_1_)
    {
        super(p_i232133_1_);
    }

    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config)
    {
    }
}
