package net.optifine.texture;

public class ColorBlenderLinear extends ColorBlenderSeparate
{
    public ColorBlenderLinear()
    {
        super(new BlenderLinear(), new BlenderLinear(), new BlenderLinear(), new BlenderLinear());
    }

    public int blend(int c1, int c2, int c3, int c4)
    {
        return c1 == c2 && c2 == c3 && c3 == c4 ? c1 : super.blend(c1, c2, c3, c4);
    }
}
