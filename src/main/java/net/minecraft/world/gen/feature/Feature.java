package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.feature.structure.BasaltDeltasStructure;
import net.minecraft.world.gen.feature.structure.NetherackBlobReplacementStructure;

public abstract class Feature<FC extends IFeatureConfig>
{
    public static final Feature<NoFeatureConfig> NO_OP = register("no_op", new NoOpFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<BaseTreeFeatureConfig> TREE = register("tree", new TreeFeature(BaseTreeFeatureConfig.CODEC));
    public static final FlowersFeature<BlockClusterFeatureConfig> FLOWER = register("flower", new DefaultFlowersFeature(BlockClusterFeatureConfig.field_236587_a_));
    public static final FlowersFeature<BlockClusterFeatureConfig> NO_BONEMEAL_FLOWER = register("no_bonemeal_flower", new DefaultFlowersFeature(BlockClusterFeatureConfig.field_236587_a_));
    public static final Feature<BlockClusterFeatureConfig> RANDOM_PATCH = register("random_patch", new RandomPatchFeature(BlockClusterFeatureConfig.field_236587_a_));
    public static final Feature<BlockStateProvidingFeatureConfig> BLOCK_PILE = register("block_pile", new BlockPileFeature(BlockStateProvidingFeatureConfig.field_236453_a_));
    public static final Feature<LiquidsConfig> SPRING_FEATURE = register("spring_feature", new SpringFeature(LiquidsConfig.field_236649_a_));
    public static final Feature<NoFeatureConfig> CHORUS_PLANT = register("chorus_plant", new ChorusPlantFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<ReplaceBlockConfig> EMERALD_ORE = register("emerald_ore", new ReplaceBlockFeature(ReplaceBlockConfig.field_236604_a_));
    public static final Feature<NoFeatureConfig> VOID_START_PLATFORM = register("void_start_platform", new VoidStartPlatformFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<NoFeatureConfig> DESERT_WELL = register("desert_well", new DesertWellsFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<NoFeatureConfig> FOSSIL = register("fossil", new FossilsFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<BigMushroomFeatureConfig> HUGE_RED_MUSHROOM = register("huge_red_mushroom", new BigRedMushroomFeature(BigMushroomFeatureConfig.field_236528_a_));
    public static final Feature<BigMushroomFeatureConfig> HUGE_BROWN_MUSHROOM = register("huge_brown_mushroom", new BigBrownMushroomFeature(BigMushroomFeatureConfig.field_236528_a_));
    public static final Feature<NoFeatureConfig> ICE_SPIKE = register("ice_spike", new IceSpikeFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<NoFeatureConfig> GLOWSTONE_BLOB = register("glowstone_blob", new GlowstoneBlobFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<NoFeatureConfig> FREEZE_TOP_LAYER = register("freeze_top_layer", new IceAndSnowFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<NoFeatureConfig> VINES = register("vines", new VinesFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<NoFeatureConfig> MONSTER_ROOM = register("monster_room", new DungeonsFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<NoFeatureConfig> BLUE_ICE = register("blue_ice", new BlueIceFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<BlockStateFeatureConfig> ICEBERG = register("iceberg", new IcebergFeature(BlockStateFeatureConfig.field_236455_a_));
    public static final Feature<BlockStateFeatureConfig> FOREST_ROCK = register("forest_rock", new BlockBlobFeature(BlockStateFeatureConfig.field_236455_a_));
    public static final Feature<SphereReplaceConfig> DISK = register("disk", new SphereReplaceFeature(SphereReplaceConfig.field_236516_a_));
    public static final Feature<SphereReplaceConfig> ICE_PATCH = register("ice_patch", new IcePathFeature(SphereReplaceConfig.field_236516_a_));
    public static final Feature<BlockStateFeatureConfig> LAKE = register("lake", new LakesFeature(BlockStateFeatureConfig.field_236455_a_));
    public static final Feature<OreFeatureConfig> ORE = register("ore", new OreFeature(OreFeatureConfig.field_236566_a_));
    public static final Feature<EndSpikeFeatureConfig> END_SPIKE = register("end_spike", new EndSpikeFeature(EndSpikeFeatureConfig.field_236644_a_));
    public static final Feature<NoFeatureConfig> END_ISLAND = register("end_island", new EndIslandFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<EndGatewayConfig> END_GATEWAY = register("end_gateway", new EndGatewayFeature(EndGatewayConfig.field_236522_a_));
    public static final SeaGrassFeature SEAGRASS = register("seagrass", new SeaGrassFeature(ProbabilityConfig.field_236576_b_));
    public static final Feature<NoFeatureConfig> KELP = register("kelp", new KelpFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<NoFeatureConfig> CORAL_TREE = register("coral_tree", new CoralTreeFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<NoFeatureConfig> CORAL_MUSHROOM = register("coral_mushroom", new CoralMushroomFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<NoFeatureConfig> CORAL_CLAW = register("coral_claw", new CoralClawFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<FeatureSpreadConfig> SEA_PICKLE = register("sea_pickle", new SeaPickleFeature(FeatureSpreadConfig.field_242797_a));
    public static final Feature<BlockWithContextConfig> SIMPLE_BLOCK = register("simple_block", new BlockWithContextFeature(BlockWithContextConfig.field_236636_a_));
    public static final Feature<ProbabilityConfig> BAMBOO = register("bamboo", new BambooFeature(ProbabilityConfig.field_236576_b_));
    public static final Feature<HugeFungusConfig> HUGE_FUNGUS = register("huge_fungus", new HugeFungusFeature(HugeFungusConfig.field_236298_a_));
    public static final Feature<BlockStateProvidingFeatureConfig> NETHER_FOREST_VEGETATION = register("nether_forest_vegetation", new NetherVegetationFeature(BlockStateProvidingFeatureConfig.field_236453_a_));
    public static final Feature<NoFeatureConfig> WEEPING_VINES = register("weeping_vines", new WeepingVineFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<NoFeatureConfig> TWISTING_VINES = register("twisting_vines", new TwistingVineFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<ColumnConfig> BASALT_COLUMNS = register("basalt_columns", new BasaltColumnFeature(ColumnConfig.CODEC));
    public static final Feature<BasaltDeltasFeature> DELTA_FEATURE = register("delta_feature", new BasaltDeltasStructure(BasaltDeltasFeature.field_236495_a_));
    public static final Feature<BlobReplacementConfig> NETHERRACK_REPLACE_BLOBS = register("netherrack_replace_blobs", new NetherackBlobReplacementStructure(BlobReplacementConfig.field_242817_a));
    public static final Feature<FillLayerConfig> FILL_LAYER = register("fill_layer", new FillLayerFeature(FillLayerConfig.field_236537_a_));
    public static final BonusChestFeature BONUS_CHEST = register("bonus_chest", new BonusChestFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<NoFeatureConfig> BASALT_PILLAR = register("basalt_pillar", new BasaltPillarFeature(NoFeatureConfig.field_236558_a_));
    public static final Feature<OreFeatureConfig> NO_SURFACE_ORE = register("no_surface_ore", new NoExposedOreFeature(OreFeatureConfig.field_236566_a_));
    public static final Feature<MultipleRandomFeatureConfig> RANDOM_SELECTOR = register("random_selector", new MultipleWithChanceRandomFeature(MultipleRandomFeatureConfig.field_236583_a_));
    public static final Feature<SingleRandomFeature> SIMPLE_RANDOM_SELECTOR = register("simple_random_selector", new SingleRandomFeatureConfig(SingleRandomFeature.field_236642_a_));
    public static final Feature<TwoFeatureChoiceConfig> RANDOM_BOOLEAN_SELECTOR = register("random_boolean_selector", new TwoFeatureChoiceFeature(TwoFeatureChoiceConfig.field_236579_a_));
    public static final Feature<DecoratedFeatureConfig> DECORATED = register("decorated", new DecoratedFeature(DecoratedFeatureConfig.field_236491_a_));
    private final Codec<ConfiguredFeature<FC, Feature<FC>>> codec;

    private static <C extends IFeatureConfig, F extends Feature<C>> F register(String key, F value)
    {
        return Registry.register(Registry.FEATURE, key, value);
    }

    public Feature(Codec<FC> codec)
    {
        this.codec = codec.fieldOf("config").xmap((config) ->
        {
            return new ConfiguredFeature<>(this, config);
        }, (configured) ->
        {
            return configured.config;
        }).codec();
    }

    public Codec<ConfiguredFeature<FC, Feature<FC>>> getCodec()
    {
        return this.codec;
    }

    public ConfiguredFeature < FC, ? > withConfiguration(FC config)
    {
        return new ConfiguredFeature<>(this, config);
    }

    protected void setBlockState(IWorldWriter world, BlockPos pos, BlockState state)
    {
        world.setBlockState(pos, state, 3);
    }

    public abstract boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, FC p_241855_5_);

    protected static boolean isStone(Block blockIn)
    {
        return blockIn == Blocks.STONE || blockIn == Blocks.GRANITE || blockIn == Blocks.DIORITE || blockIn == Blocks.ANDESITE;
    }

    public static boolean isDirt(Block blockIn)
    {
        return blockIn == Blocks.DIRT || blockIn == Blocks.GRASS_BLOCK || blockIn == Blocks.PODZOL || blockIn == Blocks.COARSE_DIRT || blockIn == Blocks.MYCELIUM;
    }

    public static boolean isDirtAt(IWorldGenerationBaseReader world, BlockPos pos)
    {
        return world.hasBlockState(pos, (state) ->
        {
            return isDirt(state.getBlock());
        });
    }

    public static boolean isAirAt(IWorldGenerationBaseReader world, BlockPos pos)
    {
        return world.hasBlockState(pos, AbstractBlock.AbstractBlockState::isAir);
    }
}
