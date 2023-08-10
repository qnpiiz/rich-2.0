package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

public class DebugChunkGenerator extends ChunkGenerator
{
    public static final Codec<DebugChunkGenerator> field_236066_e_ = RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).xmap(DebugChunkGenerator::new, DebugChunkGenerator::func_242727_g).stable().codec();
    private static final List<BlockState> ALL_VALID_STATES = StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap((p_236067_0_) ->
    {
        return p_236067_0_.getStateContainer().getValidStates().stream();
    }).collect(Collectors.toList());
    private static final int GRID_WIDTH = MathHelper.ceil(MathHelper.sqrt((float)ALL_VALID_STATES.size()));
    private static final int GRID_HEIGHT = MathHelper.ceil((float)ALL_VALID_STATES.size() / (float)GRID_WIDTH);
    protected static final BlockState AIR = Blocks.AIR.getDefaultState();
    protected static final BlockState BARRIER = Blocks.BARRIER.getDefaultState();
    private final Registry<Biome> field_242726_j;

    public DebugChunkGenerator(Registry<Biome> p_i241974_1_)
    {
        super(new SingleBiomeProvider(p_i241974_1_.getOrThrow(Biomes.PLAINS)), new DimensionStructuresSettings(false));
        this.field_242726_j = p_i241974_1_;
    }

    public Registry<Biome> func_242727_g()
    {
        return this.field_242726_j;
    }

    protected Codec <? extends ChunkGenerator > func_230347_a_()
    {
        return field_236066_e_;
    }

    public ChunkGenerator func_230349_a_(long p_230349_1_)
    {
        return this;
    }

    /**
     * Generate the SURFACE part of a chunk
     */
    public void generateSurface(WorldGenRegion p_225551_1_, IChunk p_225551_2_)
    {
    }

    public void func_230350_a_(long p_230350_1_, BiomeManager p_230350_3_, IChunk p_230350_4_, GenerationStage.Carving p_230350_5_)
    {
    }

    public void func_230351_a_(WorldGenRegion p_230351_1_, StructureManager p_230351_2_)
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        int i = p_230351_1_.getMainChunkX();
        int j = p_230351_1_.getMainChunkZ();

        for (int k = 0; k < 16; ++k)
        {
            for (int l = 0; l < 16; ++l)
            {
                int i1 = (i << 4) + k;
                int j1 = (j << 4) + l;
                p_230351_1_.setBlockState(blockpos$mutable.setPos(i1, 60, j1), BARRIER, 2);
                BlockState blockstate = getBlockStateFor(i1, j1);

                if (blockstate != null)
                {
                    p_230351_1_.setBlockState(blockpos$mutable.setPos(i1, 70, j1), blockstate, 2);
                }
            }
        }
    }

    public void func_230352_b_(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_)
    {
    }

    public int getHeight(int x, int z, Heightmap.Type heightmapType)
    {
        return 0;
    }

    public IBlockReader func_230348_a_(int p_230348_1_, int p_230348_2_)
    {
        return new Blockreader(new BlockState[0]);
    }

    public static BlockState getBlockStateFor(int p_177461_0_, int p_177461_1_)
    {
        BlockState blockstate = AIR;

        if (p_177461_0_ > 0 && p_177461_1_ > 0 && p_177461_0_ % 2 != 0 && p_177461_1_ % 2 != 0)
        {
            p_177461_0_ = p_177461_0_ / 2;
            p_177461_1_ = p_177461_1_ / 2;

            if (p_177461_0_ <= GRID_WIDTH && p_177461_1_ <= GRID_HEIGHT)
            {
                int i = MathHelper.abs(p_177461_0_ * GRID_WIDTH + p_177461_1_);

                if (i < ALL_VALID_STATES.size())
                {
                    blockstate = ALL_VALID_STATES.get(i);
                }
            }
        }

        return blockstate;
    }
}
