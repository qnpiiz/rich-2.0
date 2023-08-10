package net.minecraft.util;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntStack;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;

public class TeleportationRepositioner
{
    /**
     * Finds the rectangle with the largest area containing centerPos within the blocks specified by the predicate
     */
    public static TeleportationRepositioner.Result findLargestRectangle(BlockPos centerPos, Direction.Axis axis1, int max1, Direction.Axis axis2, int max2, Predicate<BlockPos> posPredicate)
    {
        BlockPos.Mutable blockpos$mutable = centerPos.toMutable();
        Direction direction = Direction.getFacingFromAxis(Direction.AxisDirection.NEGATIVE, axis1);
        Direction direction1 = direction.getOpposite();
        Direction direction2 = Direction.getFacingFromAxis(Direction.AxisDirection.NEGATIVE, axis2);
        Direction direction3 = direction2.getOpposite();
        int i = distanceInDirection(posPredicate, blockpos$mutable.setPos(centerPos), direction, max1);
        int j = distanceInDirection(posPredicate, blockpos$mutable.setPos(centerPos), direction1, max1);
        int k = i;
        TeleportationRepositioner.IntBounds[] ateleportationrepositioner$intbounds = new TeleportationRepositioner.IntBounds[i + 1 + j];
        ateleportationrepositioner$intbounds[i] = new TeleportationRepositioner.IntBounds(distanceInDirection(posPredicate, blockpos$mutable.setPos(centerPos), direction2, max2), distanceInDirection(posPredicate, blockpos$mutable.setPos(centerPos), direction3, max2));
        int l = ateleportationrepositioner$intbounds[i].min;

        for (int i1 = 1; i1 <= i; ++i1)
        {
            TeleportationRepositioner.IntBounds teleportationrepositioner$intbounds = ateleportationrepositioner$intbounds[k - (i1 - 1)];
            ateleportationrepositioner$intbounds[k - i1] = new TeleportationRepositioner.IntBounds(distanceInDirection(posPredicate, blockpos$mutable.setPos(centerPos).move(direction, i1), direction2, teleportationrepositioner$intbounds.min), distanceInDirection(posPredicate, blockpos$mutable.setPos(centerPos).move(direction, i1), direction3, teleportationrepositioner$intbounds.max));
        }

        for (int l2 = 1; l2 <= j; ++l2)
        {
            TeleportationRepositioner.IntBounds teleportationrepositioner$intbounds2 = ateleportationrepositioner$intbounds[k + l2 - 1];
            ateleportationrepositioner$intbounds[k + l2] = new TeleportationRepositioner.IntBounds(distanceInDirection(posPredicate, blockpos$mutable.setPos(centerPos).move(direction1, l2), direction2, teleportationrepositioner$intbounds2.min), distanceInDirection(posPredicate, blockpos$mutable.setPos(centerPos).move(direction1, l2), direction3, teleportationrepositioner$intbounds2.max));
        }

        int i3 = 0;
        int j3 = 0;
        int j1 = 0;
        int k1 = 0;
        int[] aint = new int[ateleportationrepositioner$intbounds.length];

        for (int l1 = l; l1 >= 0; --l1)
        {
            for (int i2 = 0; i2 < ateleportationrepositioner$intbounds.length; ++i2)
            {
                TeleportationRepositioner.IntBounds teleportationrepositioner$intbounds1 = ateleportationrepositioner$intbounds[i2];
                int j2 = l - teleportationrepositioner$intbounds1.min;
                int k2 = l + teleportationrepositioner$intbounds1.max;
                aint[i2] = l1 >= j2 && l1 <= k2 ? k2 + 1 - l1 : 0;
            }

            Pair<TeleportationRepositioner.IntBounds, Integer> pair = largestRectInHeights(aint);
            TeleportationRepositioner.IntBounds teleportationrepositioner$intbounds3 = pair.getFirst();
            int k3 = 1 + teleportationrepositioner$intbounds3.max - teleportationrepositioner$intbounds3.min;
            int l3 = pair.getSecond();

            if (k3 * l3 > j1 * k1)
            {
                i3 = teleportationrepositioner$intbounds3.min;
                j3 = l1;
                j1 = k3;
                k1 = l3;
            }
        }

        return new TeleportationRepositioner.Result(centerPos.func_241872_a(axis1, i3 - k).func_241872_a(axis2, j3 - l), j1, k1);
    }

    /**
     * Finds the distance we can travel in the given direction while the predicate returns true
     */
    private static int distanceInDirection(Predicate<BlockPos> posPredicate, BlockPos.Mutable centerPos, Direction direction, int max)
    {
        int i;

        for (i = 0; i < max && posPredicate.test(centerPos.move(direction)); ++i)
        {
        }

        return i;
    }

    @VisibleForTesting
    static Pair<TeleportationRepositioner.IntBounds, Integer> largestRectInHeights(int[] heights)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        IntStack intstack = new IntArrayList();
        intstack.push(0);

        for (int l = 1; l <= heights.length; ++l)
        {
            int i1 = l == heights.length ? 0 : heights[l];

            while (!intstack.isEmpty())
            {
                int j1 = heights[intstack.topInt()];

                if (i1 >= j1)
                {
                    intstack.push(l);
                    break;
                }

                intstack.popInt();
                int k1 = intstack.isEmpty() ? 0 : intstack.topInt() + 1;

                if (j1 * (l - k1) > k * (j - i))
                {
                    j = l;
                    i = k1;
                    k = j1;
                }
            }

            if (intstack.isEmpty())
            {
                intstack.push(l);
            }
        }

        return new Pair<>(new TeleportationRepositioner.IntBounds(i, j - 1), k);
    }

    public static class IntBounds
    {
        public final int min;
        public final int max;

        public IntBounds(int min, int max)
        {
            this.min = min;
            this.max = max;
        }

        public String toString()
        {
            return "IntBounds{min=" + this.min + ", max=" + this.max + '}';
        }
    }

    public static class Result
    {
        public final BlockPos startPos;
        public final int width;
        public final int height;

        public Result(BlockPos startPos, int width, int height)
        {
            this.startPos = startPos;
            this.width = width;
            this.height = height;
        }
    }
}
