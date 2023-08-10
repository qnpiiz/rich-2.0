package net.optifine.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.optifine.util.BiomeUtils;

public class BiomeId
{
    private final ResourceLocation resourceLocation;
    private ClientWorld world;
    private Biome biome;
    private static Minecraft minecraft = Minecraft.getInstance();

    private BiomeId(ResourceLocation resourceLocation)
    {
        this.resourceLocation = resourceLocation;
        this.world = minecraft.world;
        this.updateBiome();
    }

    private void updateBiome()
    {
        this.biome = null;
        Registry<Biome> registry = BiomeUtils.getBiomeRegistry(this.world);

        if (registry.containsKey(this.resourceLocation))
        {
            this.biome = registry.getOrDefault(this.resourceLocation);
        }
    }

    public Biome getBiome()
    {
        if (this.world != minecraft.world)
        {
            this.world = minecraft.world;
            this.updateBiome();
        }

        return this.biome;
    }

    public ResourceLocation getResourceLocation()
    {
        return this.resourceLocation;
    }

    public String toString()
    {
        return "" + this.resourceLocation;
    }

    public static BiomeId make(ResourceLocation resourceLocation)
    {
        BiomeId biomeid = new BiomeId(resourceLocation);
        return biomeid.biome == null ? null : biomeid;
    }
}
