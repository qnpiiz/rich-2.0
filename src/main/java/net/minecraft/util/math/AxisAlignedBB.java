package net.minecraft.util.math;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;

public class AxisAlignedBB
{
    public final double minX;
    public final double minY;
    public final double minZ;
    public final double maxX;
    public final double maxY;
    public final double maxZ;

    public AxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public AxisAlignedBB(BlockPos pos)
    {
        this((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1));
    }

    public AxisAlignedBB(BlockPos pos1, BlockPos pos2)
    {
        this((double)pos1.getX(), (double)pos1.getY(), (double)pos1.getZ(), (double)pos2.getX(), (double)pos2.getY(), (double)pos2.getZ());
    }

    public AxisAlignedBB(Vector3d min, Vector3d max)
    {
        this(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public static AxisAlignedBB toImmutable(MutableBoundingBox mutableBox)
    {
        return new AxisAlignedBB((double)mutableBox.minX, (double)mutableBox.minY, (double)mutableBox.minZ, (double)(mutableBox.maxX + 1), (double)(mutableBox.maxY + 1), (double)(mutableBox.maxZ + 1));
    }

    public static AxisAlignedBB fromVector(Vector3d vector)
    {
        return new AxisAlignedBB(vector.x, vector.y, vector.z, vector.x + 1.0D, vector.y + 1.0D, vector.z + 1.0D);
    }

    public double getMin(Direction.Axis axis)
    {
        return axis.getCoordinate(this.minX, this.minY, this.minZ);
    }

    public double getMax(Direction.Axis axis)
    {
        return axis.getCoordinate(this.maxX, this.maxY, this.maxZ);
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof AxisAlignedBB))
        {
            return false;
        }
        else
        {
            AxisAlignedBB axisalignedbb = (AxisAlignedBB)p_equals_1_;

            if (Double.compare(axisalignedbb.minX, this.minX) != 0)
            {
                return false;
            }
            else if (Double.compare(axisalignedbb.minY, this.minY) != 0)
            {
                return false;
            }
            else if (Double.compare(axisalignedbb.minZ, this.minZ) != 0)
            {
                return false;
            }
            else if (Double.compare(axisalignedbb.maxX, this.maxX) != 0)
            {
                return false;
            }
            else if (Double.compare(axisalignedbb.maxY, this.maxY) != 0)
            {
                return false;
            }
            else
            {
                return Double.compare(axisalignedbb.maxZ, this.maxZ) == 0;
            }
        }
    }

    public int hashCode()
    {
        long i = Double.doubleToLongBits(this.minX);
        int j = (int)(i ^ i >>> 32);
        i = Double.doubleToLongBits(this.minY);
        j = 31 * j + (int)(i ^ i >>> 32);
        i = Double.doubleToLongBits(this.minZ);
        j = 31 * j + (int)(i ^ i >>> 32);
        i = Double.doubleToLongBits(this.maxX);
        j = 31 * j + (int)(i ^ i >>> 32);
        i = Double.doubleToLongBits(this.maxY);
        j = 31 * j + (int)(i ^ i >>> 32);
        i = Double.doubleToLongBits(this.maxZ);
        return 31 * j + (int)(i ^ i >>> 32);
    }

