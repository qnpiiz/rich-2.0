package net.optifine;

import java.util.Properties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.optifine.config.BiomeId;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchProfession;
import net.optifine.config.Matches;
import net.optifine.config.NbtTagValue;
import net.optifine.config.RangeInt;
import net.optifine.config.RangeListInt;
import net.optifine.config.Weather;
import net.optifine.util.ArrayUtils;
import net.optifine.util.MathUtils;

public class RandomEntityRule
{
    private String pathProps = null;
    private ResourceLocation baseResLoc = null;
    private int index;
    private int[] textures = null;
    private ResourceLocation[] resourceLocations = null;
    private int[] weights = null;
    private BiomeId[] biomes = null;
    private RangeListInt heights = null;
    private RangeListInt healthRange = null;
    private boolean healthPercent = false;
    private NbtTagValue nbtName = null;
    public int[] sumWeights = null;
    public int sumAllWeights = 1;
    private MatchProfession[] professions = null;
    private DyeColor[] collarColors = null;
    private Boolean baby = null;
    private RangeListInt moonPhases = null;
    private RangeListInt dayTimes = null;
    private Weather[] weatherList = null;

    public RandomEntityRule(Properties props, String pathProps, ResourceLocation baseResLoc, int index, String valTextures, ConnectedParser cp)
    {
        this.pathProps = pathProps;
        this.baseResLoc = baseResLoc;
        this.index = index;
        this.textures = cp.parseIntList(valTextures);
        this.weights = cp.parseIntList(props.getProperty("weights." + index));
        this.biomes = cp.parseBiomes(props.getProperty("biomes." + index));
        this.heights = cp.parseRangeListInt(props.getProperty("heights." + index));

        if (this.heights == null)
        {
            this.heights = this.parseMinMaxHeight(props, index);
        }

        String s = props.getProperty("health." + index);

        if (s != null)
        {
            this.healthPercent = s.contains("%");
            s = s.replace("%", "");
            this.healthRange = cp.parseRangeListInt(s);
        }

        this.nbtName = cp.parseNbtTagValue("name", props.getProperty("name." + index));
        this.professions = cp.parseProfessions(props.getProperty("professions." + index));
        this.collarColors = cp.parseDyeColors(props.getProperty("collarColors." + index), "collar color", ConnectedParser.DYE_COLORS_INVALID);
        this.baby = cp.parseBooleanObject(props.getProperty("baby." + index));
        this.moonPhases = cp.parseRangeListInt(props.getProperty("moonPhase." + index));
        this.dayTimes = cp.parseRangeListInt(props.getProperty("dayTime." + index));
        this.weatherList = cp.parseWeather(props.getProperty("weather." + index), "weather." + index, (Weather[])null);
    }

    private RangeListInt parseMinMaxHeight(Properties props, int index)
    {
        String s = props.getProperty("minHeight." + index);
        String s1 = props.getProperty("maxHeight." + index);

        if (s == null && s1 == null)
        {
            return null;
        }
        else
        {
            int i = 0;

            if (s != null)
            {
                i = Config.parseInt(s, -1);

                if (i < 0)
                {
                    Config.warn("Invalid minHeight: " + s);
                    return null;
                }
            }

            int j = 256;

            if (s1 != null)
            {
                j = Config.parseInt(s1, -1);

                if (j < 0)
                {
                    Config.warn("Invalid maxHeight: " + s1);
                    return null;
                }
            }

            if (j < 0)
            {
                Config.warn("Invalid minHeight, maxHeight: " + s + ", " + s1);
                return null;
            }
            else
            {
                RangeListInt rangelistint = new RangeListInt();
                rangelistint.addRange(new RangeInt(i, j));
                return rangelistint;
            }
        }
    }

