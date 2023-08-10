package net.optifine.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.optifine.override.ChunkCacheOF;

public class LightCacheOF
{
    public static final float getBrightness(BlockState blockStateIn, IBlockDisplayReader worldIn, BlockPos blockPosIn)
    {
        float f = blockStateIn.getAmbientOcclusionLightValue(worldIn, blockPosIn);
        return BlockModelRenderer.fixAoLightValue(f);
    }

    public static final int getPackedLight(BlockState blockStateIn, IBlockDisplayReader worldIn, BlockPos blockPosIn)
    {
        if (worldIn instanceof ChunkCacheOF)
        {
            ChunkCacheOF chunkcacheof = (ChunkCacheOF)worldIn;
            int[] aint = chunkcacheof.getCombinedLights();
            int i = chunkcacheof.getPositionIndex(blockPosIn);

            if (i >= 0 && i < aint.length && aint != null)
            {
                int j = aint[i];

                if (j == -1)
                {
                    j = WorldRenderer.getPackedLightmapCoords(worldIn, blockStateIn, blockPosIn);
                    aint[i] = j;
                }

                return j;
            }
            else
            {
                return WorldRenderer.getPackedLightmapCoords(worldIn, blockStateIn, blockPosIn);
            }
        }
        else
        {
            return WorldRenderer.getPackedLightmapCoords(worldIn, blockStateIn, blockPosIn);
        }
    }
}
