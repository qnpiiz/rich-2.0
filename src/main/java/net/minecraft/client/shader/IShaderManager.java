package net.minecraft.client.shader;

public interface IShaderManager
{
    int getProgram();

    void markDirty();

    ShaderLoader getVertexShaderLoader();

    ShaderLoader getFragmentShaderLoader();
}
