package net.optifine.shaders;

import net.optifine.texture.ColorBlenderLabPbrSpecular;
import net.optifine.texture.ColorBlenderLinear;
import net.optifine.texture.IColorBlender;

public class TextureFormatLabPbr implements ITextureFormat
{
    private String version;

    public TextureFormatLabPbr(String ver)
    {
        this.version = ver;
    }

    public String getMacroName()
    {
        return "LAB_PBR";
    }

    public String getMacroVersion()
    {
        return this.version == null ? null : this.version.replace('.', '_');
    }

    public IColorBlender getColorBlender(ShadersTextureType typeIn)
    {
        return (IColorBlender)(typeIn == ShadersTextureType.SPECULAR ? new ColorBlenderLabPbrSpecular() : new ColorBlenderLinear());
    }

    public boolean isTextureBlend(ShadersTextureType typeIn)
    {
        return typeIn != ShadersTextureType.SPECULAR;
    }
}
