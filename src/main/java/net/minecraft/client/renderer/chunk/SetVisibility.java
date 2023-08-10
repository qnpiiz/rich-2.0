package net.minecraft.client.renderer.chunk;

import java.util.Set;
import net.minecraft.util.Direction;

public class SetVisibility
{
    private static final int COUNT_FACES = Direction.values().length;
    private long bits;

    public void setManyVisible(Set<Direction> facing)
    {
        for (Direction direction : facing)
        {
            for (Direction direction1 : facing)
            {
                this.setVisible(direction, direction1, true);
            }
        }
    }

    public void setVisible(Direction facing, Direction facing2, boolean value)
    {
        this.setBit(facing.ordinal() + facing2.ordinal() * COUNT_FACES, value);
        this.setBit(facing2.ordinal() + facing.ordinal() * COUNT_FACES, value);
    }

    public void setAllVisible(boolean visible)
    {
        if (visible)
        {
            this.bits = -1L;
        }
        else
        {
            this.bits = 0L;
        }
    }

    public boolean isVisible(Direction facing, Direction facing2)
    {
        return this.getBit(facing.ordinal() + facing2.ordinal() * COUNT_FACES);
    }

    public String toString()
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(' ');

        for (Direction direction : Direction.values())
        {
            stringbuilder.append(' ').append(direction.toString().toUpperCase().charAt(0));
        }

        stringbuilder.append('\n');

        for (Direction direction2 : Direction.values())
        {
            stringbuilder.append(direction2.toString().toUpperCase().charAt(0));

            for (Direction direction1 : Direction.values())
            {
                if (direction2 == direction1)
                {
                    stringbuilder.append("  ");
                }
                else
                {
                    boolean flag = this.isVisible(direction2, direction1);
                    stringbuilder.append(' ').append((char)(flag ? 'Y' : 'n'));
                }
            }

            stringbuilder.append('\n');
        }

        return stringbuilder.toString();
    }

    private boolean getBit(int p_getBit_1_)
    {
        return (this.bits & (long)(1 << p_getBit_1_)) != 0L;
    }

    private void setBit(int p_setBit_1_, boolean p_setBit_2_)
    {
        if (p_setBit_2_)
        {
            this.setBit(p_setBit_1_);
        }
        else
        {
            this.clearBit(p_setBit_1_);
        }
    }

    private void setBit(int p_setBit_1_)
    {
        this.bits |= (long)(1 << p_setBit_1_);
    }

    private void clearBit(int p_clearBit_1_)
    {
        this.bits &= (long)(~(1 << p_clearBit_1_));
    }
}
