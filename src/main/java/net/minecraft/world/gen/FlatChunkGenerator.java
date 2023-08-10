package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.structure.StructureManager;

public class FlatChunkGenerator extends ChunkGenerator
{
    public static final Codec<FlatChunkGenerator> field_236069_d_ = FlatGenerationSettings.field_236932_a_.fieldOf("settings").xmap(FlatChunkGenerator::new, FlatChunkGenerator::func_236073_g_).codec();
    private final FlatGenerationSettings field_236070_e_;

    public FlatChunkGenerator(FlatGenerationSettings p_i231902_1_)
    {
        super(new SingleBiomeProvider(p_i231902_1_.func_236942_c_()), new SingleBiomeProvider(p_i231902_1_.getBiome()), p_i231902_1_.func_236943_d_(), 0L);
        this.field_236070_e_ = p_i231902_1_;
    }

    protected Codec <? extends ChunkGenerator > func_230347_a_()
    {
        return field_236069_d_;
    }

    public ChunkGenerator func_230349_a_(long p_230349_1_)
    {
        return this;
    }

    public FlatGenerationSettings func_236073_g_()
    {
        return this.field_236070_e_;
    }

    /**
     * Generate the SURFACE part of a chunk
     */
    public void generateSurface(WorldGenRegion p_225551_1_, IChunk p_225551_2_)
    {
    }

    public int getGroundHeight()
    {
        BlockState[] ablockstate = this.field_236070_e_.getStates();

        for (int i = 0; i < ablockstate.length; ++i)
        {
            BlockState blockstate = ablockstate[i] == null ? Blocks.AIR.getDefaultState() : ablockstate[i];

            if (!Heightmap.Type.MOTION_BLOCKING.getHeightLimitPredicate().test(blockstate))
            {
                return i - 1;
            }
        }

        return ablockstate.length;
    }

    public void func_230352_b_(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_)
    {
        BlockState[] ablockstate = this.field_236070_e_.getStates();
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        Heightmap heightmap = p_230352_3_.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap1 = p_230352_3_.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

        for (int i = 0; i < ablockstate.length; ++i)
        {
            BlockState blockstate = ablockstate[i];

            if (blockstate != null)
            {
                for (int j = 0; j < 16; ++j)
                {
                    for (int k = 0; k < 16; ++k)
                    {
                        p_230352_3_.setBlockState(blockpos$mutable.setPos(j, i, k), blockstate, false);
                        heightmap.update(j, i, k, blockstate);
                        heightmap1.update(j, i, k, blockstate);
                    }
                }
            }
        }
    }

    public int getHeight(int x, int z, Heightmap.Type heightmapType)
    {
        BlockState[] ablockstate = this.field_236070_e_.getStates();

        for (int i = ablockstate.length - 1; i >= 0; --i)
        {
            BlockState blockstate = ablockstate[i];

            if (blockstate != null && heightmapType.getHeightLimitPredicate().test(blockstate))
            {
                return i + 1;
            }
        }

        return 0;
    }

    public IBlockReader func_230348_a_(int p_230348_1_, int p_230348_2_)
    {
        return new Blockreader(Arrays.stream(this.field_236070_e_.getStates()).map((p_236072_0_) ->
        {
            return p_236072_0_ == null ? Blocks.AIR.getDefaultState() : p_236072_0_;
        }).toArray((p_236071_0_) ->
        {
            return new BlockState[p_236071_0_];
        }));
    }
}
