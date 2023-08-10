package net.optifine.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraft.world.biome.Biomes;
import net.optifine.config.BiomeId;
import net.optifine.override.ChunkCacheOF;

public class BiomeUtils
{
    private static Registry<Biome> biomeRegistry = getBiomeRegistry(Minecraft.getInstance().world);
    public static Biome PLAINS = getBiomeSafe(biomeRegistry, Biomes.PLAINS, () ->
    {
        return BiomeMaker.makePlainsBiome(false);
    });
    public static Biome SWAMP = getBiomeSafe(biomeRegistry, Biomes.SWAMP, () ->
    {
        return BiomeMaker.makeGenericSwampBiome(-0.2F, 0.1F, false);
    });
    public static Biome SWAMP_HILLS = getBiomeSafe(biomeRegistry, Biomes.SWAMP_HILLS, () ->
    {
        return BiomeMaker.makeGenericSwampBiome(-0.1F, 0.3F, true);
    });

    public static void onWorldChanged(World worldIn)
    {
        biomeRegistry = getBiomeRegistry(worldIn);
        PLAINS = getBiomeSafe(biomeRegistry, Biomes.PLAINS, () ->
        {
            return BiomeMaker.makePlainsBiome(false);
        });
        SWAMP = getBiomeSafe(biomeRegistry, Biomes.SWAMP, () ->
        {
            return BiomeMaker.makeGenericSwampBiome(-0.2F, 0.1F, false);
        });
        SWAMP_HILLS = getBiomeSafe(biomeRegistry, Biomes.SWAMP_HILLS, () ->
        {
            return BiomeMaker.makeGenericSwampBiome(-0.1F, 0.3F, true);
        });
    }

    private static Biome getBiomeSafe(Registry<Biome> registry, RegistryKey<Biome> biomeKey, Supplier<Biome> biomeDefault)
    {
        Biome biome = registry.getValueForKey(biomeKey);

        if (biome == null)
        {
            biome = biomeDefault.get();
        }

        return biome;
    }

    public static Registry<Biome> getBiomeRegistry(World worldIn)
    {
        return (Registry<Biome>)(worldIn != null ? worldIn.func_241828_r().getRegistry(Registry.BIOME_KEY) : WorldGenRegistries.BIOME);
    }

    public static Registry<Biome> getBiomeRegistry()
    {
        return biomeRegistry;
    }

    public static ResourceLocation getLocation(Biome biome)
    {
        return getBiomeRegistry().getKey(biome);
    }

    public static int getId(Biome biome)
    {
        return getBiomeRegistry().getId(biome);
    }

    public static int getId(ResourceLocation loc)
    {
        Biome biome = getBiome(loc);
        return getBiomeRegistry().getId(biome);
    }

    public static BiomeId getBiomeId(ResourceLocation loc)
    {
        return BiomeId.make(loc);
    }

    public static Biome getBiome(ResourceLocation loc)
    {
        return getBiomeRegistry().getOrDefault(loc);
    }

    public static Set<ResourceLocation> getLocations()
    {
        return getBiomeRegistry().keySet();
    }

    public static List<Biome> getBiomes()
    {
        return Lists.newArrayList(biomeRegistry);
    }

    public static List<BiomeId> getBiomeIds()
    {
        return getBiomeIds(getLocations());
    }

    public static List<BiomeId> getBiomeIds(Collection<ResourceLocation> locations)
    {
        List<BiomeId> list = new ArrayList<>();

        for (ResourceLocation resourcelocation : locations)
        {
            BiomeId biomeid = BiomeId.make(resourcelocation);

            if (biomeid != null)
            {
                list.add(biomeid);
            }
        }

        return list;
    }

    public static Biome getBiome(IBlockDisplayReader lightReader, BlockPos blockPos)
    {
        Biome biome = PLAINS;

        if (lightReader instanceof ChunkCacheOF)
        {
            biome = ((ChunkCacheOF)lightReader).getBiome(blockPos);
        }
        else if (lightReader instanceof IWorldReader)
        {
            biome = ((IWorldReader)lightReader).getBiome(blockPos);
        }

        return biome;
    }
}