    /**
     * Creates a new {@link AxisAlignedBB} that has been contracted by the given amount, with positive changes
     * decreasing max values and negative changes increasing min values.
     * <br/>
     * If the amount to contract by is larger than the length of a side, then the side will wrap (still creating a valid
     * AABB - see last sample).
     *  
     * <h3>Samples:</h3>
     * <table>
     * <tr><th>Input</th><th>Result</th></tr>
     * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 4, 4, 4).contract(2, 2, 2)</code></pre></td><td><pre><samp>box[0.0,
     * 0.0, 0.0 -> 2.0, 2.0, 2.0]</samp></pre></td></tr>
     * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 4, 4, 4).contract(-2, -2,
     * -2)</code></pre></td><td><pre><samp>box[2.0, 2.0, 2.0 -> 4.0, 4.0, 4.0]</samp></pre></td></tr>
     * <tr><td><pre><code>new AxisAlignedBB(5, 5, 5, 7, 7, 7).contract(0, 1,
     * -1)</code></pre></td><td><pre><samp>box[5.0, 5.0, 6.0 -> 7.0, 6.0, 7.0]</samp></pre></td></tr>
     * <tr><td><pre><code>new AxisAlignedBB(-2, -2, -2, 2, 2, 2).contract(4, -4,
     * 0)</code></pre></td><td><pre><samp>box[-8.0, 2.0, -2.0 -> -2.0, 8.0, 2.0]</samp></pre></td></tr>
     * </table>
     *  
     * <h3>See Also:</h3>
     * <ul>
     * <li>{@link #expand(double, double, double)} - like this, except for expanding.</li>
     * <li>{@link #grow(double, double, double)} and {@link #grow(double)} - expands in all directions.</li>
     * <li>{@link #shrink(double)} - contracts in all directions (like {@link #grow(double)})</li>
     * </ul>
     *  
     * @return A new modified bounding box.
     */
    public AxisAlignedBB contract(double x, double y, double z)
    {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;

        if (x < 0.0D)
        {
            d0 -= x;
        }
        else if (x > 0.0D)
        {
            d3 -= x;
        }

        if (y < 0.0D)
        {
            d1 -= y;
        }
        else if (y > 0.0D)
        {
            d4 -= y;
        }

        if (z < 0.0D)
        {
            d2 -= z;
        }
        else if (z > 0.0D)
        {
            d5 -= z;
        }

        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    public AxisAlignedBB expand(Vector3d vector)
    {
        return this.expand(vector.x, vector.y, vector.z);
    }

    /**
     * Creates a new {@link AxisAlignedBB} that has been expanded by the given amount, with positive changes increasing
     * max values and negative changes decreasing min values.

     * <h3>Samples:</h3>
     * <table>
     * <tr><th>Input</th><th>Result</th></tr>
     * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 1, 1, 1).expand(2, 2, 2)</code></pre></td><td><pre><samp>box[0, 0,
     * 0 -> 3, 3, 3]</samp></pre></td><td>
     * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 1, 1, 1).expand(-2, -2, -2)</code></pre></td><td><pre><samp>box[-2,
     * -2, -2 -> 1, 1, 1]</samp></pre></td><td>
     * <tr><td><pre><code>new AxisAlignedBB(5, 5, 5, 7, 7, 7).expand(0, 1, -1)</code></pre></td><td><pre><samp>box[5, 5,
     * 4, 7, 8, 7]</samp></pre></td><td>
     * </table>

     * <h3>See Also:</h3>
     * <ul>
     * <li>{@link #contract(double, double, double)} - like this, except for shrinking.</li>
     * <li>{@link #grow(double, double, double)} and {@link #grow(double)} - expands in all directions.</li>
     * <li>{@link #shrink(double)} - contracts in all directions (like {@link #grow(double)})</li>
     * </ul>

     * @return A modified bounding box that will always be equal or greater in volume to this bounding box.
     */
    public AxisAlignedBB expand(double x, double y, double z)
    {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;

        if (x < 0.0D)
        {
            d0 += x;
        }
        else if (x > 0.0D)
        {
            d3 += x;
        }

        if (y < 0.0D)
        {
            d1 += y;
        }
        else if (y > 0.0D)
        {
            d4 += y;
        }

        if (z < 0.0D)
        {
            d2 += z;
        }
        else if (z > 0.0D)
        {
            d5 += z;
        }

        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Creates a new {@link AxisAlignedBB} that has been contracted by the given amount in both directions. Negative
     * values will shrink the AABB instead of expanding it.
     * <br/>
     * Side lengths will be increased by 2 times the value of the parameters, since both min and max are changed.
     * <br/>
     * If contracting and the amount to contract by is larger than the length of a side, then the side will wrap (still
     * creating a valid AABB - see last ample).
     *  
     * <h3>Samples:</h3>
     * <table>
     * <tr><th>Input</th><th>Result</th></tr>
     * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 1, 1, 1).grow(2, 2, 2)</code></pre></td><td><pre><samp>box[-2.0,
     * -2.0, -2.0 -> 3.0, 3.0, 3.0]</samp></pre></td></tr>
     * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 6, 6, 6).grow(-2, -2, -2)</code></pre></td><td><pre><samp>box[2.0,
     * 2.0, 2.0 -> 4.0, 4.0, 4.0]</samp></pre></td></tr>
     * <tr><td><pre><code>new AxisAlignedBB(5, 5, 5, 7, 7, 7).grow(0, 1, -1)</code></pre></td><td><pre><samp>box[5.0,
     * 4.0, 6.0 -> 7.0, 8.0, 6.0]</samp></pre></td></tr>
     * <tr><td><pre><code>new AxisAlignedBB(1, 1, 1, 3, 3, 3).grow(-4, -2, -3)</code></pre></td><td><pre><samp>box[-1.0,
     * 1.0, 0.0 -> 5.0, 3.0, 4.0]</samp></pre></td></tr>
     * </table>
     *  
     * <h3>See Also:</h3>
     * <ul>
     * <li>{@link #expand(double, double, double)} - expands in only one direction.</li>
     * <li>{@link #contract(double, double, double)} - contracts in only one direction.</li>
     * <lu>{@link #grow(double)} - version of this that expands in all directions from one parameter.</li>
     * <li>{@link #shrink(double)} - contracts in all directions</li>
     * </ul>
     *  
     * @return A modified bounding box.
     */
    public AxisAlignedBB grow(double x, double y, double z)
    {
        double d0 = this.minX - x;
        double d1 = this.minY - y;
        double d2 = this.minZ - z;
        double d3 = this.maxX + x;
        double d4 = this.maxY + y;
        double d5 = this.maxZ + z;
        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Creates a new {@link AxisAlignedBB} that is expanded by the given value in all directions. Equivalent to {@link
     * #grow(double, double, double)} with the given value for all 3 params. Negative values will shrink the AABB.
     * <br/>
     * Side lengths will be increased by 2 times the value of the parameter, since both min and max are changed.
     * <br/>
     * If contracting and the amount to contract by is larger than the length of a side, then the side will wrap (still
     * creating a valid AABB - see samples on {@link #grow(double, double, double)}).
     *  
     * @return A modified AABB.
     */
    public AxisAlignedBB grow(double value)
    {
        return this.grow(value, value, value);
    }

    public AxisAlignedBB intersect(AxisAlignedBB other)
    {
        double d0 = Math.max(this.minX, other.minX);
        double d1 = Math.max(this.minY, other.minY);
        double d2 = Math.max(this.minZ, other.minZ);
        double d3 = Math.min(this.maxX, other.maxX);
        double d4 = Math.min(this.maxY, other.maxY);
        double d5 = Math.min(this.maxZ, other.maxZ);
        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    public AxisAlignedBB union(AxisAlignedBB other)
    {
        double d0 = Math.min(this.minX, other.minX);
        double d1 = Math.min(this.minY, other.minY);
        double d2 = Math.min(this.minZ, other.minZ);
        double d3 = Math.max(this.maxX, other.maxX);
        double d4 = Math.max(this.maxY, other.maxY);
        double d5 = Math.max(this.maxZ, other.maxZ);
        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Offsets the current bounding box by the specified amount.
     */
    public AxisAlignedBB offset(double x, double y, double z)
    {
        return new AxisAlignedBB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    public AxisAlignedBB offset(BlockPos pos)
    {
        return new AxisAlignedBB(this.minX + (double)pos.getX(), this.minY + (double)pos.getY(), this.minZ + (double)pos.getZ(), this.maxX + (double)pos.getX(), this.maxY + (double)pos.getY(), this.maxZ + (double)pos.getZ());
    }

    public AxisAlignedBB offset(Vector3d vec)
    {
        return this.offset(vec.x, vec.y, vec.z);
    }

    /**
     * Checks if the bounding box intersects with another.
     */
    public boolean intersects(AxisAlignedBB other)
    {
        return this.intersects(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
    }

    public boolean intersects(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        return this.minX < x2 && this.maxX > x1 && this.minY < y2 && this.maxY > y1 && this.minZ < z2 && this.maxZ > z1;
    }

    public boolean intersects(Vector3d min, Vector3d max)
    {
        return this.intersects(Math.min(min.x, max.x), Math.min(min.y, max.y), Math.min(min.z, max.z), Math.max(min.x, max.x), Math.max(min.y, max.y), Math.max(min.z, max.z));
    }

    /**
     * Returns if the supplied Vec3D is completely inside the bounding box
     */
    public boolean contains(Vector3d vec)
    {
        return this.contains(vec.x, vec.y, vec.z);
    }

    public boolean contains(double x, double y, double z)
    {
        return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
    }

    /**
     * Returns the average length of the edges of the bounding box.
     */
    public double getAverageEdgeLength()
    {
        double d0 = this.getXSize();
        double d1 = this.getYSize();
        double d2 = this.getZSize();
        return (d0 + d1 + d2) / 3.0D;
    }

    public double getXSize()
    {
        return this.maxX - this.minX;
    }

    public double getYSize()
    {
        return this.maxY - this.minY;
    }

    public double getZSize()
    {
        return this.maxZ - this.minZ;
    }

    /**
     * Creates a new {@link AxisAlignedBB} that is expanded by the given value in all directions. Equivalent to {@link
     * #grow(double)} with value set to the negative of the value provided here. Passing a negative value to this method
     * values will grow the AABB.
     * <br/>
     * Side lengths will be decreased by 2 times the value of the parameter, since both min and max are changed.
     * <br/>
     * If contracting and the amount to contract by is larger than the length of a side, then the side will wrap (still
     * creating a valid AABB - see samples on {@link #grow(double, double, double)}).
     *  
     * @return A modified AABB.
     */
    public AxisAlignedBB shrink(double value)
    {
        return this.grow(-value);
    }

    public Optional<Vector3d> rayTrace(Vector3d from, Vector3d to)
    {
        double[] adouble = new double[] {1.0D};
        double d0 = to.x - from.x;
        double d1 = to.y - from.y;
        double d2 = to.z - from.z;
        Direction direction = calcSideHit(this, from, adouble, (Direction)null, d0, d1, d2);

        if (direction == null)
        {
            return Optional.empty();
        }
        else
        {
            double d3 = adouble[0];
            return Optional.of(from.add(d3 * d0, d3 * d1, d3 * d2));
        }
    }

    @Nullable
    public static BlockRayTraceResult rayTrace(Iterable<AxisAlignedBB> boxes, Vector3d start, Vector3d end, BlockPos pos)
    {
        double[] adouble = new double[] {1.0D};
        Direction direction = null;
        double d0 = end.x - start.x;
        double d1 = end.y - start.y;
        double d2 = end.z - start.z;

        for (AxisAlignedBB axisalignedbb : boxes)
        {
            direction = calcSideHit(axisalignedbb.offset(pos), start, adouble, direction, d0, d1, d2);
        }

        if (direction == null)
        {
            return null;
        }
        else
        {
            double d3 = adouble[0];
            return new BlockRayTraceResult(start.add(d3 * d0, d3 * d1, d3 * d2), direction, pos, false);
        }
    }

    @Nullable
    private static Direction calcSideHit(AxisAlignedBB aabb, Vector3d start, double[] minDistance, @Nullable Direction facing, double deltaX, double deltaY, double deltaZ)
    {
        if (deltaX > 1.0E-7D)
        {
            facing = checkSideForHit(minDistance, facing, deltaX, deltaY, deltaZ, aabb.minX, aabb.minY, aabb.maxY, aabb.minZ, aabb.maxZ, Direction.WEST, start.x, start.y, start.z);
        }
        else if (deltaX < -1.0E-7D)
        {
            facing = checkSideForHit(minDistance, facing, deltaX, deltaY, deltaZ, aabb.maxX, aabb.minY, aabb.maxY, aabb.minZ, aabb.maxZ, Direction.EAST, start.x, start.y, start.z);
        }

        if (deltaY > 1.0E-7D)
        {
            facing = checkSideForHit(minDistance, facing, deltaY, deltaZ, deltaX, aabb.minY, aabb.minZ, aabb.maxZ, aabb.minX, aabb.maxX, Direction.DOWN, start.y, start.z, start.x);
        }
        else if (deltaY < -1.0E-7D)
        {
            facing = checkSideForHit(minDistance, facing, deltaY, deltaZ, deltaX, aabb.maxY, aabb.minZ, aabb.maxZ, aabb.minX, aabb.maxX, Direction.UP, start.y, start.z, start.x);
        }

        if (deltaZ > 1.0E-7D)
        {
            facing = checkSideForHit(minDistance, facing, deltaZ, deltaX, deltaY, aabb.minZ, aabb.minX, aabb.maxX, aabb.minY, aabb.maxY, Direction.NORTH, start.z, start.x, start.y);
        }
        else if (deltaZ < -1.0E-7D)
        {
            facing = checkSideForHit(minDistance, facing, deltaZ, deltaX, deltaY, aabb.maxZ, aabb.minX, aabb.maxX, aabb.minY, aabb.maxY, Direction.SOUTH, start.z, start.x, start.y);
        }

        return facing;
    }

    @Nullable
    private static Direction checkSideForHit(double[] minDistance, @Nullable Direction prevDirection, double distanceSide, double distanceOtherA, double distanceOtherB, double minSide, double minOtherA, double maxOtherA, double minOtherB, double maxOtherB, Direction hitSide, double startSide, double startOtherA, double startOtherB)
    {
        double d0 = (minSide - startSide) / distanceSide;
        double d1 = startOtherA + d0 * distanceOtherA;
        double d2 = startOtherB + d0 * distanceOtherB;

        if (0.0D < d0 && d0 < minDistance[0] && minOtherA - 1.0E-7D < d1 && d1 < maxOtherA + 1.0E-7D && minOtherB - 1.0E-7D < d2 && d2 < maxOtherB + 1.0E-7D)
        {
            minDistance[0] = d0;
            return hitSide;
        }
        else
        {
            return prevDirection;
        }
    }

    public String toString()
    {
        return "AABB[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }

    public boolean hasNaN()
    {
        return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
    }

    public Vector3d getCenter()
    {
        return new Vector3d(MathHelper.lerp(0.5D, this.minX, this.maxX), MathHelper.lerp(0.5D, this.minY, this.maxY), MathHelper.lerp(0.5D, this.minZ, this.maxZ));
    }

    public static AxisAlignedBB withSizeAtOrigin(double xSize, double ySize, double zSize)
    {
        return new AxisAlignedBB(-xSize / 2.0D, -ySize / 2.0D, -zSize / 2.0D, xSize / 2.0D, ySize / 2.0D, zSize / 2.0D);
    }
}
