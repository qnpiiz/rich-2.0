package net.minecraft.tags;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public final class BlockTags
{
    protected static final TagRegistry<Block> collection = TagRegistryManager.create(new ResourceLocation("block"), ITagCollectionSupplier::getBlockTags);
    public static final ITag.INamedTag<Block> WOOL = makeWrapperTag("wool");
    public static final ITag.INamedTag<Block> PLANKS = makeWrapperTag("planks");
    public static final ITag.INamedTag<Block> STONE_BRICKS = makeWrapperTag("stone_bricks");
    public static final ITag.INamedTag<Block> WOODEN_BUTTONS = makeWrapperTag("wooden_buttons");
    public static final ITag.INamedTag<Block> BUTTONS = makeWrapperTag("buttons");
    public static final ITag.INamedTag<Block> CARPETS = makeWrapperTag("carpets");
    public static final ITag.INamedTag<Block> WOODEN_DOORS = makeWrapperTag("wooden_doors");
    public static final ITag.INamedTag<Block> WOODEN_STAIRS = makeWrapperTag("wooden_stairs");
    public static final ITag.INamedTag<Block> WOODEN_SLABS = makeWrapperTag("wooden_slabs");
    public static final ITag.INamedTag<Block> WOODEN_FENCES = makeWrapperTag("wooden_fences");
    public static final ITag.INamedTag<Block> PRESSURE_PLATES = makeWrapperTag("pressure_plates");
    public static final ITag.INamedTag<Block> WOODEN_PRESSURE_PLATES = makeWrapperTag("wooden_pressure_plates");
    public static final ITag.INamedTag<Block> STONE_PRESSURE_PLATES = makeWrapperTag("stone_pressure_plates");
    public static final ITag.INamedTag<Block> WOODEN_TRAPDOORS = makeWrapperTag("wooden_trapdoors");
    public static final ITag.INamedTag<Block> DOORS = makeWrapperTag("doors");
    public static final ITag.INamedTag<Block> SAPLINGS = makeWrapperTag("saplings");
    public static final ITag.INamedTag<Block> LOGS_THAT_BURN = makeWrapperTag("logs_that_burn");
    public static final ITag.INamedTag<Block> LOGS = makeWrapperTag("logs");
    public static final ITag.INamedTag<Block> DARK_OAK_LOGS = makeWrapperTag("dark_oak_logs");
    public static final ITag.INamedTag<Block> OAK_LOGS = makeWrapperTag("oak_logs");
    public static final ITag.INamedTag<Block> BIRCH_LOGS = makeWrapperTag("birch_logs");
    public static final ITag.INamedTag<Block> ACACIA_LOGS = makeWrapperTag("acacia_logs");
    public static final ITag.INamedTag<Block> JUNGLE_LOGS = makeWrapperTag("jungle_logs");
    public static final ITag.INamedTag<Block> SPRUCE_LOGS = makeWrapperTag("spruce_logs");
    public static final ITag.INamedTag<Block> CRIMSON_STEMS = makeWrapperTag("crimson_stems");
    public static final ITag.INamedTag<Block> WARPED_STEMS = makeWrapperTag("warped_stems");
    public static final ITag.INamedTag<Block> BANNERS = makeWrapperTag("banners");
    public static final ITag.INamedTag<Block> SAND = makeWrapperTag("sand");
    public static final ITag.INamedTag<Block> STAIRS = makeWrapperTag("stairs");
    public static final ITag.INamedTag<Block> SLABS = makeWrapperTag("slabs");
    public static final ITag.INamedTag<Block> WALLS = makeWrapperTag("walls");
    public static final ITag.INamedTag<Block> ANVIL = makeWrapperTag("anvil");
    public static final ITag.INamedTag<Block> RAILS = makeWrapperTag("rails");
    public static final ITag.INamedTag<Block> LEAVES = makeWrapperTag("leaves");
    public static final ITag.INamedTag<Block> TRAPDOORS = makeWrapperTag("trapdoors");
    public static final ITag.INamedTag<Block> SMALL_FLOWERS = makeWrapperTag("small_flowers");
    public static final ITag.INamedTag<Block> BEDS = makeWrapperTag("beds");
    public static final ITag.INamedTag<Block> FENCES = makeWrapperTag("fences");
    public static final ITag.INamedTag<Block> TALL_FLOWERS = makeWrapperTag("tall_flowers");
    public static final ITag.INamedTag<Block> FLOWERS = makeWrapperTag("flowers");
    public static final ITag.INamedTag<Block> PIGLIN_REPELLENTS = makeWrapperTag("piglin_repellents");
    public static final ITag.INamedTag<Block> GOLD_ORES = makeWrapperTag("gold_ores");
    public static final ITag.INamedTag<Block> NON_FLAMMABLE_WOOD = makeWrapperTag("non_flammable_wood");
    public static final ITag.INamedTag<Block> FLOWER_POTS = makeWrapperTag("flower_pots");
    public static final ITag.INamedTag<Block> ENDERMAN_HOLDABLE = makeWrapperTag("enderman_holdable");
    public static final ITag.INamedTag<Block> ICE = makeWrapperTag("ice");
    public static final ITag.INamedTag<Block> VALID_SPAWN = makeWrapperTag("valid_spawn");
    public static final ITag.INamedTag<Block> IMPERMEABLE = makeWrapperTag("impermeable");
    public static final ITag.INamedTag<Block> UNDERWATER_BONEMEALS = makeWrapperTag("underwater_bonemeals");
    public static final ITag.INamedTag<Block> CORAL_BLOCKS = makeWrapperTag("coral_blocks");
    public static final ITag.INamedTag<Block> WALL_CORALS = makeWrapperTag("wall_corals");
    public static final ITag.INamedTag<Block> CORAL_PLANTS = makeWrapperTag("coral_plants");
    public static final ITag.INamedTag<Block> CORALS = makeWrapperTag("corals");
    public static final ITag.INamedTag<Block> BAMBOO_PLANTABLE_ON = makeWrapperTag("bamboo_plantable_on");
    public static final ITag.INamedTag<Block> STANDING_SIGNS = makeWrapperTag("standing_signs");
    public static final ITag.INamedTag<Block> WALL_SIGNS = makeWrapperTag("wall_signs");
    public static final ITag.INamedTag<Block> SIGNS = makeWrapperTag("signs");
    public static final ITag.INamedTag<Block> DRAGON_IMMUNE = makeWrapperTag("dragon_immune");
    public static final ITag.INamedTag<Block> WITHER_IMMUNE = makeWrapperTag("wither_immune");
    public static final ITag.INamedTag<Block> WITHER_SUMMON_BASE_BLOCKS = makeWrapperTag("wither_summon_base_blocks");
    public static final ITag.INamedTag<Block> BEEHIVES = makeWrapperTag("beehives");
    public static final ITag.INamedTag<Block> CROPS = makeWrapperTag("crops");
    public static final ITag.INamedTag<Block> BEE_GROWABLES = makeWrapperTag("bee_growables");
    public static final ITag.INamedTag<Block> PORTALS = makeWrapperTag("portals");
    public static final ITag.INamedTag<Block> FIRE = makeWrapperTag("fire");
    public static final ITag.INamedTag<Block> NYLIUM = makeWrapperTag("nylium");
    public static final ITag.INamedTag<Block> WART_BLOCKS = makeWrapperTag("wart_blocks");
    public static final ITag.INamedTag<Block> BEACON_BASE_BLOCKS = makeWrapperTag("beacon_base_blocks");
    public static final ITag.INamedTag<Block> SOUL_SPEED_BLOCKS = makeWrapperTag("soul_speed_blocks");
    public static final ITag.INamedTag<Block> WALL_POST_OVERRIDE = makeWrapperTag("wall_post_override");
    public static final ITag.INamedTag<Block> CLIMBABLE = makeWrapperTag("climbable");
    public static final ITag.INamedTag<Block> SHULKER_BOXES = makeWrapperTag("shulker_boxes");
    public static final ITag.INamedTag<Block> HOGLIN_REPELLENTS = makeWrapperTag("hoglin_repellents");
    public static final ITag.INamedTag<Block> SOUL_FIRE_BASE_BLOCKS = makeWrapperTag("soul_fire_base_blocks");
    public static final ITag.INamedTag<Block> STRIDER_WARM_BLOCKS = makeWrapperTag("strider_warm_blocks");
    public static final ITag.INamedTag<Block> CAMPFIRES = makeWrapperTag("campfires");
    public static final ITag.INamedTag<Block> GUARDED_BY_PIGLINS = makeWrapperTag("guarded_by_piglins");
    public static final ITag.INamedTag<Block> PREVENT_MOB_SPAWNING_INSIDE = makeWrapperTag("prevent_mob_spawning_inside");
    public static final ITag.INamedTag<Block> FENCE_GATES = makeWrapperTag("fence_gates");
    public static final ITag.INamedTag<Block> UNSTABLE_BOTTOM_CENTER = makeWrapperTag("unstable_bottom_center");
    public static final ITag.INamedTag<Block> MUSHROOM_GROW_BLOCK = makeWrapperTag("mushroom_grow_block");
    public static final ITag.INamedTag<Block> INFINIBURN_OVERWORLD = makeWrapperTag("infiniburn_overworld");
    public static final ITag.INamedTag<Block> INFINIBURN_NETHER = makeWrapperTag("infiniburn_nether");
    public static final ITag.INamedTag<Block> INFINIBURN_END = makeWrapperTag("infiniburn_end");
    public static final ITag.INamedTag<Block> BASE_STONE_OVERWORLD = makeWrapperTag("base_stone_overworld");
    public static final ITag.INamedTag<Block> BASE_STONE_NETHER = makeWrapperTag("base_stone_nether");

    private static ITag.INamedTag<Block> makeWrapperTag(String id)
    {
        return collection.createTag(id);
    }

    public static ITagCollection<Block> getCollection()
    {
        return collection.getCollection();
    }

    public static List <? extends ITag.INamedTag<Block >> getAllTags()
    {
        return collection.getTags();
    }
}
