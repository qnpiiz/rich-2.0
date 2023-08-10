package net.optifine;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public interface IRandomEntity
{
    int getId();

    BlockPos getSpawnPosition();

    Biome getSpawnBiome();

    String getName();

    int getHealth();

    int getMaxHealth();
}
