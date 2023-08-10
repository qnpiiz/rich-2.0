package net.optifine.shaders;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class ShadowUtils
{
    public static Iterator<ChunkRenderDispatcher.ChunkRender> makeShadowChunkIterator(ClientWorld world, double partialTicks, Entity viewEntity, int renderDistanceChunks, ViewFrustum viewFrustum)
    {
        float f = Shaders.getShadowRenderDistance();

        if (!(f <= 0.0F) && !(f >= (float)((renderDistanceChunks - 1) * 16)))
        {
            int i = MathHelper.ceil(f / 16.0F) + 1;
            float f6 = world.getCelestialAngleRadians((float)partialTicks);
            float f1 = Shaders.sunPathRotation * MathHelper.deg2Rad;
            float f2 = f6 > MathHelper.PId2 && f6 < 3.0F * MathHelper.PId2 ? f6 + MathHelper.PI : f6;
            float f3 = -MathHelper.sin(f2);
            float f4 = MathHelper.cos(f2) * MathHelper.cos(f1);
            float f5 = -MathHelper.cos(f2) * MathHelper.sin(f1);
            BlockPos blockpos = new BlockPos(MathHelper.floor(viewEntity.getPosX()) >> 4, MathHelper.floor(viewEntity.getPosY()) >> 4, MathHelper.floor(viewEntity.getPosZ()) >> 4);
            BlockPos blockpos1 = blockpos.add((double)(-f3 * (float)i), (double)(-f4 * (float)i), (double)(-f5 * (float)i));
            BlockPos blockpos2 = blockpos.add((double)(f3 * (float)renderDistanceChunks), (double)(f4 * (float)renderDistanceChunks), (double)(f5 * (float)renderDistanceChunks));
            return new IteratorRenderChunks(viewFrustum, blockpos1, blockpos2, i, i);
        }
        else
        {
            List<ChunkRenderDispatcher.ChunkRender> list = Arrays.asList(viewFrustum.renderChunks);
            return list.iterator();
        }
    }
}
