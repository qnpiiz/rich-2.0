package net.optifine.shaders;

import net.optifine.Config;
import net.optifine.config.ConfigUtils;
import net.optifine.texture.IColorBlender;

public interface ITextureFormat
{
    IColorBlender getColorBlender(ShadersTextureType var1);

    boolean isTextureBlend(ShadersTextureType var1);

    String getMacroName();

    String getMacroVersion();

    static ITextureFormat readConfiguration()
    {
        if (!Config.isShaders())
        {
            return null;
        }
        else
        {
            String s = ConfigUtils.readString("optifine/texture.properties", "format");

            if (s != null)
            {
                String[] astring = Config.tokenize(s, "/");
                String s1 = astring[0];
                String s2 = astring.length > 1 ? astring[1] : null;

                if (s1.equals("lab-pbr"))
                {
                    return new TextureFormatLabPbr(s2);
                }
                else
                {
                    Config.warn("Unknown texture format: " + s);
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
    }
}
