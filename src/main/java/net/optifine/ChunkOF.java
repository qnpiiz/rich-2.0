package net.optifine;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

public class ChunkOF extends Chunk
{
    private ChunkDataOF chunkDataOF;
    private boolean hasEntitiesOF;
    private boolean loadedOF;

    public ChunkOF(World worldIn, ChunkPos chunkPosIn, BiomeContainer biomeContainerIn)
    {
        super(worldIn, chunkPosIn, biomeContainerIn);
    }

    public ChunkDataOF getChunkDataOF()
    {
        return this.chunkDataOF;
    }

    public void setChunkDataOF(ChunkDataOF chunkDataOF)
    {
        this.chunkDataOF = chunkDataOF;
    }

    public static ChunkDataOF makeChunkDataOF(Chunk chunkIn)
    {
        ChunkSectionDataOF[] achunksectiondataof = null;
        ChunkSection chunksection = chunkIn.getLastExtendedBlockStorage();

        if (chunksection != null)
        {
            int i = (chunksection.getYLocation() >> 4) + 1;
            achunksectiondataof = new ChunkSectionDataOF[i];
            ChunkSection[] achunksection = chunkIn.getSections();

            for (int j = 0; j < i; ++j)
            {
                ChunkSection chunksection1 = achunksection[j];

                if (chunksection1 != null)
                {
                    short short1 = chunksection1.getBlockRefCount();
                    short short2 = chunksection1.getTickRefCount();
                    short short3 = chunksection1.getFluidRefCount();
                    achunksectiondataof[j] = new ChunkSectionDataOF(short1, short2, short3);
                }
            }
        }

        return new ChunkDataOF(achunksectiondataof);
    }

    /**
     * Adds an entity to the chunk.
     */
    public void addEntity(Entity entityIn)
    {
        this.hasEntitiesOF = true;
        super.addEntity(entityIn);
    }

    public void setHasEntities(boolean hasEntitiesIn)
    {
        this.hasEntitiesOF = hasEntitiesIn;
        super.setHasEntities(hasEntitiesIn);
    }

    public boolean hasEntities()
    {
        return this.hasEntitiesOF;
    }

    public void setLoaded(boolean loaded)
    {
        this.loadedOF = loaded;
        super.setLoaded(loaded);
    }

    public boolean isLoaded()
    {
        return this.loadedOF;
    }
}
