package net.minecraft.util.math;

public class CubeCoordinateIterator
{
    private int startX;
    private int startY;
    private int startZ;
    private int xWidth;
    private int yHeight;
    private int zWidth;
    private int totalAmount;
    private int currentAmount;
    private int x;
    private int y;
    private int z;

    public CubeCoordinateIterator(int startX, int startY, int startZ, int endX, int yHeight, int endZ)
    {
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.xWidth = endX - startX + 1;
        this.yHeight = yHeight - startY + 1;
        this.zWidth = endZ - startZ + 1;
        this.totalAmount = this.xWidth * this.yHeight * this.zWidth;
    }

    public boolean hasNext()
    {
        if (this.currentAmount == this.totalAmount)
        {
            return false;
        }
        else
        {
            this.x = this.currentAmount % this.xWidth;
            int i = this.currentAmount / this.xWidth;
            this.y = i % this.yHeight;
            this.z = i / this.yHeight;
            ++this.currentAmount;
            return true;
        }
    }

    public int getX()
    {
        return this.startX + this.x;
    }

    public int getY()
    {
        return this.startY + this.y;
    }

    public int getZ()
    {
        return this.startZ + this.z;
    }

    public int numBoundariesTouched()
    {
        int i = 0;

        if (this.x == 0 || this.x == this.xWidth - 1)
        {
            ++i;
        }

        if (this.y == 0 || this.y == this.yHeight - 1)
        {
            ++i;
        }

        if (this.z == 0 || this.z == this.zWidth - 1)
        {
            ++i;
        }

        return i;
    }
}
