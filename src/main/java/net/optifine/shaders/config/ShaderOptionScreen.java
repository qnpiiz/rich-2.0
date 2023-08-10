package net.optifine.shaders.config;

import net.optifine.shaders.Shaders;

public class ShaderOptionScreen extends ShaderOption
{
    public ShaderOptionScreen(String name)
    {
        super(name, (String)null, (String)null, new String[0], (String)null, (String)null);
    }

    public String getNameText()
    {
        return Shaders.translate("screen." + this.getName(), this.getName());
    }

    public String getDescriptionText()
    {
        return Shaders.translate("screen." + this.getName() + ".comment", (String)null);
    }
}
