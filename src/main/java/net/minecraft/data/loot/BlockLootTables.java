package net.minecraft.data.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BeetrootBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotBlock;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.PotatoBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TNTBlock;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.AlternativesLootEntry;
import net.minecraft.loot.BinomialRange;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.DynamicLootEntry;
import net.minecraft.loot.ILootConditionConsumer;
import net.minecraft.loot.ILootFunctionConsumer;
import net.minecraft.loot.IRandomRange;
import net.minecraft.loot.IntClamper;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.conditions.EntityHasProperty;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LocationCheck;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.loot.conditions.RandomChance;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.loot.conditions.TableBonus;
import net.minecraft.loot.functions.ApplyBonus;
import net.minecraft.loot.functions.CopyBlockState;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.loot.functions.ExplosionDecay;
import net.minecraft.loot.functions.LimitCount;
import net.minecraft.loot.functions.SetContents;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>
{
    private static final ILootCondition.IBuilder SILK_TOUCH = MatchTool.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))));
    private static final ILootCondition.IBuilder NO_SILK_TOUCH = SILK_TOUCH.inverted();
    private static final ILootCondition.IBuilder SHEARS = MatchTool.builder(ItemPredicate.Builder.create().item(Items.SHEARS));
    private static final ILootCondition.IBuilder SILK_TOUCH_OR_SHEARS = SHEARS.alternative(SILK_TOUCH);
    private static final ILootCondition.IBuilder NOT_SILK_TOUCH_OR_SHEARS = SILK_TOUCH_OR_SHEARS.inverted();
    private static final Set<Item> IMMUNE_TO_EXPLOSIONS = Stream.of(Blocks.DRAGON_EGG, Blocks.BEACON, Blocks.CONDUIT, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.CREEPER_HEAD, Blocks.DRAGON_HEAD, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX).map(IItemProvider::asItem).collect(ImmutableSet.toImmutableSet());
    private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[] {0.05F, 0.0625F, 0.083333336F, 0.1F};
    private static final float[] RARE_SAPLING_DROP_RATES = new float[] {0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};
    private final Map<ResourceLocation, LootTable.Builder> lootTables = Maps.newHashMap();

    private static <T> T withExplosionDecay(IItemProvider item, ILootFunctionConsumer<T> function)
    {
        return (T)(!IMMUNE_TO_EXPLOSIONS.contains(item.asItem()) ? function.acceptFunction(ExplosionDecay.builder()) : function.cast());
    }

    private static <T> T withSurvivesExplosion(IItemProvider item, ILootConditionConsumer<T> condition)
    {
        return (T)(!IMMUNE_TO_EXPLOSIONS.contains(item.asItem()) ? condition.acceptCondition(SurvivesExplosion.builder()) : condition.cast());
    }

    private static LootTable.Builder dropping(IItemProvider item)
    {
        return LootTable.builder().addLootPool(withSurvivesExplosion(item, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(item))));
    }

    private static LootTable.Builder dropping(Block block, ILootCondition.IBuilder conditionBuilder, LootEntry.Builder<?> p_218494_2_)
    {
        return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(block).acceptCondition(conditionBuilder).alternatively(p_218494_2_)));
    }

    private static LootTable.Builder droppingWithSilkTouch(Block block, LootEntry.Builder<?> builder)
    {
        return dropping(block, SILK_TOUCH, builder);
    }

    private static LootTable.Builder droppingWithShears(Block block, LootEntry.Builder<?> noShearAlternativeEntry)
    {
        return dropping(block, SHEARS, noShearAlternativeEntry);
    }

    private static LootTable.Builder droppingWithSilkTouchOrShears(Block block, LootEntry.Builder<?> alternativeLootEntry)
    {
        return dropping(block, SILK_TOUCH_OR_SHEARS, alternativeLootEntry);
    }

    private static LootTable.Builder droppingWithSilkTouch(Block block, IItemProvider noSilkTouch)
    {
        return droppingWithSilkTouch(block, withSurvivesExplosion(block, ItemLootEntry.builder(noSilkTouch)));
    }

    private static LootTable.Builder droppingRandomly(IItemProvider item, IRandomRange range)
    {
        return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(withExplosionDecay(item, ItemLootEntry.builder(item).acceptFunction(SetCount.builder(range)))));
    }

    private static LootTable.Builder droppingWithSilkTouchOrRandomly(Block block, IItemProvider item, IRandomRange range)
    {
        return droppingWithSilkTouch(block, withExplosionDecay(block, ItemLootEntry.builder(item).acceptFunction(SetCount.builder(range))));
    }

    private static LootTable.Builder onlyWithSilkTouch(IItemProvider item)
    {
        return LootTable.builder().addLootPool(LootPool.builder().acceptCondition(SILK_TOUCH).rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(item)));
    }

    private static LootTable.Builder droppingAndFlowerPot(IItemProvider flower)
    {
        return LootTable.builder().addLootPool(withSurvivesExplosion(Blocks.FLOWER_POT, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Blocks.FLOWER_POT)))).addLootPool(withSurvivesExplosion(flower, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(flower))));
    }

    private static LootTable.Builder droppingSlab(Block slab)
    {
        return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(withExplosionDecay(slab, ItemLootEntry.builder(slab).acceptFunction(SetCount.builder(ConstantRange.of(2)).acceptCondition(BlockStateProperty.builder(slab).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withProp(SlabBlock.TYPE, SlabType.DOUBLE)))))));
    }

    private static <T extends Comparable<T> & IStringSerializable> LootTable.Builder droppingWhen(Block block, Property<T> property, T value)
    {
        return LootTable.builder().addLootPool(withSurvivesExplosion(block, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(block).acceptCondition(BlockStateProperty.builder(block).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withProp(property, value))))));
    }

    private static LootTable.Builder droppingWithName(Block block)
    {
        return LootTable.builder().addLootPool(withSurvivesExplosion(block, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(block).acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY)))));
    }

    private static LootTable.Builder droppingWithContents(Block shulker)
    {
        return LootTable.builder().addLootPool(withSurvivesExplosion(shulker, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(shulker).acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY)).acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Lock", "BlockEntityTag.Lock").replaceOperation("LootTable", "BlockEntityTag.LootTable").replaceOperation("LootTableSeed", "BlockEntityTag.LootTableSeed")).acceptFunction(SetContents.builderIn().addLootEntry(DynamicLootEntry.func_216162_a(ShulkerBoxBlock.CONTENTS))))));
    }

    private static LootTable.Builder droppingWithPatterns(Block banner)
    {
        return LootTable.builder().addLootPool(withSurvivesExplosion(banner, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(banner).acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY)).acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Patterns", "BlockEntityTag.Patterns")))));
    }

    private static LootTable.Builder droppingAndBees(Block nest)
    {
        return LootTable.builder().addLootPool(LootPool.builder().acceptCondition(SILK_TOUCH).rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(nest).acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Bees", "BlockEntityTag.Bees")).acceptFunction(CopyBlockState.func_227545_a_(nest).func_227552_a_(BeehiveBlock.HONEY_LEVEL))));
    }

    private static LootTable.Builder droppingAndBeesWithAlternative(Block hive)
    {
        return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(hive).acceptCondition(SILK_TOUCH).acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Bees", "BlockEntityTag.Bees")).acceptFunction(CopyBlockState.func_227545_a_(hive).func_227552_a_(BeehiveBlock.HONEY_LEVEL)).alternatively(ItemLootEntry.builder(hive))));
    }

    private static LootTable.Builder droppingItemWithFortune(Block block, Item item)
    {
        return droppingWithSilkTouch(block, withExplosionDecay(block, ItemLootEntry.builder(item).acceptFunction(ApplyBonus.oreDrops(Enchantments.FORTUNE))));
    }

    /**
     * Creates a builder that drops the given IItemProvider in amounts between 0 and 2, most often 0. Only used in
     * vanilla for huge mushroom blocks.
     */
    private static LootTable.Builder droppingItemRarely(Block block, IItemProvider item)
    {
        return droppingWithSilkTouch(block, withExplosionDecay(block, ItemLootEntry.builder(item).acceptFunction(SetCount.builder(RandomValueRange.of(-6.0F, 2.0F))).acceptFunction(LimitCount.func_215911_a(IntClamper.func_215848_a(0)))));
    }

    private static LootTable.Builder droppingSeeds(Block block)
    {
        return droppingWithShears(block, withExplosionDecay(block, ItemLootEntry.builder(Items.WHEAT_SEEDS).acceptCondition(RandomChance.builder(0.125F)).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE, 2))));
    }

    /**
     * Creates a builder that drops the given IItemProvider in amounts between 0 and 3, based on the AGE property. Only
     * used in vanilla for pumpkin and melon stems.
     */
    private static LootTable.Builder droppingByAge(Block stemFruit, Item item)
    {
        return LootTable.builder().addLootPool(withExplosionDecay(stemFruit, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(item).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.06666667F)).acceptCondition(BlockStateProperty.builder(stemFruit).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 0)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.13333334F)).acceptCondition(BlockStateProperty.builder(stemFruit).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 1)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.2F)).acceptCondition(BlockStateProperty.builder(stemFruit).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 2)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.26666668F)).acceptCondition(BlockStateProperty.builder(stemFruit).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 3)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.33333334F)).acceptCondition(BlockStateProperty.builder(stemFruit).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 4)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.4F)).acceptCondition(BlockStateProperty.builder(stemFruit).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 5)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.46666667F)).acceptCondition(BlockStateProperty.builder(stemFruit).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 6)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.53333336F)).acceptCondition(BlockStateProperty.builder(stemFruit).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 7)))))));
    }

    private static LootTable.Builder dropSeedsForStem(Block stem, Item stemSeed)
    {
        return LootTable.builder().addLootPool(withExplosionDecay(stem, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(stemSeed).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.53333336F))))));
    }

    private static LootTable.Builder onlyWithShears(IItemProvider item)
    {
        return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(SHEARS).addEntry(ItemLootEntry.builder(item)));
    }

    /**
     * Used for all leaves, drops self with silk touch, otherwise drops the second Block param with the passed chances
     * for fortune levels, adding in sticks.
     */
    private static LootTable.Builder droppingWithChancesAndSticks(Block block, Block sapling, float... chances)
    {
        return droppingWithSilkTouchOrShears(block, withSurvivesExplosion(block, ItemLootEntry.builder(sapling)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, chances))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(NOT_SILK_TOUCH_OR_SHEARS).addEntry(withExplosionDecay(block, ItemLootEntry.builder(Items.STICK).acceptFunction(SetCount.builder(RandomValueRange.of(1.0F, 2.0F)))).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))));
    }

    /**
     * Used for oak and dark oak, same as droppingWithChancesAndSticks but adding in apples.
     */
    private static LootTable.Builder droppingWithChancesSticksAndApples(Block block, Block sapling, float... chances)
    {
        return droppingWithChancesAndSticks(block, sapling, chances).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(NOT_SILK_TOUCH_OR_SHEARS).addEntry(withSurvivesExplosion(block, ItemLootEntry.builder(Items.APPLE)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
    }

    /**
     * Drops the first item parameter always, and the second item parameter plus more of the first when the loot
     * condition is met, applying fortune to only the second argument.
     */
    private static LootTable.Builder droppingAndBonusWhen(Block block, Item itemConditional, Item withBonus, ILootCondition.IBuilder conditionBuilder)
    {
        return withExplosionDecay(block, LootTable.builder().addLootPool(LootPool.builder().addEntry(ItemLootEntry.builder(itemConditional).acceptCondition(conditionBuilder).alternatively(ItemLootEntry.builder(withBonus)))).addLootPool(LootPool.builder().acceptCondition(conditionBuilder).addEntry(ItemLootEntry.builder(withBonus).acceptFunction(ApplyBonus.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))));
    }

    private static LootTable.Builder droppingSheared(Block sheared)
    {
        return LootTable.builder().addLootPool(LootPool.builder().acceptCondition(SHEARS).addEntry(ItemLootEntry.builder(sheared).acceptFunction(SetCount.builder(ConstantRange.of(2)))));
    }

    private static LootTable.Builder droppingSeedsTall(Block block, Block sheared)
    {
        LootEntry.Builder<?> builder = ItemLootEntry.builder(sheared).acceptFunction(SetCount.builder(ConstantRange.of(2))).acceptCondition(SHEARS).alternatively(withSurvivesExplosion(block, ItemLootEntry.builder(Items.WHEAT_SEEDS)).acceptCondition(RandomChance.builder(0.125F)));
        return LootTable.builder().addLootPool(LootPool.builder().addEntry(builder).acceptCondition(BlockStateProperty.builder(block).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withProp(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER))).acceptCondition(LocationCheck.func_241547_a_(LocationPredicate.Builder.builder().block(BlockPredicate.Builder.createBuilder().setBlock(block).setStatePredicate(StatePropertiesPredicate.Builder.newBuilder().withProp(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER).build()).build()), new BlockPos(0, 1, 0)))).addLootPool(LootPool.builder().addEntry(builder).acceptCondition(BlockStateProperty.builder(block).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withProp(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER))).acceptCondition(LocationCheck.func_241547_a_(LocationPredicate.Builder.builder().block(BlockPredicate.Builder.createBuilder().setBlock(block).setStatePredicate(StatePropertiesPredicate.Builder.newBuilder().withProp(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER).build()).build()), new BlockPos(0, -1, 0))));
    }

    public static LootTable.Builder blockNoDrop()
    {
        return LootTable.builder();
    }

    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_accept_1_)
    {
        this.registerDropSelfLootTable(Blocks.GRANITE);
        this.registerDropSelfLootTable(Blocks.POLISHED_GRANITE);
        this.registerDropSelfLootTable(Blocks.DIORITE);
        this.registerDropSelfLootTable(Blocks.POLISHED_DIORITE);
        this.registerDropSelfLootTable(Blocks.ANDESITE);
        this.registerDropSelfLootTable(Blocks.POLISHED_ANDESITE);
        this.registerDropSelfLootTable(Blocks.DIRT);
        this.registerDropSelfLootTable(Blocks.COARSE_DIRT);
        this.registerDropSelfLootTable(Blocks.COBBLESTONE);
        this.registerDropSelfLootTable(Blocks.OAK_PLANKS);
        this.registerDropSelfLootTable(Blocks.SPRUCE_PLANKS);
        this.registerDropSelfLootTable(Blocks.BIRCH_PLANKS);
        this.registerDropSelfLootTable(Blocks.JUNGLE_PLANKS);
        this.registerDropSelfLootTable(Blocks.ACACIA_PLANKS);
        this.registerDropSelfLootTable(Blocks.DARK_OAK_PLANKS);
        this.registerDropSelfLootTable(Blocks.OAK_SAPLING);
        this.registerDropSelfLootTable(Blocks.SPRUCE_SAPLING);
        this.registerDropSelfLootTable(Blocks.BIRCH_SAPLING);
        this.registerDropSelfLootTable(Blocks.JUNGLE_SAPLING);
        this.registerDropSelfLootTable(Blocks.ACACIA_SAPLING);
        this.registerDropSelfLootTable(Blocks.DARK_OAK_SAPLING);
        this.registerDropSelfLootTable(Blocks.SAND);
        this.registerDropSelfLootTable(Blocks.RED_SAND);
        this.registerDropSelfLootTable(Blocks.GOLD_ORE);
        this.registerDropSelfLootTable(Blocks.IRON_ORE);
        this.registerDropSelfLootTable(Blocks.OAK_LOG);
        this.registerDropSelfLootTable(Blocks.SPRUCE_LOG);
        this.registerDropSelfLootTable(Blocks.BIRCH_LOG);
        this.registerDropSelfLootTable(Blocks.JUNGLE_LOG);
        this.registerDropSelfLootTable(Blocks.ACACIA_LOG);
        this.registerDropSelfLootTable(Blocks.DARK_OAK_LOG);
        this.registerDropSelfLootTable(Blocks.STRIPPED_SPRUCE_LOG);
        this.registerDropSelfLootTable(Blocks.STRIPPED_BIRCH_LOG);
        this.registerDropSelfLootTable(Blocks.STRIPPED_JUNGLE_LOG);
        this.registerDropSelfLootTable(Blocks.STRIPPED_ACACIA_LOG);
        this.registerDropSelfLootTable(Blocks.STRIPPED_DARK_OAK_LOG);
        this.registerDropSelfLootTable(Blocks.STRIPPED_OAK_LOG);
        this.registerDropSelfLootTable(Blocks.STRIPPED_WARPED_STEM);
        this.registerDropSelfLootTable(Blocks.STRIPPED_CRIMSON_STEM);
        this.registerDropSelfLootTable(Blocks.OAK_WOOD);
        this.registerDropSelfLootTable(Blocks.SPRUCE_WOOD);
        this.registerDropSelfLootTable(Blocks.BIRCH_WOOD);
        this.registerDropSelfLootTable(Blocks.JUNGLE_WOOD);
        this.registerDropSelfLootTable(Blocks.ACACIA_WOOD);
        this.registerDropSelfLootTable(Blocks.DARK_OAK_WOOD);
        this.registerDropSelfLootTable(Blocks.STRIPPED_OAK_WOOD);
        this.registerDropSelfLootTable(Blocks.STRIPPED_SPRUCE_WOOD);
        this.registerDropSelfLootTable(Blocks.STRIPPED_BIRCH_WOOD);
        this.registerDropSelfLootTable(Blocks.STRIPPED_JUNGLE_WOOD);
        this.registerDropSelfLootTable(Blocks.STRIPPED_ACACIA_WOOD);
        this.registerDropSelfLootTable(Blocks.STRIPPED_DARK_OAK_WOOD);
        this.registerDropSelfLootTable(Blocks.STRIPPED_CRIMSON_HYPHAE);
        this.registerDropSelfLootTable(Blocks.STRIPPED_WARPED_HYPHAE);
        this.registerDropSelfLootTable(Blocks.SPONGE);
        this.registerDropSelfLootTable(Blocks.WET_SPONGE);
        this.registerDropSelfLootTable(Blocks.LAPIS_BLOCK);
        this.registerDropSelfLootTable(Blocks.SANDSTONE);
        this.registerDropSelfLootTable(Blocks.CHISELED_SANDSTONE);
        this.registerDropSelfLootTable(Blocks.CUT_SANDSTONE);
        this.registerDropSelfLootTable(Blocks.NOTE_BLOCK);
        this.registerDropSelfLootTable(Blocks.POWERED_RAIL);
        this.registerDropSelfLootTable(Blocks.DETECTOR_RAIL);
        this.registerDropSelfLootTable(Blocks.STICKY_PISTON);
        this.registerDropSelfLootTable(Blocks.PISTON);
        this.registerDropSelfLootTable(Blocks.WHITE_WOOL);
        this.registerDropSelfLootTable(Blocks.ORANGE_WOOL);
        this.registerDropSelfLootTable(Blocks.MAGENTA_WOOL);
        this.registerDropSelfLootTable(Blocks.LIGHT_BLUE_WOOL);
        this.registerDropSelfLootTable(Blocks.YELLOW_WOOL);
        this.registerDropSelfLootTable(Blocks.LIME_WOOL);
        this.registerDropSelfLootTable(Blocks.PINK_WOOL);
        this.registerDropSelfLootTable(Blocks.GRAY_WOOL);
        this.registerDropSelfLootTable(Blocks.LIGHT_GRAY_WOOL);
        this.registerDropSelfLootTable(Blocks.CYAN_WOOL);
        this.registerDropSelfLootTable(Blocks.PURPLE_WOOL);
        this.registerDropSelfLootTable(Blocks.BLUE_WOOL);
        this.registerDropSelfLootTable(Blocks.BROWN_WOOL);
        this.registerDropSelfLootTable(Blocks.GREEN_WOOL);
        this.registerDropSelfLootTable(Blocks.RED_WOOL);
        this.registerDropSelfLootTable(Blocks.BLACK_WOOL);
        this.registerDropSelfLootTable(Blocks.DANDELION);
        this.registerDropSelfLootTable(Blocks.POPPY);
        this.registerDropSelfLootTable(Blocks.BLUE_ORCHID);
        this.registerDropSelfLootTable(Blocks.ALLIUM);
        this.registerDropSelfLootTable(Blocks.AZURE_BLUET);
        this.registerDropSelfLootTable(Blocks.RED_TULIP);
        this.registerDropSelfLootTable(Blocks.ORANGE_TULIP);
        this.registerDropSelfLootTable(Blocks.WHITE_TULIP);
        this.registerDropSelfLootTable(Blocks.PINK_TULIP);
        this.registerDropSelfLootTable(Blocks.OXEYE_DAISY);
        this.registerDropSelfLootTable(Blocks.CORNFLOWER);
        this.registerDropSelfLootTable(Blocks.WITHER_ROSE);
        this.registerDropSelfLootTable(Blocks.LILY_OF_THE_VALLEY);
        this.registerDropSelfLootTable(Blocks.BROWN_MUSHROOM);
        this.registerDropSelfLootTable(Blocks.RED_MUSHROOM);
        this.registerDropSelfLootTable(Blocks.GOLD_BLOCK);
        this.registerDropSelfLootTable(Blocks.IRON_BLOCK);
        this.registerDropSelfLootTable(Blocks.BRICKS);
        this.registerDropSelfLootTable(Blocks.MOSSY_COBBLESTONE);
        this.registerDropSelfLootTable(Blocks.OBSIDIAN);
        this.registerDropSelfLootTable(Blocks.CRYING_OBSIDIAN);
        this.registerDropSelfLootTable(Blocks.TORCH);
        this.registerDropSelfLootTable(Blocks.OAK_STAIRS);
        this.registerDropSelfLootTable(Blocks.REDSTONE_WIRE);
        this.registerDropSelfLootTable(Blocks.DIAMOND_BLOCK);
        this.registerDropSelfLootTable(Blocks.CRAFTING_TABLE);
        this.registerDropSelfLootTable(Blocks.OAK_SIGN);
        this.registerDropSelfLootTable(Blocks.SPRUCE_SIGN);
        this.registerDropSelfLootTable(Blocks.BIRCH_SIGN);
        this.registerDropSelfLootTable(Blocks.ACACIA_SIGN);
        this.registerDropSelfLootTable(Blocks.JUNGLE_SIGN);
        this.registerDropSelfLootTable(Blocks.DARK_OAK_SIGN);
        this.registerDropSelfLootTable(Blocks.LADDER);
        this.registerDropSelfLootTable(Blocks.RAIL);
        this.registerDropSelfLootTable(Blocks.COBBLESTONE_STAIRS);
        this.registerDropSelfLootTable(Blocks.LEVER);
        this.registerDropSelfLootTable(Blocks.STONE_PRESSURE_PLATE);
        this.registerDropSelfLootTable(Blocks.OAK_PRESSURE_PLATE);
        this.registerDropSelfLootTable(Blocks.SPRUCE_PRESSURE_PLATE);
        this.registerDropSelfLootTable(Blocks.BIRCH_PRESSURE_PLATE);
        this.registerDropSelfLootTable(Blocks.JUNGLE_PRESSURE_PLATE);
        this.registerDropSelfLootTable(Blocks.ACACIA_PRESSURE_PLATE);
        this.registerDropSelfLootTable(Blocks.DARK_OAK_PRESSURE_PLATE);
        this.registerDropSelfLootTable(Blocks.REDSTONE_TORCH);
        this.registerDropSelfLootTable(Blocks.STONE_BUTTON);
        this.registerDropSelfLootTable(Blocks.CACTUS);
        this.registerDropSelfLootTable(Blocks.SUGAR_CANE);
        this.registerDropSelfLootTable(Blocks.JUKEBOX);
        this.registerDropSelfLootTable(Blocks.OAK_FENCE);
        this.registerDropSelfLootTable(Blocks.PUMPKIN);
        this.registerDropSelfLootTable(Blocks.NETHERRACK);
        this.registerDropSelfLootTable(Blocks.SOUL_SAND);
        this.registerDropSelfLootTable(Blocks.SOUL_SOIL);
        this.registerDropSelfLootTable(Blocks.BASALT);
        this.registerDropSelfLootTable(Blocks.POLISHED_BASALT);
        this.registerDropSelfLootTable(Blocks.SOUL_TORCH);
        this.registerDropSelfLootTable(Blocks.CARVED_PUMPKIN);
        this.registerDropSelfLootTable(Blocks.JACK_O_LANTERN);
        this.registerDropSelfLootTable(Blocks.REPEATER);
        this.registerDropSelfLootTable(Blocks.OAK_TRAPDOOR);
        this.registerDropSelfLootTable(Blocks.SPRUCE_TRAPDOOR);
        this.registerDropSelfLootTable(Blocks.BIRCH_TRAPDOOR);
        this.registerDropSelfLootTable(Blocks.JUNGLE_TRAPDOOR);
        this.registerDropSelfLootTable(Blocks.ACACIA_TRAPDOOR);
        this.registerDropSelfLootTable(Blocks.DARK_OAK_TRAPDOOR);
        this.registerDropSelfLootTable(Blocks.STONE_BRICKS);
        this.registerDropSelfLootTable(Blocks.MOSSY_STONE_BRICKS);
        this.registerDropSelfLootTable(Blocks.CRACKED_STONE_BRICKS);
        this.registerDropSelfLootTable(Blocks.CHISELED_STONE_BRICKS);
        this.registerDropSelfLootTable(Blocks.IRON_BARS);
        this.registerDropSelfLootTable(Blocks.OAK_FENCE_GATE);
        this.registerDropSelfLootTable(Blocks.BRICK_STAIRS);
        this.registerDropSelfLootTable(Blocks.STONE_BRICK_STAIRS);
        this.registerDropSelfLootTable(Blocks.LILY_PAD);
        this.registerDropSelfLootTable(Blocks.NETHER_BRICKS);
        this.registerDropSelfLootTable(Blocks.NETHER_BRICK_FENCE);
        this.registerDropSelfLootTable(Blocks.NETHER_BRICK_STAIRS);
        this.registerDropSelfLootTable(Blocks.CAULDRON);
        this.registerDropSelfLootTable(Blocks.END_STONE);
        this.registerDropSelfLootTable(Blocks.REDSTONE_LAMP);
        this.registerDropSelfLootTable(Blocks.SANDSTONE_STAIRS);
        this.registerDropSelfLootTable(Blocks.TRIPWIRE_HOOK);
        this.registerDropSelfLootTable(Blocks.EMERALD_BLOCK);
        this.registerDropSelfLootTable(Blocks.SPRUCE_STAIRS);
        this.registerDropSelfLootTable(Blocks.BIRCH_STAIRS);
        this.registerDropSelfLootTable(Blocks.JUNGLE_STAIRS);
        this.registerDropSelfLootTable(Blocks.COBBLESTONE_WALL);
        this.registerDropSelfLootTable(Blocks.MOSSY_COBBLESTONE_WALL);
        this.registerDropSelfLootTable(Blocks.FLOWER_POT);
        this.registerDropSelfLootTable(Blocks.OAK_BUTTON);
        this.registerDropSelfLootTable(Blocks.SPRUCE_BUTTON);
        this.registerDropSelfLootTable(Blocks.BIRCH_BUTTON);
        this.registerDropSelfLootTable(Blocks.JUNGLE_BUTTON);
        this.registerDropSelfLootTable(Blocks.ACACIA_BUTTON);
        this.registerDropSelfLootTable(Blocks.DARK_OAK_BUTTON);
        this.registerDropSelfLootTable(Blocks.SKELETON_SKULL);
        this.registerDropSelfLootTable(Blocks.WITHER_SKELETON_SKULL);
        this.registerDropSelfLootTable(Blocks.ZOMBIE_HEAD);
        this.registerDropSelfLootTable(Blocks.CREEPER_HEAD);
        this.registerDropSelfLootTable(Blocks.DRAGON_HEAD);
        this.registerDropSelfLootTable(Blocks.ANVIL);
        this.registerDropSelfLootTable(Blocks.CHIPPED_ANVIL);
        this.registerDropSelfLootTable(Blocks.DAMAGED_ANVIL);
        this.registerDropSelfLootTable(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
        this.registerDropSelfLootTable(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        this.registerDropSelfLootTable(Blocks.COMPARATOR);
        this.registerDropSelfLootTable(Blocks.DAYLIGHT_DETECTOR);
        this.registerDropSelfLootTable(Blocks.REDSTONE_BLOCK);
        this.registerDropSelfLootTable(Blocks.QUARTZ_BLOCK);
        this.registerDropSelfLootTable(Blocks.CHISELED_QUARTZ_BLOCK);
        this.registerDropSelfLootTable(Blocks.QUARTZ_PILLAR);
        this.registerDropSelfLootTable(Blocks.QUARTZ_STAIRS);
        this.registerDropSelfLootTable(Blocks.ACTIVATOR_RAIL);
        this.registerDropSelfLootTable(Blocks.WHITE_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.ORANGE_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.MAGENTA_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.LIGHT_BLUE_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.YELLOW_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.LIME_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.PINK_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.GRAY_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.LIGHT_GRAY_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.CYAN_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.PURPLE_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.BLUE_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.BROWN_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.GREEN_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.RED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.BLACK_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.ACACIA_STAIRS);
        this.registerDropSelfLootTable(Blocks.DARK_OAK_STAIRS);
        this.registerDropSelfLootTable(Blocks.SLIME_BLOCK);
        this.registerDropSelfLootTable(Blocks.IRON_TRAPDOOR);
        this.registerDropSelfLootTable(Blocks.PRISMARINE);
        this.registerDropSelfLootTable(Blocks.PRISMARINE_BRICKS);
        this.registerDropSelfLootTable(Blocks.DARK_PRISMARINE);
        this.registerDropSelfLootTable(Blocks.PRISMARINE_STAIRS);
        this.registerDropSelfLootTable(Blocks.PRISMARINE_BRICK_STAIRS);
        this.registerDropSelfLootTable(Blocks.DARK_PRISMARINE_STAIRS);
        this.registerDropSelfLootTable(Blocks.HAY_BLOCK);
        this.registerDropSelfLootTable(Blocks.WHITE_CARPET);
        this.registerDropSelfLootTable(Blocks.ORANGE_CARPET);
        this.registerDropSelfLootTable(Blocks.MAGENTA_CARPET);
        this.registerDropSelfLootTable(Blocks.LIGHT_BLUE_CARPET);
        this.registerDropSelfLootTable(Blocks.YELLOW_CARPET);
        this.registerDropSelfLootTable(Blocks.LIME_CARPET);
        this.registerDropSelfLootTable(Blocks.PINK_CARPET);
        this.registerDropSelfLootTable(Blocks.GRAY_CARPET);
        this.registerDropSelfLootTable(Blocks.LIGHT_GRAY_CARPET);
        this.registerDropSelfLootTable(Blocks.CYAN_CARPET);
        this.registerDropSelfLootTable(Blocks.PURPLE_CARPET);
        this.registerDropSelfLootTable(Blocks.BLUE_CARPET);
        this.registerDropSelfLootTable(Blocks.BROWN_CARPET);
        this.registerDropSelfLootTable(Blocks.GREEN_CARPET);
        this.registerDropSelfLootTable(Blocks.RED_CARPET);
        this.registerDropSelfLootTable(Blocks.BLACK_CARPET);
        this.registerDropSelfLootTable(Blocks.TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.COAL_BLOCK);
        this.registerDropSelfLootTable(Blocks.RED_SANDSTONE);
        this.registerDropSelfLootTable(Blocks.CHISELED_RED_SANDSTONE);
        this.registerDropSelfLootTable(Blocks.CUT_RED_SANDSTONE);
        this.registerDropSelfLootTable(Blocks.RED_SANDSTONE_STAIRS);
        this.registerDropSelfLootTable(Blocks.SMOOTH_STONE);
        this.registerDropSelfLootTable(Blocks.SMOOTH_SANDSTONE);
        this.registerDropSelfLootTable(Blocks.SMOOTH_QUARTZ);
        this.registerDropSelfLootTable(Blocks.SMOOTH_RED_SANDSTONE);
        this.registerDropSelfLootTable(Blocks.SPRUCE_FENCE_GATE);
        this.registerDropSelfLootTable(Blocks.BIRCH_FENCE_GATE);
        this.registerDropSelfLootTable(Blocks.JUNGLE_FENCE_GATE);
        this.registerDropSelfLootTable(Blocks.ACACIA_FENCE_GATE);
        this.registerDropSelfLootTable(Blocks.DARK_OAK_FENCE_GATE);
        this.registerDropSelfLootTable(Blocks.SPRUCE_FENCE);
        this.registerDropSelfLootTable(Blocks.BIRCH_FENCE);
        this.registerDropSelfLootTable(Blocks.JUNGLE_FENCE);
        this.registerDropSelfLootTable(Blocks.ACACIA_FENCE);
        this.registerDropSelfLootTable(Blocks.DARK_OAK_FENCE);
        this.registerDropSelfLootTable(Blocks.END_ROD);
        this.registerDropSelfLootTable(Blocks.PURPUR_BLOCK);
        this.registerDropSelfLootTable(Blocks.PURPUR_PILLAR);
        this.registerDropSelfLootTable(Blocks.PURPUR_STAIRS);
        this.registerDropSelfLootTable(Blocks.END_STONE_BRICKS);
        this.registerDropSelfLootTable(Blocks.MAGMA_BLOCK);
        this.registerDropSelfLootTable(Blocks.NETHER_WART_BLOCK);
        this.registerDropSelfLootTable(Blocks.RED_NETHER_BRICKS);
        this.registerDropSelfLootTable(Blocks.BONE_BLOCK);
        this.registerDropSelfLootTable(Blocks.OBSERVER);
        this.registerDropSelfLootTable(Blocks.TARGET);
        this.registerDropSelfLootTable(Blocks.WHITE_GLAZED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.ORANGE_GLAZED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.MAGENTA_GLAZED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.YELLOW_GLAZED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.LIME_GLAZED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.PINK_GLAZED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.GRAY_GLAZED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.CYAN_GLAZED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.PURPLE_GLAZED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.BLUE_GLAZED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.BROWN_GLAZED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.GREEN_GLAZED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.RED_GLAZED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.BLACK_GLAZED_TERRACOTTA);
        this.registerDropSelfLootTable(Blocks.WHITE_CONCRETE);
        this.registerDropSelfLootTable(Blocks.ORANGE_CONCRETE);
        this.registerDropSelfLootTable(Blocks.MAGENTA_CONCRETE);
        this.registerDropSelfLootTable(Blocks.LIGHT_BLUE_CONCRETE);
        this.registerDropSelfLootTable(Blocks.YELLOW_CONCRETE);
        this.registerDropSelfLootTable(Blocks.LIME_CONCRETE);
        this.registerDropSelfLootTable(Blocks.PINK_CONCRETE);
        this.registerDropSelfLootTable(Blocks.GRAY_CONCRETE);
        this.registerDropSelfLootTable(Blocks.LIGHT_GRAY_CONCRETE);
        this.registerDropSelfLootTable(Blocks.CYAN_CONCRETE);
        this.registerDropSelfLootTable(Blocks.PURPLE_CONCRETE);
        this.registerDropSelfLootTable(Blocks.BLUE_CONCRETE);
        this.registerDropSelfLootTable(Blocks.BROWN_CONCRETE);
        this.registerDropSelfLootTable(Blocks.GREEN_CONCRETE);
        this.registerDropSelfLootTable(Blocks.RED_CONCRETE);
        this.registerDropSelfLootTable(Blocks.BLACK_CONCRETE);
        this.registerDropSelfLootTable(Blocks.WHITE_CONCRETE_POWDER);
        this.registerDropSelfLootTable(Blocks.ORANGE_CONCRETE_POWDER);
        this.registerDropSelfLootTable(Blocks.MAGENTA_CONCRETE_POWDER);
        this.registerDropSelfLootTable(Blocks.LIGHT_BLUE_CONCRETE_POWDER);
        this.registerDropSelfLootTable(Blocks.YELLOW_CONCRETE_POWDER);
        this.registerDropSelfLootTable(Blocks.LIME_CONCRETE_POWDER);
        this.registerDropSelfLootTable(Blocks.PINK_CONCRETE_POWDER);
        this.registerDropSelfLootTable(Blocks.GRAY_CONCRETE_POWDER);
        this.registerDropSelfLootTable(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
        this.registerDropSelfLootTable(Blocks.CYAN_CONCRETE_POWDER);
        this.registerDropSelfLootTable(Blocks.PURPLE_CONCRETE_POWDER);
        this.registerDropSelfLootTable(Blocks.BLUE_CONCRETE_POWDER);
        this.registerDropSelfLootTable(Blocks.BROWN_CONCRETE_POWDER);
        this.registerDropSelfLootTable(Blocks.GREEN_CONCRETE_POWDER);
        this.registerDropSelfLootTable(Blocks.RED_CONCRETE_POWDER);
        this.registerDropSelfLootTable(Blocks.BLACK_CONCRETE_POWDER);
        this.registerDropSelfLootTable(Blocks.KELP);
        this.registerDropSelfLootTable(Blocks.DRIED_KELP_BLOCK);
        this.registerDropSelfLootTable(Blocks.DEAD_TUBE_CORAL_BLOCK);
        this.registerDropSelfLootTable(Blocks.DEAD_BRAIN_CORAL_BLOCK);
        this.registerDropSelfLootTable(Blocks.DEAD_BUBBLE_CORAL_BLOCK);
        this.registerDropSelfLootTable(Blocks.DEAD_FIRE_CORAL_BLOCK);
        this.registerDropSelfLootTable(Blocks.DEAD_HORN_CORAL_BLOCK);
        this.registerDropSelfLootTable(Blocks.CONDUIT);
        this.registerDropSelfLootTable(Blocks.DRAGON_EGG);
        this.registerDropSelfLootTable(Blocks.BAMBOO);
        this.registerDropSelfLootTable(Blocks.POLISHED_GRANITE_STAIRS);
        this.registerDropSelfLootTable(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
        this.registerDropSelfLootTable(Blocks.MOSSY_STONE_BRICK_STAIRS);
        this.registerDropSelfLootTable(Blocks.POLISHED_DIORITE_STAIRS);
        this.registerDropSelfLootTable(Blocks.MOSSY_COBBLESTONE_STAIRS);
        this.registerDropSelfLootTable(Blocks.END_STONE_BRICK_STAIRS);
        this.registerDropSelfLootTable(Blocks.STONE_STAIRS);
        this.registerDropSelfLootTable(Blocks.SMOOTH_SANDSTONE_STAIRS);
        this.registerDropSelfLootTable(Blocks.SMOOTH_QUARTZ_STAIRS);
        this.registerDropSelfLootTable(Blocks.GRANITE_STAIRS);
        this.registerDropSelfLootTable(Blocks.ANDESITE_STAIRS);
        this.registerDropSelfLootTable(Blocks.RED_NETHER_BRICK_STAIRS);
        this.registerDropSelfLootTable(Blocks.POLISHED_ANDESITE_STAIRS);
        this.registerDropSelfLootTable(Blocks.DIORITE_STAIRS);
        this.registerDropSelfLootTable(Blocks.BRICK_WALL);
        this.registerDropSelfLootTable(Blocks.PRISMARINE_WALL);
        this.registerDropSelfLootTable(Blocks.RED_SANDSTONE_WALL);
        this.registerDropSelfLootTable(Blocks.MOSSY_STONE_BRICK_WALL);
        this.registerDropSelfLootTable(Blocks.GRANITE_WALL);
        this.registerDropSelfLootTable(Blocks.STONE_BRICK_WALL);
        this.registerDropSelfLootTable(Blocks.NETHER_BRICK_WALL);
        this.registerDropSelfLootTable(Blocks.ANDESITE_WALL);
        this.registerDropSelfLootTable(Blocks.RED_NETHER_BRICK_WALL);
        this.registerDropSelfLootTable(Blocks.SANDSTONE_WALL);
        this.registerDropSelfLootTable(Blocks.END_STONE_BRICK_WALL);
        this.registerDropSelfLootTable(Blocks.DIORITE_WALL);
        this.registerDropSelfLootTable(Blocks.LOOM);
        this.registerDropSelfLootTable(Blocks.SCAFFOLDING);
        this.registerDropSelfLootTable(Blocks.HONEY_BLOCK);
        this.registerDropSelfLootTable(Blocks.HONEYCOMB_BLOCK);
        this.registerDropSelfLootTable(Blocks.RESPAWN_ANCHOR);
        this.registerDropSelfLootTable(Blocks.LODESTONE);
        this.registerDropSelfLootTable(Blocks.WARPED_STEM);
        this.registerDropSelfLootTable(Blocks.WARPED_HYPHAE);
        this.registerDropSelfLootTable(Blocks.WARPED_FUNGUS);
        this.registerDropSelfLootTable(Blocks.WARPED_WART_BLOCK);
        this.registerDropSelfLootTable(Blocks.CRIMSON_STEM);
        this.registerDropSelfLootTable(Blocks.CRIMSON_HYPHAE);
        this.registerDropSelfLootTable(Blocks.CRIMSON_FUNGUS);
        this.registerDropSelfLootTable(Blocks.SHROOMLIGHT);
        this.registerDropSelfLootTable(Blocks.CRIMSON_PLANKS);
        this.registerDropSelfLootTable(Blocks.WARPED_PLANKS);
        this.registerDropSelfLootTable(Blocks.WARPED_PRESSURE_PLATE);
        this.registerDropSelfLootTable(Blocks.WARPED_FENCE);
        this.registerDropSelfLootTable(Blocks.WARPED_TRAPDOOR);
        this.registerDropSelfLootTable(Blocks.WARPED_FENCE_GATE);
        this.registerDropSelfLootTable(Blocks.WARPED_STAIRS);
        this.registerDropSelfLootTable(Blocks.WARPED_BUTTON);
        this.registerDropSelfLootTable(Blocks.WARPED_SIGN);
        this.registerDropSelfLootTable(Blocks.CRIMSON_PRESSURE_PLATE);
        this.registerDropSelfLootTable(Blocks.CRIMSON_FENCE);
        this.registerDropSelfLootTable(Blocks.CRIMSON_TRAPDOOR);
        this.registerDropSelfLootTable(Blocks.CRIMSON_FENCE_GATE);
        this.registerDropSelfLootTable(Blocks.CRIMSON_STAIRS);
        this.registerDropSelfLootTable(Blocks.CRIMSON_BUTTON);
        this.registerDropSelfLootTable(Blocks.CRIMSON_SIGN);
        this.registerDropSelfLootTable(Blocks.NETHERITE_BLOCK);
        this.registerDropSelfLootTable(Blocks.ANCIENT_DEBRIS);
        this.registerDropSelfLootTable(Blocks.BLACKSTONE);
        this.registerDropSelfLootTable(Blocks.POLISHED_BLACKSTONE_BRICKS);
        this.registerDropSelfLootTable(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
        this.registerDropSelfLootTable(Blocks.BLACKSTONE_STAIRS);
        this.registerDropSelfLootTable(Blocks.BLACKSTONE_WALL);
        this.registerDropSelfLootTable(Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        this.registerDropSelfLootTable(Blocks.CHISELED_POLISHED_BLACKSTONE);
        this.registerDropSelfLootTable(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        this.registerDropSelfLootTable(Blocks.POLISHED_BLACKSTONE);
        this.registerDropSelfLootTable(Blocks.POLISHED_BLACKSTONE_STAIRS);
        this.registerDropSelfLootTable(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
        this.registerDropSelfLootTable(Blocks.POLISHED_BLACKSTONE_BUTTON);
        this.registerDropSelfLootTable(Blocks.POLISHED_BLACKSTONE_WALL);
        this.registerDropSelfLootTable(Blocks.CHISELED_NETHER_BRICKS);
        this.registerDropSelfLootTable(Blocks.CRACKED_NETHER_BRICKS);
        this.registerDropSelfLootTable(Blocks.QUARTZ_BRICKS);
        this.registerDropSelfLootTable(Blocks.CHAIN);
        this.registerDropSelfLootTable(Blocks.WARPED_ROOTS);
        this.registerDropSelfLootTable(Blocks.CRIMSON_ROOTS);
        this.registerDropping(Blocks.FARMLAND, Blocks.DIRT);
        this.registerDropping(Blocks.TRIPWIRE, Items.STRING);
        this.registerDropping(Blocks.GRASS_PATH, Blocks.DIRT);
        this.registerDropping(Blocks.KELP_PLANT, Blocks.KELP);
        this.registerDropping(Blocks.BAMBOO_SAPLING, Blocks.BAMBOO);
        this.registerLootTable(Blocks.STONE, (stone) ->
        {
            return droppingWithSilkTouch(stone, Blocks.COBBLESTONE);
        });
        this.registerLootTable(Blocks.GRASS_BLOCK, (grass) ->
        {
            return droppingWithSilkTouch(grass, Blocks.DIRT);
        });
        this.registerLootTable(Blocks.PODZOL, (podzol) ->
        {
            return droppingWithSilkTouch(podzol, Blocks.DIRT);
        });
        this.registerLootTable(Blocks.MYCELIUM, (mycelium) ->
        {
            return droppingWithSilkTouch(mycelium, Blocks.DIRT);
        });
        this.registerLootTable(Blocks.TUBE_CORAL_BLOCK, (tubeCoral) ->
        {
            return droppingWithSilkTouch(tubeCoral, Blocks.DEAD_TUBE_CORAL_BLOCK);
        });
        this.registerLootTable(Blocks.BRAIN_CORAL_BLOCK, (brainCoral) ->
        {
            return droppingWithSilkTouch(brainCoral, Blocks.DEAD_BRAIN_CORAL_BLOCK);
        });
        this.registerLootTable(Blocks.BUBBLE_CORAL_BLOCK, (bubbleCoral) ->
        {
            return droppingWithSilkTouch(bubbleCoral, Blocks.DEAD_BUBBLE_CORAL_BLOCK);
        });
        this.registerLootTable(Blocks.FIRE_CORAL_BLOCK, (fireCoral) ->
        {
            return droppingWithSilkTouch(fireCoral, Blocks.DEAD_FIRE_CORAL_BLOCK);
        });
        this.registerLootTable(Blocks.HORN_CORAL_BLOCK, (hornCoral) ->
        {
            return droppingWithSilkTouch(hornCoral, Blocks.DEAD_HORN_CORAL_BLOCK);
        });
        this.registerLootTable(Blocks.CRIMSON_NYLIUM, (crimson) ->
        {
            return droppingWithSilkTouch(crimson, Blocks.NETHERRACK);
        });
        this.registerLootTable(Blocks.WARPED_NYLIUM, (warped) ->
        {
            return droppingWithSilkTouch(warped, Blocks.NETHERRACK);
        });
        this.registerLootTable(Blocks.BOOKSHELF, (bookshelf) ->
        {
            return droppingWithSilkTouchOrRandomly(bookshelf, Items.BOOK, ConstantRange.of(3));
        });
        this.registerLootTable(Blocks.CLAY, (clay) ->
        {
            return droppingWithSilkTouchOrRandomly(clay, Items.CLAY_BALL, ConstantRange.of(4));
        });
        this.registerLootTable(Blocks.ENDER_CHEST, (enderChest) ->
        {
            return droppingWithSilkTouchOrRandomly(enderChest, Blocks.OBSIDIAN, ConstantRange.of(8));
        });
        this.registerLootTable(Blocks.SNOW_BLOCK, (snow) ->
        {
            return droppingWithSilkTouchOrRandomly(snow, Items.SNOWBALL, ConstantRange.of(4));
        });
        this.registerLootTable(Blocks.CHORUS_PLANT, droppingRandomly(Items.CHORUS_FRUIT, RandomValueRange.of(0.0F, 1.0F)));
        this.registerFlowerPot(Blocks.POTTED_OAK_SAPLING);
        this.registerFlowerPot(Blocks.POTTED_SPRUCE_SAPLING);
        this.registerFlowerPot(Blocks.POTTED_BIRCH_SAPLING);
        this.registerFlowerPot(Blocks.POTTED_JUNGLE_SAPLING);
        this.registerFlowerPot(Blocks.POTTED_ACACIA_SAPLING);
        this.registerFlowerPot(Blocks.POTTED_DARK_OAK_SAPLING);
        this.registerFlowerPot(Blocks.POTTED_FERN);
        this.registerFlowerPot(Blocks.POTTED_DANDELION);
        this.registerFlowerPot(Blocks.POTTED_POPPY);
        this.registerFlowerPot(Blocks.POTTED_BLUE_ORCHID);
        this.registerFlowerPot(Blocks.POTTED_ALLIUM);
        this.registerFlowerPot(Blocks.POTTED_AZURE_BLUET);
        this.registerFlowerPot(Blocks.POTTED_RED_TULIP);
        this.registerFlowerPot(Blocks.POTTED_ORANGE_TULIP);
        this.registerFlowerPot(Blocks.POTTED_WHITE_TULIP);
        this.registerFlowerPot(Blocks.POTTED_PINK_TULIP);
        this.registerFlowerPot(Blocks.POTTED_OXEYE_DAISY);
        this.registerFlowerPot(Blocks.POTTED_CORNFLOWER);
        this.registerFlowerPot(Blocks.POTTED_LILY_OF_THE_VALLEY);
        this.registerFlowerPot(Blocks.POTTED_WITHER_ROSE);
        this.registerFlowerPot(Blocks.POTTED_RED_MUSHROOM);
        this.registerFlowerPot(Blocks.POTTED_BROWN_MUSHROOM);
        this.registerFlowerPot(Blocks.POTTED_DEAD_BUSH);
        this.registerFlowerPot(Blocks.POTTED_CACTUS);
        this.registerFlowerPot(Blocks.POTTED_BAMBOO);
        this.registerFlowerPot(Blocks.POTTED_CRIMSON_FUNGUS);
        this.registerFlowerPot(Blocks.POTTED_WARPED_FUNGUS);
        this.registerFlowerPot(Blocks.POTTED_CRIMSON_ROOTS);
        this.registerFlowerPot(Blocks.POTTED_WARPED_ROOTS);
        this.registerLootTable(Blocks.ACACIA_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.BIRCH_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.BRICK_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.COBBLESTONE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.DARK_OAK_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.DARK_PRISMARINE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.JUNGLE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.NETHER_BRICK_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.OAK_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.PETRIFIED_OAK_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.PRISMARINE_BRICK_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.PRISMARINE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.PURPUR_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.QUARTZ_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.RED_SANDSTONE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.SANDSTONE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.CUT_RED_SANDSTONE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.CUT_SANDSTONE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.SPRUCE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.STONE_BRICK_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.STONE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.SMOOTH_STONE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.POLISHED_GRANITE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.SMOOTH_RED_SANDSTONE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.MOSSY_STONE_BRICK_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.POLISHED_DIORITE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.MOSSY_COBBLESTONE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.END_STONE_BRICK_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.SMOOTH_SANDSTONE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.SMOOTH_QUARTZ_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.GRANITE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.ANDESITE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.RED_NETHER_BRICK_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.POLISHED_ANDESITE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.DIORITE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.CRIMSON_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.WARPED_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.BLACKSTONE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.POLISHED_BLACKSTONE_SLAB, BlockLootTables::droppingSlab);
        this.registerLootTable(Blocks.ACACIA_DOOR, BlockLootTables::registerDoor);
        this.registerLootTable(Blocks.BIRCH_DOOR, BlockLootTables::registerDoor);
        this.registerLootTable(Blocks.DARK_OAK_DOOR, BlockLootTables::registerDoor);
        this.registerLootTable(Blocks.IRON_DOOR, BlockLootTables::registerDoor);
        this.registerLootTable(Blocks.JUNGLE_DOOR, BlockLootTables::registerDoor);
        this.registerLootTable(Blocks.OAK_DOOR, BlockLootTables::registerDoor);
        this.registerLootTable(Blocks.SPRUCE_DOOR, BlockLootTables::registerDoor);
        this.registerLootTable(Blocks.WARPED_DOOR, BlockLootTables::registerDoor);
        this.registerLootTable(Blocks.CRIMSON_DOOR, BlockLootTables::registerDoor);
        this.registerLootTable(Blocks.BLACK_BED, (bed) ->
        {
            return droppingWhen(bed, BedBlock.PART, BedPart.HEAD);
        });
        this.registerLootTable(Blocks.BLUE_BED, (bed) ->
        {
            return droppingWhen(bed, BedBlock.PART, BedPart.HEAD);
        });
        this.registerLootTable(Blocks.BROWN_BED, (bed) ->
        {
            return droppingWhen(bed, BedBlock.PART, BedPart.HEAD);
        });
        this.registerLootTable(Blocks.CYAN_BED, (bed) ->
        {
            return droppingWhen(bed, BedBlock.PART, BedPart.HEAD);
        });
        this.registerLootTable(Blocks.GRAY_BED, (bed) ->
        {
            return droppingWhen(bed, BedBlock.PART, BedPart.HEAD);
        });
        this.registerLootTable(Blocks.GREEN_BED, (bed) ->
        {
            return droppingWhen(bed, BedBlock.PART, BedPart.HEAD);
        });
        this.registerLootTable(Blocks.LIGHT_BLUE_BED, (bed) ->
        {
            return droppingWhen(bed, BedBlock.PART, BedPart.HEAD);
        });
        this.registerLootTable(Blocks.LIGHT_GRAY_BED, (bed) ->
        {
            return droppingWhen(bed, BedBlock.PART, BedPart.HEAD);
        });
        this.registerLootTable(Blocks.LIME_BED, (bed) ->
        {
            return droppingWhen(bed, BedBlock.PART, BedPart.HEAD);
        });
        this.registerLootTable(Blocks.MAGENTA_BED, (bed) ->
        {
            return droppingWhen(bed, BedBlock.PART, BedPart.HEAD);
        });
        this.registerLootTable(Blocks.PURPLE_BED, (bed) ->
        {
            return droppingWhen(bed, BedBlock.PART, BedPart.HEAD);
        });
        this.registerLootTable(Blocks.ORANGE_BED, (bed) ->
        {
            return droppingWhen(bed, BedBlock.PART, BedPart.HEAD);
        });
        this.registerLootTable(Blocks.PINK_BED, (bed) ->
        {
            return droppingWhen(bed, BedBlock.PART, BedPart.HEAD);
        });
        this.registerLootTable(Blocks.RED_BED, (bed) ->
        {
            return droppingWhen(bed, BedBlock.PART, BedPart.HEAD);
        });
        this.registerLootTable(Blocks.WHITE_BED, (bed) ->
        {
            return droppingWhen(bed, BedBlock.PART, BedPart.HEAD);
        });
        this.registerLootTable(Blocks.YELLOW_BED, (bed) ->
        {
            return droppingWhen(bed, BedBlock.PART, BedPart.HEAD);
        });
        this.registerLootTable(Blocks.LILAC, (lilac) ->
        {
            return droppingWhen(lilac, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
        });
        this.registerLootTable(Blocks.SUNFLOWER, (sunflower) ->
        {
            return droppingWhen(sunflower, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
        });
        this.registerLootTable(Blocks.PEONY, (peony) ->
        {
            return droppingWhen(peony, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
        });
        this.registerLootTable(Blocks.ROSE_BUSH, (roseBush) ->
        {
            return droppingWhen(roseBush, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
        });
        this.registerLootTable(Blocks.TNT, LootTable.builder().addLootPool(withSurvivesExplosion(Blocks.TNT, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Blocks.TNT).acceptCondition(BlockStateProperty.builder(Blocks.TNT).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withBoolProp(TNTBlock.UNSTABLE, false)))))));
        this.registerLootTable(Blocks.COCOA, (cocoa) ->
        {
            return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(withExplosionDecay(cocoa, ItemLootEntry.builder(Items.COCOA_BEANS).acceptFunction(SetCount.builder(ConstantRange.of(3)).acceptCondition(BlockStateProperty.builder(cocoa).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(CocoaBlock.AGE, 2)))))));
        });
        this.registerLootTable(Blocks.SEA_PICKLE, (pickle) ->
        {
            return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(withExplosionDecay(Blocks.SEA_PICKLE, ItemLootEntry.builder(pickle).acceptFunction(SetCount.builder(ConstantRange.of(2)).acceptCondition(BlockStateProperty.builder(pickle).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SeaPickleBlock.PICKLES, 2)))).acceptFunction(SetCount.builder(ConstantRange.of(3)).acceptCondition(BlockStateProperty.builder(pickle).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SeaPickleBlock.PICKLES, 3)))).acceptFunction(SetCount.builder(ConstantRange.of(4)).acceptCondition(BlockStateProperty.builder(pickle).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SeaPickleBlock.PICKLES, 4)))))));
        });
        this.registerLootTable(Blocks.COMPOSTER, (composter) ->
        {
            return LootTable.builder().addLootPool(LootPool.builder().addEntry(withExplosionDecay(composter, ItemLootEntry.builder(Items.COMPOSTER)))).addLootPool(LootPool.builder().addEntry(ItemLootEntry.builder(Items.BONE_MEAL)).acceptCondition(BlockStateProperty.builder(composter).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(ComposterBlock.LEVEL, 8))));
        });
        this.registerLootTable(Blocks.BEACON, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.BREWING_STAND, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.CHEST, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.DISPENSER, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.DROPPER, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.ENCHANTING_TABLE, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.FURNACE, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.HOPPER, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.TRAPPED_CHEST, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.SMOKER, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.BLAST_FURNACE, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.BARREL, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.CARTOGRAPHY_TABLE, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.FLETCHING_TABLE, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.GRINDSTONE, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.LECTERN, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.SMITHING_TABLE, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.STONECUTTER, BlockLootTables::droppingWithName);
        this.registerLootTable(Blocks.BELL, BlockLootTables::dropping);
        this.registerLootTable(Blocks.LANTERN, BlockLootTables::dropping);
        this.registerLootTable(Blocks.SOUL_LANTERN, BlockLootTables::dropping);
        this.registerLootTable(Blocks.SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.BLACK_SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.BLUE_SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.BROWN_SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.CYAN_SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.GRAY_SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.GREEN_SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.LIGHT_BLUE_SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.LIGHT_GRAY_SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.LIME_SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.MAGENTA_SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.ORANGE_SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.PINK_SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.PURPLE_SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.RED_SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.WHITE_SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.YELLOW_SHULKER_BOX, BlockLootTables::droppingWithContents);
        this.registerLootTable(Blocks.BLACK_BANNER, BlockLootTables::droppingWithPatterns);
        this.registerLootTable(Blocks.BLUE_BANNER, BlockLootTables::droppingWithPatterns);
        this.registerLootTable(Blocks.BROWN_BANNER, BlockLootTables::droppingWithPatterns);
        this.registerLootTable(Blocks.CYAN_BANNER, BlockLootTables::droppingWithPatterns);
        this.registerLootTable(Blocks.GRAY_BANNER, BlockLootTables::droppingWithPatterns);
        this.registerLootTable(Blocks.GREEN_BANNER, BlockLootTables::droppingWithPatterns);
        this.registerLootTable(Blocks.LIGHT_BLUE_BANNER, BlockLootTables::droppingWithPatterns);
        this.registerLootTable(Blocks.LIGHT_GRAY_BANNER, BlockLootTables::droppingWithPatterns);
        this.registerLootTable(Blocks.LIME_BANNER, BlockLootTables::droppingWithPatterns);
        this.registerLootTable(Blocks.MAGENTA_BANNER, BlockLootTables::droppingWithPatterns);
        this.registerLootTable(Blocks.ORANGE_BANNER, BlockLootTables::droppingWithPatterns);
        this.registerLootTable(Blocks.PINK_BANNER, BlockLootTables::droppingWithPatterns);
        this.registerLootTable(Blocks.PURPLE_BANNER, BlockLootTables::droppingWithPatterns);
        this.registerLootTable(Blocks.RED_BANNER, BlockLootTables::droppingWithPatterns);
        this.registerLootTable(Blocks.WHITE_BANNER, BlockLootTables::droppingWithPatterns);
        this.registerLootTable(Blocks.YELLOW_BANNER, BlockLootTables::droppingWithPatterns);
        this.registerLootTable(Blocks.PLAYER_HEAD, (playerHead) ->
        {
            return LootTable.builder().addLootPool(withSurvivesExplosion(playerHead, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(playerHead).acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("SkullOwner", "SkullOwner")))));
        });
        this.registerLootTable(Blocks.BEE_NEST, BlockLootTables::droppingAndBees);
        this.registerLootTable(Blocks.BEEHIVE, BlockLootTables::droppingAndBeesWithAlternative);
        this.registerLootTable(Blocks.BIRCH_LEAVES, (leaves) ->
        {
            return droppingWithChancesAndSticks(leaves, Blocks.BIRCH_SAPLING, DEFAULT_SAPLING_DROP_RATES);
        });
        this.registerLootTable(Blocks.ACACIA_LEAVES, (leaves) ->
        {
            return droppingWithChancesAndSticks(leaves, Blocks.ACACIA_SAPLING, DEFAULT_SAPLING_DROP_RATES);
        });
        this.registerLootTable(Blocks.JUNGLE_LEAVES, (leaves) ->
        {
            return droppingWithChancesAndSticks(leaves, Blocks.JUNGLE_SAPLING, RARE_SAPLING_DROP_RATES);
        });
        this.registerLootTable(Blocks.SPRUCE_LEAVES, (leaves) ->
        {
            return droppingWithChancesAndSticks(leaves, Blocks.SPRUCE_SAPLING, DEFAULT_SAPLING_DROP_RATES);
        });
        this.registerLootTable(Blocks.OAK_LEAVES, (leaves) ->
        {
            return droppingWithChancesSticksAndApples(leaves, Blocks.OAK_SAPLING, DEFAULT_SAPLING_DROP_RATES);
        });
        this.registerLootTable(Blocks.DARK_OAK_LEAVES, (leaves) ->
        {
            return droppingWithChancesSticksAndApples(leaves, Blocks.DARK_OAK_SAPLING, DEFAULT_SAPLING_DROP_RATES);
        });
        ILootCondition.IBuilder ilootcondition$ibuilder = BlockStateProperty.builder(Blocks.BEETROOTS).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(BeetrootBlock.BEETROOT_AGE, 3));
        this.registerLootTable(Blocks.BEETROOTS, droppingAndBonusWhen(Blocks.BEETROOTS, Items.BEETROOT, Items.BEETROOT_SEEDS, ilootcondition$ibuilder));
        ILootCondition.IBuilder ilootcondition$ibuilder1 = BlockStateProperty.builder(Blocks.WHEAT).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(CropsBlock.AGE, 7));
        this.registerLootTable(Blocks.WHEAT, droppingAndBonusWhen(Blocks.WHEAT, Items.WHEAT, Items.WHEAT_SEEDS, ilootcondition$ibuilder1));
        ILootCondition.IBuilder ilootcondition$ibuilder2 = BlockStateProperty.builder(Blocks.CARROTS).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(CarrotBlock.AGE, 7));
        this.registerLootTable(Blocks.CARROTS, withExplosionDecay(Blocks.CARROTS, LootTable.builder().addLootPool(LootPool.builder().addEntry(ItemLootEntry.builder(Items.CARROT))).addLootPool(LootPool.builder().acceptCondition(ilootcondition$ibuilder2).addEntry(ItemLootEntry.builder(Items.CARROT).acceptFunction(ApplyBonus.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3))))));
        ILootCondition.IBuilder ilootcondition$ibuilder3 = BlockStateProperty.builder(Blocks.POTATOES).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(PotatoBlock.AGE, 7));
        this.registerLootTable(Blocks.POTATOES, withExplosionDecay(Blocks.POTATOES, LootTable.builder().addLootPool(LootPool.builder().addEntry(ItemLootEntry.builder(Items.POTATO))).addLootPool(LootPool.builder().acceptCondition(ilootcondition$ibuilder3).addEntry(ItemLootEntry.builder(Items.POTATO).acceptFunction(ApplyBonus.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))).addLootPool(LootPool.builder().acceptCondition(ilootcondition$ibuilder3).addEntry(ItemLootEntry.builder(Items.POISONOUS_POTATO).acceptCondition(RandomChance.builder(0.02F))))));
        this.registerLootTable(Blocks.SWEET_BERRY_BUSH, (sweetBerry) ->
        {
            return withExplosionDecay(sweetBerry, LootTable.builder().addLootPool(LootPool.builder().acceptCondition(BlockStateProperty.builder(Blocks.SWEET_BERRY_BUSH).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SweetBerryBushBlock.AGE, 3))).addEntry(ItemLootEntry.builder(Items.SWEET_BERRIES)).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 3.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE))).addLootPool(LootPool.builder().acceptCondition(BlockStateProperty.builder(Blocks.SWEET_BERRY_BUSH).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SweetBerryBushBlock.AGE, 2))).addEntry(ItemLootEntry.builder(Items.SWEET_BERRIES)).acceptFunction(SetCount.builder(RandomValueRange.of(1.0F, 2.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE))));
        });
        this.registerLootTable(Blocks.BROWN_MUSHROOM_BLOCK, (brownMushroom) ->
        {
            return droppingItemRarely(brownMushroom, Blocks.BROWN_MUSHROOM);
        });
        this.registerLootTable(Blocks.RED_MUSHROOM_BLOCK, (redMushroom) ->
        {
            return droppingItemRarely(redMushroom, Blocks.RED_MUSHROOM);
        });
        this.registerLootTable(Blocks.COAL_ORE, (coal) ->
        {
            return droppingItemWithFortune(coal, Items.COAL);
        });
        this.registerLootTable(Blocks.EMERALD_ORE, (emerald) ->
        {
            return droppingItemWithFortune(emerald, Items.EMERALD);
        });
        this.registerLootTable(Blocks.NETHER_QUARTZ_ORE, (netherQuartz) ->
        {
            return droppingItemWithFortune(netherQuartz, Items.QUARTZ);
        });
        this.registerLootTable(Blocks.DIAMOND_ORE, (diamond) ->
        {
            return droppingItemWithFortune(diamond, Items.DIAMOND);
        });
        this.registerLootTable(Blocks.NETHER_GOLD_ORE, (netherGold) ->
        {
            return droppingWithSilkTouch(netherGold, withExplosionDecay(netherGold, ItemLootEntry.builder(Items.GOLD_NUGGET).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 6.0F))).acceptFunction(ApplyBonus.oreDrops(Enchantments.FORTUNE))));
        });
        this.registerLootTable(Blocks.LAPIS_ORE, (lapis) ->
        {
            return droppingWithSilkTouch(lapis, withExplosionDecay(lapis, ItemLootEntry.builder(Items.LAPIS_LAZULI).acceptFunction(SetCount.builder(RandomValueRange.of(4.0F, 9.0F))).acceptFunction(ApplyBonus.oreDrops(Enchantments.FORTUNE))));
        });
        this.registerLootTable(Blocks.COBWEB, (cobweb) ->
        {
            return droppingWithSilkTouchOrShears(cobweb, withSurvivesExplosion(cobweb, ItemLootEntry.builder(Items.STRING)));
        });
        this.registerLootTable(Blocks.DEAD_BUSH, (deadBush) ->
        {
            return droppingWithShears(deadBush, withExplosionDecay(deadBush, ItemLootEntry.builder(Items.STICK).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F)))));
        });
        this.registerLootTable(Blocks.NETHER_SPROUTS, BlockLootTables::onlyWithShears);
        this.registerLootTable(Blocks.SEAGRASS, BlockLootTables::onlyWithShears);
        this.registerLootTable(Blocks.VINE, BlockLootTables::onlyWithShears);
        this.registerLootTable(Blocks.TALL_SEAGRASS, droppingSheared(Blocks.SEAGRASS));
        this.registerLootTable(Blocks.LARGE_FERN, (fern) ->
        {
            return droppingSeedsTall(fern, Blocks.FERN);
        });
        this.registerLootTable(Blocks.TALL_GRASS, (tallgrass) ->
        {
            return droppingSeedsTall(tallgrass, Blocks.GRASS);
        });
        this.registerLootTable(Blocks.MELON_STEM, (stem) ->
        {
            return droppingByAge(stem, Items.MELON_SEEDS);
        });
        this.registerLootTable(Blocks.ATTACHED_MELON_STEM, (stem) ->
        {
            return dropSeedsForStem(stem, Items.MELON_SEEDS);
        });
        this.registerLootTable(Blocks.PUMPKIN_STEM, (stem) ->
        {
            return droppingByAge(stem, Items.PUMPKIN_SEEDS);
        });
        this.registerLootTable(Blocks.ATTACHED_PUMPKIN_STEM, (stem) ->
        {
            return dropSeedsForStem(stem, Items.PUMPKIN_SEEDS);
        });
        this.registerLootTable(Blocks.CHORUS_FLOWER, (chorusFlower) ->
        {
            return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(withSurvivesExplosion(chorusFlower, ItemLootEntry.builder(chorusFlower)).acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS))));
        });
        this.registerLootTable(Blocks.FERN, BlockLootTables::droppingSeeds);
        this.registerLootTable(Blocks.GRASS, BlockLootTables::droppingSeeds);
        this.registerLootTable(Blocks.GLOWSTONE, (glowstone) ->
        {
            return droppingWithSilkTouch(glowstone, withExplosionDecay(glowstone, ItemLootEntry.builder(Items.GLOWSTONE_DUST).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 4.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE)).acceptFunction(LimitCount.func_215911_a(IntClamper.func_215843_a(1, 4)))));
        });
        this.registerLootTable(Blocks.MELON, (melon) ->
        {
            return droppingWithSilkTouch(melon, withExplosionDecay(melon, ItemLootEntry.builder(Items.MELON_SLICE).acceptFunction(SetCount.builder(RandomValueRange.of(3.0F, 7.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE)).acceptFunction(LimitCount.func_215911_a(IntClamper.func_215851_b(9)))));
        });
        this.registerLootTable(Blocks.REDSTONE_ORE, (redstoneOre) ->
        {
            return droppingWithSilkTouch(redstoneOre, withExplosionDecay(redstoneOre, ItemLootEntry.builder(Items.REDSTONE).acceptFunction(SetCount.builder(RandomValueRange.of(4.0F, 5.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE))));
        });
        this.registerLootTable(Blocks.SEA_LANTERN, (seaLantern) ->
        {
            return droppingWithSilkTouch(seaLantern, withExplosionDecay(seaLantern, ItemLootEntry.builder(Items.PRISMARINE_CRYSTALS).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 3.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE)).acceptFunction(LimitCount.func_215911_a(IntClamper.func_215843_a(1, 5)))));
        });
        this.registerLootTable(Blocks.NETHER_WART, (netherWart) ->
        {
            return LootTable.builder().addLootPool(withExplosionDecay(netherWart, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.NETHER_WART).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 4.0F)).acceptCondition(BlockStateProperty.builder(netherWart).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(NetherWartBlock.AGE, 3)))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE).acceptCondition(BlockStateProperty.builder(netherWart).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(NetherWartBlock.AGE, 3)))))));
        });
        this.registerLootTable(Blocks.SNOW, (snow) ->
        {
            return LootTable.builder().addLootPool(LootPool.builder().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS)).addEntry(AlternativesLootEntry.builder(AlternativesLootEntry.builder(ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(snow).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 1))), ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(snow).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 2))).acceptFunction(SetCount.builder(ConstantRange.of(2))), ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(snow).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 3))).acceptFunction(SetCount.builder(ConstantRange.of(3))), ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(snow).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 4))).acceptFunction(SetCount.builder(ConstantRange.of(4))), ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(snow).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 5))).acceptFunction(SetCount.builder(ConstantRange.of(5))), ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(snow).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 6))).acceptFunction(SetCount.builder(ConstantRange.of(6))), ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(snow).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 7))).acceptFunction(SetCount.builder(ConstantRange.of(7))), ItemLootEntry.builder(Items.SNOWBALL).acceptFunction(SetCount.builder(ConstantRange.of(8)))).acceptCondition(NO_SILK_TOUCH), AlternativesLootEntry.builder(ItemLootEntry.builder(Blocks.SNOW).acceptCondition(BlockStateProperty.builder(snow).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 1))), ItemLootEntry.builder(Blocks.SNOW).acceptFunction(SetCount.builder(ConstantRange.of(2))).acceptCondition(BlockStateProperty.builder(snow).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 2))), ItemLootEntry.builder(Blocks.SNOW).acceptFunction(SetCount.builder(ConstantRange.of(3))).acceptCondition(BlockStateProperty.builder(snow).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 3))), ItemLootEntry.builder(Blocks.SNOW).acceptFunction(SetCount.builder(ConstantRange.of(4))).acceptCondition(BlockStateProperty.builder(snow).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 4))), ItemLootEntry.builder(Blocks.SNOW).acceptFunction(SetCount.builder(ConstantRange.of(5))).acceptCondition(BlockStateProperty.builder(snow).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 5))), ItemLootEntry.builder(Blocks.SNOW).acceptFunction(SetCount.builder(ConstantRange.of(6))).acceptCondition(BlockStateProperty.builder(snow).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 6))), ItemLootEntry.builder(Blocks.SNOW).acceptFunction(SetCount.builder(ConstantRange.of(7))).acceptCondition(BlockStateProperty.builder(snow).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 7))), ItemLootEntry.builder(Blocks.SNOW_BLOCK)))));
        });
        this.registerLootTable(Blocks.GRAVEL, (gravel) ->
        {
            return droppingWithSilkTouch(gravel, withSurvivesExplosion(gravel, ItemLootEntry.builder(Items.FLINT).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F)).alternatively(ItemLootEntry.builder(gravel))));
        });
        this.registerLootTable(Blocks.CAMPFIRE, (unlit) ->
        {
            return droppingWithSilkTouch(unlit, withSurvivesExplosion(unlit, ItemLootEntry.builder(Items.CHARCOAL).acceptFunction(SetCount.builder(ConstantRange.of(2)))));
        });
        this.registerLootTable(Blocks.GILDED_BLACKSTONE, (gildedBlackstone) ->
        {
            return droppingWithSilkTouch(gildedBlackstone, withSurvivesExplosion(gildedBlackstone, ItemLootEntry.builder(Items.GOLD_NUGGET).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 5.0F))).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F)).alternatively(ItemLootEntry.builder(gildedBlackstone))));
        });
        this.registerLootTable(Blocks.SOUL_CAMPFIRE, (unlit) ->
        {
            return droppingWithSilkTouch(unlit, withSurvivesExplosion(unlit, ItemLootEntry.builder(Items.SOUL_SOIL).acceptFunction(SetCount.builder(ConstantRange.of(1)))));
        });
        this.registerSilkTouch(Blocks.GLASS);
        this.registerSilkTouch(Blocks.WHITE_STAINED_GLASS);
        this.registerSilkTouch(Blocks.ORANGE_STAINED_GLASS);
        this.registerSilkTouch(Blocks.MAGENTA_STAINED_GLASS);
        this.registerSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS);
        this.registerSilkTouch(Blocks.YELLOW_STAINED_GLASS);
        this.registerSilkTouch(Blocks.LIME_STAINED_GLASS);
        this.registerSilkTouch(Blocks.PINK_STAINED_GLASS);
        this.registerSilkTouch(Blocks.GRAY_STAINED_GLASS);
        this.registerSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS);
        this.registerSilkTouch(Blocks.CYAN_STAINED_GLASS);
        this.registerSilkTouch(Blocks.PURPLE_STAINED_GLASS);
        this.registerSilkTouch(Blocks.BLUE_STAINED_GLASS);
        this.registerSilkTouch(Blocks.BROWN_STAINED_GLASS);
        this.registerSilkTouch(Blocks.GREEN_STAINED_GLASS);
        this.registerSilkTouch(Blocks.RED_STAINED_GLASS);
        this.registerSilkTouch(Blocks.BLACK_STAINED_GLASS);
        this.registerSilkTouch(Blocks.GLASS_PANE);
        this.registerSilkTouch(Blocks.WHITE_STAINED_GLASS_PANE);
        this.registerSilkTouch(Blocks.ORANGE_STAINED_GLASS_PANE);
        this.registerSilkTouch(Blocks.MAGENTA_STAINED_GLASS_PANE);
        this.registerSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
        this.registerSilkTouch(Blocks.YELLOW_STAINED_GLASS_PANE);
        this.registerSilkTouch(Blocks.LIME_STAINED_GLASS_PANE);
        this.registerSilkTouch(Blocks.PINK_STAINED_GLASS_PANE);
        this.registerSilkTouch(Blocks.GRAY_STAINED_GLASS_PANE);
        this.registerSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
        this.registerSilkTouch(Blocks.CYAN_STAINED_GLASS_PANE);
        this.registerSilkTouch(Blocks.PURPLE_STAINED_GLASS_PANE);
        this.registerSilkTouch(Blocks.BLUE_STAINED_GLASS_PANE);
        this.registerSilkTouch(Blocks.BROWN_STAINED_GLASS_PANE);
        this.registerSilkTouch(Blocks.GREEN_STAINED_GLASS_PANE);
        this.registerSilkTouch(Blocks.RED_STAINED_GLASS_PANE);
        this.registerSilkTouch(Blocks.BLACK_STAINED_GLASS_PANE);
        this.registerSilkTouch(Blocks.ICE);
        this.registerSilkTouch(Blocks.PACKED_ICE);
        this.registerSilkTouch(Blocks.BLUE_ICE);
        this.registerSilkTouch(Blocks.TURTLE_EGG);
        this.registerSilkTouch(Blocks.MUSHROOM_STEM);
        this.registerSilkTouch(Blocks.DEAD_TUBE_CORAL);
        this.registerSilkTouch(Blocks.DEAD_BRAIN_CORAL);
        this.registerSilkTouch(Blocks.DEAD_BUBBLE_CORAL);
        this.registerSilkTouch(Blocks.DEAD_FIRE_CORAL);
        this.registerSilkTouch(Blocks.DEAD_HORN_CORAL);
        this.registerSilkTouch(Blocks.TUBE_CORAL);
        this.registerSilkTouch(Blocks.BRAIN_CORAL);
        this.registerSilkTouch(Blocks.BUBBLE_CORAL);
        this.registerSilkTouch(Blocks.FIRE_CORAL);
        this.registerSilkTouch(Blocks.HORN_CORAL);
        this.registerSilkTouch(Blocks.DEAD_TUBE_CORAL_FAN);
        this.registerSilkTouch(Blocks.DEAD_BRAIN_CORAL_FAN);
        this.registerSilkTouch(Blocks.DEAD_BUBBLE_CORAL_FAN);
        this.registerSilkTouch(Blocks.DEAD_FIRE_CORAL_FAN);
        this.registerSilkTouch(Blocks.DEAD_HORN_CORAL_FAN);
        this.registerSilkTouch(Blocks.TUBE_CORAL_FAN);
        this.registerSilkTouch(Blocks.BRAIN_CORAL_FAN);
        this.registerSilkTouch(Blocks.BUBBLE_CORAL_FAN);
        this.registerSilkTouch(Blocks.FIRE_CORAL_FAN);
        this.registerSilkTouch(Blocks.HORN_CORAL_FAN);
        this.registerSilkTouch(Blocks.INFESTED_STONE, Blocks.STONE);
        this.registerSilkTouch(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE);
        this.registerSilkTouch(Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICKS);
        this.registerSilkTouch(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
        this.registerSilkTouch(Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
        this.registerSilkTouch(Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
        this.droppingNetherVines(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT);
        this.droppingNetherVines(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT);
        this.registerLootTable(Blocks.CAKE, blockNoDrop());
        this.registerLootTable(Blocks.FROSTED_ICE, blockNoDrop());
        this.registerLootTable(Blocks.SPAWNER, blockNoDrop());
        this.registerLootTable(Blocks.FIRE, blockNoDrop());
        this.registerLootTable(Blocks.SOUL_FIRE, blockNoDrop());
        this.registerLootTable(Blocks.NETHER_PORTAL, blockNoDrop());
        Set<ResourceLocation> set = Sets.newHashSet();

        for (Block block : Registry.BLOCK)
        {
            ResourceLocation resourcelocation = block.getLootTable();

            if (resourcelocation != LootTables.EMPTY && set.add(resourcelocation))
            {
                LootTable.Builder loottable$builder = this.lootTables.remove(resourcelocation);

                if (loottable$builder == null)
                {
                    throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", resourcelocation, Registry.BLOCK.getKey(block)));
                }

                p_accept_1_.accept(resourcelocation, loottable$builder);
            }
        }

        if (!this.lootTables.isEmpty())
        {
            throw new IllegalStateException("Created block loot tables for non-blocks: " + this.lootTables.keySet());
        }
    }

    private void droppingNetherVines(Block vines, Block plant)
    {
        LootTable.Builder loottable$builder = droppingWithSilkTouchOrShears(vines, ItemLootEntry.builder(vines).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.33F, 0.55F, 0.77F, 1.0F)));
        this.registerLootTable(vines, loottable$builder);
        this.registerLootTable(plant, loottable$builder);
    }

    public static LootTable.Builder registerDoor(Block door)
    {
        return droppingWhen(door, DoorBlock.HALF, DoubleBlockHalf.LOWER);
    }

    public void registerFlowerPot(Block flowerPot)
    {
        this.registerLootTable(flowerPot, (pot) ->
        {
            return droppingAndFlowerPot(((FlowerPotBlock)pot).getFlower());
        });
    }

    public void registerSilkTouch(Block blockIn, Block silkTouchDrop)
    {
        this.registerLootTable(blockIn, onlyWithSilkTouch(silkTouchDrop));
    }

    public void registerDropping(Block blockIn, IItemProvider drop)
    {
        this.registerLootTable(blockIn, dropping(drop));
    }

    public void registerSilkTouch(Block blockIn)
    {
        this.registerSilkTouch(blockIn, blockIn);
    }

    public void registerDropSelfLootTable(Block block)
    {
        this.registerDropping(block, block);
    }

    private void registerLootTable(Block blockIn, Function<Block, LootTable.Builder> factory)
    {
        this.registerLootTable(blockIn, factory.apply(blockIn));
    }

    private void registerLootTable(Block blockIn, LootTable.Builder table)
    {
        this.lootTables.put(blockIn.getLootTable(), table);
    }
}
