package net.optifine.texture;

public class ColorBlenderLabPbrSpecular extends ColorBlenderSeparate
{
    public ColorBlenderLabPbrSpecular()
    {
        super(new BlenderLinear(), new BlenderSplit(230, true), new BlenderSplit(65, false), new BlenderSplit(255, true));
    }
}
