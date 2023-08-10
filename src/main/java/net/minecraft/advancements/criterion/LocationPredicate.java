package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.CampfireBlock;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocationPredicate
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final LocationPredicate ANY = new LocationPredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, (RegistryKey<Biome>)null, (Structure<?>)null, (RegistryKey<World>)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    private final MinMaxBounds.FloatBound x;
    private final MinMaxBounds.FloatBound y;
    private final MinMaxBounds.FloatBound z;
    @Nullable
    private final RegistryKey<Biome> biome;
    @Nullable
    private final Structure<?> feature;
    @Nullable
    private final RegistryKey<World> dimension;
    @Nullable
    private final Boolean smokey;
    private final LightPredicate light;
    private final BlockPredicate block;
    private final FluidPredicate fluid;

    public LocationPredicate(MinMaxBounds.FloatBound x, MinMaxBounds.FloatBound y, MinMaxBounds.FloatBound z, @Nullable RegistryKey<Biome> biome, @Nullable Structure<?> feature, @Nullable RegistryKey<World> dimension, @Nullable Boolean smokey, LightPredicate light, BlockPredicate block, FluidPredicate fluid)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.biome = biome;
        this.feature = feature;
        this.dimension = dimension;
        this.smokey = smokey;
        this.light = light;
        this.block = block;
        this.fluid = fluid;
    }

    public static LocationPredicate forBiome(RegistryKey<Biome> biome)
    {
        return new LocationPredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, biome, (Structure<?>)null, (RegistryKey<World>)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public static LocationPredicate forRegistryKey(RegistryKey<World> dimension)
    {
        return new LocationPredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, (RegistryKey<Biome>)null, (Structure<?>)null, dimension, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public static LocationPredicate forFeature(Structure<?> feature)
    {
        return new LocationPredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, (RegistryKey<Biome>)null, feature, (RegistryKey<World>)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public boolean test(ServerWorld world, double x, double y, double z)
    {
        return this.test(world, (float)x, (float)y, (float)z);
    }

    public boolean test(ServerWorld world, float x, float y, float z)
    {
        if (!this.x.test(x))
        {
            return false;
        }
        else if (!this.y.test(y))
        {
            return false;
        }
        else if (!this.z.test(z))
        {
            return false;
        }
        else if (this.dimension != null && this.dimension != world.getDimensionKey())
        {
            return false;
        }
        else
        {
            BlockPos blockpos = new BlockPos((double)x, (double)y, (double)z);
            boolean flag = world.isBlockPresent(blockpos);
            Optional<RegistryKey<Biome>> optional = world.func_241828_r().getRegistry(Registry.BIOME_KEY).getOptionalKey(world.getBiome(blockpos));

            if (!optional.isPresent())
            {
                return false;
            }
            else if (this.biome == null || flag && this.biome == optional.get())
            {
                if (this.feature == null || flag && world.func_241112_a_().func_235010_a_(blockpos, true, this.feature).isValid())
                {
                    if (this.smokey == null || flag && this.smokey == CampfireBlock.isSmokingBlockAt(world, blockpos))
                    {
                        if (!this.light.test(world, blockpos))
                        {
                            return false;
                        }
                        else if (!this.block.test(world, blockpos))
                        {
                            return false;
                        }
                        else
                        {
                            return this.fluid.test(world, blockpos);
                        }
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
    }

    public JsonElement serialize()
    {
        if (this == ANY)
        {
            return JsonNull.INSTANCE;
        }
        else
        {
            JsonObject jsonobject = new JsonObject();

            if (!this.x.isUnbounded() || !this.y.isUnbounded() || !this.z.isUnbounded())
            {
                JsonObject jsonobject1 = new JsonObject();
                jsonobject1.add("x", this.x.serialize());
                jsonobject1.add("y", this.y.serialize());
                jsonobject1.add("z", this.z.serialize());
                jsonobject.add("position", jsonobject1);
            }

            if (this.dimension != null)
            {
                World.CODEC.encodeStart(JsonOps.INSTANCE, this.dimension).resultOrPartial(LOGGER::error).ifPresent((dimensionID) ->
                {
                    jsonobject.add("dimension", dimensionID);
                });
            }

            if (this.feature != null)
            {
                jsonobject.addProperty("feature", this.feature.getStructureName());
            }

            if (this.biome != null)
            {
                jsonobject.addProperty("biome", this.biome.getLocation().toString());
            }

            if (this.smokey != null)
            {
                jsonobject.addProperty("smokey", this.smokey);
            }

            jsonobject.add("light", this.light.serialize());
            jsonobject.add("block", this.block.serialize());
            jsonobject.add("fluid", this.fluid.serialize());
            return jsonobject;
        }
    }

    public static LocationPredicate deserialize(@Nullable JsonElement element)
    {
        if (element != null && !element.isJsonNull())
        {
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "location");
            JsonObject jsonobject1 = JSONUtils.getJsonObject(jsonobject, "position", new JsonObject());
            MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.fromJson(jsonobject1.get("x"));
            MinMaxBounds.FloatBound minmaxbounds$floatbound1 = MinMaxBounds.FloatBound.fromJson(jsonobject1.get("y"));
            MinMaxBounds.FloatBound minmaxbounds$floatbound2 = MinMaxBounds.FloatBound.fromJson(jsonobject1.get("z"));
            RegistryKey<World> registrykey = jsonobject.has("dimension") ? ResourceLocation.CODEC.parse(JsonOps.INSTANCE, jsonobject.get("dimension")).resultOrPartial(LOGGER::error).map((dimensionKey) ->
            {
                return RegistryKey.getOrCreateKey(Registry.WORLD_KEY, dimensionKey);
            }).orElse((RegistryKey<World>)null) : null;
            Structure<?> structure = jsonobject.has("feature") ? Structure.field_236365_a_.get(JSONUtils.getString(jsonobject, "feature")) : null;
            RegistryKey<Biome> registrykey1 = null;

            if (jsonobject.has("biome"))
            {
                ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonobject, "biome"));
                registrykey1 = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, resourcelocation);
            }

            Boolean obool = jsonobject.has("smokey") ? jsonobject.get("smokey").getAsBoolean() : null;
            LightPredicate lightpredicate = LightPredicate.deserialize(jsonobject.get("light"));
            BlockPredicate blockpredicate = BlockPredicate.deserialize(jsonobject.get("block"));
            FluidPredicate fluidpredicate = FluidPredicate.deserialize(jsonobject.get("fluid"));
            return new LocationPredicate(minmaxbounds$floatbound, minmaxbounds$floatbound1, minmaxbounds$floatbound2, registrykey1, structure, registrykey, obool, lightpredicate, blockpredicate, fluidpredicate);
        }
        else
        {
            return ANY;
        }
    }

    public static class Builder
    {
        private MinMaxBounds.FloatBound x = MinMaxBounds.FloatBound.UNBOUNDED;
        private MinMaxBounds.FloatBound y = MinMaxBounds.FloatBound.UNBOUNDED;
        private MinMaxBounds.FloatBound z = MinMaxBounds.FloatBound.UNBOUNDED;
        @Nullable
        private RegistryKey<Biome> biome;
        @Nullable
        private Structure<?> feature;
        @Nullable
        private RegistryKey<World> dimension;
        @Nullable
        private Boolean smokey;
        private LightPredicate light = LightPredicate.ANY;
        private BlockPredicate block = BlockPredicate.ANY;
        private FluidPredicate fluid = FluidPredicate.ANY;

        public static LocationPredicate.Builder builder()
        {
            return new LocationPredicate.Builder();
        }

        public LocationPredicate.Builder biome(@Nullable RegistryKey<Biome> biome)
        {
            this.biome = biome;
            return this;
        }

        public LocationPredicate.Builder block(BlockPredicate block)
        {
            this.block = block;
            return this;
        }

        public LocationPredicate.Builder smokey(Boolean smokey)
        {
            this.smokey = smokey;
            return this;
        }

        public LocationPredicate build()
        {
            return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension, this.smokey, this.light, this.block, this.fluid);
        }
    }
}
