package net.optifine.texture;

import net.optifine.util.IntArray;

public class BlenderSplit implements IBlender
{
    private int startHigh;
    private boolean discreteHigh;

    public BlenderSplit(int startHigh, boolean discreteHigh)
    {
        this.startHigh = startHigh;
        this.discreteHigh = discreteHigh;
    }

    public int blend(int v1, int v2, int v3, int v4)
    {
        if (v1 == v2 && v2 == v3 && v3 == v4)
        {
            return v1;
        }
        else
        {
            boolean flag = v1 < this.startHigh;
            boolean flag1 = v2 < this.startHigh;
            boolean flag2 = v3 < this.startHigh;
            boolean flag3 = v4 < this.startHigh;

            if (flag == flag1 && flag1 == flag2 && flag2 == flag3)
            {
                return !flag && this.discreteHigh ? v1 : (v1 + v2 + v3 + v4) / 4;
            }
            else
            {
                IntArray intarray = new IntArray(4);
                IntArray intarray1 = new IntArray(4);
                this.separate(v1, intarray, intarray1);
                this.separate(v2, intarray, intarray1);
                this.separate(v3, intarray, intarray1);
                this.separate(v4, intarray, intarray1);

                if (intarray1.getPosition() > intarray.getPosition())
                {
                    return this.discreteHigh ? intarray1.get(0) : this.getAverage(intarray1);
                }
                else
                {
                    return this.getAverage(intarray);
                }
            }
        }
    }

    private void separate(int val, IntArray low, IntArray high)
    {
        if (val < this.startHigh)
        {
            low.put(val);
        }
        else
        {
            high.put(val);
        }
    }

    private int getAverage(IntArray arr)
    {
        int i = arr.getLimit();

        switch (i)
        {
            case 2:
                return (arr.get(0) + arr.get(1)) / 2;

            case 3:
                return (arr.get(0) + arr.get(1) + arr.get(2)) / 3;

            default:
                int j = 0;

                for (int k = 0; k < i; ++k)
                {
                    j += arr.get(k);
                }

                return j / i;
        }
    }

    public String toString()
    {
        return "BlenderSplit: " + this.startHigh + ", " + this.discreteHigh;
    }

    public static void main(String[] args)
    {
        BlenderSplit blendersplit = new BlenderSplit(230, true);
        System.out.println("" + blendersplit);
        int i = blendersplit.blend(10, 20, 30, 40);
        System.out.println("" + i + " =? 25");
        int j = blendersplit.blend(10, 20, 30, 230);
        System.out.println("" + j + " =? 20");
        int k = blendersplit.blend(10, 20, 240, 230);
        System.out.println("" + k + " =? 15");
        int l = blendersplit.blend(10, 250, 240, 230);
        System.out.println("" + l + " =? 250");
        int i1 = blendersplit.blend(245, 250, 240, 230);
        System.out.println("" + i1 + " =? 245");
        int j1 = blendersplit.blend(10, 10, 10, 10);
        System.out.println("" + j1 + " =? 10");
        BlenderSplit blendersplit1 = new BlenderSplit(65, false);
        System.out.println("" + blendersplit1);
        int k1 = blendersplit1.blend(10, 20, 30, 40);
        System.out.println("" + k1 + " =? 25");
        int l1 = blendersplit1.blend(10, 20, 30, 70);
        System.out.println("" + l1 + " =? 20");
        int i2 = blendersplit1.blend(10, 90, 20, 70);
        System.out.println("" + i2 + " =? 15");
        int j2 = blendersplit1.blend(110, 90, 20, 70);
        System.out.println("" + j2 + " =? 90");
        int k2 = blendersplit1.blend(110, 90, 130, 70);
        System.out.println("" + k2 + " =? 100");
    }
}
