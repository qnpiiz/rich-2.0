package net.minecraft.world.gen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class ErodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder
{
    private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
    private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
    private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();

    public ErodedBadlandsSurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232125_1_)
    {
        super(p_i232125_1_);
    }

    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config)
    {
        double d0 = 0.0D;
        double d1 = Math.min(Math.abs(noise), this.field_215435_c.noiseAt((double)x * 0.25D, (double)z * 0.25D, false) * 15.0D);

        if (d1 > 0.0D)
        {
            double d2 = 0.001953125D;
            double d3 = Math.abs(this.field_215437_d.noiseAt((double)x * 0.001953125D, (double)z * 0.001953125D, false));
            d0 = d1 * d1 * 2.5D;
            double d4 = Math.ceil(d3 * 50.0D) + 14.0D;

            if (d0 > d4)
            {
                d0 = d4;
            }

            d0 = d0 + 64.0D;
        }

        int i1 = x & 15;
        int i = z & 15;
        BlockState blockstate3 = WHITE_TERRACOTTA;
        ISurfaceBuilderConfig isurfacebuilderconfig = biomeIn.getGenerationSettings().getSurfaceBuilderConfig();
        BlockState blockstate4 = isurfacebuilderconfig.getUnder();
        BlockState blockstate = isurfacebuilderconfig.getTop();
        BlockState blockstate1 = blockstate4;
        int j = (int)(noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        boolean flag = Math.cos(noise / 3.0D * Math.PI) > 0.0D;
        int k = -1;
        boolean flag1 = false;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (int l = Math.max(startHeight, (int)d0 + 1); l >= 0; --l)
        {
            blockpos$mutable.setPos(i1, l, i);

            if (chunkIn.getBlockState(blockpos$mutable).isAir() && l < (int)d0)
            {
                chunkIn.setBlockState(blockpos$mutable, defaultBlock, false);
            }

            BlockState blockstate2 = chunkIn.getBlockState(blockpos$mutable);

            if (blockstate2.isAir())
            {
                k = -1;
            }
            else if (blockstate2.isIn(defaultBlock.getBlock()))
            {
                if (k == -1)
                {
                    flag1 = false;

                    if (j <= 0)
                    {
                        blockstate3 = Blocks.AIR.getDefaultState();
                        blockstate1 = defaultBlock;
                    }
                    else if (l >= seaLevel - 4 && l <= seaLevel + 1)
                    {
                        blockstate3 = WHITE_TERRACOTTA;
                        blockstate1 = blockstate4;
                    }

                    if (l < seaLevel && (blockstate3 == null || blockstate3.isAir()))
                    {
                        blockstate3 = defaultFluid;
                    }

                    k = j + Math.max(0, l - seaLevel);

                    if (l >= seaLevel - 1)
                    {
                        if (l <= seaLevel + 3 + j)
                        {
                            chunkIn.setBlockState(blockpos$mutable, blockstate, false);
                            flag1 = true;
                        }
                        else
                        {
                            BlockState blockstate5;

                            if (l >= 64 && l <= 127)
                            {
                                if (flag)
                                {
                                    blockstate5 = TERRACOTTA;
                                }
                                else
                                {
                                    blockstate5 = this.func_215431_a(x, l, z);
                                }
                            }
                            else
                            {
                                blockstate5 = ORANGE_TERRACOTTA;
                            }

                            chunkIn.setBlockState(blockpos$mutable, blockstate5, false);
                        }
                    }
                    else
                    {
                        chunkIn.setBlockState(blockpos$mutable, blockstate1, false);
                        Block block = blockstate1.getBlock();

                        if (block == Blocks.WHITE_TERRACOTTA || block == Blocks.ORANGE_TERRACOTTA || block == Blocks.MAGENTA_TERRACOTTA || block == Blocks.LIGHT_BLUE_TERRACOTTA || block == Blocks.YELLOW_TERRACOTTA || block == Blocks.LIME_TERRACOTTA || block == Blocks.PINK_TERRACOTTA || block == Blocks.GRAY_TERRACOTTA || block == Blocks.LIGHT_GRAY_TERRACOTTA || block == Blocks.CYAN_TERRACOTTA || block == Blocks.PURPLE_TERRACOTTA || block == Blocks.BLUE_TERRACOTTA || block == Blocks.BROWN_TERRACOTTA || block == Blocks.GREEN_TERRACOTTA || block == Blocks.RED_TERRACOTTA || block == Blocks.BLACK_TERRACOTTA)
                        {
                            chunkIn.setBlockState(blockpos$mutable, ORANGE_TERRACOTTA, false);
                        }
                    }
                }
                else if (k > 0)
                {
                    --k;

                    if (flag1)
                    {
                        chunkIn.setBlockState(blockpos$mutable, ORANGE_TERRACOTTA, false);
                    }
                    else
                    {
                        chunkIn.setBlockState(blockpos$mutable, this.func_215431_a(x, l, z), false);
                    }
                }
            }
        }
    }
}
