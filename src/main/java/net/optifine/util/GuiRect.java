package net.optifine.util;

public class GuiRect
{
    private int left;
    private int top;
    private int right;
    private int bottom;

    public GuiRect(int left, int top, int right, int bottom)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int getLeft()
    {
        return this.left;
    }

    public int getTop()
    {
        return this.top;
    }

    public int getRight()
    {
        return this.right;
    }

    public int getBottom()
    {
        return this.bottom;
    }
}
