package net.optifine.override;

import java.util.Arrays;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;
import net.optifine.BlockPosM;
import net.optifine.render.RenderEnv;
import net.optifine.util.ArrayCache;

public class ChunkCacheOF implements IBlockDisplayReader
{
    private final ChunkRenderCache chunkCache;
    private final int posX;
    private final int posY;
    private final int posZ;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private final int sizeXZ;
    private int[] combinedLights;
    private BlockState[] blockStates;
    private Biome[] biomes;
    private final int arraySize;
    private RenderEnv renderEnv;
    private static final ArrayCache cacheCombinedLights = new ArrayCache(Integer.TYPE, 16);
    private static final ArrayCache cacheBlockStates = new ArrayCache(BlockState.class, 16);
    private static final ArrayCache cacheBiomes = new ArrayCache(Biome.class, 16);

    public ChunkCacheOF(ChunkRenderCache chunkCache, BlockPos posFromIn, BlockPos posToIn, int subIn)
    {
        this.chunkCache = chunkCache;
        int i = posFromIn.getX() - subIn >> 4;
        int j = posFromIn.getY() - subIn >> 4;
        int k = posFromIn.getZ() - subIn >> 4;
        int l = posToIn.getX() + subIn >> 4;
        int i1 = posToIn.getY() + subIn >> 4;
        int j1 = posToIn.getZ() + subIn >> 4;
        this.sizeX = l - i + 1 << 4;
        this.sizeY = i1 - j + 1 << 4;
        this.sizeZ = j1 - k + 1 << 4;
        this.sizeXZ = this.sizeX * this.sizeZ;
        this.arraySize = this.sizeX * this.sizeY * this.sizeZ;
        this.posX = i << 4;
        this.posY = j << 4;
        this.posZ = k << 4;
    }

    public int getPositionIndex(BlockPos pos)
    {
        int i = pos.getX() - this.posX;

        if (i >= 0 && i < this.sizeX)
        {
            int j = pos.getY() - this.posY;

            if (j >= 0 && j < this.sizeY)
            {
                int k = pos.getZ() - this.posZ;
                return k >= 0 && k < this.sizeZ ? j * this.sizeXZ + k * this.sizeX + i : -1;
            }
            else
            {
                return -1;
            }
        }
        else
        {
            return -1;
        }
    }

    public int getLightFor(LightType type, BlockPos pos)
    {
        return this.chunkCache.getLightFor(type, pos);
    }

    public BlockState getBlockState(BlockPos pos)
    {
        int i = this.getPositionIndex(pos);

        if (i >= 0 && i < this.arraySize && this.blockStates != null)
        {
            BlockState blockstate = this.blockStates[i];

            if (blockstate == null)
            {
                blockstate = this.chunkCache.getBlockState(pos);
                this.blockStates[i] = blockstate;
            }

            return blockstate;
        }
        else
        {
            return this.chunkCache.getBlockState(pos);
        }
    }

    public void renderStart()
    {
        if (this.combinedLights == null)
        {
            this.combinedLights = (int[])cacheCombinedLights.allocate(this.arraySize);
        }

        if (this.blockStates == null)
        {
            this.blockStates = (BlockState[])cacheBlockStates.allocate(this.arraySize);
        }

        if (this.biomes == null)
        {
            this.biomes = (Biome[])cacheBiomes.allocate(this.arraySize);
        }

        Arrays.fill(this.combinedLights, -1);
        Arrays.fill(this.blockStates, (Object)null);
        Arrays.fill(this.biomes, (Object)null);
        this.loadBlockStates();
    }

    private void loadBlockStates()
    {
        if (this.sizeX == 48 && this.sizeY == 48 && this.sizeZ == 48)
        {
            Chunk chunk = this.chunkCache.getChunk(1, 1);
            BlockPosM blockposm = new BlockPosM();

            for (int i = 16; i < 32; ++i)
            {
                int j = i * this.sizeXZ;

                for (int k = 16; k < 32; ++k)
                {
                    int l = k * this.sizeX;

                    for (int i1 = 16; i1 < 32; ++i1)
                    {
                        blockposm.setXyz(this.posX + i1, this.posY + i, this.posZ + k);
                        int j1 = j + l + i1;
                        BlockState blockstate = chunk.getBlockState(blockposm);
                        this.blockStates[j1] = blockstate;
                    }
                }
            }
        }
    }

    public void renderFinish()
    {
        cacheCombinedLights.free(this.combinedLights);
        this.combinedLights = null;
        cacheBlockStates.free(this.blockStates);
        this.blockStates = null;
        cacheBiomes.free(this.biomes);
        this.biomes = null;
    }

    public int[] getCombinedLights()
    {
        return this.combinedLights;
    }

    public Biome getBiome(BlockPos pos)
    {
        int i = this.getPositionIndex(pos);

        if (i >= 0 && i < this.arraySize && this.biomes != null)
        {
            Biome biome = this.biomes[i];

            if (biome == null)
            {
                biome = this.chunkCache.getBiome(pos);
                this.biomes[i] = biome;
            }

            return biome;
        }
        else
        {
            return this.chunkCache.getBiome(pos);
        }
    }

    public TileEntity getTileEntity(BlockPos pos)
    {
        return this.chunkCache.getTileEntity(pos, Chunk.CreateEntityType.CHECK);
    }

    public TileEntity getTileEntity(BlockPos pos, Chunk.CreateEntityType type)
    {
        return this.chunkCache.getTileEntity(pos, type);
    }

    public boolean canSeeSky(BlockPos pos)
    {
        return this.chunkCache.canSeeSky(pos);
    }

    public FluidState getFluidState(BlockPos pos)
    {
        return this.getBlockState(pos).getFluidState();
    }

    public int getBlockColor(BlockPos blockPosIn, ColorResolver colorResolverIn)
    {
        return this.chunkCache.getBlockColor(blockPosIn, colorResolverIn);
    }

    public WorldLightManager getLightManager()
    {
        return this.chunkCache.getLightManager();
    }

    public RenderEnv getRenderEnv()
    {
        return this.renderEnv;
    }

    public void setRenderEnv(RenderEnv renderEnv)
    {
        this.renderEnv = renderEnv;
    }

    public float func_230487_a_(Direction directionIn, boolean shadeIn)
    {
        return this.chunkCache.func_230487_a_(directionIn, shadeIn);
    }
}
