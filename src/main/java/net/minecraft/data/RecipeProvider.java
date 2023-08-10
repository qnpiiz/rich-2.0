package net.minecraft.data;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.criterion.EnterBlockTrigger;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.CookingRecipeSerializer;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeProvider implements IDataProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;

    public RecipeProvider(DataGenerator generatorIn)
    {
        this.generator = generatorIn;
    }

    /**
     * Performs this provider's action.
     */
    public void act(DirectoryCache cache) throws IOException
    {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        registerRecipes((recipe) ->
        {
            if (!set.add(recipe.getID()))
            {
                throw new IllegalStateException("Duplicate recipe " + recipe.getID());
            }
            else {
                saveRecipe(cache, recipe.getRecipeJson(), path.resolve("data/" + recipe.getID().getNamespace() + "/recipes/" + recipe.getID().getPath() + ".json"));
                JsonObject jsonobject = recipe.getAdvancementJson();

                if (jsonobject != null)
                {
                    saveRecipeAdvancement(cache, jsonobject, path.resolve("data/" + recipe.getID().getNamespace() + "/advancements/" + recipe.getAdvancementID().getPath() + ".json"));
                }
            }
        });
        saveRecipeAdvancement(cache, Advancement.Builder.builder().withCriterion("impossible", new ImpossibleTrigger.Instance()).serialize(), path.resolve("data/minecraft/advancements/recipes/root.json"));
    }

    /**
     * Saves a recipe to a file.
     */
    private static void saveRecipe(DirectoryCache cache, JsonObject cache2, Path recipeJson)
    {
        try
        {
            String s = GSON.toJson((JsonElement)cache2);
            String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();

            if (!Objects.equals(cache.getPreviousHash(recipeJson), s1) || !Files.exists(recipeJson))
            {
                Files.createDirectories(recipeJson.getParent());

                try (BufferedWriter bufferedwriter = Files.newBufferedWriter(recipeJson))
                {
                    bufferedwriter.write(s);
                }
            }

            cache.recordHash(recipeJson, s1);
        }
        catch (IOException ioexception)
        {
            LOGGER.error("Couldn't save recipe {}", recipeJson, ioexception);
        }
    }

    /**
     * Saves an advancement to a file.
     */
    private static void saveRecipeAdvancement(DirectoryCache cache, JsonObject cache2, Path advancementJson)
    {
        try
        {
            String s = GSON.toJson((JsonElement)cache2);
            String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();

            if (!Objects.equals(cache.getPreviousHash(advancementJson), s1) || !Files.exists(advancementJson))
            {
                Files.createDirectories(advancementJson.getParent());

                try (BufferedWriter bufferedwriter = Files.newBufferedWriter(advancementJson))
                {
                    bufferedwriter.write(s);
                }
            }

            cache.recordHash(advancementJson, s1);
        }
        catch (IOException ioexception)
        {
            LOGGER.error("Couldn't save recipe advancement {}", advancementJson, ioexception);
        }
    }

    /**
     * Registers all recipes to the given consumer.
     */
    private static void registerRecipes(Consumer<IFinishedRecipe> consumer)
    {
        shapelessPlanksNew(consumer, Blocks.ACACIA_PLANKS, ItemTags.ACACIA_LOGS);
        shapelessPlanks(consumer, Blocks.BIRCH_PLANKS, ItemTags.BIRCH_LOGS);
        shapelessPlanks(consumer, Blocks.CRIMSON_PLANKS, ItemTags.CRIMSON_STEMS);
        shapelessPlanksNew(consumer, Blocks.DARK_OAK_PLANKS, ItemTags.DARK_OAK_LOGS);
        shapelessPlanks(consumer, Blocks.JUNGLE_PLANKS, ItemTags.JUNGLE_LOGS);
        shapelessPlanks(consumer, Blocks.OAK_PLANKS, ItemTags.OAK_LOGS);
        shapelessPlanks(consumer, Blocks.SPRUCE_PLANKS, ItemTags.SPRUCE_LOGS);
        shapelessPlanks(consumer, Blocks.WARPED_PLANKS, ItemTags.WARPED_STEMS);
        shapelessStrippedToPlanks(consumer, Blocks.ACACIA_WOOD, Blocks.ACACIA_LOG);
        shapelessStrippedToPlanks(consumer, Blocks.BIRCH_WOOD, Blocks.BIRCH_LOG);
        shapelessStrippedToPlanks(consumer, Blocks.DARK_OAK_WOOD, Blocks.DARK_OAK_LOG);
        shapelessStrippedToPlanks(consumer, Blocks.JUNGLE_WOOD, Blocks.JUNGLE_LOG);
        shapelessStrippedToPlanks(consumer, Blocks.OAK_WOOD, Blocks.OAK_LOG);
        shapelessStrippedToPlanks(consumer, Blocks.SPRUCE_WOOD, Blocks.SPRUCE_LOG);
        shapelessStrippedToPlanks(consumer, Blocks.CRIMSON_HYPHAE, Blocks.CRIMSON_STEM);
        shapelessStrippedToPlanks(consumer, Blocks.WARPED_HYPHAE, Blocks.WARPED_STEM);
        shapelessStrippedToPlanks(consumer, Blocks.STRIPPED_ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG);
        shapelessStrippedToPlanks(consumer, Blocks.STRIPPED_BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG);
        shapelessStrippedToPlanks(consumer, Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG);
        shapelessStrippedToPlanks(consumer, Blocks.STRIPPED_JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG);
        shapelessStrippedToPlanks(consumer, Blocks.STRIPPED_OAK_WOOD, Blocks.STRIPPED_OAK_LOG);
        shapelessStrippedToPlanks(consumer, Blocks.STRIPPED_SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG);
        shapelessStrippedToPlanks(consumer, Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_STEM);
        shapelessStrippedToPlanks(consumer, Blocks.STRIPPED_WARPED_HYPHAE, Blocks.STRIPPED_WARPED_STEM);
        shapedBoat(consumer, Items.ACACIA_BOAT, Blocks.ACACIA_PLANKS);
        shapedBoat(consumer, Items.BIRCH_BOAT, Blocks.BIRCH_PLANKS);
        shapedBoat(consumer, Items.DARK_OAK_BOAT, Blocks.DARK_OAK_PLANKS);
        shapedBoat(consumer, Items.JUNGLE_BOAT, Blocks.JUNGLE_PLANKS);
        shapedBoat(consumer, Items.OAK_BOAT, Blocks.OAK_PLANKS);
        shapedBoat(consumer, Items.SPRUCE_BOAT, Blocks.SPRUCE_PLANKS);
        shapelessWoodenButton(consumer, Blocks.ACACIA_BUTTON, Blocks.ACACIA_PLANKS);
        shapedWoodenDoor(consumer, Blocks.ACACIA_DOOR, Blocks.ACACIA_PLANKS);
        shapedWoodenFence(consumer, Blocks.ACACIA_FENCE, Blocks.ACACIA_PLANKS);
        shapedWoodenFenceGate(consumer, Blocks.ACACIA_FENCE_GATE, Blocks.ACACIA_PLANKS);
        shapedWoodenPressurePlate(consumer, Blocks.ACACIA_PRESSURE_PLATE, Blocks.ACACIA_PLANKS);
        shapedWoodenSlab(consumer, Blocks.ACACIA_SLAB, Blocks.ACACIA_PLANKS);
        shapedWoodenStairs(consumer, Blocks.ACACIA_STAIRS, Blocks.ACACIA_PLANKS);
        shapedWoodenTrapdoor(consumer, Blocks.ACACIA_TRAPDOOR, Blocks.ACACIA_PLANKS);
        shapedSign(consumer, Blocks.ACACIA_SIGN, Blocks.ACACIA_PLANKS);
        shapelessWoodenButton(consumer, Blocks.BIRCH_BUTTON, Blocks.BIRCH_PLANKS);
        shapedWoodenDoor(consumer, Blocks.BIRCH_DOOR, Blocks.BIRCH_PLANKS);
        shapedWoodenFence(consumer, Blocks.BIRCH_FENCE, Blocks.BIRCH_PLANKS);
        shapedWoodenFenceGate(consumer, Blocks.BIRCH_FENCE_GATE, Blocks.BIRCH_PLANKS);
        shapedWoodenPressurePlate(consumer, Blocks.BIRCH_PRESSURE_PLATE, Blocks.BIRCH_PLANKS);
        shapedWoodenSlab(consumer, Blocks.BIRCH_SLAB, Blocks.BIRCH_PLANKS);
        shapedWoodenStairs(consumer, Blocks.BIRCH_STAIRS, Blocks.BIRCH_PLANKS);
        shapedWoodenTrapdoor(consumer, Blocks.BIRCH_TRAPDOOR, Blocks.BIRCH_PLANKS);
        shapedSign(consumer, Blocks.BIRCH_SIGN, Blocks.BIRCH_PLANKS);
        shapelessWoodenButton(consumer, Blocks.CRIMSON_BUTTON, Blocks.CRIMSON_PLANKS);
        shapedWoodenDoor(consumer, Blocks.CRIMSON_DOOR, Blocks.CRIMSON_PLANKS);
        shapedWoodenFence(consumer, Blocks.CRIMSON_FENCE, Blocks.CRIMSON_PLANKS);
        shapedWoodenFenceGate(consumer, Blocks.CRIMSON_FENCE_GATE, Blocks.CRIMSON_PLANKS);
        shapedWoodenPressurePlate(consumer, Blocks.CRIMSON_PRESSURE_PLATE, Blocks.CRIMSON_PLANKS);
        shapedWoodenSlab(consumer, Blocks.CRIMSON_SLAB, Blocks.CRIMSON_PLANKS);
        shapedWoodenStairs(consumer, Blocks.CRIMSON_STAIRS, Blocks.CRIMSON_PLANKS);
        shapedWoodenTrapdoor(consumer, Blocks.CRIMSON_TRAPDOOR, Blocks.CRIMSON_PLANKS);
        shapedSign(consumer, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_PLANKS);
        shapelessWoodenButton(consumer, Blocks.DARK_OAK_BUTTON, Blocks.DARK_OAK_PLANKS);
        shapedWoodenDoor(consumer, Blocks.DARK_OAK_DOOR, Blocks.DARK_OAK_PLANKS);
        shapedWoodenFence(consumer, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_PLANKS);
        shapedWoodenFenceGate(consumer, Blocks.DARK_OAK_FENCE_GATE, Blocks.DARK_OAK_PLANKS);
        shapedWoodenPressurePlate(consumer, Blocks.DARK_OAK_PRESSURE_PLATE, Blocks.DARK_OAK_PLANKS);
        shapedWoodenSlab(consumer, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_PLANKS);
        shapedWoodenStairs(consumer, Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_PLANKS);
        shapedWoodenTrapdoor(consumer, Blocks.DARK_OAK_TRAPDOOR, Blocks.DARK_OAK_PLANKS);
        shapedSign(consumer, Blocks.DARK_OAK_SIGN, Blocks.DARK_OAK_PLANKS);
        shapelessWoodenButton(consumer, Blocks.JUNGLE_BUTTON, Blocks.JUNGLE_PLANKS);
        shapedWoodenDoor(consumer, Blocks.JUNGLE_DOOR, Blocks.JUNGLE_PLANKS);
        shapedWoodenFence(consumer, Blocks.JUNGLE_FENCE, Blocks.JUNGLE_PLANKS);
        shapedWoodenFenceGate(consumer, Blocks.JUNGLE_FENCE_GATE, Blocks.JUNGLE_PLANKS);
        shapedWoodenPressurePlate(consumer, Blocks.JUNGLE_PRESSURE_PLATE, Blocks.JUNGLE_PLANKS);
        shapedWoodenSlab(consumer, Blocks.JUNGLE_SLAB, Blocks.JUNGLE_PLANKS);
        shapedWoodenStairs(consumer, Blocks.JUNGLE_STAIRS, Blocks.JUNGLE_PLANKS);
        shapedWoodenTrapdoor(consumer, Blocks.JUNGLE_TRAPDOOR, Blocks.JUNGLE_PLANKS);
        shapedSign(consumer, Blocks.JUNGLE_SIGN, Blocks.JUNGLE_PLANKS);
        shapelessWoodenButton(consumer, Blocks.OAK_BUTTON, Blocks.OAK_PLANKS);
        shapedWoodenDoor(consumer, Blocks.OAK_DOOR, Blocks.OAK_PLANKS);
        shapedWoodenFence(consumer, Blocks.OAK_FENCE, Blocks.OAK_PLANKS);
        shapedWoodenFenceGate(consumer, Blocks.OAK_FENCE_GATE, Blocks.OAK_PLANKS);
        shapedWoodenPressurePlate(consumer, Blocks.OAK_PRESSURE_PLATE, Blocks.OAK_PLANKS);
        shapedWoodenSlab(consumer, Blocks.OAK_SLAB, Blocks.OAK_PLANKS);
        shapedWoodenStairs(consumer, Blocks.OAK_STAIRS, Blocks.OAK_PLANKS);
        shapedWoodenTrapdoor(consumer, Blocks.OAK_TRAPDOOR, Blocks.OAK_PLANKS);
        shapedSign(consumer, Blocks.OAK_SIGN, Blocks.OAK_PLANKS);
        shapelessWoodenButton(consumer, Blocks.SPRUCE_BUTTON, Blocks.SPRUCE_PLANKS);
        shapedWoodenDoor(consumer, Blocks.SPRUCE_DOOR, Blocks.SPRUCE_PLANKS);
        shapedWoodenFence(consumer, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_PLANKS);
        shapedWoodenFenceGate(consumer, Blocks.SPRUCE_FENCE_GATE, Blocks.SPRUCE_PLANKS);
        shapedWoodenPressurePlate(consumer, Blocks.SPRUCE_PRESSURE_PLATE, Blocks.SPRUCE_PLANKS);
        shapedWoodenSlab(consumer, Blocks.SPRUCE_SLAB, Blocks.SPRUCE_PLANKS);
        shapedWoodenStairs(consumer, Blocks.SPRUCE_STAIRS, Blocks.SPRUCE_PLANKS);
        shapedWoodenTrapdoor(consumer, Blocks.SPRUCE_TRAPDOOR, Blocks.SPRUCE_PLANKS);
        shapedSign(consumer, Blocks.SPRUCE_SIGN, Blocks.SPRUCE_PLANKS);
        shapelessWoodenButton(consumer, Blocks.WARPED_BUTTON, Blocks.WARPED_PLANKS);
        shapedWoodenDoor(consumer, Blocks.WARPED_DOOR, Blocks.WARPED_PLANKS);
        shapedWoodenFence(consumer, Blocks.WARPED_FENCE, Blocks.WARPED_PLANKS);
        shapedWoodenFenceGate(consumer, Blocks.WARPED_FENCE_GATE, Blocks.WARPED_PLANKS);
        shapedWoodenPressurePlate(consumer, Blocks.WARPED_PRESSURE_PLATE, Blocks.WARPED_PLANKS);
        shapedWoodenSlab(consumer, Blocks.WARPED_SLAB, Blocks.WARPED_PLANKS);
        shapedWoodenStairs(consumer, Blocks.WARPED_STAIRS, Blocks.WARPED_PLANKS);
        shapedWoodenTrapdoor(consumer, Blocks.WARPED_TRAPDOOR, Blocks.WARPED_PLANKS);
        shapedSign(consumer, Blocks.WARPED_SIGN, Blocks.WARPED_PLANKS);
        shapelessColoredWool(consumer, Blocks.BLACK_WOOL, Items.BLACK_DYE);
        shapedCarpet(consumer, Blocks.BLACK_CARPET, Blocks.BLACK_WOOL);
        shapelessColoredCarpet(consumer, Blocks.BLACK_CARPET, Items.BLACK_DYE);
        shapedBed(consumer, Items.BLACK_BED, Blocks.BLACK_WOOL);
        shapedColoredBed(consumer, Items.BLACK_BED, Items.BLACK_DYE);
        shapedBanner(consumer, Items.BLACK_BANNER, Blocks.BLACK_WOOL);
        shapelessColoredWool(consumer, Blocks.BLUE_WOOL, Items.BLUE_DYE);
        shapedCarpet(consumer, Blocks.BLUE_CARPET, Blocks.BLUE_WOOL);
        shapelessColoredCarpet(consumer, Blocks.BLUE_CARPET, Items.BLUE_DYE);
        shapedBed(consumer, Items.BLUE_BED, Blocks.BLUE_WOOL);
        shapedColoredBed(consumer, Items.BLUE_BED, Items.BLUE_DYE);
        shapedBanner(consumer, Items.BLUE_BANNER, Blocks.BLUE_WOOL);
        shapelessColoredWool(consumer, Blocks.BROWN_WOOL, Items.BROWN_DYE);
        shapedCarpet(consumer, Blocks.BROWN_CARPET, Blocks.BROWN_WOOL);
        shapelessColoredCarpet(consumer, Blocks.BROWN_CARPET, Items.BROWN_DYE);
        shapedBed(consumer, Items.BROWN_BED, Blocks.BROWN_WOOL);
        shapedColoredBed(consumer, Items.BROWN_BED, Items.BROWN_DYE);
        shapedBanner(consumer, Items.BROWN_BANNER, Blocks.BROWN_WOOL);
        shapelessColoredWool(consumer, Blocks.CYAN_WOOL, Items.CYAN_DYE);
        shapedCarpet(consumer, Blocks.CYAN_CARPET, Blocks.CYAN_WOOL);
        shapelessColoredCarpet(consumer, Blocks.CYAN_CARPET, Items.CYAN_DYE);
        shapedBed(consumer, Items.CYAN_BED, Blocks.CYAN_WOOL);
        shapedColoredBed(consumer, Items.CYAN_BED, Items.CYAN_DYE);
        shapedBanner(consumer, Items.CYAN_BANNER, Blocks.CYAN_WOOL);
        shapelessColoredWool(consumer, Blocks.GRAY_WOOL, Items.GRAY_DYE);
        shapedCarpet(consumer, Blocks.GRAY_CARPET, Blocks.GRAY_WOOL);
        shapelessColoredCarpet(consumer, Blocks.GRAY_CARPET, Items.GRAY_DYE);
        shapedBed(consumer, Items.GRAY_BED, Blocks.GRAY_WOOL);
        shapedColoredBed(consumer, Items.GRAY_BED, Items.GRAY_DYE);
        shapedBanner(consumer, Items.GRAY_BANNER, Blocks.GRAY_WOOL);
        shapelessColoredWool(consumer, Blocks.GREEN_WOOL, Items.GREEN_DYE);
        shapedCarpet(consumer, Blocks.GREEN_CARPET, Blocks.GREEN_WOOL);
        shapelessColoredCarpet(consumer, Blocks.GREEN_CARPET, Items.GREEN_DYE);
        shapedBed(consumer, Items.GREEN_BED, Blocks.GREEN_WOOL);
        shapedColoredBed(consumer, Items.GREEN_BED, Items.GREEN_DYE);
        shapedBanner(consumer, Items.GREEN_BANNER, Blocks.GREEN_WOOL);
        shapelessColoredWool(consumer, Blocks.LIGHT_BLUE_WOOL, Items.LIGHT_BLUE_DYE);
        shapedCarpet(consumer, Blocks.LIGHT_BLUE_CARPET, Blocks.LIGHT_BLUE_WOOL);
        shapelessColoredCarpet(consumer, Blocks.LIGHT_BLUE_CARPET, Items.LIGHT_BLUE_DYE);
        shapedBed(consumer, Items.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
        shapedColoredBed(consumer, Items.LIGHT_BLUE_BED, Items.LIGHT_BLUE_DYE);
        shapedBanner(consumer, Items.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WOOL);
        shapelessColoredWool(consumer, Blocks.LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_DYE);
        shapedCarpet(consumer, Blocks.LIGHT_GRAY_CARPET, Blocks.LIGHT_GRAY_WOOL);
        shapelessColoredCarpet(consumer, Blocks.LIGHT_GRAY_CARPET, Items.LIGHT_GRAY_DYE);
        shapedBed(consumer, Items.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
        shapedColoredBed(consumer, Items.LIGHT_GRAY_BED, Items.LIGHT_GRAY_DYE);
        shapedBanner(consumer, Items.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WOOL);
        shapelessColoredWool(consumer, Blocks.LIME_WOOL, Items.LIME_DYE);
        shapedCarpet(consumer, Blocks.LIME_CARPET, Blocks.LIME_WOOL);
        shapelessColoredCarpet(consumer, Blocks.LIME_CARPET, Items.LIME_DYE);
        shapedBed(consumer, Items.LIME_BED, Blocks.LIME_WOOL);
        shapedColoredBed(consumer, Items.LIME_BED, Items.LIME_DYE);
        shapedBanner(consumer, Items.LIME_BANNER, Blocks.LIME_WOOL);
        shapelessColoredWool(consumer, Blocks.MAGENTA_WOOL, Items.MAGENTA_DYE);
        shapedCarpet(consumer, Blocks.MAGENTA_CARPET, Blocks.MAGENTA_WOOL);
        shapelessColoredCarpet(consumer, Blocks.MAGENTA_CARPET, Items.MAGENTA_DYE);
        shapedBed(consumer, Items.MAGENTA_BED, Blocks.MAGENTA_WOOL);
        shapedColoredBed(consumer, Items.MAGENTA_BED, Items.MAGENTA_DYE);
        shapedBanner(consumer, Items.MAGENTA_BANNER, Blocks.MAGENTA_WOOL);
        shapelessColoredWool(consumer, Blocks.ORANGE_WOOL, Items.ORANGE_DYE);
        shapedCarpet(consumer, Blocks.ORANGE_CARPET, Blocks.ORANGE_WOOL);
        shapelessColoredCarpet(consumer, Blocks.ORANGE_CARPET, Items.ORANGE_DYE);
        shapedBed(consumer, Items.ORANGE_BED, Blocks.ORANGE_WOOL);
        shapedColoredBed(consumer, Items.ORANGE_BED, Items.ORANGE_DYE);
        shapedBanner(consumer, Items.ORANGE_BANNER, Blocks.ORANGE_WOOL);
        shapelessColoredWool(consumer, Blocks.PINK_WOOL, Items.PINK_DYE);
        shapedCarpet(consumer, Blocks.PINK_CARPET, Blocks.PINK_WOOL);
        shapelessColoredCarpet(consumer, Blocks.PINK_CARPET, Items.PINK_DYE);
        shapedBed(consumer, Items.PINK_BED, Blocks.PINK_WOOL);
        shapedColoredBed(consumer, Items.PINK_BED, Items.PINK_DYE);
        shapedBanner(consumer, Items.PINK_BANNER, Blocks.PINK_WOOL);
        shapelessColoredWool(consumer, Blocks.PURPLE_WOOL, Items.PURPLE_DYE);
        shapedCarpet(consumer, Blocks.PURPLE_CARPET, Blocks.PURPLE_WOOL);
        shapelessColoredCarpet(consumer, Blocks.PURPLE_CARPET, Items.PURPLE_DYE);
        shapedBed(consumer, Items.PURPLE_BED, Blocks.PURPLE_WOOL);
        shapedColoredBed(consumer, Items.PURPLE_BED, Items.PURPLE_DYE);
        shapedBanner(consumer, Items.PURPLE_BANNER, Blocks.PURPLE_WOOL);
        shapelessColoredWool(consumer, Blocks.RED_WOOL, Items.RED_DYE);
        shapedCarpet(consumer, Blocks.RED_CARPET, Blocks.RED_WOOL);
        shapelessColoredCarpet(consumer, Blocks.RED_CARPET, Items.RED_DYE);
        shapedBed(consumer, Items.RED_BED, Blocks.RED_WOOL);
        shapedColoredBed(consumer, Items.RED_BED, Items.RED_DYE);
        shapedBanner(consumer, Items.RED_BANNER, Blocks.RED_WOOL);
        shapedCarpet(consumer, Blocks.WHITE_CARPET, Blocks.WHITE_WOOL);
        shapedBed(consumer, Items.WHITE_BED, Blocks.WHITE_WOOL);
        shapedBanner(consumer, Items.WHITE_BANNER, Blocks.WHITE_WOOL);
        shapelessColoredWool(consumer, Blocks.YELLOW_WOOL, Items.YELLOW_DYE);
        shapedCarpet(consumer, Blocks.YELLOW_CARPET, Blocks.YELLOW_WOOL);
        shapelessColoredCarpet(consumer, Blocks.YELLOW_CARPET, Items.YELLOW_DYE);
        shapedBed(consumer, Items.YELLOW_BED, Blocks.YELLOW_WOOL);
        shapedColoredBed(consumer, Items.YELLOW_BED, Items.YELLOW_DYE);
        shapedBanner(consumer, Items.YELLOW_BANNER, Blocks.YELLOW_WOOL);
        shapedColoredGlass(consumer, Blocks.BLACK_STAINED_GLASS, Items.BLACK_DYE);
        shapedGlassPane(consumer, Blocks.BLACK_STAINED_GLASS_PANE, Blocks.BLACK_STAINED_GLASS);
        shapedColoredPane(consumer, Blocks.BLACK_STAINED_GLASS_PANE, Items.BLACK_DYE);
        shapedColoredGlass(consumer, Blocks.BLUE_STAINED_GLASS, Items.BLUE_DYE);
        shapedGlassPane(consumer, Blocks.BLUE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS);
        shapedColoredPane(consumer, Blocks.BLUE_STAINED_GLASS_PANE, Items.BLUE_DYE);
        shapedColoredGlass(consumer, Blocks.BROWN_STAINED_GLASS, Items.BROWN_DYE);
        shapedGlassPane(consumer, Blocks.BROWN_STAINED_GLASS_PANE, Blocks.BROWN_STAINED_GLASS);
        shapedColoredPane(consumer, Blocks.BROWN_STAINED_GLASS_PANE, Items.BROWN_DYE);
        shapedColoredGlass(consumer, Blocks.CYAN_STAINED_GLASS, Items.CYAN_DYE);
        shapedGlassPane(consumer, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS);
        shapedColoredPane(consumer, Blocks.CYAN_STAINED_GLASS_PANE, Items.CYAN_DYE);
        shapedColoredGlass(consumer, Blocks.GRAY_STAINED_GLASS, Items.GRAY_DYE);
        shapedGlassPane(consumer, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS);
        shapedColoredPane(consumer, Blocks.GRAY_STAINED_GLASS_PANE, Items.GRAY_DYE);
        shapedColoredGlass(consumer, Blocks.GREEN_STAINED_GLASS, Items.GREEN_DYE);
        shapedGlassPane(consumer, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS);
        shapedColoredPane(consumer, Blocks.GREEN_STAINED_GLASS_PANE, Items.GREEN_DYE);
        shapedColoredGlass(consumer, Blocks.LIGHT_BLUE_STAINED_GLASS, Items.LIGHT_BLUE_DYE);
        shapedGlassPane(consumer, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS);
        shapedColoredPane(consumer, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Items.LIGHT_BLUE_DYE);
        shapedColoredGlass(consumer, Blocks.LIGHT_GRAY_STAINED_GLASS, Items.LIGHT_GRAY_DYE);
        shapedGlassPane(consumer, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS);
        shapedColoredPane(consumer, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Items.LIGHT_GRAY_DYE);
        shapedColoredGlass(consumer, Blocks.LIME_STAINED_GLASS, Items.LIME_DYE);
        shapedGlassPane(consumer, Blocks.LIME_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS);
        shapedColoredPane(consumer, Blocks.LIME_STAINED_GLASS_PANE, Items.LIME_DYE);
        shapedColoredGlass(consumer, Blocks.MAGENTA_STAINED_GLASS, Items.MAGENTA_DYE);
        shapedGlassPane(consumer, Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS);
        shapedColoredPane(consumer, Blocks.MAGENTA_STAINED_GLASS_PANE, Items.MAGENTA_DYE);
        shapedColoredGlass(consumer, Blocks.ORANGE_STAINED_GLASS, Items.ORANGE_DYE);
        shapedGlassPane(consumer, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS);
        shapedColoredPane(consumer, Blocks.ORANGE_STAINED_GLASS_PANE, Items.ORANGE_DYE);
        shapedColoredGlass(consumer, Blocks.PINK_STAINED_GLASS, Items.PINK_DYE);
        shapedGlassPane(consumer, Blocks.PINK_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS);
        shapedColoredPane(consumer, Blocks.PINK_STAINED_GLASS_PANE, Items.PINK_DYE);
        shapedColoredGlass(consumer, Blocks.PURPLE_STAINED_GLASS, Items.PURPLE_DYE);
        shapedGlassPane(consumer, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS);
        shapedColoredPane(consumer, Blocks.PURPLE_STAINED_GLASS_PANE, Items.PURPLE_DYE);
        shapedColoredGlass(consumer, Blocks.RED_STAINED_GLASS, Items.RED_DYE);
        shapedGlassPane(consumer, Blocks.RED_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS);
        shapedColoredPane(consumer, Blocks.RED_STAINED_GLASS_PANE, Items.RED_DYE);
        shapedColoredGlass(consumer, Blocks.WHITE_STAINED_GLASS, Items.WHITE_DYE);
        shapedGlassPane(consumer, Blocks.WHITE_STAINED_GLASS_PANE, Blocks.WHITE_STAINED_GLASS);
        shapedColoredPane(consumer, Blocks.WHITE_STAINED_GLASS_PANE, Items.WHITE_DYE);
        shapedColoredGlass(consumer, Blocks.YELLOW_STAINED_GLASS, Items.YELLOW_DYE);
        shapedGlassPane(consumer, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS);
        shapedColoredPane(consumer, Blocks.YELLOW_STAINED_GLASS_PANE, Items.YELLOW_DYE);
        shapedColoredTerracotta(consumer, Blocks.BLACK_TERRACOTTA, Items.BLACK_DYE);
        shapedColoredTerracotta(consumer, Blocks.BLUE_TERRACOTTA, Items.BLUE_DYE);
        shapedColoredTerracotta(consumer, Blocks.BROWN_TERRACOTTA, Items.BROWN_DYE);
        shapedColoredTerracotta(consumer, Blocks.CYAN_TERRACOTTA, Items.CYAN_DYE);
        shapedColoredTerracotta(consumer, Blocks.GRAY_TERRACOTTA, Items.GRAY_DYE);
        shapedColoredTerracotta(consumer, Blocks.GREEN_TERRACOTTA, Items.GREEN_DYE);
        shapedColoredTerracotta(consumer, Blocks.LIGHT_BLUE_TERRACOTTA, Items.LIGHT_BLUE_DYE);
        shapedColoredTerracotta(consumer, Blocks.LIGHT_GRAY_TERRACOTTA, Items.LIGHT_GRAY_DYE);
        shapedColoredTerracotta(consumer, Blocks.LIME_TERRACOTTA, Items.LIME_DYE);
        shapedColoredTerracotta(consumer, Blocks.MAGENTA_TERRACOTTA, Items.MAGENTA_DYE);
        shapedColoredTerracotta(consumer, Blocks.ORANGE_TERRACOTTA, Items.ORANGE_DYE);
        shapedColoredTerracotta(consumer, Blocks.PINK_TERRACOTTA, Items.PINK_DYE);
        shapedColoredTerracotta(consumer, Blocks.PURPLE_TERRACOTTA, Items.PURPLE_DYE);
        shapedColoredTerracotta(consumer, Blocks.RED_TERRACOTTA, Items.RED_DYE);
        shapedColoredTerracotta(consumer, Blocks.WHITE_TERRACOTTA, Items.WHITE_DYE);
        shapedColoredTerracotta(consumer, Blocks.YELLOW_TERRACOTTA, Items.YELLOW_DYE);
        shapedColorConcretePowder(consumer, Blocks.BLACK_CONCRETE_POWDER, Items.BLACK_DYE);
        shapedColorConcretePowder(consumer, Blocks.BLUE_CONCRETE_POWDER, Items.BLUE_DYE);
        shapedColorConcretePowder(consumer, Blocks.BROWN_CONCRETE_POWDER, Items.BROWN_DYE);
        shapedColorConcretePowder(consumer, Blocks.CYAN_CONCRETE_POWDER, Items.CYAN_DYE);
        shapedColorConcretePowder(consumer, Blocks.GRAY_CONCRETE_POWDER, Items.GRAY_DYE);
        shapedColorConcretePowder(consumer, Blocks.GREEN_CONCRETE_POWDER, Items.GREEN_DYE);
        shapedColorConcretePowder(consumer, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Items.LIGHT_BLUE_DYE);
        shapedColorConcretePowder(consumer, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_DYE);
        shapedColorConcretePowder(consumer, Blocks.LIME_CONCRETE_POWDER, Items.LIME_DYE);
        shapedColorConcretePowder(consumer, Blocks.MAGENTA_CONCRETE_POWDER, Items.MAGENTA_DYE);
        shapedColorConcretePowder(consumer, Blocks.ORANGE_CONCRETE_POWDER, Items.ORANGE_DYE);
        shapedColorConcretePowder(consumer, Blocks.PINK_CONCRETE_POWDER, Items.PINK_DYE);
        shapedColorConcretePowder(consumer, Blocks.PURPLE_CONCRETE_POWDER, Items.PURPLE_DYE);
        shapedColorConcretePowder(consumer, Blocks.RED_CONCRETE_POWDER, Items.RED_DYE);
        shapedColorConcretePowder(consumer, Blocks.WHITE_CONCRETE_POWDER, Items.WHITE_DYE);
        shapedColorConcretePowder(consumer, Blocks.YELLOW_CONCRETE_POWDER, Items.YELLOW_DYE);
        ShapedRecipeBuilder.shapedRecipe(Blocks.ACTIVATOR_RAIL, 6).key('#', Blocks.REDSTONE_TORCH).key('S', Items.STICK).key('X', Items.IRON_INGOT).patternLine("XSX").patternLine("X#X").patternLine("XSX").addCriterion("has_rail", hasItem(Blocks.RAIL)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Blocks.ANDESITE, 2).addIngredient(Blocks.DIORITE).addIngredient(Blocks.COBBLESTONE).addCriterion("has_stone", hasItem(Blocks.DIORITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.ANVIL).key('I', Blocks.IRON_BLOCK).key('i', Items.IRON_INGOT).patternLine("III").patternLine(" i ").patternLine("iii").addCriterion("has_iron_block", hasItem(Blocks.IRON_BLOCK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.ARMOR_STAND).key('/', Items.STICK).key('_', Blocks.SMOOTH_STONE_SLAB).patternLine("///").patternLine(" / ").patternLine("/_/").addCriterion("has_stone_slab", hasItem(Blocks.SMOOTH_STONE_SLAB)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.ARROW, 4).key('#', Items.STICK).key('X', Items.FLINT).key('Y', Items.FEATHER).patternLine("X").patternLine("#").patternLine("Y").addCriterion("has_feather", hasItem(Items.FEATHER)).addCriterion("has_flint", hasItem(Items.FLINT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.BARREL, 1).key('P', ItemTags.PLANKS).key('S', ItemTags.WOODEN_SLABS).patternLine("PSP").patternLine("P P").patternLine("PSP").addCriterion("has_planks", hasItem(ItemTags.PLANKS)).addCriterion("has_wood_slab", hasItem(ItemTags.WOODEN_SLABS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.BEACON).key('S', Items.NETHER_STAR).key('G', Blocks.GLASS).key('O', Blocks.OBSIDIAN).patternLine("GGG").patternLine("GSG").patternLine("OOO").addCriterion("has_nether_star", hasItem(Items.NETHER_STAR)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.BEEHIVE).key('P', ItemTags.PLANKS).key('H', Items.HONEYCOMB).patternLine("PPP").patternLine("HHH").patternLine("PPP").addCriterion("has_honeycomb", hasItem(Items.HONEYCOMB)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.BEETROOT_SOUP).addIngredient(Items.BOWL).addIngredient(Items.BEETROOT, 6).addCriterion("has_beetroot", hasItem(Items.BEETROOT)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.BLACK_DYE).addIngredient(Items.INK_SAC).setGroup("black_dye").addCriterion("has_ink_sac", hasItem(Items.INK_SAC)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.BLACK_DYE).addIngredient(Blocks.WITHER_ROSE).setGroup("black_dye").addCriterion("has_black_flower", hasItem(Blocks.WITHER_ROSE)).build(consumer, "black_dye_from_wither_rose");
        ShapelessRecipeBuilder.shapelessRecipe(Items.BLAZE_POWDER, 2).addIngredient(Items.BLAZE_ROD).addCriterion("has_blaze_rod", hasItem(Items.BLAZE_ROD)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.BLUE_DYE).addIngredient(Items.LAPIS_LAZULI).setGroup("blue_dye").addCriterion("has_lapis_lazuli", hasItem(Items.LAPIS_LAZULI)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.BLUE_DYE).addIngredient(Blocks.CORNFLOWER).setGroup("blue_dye").addCriterion("has_blue_flower", hasItem(Blocks.CORNFLOWER)).build(consumer, "blue_dye_from_cornflower");
        ShapedRecipeBuilder.shapedRecipe(Blocks.BLUE_ICE).key('#', Blocks.PACKED_ICE).patternLine("###").patternLine("###").patternLine("###").addCriterion("has_packed_ice", hasItem(Blocks.PACKED_ICE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.BONE_BLOCK).key('X', Items.BONE_MEAL).patternLine("XXX").patternLine("XXX").patternLine("XXX").addCriterion("has_bonemeal", hasItem(Items.BONE_MEAL)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.BONE_MEAL, 3).addIngredient(Items.BONE).setGroup("bonemeal").addCriterion("has_bone", hasItem(Items.BONE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.BONE_MEAL, 9).addIngredient(Blocks.BONE_BLOCK).setGroup("bonemeal").addCriterion("has_bone_block", hasItem(Blocks.BONE_BLOCK)).build(consumer, "bone_meal_from_bone_block");
        ShapelessRecipeBuilder.shapelessRecipe(Items.BOOK).addIngredient(Items.PAPER, 3).addIngredient(Items.LEATHER).addCriterion("has_paper", hasItem(Items.PAPER)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.BOOKSHELF).key('#', ItemTags.PLANKS).key('X', Items.BOOK).patternLine("###").patternLine("XXX").patternLine("###").addCriterion("has_book", hasItem(Items.BOOK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.BOW).key('#', Items.STICK).key('X', Items.STRING).patternLine(" #X").patternLine("# X").patternLine(" #X").addCriterion("has_string", hasItem(Items.STRING)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.BOWL, 4).key('#', ItemTags.PLANKS).patternLine("# #").patternLine(" # ").addCriterion("has_brown_mushroom", hasItem(Blocks.BROWN_MUSHROOM)).addCriterion("has_red_mushroom", hasItem(Blocks.RED_MUSHROOM)).addCriterion("has_mushroom_stew", hasItem(Items.MUSHROOM_STEW)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.BREAD).key('#', Items.WHEAT).patternLine("###").addCriterion("has_wheat", hasItem(Items.WHEAT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.BREWING_STAND).key('B', Items.BLAZE_ROD).key('#', ItemTags.STONE_CRAFTING_MATERIALS).patternLine(" B ").patternLine("###").addCriterion("has_blaze_rod", hasItem(Items.BLAZE_ROD)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.BRICKS).key('#', Items.BRICK).patternLine("##").patternLine("##").addCriterion("has_brick", hasItem(Items.BRICK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.BRICK_SLAB, 6).key('#', Blocks.BRICKS).patternLine("###").addCriterion("has_brick_block", hasItem(Blocks.BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.BRICK_STAIRS, 4).key('#', Blocks.BRICKS).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_brick_block", hasItem(Blocks.BRICKS)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.BROWN_DYE).addIngredient(Items.COCOA_BEANS).setGroup("brown_dye").addCriterion("has_cocoa_beans", hasItem(Items.COCOA_BEANS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.BUCKET).key('#', Items.IRON_INGOT).patternLine("# #").patternLine(" # ").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CAKE).key('A', Items.MILK_BUCKET).key('B', Items.SUGAR).key('C', Items.WHEAT).key('E', Items.EGG).patternLine("AAA").patternLine("BEB").patternLine("CCC").addCriterion("has_egg", hasItem(Items.EGG)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CAMPFIRE).key('L', ItemTags.LOGS).key('S', Items.STICK).key('C', ItemTags.COALS).patternLine(" S ").patternLine("SCS").patternLine("LLL").addCriterion("has_stick", hasItem(Items.STICK)).addCriterion("has_coal", hasItem(ItemTags.COALS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.CARROT_ON_A_STICK).key('#', Items.FISHING_ROD).key('X', Items.CARROT).patternLine("# ").patternLine(" X").addCriterion("has_carrot", hasItem(Items.CARROT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.WARPED_FUNGUS_ON_A_STICK).key('#', Items.FISHING_ROD).key('X', Items.WARPED_FUNGUS).patternLine("# ").patternLine(" X").addCriterion("has_warped_fungus", hasItem(Items.WARPED_FUNGUS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CAULDRON).key('#', Items.IRON_INGOT).patternLine("# #").patternLine("# #").patternLine("###").addCriterion("has_water_bucket", hasItem(Items.WATER_BUCKET)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.COMPOSTER).key('#', ItemTags.WOODEN_SLABS).patternLine("# #").patternLine("# #").patternLine("###").addCriterion("has_wood_slab", hasItem(ItemTags.WOODEN_SLABS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CHEST).key('#', ItemTags.PLANKS).patternLine("###").patternLine("# #").patternLine("###").addCriterion("has_lots_of_items", new InventoryChangeTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, MinMaxBounds.IntBound.atLeast(10), MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, new ItemPredicate[0])).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.CHEST_MINECART).key('A', Blocks.CHEST).key('B', Items.MINECART).patternLine("A").patternLine("B").addCriterion("has_minecart", hasItem(Items.MINECART)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CHISELED_NETHER_BRICKS).key('#', Blocks.NETHER_BRICK_SLAB).patternLine("#").patternLine("#").addCriterion("has_nether_bricks", hasItem(Blocks.NETHER_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CHISELED_QUARTZ_BLOCK).key('#', Blocks.QUARTZ_SLAB).patternLine("#").patternLine("#").addCriterion("has_chiseled_quartz_block", hasItem(Blocks.CHISELED_QUARTZ_BLOCK)).addCriterion("has_quartz_block", hasItem(Blocks.QUARTZ_BLOCK)).addCriterion("has_quartz_pillar", hasItem(Blocks.QUARTZ_PILLAR)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CHISELED_STONE_BRICKS).key('#', Blocks.STONE_BRICK_SLAB).patternLine("#").patternLine("#").addCriterion("has_stone_bricks", hasItem(ItemTags.STONE_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CLAY).key('#', Items.CLAY_BALL).patternLine("##").patternLine("##").addCriterion("has_clay_ball", hasItem(Items.CLAY_BALL)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.CLOCK).key('#', Items.GOLD_INGOT).key('X', Items.REDSTONE).patternLine(" # ").patternLine("#X#").patternLine(" # ").addCriterion("has_redstone", hasItem(Items.REDSTONE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.COAL, 9).addIngredient(Blocks.COAL_BLOCK).addCriterion("has_coal_block", hasItem(Blocks.COAL_BLOCK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.COAL_BLOCK).key('#', Items.COAL).patternLine("###").patternLine("###").patternLine("###").addCriterion("has_coal", hasItem(Items.COAL)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.COARSE_DIRT, 4).key('D', Blocks.DIRT).key('G', Blocks.GRAVEL).patternLine("DG").patternLine("GD").addCriterion("has_gravel", hasItem(Blocks.GRAVEL)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.COBBLESTONE_SLAB, 6).key('#', Blocks.COBBLESTONE).patternLine("###").addCriterion("has_cobblestone", hasItem(Blocks.COBBLESTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.COBBLESTONE_WALL, 6).key('#', Blocks.COBBLESTONE).patternLine("###").patternLine("###").addCriterion("has_cobblestone", hasItem(Blocks.COBBLESTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.COMPARATOR).key('#', Blocks.REDSTONE_TORCH).key('X', Items.QUARTZ).key('I', Blocks.STONE).patternLine(" # ").patternLine("#X#").patternLine("III").addCriterion("has_quartz", hasItem(Items.QUARTZ)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.COMPASS).key('#', Items.IRON_INGOT).key('X', Items.REDSTONE).patternLine(" # ").patternLine("#X#").patternLine(" # ").addCriterion("has_redstone", hasItem(Items.REDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.COOKIE, 8).key('#', Items.WHEAT).key('X', Items.COCOA_BEANS).patternLine("#X#").addCriterion("has_cocoa", hasItem(Items.COCOA_BEANS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CRAFTING_TABLE).key('#', ItemTags.PLANKS).patternLine("##").patternLine("##").addCriterion("has_planks", hasItem(ItemTags.PLANKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.CROSSBOW).key('~', Items.STRING).key('#', Items.STICK).key('&', Items.IRON_INGOT).key('$', Blocks.TRIPWIRE_HOOK).patternLine("#&#").patternLine("~$~").patternLine(" # ").addCriterion("has_string", hasItem(Items.STRING)).addCriterion("has_stick", hasItem(Items.STICK)).addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).addCriterion("has_tripwire_hook", hasItem(Blocks.TRIPWIRE_HOOK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.LOOM).key('#', ItemTags.PLANKS).key('@', Items.STRING).patternLine("@@").patternLine("##").addCriterion("has_string", hasItem(Items.STRING)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CHISELED_RED_SANDSTONE).key('#', Blocks.RED_SANDSTONE_SLAB).patternLine("#").patternLine("#").addCriterion("has_red_sandstone", hasItem(Blocks.RED_SANDSTONE)).addCriterion("has_chiseled_red_sandstone", hasItem(Blocks.CHISELED_RED_SANDSTONE)).addCriterion("has_cut_red_sandstone", hasItem(Blocks.CUT_RED_SANDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CHISELED_SANDSTONE).key('#', Blocks.SANDSTONE_SLAB).patternLine("#").patternLine("#").addCriterion("has_stone_slab", hasItem(Blocks.SANDSTONE_SLAB)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.CYAN_DYE, 2).addIngredient(Items.BLUE_DYE).addIngredient(Items.GREEN_DYE).addCriterion("has_green_dye", hasItem(Items.GREEN_DYE)).addCriterion("has_blue_dye", hasItem(Items.BLUE_DYE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.DARK_PRISMARINE).key('S', Items.PRISMARINE_SHARD).key('I', Items.BLACK_DYE).patternLine("SSS").patternLine("SIS").patternLine("SSS").addCriterion("has_prismarine_shard", hasItem(Items.PRISMARINE_SHARD)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.PRISMARINE_STAIRS, 4).key('#', Blocks.PRISMARINE).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_prismarine", hasItem(Blocks.PRISMARINE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.PRISMARINE_BRICK_STAIRS, 4).key('#', Blocks.PRISMARINE_BRICKS).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_prismarine_bricks", hasItem(Blocks.PRISMARINE_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.DARK_PRISMARINE_STAIRS, 4).key('#', Blocks.DARK_PRISMARINE).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_dark_prismarine", hasItem(Blocks.DARK_PRISMARINE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.DAYLIGHT_DETECTOR).key('Q', Items.QUARTZ).key('G', Blocks.GLASS).key('W', Ingredient.fromTag(ItemTags.WOODEN_SLABS)).patternLine("GGG").patternLine("QQQ").patternLine("WWW").addCriterion("has_quartz", hasItem(Items.QUARTZ)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.DETECTOR_RAIL, 6).key('R', Items.REDSTONE).key('#', Blocks.STONE_PRESSURE_PLATE).key('X', Items.IRON_INGOT).patternLine("X X").patternLine("X#X").patternLine("XRX").addCriterion("has_rail", hasItem(Blocks.RAIL)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.DIAMOND, 9).addIngredient(Blocks.DIAMOND_BLOCK).addCriterion("has_diamond_block", hasItem(Blocks.DIAMOND_BLOCK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.DIAMOND_AXE).key('#', Items.STICK).key('X', Items.DIAMOND).patternLine("XX").patternLine("X#").patternLine(" #").addCriterion("has_diamond", hasItem(Items.DIAMOND)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.DIAMOND_BLOCK).key('#', Items.DIAMOND).patternLine("###").patternLine("###").patternLine("###").addCriterion("has_diamond", hasItem(Items.DIAMOND)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.DIAMOND_BOOTS).key('X', Items.DIAMOND).patternLine("X X").patternLine("X X").addCriterion("has_diamond", hasItem(Items.DIAMOND)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.DIAMOND_CHESTPLATE).key('X', Items.DIAMOND).patternLine("X X").patternLine("XXX").patternLine("XXX").addCriterion("has_diamond", hasItem(Items.DIAMOND)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.DIAMOND_HELMET).key('X', Items.DIAMOND).patternLine("XXX").patternLine("X X").addCriterion("has_diamond", hasItem(Items.DIAMOND)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.DIAMOND_HOE).key('#', Items.STICK).key('X', Items.DIAMOND).patternLine("XX").patternLine(" #").patternLine(" #").addCriterion("has_diamond", hasItem(Items.DIAMOND)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.DIAMOND_LEGGINGS).key('X', Items.DIAMOND).patternLine("XXX").patternLine("X X").patternLine("X X").addCriterion("has_diamond", hasItem(Items.DIAMOND)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.DIAMOND_PICKAXE).key('#', Items.STICK).key('X', Items.DIAMOND).patternLine("XXX").patternLine(" # ").patternLine(" # ").addCriterion("has_diamond", hasItem(Items.DIAMOND)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.DIAMOND_SHOVEL).key('#', Items.STICK).key('X', Items.DIAMOND).patternLine("X").patternLine("#").patternLine("#").addCriterion("has_diamond", hasItem(Items.DIAMOND)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.DIAMOND_SWORD).key('#', Items.STICK).key('X', Items.DIAMOND).patternLine("X").patternLine("X").patternLine("#").addCriterion("has_diamond", hasItem(Items.DIAMOND)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.DIORITE, 2).key('Q', Items.QUARTZ).key('C', Blocks.COBBLESTONE).patternLine("CQ").patternLine("QC").addCriterion("has_quartz", hasItem(Items.QUARTZ)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.DISPENSER).key('R', Items.REDSTONE).key('#', Blocks.COBBLESTONE).key('X', Items.BOW).patternLine("###").patternLine("#X#").patternLine("#R#").addCriterion("has_bow", hasItem(Items.BOW)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.DROPPER).key('R', Items.REDSTONE).key('#', Blocks.COBBLESTONE).patternLine("###").patternLine("# #").patternLine("#R#").addCriterion("has_redstone", hasItem(Items.REDSTONE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.EMERALD, 9).addIngredient(Blocks.EMERALD_BLOCK).addCriterion("has_emerald_block", hasItem(Blocks.EMERALD_BLOCK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.EMERALD_BLOCK).key('#', Items.EMERALD).patternLine("###").patternLine("###").patternLine("###").addCriterion("has_emerald", hasItem(Items.EMERALD)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.ENCHANTING_TABLE).key('B', Items.BOOK).key('#', Blocks.OBSIDIAN).key('D', Items.DIAMOND).patternLine(" B ").patternLine("D#D").patternLine("###").addCriterion("has_obsidian", hasItem(Blocks.OBSIDIAN)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.ENDER_CHEST).key('#', Blocks.OBSIDIAN).key('E', Items.ENDER_EYE).patternLine("###").patternLine("#E#").patternLine("###").addCriterion("has_ender_eye", hasItem(Items.ENDER_EYE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.ENDER_EYE).addIngredient(Items.ENDER_PEARL).addIngredient(Items.BLAZE_POWDER).addCriterion("has_blaze_powder", hasItem(Items.BLAZE_POWDER)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.END_STONE_BRICKS, 4).key('#', Blocks.END_STONE).patternLine("##").patternLine("##").addCriterion("has_end_stone", hasItem(Blocks.END_STONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.END_CRYSTAL).key('T', Items.GHAST_TEAR).key('E', Items.ENDER_EYE).key('G', Blocks.GLASS).patternLine("GGG").patternLine("GEG").patternLine("GTG").addCriterion("has_ender_eye", hasItem(Items.ENDER_EYE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.END_ROD, 4).key('#', Items.POPPED_CHORUS_FRUIT).key('/', Items.BLAZE_ROD).patternLine("/").patternLine("#").addCriterion("has_chorus_fruit_popped", hasItem(Items.POPPED_CHORUS_FRUIT)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.FERMENTED_SPIDER_EYE).addIngredient(Items.SPIDER_EYE).addIngredient(Blocks.BROWN_MUSHROOM).addIngredient(Items.SUGAR).addCriterion("has_spider_eye", hasItem(Items.SPIDER_EYE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.FIRE_CHARGE, 3).addIngredient(Items.GUNPOWDER).addIngredient(Items.BLAZE_POWDER).addIngredient(Ingredient.fromItems(Items.COAL, Items.CHARCOAL)).addCriterion("has_blaze_powder", hasItem(Items.BLAZE_POWDER)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.FISHING_ROD).key('#', Items.STICK).key('X', Items.STRING).patternLine("  #").patternLine(" #X").patternLine("# X").addCriterion("has_string", hasItem(Items.STRING)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.FLINT_AND_STEEL).addIngredient(Items.IRON_INGOT).addIngredient(Items.FLINT).addCriterion("has_flint", hasItem(Items.FLINT)).addCriterion("has_obsidian", hasItem(Blocks.OBSIDIAN)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.FLOWER_POT).key('#', Items.BRICK).patternLine("# #").patternLine(" # ").addCriterion("has_brick", hasItem(Items.BRICK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.FURNACE).key('#', ItemTags.STONE_CRAFTING_MATERIALS).patternLine("###").patternLine("# #").patternLine("###").addCriterion("has_cobblestone", hasItem(ItemTags.STONE_CRAFTING_MATERIALS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.FURNACE_MINECART).key('A', Blocks.FURNACE).key('B', Items.MINECART).patternLine("A").patternLine("B").addCriterion("has_minecart", hasItem(Items.MINECART)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.GLASS_BOTTLE, 3).key('#', Blocks.GLASS).patternLine("# #").patternLine(" # ").addCriterion("has_glass", hasItem(Blocks.GLASS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.GLASS_PANE, 16).key('#', Blocks.GLASS).patternLine("###").patternLine("###").addCriterion("has_glass", hasItem(Blocks.GLASS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.GLOWSTONE).key('#', Items.GLOWSTONE_DUST).patternLine("##").patternLine("##").addCriterion("has_glowstone_dust", hasItem(Items.GLOWSTONE_DUST)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.GOLDEN_APPLE).key('#', Items.GOLD_INGOT).key('X', Items.APPLE).patternLine("###").patternLine("#X#").patternLine("###").addCriterion("has_gold_ingot", hasItem(Items.GOLD_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.GOLDEN_AXE).key('#', Items.STICK).key('X', Items.GOLD_INGOT).patternLine("XX").patternLine("X#").patternLine(" #").addCriterion("has_gold_ingot", hasItem(Items.GOLD_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.GOLDEN_BOOTS).key('X', Items.GOLD_INGOT).patternLine("X X").patternLine("X X").addCriterion("has_gold_ingot", hasItem(Items.GOLD_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.GOLDEN_CARROT).key('#', Items.GOLD_NUGGET).key('X', Items.CARROT).patternLine("###").patternLine("#X#").patternLine("###").addCriterion("has_gold_nugget", hasItem(Items.GOLD_NUGGET)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.GOLDEN_CHESTPLATE).key('X', Items.GOLD_INGOT).patternLine("X X").patternLine("XXX").patternLine("XXX").addCriterion("has_gold_ingot", hasItem(Items.GOLD_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.GOLDEN_HELMET).key('X', Items.GOLD_INGOT).patternLine("XXX").patternLine("X X").addCriterion("has_gold_ingot", hasItem(Items.GOLD_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.GOLDEN_HOE).key('#', Items.STICK).key('X', Items.GOLD_INGOT).patternLine("XX").patternLine(" #").patternLine(" #").addCriterion("has_gold_ingot", hasItem(Items.GOLD_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.GOLDEN_LEGGINGS).key('X', Items.GOLD_INGOT).patternLine("XXX").patternLine("X X").patternLine("X X").addCriterion("has_gold_ingot", hasItem(Items.GOLD_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.GOLDEN_PICKAXE).key('#', Items.STICK).key('X', Items.GOLD_INGOT).patternLine("XXX").patternLine(" # ").patternLine(" # ").addCriterion("has_gold_ingot", hasItem(Items.GOLD_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POWERED_RAIL, 6).key('R', Items.REDSTONE).key('#', Items.STICK).key('X', Items.GOLD_INGOT).patternLine("X X").patternLine("X#X").patternLine("XRX").addCriterion("has_rail", hasItem(Blocks.RAIL)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.GOLDEN_SHOVEL).key('#', Items.STICK).key('X', Items.GOLD_INGOT).patternLine("X").patternLine("#").patternLine("#").addCriterion("has_gold_ingot", hasItem(Items.GOLD_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.GOLDEN_SWORD).key('#', Items.STICK).key('X', Items.GOLD_INGOT).patternLine("X").patternLine("X").patternLine("#").addCriterion("has_gold_ingot", hasItem(Items.GOLD_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.GOLD_BLOCK).key('#', Items.GOLD_INGOT).patternLine("###").patternLine("###").patternLine("###").addCriterion("has_gold_ingot", hasItem(Items.GOLD_INGOT)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.GOLD_INGOT, 9).addIngredient(Blocks.GOLD_BLOCK).setGroup("gold_ingot").addCriterion("has_gold_block", hasItem(Blocks.GOLD_BLOCK)).build(consumer, "gold_ingot_from_gold_block");
        ShapedRecipeBuilder.shapedRecipe(Items.GOLD_INGOT).key('#', Items.GOLD_NUGGET).patternLine("###").patternLine("###").patternLine("###").setGroup("gold_ingot").addCriterion("has_gold_nugget", hasItem(Items.GOLD_NUGGET)).build(consumer, "gold_ingot_from_nuggets");
        ShapelessRecipeBuilder.shapelessRecipe(Items.GOLD_NUGGET, 9).addIngredient(Items.GOLD_INGOT).addCriterion("has_gold_ingot", hasItem(Items.GOLD_INGOT)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Blocks.GRANITE).addIngredient(Blocks.DIORITE).addIngredient(Items.QUARTZ).addCriterion("has_quartz", hasItem(Items.QUARTZ)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.GRAY_DYE, 2).addIngredient(Items.BLACK_DYE).addIngredient(Items.WHITE_DYE).addCriterion("has_white_dye", hasItem(Items.WHITE_DYE)).addCriterion("has_black_dye", hasItem(Items.BLACK_DYE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.HAY_BLOCK).key('#', Items.WHEAT).patternLine("###").patternLine("###").patternLine("###").addCriterion("has_wheat", hasItem(Items.WHEAT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE).key('#', Items.IRON_INGOT).patternLine("##").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.HONEY_BOTTLE, 4).addIngredient(Items.HONEY_BLOCK).addIngredient(Items.GLASS_BOTTLE, 4).addCriterion("has_honey_block", hasItem(Blocks.HONEY_BLOCK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.HONEY_BLOCK, 1).key('S', Items.HONEY_BOTTLE).patternLine("SS").patternLine("SS").addCriterion("has_honey_bottle", hasItem(Items.HONEY_BOTTLE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.HONEYCOMB_BLOCK).key('H', Items.HONEYCOMB).patternLine("HH").patternLine("HH").addCriterion("has_honeycomb", hasItem(Items.HONEYCOMB)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.HOPPER).key('C', Blocks.CHEST).key('I', Items.IRON_INGOT).patternLine("I I").patternLine("ICI").patternLine(" I ").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.HOPPER_MINECART).key('A', Blocks.HOPPER).key('B', Items.MINECART).patternLine("A").patternLine("B").addCriterion("has_minecart", hasItem(Items.MINECART)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.IRON_AXE).key('#', Items.STICK).key('X', Items.IRON_INGOT).patternLine("XX").patternLine("X#").patternLine(" #").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.IRON_BARS, 16).key('#', Items.IRON_INGOT).patternLine("###").patternLine("###").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.IRON_BLOCK).key('#', Items.IRON_INGOT).patternLine("###").patternLine("###").patternLine("###").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.IRON_BOOTS).key('X', Items.IRON_INGOT).patternLine("X X").patternLine("X X").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.IRON_CHESTPLATE).key('X', Items.IRON_INGOT).patternLine("X X").patternLine("XXX").patternLine("XXX").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.IRON_DOOR, 3).key('#', Items.IRON_INGOT).patternLine("##").patternLine("##").patternLine("##").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.IRON_HELMET).key('X', Items.IRON_INGOT).patternLine("XXX").patternLine("X X").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.IRON_HOE).key('#', Items.STICK).key('X', Items.IRON_INGOT).patternLine("XX").patternLine(" #").patternLine(" #").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.IRON_INGOT, 9).addIngredient(Blocks.IRON_BLOCK).setGroup("iron_ingot").addCriterion("has_iron_block", hasItem(Blocks.IRON_BLOCK)).build(consumer, "iron_ingot_from_iron_block");
        ShapedRecipeBuilder.shapedRecipe(Items.IRON_INGOT).key('#', Items.IRON_NUGGET).patternLine("###").patternLine("###").patternLine("###").setGroup("iron_ingot").addCriterion("has_iron_nugget", hasItem(Items.IRON_NUGGET)).build(consumer, "iron_ingot_from_nuggets");
        ShapedRecipeBuilder.shapedRecipe(Items.IRON_LEGGINGS).key('X', Items.IRON_INGOT).patternLine("XXX").patternLine("X X").patternLine("X X").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.IRON_NUGGET, 9).addIngredient(Items.IRON_INGOT).addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.IRON_PICKAXE).key('#', Items.STICK).key('X', Items.IRON_INGOT).patternLine("XXX").patternLine(" # ").patternLine(" # ").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.IRON_SHOVEL).key('#', Items.STICK).key('X', Items.IRON_INGOT).patternLine("X").patternLine("#").patternLine("#").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.IRON_SWORD).key('#', Items.STICK).key('X', Items.IRON_INGOT).patternLine("X").patternLine("X").patternLine("#").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.IRON_TRAPDOOR).key('#', Items.IRON_INGOT).patternLine("##").patternLine("##").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.ITEM_FRAME).key('#', Items.STICK).key('X', Items.LEATHER).patternLine("###").patternLine("#X#").patternLine("###").addCriterion("has_leather", hasItem(Items.LEATHER)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.JUKEBOX).key('#', ItemTags.PLANKS).key('X', Items.DIAMOND).patternLine("###").patternLine("#X#").patternLine("###").addCriterion("has_diamond", hasItem(Items.DIAMOND)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.LADDER, 3).key('#', Items.STICK).patternLine("# #").patternLine("###").patternLine("# #").addCriterion("has_stick", hasItem(Items.STICK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.LAPIS_BLOCK).key('#', Items.LAPIS_LAZULI).patternLine("###").patternLine("###").patternLine("###").addCriterion("has_lapis", hasItem(Items.LAPIS_LAZULI)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.LAPIS_LAZULI, 9).addIngredient(Blocks.LAPIS_BLOCK).addCriterion("has_lapis_block", hasItem(Blocks.LAPIS_BLOCK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.LEAD, 2).key('~', Items.STRING).key('O', Items.SLIME_BALL).patternLine("~~ ").patternLine("~O ").patternLine("  ~").addCriterion("has_slime_ball", hasItem(Items.SLIME_BALL)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.LEATHER).key('#', Items.RABBIT_HIDE).patternLine("##").patternLine("##").addCriterion("has_rabbit_hide", hasItem(Items.RABBIT_HIDE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.LEATHER_BOOTS).key('X', Items.LEATHER).patternLine("X X").patternLine("X X").addCriterion("has_leather", hasItem(Items.LEATHER)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.LEATHER_CHESTPLATE).key('X', Items.LEATHER).patternLine("X X").patternLine("XXX").patternLine("XXX").addCriterion("has_leather", hasItem(Items.LEATHER)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.LEATHER_HELMET).key('X', Items.LEATHER).patternLine("XXX").patternLine("X X").addCriterion("has_leather", hasItem(Items.LEATHER)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.LEATHER_LEGGINGS).key('X', Items.LEATHER).patternLine("XXX").patternLine("X X").patternLine("X X").addCriterion("has_leather", hasItem(Items.LEATHER)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.LEATHER_HORSE_ARMOR).key('X', Items.LEATHER).patternLine("X X").patternLine("XXX").patternLine("X X").addCriterion("has_leather", hasItem(Items.LEATHER)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.LECTERN).key('S', ItemTags.WOODEN_SLABS).key('B', Blocks.BOOKSHELF).patternLine("SSS").patternLine(" B ").patternLine(" S ").addCriterion("has_book", hasItem(Items.BOOK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.LEVER).key('#', Blocks.COBBLESTONE).key('X', Items.STICK).patternLine("X").patternLine("#").addCriterion("has_cobblestone", hasItem(Blocks.COBBLESTONE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.LIGHT_BLUE_DYE).addIngredient(Blocks.BLUE_ORCHID).setGroup("light_blue_dye").addCriterion("has_red_flower", hasItem(Blocks.BLUE_ORCHID)).build(consumer, "light_blue_dye_from_blue_orchid");
        ShapelessRecipeBuilder.shapelessRecipe(Items.LIGHT_BLUE_DYE, 2).addIngredient(Items.BLUE_DYE).addIngredient(Items.WHITE_DYE).setGroup("light_blue_dye").addCriterion("has_blue_dye", hasItem(Items.BLUE_DYE)).addCriterion("has_white_dye", hasItem(Items.WHITE_DYE)).build(consumer, "light_blue_dye_from_blue_white_dye");
        ShapelessRecipeBuilder.shapelessRecipe(Items.LIGHT_GRAY_DYE).addIngredient(Blocks.AZURE_BLUET).setGroup("light_gray_dye").addCriterion("has_red_flower", hasItem(Blocks.AZURE_BLUET)).build(consumer, "light_gray_dye_from_azure_bluet");
        ShapelessRecipeBuilder.shapelessRecipe(Items.LIGHT_GRAY_DYE, 2).addIngredient(Items.GRAY_DYE).addIngredient(Items.WHITE_DYE).setGroup("light_gray_dye").addCriterion("has_gray_dye", hasItem(Items.GRAY_DYE)).addCriterion("has_white_dye", hasItem(Items.WHITE_DYE)).build(consumer, "light_gray_dye_from_gray_white_dye");
        ShapelessRecipeBuilder.shapelessRecipe(Items.LIGHT_GRAY_DYE, 3).addIngredient(Items.BLACK_DYE).addIngredient(Items.WHITE_DYE, 2).setGroup("light_gray_dye").addCriterion("has_white_dye", hasItem(Items.WHITE_DYE)).addCriterion("has_black_dye", hasItem(Items.BLACK_DYE)).build(consumer, "light_gray_dye_from_black_white_dye");
        ShapelessRecipeBuilder.shapelessRecipe(Items.LIGHT_GRAY_DYE).addIngredient(Blocks.OXEYE_DAISY).setGroup("light_gray_dye").addCriterion("has_red_flower", hasItem(Blocks.OXEYE_DAISY)).build(consumer, "light_gray_dye_from_oxeye_daisy");
        ShapelessRecipeBuilder.shapelessRecipe(Items.LIGHT_GRAY_DYE).addIngredient(Blocks.WHITE_TULIP).setGroup("light_gray_dye").addCriterion("has_red_flower", hasItem(Blocks.WHITE_TULIP)).build(consumer, "light_gray_dye_from_white_tulip");
        ShapedRecipeBuilder.shapedRecipe(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE).key('#', Items.GOLD_INGOT).patternLine("##").addCriterion("has_gold_ingot", hasItem(Items.GOLD_INGOT)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.LIME_DYE, 2).addIngredient(Items.GREEN_DYE).addIngredient(Items.WHITE_DYE).addCriterion("has_green_dye", hasItem(Items.GREEN_DYE)).addCriterion("has_white_dye", hasItem(Items.WHITE_DYE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.JACK_O_LANTERN).key('A', Blocks.CARVED_PUMPKIN).key('B', Blocks.TORCH).patternLine("A").patternLine("B").addCriterion("has_carved_pumpkin", hasItem(Blocks.CARVED_PUMPKIN)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.MAGENTA_DYE).addIngredient(Blocks.ALLIUM).setGroup("magenta_dye").addCriterion("has_red_flower", hasItem(Blocks.ALLIUM)).build(consumer, "magenta_dye_from_allium");
        ShapelessRecipeBuilder.shapelessRecipe(Items.MAGENTA_DYE, 4).addIngredient(Items.BLUE_DYE).addIngredient(Items.RED_DYE, 2).addIngredient(Items.WHITE_DYE).setGroup("magenta_dye").addCriterion("has_blue_dye", hasItem(Items.BLUE_DYE)).addCriterion("has_rose_red", hasItem(Items.RED_DYE)).addCriterion("has_white_dye", hasItem(Items.WHITE_DYE)).build(consumer, "magenta_dye_from_blue_red_white_dye");
        ShapelessRecipeBuilder.shapelessRecipe(Items.MAGENTA_DYE, 3).addIngredient(Items.BLUE_DYE).addIngredient(Items.RED_DYE).addIngredient(Items.PINK_DYE).setGroup("magenta_dye").addCriterion("has_pink_dye", hasItem(Items.PINK_DYE)).addCriterion("has_blue_dye", hasItem(Items.BLUE_DYE)).addCriterion("has_red_dye", hasItem(Items.RED_DYE)).build(consumer, "magenta_dye_from_blue_red_pink");
        ShapelessRecipeBuilder.shapelessRecipe(Items.MAGENTA_DYE, 2).addIngredient(Blocks.LILAC).setGroup("magenta_dye").addCriterion("has_double_plant", hasItem(Blocks.LILAC)).build(consumer, "magenta_dye_from_lilac");
        ShapelessRecipeBuilder.shapelessRecipe(Items.MAGENTA_DYE, 2).addIngredient(Items.PURPLE_DYE).addIngredient(Items.PINK_DYE).setGroup("magenta_dye").addCriterion("has_pink_dye", hasItem(Items.PINK_DYE)).addCriterion("has_purple_dye", hasItem(Items.PURPLE_DYE)).build(consumer, "magenta_dye_from_purple_and_pink");
        ShapedRecipeBuilder.shapedRecipe(Blocks.MAGMA_BLOCK).key('#', Items.MAGMA_CREAM).patternLine("##").patternLine("##").addCriterion("has_magma_cream", hasItem(Items.MAGMA_CREAM)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.MAGMA_CREAM).addIngredient(Items.BLAZE_POWDER).addIngredient(Items.SLIME_BALL).addCriterion("has_blaze_powder", hasItem(Items.BLAZE_POWDER)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.MAP).key('#', Items.PAPER).key('X', Items.COMPASS).patternLine("###").patternLine("#X#").patternLine("###").addCriterion("has_compass", hasItem(Items.COMPASS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.MELON).key('M', Items.MELON_SLICE).patternLine("MMM").patternLine("MMM").patternLine("MMM").addCriterion("has_melon", hasItem(Items.MELON_SLICE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.MELON_SEEDS).addIngredient(Items.MELON_SLICE).addCriterion("has_melon", hasItem(Items.MELON_SLICE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.MINECART).key('#', Items.IRON_INGOT).patternLine("# #").patternLine("###").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Blocks.MOSSY_COBBLESTONE).addIngredient(Blocks.COBBLESTONE).addIngredient(Blocks.VINE).addCriterion("has_vine", hasItem(Blocks.VINE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.MOSSY_COBBLESTONE_WALL, 6).key('#', Blocks.MOSSY_COBBLESTONE).patternLine("###").patternLine("###").addCriterion("has_mossy_cobblestone", hasItem(Blocks.MOSSY_COBBLESTONE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Blocks.MOSSY_STONE_BRICKS).addIngredient(Blocks.STONE_BRICKS).addIngredient(Blocks.VINE).addCriterion("has_mossy_cobblestone", hasItem(Blocks.MOSSY_COBBLESTONE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.MUSHROOM_STEW).addIngredient(Blocks.BROWN_MUSHROOM).addIngredient(Blocks.RED_MUSHROOM).addIngredient(Items.BOWL).addCriterion("has_mushroom_stew", hasItem(Items.MUSHROOM_STEW)).addCriterion("has_bowl", hasItem(Items.BOWL)).addCriterion("has_brown_mushroom", hasItem(Blocks.BROWN_MUSHROOM)).addCriterion("has_red_mushroom", hasItem(Blocks.RED_MUSHROOM)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.NETHER_BRICKS).key('N', Items.NETHER_BRICK).patternLine("NN").patternLine("NN").addCriterion("has_netherbrick", hasItem(Items.NETHER_BRICK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.NETHER_BRICK_FENCE, 6).key('#', Blocks.NETHER_BRICKS).key('-', Items.NETHER_BRICK).patternLine("#-#").patternLine("#-#").addCriterion("has_nether_brick", hasItem(Blocks.NETHER_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.NETHER_BRICK_SLAB, 6).key('#', Blocks.NETHER_BRICKS).patternLine("###").addCriterion("has_nether_brick", hasItem(Blocks.NETHER_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.NETHER_BRICK_STAIRS, 4).key('#', Blocks.NETHER_BRICKS).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_nether_brick", hasItem(Blocks.NETHER_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.NETHER_WART_BLOCK).key('#', Items.NETHER_WART).patternLine("###").patternLine("###").patternLine("###").addCriterion("has_nether_wart", hasItem(Items.NETHER_WART)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.NOTE_BLOCK).key('#', ItemTags.PLANKS).key('X', Items.REDSTONE).patternLine("###").patternLine("#X#").patternLine("###").addCriterion("has_redstone", hasItem(Items.REDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.OBSERVER).key('Q', Items.QUARTZ).key('R', Items.REDSTONE).key('#', Blocks.COBBLESTONE).patternLine("###").patternLine("RRQ").patternLine("###").addCriterion("has_quartz", hasItem(Items.QUARTZ)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.ORANGE_DYE).addIngredient(Blocks.ORANGE_TULIP).setGroup("orange_dye").addCriterion("has_red_flower", hasItem(Blocks.ORANGE_TULIP)).build(consumer, "orange_dye_from_orange_tulip");
        ShapelessRecipeBuilder.shapelessRecipe(Items.ORANGE_DYE, 2).addIngredient(Items.RED_DYE).addIngredient(Items.YELLOW_DYE).setGroup("orange_dye").addCriterion("has_red_dye", hasItem(Items.RED_DYE)).addCriterion("has_yellow_dye", hasItem(Items.YELLOW_DYE)).build(consumer, "orange_dye_from_red_yellow");
        ShapedRecipeBuilder.shapedRecipe(Items.PAINTING).key('#', Items.STICK).key('X', Ingredient.fromTag(ItemTags.WOOL)).patternLine("###").patternLine("#X#").patternLine("###").addCriterion("has_wool", hasItem(ItemTags.WOOL)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.PAPER, 3).key('#', Blocks.SUGAR_CANE).patternLine("###").addCriterion("has_reeds", hasItem(Blocks.SUGAR_CANE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.QUARTZ_PILLAR, 2).key('#', Blocks.QUARTZ_BLOCK).patternLine("#").patternLine("#").addCriterion("has_chiseled_quartz_block", hasItem(Blocks.CHISELED_QUARTZ_BLOCK)).addCriterion("has_quartz_block", hasItem(Blocks.QUARTZ_BLOCK)).addCriterion("has_quartz_pillar", hasItem(Blocks.QUARTZ_PILLAR)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Blocks.PACKED_ICE).addIngredient(Blocks.ICE, 9).addCriterion("has_ice", hasItem(Blocks.ICE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.PINK_DYE, 2).addIngredient(Blocks.PEONY).setGroup("pink_dye").addCriterion("has_double_plant", hasItem(Blocks.PEONY)).build(consumer, "pink_dye_from_peony");
        ShapelessRecipeBuilder.shapelessRecipe(Items.PINK_DYE).addIngredient(Blocks.PINK_TULIP).setGroup("pink_dye").addCriterion("has_red_flower", hasItem(Blocks.PINK_TULIP)).build(consumer, "pink_dye_from_pink_tulip");
        ShapelessRecipeBuilder.shapelessRecipe(Items.PINK_DYE, 2).addIngredient(Items.RED_DYE).addIngredient(Items.WHITE_DYE).setGroup("pink_dye").addCriterion("has_white_dye", hasItem(Items.WHITE_DYE)).addCriterion("has_red_dye", hasItem(Items.RED_DYE)).build(consumer, "pink_dye_from_red_white_dye");
        ShapedRecipeBuilder.shapedRecipe(Blocks.PISTON).key('R', Items.REDSTONE).key('#', Blocks.COBBLESTONE).key('T', ItemTags.PLANKS).key('X', Items.IRON_INGOT).patternLine("TTT").patternLine("#X#").patternLine("#R#").addCriterion("has_redstone", hasItem(Items.REDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_BASALT, 4).key('S', Blocks.BASALT).patternLine("SS").patternLine("SS").addCriterion("has_basalt", hasItem(Blocks.BASALT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_GRANITE, 4).key('S', Blocks.GRANITE).patternLine("SS").patternLine("SS").addCriterion("has_stone", hasItem(Blocks.GRANITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_DIORITE, 4).key('S', Blocks.DIORITE).patternLine("SS").patternLine("SS").addCriterion("has_stone", hasItem(Blocks.DIORITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_ANDESITE, 4).key('S', Blocks.ANDESITE).patternLine("SS").patternLine("SS").addCriterion("has_stone", hasItem(Blocks.ANDESITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.PRISMARINE).key('S', Items.PRISMARINE_SHARD).patternLine("SS").patternLine("SS").addCriterion("has_prismarine_shard", hasItem(Items.PRISMARINE_SHARD)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.PRISMARINE_BRICKS).key('S', Items.PRISMARINE_SHARD).patternLine("SSS").patternLine("SSS").patternLine("SSS").addCriterion("has_prismarine_shard", hasItem(Items.PRISMARINE_SHARD)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.PRISMARINE_SLAB, 6).key('#', Blocks.PRISMARINE).patternLine("###").addCriterion("has_prismarine", hasItem(Blocks.PRISMARINE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.PRISMARINE_BRICK_SLAB, 6).key('#', Blocks.PRISMARINE_BRICKS).patternLine("###").addCriterion("has_prismarine_bricks", hasItem(Blocks.PRISMARINE_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.DARK_PRISMARINE_SLAB, 6).key('#', Blocks.DARK_PRISMARINE).patternLine("###").addCriterion("has_dark_prismarine", hasItem(Blocks.DARK_PRISMARINE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.PUMPKIN_PIE).addIngredient(Blocks.PUMPKIN).addIngredient(Items.SUGAR).addIngredient(Items.EGG).addCriterion("has_carved_pumpkin", hasItem(Blocks.CARVED_PUMPKIN)).addCriterion("has_pumpkin", hasItem(Blocks.PUMPKIN)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.PUMPKIN_SEEDS, 4).addIngredient(Blocks.PUMPKIN).addCriterion("has_pumpkin", hasItem(Blocks.PUMPKIN)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.PURPLE_DYE, 2).addIngredient(Items.BLUE_DYE).addIngredient(Items.RED_DYE).addCriterion("has_blue_dye", hasItem(Items.BLUE_DYE)).addCriterion("has_red_dye", hasItem(Items.RED_DYE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SHULKER_BOX).key('#', Blocks.CHEST).key('-', Items.SHULKER_SHELL).patternLine("-").patternLine("#").patternLine("-").addCriterion("has_shulker_shell", hasItem(Items.SHULKER_SHELL)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.PURPUR_BLOCK, 4).key('F', Items.POPPED_CHORUS_FRUIT).patternLine("FF").patternLine("FF").addCriterion("has_chorus_fruit_popped", hasItem(Items.POPPED_CHORUS_FRUIT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.PURPUR_PILLAR).key('#', Blocks.PURPUR_SLAB).patternLine("#").patternLine("#").addCriterion("has_purpur_block", hasItem(Blocks.PURPUR_BLOCK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.PURPUR_SLAB, 6).key('#', Ingredient.fromItems(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)).patternLine("###").addCriterion("has_purpur_block", hasItem(Blocks.PURPUR_BLOCK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.PURPUR_STAIRS, 4).key('#', Ingredient.fromItems(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_purpur_block", hasItem(Blocks.PURPUR_BLOCK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.QUARTZ_BLOCK).key('#', Items.QUARTZ).patternLine("##").patternLine("##").addCriterion("has_quartz", hasItem(Items.QUARTZ)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.QUARTZ_BRICKS, 4).key('#', Blocks.QUARTZ_BLOCK).patternLine("##").patternLine("##").addCriterion("has_quartz_block", hasItem(Blocks.QUARTZ_BLOCK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.QUARTZ_SLAB, 6).key('#', Ingredient.fromItems(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR)).patternLine("###").addCriterion("has_chiseled_quartz_block", hasItem(Blocks.CHISELED_QUARTZ_BLOCK)).addCriterion("has_quartz_block", hasItem(Blocks.QUARTZ_BLOCK)).addCriterion("has_quartz_pillar", hasItem(Blocks.QUARTZ_PILLAR)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.QUARTZ_STAIRS, 4).key('#', Ingredient.fromItems(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR)).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_chiseled_quartz_block", hasItem(Blocks.CHISELED_QUARTZ_BLOCK)).addCriterion("has_quartz_block", hasItem(Blocks.QUARTZ_BLOCK)).addCriterion("has_quartz_pillar", hasItem(Blocks.QUARTZ_PILLAR)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.RABBIT_STEW).addIngredient(Items.BAKED_POTATO).addIngredient(Items.COOKED_RABBIT).addIngredient(Items.BOWL).addIngredient(Items.CARROT).addIngredient(Blocks.BROWN_MUSHROOM).setGroup("rabbit_stew").addCriterion("has_cooked_rabbit", hasItem(Items.COOKED_RABBIT)).build(consumer, "rabbit_stew_from_brown_mushroom");
        ShapelessRecipeBuilder.shapelessRecipe(Items.RABBIT_STEW).addIngredient(Items.BAKED_POTATO).addIngredient(Items.COOKED_RABBIT).addIngredient(Items.BOWL).addIngredient(Items.CARROT).addIngredient(Blocks.RED_MUSHROOM).setGroup("rabbit_stew").addCriterion("has_cooked_rabbit", hasItem(Items.COOKED_RABBIT)).build(consumer, "rabbit_stew_from_red_mushroom");
        ShapedRecipeBuilder.shapedRecipe(Blocks.RAIL, 16).key('#', Items.STICK).key('X', Items.IRON_INGOT).patternLine("X X").patternLine("X#X").patternLine("X X").addCriterion("has_minecart", hasItem(Items.MINECART)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.REDSTONE, 9).addIngredient(Blocks.REDSTONE_BLOCK).addCriterion("has_redstone_block", hasItem(Blocks.REDSTONE_BLOCK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.REDSTONE_BLOCK).key('#', Items.REDSTONE).patternLine("###").patternLine("###").patternLine("###").addCriterion("has_redstone", hasItem(Items.REDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.REDSTONE_LAMP).key('R', Items.REDSTONE).key('G', Blocks.GLOWSTONE).patternLine(" R ").patternLine("RGR").patternLine(" R ").addCriterion("has_glowstone", hasItem(Blocks.GLOWSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.REDSTONE_TORCH).key('#', Items.STICK).key('X', Items.REDSTONE).patternLine("X").patternLine("#").addCriterion("has_redstone", hasItem(Items.REDSTONE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.RED_DYE).addIngredient(Items.BEETROOT).setGroup("red_dye").addCriterion("has_beetroot", hasItem(Items.BEETROOT)).build(consumer, "red_dye_from_beetroot");
        ShapelessRecipeBuilder.shapelessRecipe(Items.RED_DYE).addIngredient(Blocks.POPPY).setGroup("red_dye").addCriterion("has_red_flower", hasItem(Blocks.POPPY)).build(consumer, "red_dye_from_poppy");
        ShapelessRecipeBuilder.shapelessRecipe(Items.RED_DYE, 2).addIngredient(Blocks.ROSE_BUSH).setGroup("red_dye").addCriterion("has_double_plant", hasItem(Blocks.ROSE_BUSH)).build(consumer, "red_dye_from_rose_bush");
        ShapelessRecipeBuilder.shapelessRecipe(Items.RED_DYE).addIngredient(Blocks.RED_TULIP).setGroup("red_dye").addCriterion("has_red_flower", hasItem(Blocks.RED_TULIP)).build(consumer, "red_dye_from_tulip");
        ShapedRecipeBuilder.shapedRecipe(Blocks.RED_NETHER_BRICKS).key('W', Items.NETHER_WART).key('N', Items.NETHER_BRICK).patternLine("NW").patternLine("WN").addCriterion("has_nether_wart", hasItem(Items.NETHER_WART)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.RED_SANDSTONE).key('#', Blocks.RED_SAND).patternLine("##").patternLine("##").addCriterion("has_sand", hasItem(Blocks.RED_SAND)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.RED_SANDSTONE_SLAB, 6).key('#', Ingredient.fromItems(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE)).patternLine("###").addCriterion("has_red_sandstone", hasItem(Blocks.RED_SANDSTONE)).addCriterion("has_chiseled_red_sandstone", hasItem(Blocks.CHISELED_RED_SANDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CUT_RED_SANDSTONE_SLAB, 6).key('#', Blocks.CUT_RED_SANDSTONE).patternLine("###").addCriterion("has_cut_red_sandstone", hasItem(Blocks.CUT_RED_SANDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.RED_SANDSTONE_STAIRS, 4).key('#', Ingredient.fromItems(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE)).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_red_sandstone", hasItem(Blocks.RED_SANDSTONE)).addCriterion("has_chiseled_red_sandstone", hasItem(Blocks.CHISELED_RED_SANDSTONE)).addCriterion("has_cut_red_sandstone", hasItem(Blocks.CUT_RED_SANDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.REPEATER).key('#', Blocks.REDSTONE_TORCH).key('X', Items.REDSTONE).key('I', Blocks.STONE).patternLine("#X#").patternLine("III").addCriterion("has_redstone_torch", hasItem(Blocks.REDSTONE_TORCH)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SANDSTONE).key('#', Blocks.SAND).patternLine("##").patternLine("##").addCriterion("has_sand", hasItem(Blocks.SAND)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SANDSTONE_SLAB, 6).key('#', Ingredient.fromItems(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE)).patternLine("###").addCriterion("has_sandstone", hasItem(Blocks.SANDSTONE)).addCriterion("has_chiseled_sandstone", hasItem(Blocks.CHISELED_SANDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CUT_SANDSTONE_SLAB, 6).key('#', Blocks.CUT_SANDSTONE).patternLine("###").addCriterion("has_cut_sandstone", hasItem(Blocks.CUT_SANDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SANDSTONE_STAIRS, 4).key('#', Ingredient.fromItems(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE)).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_sandstone", hasItem(Blocks.SANDSTONE)).addCriterion("has_chiseled_sandstone", hasItem(Blocks.CHISELED_SANDSTONE)).addCriterion("has_cut_sandstone", hasItem(Blocks.CUT_SANDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SEA_LANTERN).key('S', Items.PRISMARINE_SHARD).key('C', Items.PRISMARINE_CRYSTALS).patternLine("SCS").patternLine("CCC").patternLine("SCS").addCriterion("has_prismarine_crystals", hasItem(Items.PRISMARINE_CRYSTALS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.SHEARS).key('#', Items.IRON_INGOT).patternLine(" #").patternLine("# ").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.SHIELD).key('W', ItemTags.PLANKS).key('o', Items.IRON_INGOT).patternLine("WoW").patternLine("WWW").patternLine(" W ").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SLIME_BLOCK).key('#', Items.SLIME_BALL).patternLine("###").patternLine("###").patternLine("###").addCriterion("has_slime_ball", hasItem(Items.SLIME_BALL)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.SLIME_BALL, 9).addIngredient(Blocks.SLIME_BLOCK).addCriterion("has_slime", hasItem(Blocks.SLIME_BLOCK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CUT_RED_SANDSTONE, 4).key('#', Blocks.RED_SANDSTONE).patternLine("##").patternLine("##").addCriterion("has_red_sandstone", hasItem(Blocks.RED_SANDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CUT_SANDSTONE, 4).key('#', Blocks.SANDSTONE).patternLine("##").patternLine("##").addCriterion("has_sandstone", hasItem(Blocks.SANDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SNOW_BLOCK).key('#', Items.SNOWBALL).patternLine("##").patternLine("##").addCriterion("has_snowball", hasItem(Items.SNOWBALL)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SNOW, 6).key('#', Blocks.SNOW_BLOCK).patternLine("###").addCriterion("has_snowball", hasItem(Items.SNOWBALL)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SOUL_CAMPFIRE).key('L', ItemTags.LOGS).key('S', Items.STICK).key('#', ItemTags.SOUL_FIRE_BASE_BLOCKS).patternLine(" S ").patternLine("S#S").patternLine("LLL").addCriterion("has_stick", hasItem(Items.STICK)).addCriterion("has_soul_sand", hasItem(ItemTags.SOUL_FIRE_BASE_BLOCKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.GLISTERING_MELON_SLICE).key('#', Items.GOLD_NUGGET).key('X', Items.MELON_SLICE).patternLine("###").patternLine("#X#").patternLine("###").addCriterion("has_melon", hasItem(Items.MELON_SLICE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.SPECTRAL_ARROW, 2).key('#', Items.GLOWSTONE_DUST).key('X', Items.ARROW).patternLine(" # ").patternLine("#X#").patternLine(" # ").addCriterion("has_glowstone_dust", hasItem(Items.GLOWSTONE_DUST)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.STICK, 4).key('#', ItemTags.PLANKS).patternLine("#").patternLine("#").setGroup("sticks").addCriterion("has_planks", hasItem(ItemTags.PLANKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.STICK, 1).key('#', Blocks.BAMBOO).patternLine("#").patternLine("#").setGroup("sticks").addCriterion("has_bamboo", hasItem(Blocks.BAMBOO)).build(consumer, "stick_from_bamboo_item");
        ShapedRecipeBuilder.shapedRecipe(Blocks.STICKY_PISTON).key('P', Blocks.PISTON).key('S', Items.SLIME_BALL).patternLine("S").patternLine("P").addCriterion("has_slime_ball", hasItem(Items.SLIME_BALL)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.STONE_BRICKS, 4).key('#', Blocks.STONE).patternLine("##").patternLine("##").addCriterion("has_stone", hasItem(Blocks.STONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.STONE_AXE).key('#', Items.STICK).key('X', ItemTags.STONE_TOOL_MATERIALS).patternLine("XX").patternLine("X#").patternLine(" #").addCriterion("has_cobblestone", hasItem(ItemTags.STONE_TOOL_MATERIALS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.STONE_BRICK_SLAB, 6).key('#', Blocks.STONE_BRICKS).patternLine("###").addCriterion("has_stone_bricks", hasItem(ItemTags.STONE_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.STONE_BRICK_STAIRS, 4).key('#', Blocks.STONE_BRICKS).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_stone_bricks", hasItem(ItemTags.STONE_BRICKS)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Blocks.STONE_BUTTON).addIngredient(Blocks.STONE).addCriterion("has_stone", hasItem(Blocks.STONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.STONE_HOE).key('#', Items.STICK).key('X', ItemTags.STONE_TOOL_MATERIALS).patternLine("XX").patternLine(" #").patternLine(" #").addCriterion("has_cobblestone", hasItem(ItemTags.STONE_TOOL_MATERIALS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.STONE_PICKAXE).key('#', Items.STICK).key('X', ItemTags.STONE_TOOL_MATERIALS).patternLine("XXX").patternLine(" # ").patternLine(" # ").addCriterion("has_cobblestone", hasItem(ItemTags.STONE_TOOL_MATERIALS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.STONE_PRESSURE_PLATE).key('#', Blocks.STONE).patternLine("##").addCriterion("has_stone", hasItem(Blocks.STONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.STONE_SHOVEL).key('#', Items.STICK).key('X', ItemTags.STONE_TOOL_MATERIALS).patternLine("X").patternLine("#").patternLine("#").addCriterion("has_cobblestone", hasItem(ItemTags.STONE_TOOL_MATERIALS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.STONE_SLAB, 6).key('#', Blocks.STONE).patternLine("###").addCriterion("has_stone", hasItem(Blocks.STONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SMOOTH_STONE_SLAB, 6).key('#', Blocks.SMOOTH_STONE).patternLine("###").addCriterion("has_smooth_stone", hasItem(Blocks.SMOOTH_STONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.COBBLESTONE_STAIRS, 4).key('#', Blocks.COBBLESTONE).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_cobblestone", hasItem(Blocks.COBBLESTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.STONE_SWORD).key('#', Items.STICK).key('X', ItemTags.STONE_TOOL_MATERIALS).patternLine("X").patternLine("X").patternLine("#").addCriterion("has_cobblestone", hasItem(ItemTags.STONE_TOOL_MATERIALS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.WHITE_WOOL).key('#', Items.STRING).patternLine("##").patternLine("##").addCriterion("has_string", hasItem(Items.STRING)).build(consumer, "white_wool_from_string");
        ShapelessRecipeBuilder.shapelessRecipe(Items.SUGAR).addIngredient(Blocks.SUGAR_CANE).setGroup("sugar").addCriterion("has_reeds", hasItem(Blocks.SUGAR_CANE)).build(consumer, "sugar_from_sugar_cane");
        ShapelessRecipeBuilder.shapelessRecipe(Items.SUGAR, 3).addIngredient(Items.HONEY_BOTTLE).setGroup("sugar").addCriterion("has_honey_bottle", hasItem(Items.HONEY_BOTTLE)).build(consumer, "sugar_from_honey_bottle");
        ShapedRecipeBuilder.shapedRecipe(Blocks.TARGET).key('H', Items.HAY_BLOCK).key('R', Items.REDSTONE).patternLine(" R ").patternLine("RHR").patternLine(" R ").addCriterion("has_redstone", hasItem(Items.REDSTONE)).addCriterion("has_hay_block", hasItem(Blocks.HAY_BLOCK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.TNT).key('#', Ingredient.fromItems(Blocks.SAND, Blocks.RED_SAND)).key('X', Items.GUNPOWDER).patternLine("X#X").patternLine("#X#").patternLine("X#X").addCriterion("has_gunpowder", hasItem(Items.GUNPOWDER)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.TNT_MINECART).key('A', Blocks.TNT).key('B', Items.MINECART).patternLine("A").patternLine("B").addCriterion("has_minecart", hasItem(Items.MINECART)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.TORCH, 4).key('#', Items.STICK).key('X', Ingredient.fromItems(Items.COAL, Items.CHARCOAL)).patternLine("X").patternLine("#").addCriterion("has_stone_pickaxe", hasItem(Items.STONE_PICKAXE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SOUL_TORCH, 4).key('X', Ingredient.fromItems(Items.COAL, Items.CHARCOAL)).key('#', Items.STICK).key('S', ItemTags.SOUL_FIRE_BASE_BLOCKS).patternLine("X").patternLine("#").patternLine("S").addCriterion("has_soul_sand", hasItem(ItemTags.SOUL_FIRE_BASE_BLOCKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.LANTERN).key('#', Items.TORCH).key('X', Items.IRON_NUGGET).patternLine("XXX").patternLine("X#X").patternLine("XXX").addCriterion("has_iron_nugget", hasItem(Items.IRON_NUGGET)).addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SOUL_LANTERN).key('#', Items.SOUL_TORCH).key('X', Items.IRON_NUGGET).patternLine("XXX").patternLine("X#X").patternLine("XXX").addCriterion("has_soul_torch", hasItem(Items.SOUL_TORCH)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Blocks.TRAPPED_CHEST).addIngredient(Blocks.CHEST).addIngredient(Blocks.TRIPWIRE_HOOK).addCriterion("has_tripwire_hook", hasItem(Blocks.TRIPWIRE_HOOK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.TRIPWIRE_HOOK, 2).key('#', ItemTags.PLANKS).key('S', Items.STICK).key('I', Items.IRON_INGOT).patternLine("I").patternLine("S").patternLine("#").addCriterion("has_string", hasItem(Items.STRING)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.TURTLE_HELMET).key('X', Items.SCUTE).patternLine("XXX").patternLine("X X").addCriterion("has_scute", hasItem(Items.SCUTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.WHEAT, 9).addIngredient(Blocks.HAY_BLOCK).addCriterion("has_hay_block", hasItem(Blocks.HAY_BLOCK)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.WHITE_DYE).addIngredient(Items.BONE_MEAL).setGroup("white_dye").addCriterion("has_bone_meal", hasItem(Items.BONE_MEAL)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.WHITE_DYE).addIngredient(Blocks.LILY_OF_THE_VALLEY).setGroup("white_dye").addCriterion("has_white_flower", hasItem(Blocks.LILY_OF_THE_VALLEY)).build(consumer, "white_dye_from_lily_of_the_valley");
        ShapedRecipeBuilder.shapedRecipe(Items.WOODEN_AXE).key('#', Items.STICK).key('X', ItemTags.PLANKS).patternLine("XX").patternLine("X#").patternLine(" #").addCriterion("has_stick", hasItem(Items.STICK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.WOODEN_HOE).key('#', Items.STICK).key('X', ItemTags.PLANKS).patternLine("XX").patternLine(" #").patternLine(" #").addCriterion("has_stick", hasItem(Items.STICK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.WOODEN_PICKAXE).key('#', Items.STICK).key('X', ItemTags.PLANKS).patternLine("XXX").patternLine(" # ").patternLine(" # ").addCriterion("has_stick", hasItem(Items.STICK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.WOODEN_SHOVEL).key('#', Items.STICK).key('X', ItemTags.PLANKS).patternLine("X").patternLine("#").patternLine("#").addCriterion("has_stick", hasItem(Items.STICK)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.WOODEN_SWORD).key('#', Items.STICK).key('X', ItemTags.PLANKS).patternLine("X").patternLine("X").patternLine("#").addCriterion("has_stick", hasItem(Items.STICK)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.WRITABLE_BOOK).addIngredient(Items.BOOK).addIngredient(Items.INK_SAC).addIngredient(Items.FEATHER).addCriterion("has_book", hasItem(Items.BOOK)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.YELLOW_DYE).addIngredient(Blocks.DANDELION).setGroup("yellow_dye").addCriterion("has_yellow_flower", hasItem(Blocks.DANDELION)).build(consumer, "yellow_dye_from_dandelion");
        ShapelessRecipeBuilder.shapelessRecipe(Items.YELLOW_DYE, 2).addIngredient(Blocks.SUNFLOWER).setGroup("yellow_dye").addCriterion("has_double_plant", hasItem(Blocks.SUNFLOWER)).build(consumer, "yellow_dye_from_sunflower");
        ShapelessRecipeBuilder.shapelessRecipe(Items.DRIED_KELP, 9).addIngredient(Blocks.DRIED_KELP_BLOCK).addCriterion("has_dried_kelp_block", hasItem(Blocks.DRIED_KELP_BLOCK)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Blocks.DRIED_KELP_BLOCK).addIngredient(Items.DRIED_KELP, 9).addCriterion("has_dried_kelp", hasItem(Items.DRIED_KELP)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CONDUIT).key('#', Items.NAUTILUS_SHELL).key('X', Items.HEART_OF_THE_SEA).patternLine("###").patternLine("#X#").patternLine("###").addCriterion("has_nautilus_core", hasItem(Items.HEART_OF_THE_SEA)).addCriterion("has_nautilus_shell", hasItem(Items.NAUTILUS_SHELL)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_GRANITE_STAIRS, 4).key('#', Blocks.POLISHED_GRANITE).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_polished_granite", hasItem(Blocks.POLISHED_GRANITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SMOOTH_RED_SANDSTONE_STAIRS, 4).key('#', Blocks.SMOOTH_RED_SANDSTONE).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_smooth_red_sandstone", hasItem(Blocks.SMOOTH_RED_SANDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.MOSSY_STONE_BRICK_STAIRS, 4).key('#', Blocks.MOSSY_STONE_BRICKS).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_mossy_stone_bricks", hasItem(Blocks.MOSSY_STONE_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_DIORITE_STAIRS, 4).key('#', Blocks.POLISHED_DIORITE).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_polished_diorite", hasItem(Blocks.POLISHED_DIORITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.MOSSY_COBBLESTONE_STAIRS, 4).key('#', Blocks.MOSSY_COBBLESTONE).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_mossy_cobblestone", hasItem(Blocks.MOSSY_COBBLESTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.END_STONE_BRICK_STAIRS, 4).key('#', Blocks.END_STONE_BRICKS).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_end_stone_bricks", hasItem(Blocks.END_STONE_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.STONE_STAIRS, 4).key('#', Blocks.STONE).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_stone", hasItem(Blocks.STONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SMOOTH_SANDSTONE_STAIRS, 4).key('#', Blocks.SMOOTH_SANDSTONE).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_smooth_sandstone", hasItem(Blocks.SMOOTH_SANDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SMOOTH_QUARTZ_STAIRS, 4).key('#', Blocks.SMOOTH_QUARTZ).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_smooth_quartz", hasItem(Blocks.SMOOTH_QUARTZ)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.GRANITE_STAIRS, 4).key('#', Blocks.GRANITE).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_granite", hasItem(Blocks.GRANITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.ANDESITE_STAIRS, 4).key('#', Blocks.ANDESITE).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_andesite", hasItem(Blocks.ANDESITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.RED_NETHER_BRICK_STAIRS, 4).key('#', Blocks.RED_NETHER_BRICKS).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_red_nether_bricks", hasItem(Blocks.RED_NETHER_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_ANDESITE_STAIRS, 4).key('#', Blocks.POLISHED_ANDESITE).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_polished_andesite", hasItem(Blocks.POLISHED_ANDESITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.DIORITE_STAIRS, 4).key('#', Blocks.DIORITE).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_diorite", hasItem(Blocks.DIORITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_GRANITE_SLAB, 6).key('#', Blocks.POLISHED_GRANITE).patternLine("###").addCriterion("has_polished_granite", hasItem(Blocks.POLISHED_GRANITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SMOOTH_RED_SANDSTONE_SLAB, 6).key('#', Blocks.SMOOTH_RED_SANDSTONE).patternLine("###").addCriterion("has_smooth_red_sandstone", hasItem(Blocks.SMOOTH_RED_SANDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.MOSSY_STONE_BRICK_SLAB, 6).key('#', Blocks.MOSSY_STONE_BRICKS).patternLine("###").addCriterion("has_mossy_stone_bricks", hasItem(Blocks.MOSSY_STONE_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_DIORITE_SLAB, 6).key('#', Blocks.POLISHED_DIORITE).patternLine("###").addCriterion("has_polished_diorite", hasItem(Blocks.POLISHED_DIORITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.MOSSY_COBBLESTONE_SLAB, 6).key('#', Blocks.MOSSY_COBBLESTONE).patternLine("###").addCriterion("has_mossy_cobblestone", hasItem(Blocks.MOSSY_COBBLESTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.END_STONE_BRICK_SLAB, 6).key('#', Blocks.END_STONE_BRICKS).patternLine("###").addCriterion("has_end_stone_bricks", hasItem(Blocks.END_STONE_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SMOOTH_SANDSTONE_SLAB, 6).key('#', Blocks.SMOOTH_SANDSTONE).patternLine("###").addCriterion("has_smooth_sandstone", hasItem(Blocks.SMOOTH_SANDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SMOOTH_QUARTZ_SLAB, 6).key('#', Blocks.SMOOTH_QUARTZ).patternLine("###").addCriterion("has_smooth_quartz", hasItem(Blocks.SMOOTH_QUARTZ)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.GRANITE_SLAB, 6).key('#', Blocks.GRANITE).patternLine("###").addCriterion("has_granite", hasItem(Blocks.GRANITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.ANDESITE_SLAB, 6).key('#', Blocks.ANDESITE).patternLine("###").addCriterion("has_andesite", hasItem(Blocks.ANDESITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.RED_NETHER_BRICK_SLAB, 6).key('#', Blocks.RED_NETHER_BRICKS).patternLine("###").addCriterion("has_red_nether_bricks", hasItem(Blocks.RED_NETHER_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_ANDESITE_SLAB, 6).key('#', Blocks.POLISHED_ANDESITE).patternLine("###").addCriterion("has_polished_andesite", hasItem(Blocks.POLISHED_ANDESITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.DIORITE_SLAB, 6).key('#', Blocks.DIORITE).patternLine("###").addCriterion("has_diorite", hasItem(Blocks.DIORITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.BRICK_WALL, 6).key('#', Blocks.BRICKS).patternLine("###").patternLine("###").addCriterion("has_bricks", hasItem(Blocks.BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.PRISMARINE_WALL, 6).key('#', Blocks.PRISMARINE).patternLine("###").patternLine("###").addCriterion("has_prismarine", hasItem(Blocks.PRISMARINE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.RED_SANDSTONE_WALL, 6).key('#', Blocks.RED_SANDSTONE).patternLine("###").patternLine("###").addCriterion("has_red_sandstone", hasItem(Blocks.RED_SANDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.MOSSY_STONE_BRICK_WALL, 6).key('#', Blocks.MOSSY_STONE_BRICKS).patternLine("###").patternLine("###").addCriterion("has_mossy_stone_bricks", hasItem(Blocks.MOSSY_STONE_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.GRANITE_WALL, 6).key('#', Blocks.GRANITE).patternLine("###").patternLine("###").addCriterion("has_granite", hasItem(Blocks.GRANITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.STONE_BRICK_WALL, 6).key('#', Blocks.STONE_BRICKS).patternLine("###").patternLine("###").addCriterion("has_stone_bricks", hasItem(Blocks.STONE_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.NETHER_BRICK_WALL, 6).key('#', Blocks.NETHER_BRICKS).patternLine("###").patternLine("###").addCriterion("has_nether_bricks", hasItem(Blocks.NETHER_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.ANDESITE_WALL, 6).key('#', Blocks.ANDESITE).patternLine("###").patternLine("###").addCriterion("has_andesite", hasItem(Blocks.ANDESITE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.RED_NETHER_BRICK_WALL, 6).key('#', Blocks.RED_NETHER_BRICKS).patternLine("###").patternLine("###").addCriterion("has_red_nether_bricks", hasItem(Blocks.RED_NETHER_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SANDSTONE_WALL, 6).key('#', Blocks.SANDSTONE).patternLine("###").patternLine("###").addCriterion("has_sandstone", hasItem(Blocks.SANDSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.END_STONE_BRICK_WALL, 6).key('#', Blocks.END_STONE_BRICKS).patternLine("###").patternLine("###").addCriterion("has_end_stone_bricks", hasItem(Blocks.END_STONE_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.DIORITE_WALL, 6).key('#', Blocks.DIORITE).patternLine("###").patternLine("###").addCriterion("has_diorite", hasItem(Blocks.DIORITE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.CREEPER_BANNER_PATTERN).addIngredient(Items.PAPER).addIngredient(Items.CREEPER_HEAD).addCriterion("has_creeper_head", hasItem(Items.CREEPER_HEAD)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.SKULL_BANNER_PATTERN).addIngredient(Items.PAPER).addIngredient(Items.WITHER_SKELETON_SKULL).addCriterion("has_wither_skeleton_skull", hasItem(Items.WITHER_SKELETON_SKULL)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.FLOWER_BANNER_PATTERN).addIngredient(Items.PAPER).addIngredient(Blocks.OXEYE_DAISY).addCriterion("has_oxeye_daisy", hasItem(Blocks.OXEYE_DAISY)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.MOJANG_BANNER_PATTERN).addIngredient(Items.PAPER).addIngredient(Items.ENCHANTED_GOLDEN_APPLE).addCriterion("has_enchanted_golden_apple", hasItem(Items.ENCHANTED_GOLDEN_APPLE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SCAFFOLDING, 6).key('~', Items.STRING).key('I', Blocks.BAMBOO).patternLine("I~I").patternLine("I I").patternLine("I I").addCriterion("has_bamboo", hasItem(Blocks.BAMBOO)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.GRINDSTONE).key('I', Items.STICK).key('-', Blocks.STONE_SLAB).key('#', ItemTags.PLANKS).patternLine("I-I").patternLine("# #").addCriterion("has_stone_slab", hasItem(Blocks.STONE_SLAB)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.BLAST_FURNACE).key('#', Blocks.SMOOTH_STONE).key('X', Blocks.FURNACE).key('I', Items.IRON_INGOT).patternLine("III").patternLine("IXI").patternLine("###").addCriterion("has_smooth_stone", hasItem(Blocks.SMOOTH_STONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SMOKER).key('#', ItemTags.LOGS).key('X', Blocks.FURNACE).patternLine(" # ").patternLine("#X#").patternLine(" # ").addCriterion("has_furnace", hasItem(Blocks.FURNACE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CARTOGRAPHY_TABLE).key('#', ItemTags.PLANKS).key('@', Items.PAPER).patternLine("@@").patternLine("##").patternLine("##").addCriterion("has_paper", hasItem(Items.PAPER)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.SMITHING_TABLE).key('#', ItemTags.PLANKS).key('@', Items.IRON_INGOT).patternLine("@@").patternLine("##").patternLine("##").addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.FLETCHING_TABLE).key('#', ItemTags.PLANKS).key('@', Items.FLINT).patternLine("@@").patternLine("##").patternLine("##").addCriterion("has_flint", hasItem(Items.FLINT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.STONECUTTER).key('I', Items.IRON_INGOT).key('#', Blocks.STONE).patternLine(" I ").patternLine("###").addCriterion("has_stone", hasItem(Blocks.STONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.LODESTONE).key('S', Items.CHISELED_STONE_BRICKS).key('#', Items.NETHERITE_INGOT).patternLine("SSS").patternLine("S#S").patternLine("SSS").addCriterion("has_netherite_ingot", hasItem(Items.NETHERITE_INGOT)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.NETHERITE_BLOCK).key('#', Items.NETHERITE_INGOT).patternLine("###").patternLine("###").patternLine("###").addCriterion("has_netherite_ingot", hasItem(Items.NETHERITE_INGOT)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Items.NETHERITE_INGOT, 9).addIngredient(Blocks.NETHERITE_BLOCK).setGroup("netherite_ingot").addCriterion("has_netherite_block", hasItem(Blocks.NETHERITE_BLOCK)).build(consumer, "netherite_ingot_from_netherite_block");
        ShapelessRecipeBuilder.shapelessRecipe(Items.NETHERITE_INGOT).addIngredient(Items.NETHERITE_SCRAP, 4).addIngredient(Items.GOLD_INGOT, 4).setGroup("netherite_ingot").addCriterion("has_netherite_scrap", hasItem(Items.NETHERITE_SCRAP)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.RESPAWN_ANCHOR).key('O', Blocks.CRYING_OBSIDIAN).key('G', Blocks.GLOWSTONE).patternLine("OOO").patternLine("GGG").patternLine("OOO").addCriterion("has_obsidian", hasItem(Blocks.CRYING_OBSIDIAN)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.BLACKSTONE_STAIRS, 4).key('#', Blocks.BLACKSTONE).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_blackstone", hasItem(Blocks.BLACKSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_BLACKSTONE_STAIRS, 4).key('#', Blocks.POLISHED_BLACKSTONE).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_polished_blackstone", hasItem(Blocks.POLISHED_BLACKSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, 4).key('#', Blocks.POLISHED_BLACKSTONE_BRICKS).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_polished_blackstone_bricks", hasItem(Blocks.POLISHED_BLACKSTONE_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.BLACKSTONE_SLAB, 6).key('#', Blocks.BLACKSTONE).patternLine("###").addCriterion("has_blackstone", hasItem(Blocks.BLACKSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_BLACKSTONE_SLAB, 6).key('#', Blocks.POLISHED_BLACKSTONE).patternLine("###").addCriterion("has_polished_blackstone", hasItem(Blocks.POLISHED_BLACKSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 6).key('#', Blocks.POLISHED_BLACKSTONE_BRICKS).patternLine("###").addCriterion("has_polished_blackstone_bricks", hasItem(Blocks.POLISHED_BLACKSTONE_BRICKS)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_BLACKSTONE, 4).key('S', Blocks.BLACKSTONE).patternLine("SS").patternLine("SS").addCriterion("has_blackstone", hasItem(Blocks.BLACKSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_BLACKSTONE_BRICKS, 4).key('#', Blocks.POLISHED_BLACKSTONE).patternLine("##").patternLine("##").addCriterion("has_polished_blackstone", hasItem(Blocks.POLISHED_BLACKSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CHISELED_POLISHED_BLACKSTONE).key('#', Blocks.POLISHED_BLACKSTONE_SLAB).patternLine("#").patternLine("#").addCriterion("has_polished_blackstone", hasItem(Blocks.POLISHED_BLACKSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.BLACKSTONE_WALL, 6).key('#', Blocks.BLACKSTONE).patternLine("###").patternLine("###").addCriterion("has_blackstone", hasItem(Blocks.BLACKSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_BLACKSTONE_WALL, 6).key('#', Blocks.POLISHED_BLACKSTONE).patternLine("###").patternLine("###").addCriterion("has_polished_blackstone", hasItem(Blocks.POLISHED_BLACKSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_BLACKSTONE_BRICK_WALL, 6).key('#', Blocks.POLISHED_BLACKSTONE_BRICKS).patternLine("###").patternLine("###").addCriterion("has_polished_blackstone_bricks", hasItem(Blocks.POLISHED_BLACKSTONE_BRICKS)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(Blocks.POLISHED_BLACKSTONE_BUTTON).addIngredient(Blocks.POLISHED_BLACKSTONE).addCriterion("has_polished_blackstone", hasItem(Blocks.POLISHED_BLACKSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE).key('#', Blocks.POLISHED_BLACKSTONE).patternLine("##").addCriterion("has_polished_blackstone", hasItem(Blocks.POLISHED_BLACKSTONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Blocks.CHAIN).key('I', Items.IRON_INGOT).key('N', Items.IRON_NUGGET).patternLine("N").patternLine("I").patternLine("N").addCriterion("has_iron_nugget", hasItem(Items.IRON_NUGGET)).addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT)).build(consumer);
        CustomRecipeBuilder.customRecipe(IRecipeSerializer.CRAFTING_SPECIAL_ARMORDYE).build(consumer, "armor_dye");
        CustomRecipeBuilder.customRecipe(IRecipeSerializer.CRAFTING_SPECIAL_BANNERDUPLICATE).build(consumer, "banner_duplicate");
        CustomRecipeBuilder.customRecipe(IRecipeSerializer.CRAFTING_SPECIAL_BOOKCLONING).build(consumer, "book_cloning");
        CustomRecipeBuilder.customRecipe(IRecipeSerializer.CRAFTING_SPECIAL_FIREWORK_ROCKET).build(consumer, "firework_rocket");
        CustomRecipeBuilder.customRecipe(IRecipeSerializer.CRAFTING_SPECIAL_FIREWORK_STAR).build(consumer, "firework_star");
        CustomRecipeBuilder.customRecipe(IRecipeSerializer.CRAFTING_SPECIAL_FIREWORK_STAR_FADE).build(consumer, "firework_star_fade");
        CustomRecipeBuilder.customRecipe(IRecipeSerializer.CRAFTING_SPECIAL_MAPCLONING).build(consumer, "map_cloning");
        CustomRecipeBuilder.customRecipe(IRecipeSerializer.CRAFTING_SPECIAL_MAPEXTENDING).build(consumer, "map_extending");
        CustomRecipeBuilder.customRecipe(IRecipeSerializer.CRAFTING_SPECIAL_REPAIRITEM).build(consumer, "repair_item");
        CustomRecipeBuilder.customRecipe(IRecipeSerializer.CRAFTING_SPECIAL_SHIELD).build(consumer, "shield_decoration");
        CustomRecipeBuilder.customRecipe(IRecipeSerializer.CRAFTING_SPECIAL_SHULKERBOXCOLORING).build(consumer, "shulker_box_coloring");
        CustomRecipeBuilder.customRecipe(IRecipeSerializer.CRAFTING_SPECIAL_TIPPEDARROW).build(consumer, "tipped_arrow");
        CustomRecipeBuilder.customRecipe(IRecipeSerializer.CRAFTING_SPECIAL_SUSPICIOUSSTEW).build(consumer, "suspicious_stew");
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Items.POTATO), Items.BAKED_POTATO, 0.35F, 200).addCriterion("has_potato", hasItem(Items.POTATO)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Items.CLAY_BALL), Items.BRICK, 0.3F, 200).addCriterion("has_clay_ball", hasItem(Items.CLAY_BALL)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromTag(ItemTags.LOGS_THAT_BURN), Items.CHARCOAL, 0.15F, 200).addCriterion("has_log", hasItem(ItemTags.LOGS_THAT_BURN)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Items.CHORUS_FRUIT), Items.POPPED_CHORUS_FRUIT, 0.1F, 200).addCriterion("has_chorus_fruit", hasItem(Items.CHORUS_FRUIT)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.COAL_ORE.asItem()), Items.COAL, 0.1F, 200).addCriterion("has_coal_ore", hasItem(Blocks.COAL_ORE)).build(consumer, "coal_from_smelting");
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Items.BEEF), Items.COOKED_BEEF, 0.35F, 200).addCriterion("has_beef", hasItem(Items.BEEF)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Items.CHICKEN), Items.COOKED_CHICKEN, 0.35F, 200).addCriterion("has_chicken", hasItem(Items.CHICKEN)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Items.COD), Items.COOKED_COD, 0.35F, 200).addCriterion("has_cod", hasItem(Items.COD)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.KELP), Items.DRIED_KELP, 0.1F, 200).addCriterion("has_kelp", hasItem(Blocks.KELP)).build(consumer, "dried_kelp_from_smelting");
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Items.SALMON), Items.COOKED_SALMON, 0.35F, 200).addCriterion("has_salmon", hasItem(Items.SALMON)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Items.MUTTON), Items.COOKED_MUTTON, 0.35F, 200).addCriterion("has_mutton", hasItem(Items.MUTTON)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Items.PORKCHOP), Items.COOKED_PORKCHOP, 0.35F, 200).addCriterion("has_porkchop", hasItem(Items.PORKCHOP)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Items.RABBIT), Items.COOKED_RABBIT, 0.35F, 200).addCriterion("has_rabbit", hasItem(Items.RABBIT)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.DIAMOND_ORE.asItem()), Items.DIAMOND, 1.0F, 200).addCriterion("has_diamond_ore", hasItem(Blocks.DIAMOND_ORE)).build(consumer, "diamond_from_smelting");
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.LAPIS_ORE.asItem()), Items.LAPIS_LAZULI, 0.2F, 200).addCriterion("has_lapis_ore", hasItem(Blocks.LAPIS_ORE)).build(consumer, "lapis_from_smelting");
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.EMERALD_ORE.asItem()), Items.EMERALD, 1.0F, 200).addCriterion("has_emerald_ore", hasItem(Blocks.EMERALD_ORE)).build(consumer, "emerald_from_smelting");
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromTag(ItemTags.SAND), Blocks.GLASS.asItem(), 0.1F, 200).addCriterion("has_sand", hasItem(ItemTags.SAND)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromTag(ItemTags.GOLD_ORES), Items.GOLD_INGOT, 1.0F, 200).addCriterion("has_gold_ore", hasItem(ItemTags.GOLD_ORES)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.SEA_PICKLE.asItem()), Items.LIME_DYE, 0.1F, 200).addCriterion("has_sea_pickle", hasItem(Blocks.SEA_PICKLE)).build(consumer, "lime_dye_from_smelting");
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.CACTUS.asItem()), Items.GREEN_DYE, 1.0F, 200).addCriterion("has_cactus", hasItem(Blocks.CACTUS)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SWORD, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR), Items.GOLD_NUGGET, 0.1F, 200).addCriterion("has_golden_pickaxe", hasItem(Items.GOLDEN_PICKAXE)).addCriterion("has_golden_shovel", hasItem(Items.GOLDEN_SHOVEL)).addCriterion("has_golden_axe", hasItem(Items.GOLDEN_AXE)).addCriterion("has_golden_hoe", hasItem(Items.GOLDEN_HOE)).addCriterion("has_golden_sword", hasItem(Items.GOLDEN_SWORD)).addCriterion("has_golden_helmet", hasItem(Items.GOLDEN_HELMET)).addCriterion("has_golden_chestplate", hasItem(Items.GOLDEN_CHESTPLATE)).addCriterion("has_golden_leggings", hasItem(Items.GOLDEN_LEGGINGS)).addCriterion("has_golden_boots", hasItem(Items.GOLDEN_BOOTS)).addCriterion("has_golden_horse_armor", hasItem(Items.GOLDEN_HORSE_ARMOR)).build(consumer, "gold_nugget_from_smelting");
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SWORD, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_HORSE_ARMOR, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS), Items.IRON_NUGGET, 0.1F, 200).addCriterion("has_iron_pickaxe", hasItem(Items.IRON_PICKAXE)).addCriterion("has_iron_shovel", hasItem(Items.IRON_SHOVEL)).addCriterion("has_iron_axe", hasItem(Items.IRON_AXE)).addCriterion("has_iron_hoe", hasItem(Items.IRON_HOE)).addCriterion("has_iron_sword", hasItem(Items.IRON_SWORD)).addCriterion("has_iron_helmet", hasItem(Items.IRON_HELMET)).addCriterion("has_iron_chestplate", hasItem(Items.IRON_CHESTPLATE)).addCriterion("has_iron_leggings", hasItem(Items.IRON_LEGGINGS)).addCriterion("has_iron_boots", hasItem(Items.IRON_BOOTS)).addCriterion("has_iron_horse_armor", hasItem(Items.IRON_HORSE_ARMOR)).addCriterion("has_chainmail_helmet", hasItem(Items.CHAINMAIL_HELMET)).addCriterion("has_chainmail_chestplate", hasItem(Items.CHAINMAIL_CHESTPLATE)).addCriterion("has_chainmail_leggings", hasItem(Items.CHAINMAIL_LEGGINGS)).addCriterion("has_chainmail_boots", hasItem(Items.CHAINMAIL_BOOTS)).build(consumer, "iron_nugget_from_smelting");
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.IRON_ORE.asItem()), Items.IRON_INGOT, 0.7F, 200).addCriterion("has_iron_ore", hasItem(Blocks.IRON_ORE.asItem())).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.CLAY), Blocks.TERRACOTTA.asItem(), 0.35F, 200).addCriterion("has_clay_block", hasItem(Blocks.CLAY)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.NETHERRACK), Items.NETHER_BRICK, 0.1F, 200).addCriterion("has_netherrack", hasItem(Blocks.NETHERRACK)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2F, 200).addCriterion("has_nether_quartz_ore", hasItem(Blocks.NETHER_QUARTZ_ORE)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.REDSTONE_ORE), Items.REDSTONE, 0.7F, 200).addCriterion("has_redstone_ore", hasItem(Blocks.REDSTONE_ORE)).build(consumer, "redstone_from_smelting");
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.WET_SPONGE), Blocks.SPONGE.asItem(), 0.15F, 200).addCriterion("has_wet_sponge", hasItem(Blocks.WET_SPONGE)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.COBBLESTONE), Blocks.STONE.asItem(), 0.1F, 200).addCriterion("has_cobblestone", hasItem(Blocks.COBBLESTONE)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.STONE), Blocks.SMOOTH_STONE.asItem(), 0.1F, 200).addCriterion("has_stone", hasItem(Blocks.STONE)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.SANDSTONE), Blocks.SMOOTH_SANDSTONE.asItem(), 0.1F, 200).addCriterion("has_sandstone", hasItem(Blocks.SANDSTONE)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE.asItem(), 0.1F, 200).addCriterion("has_red_sandstone", hasItem(Blocks.RED_SANDSTONE)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.QUARTZ_BLOCK), Blocks.SMOOTH_QUARTZ.asItem(), 0.1F, 200).addCriterion("has_quartz_block", hasItem(Blocks.QUARTZ_BLOCK)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.STONE_BRICKS), Blocks.CRACKED_STONE_BRICKS.asItem(), 0.1F, 200).addCriterion("has_stone_bricks", hasItem(Blocks.STONE_BRICKS)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.BLACK_TERRACOTTA), Blocks.BLACK_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).addCriterion("has_black_terracotta", hasItem(Blocks.BLACK_TERRACOTTA)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.BLUE_TERRACOTTA), Blocks.BLUE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).addCriterion("has_blue_terracotta", hasItem(Blocks.BLUE_TERRACOTTA)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.BROWN_TERRACOTTA), Blocks.BROWN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).addCriterion("has_brown_terracotta", hasItem(Blocks.BROWN_TERRACOTTA)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.CYAN_TERRACOTTA), Blocks.CYAN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).addCriterion("has_cyan_terracotta", hasItem(Blocks.CYAN_TERRACOTTA)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.GRAY_TERRACOTTA), Blocks.GRAY_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).addCriterion("has_gray_terracotta", hasItem(Blocks.GRAY_TERRACOTTA)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.GREEN_TERRACOTTA), Blocks.GREEN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).addCriterion("has_green_terracotta", hasItem(Blocks.GREEN_TERRACOTTA)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.LIGHT_BLUE_TERRACOTTA), Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).addCriterion("has_light_blue_terracotta", hasItem(Blocks.LIGHT_BLUE_TERRACOTTA)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.LIGHT_GRAY_TERRACOTTA), Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).addCriterion("has_light_gray_terracotta", hasItem(Blocks.LIGHT_GRAY_TERRACOTTA)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.LIME_TERRACOTTA), Blocks.LIME_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).addCriterion("has_lime_terracotta", hasItem(Blocks.LIME_TERRACOTTA)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.MAGENTA_TERRACOTTA), Blocks.MAGENTA_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).addCriterion("has_magenta_terracotta", hasItem(Blocks.MAGENTA_TERRACOTTA)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.ORANGE_TERRACOTTA), Blocks.ORANGE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).addCriterion("has_orange_terracotta", hasItem(Blocks.ORANGE_TERRACOTTA)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.PINK_TERRACOTTA), Blocks.PINK_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).addCriterion("has_pink_terracotta", hasItem(Blocks.PINK_TERRACOTTA)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.PURPLE_TERRACOTTA), Blocks.PURPLE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).addCriterion("has_purple_terracotta", hasItem(Blocks.PURPLE_TERRACOTTA)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.RED_TERRACOTTA), Blocks.RED_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).addCriterion("has_red_terracotta", hasItem(Blocks.RED_TERRACOTTA)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.WHITE_TERRACOTTA), Blocks.WHITE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).addCriterion("has_white_terracotta", hasItem(Blocks.WHITE_TERRACOTTA)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.YELLOW_TERRACOTTA), Blocks.YELLOW_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).addCriterion("has_yellow_terracotta", hasItem(Blocks.YELLOW_TERRACOTTA)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.ANCIENT_DEBRIS), Items.NETHERITE_SCRAP, 2.0F, 200).addCriterion("has_ancient_debris", hasItem(Blocks.ANCIENT_DEBRIS)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.asItem(), 0.1F, 200).addCriterion("has_blackstone_bricks", hasItem(Blocks.POLISHED_BLACKSTONE_BRICKS)).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.NETHER_BRICKS), Blocks.CRACKED_NETHER_BRICKS.asItem(), 0.1F, 200).addCriterion("has_nether_bricks", hasItem(Blocks.NETHER_BRICKS)).build(consumer);
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(Blocks.IRON_ORE.asItem()), Items.IRON_INGOT, 0.7F, 100).addCriterion("has_iron_ore", hasItem(Blocks.IRON_ORE.asItem())).build(consumer, "iron_ingot_from_blasting");
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromTag(ItemTags.GOLD_ORES), Items.GOLD_INGOT, 1.0F, 100).addCriterion("has_gold_ore", hasItem(ItemTags.GOLD_ORES)).build(consumer, "gold_ingot_from_blasting");
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(Blocks.DIAMOND_ORE.asItem()), Items.DIAMOND, 1.0F, 100).addCriterion("has_diamond_ore", hasItem(Blocks.DIAMOND_ORE)).build(consumer, "diamond_from_blasting");
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(Blocks.LAPIS_ORE.asItem()), Items.LAPIS_LAZULI, 0.2F, 100).addCriterion("has_lapis_ore", hasItem(Blocks.LAPIS_ORE)).build(consumer, "lapis_from_blasting");
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(Blocks.REDSTONE_ORE), Items.REDSTONE, 0.7F, 100).addCriterion("has_redstone_ore", hasItem(Blocks.REDSTONE_ORE)).build(consumer, "redstone_from_blasting");
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(Blocks.COAL_ORE.asItem()), Items.COAL, 0.1F, 100).addCriterion("has_coal_ore", hasItem(Blocks.COAL_ORE)).build(consumer, "coal_from_blasting");
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(Blocks.EMERALD_ORE.asItem()), Items.EMERALD, 1.0F, 100).addCriterion("has_emerald_ore", hasItem(Blocks.EMERALD_ORE)).build(consumer, "emerald_from_blasting");
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2F, 100).addCriterion("has_nether_quartz_ore", hasItem(Blocks.NETHER_QUARTZ_ORE)).build(consumer, "quartz_from_blasting");
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SWORD, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR), Items.GOLD_NUGGET, 0.1F, 100).addCriterion("has_golden_pickaxe", hasItem(Items.GOLDEN_PICKAXE)).addCriterion("has_golden_shovel", hasItem(Items.GOLDEN_SHOVEL)).addCriterion("has_golden_axe", hasItem(Items.GOLDEN_AXE)).addCriterion("has_golden_hoe", hasItem(Items.GOLDEN_HOE)).addCriterion("has_golden_sword", hasItem(Items.GOLDEN_SWORD)).addCriterion("has_golden_helmet", hasItem(Items.GOLDEN_HELMET)).addCriterion("has_golden_chestplate", hasItem(Items.GOLDEN_CHESTPLATE)).addCriterion("has_golden_leggings", hasItem(Items.GOLDEN_LEGGINGS)).addCriterion("has_golden_boots", hasItem(Items.GOLDEN_BOOTS)).addCriterion("has_golden_horse_armor", hasItem(Items.GOLDEN_HORSE_ARMOR)).build(consumer, "gold_nugget_from_blasting");
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SWORD, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_HORSE_ARMOR, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS), Items.IRON_NUGGET, 0.1F, 100).addCriterion("has_iron_pickaxe", hasItem(Items.IRON_PICKAXE)).addCriterion("has_iron_shovel", hasItem(Items.IRON_SHOVEL)).addCriterion("has_iron_axe", hasItem(Items.IRON_AXE)).addCriterion("has_iron_hoe", hasItem(Items.IRON_HOE)).addCriterion("has_iron_sword", hasItem(Items.IRON_SWORD)).addCriterion("has_iron_helmet", hasItem(Items.IRON_HELMET)).addCriterion("has_iron_chestplate", hasItem(Items.IRON_CHESTPLATE)).addCriterion("has_iron_leggings", hasItem(Items.IRON_LEGGINGS)).addCriterion("has_iron_boots", hasItem(Items.IRON_BOOTS)).addCriterion("has_iron_horse_armor", hasItem(Items.IRON_HORSE_ARMOR)).addCriterion("has_chainmail_helmet", hasItem(Items.CHAINMAIL_HELMET)).addCriterion("has_chainmail_chestplate", hasItem(Items.CHAINMAIL_CHESTPLATE)).addCriterion("has_chainmail_leggings", hasItem(Items.CHAINMAIL_LEGGINGS)).addCriterion("has_chainmail_boots", hasItem(Items.CHAINMAIL_BOOTS)).build(consumer, "iron_nugget_from_blasting");
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(Blocks.ANCIENT_DEBRIS), Items.NETHERITE_SCRAP, 2.0F, 100).addCriterion("has_ancient_debris", hasItem(Blocks.ANCIENT_DEBRIS)).build(consumer, "netherite_scrap_from_blasting");
        cookingRecipesForMethod(consumer, "smoking", IRecipeSerializer.SMOKING, 100);
        cookingRecipesForMethod(consumer, "campfire_cooking", IRecipeSerializer.CAMPFIRE_COOKING, 600);
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.STONE), Blocks.STONE_SLAB, 2).addCriterion("has_stone", hasItem(Blocks.STONE)).build(consumer, "stone_slab_from_stone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.STONE), Blocks.STONE_STAIRS).addCriterion("has_stone", hasItem(Blocks.STONE)).build(consumer, "stone_stairs_from_stone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.STONE), Blocks.STONE_BRICKS).addCriterion("has_stone", hasItem(Blocks.STONE)).build(consumer, "stone_bricks_from_stone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.STONE), Blocks.STONE_BRICK_SLAB, 2).addCriterion("has_stone", hasItem(Blocks.STONE)).build(consumer, "stone_brick_slab_from_stone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.STONE), Blocks.STONE_BRICK_STAIRS).addCriterion("has_stone", hasItem(Blocks.STONE)).build(consumer, "stone_brick_stairs_from_stone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.STONE), Blocks.CHISELED_STONE_BRICKS).addCriterion("has_stone", hasItem(Blocks.STONE)).build(consumer, "chiseled_stone_bricks_stone_from_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.STONE), Blocks.STONE_BRICK_WALL).addCriterion("has_stone", hasItem(Blocks.STONE)).build(consumer, "stone_brick_walls_from_stone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.SANDSTONE), Blocks.CUT_SANDSTONE).addCriterion("has_sandstone", hasItem(Blocks.SANDSTONE)).build(consumer, "cut_sandstone_from_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.SANDSTONE), Blocks.SANDSTONE_SLAB, 2).addCriterion("has_sandstone", hasItem(Blocks.SANDSTONE)).build(consumer, "sandstone_slab_from_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.SANDSTONE), Blocks.CUT_SANDSTONE_SLAB, 2).addCriterion("has_sandstone", hasItem(Blocks.SANDSTONE)).build(consumer, "cut_sandstone_slab_from_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.CUT_SANDSTONE), Blocks.CUT_SANDSTONE_SLAB, 2).addCriterion("has_cut_sandstone", hasItem(Blocks.SANDSTONE)).build(consumer, "cut_sandstone_slab_from_cut_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.SANDSTONE), Blocks.SANDSTONE_STAIRS).addCriterion("has_sandstone", hasItem(Blocks.SANDSTONE)).build(consumer, "sandstone_stairs_from_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.SANDSTONE), Blocks.SANDSTONE_WALL).addCriterion("has_sandstone", hasItem(Blocks.SANDSTONE)).build(consumer, "sandstone_wall_from_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.SANDSTONE), Blocks.CHISELED_SANDSTONE).addCriterion("has_sandstone", hasItem(Blocks.SANDSTONE)).build(consumer, "chiseled_sandstone_from_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE).addCriterion("has_red_sandstone", hasItem(Blocks.RED_SANDSTONE)).build(consumer, "cut_red_sandstone_from_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_SLAB, 2).addCriterion("has_red_sandstone", hasItem(Blocks.RED_SANDSTONE)).build(consumer, "red_sandstone_slab_from_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE_SLAB, 2).addCriterion("has_red_sandstone", hasItem(Blocks.RED_SANDSTONE)).build(consumer, "cut_red_sandstone_slab_from_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.CUT_RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE_SLAB, 2).addCriterion("has_cut_red_sandstone", hasItem(Blocks.RED_SANDSTONE)).build(consumer, "cut_red_sandstone_slab_from_cut_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_STAIRS).addCriterion("has_red_sandstone", hasItem(Blocks.RED_SANDSTONE)).build(consumer, "red_sandstone_stairs_from_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_WALL).addCriterion("has_red_sandstone", hasItem(Blocks.RED_SANDSTONE)).build(consumer, "red_sandstone_wall_from_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.RED_SANDSTONE), Blocks.CHISELED_RED_SANDSTONE).addCriterion("has_red_sandstone", hasItem(Blocks.RED_SANDSTONE)).build(consumer, "chiseled_red_sandstone_from_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_SLAB, 2).addCriterion("has_quartz_block", hasItem(Blocks.QUARTZ_BLOCK)).build(consumer, "quartz_slab_from_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_STAIRS).addCriterion("has_quartz_block", hasItem(Blocks.QUARTZ_BLOCK)).build(consumer, "quartz_stairs_from_quartz_block_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_PILLAR).addCriterion("has_quartz_block", hasItem(Blocks.QUARTZ_BLOCK)).build(consumer, "quartz_pillar_from_quartz_block_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.QUARTZ_BLOCK), Blocks.CHISELED_QUARTZ_BLOCK).addCriterion("has_quartz_block", hasItem(Blocks.QUARTZ_BLOCK)).build(consumer, "chiseled_quartz_block_from_quartz_block_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_BRICKS).addCriterion("has_quartz_block", hasItem(Blocks.QUARTZ_BLOCK)).build(consumer, "quartz_bricks_from_quartz_block_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.COBBLESTONE), Blocks.COBBLESTONE_STAIRS).addCriterion("has_cobblestone", hasItem(Blocks.COBBLESTONE)).build(consumer, "cobblestone_stairs_from_cobblestone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.COBBLESTONE), Blocks.COBBLESTONE_SLAB, 2).addCriterion("has_cobblestone", hasItem(Blocks.COBBLESTONE)).build(consumer, "cobblestone_slab_from_cobblestone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.COBBLESTONE), Blocks.COBBLESTONE_WALL).addCriterion("has_cobblestone", hasItem(Blocks.COBBLESTONE)).build(consumer, "cobblestone_wall_from_cobblestone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_SLAB, 2).addCriterion("has_stone_bricks", hasItem(Blocks.STONE_BRICKS)).build(consumer, "stone_brick_slab_from_stone_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_STAIRS).addCriterion("has_stone_bricks", hasItem(Blocks.STONE_BRICKS)).build(consumer, "stone_brick_stairs_from_stone_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_WALL).addCriterion("has_stone_bricks", hasItem(Blocks.STONE_BRICKS)).build(consumer, "stone_brick_wall_from_stone_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.STONE_BRICKS), Blocks.CHISELED_STONE_BRICKS).addCriterion("has_stone_bricks", hasItem(Blocks.STONE_BRICKS)).build(consumer, "chiseled_stone_bricks_from_stone_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.BRICKS), Blocks.BRICK_SLAB, 2).addCriterion("has_bricks", hasItem(Blocks.BRICKS)).build(consumer, "brick_slab_from_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.BRICKS), Blocks.BRICK_STAIRS).addCriterion("has_bricks", hasItem(Blocks.BRICKS)).build(consumer, "brick_stairs_from_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.BRICKS), Blocks.BRICK_WALL).addCriterion("has_bricks", hasItem(Blocks.BRICKS)).build(consumer, "brick_wall_from_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_SLAB, 2).addCriterion("has_nether_bricks", hasItem(Blocks.NETHER_BRICKS)).build(consumer, "nether_brick_slab_from_nether_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_STAIRS).addCriterion("has_nether_bricks", hasItem(Blocks.NETHER_BRICKS)).build(consumer, "nether_brick_stairs_from_nether_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_WALL).addCriterion("has_nether_bricks", hasItem(Blocks.NETHER_BRICKS)).build(consumer, "nether_brick_wall_from_nether_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.NETHER_BRICKS), Blocks.CHISELED_NETHER_BRICKS).addCriterion("has_nether_bricks", hasItem(Blocks.NETHER_BRICKS)).build(consumer, "chiseled_nether_bricks_from_nether_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_SLAB, 2).addCriterion("has_nether_bricks", hasItem(Blocks.RED_NETHER_BRICKS)).build(consumer, "red_nether_brick_slab_from_red_nether_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_STAIRS).addCriterion("has_nether_bricks", hasItem(Blocks.RED_NETHER_BRICKS)).build(consumer, "red_nether_brick_stairs_from_red_nether_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_WALL).addCriterion("has_nether_bricks", hasItem(Blocks.RED_NETHER_BRICKS)).build(consumer, "red_nether_brick_wall_from_red_nether_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.PURPUR_BLOCK), Blocks.PURPUR_SLAB, 2).addCriterion("has_purpur_block", hasItem(Blocks.PURPUR_BLOCK)).build(consumer, "purpur_slab_from_purpur_block_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.PURPUR_BLOCK), Blocks.PURPUR_STAIRS).addCriterion("has_purpur_block", hasItem(Blocks.PURPUR_BLOCK)).build(consumer, "purpur_stairs_from_purpur_block_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.PURPUR_BLOCK), Blocks.PURPUR_PILLAR).addCriterion("has_purpur_block", hasItem(Blocks.PURPUR_BLOCK)).build(consumer, "purpur_pillar_from_purpur_block_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.PRISMARINE), Blocks.PRISMARINE_SLAB, 2).addCriterion("has_prismarine", hasItem(Blocks.PRISMARINE)).build(consumer, "prismarine_slab_from_prismarine_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.PRISMARINE), Blocks.PRISMARINE_STAIRS).addCriterion("has_prismarine", hasItem(Blocks.PRISMARINE)).build(consumer, "prismarine_stairs_from_prismarine_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.PRISMARINE), Blocks.PRISMARINE_WALL).addCriterion("has_prismarine", hasItem(Blocks.PRISMARINE)).build(consumer, "prismarine_wall_from_prismarine_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_SLAB, 2).addCriterion("has_prismarine_brick", hasItem(Blocks.PRISMARINE_BRICKS)).build(consumer, "prismarine_brick_slab_from_prismarine_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_STAIRS).addCriterion("has_prismarine_brick", hasItem(Blocks.PRISMARINE_BRICKS)).build(consumer, "prismarine_brick_stairs_from_prismarine_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.DARK_PRISMARINE), Blocks.DARK_PRISMARINE_SLAB, 2).addCriterion("has_dark_prismarine", hasItem(Blocks.DARK_PRISMARINE)).build(consumer, "dark_prismarine_slab_from_dark_prismarine_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.DARK_PRISMARINE), Blocks.DARK_PRISMARINE_STAIRS).addCriterion("has_dark_prismarine", hasItem(Blocks.DARK_PRISMARINE)).build(consumer, "dark_prismarine_stairs_from_dark_prismarine_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.ANDESITE), Blocks.ANDESITE_SLAB, 2).addCriterion("has_andesite", hasItem(Blocks.ANDESITE)).build(consumer, "andesite_slab_from_andesite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.ANDESITE), Blocks.ANDESITE_STAIRS).addCriterion("has_andesite", hasItem(Blocks.ANDESITE)).build(consumer, "andesite_stairs_from_andesite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.ANDESITE), Blocks.ANDESITE_WALL).addCriterion("has_andesite", hasItem(Blocks.ANDESITE)).build(consumer, "andesite_wall_from_andesite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE).addCriterion("has_andesite", hasItem(Blocks.ANDESITE)).build(consumer, "polished_andesite_from_andesite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE_SLAB, 2).addCriterion("has_andesite", hasItem(Blocks.ANDESITE)).build(consumer, "polished_andesite_slab_from_andesite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE_STAIRS).addCriterion("has_andesite", hasItem(Blocks.ANDESITE)).build(consumer, "polished_andesite_stairs_from_andesite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_ANDESITE), Blocks.POLISHED_ANDESITE_SLAB, 2).addCriterion("has_polished_andesite", hasItem(Blocks.POLISHED_ANDESITE)).build(consumer, "polished_andesite_slab_from_polished_andesite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_ANDESITE), Blocks.POLISHED_ANDESITE_STAIRS).addCriterion("has_polished_andesite", hasItem(Blocks.POLISHED_ANDESITE)).build(consumer, "polished_andesite_stairs_from_polished_andesite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.BASALT), Blocks.POLISHED_BASALT).addCriterion("has_basalt", hasItem(Blocks.BASALT)).build(consumer, "polished_basalt_from_basalt_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.GRANITE), Blocks.GRANITE_SLAB, 2).addCriterion("has_granite", hasItem(Blocks.GRANITE)).build(consumer, "granite_slab_from_granite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.GRANITE), Blocks.GRANITE_STAIRS).addCriterion("has_granite", hasItem(Blocks.GRANITE)).build(consumer, "granite_stairs_from_granite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.GRANITE), Blocks.GRANITE_WALL).addCriterion("has_granite", hasItem(Blocks.GRANITE)).build(consumer, "granite_wall_from_granite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.GRANITE), Blocks.POLISHED_GRANITE).addCriterion("has_granite", hasItem(Blocks.GRANITE)).build(consumer, "polished_granite_from_granite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.GRANITE), Blocks.POLISHED_GRANITE_SLAB, 2).addCriterion("has_granite", hasItem(Blocks.GRANITE)).build(consumer, "polished_granite_slab_from_granite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.GRANITE), Blocks.POLISHED_GRANITE_STAIRS).addCriterion("has_granite", hasItem(Blocks.GRANITE)).build(consumer, "polished_granite_stairs_from_granite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_GRANITE), Blocks.POLISHED_GRANITE_SLAB, 2).addCriterion("has_polished_granite", hasItem(Blocks.POLISHED_GRANITE)).build(consumer, "polished_granite_slab_from_polished_granite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_GRANITE), Blocks.POLISHED_GRANITE_STAIRS).addCriterion("has_polished_granite", hasItem(Blocks.POLISHED_GRANITE)).build(consumer, "polished_granite_stairs_from_polished_granite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.DIORITE), Blocks.DIORITE_SLAB, 2).addCriterion("has_diorite", hasItem(Blocks.DIORITE)).build(consumer, "diorite_slab_from_diorite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.DIORITE), Blocks.DIORITE_STAIRS).addCriterion("has_diorite", hasItem(Blocks.DIORITE)).build(consumer, "diorite_stairs_from_diorite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.DIORITE), Blocks.DIORITE_WALL).addCriterion("has_diorite", hasItem(Blocks.DIORITE)).build(consumer, "diorite_wall_from_diorite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.DIORITE), Blocks.POLISHED_DIORITE).addCriterion("has_diorite", hasItem(Blocks.DIORITE)).build(consumer, "polished_diorite_from_diorite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.DIORITE), Blocks.POLISHED_DIORITE_SLAB, 2).addCriterion("has_diorite", hasItem(Blocks.POLISHED_DIORITE)).build(consumer, "polished_diorite_slab_from_diorite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.DIORITE), Blocks.POLISHED_DIORITE_STAIRS).addCriterion("has_diorite", hasItem(Blocks.POLISHED_DIORITE)).build(consumer, "polished_diorite_stairs_from_diorite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_DIORITE), Blocks.POLISHED_DIORITE_SLAB, 2).addCriterion("has_polished_diorite", hasItem(Blocks.POLISHED_DIORITE)).build(consumer, "polished_diorite_slab_from_polished_diorite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_DIORITE), Blocks.POLISHED_DIORITE_STAIRS).addCriterion("has_polished_diorite", hasItem(Blocks.POLISHED_DIORITE)).build(consumer, "polished_diorite_stairs_from_polished_diorite_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_SLAB, 2).addCriterion("has_mossy_stone_bricks", hasItem(Blocks.MOSSY_STONE_BRICKS)).build(consumer, "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_STAIRS).addCriterion("has_mossy_stone_bricks", hasItem(Blocks.MOSSY_STONE_BRICKS)).build(consumer, "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_WALL).addCriterion("has_mossy_stone_bricks", hasItem(Blocks.MOSSY_STONE_BRICKS)).build(consumer, "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_SLAB, 2).addCriterion("has_mossy_cobblestone", hasItem(Blocks.MOSSY_COBBLESTONE)).build(consumer, "mossy_cobblestone_slab_from_mossy_cobblestone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_STAIRS).addCriterion("has_mossy_cobblestone", hasItem(Blocks.MOSSY_COBBLESTONE)).build(consumer, "mossy_cobblestone_stairs_from_mossy_cobblestone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_WALL).addCriterion("has_mossy_cobblestone", hasItem(Blocks.MOSSY_COBBLESTONE)).build(consumer, "mossy_cobblestone_wall_from_mossy_cobblestone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.SMOOTH_SANDSTONE), Blocks.SMOOTH_SANDSTONE_SLAB, 2).addCriterion("has_smooth_sandstone", hasItem(Blocks.SMOOTH_SANDSTONE)).build(consumer, "smooth_sandstone_slab_from_smooth_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.SMOOTH_SANDSTONE), Blocks.SMOOTH_SANDSTONE_STAIRS).addCriterion("has_mossy_cobblestone", hasItem(Blocks.SMOOTH_SANDSTONE)).build(consumer, "smooth_sandstone_stairs_from_smooth_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.SMOOTH_RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE_SLAB, 2).addCriterion("has_smooth_red_sandstone", hasItem(Blocks.SMOOTH_RED_SANDSTONE)).build(consumer, "smooth_red_sandstone_slab_from_smooth_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.SMOOTH_RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE_STAIRS).addCriterion("has_smooth_red_sandstone", hasItem(Blocks.SMOOTH_RED_SANDSTONE)).build(consumer, "smooth_red_sandstone_stairs_from_smooth_red_sandstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.SMOOTH_QUARTZ), Blocks.SMOOTH_QUARTZ_SLAB, 2).addCriterion("has_smooth_quartz", hasItem(Blocks.SMOOTH_QUARTZ)).build(consumer, "smooth_quartz_slab_from_smooth_quartz_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.SMOOTH_QUARTZ), Blocks.SMOOTH_QUARTZ_STAIRS).addCriterion("has_smooth_quartz", hasItem(Blocks.SMOOTH_QUARTZ)).build(consumer, "smooth_quartz_stairs_from_smooth_quartz_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_SLAB, 2).addCriterion("has_end_stone_brick", hasItem(Blocks.END_STONE_BRICKS)).build(consumer, "end_stone_brick_slab_from_end_stone_brick_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_STAIRS).addCriterion("has_end_stone_brick", hasItem(Blocks.END_STONE_BRICKS)).build(consumer, "end_stone_brick_stairs_from_end_stone_brick_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_WALL).addCriterion("has_end_stone_brick", hasItem(Blocks.END_STONE_BRICKS)).build(consumer, "end_stone_brick_wall_from_end_stone_brick_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.END_STONE), Blocks.END_STONE_BRICKS).addCriterion("has_end_stone", hasItem(Blocks.END_STONE)).build(consumer, "end_stone_bricks_from_end_stone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.END_STONE), Blocks.END_STONE_BRICK_SLAB, 2).addCriterion("has_end_stone", hasItem(Blocks.END_STONE)).build(consumer, "end_stone_brick_slab_from_end_stone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.END_STONE), Blocks.END_STONE_BRICK_STAIRS).addCriterion("has_end_stone", hasItem(Blocks.END_STONE)).build(consumer, "end_stone_brick_stairs_from_end_stone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.END_STONE), Blocks.END_STONE_BRICK_WALL).addCriterion("has_end_stone", hasItem(Blocks.END_STONE)).build(consumer, "end_stone_brick_wall_from_end_stone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.SMOOTH_STONE), Blocks.SMOOTH_STONE_SLAB, 2).addCriterion("has_smooth_stone", hasItem(Blocks.SMOOTH_STONE)).build(consumer, "smooth_stone_slab_from_smooth_stone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.BLACKSTONE), Blocks.BLACKSTONE_SLAB, 2).addCriterion("has_blackstone", hasItem(Blocks.BLACKSTONE)).build(consumer, "blackstone_slab_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.BLACKSTONE), Blocks.BLACKSTONE_STAIRS).addCriterion("has_blackstone", hasItem(Blocks.BLACKSTONE)).build(consumer, "blackstone_stairs_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.BLACKSTONE), Blocks.BLACKSTONE_WALL).addCriterion("has_blackstone", hasItem(Blocks.BLACKSTONE)).build(consumer, "blackstone_wall_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE).addCriterion("has_blackstone", hasItem(Blocks.BLACKSTONE)).build(consumer, "polished_blackstone_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_WALL).addCriterion("has_blackstone", hasItem(Blocks.BLACKSTONE)).build(consumer, "polished_blackstone_wall_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_SLAB, 2).addCriterion("has_blackstone", hasItem(Blocks.BLACKSTONE)).build(consumer, "polished_blackstone_slab_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_STAIRS).addCriterion("has_blackstone", hasItem(Blocks.BLACKSTONE)).build(consumer, "polished_blackstone_stairs_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.BLACKSTONE), Blocks.CHISELED_POLISHED_BLACKSTONE).addCriterion("has_blackstone", hasItem(Blocks.BLACKSTONE)).build(consumer, "chiseled_polished_blackstone_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICKS).addCriterion("has_blackstone", hasItem(Blocks.BLACKSTONE)).build(consumer, "polished_blackstone_bricks_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 2).addCriterion("has_blackstone", hasItem(Blocks.BLACKSTONE)).build(consumer, "polished_blackstone_brick_slab_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS).addCriterion("has_blackstone", hasItem(Blocks.BLACKSTONE)).build(consumer, "polished_blackstone_brick_stairs_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_WALL).addCriterion("has_blackstone", hasItem(Blocks.BLACKSTONE)).build(consumer, "polished_blackstone_brick_wall_from_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_SLAB, 2).addCriterion("has_polished_blackstone", hasItem(Blocks.POLISHED_BLACKSTONE)).build(consumer, "polished_blackstone_slab_from_polished_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_STAIRS).addCriterion("has_polished_blackstone", hasItem(Blocks.POLISHED_BLACKSTONE)).build(consumer, "polished_blackstone_stairs_from_polished_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICKS).addCriterion("has_polished_blackstone", hasItem(Blocks.POLISHED_BLACKSTONE)).build(consumer, "polished_blackstone_bricks_from_polished_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_WALL).addCriterion("has_polished_blackstone", hasItem(Blocks.POLISHED_BLACKSTONE)).build(consumer, "polished_blackstone_wall_from_polished_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 2).addCriterion("has_polished_blackstone", hasItem(Blocks.POLISHED_BLACKSTONE)).build(consumer, "polished_blackstone_brick_slab_from_polished_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS).addCriterion("has_polished_blackstone", hasItem(Blocks.POLISHED_BLACKSTONE)).build(consumer, "polished_blackstone_brick_stairs_from_polished_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_WALL).addCriterion("has_polished_blackstone", hasItem(Blocks.POLISHED_BLACKSTONE)).build(consumer, "polished_blackstone_brick_wall_from_polished_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_BLACKSTONE), Blocks.CHISELED_POLISHED_BLACKSTONE).addCriterion("has_polished_blackstone", hasItem(Blocks.POLISHED_BLACKSTONE)).build(consumer, "chiseled_polished_blackstone_from_polished_blackstone_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 2).addCriterion("has_polished_blackstone_bricks", hasItem(Blocks.POLISHED_BLACKSTONE_BRICKS)).build(consumer, "polished_blackstone_brick_slab_from_polished_blackstone_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS).addCriterion("has_polished_blackstone_bricks", hasItem(Blocks.POLISHED_BLACKSTONE_BRICKS)).build(consumer, "polished_blackstone_brick_stairs_from_polished_blackstone_bricks_stonecutting");
        SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.POLISHED_BLACKSTONE_BRICK_WALL).addCriterion("has_polished_blackstone_bricks", hasItem(Blocks.POLISHED_BLACKSTONE_BRICKS)).build(consumer, "polished_blackstone_brick_wall_from_polished_blackstone_bricks_stonecutting");
        smithingReinforce(consumer, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);
        smithingReinforce(consumer, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);
        smithingReinforce(consumer, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET);
        smithingReinforce(consumer, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS);
        smithingReinforce(consumer, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
        smithingReinforce(consumer, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
        smithingReinforce(consumer, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
        smithingReinforce(consumer, Items.DIAMOND_HOE, Items.NETHERITE_HOE);
        smithingReinforce(consumer, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);
    }

    private static void smithingReinforce(Consumer<IFinishedRecipe> recipeConsumer, Item itemToReinforce, Item output)
    {
        SmithingRecipeBuilder.smithingRecipe(Ingredient.fromItems(itemToReinforce), Ingredient.fromItems(Items.NETHERITE_INGOT), output).addCriterion("has_netherite_ingot", hasItem(Items.NETHERITE_INGOT)).build(recipeConsumer, Registry.ITEM.getKey(output.asItem()).getPath() + "_smithing");
    }

    private static void shapelessPlanksNew(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider planks, ITag<Item> input)
    {
        ShapelessRecipeBuilder.shapelessRecipe(planks, 4).addIngredient(input).setGroup("planks").addCriterion("has_log", hasItem(input)).build(recipeConsumer);
    }

    private static void shapelessPlanks(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider planks, ITag<Item> input)
    {
        ShapelessRecipeBuilder.shapelessRecipe(planks, 4).addIngredient(input).setGroup("planks").addCriterion("has_logs", hasItem(input)).build(recipeConsumer);
    }

    private static void shapelessStrippedToPlanks(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider stripped, IItemProvider input)
    {
        ShapedRecipeBuilder.shapedRecipe(stripped, 3).key('#', input).patternLine("##").patternLine("##").setGroup("bark").addCriterion("has_log", hasItem(input)).build(recipeConsumer);
    }

    private static void shapedBoat(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider boat, IItemProvider input)
    {
        ShapedRecipeBuilder.shapedRecipe(boat).key('#', input).patternLine("# #").patternLine("###").setGroup("boat").addCriterion("in_water", enteredBlock(Blocks.WATER)).build(recipeConsumer);
    }

    private static void shapelessWoodenButton(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider button, IItemProvider input)
    {
        ShapelessRecipeBuilder.shapelessRecipe(button).addIngredient(input).setGroup("wooden_button").addCriterion("has_planks", hasItem(input)).build(recipeConsumer);
    }

    private static void shapedWoodenDoor(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider door, IItemProvider input)
    {
        ShapedRecipeBuilder.shapedRecipe(door, 3).key('#', input).patternLine("##").patternLine("##").patternLine("##").setGroup("wooden_door").addCriterion("has_planks", hasItem(input)).build(recipeConsumer);
    }

    private static void shapedWoodenFence(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider fence, IItemProvider input)
    {
        ShapedRecipeBuilder.shapedRecipe(fence, 3).key('#', Items.STICK).key('W', input).patternLine("W#W").patternLine("W#W").setGroup("wooden_fence").addCriterion("has_planks", hasItem(input)).build(recipeConsumer);
    }

    private static void shapedWoodenFenceGate(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider fenceGate, IItemProvider input)
    {
        ShapedRecipeBuilder.shapedRecipe(fenceGate).key('#', Items.STICK).key('W', input).patternLine("#W#").patternLine("#W#").setGroup("wooden_fence_gate").addCriterion("has_planks", hasItem(input)).build(recipeConsumer);
    }

    private static void shapedWoodenPressurePlate(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider pressurePlate, IItemProvider input)
    {
        ShapedRecipeBuilder.shapedRecipe(pressurePlate).key('#', input).patternLine("##").setGroup("wooden_pressure_plate").addCriterion("has_planks", hasItem(input)).build(recipeConsumer);
    }

    private static void shapedWoodenSlab(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider slab, IItemProvider input)
    {
        ShapedRecipeBuilder.shapedRecipe(slab, 6).key('#', input).patternLine("###").setGroup("wooden_slab").addCriterion("has_planks", hasItem(input)).build(recipeConsumer);
    }

    private static void shapedWoodenStairs(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider stairs, IItemProvider input)
    {
        ShapedRecipeBuilder.shapedRecipe(stairs, 4).key('#', input).patternLine("#  ").patternLine("## ").patternLine("###").setGroup("wooden_stairs").addCriterion("has_planks", hasItem(input)).build(recipeConsumer);
    }

    private static void shapedWoodenTrapdoor(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider trapdoor, IItemProvider input)
    {
        ShapedRecipeBuilder.shapedRecipe(trapdoor, 2).key('#', input).patternLine("###").patternLine("###").setGroup("wooden_trapdoor").addCriterion("has_planks", hasItem(input)).build(recipeConsumer);
    }

    private static void shapedSign(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider sign, IItemProvider input)
    {
        String s = Registry.ITEM.getKey(input.asItem()).getPath();
        ShapedRecipeBuilder.shapedRecipe(sign, 3).setGroup("sign").key('#', input).key('X', Items.STICK).patternLine("###").patternLine("###").patternLine(" X ").addCriterion("has_" + s, hasItem(input)).build(recipeConsumer);
    }

    private static void shapelessColoredWool(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider coloredWool, IItemProvider dye)
    {
        ShapelessRecipeBuilder.shapelessRecipe(coloredWool).addIngredient(dye).addIngredient(Blocks.WHITE_WOOL).setGroup("wool").addCriterion("has_white_wool", hasItem(Blocks.WHITE_WOOL)).build(recipeConsumer);
    }

    private static void shapedCarpet(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider carpet, IItemProvider input)
    {
        String s = Registry.ITEM.getKey(input.asItem()).getPath();
        ShapedRecipeBuilder.shapedRecipe(carpet, 3).key('#', input).patternLine("##").setGroup("carpet").addCriterion("has_" + s, hasItem(input)).build(recipeConsumer);
    }

    private static void shapelessColoredCarpet(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider coloredCarpet, IItemProvider dye)
    {
        String s = Registry.ITEM.getKey(coloredCarpet.asItem()).getPath();
        String s1 = Registry.ITEM.getKey(dye.asItem()).getPath();
        ShapedRecipeBuilder.shapedRecipe(coloredCarpet, 8).key('#', Blocks.WHITE_CARPET).key('$', dye).patternLine("###").patternLine("#$#").patternLine("###").setGroup("carpet").addCriterion("has_white_carpet", hasItem(Blocks.WHITE_CARPET)).addCriterion("has_" + s1, hasItem(dye)).build(recipeConsumer, s + "_from_white_carpet");
    }

    private static void shapedBed(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider bed, IItemProvider wool)
    {
        String s = Registry.ITEM.getKey(wool.asItem()).getPath();
        ShapedRecipeBuilder.shapedRecipe(bed).key('#', wool).key('X', ItemTags.PLANKS).patternLine("###").patternLine("XXX").setGroup("bed").addCriterion("has_" + s, hasItem(wool)).build(recipeConsumer);
    }

    private static void shapedColoredBed(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider coloredBed, IItemProvider dye)
    {
        String s = Registry.ITEM.getKey(coloredBed.asItem()).getPath();
        ShapelessRecipeBuilder.shapelessRecipe(coloredBed).addIngredient(Items.WHITE_BED).addIngredient(dye).setGroup("dyed_bed").addCriterion("has_bed", hasItem(Items.WHITE_BED)).build(recipeConsumer, s + "_from_white_bed");
    }

    private static void shapedBanner(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider banner, IItemProvider input)
    {
        String s = Registry.ITEM.getKey(input.asItem()).getPath();
        ShapedRecipeBuilder.shapedRecipe(banner).key('#', input).key('|', Items.STICK).patternLine("###").patternLine("###").patternLine(" | ").setGroup("banner").addCriterion("has_" + s, hasItem(input)).build(recipeConsumer);
    }

    private static void shapedColoredGlass(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider coloredGlass, IItemProvider dye)
    {
        ShapedRecipeBuilder.shapedRecipe(coloredGlass, 8).key('#', Blocks.GLASS).key('X', dye).patternLine("###").patternLine("#X#").patternLine("###").setGroup("stained_glass").addCriterion("has_glass", hasItem(Blocks.GLASS)).build(recipeConsumer);
    }

    private static void shapedGlassPane(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider pane, IItemProvider glass)
    {
        ShapedRecipeBuilder.shapedRecipe(pane, 16).key('#', glass).patternLine("###").patternLine("###").setGroup("stained_glass_pane").addCriterion("has_glass", hasItem(glass)).build(recipeConsumer);
    }

    private static void shapedColoredPane(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider coloredPane, IItemProvider dye)
    {
        String s = Registry.ITEM.getKey(coloredPane.asItem()).getPath();
        String s1 = Registry.ITEM.getKey(dye.asItem()).getPath();
        ShapedRecipeBuilder.shapedRecipe(coloredPane, 8).key('#', Blocks.GLASS_PANE).key('$', dye).patternLine("###").patternLine("#$#").patternLine("###").setGroup("stained_glass_pane").addCriterion("has_glass_pane", hasItem(Blocks.GLASS_PANE)).addCriterion("has_" + s1, hasItem(dye)).build(recipeConsumer, s + "_from_glass_pane");
    }

    private static void shapedColoredTerracotta(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider coloredTerracotta, IItemProvider dye)
    {
        ShapedRecipeBuilder.shapedRecipe(coloredTerracotta, 8).key('#', Blocks.TERRACOTTA).key('X', dye).patternLine("###").patternLine("#X#").patternLine("###").setGroup("stained_terracotta").addCriterion("has_terracotta", hasItem(Blocks.TERRACOTTA)).build(recipeConsumer);
    }

    private static void shapedColorConcretePowder(Consumer<IFinishedRecipe> recipeConsumer, IItemProvider coloredConcretePowder, IItemProvider dye)
    {
        ShapelessRecipeBuilder.shapelessRecipe(coloredConcretePowder, 8).addIngredient(dye).addIngredient(Blocks.SAND, 4).addIngredient(Blocks.GRAVEL, 4).setGroup("concrete_powder").addCriterion("has_sand", hasItem(Blocks.SAND)).addCriterion("has_gravel", hasItem(Blocks.GRAVEL)).build(recipeConsumer);
    }

    private static void cookingRecipesForMethod(Consumer<IFinishedRecipe> recipeConsumer, String recipeConsumerIn, CookingRecipeSerializer<?> cookingMethod, int serializerIn)
    {
        CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(Items.BEEF), Items.COOKED_BEEF, 0.35F, serializerIn, cookingMethod).addCriterion("has_beef", hasItem(Items.BEEF)).build(recipeConsumer, "cooked_beef_from_" + recipeConsumerIn);
        CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(Items.CHICKEN), Items.COOKED_CHICKEN, 0.35F, serializerIn, cookingMethod).addCriterion("has_chicken", hasItem(Items.CHICKEN)).build(recipeConsumer, "cooked_chicken_from_" + recipeConsumerIn);
        CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(Items.COD), Items.COOKED_COD, 0.35F, serializerIn, cookingMethod).addCriterion("has_cod", hasItem(Items.COD)).build(recipeConsumer, "cooked_cod_from_" + recipeConsumerIn);
        CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(Blocks.KELP), Items.DRIED_KELP, 0.1F, serializerIn, cookingMethod).addCriterion("has_kelp", hasItem(Blocks.KELP)).build(recipeConsumer, "dried_kelp_from_" + recipeConsumerIn);
        CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(Items.SALMON), Items.COOKED_SALMON, 0.35F, serializerIn, cookingMethod).addCriterion("has_salmon", hasItem(Items.SALMON)).build(recipeConsumer, "cooked_salmon_from_" + recipeConsumerIn);
        CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(Items.MUTTON), Items.COOKED_MUTTON, 0.35F, serializerIn, cookingMethod).addCriterion("has_mutton", hasItem(Items.MUTTON)).build(recipeConsumer, "cooked_mutton_from_" + recipeConsumerIn);
        CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(Items.PORKCHOP), Items.COOKED_PORKCHOP, 0.35F, serializerIn, cookingMethod).addCriterion("has_porkchop", hasItem(Items.PORKCHOP)).build(recipeConsumer, "cooked_porkchop_from_" + recipeConsumerIn);
        CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(Items.POTATO), Items.BAKED_POTATO, 0.35F, serializerIn, cookingMethod).addCriterion("has_potato", hasItem(Items.POTATO)).build(recipeConsumer, "baked_potato_from_" + recipeConsumerIn);
        CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(Items.RABBIT), Items.COOKED_RABBIT, 0.35F, serializerIn, cookingMethod).addCriterion("has_rabbit", hasItem(Items.RABBIT)).build(recipeConsumer, "cooked_rabbit_from_" + recipeConsumerIn);
    }

    /**
     * Creates a new {@link EnterBlockTrigger} for use with recipe unlock criteria.
     */
    private static EnterBlockTrigger.Instance enteredBlock(Block block)
    {
        return new EnterBlockTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, block, StatePropertiesPredicate.EMPTY);
    }

    /**
     * Creates a new {@link InventoryChangeTrigger} that checks for a player having a certain item.
     */
    private static InventoryChangeTrigger.Instance hasItem(IItemProvider item)
    {
        return hasItem(ItemPredicate.Builder.create().item(item).build());
    }

    /**
     * Creates a new {@link InventoryChangeTrigger} that checks for a player having an item within the given tag.
     */
    private static InventoryChangeTrigger.Instance hasItem(ITag<Item> tag)
    {
        return hasItem(ItemPredicate.Builder.create().tag(tag).build());
    }

    /**
     * Creates a new {@link InventoryChangeTrigger} that checks for a player having a certain item.
     */
    private static InventoryChangeTrigger.Instance hasItem(ItemPredicate... predicate)
    {
        return new InventoryChangeTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, predicate);
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    public String getName()
    {
        return "Recipes";
    }
}
