package net.minecraft.util.math.vector;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import java.util.stream.IntStream;
import javax.annotation.concurrent.Immutable;
import net.minecraft.dispenser.IPosition;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Immutable
public class Vector3i implements Comparable<Vector3i>
{
    public static final Codec<Vector3i> CODEC = Codec.INT_STREAM.comapFlatMap((stream) ->
    {
        return Util.validateIntStreamSize(stream, 3).map((componentArray) -> {
            return new Vector3i(componentArray[0], componentArray[1], componentArray[2]);
        });
    }, (vector) ->
    {
        return IntStream.of(vector.getX(), vector.getY(), vector.getZ());
    });

    /** An immutable vector with zero as all coordinates. */
    public static final Vector3i NULL_VECTOR = new Vector3i(0, 0, 0);
    private int x;
    private int y;
    private int z;

    public Vector3i(int xIn, int yIn, int zIn)
    {
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
    }

    public Vector3i(double xIn, double yIn, double zIn)
    {
        this(MathHelper.floor(xIn), MathHelper.floor(yIn), MathHelper.floor(zIn));
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof Vector3i))
        {
            return false;
        }
        else
        {
            Vector3i vector3i = (Vector3i)p_equals_1_;

            if (this.getX() != vector3i.getX())
            {
                return false;
            }
            else if (this.getY() != vector3i.getY())
            {
                return false;
            }
            else
            {
                return this.getZ() == vector3i.getZ();
            }
        }
    }

    public int hashCode()
    {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    public int compareTo(Vector3i p_compareTo_1_)
    {
        if (this.getY() == p_compareTo_1_.getY())
        {
            return this.getZ() == p_compareTo_1_.getZ() ? this.getX() - p_compareTo_1_.getX() : this.getZ() - p_compareTo_1_.getZ();
        }
        else
        {
            return this.getY() - p_compareTo_1_.getY();
        }
    }

    /**
     * Gets the X coordinate.
     */
    public int getX()
    {
        return this.x;
    }

    /**
     * Gets the Y coordinate.
     */
    public int getY()
    {
        return this.y;
    }

    /**
     * Gets the Z coordinate.
     */
    public int getZ()
    {
        return this.z;
    }

    /**
     * Sets the X coordinate.
     */
    protected void setX(int xIn)
    {
        this.x = xIn;
    }

    protected void setY(int yIn)
    {
        this.y = yIn;
    }

    /**
     * Sets the Z coordinate.
     */
    protected void setZ(int zIn)
    {
        this.z = zIn;
    }

    /**
     * Offset this BlockPos 1 block up
     */
    public Vector3i up()
    {
        return this.up(1);
    }

    /**
     * Offset this BlockPos n blocks up
     */
    public Vector3i up(int n)
    {
        return this.offset(Direction.UP, n);
    }

    /**
     * Offset this BlockPos 1 block down
     */
    public Vector3i down()
    {
        return this.down(1);
    }

    /**
     * Offset this BlockPos n blocks down
     */
    public Vector3i down(int n)
    {
        return this.offset(Direction.DOWN, n);
    }

    /**
     * Offsets this BlockPos n blocks in the given direction
     */
    public Vector3i offset(Direction facing, int n)
    {
        return n == 0 ? this : new Vector3i(this.getX() + facing.getXOffset() * n, this.getY() + facing.getYOffset() * n, this.getZ() + facing.getZOffset() * n);
    }

    /**
     * Calculate the cross product of this and the given Vector
     */
    public Vector3i crossProduct(Vector3i vec)
    {
        return new Vector3i(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    public boolean withinDistance(Vector3i vector, double distance)
    {
        return this.distanceSq((double)vector.getX(), (double)vector.getY(), (double)vector.getZ(), false) < distance * distance;
    }

    public boolean withinDistance(IPosition position, double distance)
    {
        return this.distanceSq(position.getX(), position.getY(), position.getZ(), true) < distance * distance;
    }

    /**
     * Calculate squared distance to the given Vector
     */
    public double distanceSq(Vector3i to)
    {
        return this.distanceSq((double)to.getX(), (double)to.getY(), (double)to.getZ(), true);
    }

    public double distanceSq(IPosition position, boolean useCenter)
    {
        return this.distanceSq(position.getX(), position.getY(), position.getZ(), useCenter);
    }

    public double distanceSq(double x, double y, double z, boolean useCenter)
    {
        double d0 = useCenter ? 0.5D : 0.0D;
        double d1 = (double)this.getX() + d0 - x;
        double d2 = (double)this.getY() + d0 - y;
        double d3 = (double)this.getZ() + d0 - z;
        return d1 * d1 + d2 * d2 + d3 * d3;
    }

    public int manhattanDistance(Vector3i vector)
    {
        float f = (float)Math.abs(vector.getX() - this.getX());
        float f1 = (float)Math.abs(vector.getY() - this.getY());
        float f2 = (float)Math.abs(vector.getZ() - this.getZ());
        return (int)(f + f1 + f2);
    }

    public int func_243648_a(Direction.Axis p_243648_1_)
    {
        return p_243648_1_.getCoordinate(this.x, this.y, this.z);
    }

    public String toString()
    {
        return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
    }

    public String getCoordinatesAsString()
    {
        return "" + this.getX() + ", " + this.getY() + ", " + this.getZ();
    }
}
