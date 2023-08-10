package net.minecraft.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeManager extends JsonReloadListener
{
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogManager.getLogger();
    private Map < IRecipeType<?>, Map < ResourceLocation, IRecipe<? >>> recipes = ImmutableMap.of();
    private boolean someRecipesErrored;

    public RecipeManager()
    {
        super(GSON, "recipes");
    }

    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        this.someRecipesErrored = false;
        Map < IRecipeType<?>, Builder < ResourceLocation, IRecipe<? >>> map = Maps.newHashMap();

        for (Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet())
        {
            ResourceLocation resourcelocation = entry.getKey();

            try
            {
                IRecipe<?> irecipe = deserializeRecipe(resourcelocation, JSONUtils.getJsonObject(entry.getValue(), "top element"));
                map.computeIfAbsent(irecipe.getType(), (recipeType) ->
                {
                    return ImmutableMap.builder();
                }).put(resourcelocation, irecipe);
            }
            catch (IllegalArgumentException | JsonParseException jsonparseexception)
            {
                LOGGER.error("Parsing error loading recipe {}", resourcelocation, jsonparseexception);
            }
        }

        this.recipes = map.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (recipeEntry) ->
        {
            return recipeEntry.getValue().build();
        }));
        LOGGER.info("Loaded {} recipes", (int)map.size());
    }

    public <C extends IInventory, T extends IRecipe<C>> Optional<T> getRecipe(IRecipeType<T> recipeTypeIn, C inventoryIn, World worldIn)
    {
        return this.getRecipes(recipeTypeIn).values().stream().flatMap((recipe) ->
        {
            return Util.streamOptional(recipeTypeIn.matches(recipe, worldIn, inventoryIn));
        }).findFirst();
    }

    public <C extends IInventory, T extends IRecipe<C>> List<T> getRecipesForType(IRecipeType<T> recipeType)
    {
        return this.getRecipes(recipeType).values().stream().map((recipe) ->
        {
            return (T) recipe;
        }).collect(Collectors.toList());
    }

    public <C extends IInventory, T extends IRecipe<C>> List<T> getRecipes(IRecipeType<T> recipeTypeIn, C inventoryIn, World worldIn)
    {
        return this.getRecipes(recipeTypeIn).values().stream().flatMap((recipe) ->
        {
            return Util.streamOptional(recipeTypeIn.matches(recipe, worldIn, inventoryIn));
        }).sorted(Comparator.comparing((recipe) ->
        {
            return recipe.getRecipeOutput().getTranslationKey();
        })).collect(Collectors.toList());
    }

    private <C extends IInventory, T extends IRecipe<C>> Map<ResourceLocation, IRecipe<C>> getRecipes(IRecipeType<T> recipeTypeIn)
    {
        return (Map)this.recipes.getOrDefault(recipeTypeIn, Collections.emptyMap());
    }

    public <C extends IInventory, T extends IRecipe<C>> NonNullList<ItemStack> getRecipeNonNull(IRecipeType<T> recipeTypeIn, C inventoryIn, World worldIn)
    {
        Optional<T> optional = this.getRecipe(recipeTypeIn, inventoryIn, worldIn);

        if (optional.isPresent())
        {
            return optional.get().getRemainingItems(inventoryIn);
        }
        else
        {
            NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inventoryIn.getSizeInventory(), ItemStack.EMPTY);

            for (int i = 0; i < nonnulllist.size(); ++i)
            {
                nonnulllist.set(i, inventoryIn.getStackInSlot(i));
            }

            return nonnulllist;
        }
    }

    public Optional <? extends IRecipe<? >> getRecipe(ResourceLocation recipeId)
    {
        return this.recipes.values().stream().map((recipeMap) ->
        {
            return recipeMap.get(recipeId);
        }).filter(Objects::nonNull).findFirst();
    }

    public Collection < IRecipe<? >> getRecipes()
    {
        return this.recipes.values().stream().flatMap((recipeMap) ->
        {
            return recipeMap.values().stream();
        }).collect(Collectors.toSet());
    }

    public Stream<ResourceLocation> getKeys()
    {
        return this.recipes.values().stream().flatMap((recipeMap) ->
        {
            return recipeMap.keySet().stream();
        });
    }

    public static IRecipe<?> deserializeRecipe(ResourceLocation recipeId, JsonObject json)
    {
        String s = JSONUtils.getString(json, "type");
        return Registry.RECIPE_SERIALIZER.getOptional(new ResourceLocation(s)).orElseThrow(() ->
        {
            return new JsonSyntaxException("Invalid or unsupported recipe type '" + s + "'");
        }).read(recipeId, json);
    }

    public void deserializeRecipes(Iterable < IRecipe<? >> recipes)
    {
        this.someRecipesErrored = false;
        Map < IRecipeType<?>, Map < ResourceLocation, IRecipe<? >>> map = Maps.newHashMap();
        recipes.forEach((recipe) ->
        {
            Map < ResourceLocation, IRecipe<? >> map1 = map.computeIfAbsent(recipe.getType(), (recipeType) -> {
                return Maps.newHashMap();
            });
            IRecipe<?> irecipe = map1.put(recipe.getId(), recipe);

            if (irecipe != null)
            {
                throw new IllegalStateException("Duplicate recipe ignored with ID " + recipe.getId());
            }
        });
        this.recipes = ImmutableMap.copyOf(map);
    }
}
