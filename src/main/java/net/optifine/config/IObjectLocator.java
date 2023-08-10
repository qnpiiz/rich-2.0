package net.optifine.config;

import net.minecraft.util.ResourceLocation;

public interface IObjectLocator<T>
{
    T getObject(ResourceLocation var1);
}
