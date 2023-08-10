package net.optifine.shaders;

public interface ICustomTexture
{
    int getTextureId();

    int getTextureUnit();

    void deleteTexture();

default int getTarget()
    {
        return 3553;
    }
}
