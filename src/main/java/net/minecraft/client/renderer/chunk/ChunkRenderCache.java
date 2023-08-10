package net.minecraft.client.renderer.chunk;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;

public class ChunkRenderCache implements IBlockDisplayReader
{
    protected final int chunkStartX;
    protected final int chunkStartZ;
    protected final BlockPos cacheStartPos;
    protected final int cacheSizeX;
    protected final int cacheSizeY;
    protected final int cacheSizeZ;
    protected final Chunk[][] chunks;
    protected final BlockState[] blockStates;
    protected final FluidState[] fluidStates;
    protected final World world;

    @Nullable

    /**
     * generates a RenderChunkCache, but returns null if the chunk is empty (contains only air)
     */
    public static ChunkRenderCache generateCache(World worldIn, BlockPos from, BlockPos to, int padding)
    {
        return generateCache(worldIn, from, to, padding, true);
    }

    public static ChunkRenderCache generateCache(World p_generateCache_0_, BlockPos p_generateCache_1_, BlockPos p_generateCache_2_, int p_generateCache_3_, boolean p_generateCache_4_)
    {
        int i = p_generateCache_1_.getX() - p_generateCache_3_ >> 4;
        int j = p_generateCache_1_.getZ() - p_generateCache_3_ >> 4;
        int k = p_generateCache_2_.getX() + p_generateCache_3_ >> 4;
        int l = p_generateCache_2_.getZ() + p_generateCache_3_ >> 4;
        Chunk[][] achunk = new Chunk[k - i + 1][l - j + 1];

        for (int i1 = i; i1 <= k; ++i1)
        {
            for (int j1 = j; j1 <= l; ++j1)
            {
                achunk[i1 - i][j1 - j] = p_generateCache_0_.getChunk(i1, j1);
            }
        }

        if (p_generateCache_4_ && func_241718_a_(p_generateCache_1_, p_generateCache_2_, i, j, achunk))
        {
            return null;
        }
        else
        {
            int k1 = 1;
            BlockPos blockpos1 = p_generateCache_1_.add(-1, -1, -1);
            BlockPos blockpos = p_generateCache_2_.add(1, 1, 1);
            return new ChunkRenderCache(p_generateCache_0_, i, j, achunk, blockpos1, blockpos);
        }
    }

    public static boolean func_241718_a_(BlockPos p_241718_0_, BlockPos p_241718_1_, int p_241718_2_, int p_241718_3_, Chunk[][] p_241718_4_)
    {
        for (int i = p_241718_0_.getX() >> 4; i <= p_241718_1_.getX() >> 4; ++i)
        {
            for (int j = p_241718_0_.getZ() >> 4; j <= p_241718_1_.getZ() >> 4; ++j)
            {
                Chunk chunk = p_241718_4_[i - p_241718_2_][j - p_241718_3_];

                if (!chunk.isEmptyBetween(p_241718_0_.getY(), p_241718_1_.getY()))
                {
                    return false;
                }
            }
        }

        return true;
    }

    public ChunkRenderCache(World worldIn, int chunkStartXIn, int chunkStartZIn, Chunk[][] chunksIn, BlockPos startPos, BlockPos endPos)
    {
        this.world = worldIn;
        this.chunkStartX = chunkStartXIn;
        this.chunkStartZ = chunkStartZIn;
        this.chunks = chunksIn;
        this.cacheStartPos = startPos;
        this.cacheSizeX = endPos.getX() - startPos.getX() + 1;
        this.cacheSizeY = endPos.getY() - startPos.getY() + 1;
        this.cacheSizeZ = endPos.getZ() - startPos.getZ() + 1;
        this.blockStates = null;
        this.fluidStates = null;
    }

    protected final int getIndex(BlockPos pos)
    {
        return this.getIndex(pos.getX(), pos.getY(), pos.getZ());
    }

    protected int getIndex(int xIn, int yIn, int zIn)
    {
        int i = xIn - this.cacheStartPos.getX();
        int j = yIn - this.cacheStartPos.getY();
        int k = zIn - this.cacheStartPos.getZ();
        return k * this.cacheSizeX * this.cacheSizeY + j * this.cacheSizeX + i;
    }

    public BlockState getBlockState(BlockPos pos)
    {
        int i = (pos.getX() >> 4) - this.chunkStartX;
        int j = (pos.getZ() >> 4) - this.chunkStartZ;
        return this.chunks[i][j].getBlockState(pos);
    }

    public FluidState getFluidState(BlockPos pos)
    {
        int i = (pos.getX() >> 4) - this.chunkStartX;
        int j = (pos.getZ() >> 4) - this.chunkStartZ;
        return this.chunks[i][j].getFluidState(pos);
    }

    public float func_230487_a_(Direction p_230487_1_, boolean p_230487_2_)
    {
        return this.world.func_230487_a_(p_230487_1_, p_230487_2_);
    }

    public WorldLightManager getLightManager()
    {
        return this.world.getLightManager();
    }

    @Nullable
    public TileEntity getTileEntity(BlockPos pos)
    {
        return this.getTileEntity(pos, Chunk.CreateEntityType.IMMEDIATE);
    }

    @Nullable
    public TileEntity getTileEntity(BlockPos pos, Chunk.CreateEntityType creationType)
    {
        int i = (pos.getX() >> 4) - this.chunkStartX;
        int j = (pos.getZ() >> 4) - this.chunkStartZ;
        return this.chunks[i][j].getTileEntity(pos, creationType);
    }

    public int getBlockColor(BlockPos blockPosIn, ColorResolver colorResolverIn)
    {
        return this.world.getBlockColor(blockPosIn, colorResolverIn);
    }

    public Biome getBiome(BlockPos p_getBiome_1_)
    {
        return this.world.getBiome(p_getBiome_1_);
    }

    public Chunk getChunk(int p_getChunk_1_, int p_getChunk_2_)
    {
        return this.chunks[p_getChunk_1_][p_getChunk_2_];
    }
}
