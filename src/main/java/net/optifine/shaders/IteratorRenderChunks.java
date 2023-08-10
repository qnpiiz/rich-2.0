package net.optifine.shaders;

import java.util.Iterator;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.math.BlockPos;
import net.optifine.BlockPosM;

public class IteratorRenderChunks implements Iterator<ChunkRenderDispatcher.ChunkRender>
{
    private ViewFrustum viewFrustum;
    private Iterator3d Iterator3d;
    private BlockPosM posBlock = new BlockPosM(0, 0, 0);

    public IteratorRenderChunks(ViewFrustum viewFrustum, BlockPos posStart, BlockPos posEnd, int width, int height)
    {
        this.viewFrustum = viewFrustum;
        this.Iterator3d = new Iterator3d(posStart, posEnd, width, height);
    }

    public boolean hasNext()
    {
        return this.Iterator3d.hasNext();
    }

    public ChunkRenderDispatcher.ChunkRender next()
    {
        BlockPos blockpos = this.Iterator3d.next();
        this.posBlock.setXyz(blockpos.getX() << 4, blockpos.getY() << 4, blockpos.getZ() << 4);
        return this.viewFrustum.getRenderChunk(this.posBlock);
    }

    public void remove()
    {
        throw new RuntimeException("Not implemented");
    }
}
