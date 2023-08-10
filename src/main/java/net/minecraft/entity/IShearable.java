package net.minecraft.entity;

import net.minecraft.util.SoundCategory;

public interface IShearable
{
    void shear(SoundCategory category);

    boolean isShearable();
}
