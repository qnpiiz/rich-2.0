package net.minecraft.block.pattern;

import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IWorldReader;

public class BlockPattern
{
    private final Predicate<CachedBlockInfo>[][][] blockMatches;
    private final int fingerLength;
    private final int thumbLength;
    private final int palmLength;

    public BlockPattern(Predicate<CachedBlockInfo>[][][] predicates)
    {
        this.blockMatches = predicates;
        this.fingerLength = predicates.length;

        if (this.fingerLength > 0)
        {
            this.thumbLength = predicates[0].length;

            if (this.thumbLength > 0)
            {
                this.palmLength = predicates[0][0].length;
            }
            else
            {
                this.palmLength = 0;
            }
        }
        else
        {
            this.thumbLength = 0;
            this.palmLength = 0;
        }
    }

    public int getFingerLength()
    {
        return this.fingerLength;
    }

    public int getThumbLength()
    {
        return this.thumbLength;
    }

    public int getPalmLength()
    {
        return this.palmLength;
    }

    @Nullable

    /**
     * checks that the given pattern & rotation is at the block co-ordinates.
     */
    private BlockPattern.PatternHelper checkPatternAt(BlockPos pos, Direction finger, Direction thumb, LoadingCache<BlockPos, CachedBlockInfo> lcache)
    {
        for (int i = 0; i < this.palmLength; ++i)
        {
            for (int j = 0; j < this.thumbLength; ++j)
            {
                for (int k = 0; k < this.fingerLength; ++k)
                {
                    if (!this.blockMatches[k][j][i].test(lcache.getUnchecked(translateOffset(pos, finger, thumb, i, j, k))))
                    {
                        return null;
                    }
                }
            }
        }

        return new BlockPattern.PatternHelper(pos, finger, thumb, lcache, this.palmLength, this.thumbLength, this.fingerLength);
    }

    @Nullable

    /**
     * Calculates whether the given world position matches the pattern. Warning, fairly heavy function. @return a
     * BlockPattern.PatternHelper if found, null otherwise.
     */
    public BlockPattern.PatternHelper match(IWorldReader worldIn, BlockPos pos)
    {
        LoadingCache<BlockPos, CachedBlockInfo> loadingcache = createLoadingCache(worldIn, false);
        int i = Math.max(Math.max(this.palmLength, this.thumbLength), this.fingerLength);

        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos, pos.add(i - 1, i - 1, i - 1)))
        {
            for (Direction direction : Direction.values())
            {
                for (Direction direction1 : Direction.values())
                {
                    if (direction1 != direction && direction1 != direction.getOpposite())
                    {
                        BlockPattern.PatternHelper blockpattern$patternhelper = this.checkPatternAt(blockpos, direction, direction1, loadingcache);

                        if (blockpattern$patternhelper != null)
                        {
                            return blockpattern$patternhelper;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static LoadingCache<BlockPos, CachedBlockInfo> createLoadingCache(IWorldReader worldIn, boolean forceLoadIn)
    {
        return CacheBuilder.newBuilder().build(new BlockPattern.CacheLoader(worldIn, forceLoadIn));
    }

    /**
     * Offsets the position of pos in the direction of finger and thumb facing by offset amounts, follows the right-hand
     * rule for cross products (finger, thumb, palm) @return A new BlockPos offset in the facing directions
     */
    protected static BlockPos translateOffset(BlockPos pos, Direction finger, Direction thumb, int palmOffset, int thumbOffset, int fingerOffset)
    {
        if (finger != thumb && finger != thumb.getOpposite())
        {
            Vector3i vector3i = new Vector3i(finger.getXOffset(), finger.getYOffset(), finger.getZOffset());
            Vector3i vector3i1 = new Vector3i(thumb.getXOffset(), thumb.getYOffset(), thumb.getZOffset());
            Vector3i vector3i2 = vector3i.crossProduct(vector3i1);
            return pos.add(vector3i1.getX() * -thumbOffset + vector3i2.getX() * palmOffset + vector3i.getX() * fingerOffset, vector3i1.getY() * -thumbOffset + vector3i2.getY() * palmOffset + vector3i.getY() * fingerOffset, vector3i1.getZ() * -thumbOffset + vector3i2.getZ() * palmOffset + vector3i.getZ() * fingerOffset);
        }
        else
        {
            throw new IllegalArgumentException("Invalid forwards & up combination");
        }
    }

    static class CacheLoader extends com.google.common.cache.CacheLoader<BlockPos, CachedBlockInfo>
    {
        private final IWorldReader world;
        private final boolean forceLoad;

        public CacheLoader(IWorldReader worldIn, boolean forceLoadIn)
        {
            this.world = worldIn;
            this.forceLoad = forceLoadIn;
        }

        public CachedBlockInfo load(BlockPos p_load_1_) throws Exception
        {
            return new CachedBlockInfo(this.world, p_load_1_, this.forceLoad);
        }
    }

    public static class PatternHelper
    {
        private final BlockPos frontTopLeft;
        private final Direction forwards;
        private final Direction up;
        private final LoadingCache<BlockPos, CachedBlockInfo> lcache;
        private final int width;
        private final int height;
        private final int depth;

        public PatternHelper(BlockPos posIn, Direction fingerIn, Direction thumbIn, LoadingCache<BlockPos, CachedBlockInfo> lcacheIn, int widthIn, int heightIn, int depthIn)
        {
            this.frontTopLeft = posIn;
            this.forwards = fingerIn;
            this.up = thumbIn;
            this.lcache = lcacheIn;
            this.width = widthIn;
            this.height = heightIn;
            this.depth = depthIn;
        }

        public BlockPos getFrontTopLeft()
        {
            return this.frontTopLeft;
        }

        public Direction getForwards()
        {
            return this.forwards;
        }

        public Direction getUp()
        {
            return this.up;
        }

        public CachedBlockInfo translateOffset(int palmOffset, int thumbOffset, int fingerOffset)
        {
            return this.lcache.getUnchecked(BlockPattern.translateOffset(this.frontTopLeft, this.getForwards(), this.getUp(), palmOffset, thumbOffset, fingerOffset));
        }

        public String toString()
        {
            return MoreObjects.toStringHelper(this).add("up", this.up).add("forwards", this.forwards).add("frontTopLeft", this.frontTopLeft).toString();
        }
    }
}
