package net.minecraft.entity.villager;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public final class VillagerType
{
    public static final VillagerType DESERT = register("desert");
    public static final VillagerType JUNGLE = register("jungle");
    public static final VillagerType PLAINS = register("plains");
    public static final VillagerType SAVANNA = register("savanna");
    public static final VillagerType SNOW = register("snow");
    public static final VillagerType SWAMP = register("swamp");
    public static final VillagerType TAIGA = register("taiga");
    private final String field_242370_h;
    private static final Map<RegistryKey<Biome>, VillagerType> BY_BIOME = Util.make(Maps.newHashMap(), (p_221172_0_) ->
    {
        p_221172_0_.put(Biomes.BADLANDS, DESERT);
        p_221172_0_.put(Biomes.BADLANDS_PLATEAU, DESERT);
        p_221172_0_.put(Biomes.DESERT, DESERT);
        p_221172_0_.put(Biomes.DESERT_HILLS, DESERT);
        p_221172_0_.put(Biomes.DESERT_LAKES, DESERT);
        p_221172_0_.put(Biomes.ERODED_BADLANDS, DESERT);
        p_221172_0_.put(Biomes.MODIFIED_BADLANDS_PLATEAU, DESERT);
        p_221172_0_.put(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, DESERT);
        p_221172_0_.put(Biomes.WOODED_BADLANDS_PLATEAU, DESERT);
        p_221172_0_.put(Biomes.BAMBOO_JUNGLE, JUNGLE);
        p_221172_0_.put(Biomes.BAMBOO_JUNGLE_HILLS, JUNGLE);
        p_221172_0_.put(Biomes.JUNGLE, JUNGLE);
        p_221172_0_.put(Biomes.JUNGLE_EDGE, JUNGLE);
        p_221172_0_.put(Biomes.JUNGLE_HILLS, JUNGLE);
        p_221172_0_.put(Biomes.MODIFIED_JUNGLE, JUNGLE);
        p_221172_0_.put(Biomes.MODIFIED_JUNGLE_EDGE, JUNGLE);
        p_221172_0_.put(Biomes.SAVANNA_PLATEAU, SAVANNA);
        p_221172_0_.put(Biomes.SAVANNA, SAVANNA);
        p_221172_0_.put(Biomes.SHATTERED_SAVANNA, SAVANNA);
        p_221172_0_.put(Biomes.SHATTERED_SAVANNA_PLATEAU, SAVANNA);
        p_221172_0_.put(Biomes.DEEP_FROZEN_OCEAN, SNOW);
        p_221172_0_.put(Biomes.FROZEN_OCEAN, SNOW);
        p_221172_0_.put(Biomes.FROZEN_RIVER, SNOW);
        p_221172_0_.put(Biomes.ICE_SPIKES, SNOW);
        p_221172_0_.put(Biomes.SNOWY_BEACH, SNOW);
        p_221172_0_.put(Biomes.SNOWY_MOUNTAINS, SNOW);
        p_221172_0_.put(Biomes.SNOWY_TAIGA, SNOW);
        p_221172_0_.put(Biomes.SNOWY_TAIGA_HILLS, SNOW);
        p_221172_0_.put(Biomes.SNOWY_TAIGA_MOUNTAINS, SNOW);
        p_221172_0_.put(Biomes.SNOWY_TUNDRA, SNOW);
        p_221172_0_.put(Biomes.SWAMP, SWAMP);
        p_221172_0_.put(Biomes.SWAMP_HILLS, SWAMP);
        p_221172_0_.put(Biomes.GIANT_SPRUCE_TAIGA, TAIGA);
        p_221172_0_.put(Biomes.GIANT_SPRUCE_TAIGA_HILLS, TAIGA);
        p_221172_0_.put(Biomes.GIANT_TREE_TAIGA, TAIGA);
        p_221172_0_.put(Biomes.GIANT_TREE_TAIGA_HILLS, TAIGA);
        p_221172_0_.put(Biomes.GRAVELLY_MOUNTAINS, TAIGA);
        p_221172_0_.put(Biomes.MODIFIED_GRAVELLY_MOUNTAINS, TAIGA);
        p_221172_0_.put(Biomes.MOUNTAIN_EDGE, TAIGA);
        p_221172_0_.put(Biomes.MOUNTAINS, TAIGA);
        p_221172_0_.put(Biomes.TAIGA, TAIGA);
        p_221172_0_.put(Biomes.TAIGA_HILLS, TAIGA);
        p_221172_0_.put(Biomes.TAIGA_MOUNTAINS, TAIGA);
        p_221172_0_.put(Biomes.WOODED_MOUNTAINS, TAIGA);
    });

    private VillagerType(String p_i241919_1_)
    {
        this.field_242370_h = p_i241919_1_;
    }

    public String toString()
    {
        return this.field_242370_h;
    }

    private static VillagerType register(String key)
    {
        return Registry.register(Registry.VILLAGER_TYPE, new ResourceLocation(key), new VillagerType(key));
    }

    public static VillagerType func_242371_a(Optional<RegistryKey<Biome>> p_242371_0_)
    {
        return p_242371_0_.flatMap((p_242372_0_) ->
        {
            return Optional.ofNullable(BY_BIOME.get(p_242372_0_));
        }).orElse(PLAINS);
    }
}
