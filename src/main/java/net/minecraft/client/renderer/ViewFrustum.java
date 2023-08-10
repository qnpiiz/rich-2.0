package net.minecraft.client.renderer;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.optifine.Config;
import net.optifine.render.VboRegion;

public class ViewFrustum
{
    protected final WorldRenderer renderGlobal;
    protected final World world;
    protected int countChunksY;
    protected int countChunksX;
    protected int countChunksZ;
    public ChunkRenderDispatcher.ChunkRender[] renderChunks;
    private Map<ChunkPos, VboRegion[]> mapVboRegions = new HashMap<>();

    public ViewFrustum(ChunkRenderDispatcher renderDispatcherIn, World worldIn, int countChunksIn, WorldRenderer renderGlobalIn)
    {
        this.renderGlobal = renderGlobalIn;
        this.world = worldIn;
        this.setCountChunksXYZ(countChunksIn);
        this.createRenderChunks(renderDispatcherIn);
    }

    protected void createRenderChunks(ChunkRenderDispatcher renderChunkFactory)
    {
        int i = this.countChunksX * this.countChunksY * this.countChunksZ;
        this.renderChunks = new ChunkRenderDispatcher.ChunkRender[i];

        for (int j = 0; j < this.countChunksX; ++j)
        {
            for (int k = 0; k < this.countChunksY; ++k)
            {
                for (int l = 0; l < this.countChunksZ; ++l)
                {
                    int i1 = this.getIndex(j, k, l);
                    this.renderChunks[i1] = renderChunkFactory.new ChunkRender();
                    this.renderChunks[i1].setPosition(j * 16, k * 16, l * 16);

                    if (Config.isVbo() && Config.isRenderRegions())
                    {
                        this.updateVboRegion(this.renderChunks[i1]);
                    }
                }
            }
        }

        for (int j1 = 0; j1 < this.renderChunks.length; ++j1)
        {
            ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender1 = this.renderChunks[j1];

            for (int k1 = 0; k1 < Direction.VALUES.length; ++k1)
            {
                Direction direction = Direction.VALUES[k1];
                BlockPos blockpos = chunkrenderdispatcher$chunkrender1.getBlockPosOffset16(direction);
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = this.getRenderChunk(blockpos);
                chunkrenderdispatcher$chunkrender1.setRenderChunkNeighbour(direction, chunkrenderdispatcher$chunkrender);
            }
        }
    }

    public void deleteGlResources()
    {
        for (ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender : this.renderChunks)
        {
            chunkrenderdispatcher$chunkrender.deleteGlResources();
        }

        this.deleteVboRegions();
    }

    private int getIndex(int x, int y, int z)
    {
        return (z * this.countChunksY + y) * this.countChunksX + x;
    }

    protected void setCountChunksXYZ(int renderDistanceChunks)
    {
        int i = renderDistanceChunks * 2 + 1;
        this.countChunksX = i;
        this.countChunksY = 16;
        this.countChunksZ = i;
    }

    public void updateChunkPositions(double viewEntityX, double viewEntityZ)
    {
        int i = MathHelper.floor(viewEntityX);
        int j = MathHelper.floor(viewEntityZ);

        for (int k = 0; k < this.countChunksX; ++k)
        {
            int l = this.countChunksX * 16;
            int i1 = i - 8 - l / 2;
            int j1 = i1 + Math.floorMod(k * 16 - i1, l);

            for (int k1 = 0; k1 < this.countChunksZ; ++k1)
            {
                int l1 = this.countChunksZ * 16;
                int i2 = j - 8 - l1 / 2;
                int j2 = i2 + Math.floorMod(k1 * 16 - i2, l1);

                for (int k2 = 0; k2 < this.countChunksY; ++k2)
                {
                    int l2 = k2 * 16;
                    ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = this.renderChunks[this.getIndex(k, k2, k1)];
                    chunkrenderdispatcher$chunkrender.setPosition(j1, l2, j2);
                }
            }
        }
    }

    public void markForRerender(int sectionX, int sectionY, int sectionZ, boolean rerenderOnMainThread)
    {
        int i = Math.floorMod(sectionX, this.countChunksX);
        int j = Math.floorMod(sectionY, this.countChunksY);
        int k = Math.floorMod(sectionZ, this.countChunksZ);
        ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = this.renderChunks[this.getIndex(i, j, k)];
        chunkrenderdispatcher$chunkrender.setNeedsUpdate(rerenderOnMainThread);
    }

    @Nullable
    public ChunkRenderDispatcher.ChunkRender getRenderChunk(BlockPos pos)
    {
        int i = pos.getX() >> 4;
        int j = pos.getY() >> 4;
        int k = pos.getZ() >> 4;

        if (j >= 0 && j < this.countChunksY)
        {
            i = MathHelper.normalizeAngle(i, this.countChunksX);
            k = MathHelper.normalizeAngle(k, this.countChunksZ);
            return this.renderChunks[this.getIndex(i, j, k)];
        }
        else
        {
            return null;
        }
    }

    private void updateVboRegion(ChunkRenderDispatcher.ChunkRender p_updateVboRegion_1_)
    {
        BlockPos blockpos = p_updateVboRegion_1_.getPosition();
        int i = blockpos.getX() >> 8 << 8;
        int j = blockpos.getZ() >> 8 << 8;
        ChunkPos chunkpos = new ChunkPos(i, j);
        RenderType[] arendertype = RenderType.CHUNK_RENDER_TYPES;
        VboRegion[] avboregion = this.mapVboRegions.get(chunkpos);

        if (avboregion == null)
        {
            avboregion = new VboRegion[arendertype.length];

            for (int k = 0; k < arendertype.length; ++k)
            {
                avboregion[k] = new VboRegion(arendertype[k]);
            }

            this.mapVboRegions.put(chunkpos, avboregion);
        }

        for (int l = 0; l < arendertype.length; ++l)
        {
            RenderType rendertype = arendertype[l];
            VboRegion vboregion = avboregion[l];

            if (vboregion != null)
            {
                p_updateVboRegion_1_.getVertexBuffer(rendertype).setVboRegion(vboregion);
            }
        }
    }

    public void deleteVboRegions()
    {
        for (ChunkPos chunkpos : this.mapVboRegions.keySet())
        {
            VboRegion[] avboregion = this.mapVboRegions.get(chunkpos);

            for (int i = 0; i < avboregion.length; ++i)
            {
                VboRegion vboregion = avboregion[i];

                if (vboregion != null)
                {
                    vboregion.deleteGlBuffers();
                }

                avboregion[i] = null;
            }
        }

        this.mapVboRegions.clear();
    }
}
