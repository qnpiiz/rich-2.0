package net.optifine.render;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.optifine.Config;

public class ChunkVisibility
{
    public static final int MASK_FACINGS = 63;
    public static final Direction[][] enumFacingArrays = makeEnumFacingArrays(false);
    public static final Direction[][] enumFacingOppositeArrays = makeEnumFacingArrays(true);
    private static int counter = 0;
    private static int iMaxStatic = -1;
    private static int iMaxStaticFinal = 16;
    private static World worldLast = null;
    private static int pcxLast = Integer.MIN_VALUE;
    private static int pczLast = Integer.MIN_VALUE;

    public static int getMaxChunkY(World world, Entity viewEntity, int renderDistanceChunks)
    {
        int i = MathHelper.floor(viewEntity.getPosX()) >> 4;
        int j = MathHelper.floor(viewEntity.getPosY()) >> 4;
        int k = MathHelper.floor(viewEntity.getPosZ()) >> 4;
        j = Config.limit(j, 0, 15);
        Chunk chunk = world.getChunk(i, k);
        int l = i - renderDistanceChunks;
        int i1 = i + renderDistanceChunks;
        int j1 = k - renderDistanceChunks;
        int k1 = k + renderDistanceChunks;

        if (world != worldLast || i != pcxLast || k != pczLast)
        {
            counter = 0;
            iMaxStaticFinal = 16;
            worldLast = world;
            pcxLast = i;
            pczLast = k;
        }

        if (counter == 0)
        {
            iMaxStatic = -1;
        }

        int l1 = iMaxStatic;

        switch (counter)
        {
            case 0:
                i1 = i;
                k1 = k;
                break;

            case 1:
                l = i;
                k1 = k;
                break;

            case 2:
                i1 = i;
                j1 = k;
                break;

            case 3:
                l = i;
                j1 = k;
        }

        for (int i2 = l; i2 < i1; ++i2)
        {
            for (int j2 = j1; j2 < k1; ++j2)
            {
                Chunk chunk1 = world.getChunk(i2, j2);

                if (!chunk1.isEmpty())
                {
                    ChunkSection[] achunksection = chunk1.getSections();

                    for (int k2 = achunksection.length - 1; k2 > l1; --k2)
                    {
                        ChunkSection chunksection = achunksection[k2];

                        if (chunksection != null && !chunksection.isEmpty())
                        {
                            if (k2 > l1)
                            {
                                l1 = k2;
                            }

                            break;
                        }
                    }

                    try
                    {
                        Map<BlockPos, TileEntity> map = chunk1.getTileEntityMap();

                        if (!map.isEmpty())
                        {
                            for (BlockPos blockpos : map.keySet())
                            {
                                int l2 = blockpos.getY() >> 4;

                                if (l2 > l1)
                                {
                                    l1 = l2;
                                }
                            }
                        }
                    }
                    catch (ConcurrentModificationException concurrentmodificationexception)
                    {
                    }

                    ClassInheritanceMultiMap<Entity>[] classinheritancemultimap = chunk1.getEntityLists();

                    for (int i3 = classinheritancemultimap.length - 1; i3 > l1; --i3)
                    {
                        ClassInheritanceMultiMap<Entity> classinheritancemultimap1 = classinheritancemultimap[i3];

                        if (!classinheritancemultimap1.isEmpty() && (chunk1 != chunk || i3 != j || classinheritancemultimap1.size() != 1))
                        {
                            if (i3 > l1)
                            {
                                l1 = i3;
                            }

                            break;
                        }
                    }
                }
            }
        }

        if (counter < 3)
        {
            iMaxStatic = l1;
            l1 = iMaxStaticFinal;
        }
        else
        {
            iMaxStaticFinal = l1;
            iMaxStatic = -1;
        }

        counter = (counter + 1) % 4;
        return l1 << 4;
    }

    public static boolean isFinished()
    {
        return counter == 0;
    }

    private static Direction[][] makeEnumFacingArrays(boolean opposite)
    {
        int i = 64;
        Direction[][] adirection = new Direction[i][];

        for (int j = 0; j < i; ++j)
        {
            List<Direction> list = new ArrayList<>();

            for (int k = 0; k < Direction.VALUES.length; ++k)
            {
                Direction direction = Direction.VALUES[k];
                Direction direction1 = opposite ? direction.getOpposite() : direction;
                int l = 1 << direction1.ordinal();

                if ((j & l) != 0)
                {
                    list.add(direction);
                }
            }

            Direction[] adirection1 = list.toArray(new Direction[list.size()]);
            adirection[j] = adirection1;
        }

        return adirection;
    }

    public static Direction[] getFacingsNotOpposite(int setDisabled)
    {
        int i = ~setDisabled & 63;
        return enumFacingOppositeArrays[i];
    }

    public static void reset()
    {
        worldLast = null;
    }
}
