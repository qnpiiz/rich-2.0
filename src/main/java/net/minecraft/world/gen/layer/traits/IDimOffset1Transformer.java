package net.minecraft.world.gen.layer.traits;

public interface IDimOffset1Transformer extends IDimTransformer
{
default int getOffsetX(int x)
    {
        return x - 1;
    }

default int getOffsetZ(int z)
    {
        return z - 1;
    }
}
