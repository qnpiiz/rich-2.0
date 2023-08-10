package net.minecraft.tags;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public final class ItemTags
{
    protected static final TagRegistry<Item> collection = TagRegistryManager.create(new ResourceLocation("item"), ITagCollectionSupplier::getItemTags);
    public static final ITag.INamedTag<Item> WOOL = makeWrapperTag("wool");
    public static final ITag.INamedTag<Item> PLANKS = makeWrapperTag("planks");
    public static final ITag.INamedTag<Item> STONE_BRICKS = makeWrapperTag("stone_bricks");
    public static final ITag.INamedTag<Item> WOODEN_BUTTONS = makeWrapperTag("wooden_buttons");
    public static final ITag.INamedTag<Item> BUTTONS = makeWrapperTag("buttons");
    public static final ITag.INamedTag<Item> CARPETS = makeWrapperTag("carpets");
    public static final ITag.INamedTag<Item> WOODEN_DOORS = makeWrapperTag("wooden_doors");
    public static final ITag.INamedTag<Item> WOODEN_STAIRS = makeWrapperTag("wooden_stairs");
    public static final ITag.INamedTag<Item> WOODEN_SLABS = makeWrapperTag("wooden_slabs");
    public static final ITag.INamedTag<Item> WOODEN_FENCES = makeWrapperTag("wooden_fences");
    public static final ITag.INamedTag<Item> WOODEN_PRESSURE_PLATES = makeWrapperTag("wooden_pressure_plates");
    public static final ITag.INamedTag<Item> WOODEN_TRAPDOORS = makeWrapperTag("wooden_trapdoors");
    public static final ITag.INamedTag<Item> DOORS = makeWrapperTag("doors");
    public static final ITag.INamedTag<Item> SAPLINGS = makeWrapperTag("saplings");
    public static final ITag.INamedTag<Item> LOGS_THAT_BURN = makeWrapperTag("logs_that_burn");
    public static final ITag.INamedTag<Item> LOGS = makeWrapperTag("logs");
    public static final ITag.INamedTag<Item> DARK_OAK_LOGS = makeWrapperTag("dark_oak_logs");
    public static final ITag.INamedTag<Item> OAK_LOGS = makeWrapperTag("oak_logs");
    public static final ITag.INamedTag<Item> BIRCH_LOGS = makeWrapperTag("birch_logs");
    public static final ITag.INamedTag<Item> ACACIA_LOGS = makeWrapperTag("acacia_logs");
    public static final ITag.INamedTag<Item> JUNGLE_LOGS = makeWrapperTag("jungle_logs");
    public static final ITag.INamedTag<Item> SPRUCE_LOGS = makeWrapperTag("spruce_logs");
    public static final ITag.INamedTag<Item> CRIMSON_STEMS = makeWrapperTag("crimson_stems");
    public static final ITag.INamedTag<Item> WARPED_STEMS = makeWrapperTag("warped_stems");
    public static final ITag.INamedTag<Item> BANNERS = makeWrapperTag("banners");
    public static final ITag.INamedTag<Item> SAND = makeWrapperTag("sand");
    public static final ITag.INamedTag<Item> STAIRS = makeWrapperTag("stairs");
    public static final ITag.INamedTag<Item> SLABS = makeWrapperTag("slabs");
    public static final ITag.INamedTag<Item> WALLS = makeWrapperTag("walls");
    public static final ITag.INamedTag<Item> ANVIL = makeWrapperTag("anvil");
    public static final ITag.INamedTag<Item> RAILS = makeWrapperTag("rails");
    public static final ITag.INamedTag<Item> LEAVES = makeWrapperTag("leaves");
    public static final ITag.INamedTag<Item> TRAPDOORS = makeWrapperTag("trapdoors");
    public static final ITag.INamedTag<Item> SMALL_FLOWERS = makeWrapperTag("small_flowers");
    public static final ITag.INamedTag<Item> BEDS = makeWrapperTag("beds");
    public static final ITag.INamedTag<Item> FENCES = makeWrapperTag("fences");
    public static final ITag.INamedTag<Item> TALL_FLOWERS = makeWrapperTag("tall_flowers");
    public static final ITag.INamedTag<Item> FLOWERS = makeWrapperTag("flowers");
    public static final ITag.INamedTag<Item> PIGLIN_REPELLENTS = makeWrapperTag("piglin_repellents");
    public static final ITag.INamedTag<Item> PIGLIN_LOVED = makeWrapperTag("piglin_loved");
    public static final ITag.INamedTag<Item> GOLD_ORES = makeWrapperTag("gold_ores");
    public static final ITag.INamedTag<Item> NON_FLAMMABLE_WOOD = makeWrapperTag("non_flammable_wood");
    public static final ITag.INamedTag<Item> SOUL_FIRE_BASE_BLOCKS = makeWrapperTag("soul_fire_base_blocks");
    public static final ITag.INamedTag<Item> BOATS = makeWrapperTag("boats");
    public static final ITag.INamedTag<Item> FISHES = makeWrapperTag("fishes");
    public static final ITag.INamedTag<Item> SIGNS = makeWrapperTag("signs");
    public static final ITag.INamedTag<Item> MUSIC_DISCS = makeWrapperTag("music_discs");
    public static final ITag.INamedTag<Item> CREEPER_DROP_MUSIC_DISCS = makeWrapperTag("creeper_drop_music_discs");
    public static final ITag.INamedTag<Item> COALS = makeWrapperTag("coals");
    public static final ITag.INamedTag<Item> ARROWS = makeWrapperTag("arrows");
    public static final ITag.INamedTag<Item> LECTERN_BOOKS = makeWrapperTag("lectern_books");
    public static final ITag.INamedTag<Item> BEACON_PAYMENT_ITEMS = makeWrapperTag("beacon_payment_items");
    public static final ITag.INamedTag<Item> STONE_TOOL_MATERIALS = makeWrapperTag("stone_tool_materials");
    public static final ITag.INamedTag<Item> STONE_CRAFTING_MATERIALS = makeWrapperTag("stone_crafting_materials");

    private static ITag.INamedTag<Item> makeWrapperTag(String id)
    {
        return collection.createTag(id);
    }

    public static ITagCollection<Item> getCollection()
    {
        return collection.getCollection();
    }

    public static List <? extends ITag.INamedTag<Item >> getAllTags()
    {
        return collection.getTags();
    }
}
