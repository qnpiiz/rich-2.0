package net.optifine.texture;

public class ColorBlenderSeparate implements IColorBlender
{
    private IBlender blenderR;
    private IBlender blenderG;
    private IBlender blenderB;
    private IBlender blenderA;

    public ColorBlenderSeparate(IBlender blenderR, IBlender blenderG, IBlender blenderB, IBlender blenderA)
    {
        this.blenderR = blenderR;
        this.blenderG = blenderG;
        this.blenderB = blenderB;
        this.blenderA = blenderA;
    }

    public int blend(int c1, int c2, int c3, int c4)
    {
        int i = c1 >> 24 & 255;
        int j = c1 >> 16 & 255;
        int k = c1 >> 8 & 255;
        int l = c1 & 255;
        int i1 = c2 >> 24 & 255;
        int j1 = c2 >> 16 & 255;
        int k1 = c2 >> 8 & 255;
        int l1 = c2 & 255;
        int i2 = c3 >> 24 & 255;
        int j2 = c3 >> 16 & 255;
        int k2 = c3 >> 8 & 255;
        int l2 = c3 & 255;
        int i3 = c4 >> 24 & 255;
        int j3 = c4 >> 16 & 255;
        int k3 = c4 >> 8 & 255;
        int l3 = c4 & 255;
        int i4 = this.blenderA.blend(i, i1, i2, i3);
        int j4 = this.blenderR.blend(j, j1, j2, j3);
        int k4 = this.blenderG.blend(k, k1, k2, k3);
        int l4 = this.blenderB.blend(l, l1, l2, l3);
        return i4 << 24 | j4 << 16 | k4 << 8 | l4;
    }
}
