package net.minecraft.advancements.criterion;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameType;

public class PlayerPredicate
{
    public static final PlayerPredicate ANY = (new PlayerPredicate.Default()).create();
    private final MinMaxBounds.IntBound level;
    private final GameType gamemode;
    private final Map < Stat<?>, MinMaxBounds.IntBound > stats;
    private final Object2BooleanMap<ResourceLocation> recipes;
    private final Map<ResourceLocation, PlayerPredicate.IAdvancementPredicate> advancements;

    private static PlayerPredicate.IAdvancementPredicate deserializeAdvancementPredicate(JsonElement element)
    {
        if (element.isJsonPrimitive())
        {
            boolean flag = element.getAsBoolean();
            return new PlayerPredicate.CompletedAdvancementPredicate(flag);
        }
        else
        {
            Object2BooleanMap<String> object2booleanmap = new Object2BooleanOpenHashMap<>();
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "criterion data");
            jsonobject.entrySet().forEach((criterionEntry) ->
            {
                boolean flag1 = JSONUtils.getBoolean(criterionEntry.getValue(), "criterion test");
                object2booleanmap.put(criterionEntry.getKey(), flag1);
            });
            return new PlayerPredicate.CriteriaPredicate(object2booleanmap);
        }
    }

    private PlayerPredicate(MinMaxBounds.IntBound level, GameType gamemode, Map < Stat<?>, MinMaxBounds.IntBound > stats, Object2BooleanMap<ResourceLocation> recipes, Map<ResourceLocation, PlayerPredicate.IAdvancementPredicate> advancements)
    {
        this.level = level;
        this.gamemode = gamemode;
        this.stats = stats;
        this.recipes = recipes;
        this.advancements = advancements;
    }

    public boolean test(Entity player)
    {
        if (this == ANY)
        {
            return true;
        }
        else if (!(player instanceof ServerPlayerEntity))
        {
            return false;
        }
        else
        {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)player;

            if (!this.level.test(serverplayerentity.experienceLevel))
            {
                return false;
            }
            else if (this.gamemode != GameType.NOT_SET && this.gamemode != serverplayerentity.interactionManager.getGameType())
            {
                return false;
            }
            else
            {
                StatisticsManager statisticsmanager = serverplayerentity.getStats();

                for (Entry < Stat<?>, MinMaxBounds.IntBound > entry : this.stats.entrySet())
                {
                    int i = statisticsmanager.getValue(entry.getKey());

                    if (!entry.getValue().test(i))
                    {
                        return false;
                    }
                }

                RecipeBook recipebook = serverplayerentity.getRecipeBook();

                for (it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry<ResourceLocation> entry2 : this.recipes.object2BooleanEntrySet())
                {
                    if (recipebook.isUnlocked(entry2.getKey()) != entry2.getBooleanValue())
                    {
                        return false;
                    }
                }

                if (!this.advancements.isEmpty())
                {
                    PlayerAdvancements playeradvancements = serverplayerentity.getAdvancements();
                    AdvancementManager advancementmanager = serverplayerentity.getServer().getAdvancementManager();

                    for (Entry<ResourceLocation, PlayerPredicate.IAdvancementPredicate> entry1 : this.advancements.entrySet())
                    {
                        Advancement advancement = advancementmanager.getAdvancement(entry1.getKey());

                        if (advancement == null || !entry1.getValue().test(playeradvancements.getProgress(advancement)))
                        {
                            return false;
                        }
                    }
                }

                return true;
            }
        }
    }

    public static PlayerPredicate deserialize(@Nullable JsonElement element)
    {
        if (element != null && !element.isJsonNull())
        {
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "player");
            MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(jsonobject.get("level"));
            String s = JSONUtils.getString(jsonobject, "gamemode", "");
            GameType gametype = GameType.parseGameTypeWithDefault(s, GameType.NOT_SET);
            Map < Stat<?>, MinMaxBounds.IntBound > map = Maps.newHashMap();
            JsonArray jsonarray = JSONUtils.getJsonArray(jsonobject, "stats", (JsonArray)null);

            if (jsonarray != null)
            {
                for (JsonElement jsonelement : jsonarray)
                {
                    JsonObject jsonobject1 = JSONUtils.getJsonObject(jsonelement, "stats entry");
                    ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonobject1, "type"));
                    StatType<?> stattype = Registry.STATS.getOrDefault(resourcelocation);

                    if (stattype == null)
                    {
                        throw new JsonParseException("Invalid stat type: " + resourcelocation);
                    }

                    ResourceLocation resourcelocation1 = new ResourceLocation(JSONUtils.getString(jsonobject1, "stat"));
                    Stat<?> stat = getStat(stattype, resourcelocation1);
                    MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(jsonobject1.get("value"));
                    map.put(stat, minmaxbounds$intbound1);
                }
            }

            Object2BooleanMap<ResourceLocation> object2booleanmap = new Object2BooleanOpenHashMap<>();
            JsonObject jsonobject2 = JSONUtils.getJsonObject(jsonobject, "recipes", new JsonObject());

            for (Entry<String, JsonElement> entry : jsonobject2.entrySet())
            {
                ResourceLocation resourcelocation2 = new ResourceLocation(entry.getKey());
                boolean flag = JSONUtils.getBoolean(entry.getValue(), "recipe present");
                object2booleanmap.put(resourcelocation2, flag);
            }

            Map<ResourceLocation, PlayerPredicate.IAdvancementPredicate> map1 = Maps.newHashMap();
            JsonObject jsonobject3 = JSONUtils.getJsonObject(jsonobject, "advancements", new JsonObject());

            for (Entry<String, JsonElement> entry1 : jsonobject3.entrySet())
            {
                ResourceLocation resourcelocation3 = new ResourceLocation(entry1.getKey());
                PlayerPredicate.IAdvancementPredicate playerpredicate$iadvancementpredicate = deserializeAdvancementPredicate(entry1.getValue());
                map1.put(resourcelocation3, playerpredicate$iadvancementpredicate);
            }

            return new PlayerPredicate(minmaxbounds$intbound, gametype, map, object2booleanmap, map1);
        }
        else
        {
            return ANY;
        }
    }

    private static <T> Stat<T> getStat(StatType<T> type, ResourceLocation identifier)
    {
        Registry<T> registry = type.getRegistry();
        T t = registry.getOrDefault(identifier);

        if (t == null)
        {
            throw new JsonParseException("Unknown object " + identifier + " for stat type " + Registry.STATS.getKey(type));
        }
        else
        {
            return type.get(t);
        }
    }

    private static <T> ResourceLocation getRegistryKeyForStat(Stat<T> stat)
    {
        return stat.getType().getRegistry().getKey(stat.getValue());
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
            jsonobject.add("level", this.level.serialize());

            if (this.gamemode != GameType.NOT_SET)
            {
                jsonobject.addProperty("gamemode", this.gamemode.getName());
            }

            if (!this.stats.isEmpty())
            {
                JsonArray jsonarray = new JsonArray();
                this.stats.forEach((stat, value) ->
                {
                    JsonObject jsonobject3 = new JsonObject();
                    jsonobject3.addProperty("type", Registry.STATS.getKey(stat.getType()).toString());
                    jsonobject3.addProperty("stat", getRegistryKeyForStat(stat).toString());
                    jsonobject3.add("value", value.serialize());
                    jsonarray.add(jsonobject3);
                });
                jsonobject.add("stats", jsonarray);
            }

            if (!this.recipes.isEmpty())
            {
                JsonObject jsonobject1 = new JsonObject();
                this.recipes.forEach((recipeID, unlocked) ->
                {
                    jsonobject1.addProperty(recipeID.toString(), unlocked);
                });
                jsonobject.add("recipes", jsonobject1);
            }

            if (!this.advancements.isEmpty())
            {
                JsonObject jsonobject2 = new JsonObject();
                this.advancements.forEach((advancementID, playerAdvancements) ->
                {
                    jsonobject2.add(advancementID.toString(), playerAdvancements.serialize());
                });
                jsonobject.add("advancements", jsonobject2);
            }

            return jsonobject;
        }
    }

    static class CompletedAdvancementPredicate implements PlayerPredicate.IAdvancementPredicate
    {
        private final boolean completion;

        public CompletedAdvancementPredicate(boolean completion)
        {
            this.completion = completion;
        }

        public JsonElement serialize()
        {
            return new JsonPrimitive(this.completion);
        }

        public boolean test(AdvancementProgress p_test_1_)
        {
            return p_test_1_.isDone() == this.completion;
        }
    }

    static class CriteriaPredicate implements PlayerPredicate.IAdvancementPredicate
    {
        private final Object2BooleanMap<String> completion;

        public CriteriaPredicate(Object2BooleanMap<String> completion)
        {
            this.completion = completion;
        }

        public JsonElement serialize()
        {
            JsonObject jsonobject = new JsonObject();
            this.completion.forEach(jsonobject::addProperty);
            return jsonobject;
        }

        public boolean test(AdvancementProgress p_test_1_)
        {
            for (it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry<String> entry : this.completion.object2BooleanEntrySet())
            {
                CriterionProgress criterionprogress = p_test_1_.getCriterionProgress(entry.getKey());

                if (criterionprogress == null || criterionprogress.isObtained() != entry.getBooleanValue())
                {
                    return false;
                }
            }

            return true;
        }
    }

    public static class Default
    {
        private MinMaxBounds.IntBound level = MinMaxBounds.IntBound.UNBOUNDED;
        private GameType gameType = GameType.NOT_SET;
        private final Map < Stat<?>, MinMaxBounds.IntBound > statValues = Maps.newHashMap();
        private final Object2BooleanMap<ResourceLocation> recipes = new Object2BooleanOpenHashMap<>();
        private final Map<ResourceLocation, PlayerPredicate.IAdvancementPredicate> advancements = Maps.newHashMap();

        public PlayerPredicate create()
        {
            return new PlayerPredicate(this.level, this.gameType, this.statValues, this.recipes, this.advancements);
        }
    }

    interface IAdvancementPredicate extends Predicate<AdvancementProgress>
    {
        JsonElement serialize();
    }
}
