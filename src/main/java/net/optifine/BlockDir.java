package net.optifine;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public enum BlockDir
{
    DOWN(Direction.DOWN),
    UP(Direction.UP),
    NORTH(Direction.NORTH),
    SOUTH(Direction.SOUTH),
    WEST(Direction.WEST),
    EAST(Direction.EAST),
    NORTH_WEST(Direction.NORTH, Direction.WEST),
    NORTH_EAST(Direction.NORTH, Direction.EAST),
    SOUTH_WEST(Direction.SOUTH, Direction.WEST),
    SOUTH_EAST(Direction.SOUTH, Direction.EAST),
    DOWN_NORTH(Direction.DOWN, Direction.NORTH),
    DOWN_SOUTH(Direction.DOWN, Direction.SOUTH),
    UP_NORTH(Direction.UP, Direction.NORTH),
    UP_SOUTH(Direction.UP, Direction.SOUTH),
    DOWN_WEST(Direction.DOWN, Direction.WEST),
    DOWN_EAST(Direction.DOWN, Direction.EAST),
    UP_WEST(Direction.UP, Direction.WEST),
    UP_EAST(Direction.UP, Direction.EAST);

    private Direction facing1;
    private Direction facing2;

    private BlockDir(Direction facing1)
    {
        this.facing1 = facing1;
    }

    private BlockDir(Direction facing1, Direction facing2)
    {
        this.facing1 = facing1;
        this.facing2 = facing2;
    }

    public Direction getFacing1()
    {
        return this.facing1;
    }

    public Direction getFacing2()
    {
        return this.facing2;
    }

    BlockPos offset(BlockPos pos)
    {
        pos = pos.offset(this.facing1, 1);

        if (this.facing2 != null)
        {
            pos = pos.offset(this.facing2, 1);
        }

        return pos;
    }

    public int getOffsetX()
    {
        int i = this.facing1.getXOffset();

        if (this.facing2 != null)
        {
            i += this.facing2.getXOffset();
        }

        return i;
    }

    public int getOffsetY()
    {
        int i = this.facing1.getYOffset();

        if (this.facing2 != null)
        {
            i += this.facing2.getYOffset();
        }

        return i;
    }

    public int getOffsetZ()
    {
        int i = this.facing1.getZOffset();

        if (this.facing2 != null)
        {
            i += this.facing2.getZOffset();
        }

        return i;
    }

    public boolean isDouble()
    {
        return this.facing2 != null;
    }
}
