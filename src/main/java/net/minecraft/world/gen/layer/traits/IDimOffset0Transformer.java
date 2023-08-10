package net.minecraft.world.gen.layer.traits;

public interface IDimOffset0Transformer extends IDimTransformer
{
default int getOffsetX(int x)
    {
        return x;
    }

default int getOffsetZ(int z)
    {
        return z;
    }
}
