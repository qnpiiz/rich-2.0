package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureSpreadConfig;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

public abstract class Placement<DC extends IPlacementConfig>
{
    public static final Placement<NoPlacementConfig> NOPE = register("nope", new Passthrough(NoPlacementConfig.field_236555_a_));
    public static final Placement<ChanceConfig> field_242898_b = register("chance", new ChancePlacement(ChanceConfig.field_236950_a_));
    public static final Placement<FeatureSpreadConfig> field_242899_c = register("count", new CountPlacement(FeatureSpreadConfig.field_242797_a));
    public static final Placement<NoiseDependant> field_242900_d = register("count_noise", new CountNoisePlacement(NoiseDependant.field_236550_a_));
    public static final Placement<TopSolidWithNoiseConfig> field_242901_e = register("count_noise_biased", new CountNoiseBiasedPlacement(TopSolidWithNoiseConfig.field_236978_a_));
    public static final Placement<AtSurfaceWithExtraConfig> field_242902_f = register("count_extra", new CountExtraPlacement(AtSurfaceWithExtraConfig.field_236973_a_));
    public static final Placement<NoPlacementConfig> field_242903_g = register("square", new SquarePlacement(NoPlacementConfig.field_236555_a_));
    public static final Placement<NoPlacementConfig> field_242904_h = register("heightmap", new HeightmapPlacement<>(NoPlacementConfig.field_236555_a_));
    public static final Placement<NoPlacementConfig> field_242905_i = register("heightmap_spread_double", new HeightmapSpreadDoublePlacement<>(NoPlacementConfig.field_236555_a_));
    public static final Placement<NoPlacementConfig> TOP_SOLID_HEIGHTMAP = register("top_solid_heightmap", new TopSolidOnce(NoPlacementConfig.field_236555_a_));
    public static final Placement<NoPlacementConfig> field_242906_k = register("heightmap_world_surface", new HeightmapWorldSurfacePlacement(NoPlacementConfig.field_236555_a_));
    public static final Placement<TopSolidRangeConfig> field_242907_l = register("range", new RangePlacement(TopSolidRangeConfig.field_236985_a_));
    public static final Placement<TopSolidRangeConfig> field_242908_m = register("range_biased", new RangeBiasedPlacement(TopSolidRangeConfig.field_236985_a_));
    public static final Placement<TopSolidRangeConfig> field_242909_n = register("range_very_biased", new RangeVeryBiasedPlacement(TopSolidRangeConfig.field_236985_a_));
    public static final Placement<DepthAverageConfig> field_242910_o = register("depth_average", new DepthAveragePlacement(DepthAverageConfig.field_236955_a_));
    public static final Placement<NoPlacementConfig> field_242911_p = register("spread_32_above", new Spread32AbovePlacement(NoPlacementConfig.field_236555_a_));
    public static final Placement<CaveEdgeConfig> CARVING_MASK = register("carving_mask", new CaveEdge(CaveEdgeConfig.field_236946_a_));
    public static final Placement<FeatureSpreadConfig> FIRE = register("fire", new FirePlacement(FeatureSpreadConfig.field_242797_a));
    public static final Placement<NoPlacementConfig> MAGMA = register("magma", new NetherMagma(NoPlacementConfig.field_236555_a_));
    public static final Placement<NoPlacementConfig> EMERALD_ORE = register("emerald_ore", new Height4To32(NoPlacementConfig.field_236555_a_));
    public static final Placement<ChanceConfig> LAVA_LAKE = register("lava_lake", new LakeLava(ChanceConfig.field_236950_a_));
    public static final Placement<ChanceConfig> WATER_LAKE = register("water_lake", new LakeWater(ChanceConfig.field_236950_a_));
    public static final Placement<FeatureSpreadConfig> field_242912_w = register("glowstone", new GlowstonePlacement(FeatureSpreadConfig.field_242797_a));
    public static final Placement<NoPlacementConfig> END_GATEWAY = register("end_gateway", new EndGateway(NoPlacementConfig.field_236555_a_));
    public static final Placement<NoPlacementConfig> DARK_OAK_TREE = register("dark_oak_tree", new DarkOakTreePlacement(NoPlacementConfig.field_236555_a_));
    public static final Placement<NoPlacementConfig> ICEBERG = register("iceberg", new IcebergPlacement(NoPlacementConfig.field_236555_a_));
    public static final Placement<NoPlacementConfig> END_ISLAND = register("end_island", new EndIsland(NoPlacementConfig.field_236555_a_));
    public static final Placement<DecoratedPlacementConfig> field_242896_B = register("decorated", new DecoratedPlacement(DecoratedPlacementConfig.field_242883_a));
    public static final Placement<FeatureSpreadConfig> field_242897_C = register("count_multilayer", new CountMultilayerPlacement(FeatureSpreadConfig.field_242797_a));
    private final Codec<ConfiguredPlacement<DC>> codec;

    private static <T extends IPlacementConfig, G extends Placement<T>> G register(String key, G placement)
    {
        return Registry.register(Registry.DECORATOR, key, placement);
    }

    public Placement(Codec<DC> codec)
    {
        this.codec = codec.fieldOf("config").xmap((placementConfig) ->
        {
            return new ConfiguredPlacement<DC>(this, placementConfig);
        }, ConfiguredPlacement::func_242877_b).codec();
    }

    public ConfiguredPlacement<DC> configure(DC config)
    {
        return new ConfiguredPlacement<>(this, config);
    }

    public Codec<ConfiguredPlacement<DC>> getCodec()
    {
        return this.codec;
    }

    public abstract Stream<BlockPos> func_241857_a(WorldDecoratingHelper p_241857_1_, Random p_241857_2_, DC p_241857_3_, BlockPos p_241857_4_);

    public String toString()
    {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
    }
}
