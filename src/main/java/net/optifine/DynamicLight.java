package net.optifine;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class DynamicLight
{
    private Entity entity = null;
    private double offsetY = 0.0D;
    private double lastPosX = (double) - 2.14748365E9F;
    private double lastPosY = (double) - 2.14748365E9F;
    private double lastPosZ = (double) - 2.14748365E9F;
    private int lastLightLevel = 0;
    private long timeCheckMs = 0L;
    private Set<BlockPos> setLitChunkPos = new HashSet<>();
    private BlockPos.Mutable blockPosMutable = new BlockPos.Mutable();

    public DynamicLight(Entity entity)
    {
        this.entity = entity;
        this.offsetY = (double)entity.getEyeHeight();
    }

    public void update(WorldRenderer renderGlobal)
    {
        if (Config.isDynamicLightsFast())
        {
            long i = System.currentTimeMillis();

            if (i < this.timeCheckMs + 500L)
            {
                return;
            }

            this.timeCheckMs = i;
        }

        double d6 = this.entity.getPosX() - 0.5D;
        double d0 = this.entity.getPosY() - 0.5D + this.offsetY;
        double d1 = this.entity.getPosZ() - 0.5D;
        int j = DynamicLights.getLightLevel(this.entity);
        double d2 = d6 - this.lastPosX;
        double d3 = d0 - this.lastPosY;
        double d4 = d1 - this.lastPosZ;
        double d5 = 0.1D;

        if (!(Math.abs(d2) <= d5) || !(Math.abs(d3) <= d5) || !(Math.abs(d4) <= d5) || this.lastLightLevel != j)
        {
            this.lastPosX = d6;
            this.lastPosY = d0;
            this.lastPosZ = d1;
            this.lastLightLevel = j;
            Set<BlockPos> set = new HashSet<>();

            if (j > 0)
            {
                Direction direction = (MathHelper.floor(d6) & 15) >= 8 ? Direction.EAST : Direction.WEST;
                Direction direction1 = (MathHelper.floor(d0) & 15) >= 8 ? Direction.UP : Direction.DOWN;
                Direction direction2 = (MathHelper.floor(d1) & 15) >= 8 ? Direction.SOUTH : Direction.NORTH;
                BlockPos blockpos = new BlockPos(d6, d0, d1);
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = renderGlobal.getRenderChunk(blockpos);
                BlockPos blockpos1 = this.getChunkPos(chunkrenderdispatcher$chunkrender, blockpos, direction);
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender1 = renderGlobal.getRenderChunk(blockpos1);
                BlockPos blockpos2 = this.getChunkPos(chunkrenderdispatcher$chunkrender, blockpos, direction2);
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender2 = renderGlobal.getRenderChunk(blockpos2);
                BlockPos blockpos3 = this.getChunkPos(chunkrenderdispatcher$chunkrender1, blockpos1, direction2);
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender3 = renderGlobal.getRenderChunk(blockpos3);
                BlockPos blockpos4 = this.getChunkPos(chunkrenderdispatcher$chunkrender, blockpos, direction1);
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender4 = renderGlobal.getRenderChunk(blockpos4);
                BlockPos blockpos5 = this.getChunkPos(chunkrenderdispatcher$chunkrender4, blockpos4, direction);
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender5 = renderGlobal.getRenderChunk(blockpos5);
                BlockPos blockpos6 = this.getChunkPos(chunkrenderdispatcher$chunkrender4, blockpos4, direction2);
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender6 = renderGlobal.getRenderChunk(blockpos6);
                BlockPos blockpos7 = this.getChunkPos(chunkrenderdispatcher$chunkrender5, blockpos5, direction2);
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender7 = renderGlobal.getRenderChunk(blockpos7);
                this.updateChunkLight(chunkrenderdispatcher$chunkrender, this.setLitChunkPos, set);
                this.updateChunkLight(chunkrenderdispatcher$chunkrender1, this.setLitChunkPos, set);
                this.updateChunkLight(chunkrenderdispatcher$chunkrender2, this.setLitChunkPos, set);
                this.updateChunkLight(chunkrenderdispatcher$chunkrender3, this.setLitChunkPos, set);
                this.updateChunkLight(chunkrenderdispatcher$chunkrender4, this.setLitChunkPos, set);
                this.updateChunkLight(chunkrenderdispatcher$chunkrender5, this.setLitChunkPos, set);
                this.updateChunkLight(chunkrenderdispatcher$chunkrender6, this.setLitChunkPos, set);
                this.updateChunkLight(chunkrenderdispatcher$chunkrender7, this.setLitChunkPos, set);
            }

            this.updateLitChunks(renderGlobal);
            this.setLitChunkPos = set;
        }
    }

    private BlockPos getChunkPos(ChunkRenderDispatcher.ChunkRender renderChunk, BlockPos pos, Direction facing)
    {
        return renderChunk != null ? renderChunk.getBlockPosOffset16(facing) : pos.offset(facing, 16);
    }

    private void updateChunkLight(ChunkRenderDispatcher.ChunkRender renderChunk, Set<BlockPos> setPrevPos, Set<BlockPos> setNewPos)
    {
        if (renderChunk != null)
        {
            ChunkRenderDispatcher.CompiledChunk chunkrenderdispatcher$compiledchunk = renderChunk.getCompiledChunk();

            if (chunkrenderdispatcher$compiledchunk != null && !chunkrenderdispatcher$compiledchunk.isEmpty())
            {
                renderChunk.setNeedsUpdate(false);
            }

            BlockPos blockpos = renderChunk.getPosition().toImmutable();

            if (setPrevPos != null)
            {
                setPrevPos.remove(blockpos);
            }

            if (setNewPos != null)
            {
                setNewPos.add(blockpos);
            }
        }
    }

    public void updateLitChunks(WorldRenderer renderGlobal)
    {
        for (BlockPos blockpos : this.setLitChunkPos)
        {
            ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = renderGlobal.getRenderChunk(blockpos);
            this.updateChunkLight(chunkrenderdispatcher$chunkrender, (Set<BlockPos>)null, (Set<BlockPos>)null);
        }
    }

    public Entity getEntity()
    {
        return this.entity;
    }

    public double getLastPosX()
    {
        return this.lastPosX;
    }

    public double getLastPosY()
    {
        return this.lastPosY;
    }

    public double getLastPosZ()
    {
        return this.lastPosZ;
    }

    public int getLastLightLevel()
    {
        return this.lastLightLevel;
    }

    public double getOffsetY()
    {
        return this.offsetY;
    }

    public String toString()
    {
        return "Entity: " + this.entity + ", offsetY: " + this.offsetY;
    }
}
