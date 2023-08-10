package net.minecraft.world.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.level.ColorResolver;

public class BiomeColors
{
    public static final ColorResolver GRASS_COLOR = Biome::getGrassColor;
    public static final ColorResolver FOLIAGE_COLOR = (biome, x, z) ->
    {
        return biome.getFoliageColor();
    };
    public static final ColorResolver WATER_COLOR = (biome, x, z) ->
    {
        return biome.getWaterColor();
    };

    private static int getBlockColor(IBlockDisplayReader worldIn, BlockPos blockPosIn, ColorResolver colorResolverIn)
    {
        return worldIn.getBlockColor(blockPosIn, colorResolverIn);
    }

    public static int getGrassColor(IBlockDisplayReader worldIn, BlockPos blockPosIn)
    {
        return getBlockColor(worldIn, blockPosIn, GRASS_COLOR);
    }

    public static int getFoliageColor(IBlockDisplayReader worldIn, BlockPos blockPosIn)
    {
        return getBlockColor(worldIn, blockPosIn, FOLIAGE_COLOR);
    }

    public static int getWaterColor(IBlockDisplayReader worldIn, BlockPos blockPosIn)
    {
        return getBlockColor(worldIn, blockPosIn, WATER_COLOR);
    }
}
