package net.optifine.shaders;

import java.io.InputStream;
import net.optifine.Config;

public class ShaderPackDefault implements IShaderPack
{
    public void close()
    {
    }

    public InputStream getResourceAsStream(String resName)
    {
        return Config.getOptiFineResourceStream(resName);
    }

    public String getName()
    {
        return "(internal)";
    }

    public boolean hasDirectory(String name)
    {
        return false;
    }
}
