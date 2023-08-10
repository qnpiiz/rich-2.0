package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class WorldCarver<C extends ICarverConfig>
{
    public static final WorldCarver<ProbabilityConfig> CAVE = register("cave", new CaveWorldCarver(ProbabilityConfig.field_236576_b_, 256));
    public static final WorldCarver<ProbabilityConfig> field_236240_b_ = register("nether_cave", new NetherCaveCarver(ProbabilityConfig.field_236576_b_));
    public static final WorldCarver<ProbabilityConfig> CANYON = register("canyon", new CanyonWorldCarver(ProbabilityConfig.field_236576_b_));
    public static final WorldCarver<ProbabilityConfig> UNDERWATER_CANYON = register("underwater_canyon", new UnderwaterCanyonWorldCarver(ProbabilityConfig.field_236576_b_));
    public static final WorldCarver<ProbabilityConfig> UNDERWATER_CAVE = register("underwater_cave", new UnderwaterCaveWorldCarver(ProbabilityConfig.field_236576_b_));
    protected static final BlockState AIR = Blocks.AIR.getDefaultState();
    protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
    protected static final FluidState WATER = Fluids.WATER.getDefaultState();
    protected static final FluidState LAVA = Fluids.LAVA.getDefaultState();
    protected Set<Block> carvableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE);
    protected Set<Fluid> carvableFluids = ImmutableSet.of(Fluids.WATER);
    private final Codec<ConfiguredCarver<C>> field_236241_m_;
    protected final int maxHeight;

    private static <C extends ICarverConfig, F extends WorldCarver<C>> F register(String key, F carver)
    {
        return Registry.register(Registry.CARVER, key, carver);
    }

    public WorldCarver(Codec<C> p_i231921_1_, int p_i231921_2_)
    {
        this.maxHeight = p_i231921_2_;
        this.field_236241_m_ = p_i231921_1_.fieldOf("config").xmap(this::func_242761_a, ConfiguredCarver::func_242760_a).codec();
    }

    public ConfiguredCarver<C> func_242761_a(C p_242761_1_)
    {
        return new ConfiguredCarver<>(this, p_242761_1_);
    }

    public Codec<ConfiguredCarver<C>> func_236244_c_()
    {
        return this.field_236241_m_;
    }

    public int func_222704_c()
    {
        return 4;
    }

    protected boolean func_227208_a_(IChunk chunk, Function<BlockPos, Biome> biomePos, long seed, int seaLevel, int chunkX, int chunkZ, double randOffsetXCoord, double startY, double randOffsetZCoord, double p_227208_14_, double p_227208_16_, BitSet carvingMask)
    {
        Random random = new Random(seed + (long)chunkX + (long)chunkZ);
        double d0 = (double)(chunkX * 16 + 8);
        double d1 = (double)(chunkZ * 16 + 8);

        if (!(randOffsetXCoord < d0 - 16.0D - p_227208_14_ * 2.0D) && !(randOffsetZCoord < d1 - 16.0D - p_227208_14_ * 2.0D) && !(randOffsetXCoord > d0 + 16.0D + p_227208_14_ * 2.0D) && !(randOffsetZCoord > d1 + 16.0D + p_227208_14_ * 2.0D))
        {
            int i = Math.max(MathHelper.floor(randOffsetXCoord - p_227208_14_) - chunkX * 16 - 1, 0);
            int j = Math.min(MathHelper.floor(randOffsetXCoord + p_227208_14_) - chunkX * 16 + 1, 16);
            int k = Math.max(MathHelper.floor(startY - p_227208_16_) - 1, 1);
            int l = Math.min(MathHelper.floor(startY + p_227208_16_) + 1, this.maxHeight - 8);
            int i1 = Math.max(MathHelper.floor(randOffsetZCoord - p_227208_14_) - chunkZ * 16 - 1, 0);
            int j1 = Math.min(MathHelper.floor(randOffsetZCoord + p_227208_14_) - chunkZ * 16 + 1, 16);

            if (this.func_222700_a(chunk, chunkX, chunkZ, i, j, k, l, i1, j1))
            {
                return false;
            }
            else
            {
                boolean flag = false;
                BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
                BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();
                BlockPos.Mutable blockpos$mutable2 = new BlockPos.Mutable();

                for (int k1 = i; k1 < j; ++k1)
                {
                    int l1 = k1 + chunkX * 16;
                    double d2 = ((double)l1 + 0.5D - randOffsetXCoord) / p_227208_14_;

                    for (int i2 = i1; i2 < j1; ++i2)
                    {
                        int j2 = i2 + chunkZ * 16;
                        double d3 = ((double)j2 + 0.5D - randOffsetZCoord) / p_227208_14_;

                        if (!(d2 * d2 + d3 * d3 >= 1.0D))
                        {
                            MutableBoolean mutableboolean = new MutableBoolean(false);

                            for (int k2 = l; k2 > k; --k2)
                            {
                                double d4 = ((double)k2 - 0.5D - startY) / p_227208_16_;

                                if (!this.func_222708_a(d2, d4, d3, k2))
                                {
                                    flag |= this.func_230358_a_(chunk, biomePos, carvingMask, random, blockpos$mutable, blockpos$mutable1, blockpos$mutable2, seaLevel, chunkX, chunkZ, l1, j2, k1, k2, i2, mutableboolean);
                                }
                            }
                        }
                    }
                }

                return flag;
            }
        }
        else
        {
            return false;
        }
    }

    protected boolean func_230358_a_(IChunk p_230358_1_, Function<BlockPos, Biome> p_230358_2_, BitSet p_230358_3_, Random p_230358_4_, BlockPos.Mutable p_230358_5_, BlockPos.Mutable p_230358_6_, BlockPos.Mutable p_230358_7_, int p_230358_8_, int p_230358_9_, int p_230358_10_, int p_230358_11_, int p_230358_12_, int p_230358_13_, int p_230358_14_, int p_230358_15_, MutableBoolean p_230358_16_)
    {
        int i = p_230358_13_ | p_230358_15_ << 4 | p_230358_14_ << 8;

        if (p_230358_3_.get(i))
        {
            return false;
        }
        else
        {
            p_230358_3_.set(i);
            p_230358_5_.setPos(p_230358_11_, p_230358_14_, p_230358_12_);
            BlockState blockstate = p_230358_1_.getBlockState(p_230358_5_);
            BlockState blockstate1 = p_230358_1_.getBlockState(p_230358_6_.setAndMove(p_230358_5_, Direction.UP));

            if (blockstate.isIn(Blocks.GRASS_BLOCK) || blockstate.isIn(Blocks.MYCELIUM))
            {
                p_230358_16_.setTrue();
            }

            if (!this.canCarveBlock(blockstate, blockstate1))
            {
                return false;
            }
            else
            {
                if (p_230358_14_ < 11)
                {
                    p_230358_1_.setBlockState(p_230358_5_, LAVA.getBlockState(), false);
                }
                else
                {
                    p_230358_1_.setBlockState(p_230358_5_, CAVE_AIR, false);

                    if (p_230358_16_.isTrue())
                    {
                        p_230358_7_.setAndMove(p_230358_5_, Direction.DOWN);

                        if (p_230358_1_.getBlockState(p_230358_7_).isIn(Blocks.DIRT))
                        {
                            p_230358_1_.setBlockState(p_230358_7_, p_230358_2_.apply(p_230358_5_).getGenerationSettings().getSurfaceBuilderConfig().getTop(), false);
                        }
                    }
                }

                return true;
            }
        }
    }

    public abstract boolean carveRegion(IChunk chunk, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, C config);

    public abstract boolean shouldCarve(Random rand, int chunkX, int chunkZ, C config);

    protected boolean isCarvable(BlockState p_222706_1_)
    {
        return this.carvableBlocks.contains(p_222706_1_.getBlock());
    }

    protected boolean canCarveBlock(BlockState state, BlockState aboveState)
    {
        return this.isCarvable(state) || (state.isIn(Blocks.SAND) || state.isIn(Blocks.GRAVEL)) && !aboveState.getFluidState().isTagged(FluidTags.WATER);
    }

    protected boolean func_222700_a(IChunk chunkIn, int chunkX, int chunkZ, int minX, int maxX, int minY, int maxY, int minZ, int maxZ)
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (int i = minX; i < maxX; ++i)
        {
            for (int j = minZ; j < maxZ; ++j)
            {
                for (int k = minY - 1; k <= maxY + 1; ++k)
                {
                    if (this.carvableFluids.contains(chunkIn.getFluidState(blockpos$mutable.setPos(i + chunkX * 16, k, j + chunkZ * 16)).getFluid()))
                    {
                        return true;
                    }

                    if (k != maxY + 1 && !this.isOnEdge(minX, maxX, minZ, maxZ, i, j))
                    {
                        k = maxY;
                    }
                }
            }
        }

        return false;
    }

    private boolean isOnEdge(int minX, int maxX, int minZ, int maxZ, int x, int z)
    {
        return x == minX || x == maxX - 1 || z == minZ || z == maxZ - 1;
    }

    protected boolean func_222702_a(int p_222702_1_, int p_222702_2_, double p_222702_3_, double p_222702_5_, int p_222702_7_, int p_222702_8_, float p_222702_9_)
    {
        double d0 = (double)(p_222702_1_ * 16 + 8);
        double d1 = (double)(p_222702_2_ * 16 + 8);
        double d2 = p_222702_3_ - d0;
        double d3 = p_222702_5_ - d1;
        double d4 = (double)(p_222702_8_ - p_222702_7_);
        double d5 = (double)(p_222702_9_ + 2.0F + 16.0F);
        return d2 * d2 + d3 * d3 - d4 * d4 <= d5 * d5;
    }

    protected abstract boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_);
}