    public boolean isValid(String path)
    {
        if (this.textures != null && this.textures.length != 0)
        {
            if (this.resourceLocations != null)
            {
                return true;
            }
            else
            {
                this.resourceLocations = new ResourceLocation[this.textures.length];
                boolean flag = this.pathProps.startsWith("optifine/mob/");
                ResourceLocation resourcelocation = RandomEntities.getLocationRandom(this.baseResLoc, flag);

                if (resourcelocation == null)
                {
                    Config.warn("Invalid path: " + this.baseResLoc.getPath());
                    return false;
                }
                else
                {
                    for (int i = 0; i < this.resourceLocations.length; ++i)
                    {
                        int j = this.textures[i];

                        if (j <= 1)
                        {
                            this.resourceLocations[i] = this.baseResLoc;
                        }
                        else
                        {
                            ResourceLocation resourcelocation1 = RandomEntities.getLocationIndexed(resourcelocation, j);

                            if (resourcelocation1 == null)
                            {
                                Config.warn("Invalid path: " + this.baseResLoc.getPath());
                                return false;
                            }

                            if (!Config.hasResource(resourcelocation1))
                            {
                                Config.warn("Texture not found: " + resourcelocation1.getPath());
                                return false;
                            }

                            this.resourceLocations[i] = resourcelocation1;
                        }
                    }

                    if (this.weights != null)
                    {
                        if (this.weights.length > this.resourceLocations.length)
                        {
                            Config.warn("More weights defined than skins, trimming weights: " + path);
                            int[] aint = new int[this.resourceLocations.length];
                            System.arraycopy(this.weights, 0, aint, 0, aint.length);
                            this.weights = aint;
                        }

                        if (this.weights.length < this.resourceLocations.length)
                        {
                            Config.warn("Less weights defined than skins, expanding weights: " + path);
                            int[] aint1 = new int[this.resourceLocations.length];
                            System.arraycopy(this.weights, 0, aint1, 0, this.weights.length);
                            int l = MathUtils.getAverage(this.weights);

                            for (int j1 = this.weights.length; j1 < aint1.length; ++j1)
                            {
                                aint1[j1] = l;
                            }

                            this.weights = aint1;
                        }

                        this.sumWeights = new int[this.weights.length];
                        int k = 0;

                        for (int i1 = 0; i1 < this.weights.length; ++i1)
                        {
                            if (this.weights[i1] < 0)
                            {
                                Config.warn("Invalid weight: " + this.weights[i1]);
                                return false;
                            }

                            k += this.weights[i1];
                            this.sumWeights[i1] = k;
                        }

                        this.sumAllWeights = k;

                        if (this.sumAllWeights <= 0)
                        {
                            Config.warn("Invalid sum of all weights: " + k);
                            this.sumAllWeights = 1;
                        }
                    }

                    if (this.professions == ConnectedParser.PROFESSIONS_INVALID)
                    {
                        Config.warn("Invalid professions or careers: " + path);
                        return false;
                    }
                    else if (this.collarColors == ConnectedParser.DYE_COLORS_INVALID)
                    {
                        Config.warn("Invalid collar colors: " + path);
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }
            }
        }
        else
        {
            Config.warn("Invalid skins for rule: " + this.index);
            return false;
        }
    }

    public boolean matches(IRandomEntity randomEntity)
    {
        if (this.biomes != null && !Matches.biome(randomEntity.getSpawnBiome(), this.biomes))
        {
            return false;
        }
        else
        {
            if (this.heights != null)
            {
                BlockPos blockpos = randomEntity.getSpawnPosition();

                if (blockpos != null && !this.heights.isInRange(blockpos.getY()))
                {
                    return false;
                }
            }

            if (this.healthRange != null)
            {
                int k = randomEntity.getHealth();

                if (this.healthPercent)
                {
                    int i = randomEntity.getMaxHealth();

                    if (i > 0)
                    {
                        k = (int)((double)(k * 100) / (double)i);
                    }
                }

                if (!this.healthRange.isInRange(k))
                {
                    return false;
                }
            }

            if (this.nbtName != null)
            {
                String s = randomEntity.getName();

                if (!this.nbtName.matchesValue(s))
                {
                    return false;
                }
            }

            if (this.professions != null && randomEntity instanceof RandomEntity)
            {
                RandomEntity randomentity = (RandomEntity)randomEntity;
                Entity entity = randomentity.getEntity();

                if (entity instanceof VillagerEntity)
                {
                    VillagerEntity villagerentity = (VillagerEntity)entity;
                    VillagerData villagerdata = villagerentity.getVillagerData();
                    VillagerProfession villagerprofession = villagerdata.getProfession();
                    int j = villagerdata.getLevel();

                    if (!MatchProfession.matchesOne(villagerprofession, j, this.professions))
                    {
                        return false;
                    }
                }
            }

            if (this.collarColors != null && randomEntity instanceof RandomEntity)
            {
                RandomEntity randomentity1 = (RandomEntity)randomEntity;
                Entity entity1 = randomentity1.getEntity();

                if (entity1 instanceof WolfEntity)
                {
                    WolfEntity wolfentity = (WolfEntity)entity1;

                    if (!wolfentity.isTamed())
                    {
                        return false;
                    }

                    DyeColor dyecolor = wolfentity.getCollarColor();

                    if (!Config.equalsOne(dyecolor, this.collarColors))
                    {
                        return false;
                    }
                }

                if (entity1 instanceof CatEntity)
                {
                    CatEntity catentity = (CatEntity)entity1;

                    if (!catentity.isTamed())
                    {
                        return false;
                    }

                    DyeColor dyecolor1 = catentity.getCollarColor();

                    if (!Config.equalsOne(dyecolor1, this.collarColors))
                    {
                        return false;
                    }
                }
            }

            if (this.baby != null && randomEntity instanceof RandomEntity)
            {
                RandomEntity randomentity2 = (RandomEntity)randomEntity;
                Entity entity2 = randomentity2.getEntity();

                if (entity2 instanceof LivingEntity)
                {
                    LivingEntity livingentity = (LivingEntity)entity2;

                    if (livingentity.isChild() != this.baby)
                    {
                        return false;
                    }
                }
            }

            if (this.moonPhases != null)
            {
                World world = Config.getMinecraft().world;

                if (world != null)
                {
                    int l = world.getMoonPhase();

                    if (!this.moonPhases.isInRange(l))
                    {
                        return false;
                    }
                }
            }

            if (this.dayTimes != null)
            {
                World world1 = Config.getMinecraft().world;

                if (world1 != null)
                {
                    int i1 = (int)world1.getDayTime();

                    if (!this.dayTimes.isInRange(i1))
                    {
                        return false;
                    }
                }
            }

            if (this.weatherList != null)
            {
                World world2 = Config.getMinecraft().world;

                if (world2 != null)
                {
                    Weather weather = Weather.getWeather(world2, 0.0F);

                    if (!ArrayUtils.contains(this.weatherList, weather))
                    {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    public ResourceLocation getTextureLocation(ResourceLocation loc, int randomId)
    {
        if (this.resourceLocations != null && this.resourceLocations.length != 0)
        {
            int i = 0;

            if (this.weights == null)
            {
                i = randomId % this.resourceLocations.length;
            }
            else
            {
                int j = randomId % this.sumAllWeights;

                for (int k = 0; k < this.sumWeights.length; ++k)
                {
                    if (this.sumWeights[k] > j)
                    {
                        i = k;
                        break;
                    }
                }
            }

            return this.resourceLocations[i];
        }
        else
        {
            return loc;
        }
    }
}
