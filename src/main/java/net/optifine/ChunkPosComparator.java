package net.optifine;

import java.util.Comparator;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

public class ChunkPosComparator implements Comparator<ChunkPos>
{
    private int chunkPosX;
    private int chunkPosZ;
    private double yawRad;
    private double pitchNorm;

    public ChunkPosComparator(int chunkPosX, int chunkPosZ, double yawRad, double pitchRad)
    {
        this.chunkPosX = chunkPosX;
        this.chunkPosZ = chunkPosZ;
        this.yawRad = yawRad;
        this.pitchNorm = 1.0D - MathHelper.clamp(Math.abs(pitchRad) / (Math.PI / 2D), 0.0D, 1.0D);
    }

    public int compare(ChunkPos cp1, ChunkPos cp2)
    {
        int i = this.getDistSq(cp1);
        int j = this.getDistSq(cp2);
        return i - j;
    }

    private int getDistSq(ChunkPos cp)
    {
        int i = cp.x - this.chunkPosX;
        int j = cp.z - this.chunkPosZ;
        int k = i * i + j * j;
        double d0 = MathHelper.atan2((double)j, (double)i);
        double d1 = Math.abs(d0 - this.yawRad);

        if (d1 > Math.PI)
        {
            d1 = (Math.PI * 2D) - d1;
        }

        return (int)((double)k * 1000.0D * this.pitchNorm * d1 * d1);
    }
}
