package net.minecraft.world.gen.surfacebuilders;

import net.minecraft.block.Blocks;
import net.minecraft.util.registry.WorldGenRegistries;

public class ConfiguredSurfaceBuilders
{
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244169_a = func_244192_a("badlands", SurfaceBuilder.BADLANDS.func_242929_a(SurfaceBuilder.RED_SAND_WHITE_TERRACOTTA_GRAVEL_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244170_b = func_244192_a("basalt_deltas", SurfaceBuilder.field_237191_af_.func_242929_a(SurfaceBuilder.field_237187_R_));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244171_c = func_244192_a("crimson_forest", SurfaceBuilder.field_237189_ad_.func_242929_a(SurfaceBuilder.field_237185_P_));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244172_d = func_244192_a("desert", SurfaceBuilder.DEFAULT.func_242929_a(SurfaceBuilder.SAND_SAND_GRAVEL_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244173_e = func_244192_a("end", SurfaceBuilder.DEFAULT.func_242929_a(SurfaceBuilder.END_STONE_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244174_f = func_244192_a("eroded_badlands", SurfaceBuilder.ERODED_BADLANDS.func_242929_a(SurfaceBuilder.RED_SAND_WHITE_TERRACOTTA_GRAVEL_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244175_g = func_244192_a("frozen_ocean", SurfaceBuilder.FROZEN_OCEAN.func_242929_a(SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244176_h = func_244192_a("full_sand", SurfaceBuilder.DEFAULT.func_242929_a(SurfaceBuilder.SAND_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244177_i = func_244192_a("giant_tree_taiga", SurfaceBuilder.GIANT_TREE_TAIGA.func_242929_a(SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244178_j = func_244192_a("grass", SurfaceBuilder.DEFAULT.func_242929_a(SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244179_k = func_244192_a("gravelly_mountain", SurfaceBuilder.GRAVELLY_MOUNTAIN.func_242929_a(SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244180_l = func_244192_a("ice_spikes", SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(Blocks.SNOW_BLOCK.getDefaultState(), Blocks.DIRT.getDefaultState(), Blocks.GRAVEL.getDefaultState())));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244181_m = func_244192_a("mountain", SurfaceBuilder.MOUNTAIN.func_242929_a(SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244182_n = func_244192_a("mycelium", SurfaceBuilder.DEFAULT.func_242929_a(SurfaceBuilder.MYCELIUM_DIRT_GRAVEL_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244183_o = func_244192_a("nether", SurfaceBuilder.NETHER.func_242929_a(SurfaceBuilder.NETHERRACK_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244184_p = func_244192_a("nope", SurfaceBuilder.NOPE.func_242929_a(SurfaceBuilder.STONE_STONE_GRAVEL_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244185_q = func_244192_a("ocean_sand", SurfaceBuilder.DEFAULT.func_242929_a(SurfaceBuilder.GRASS_DIRT_SAND_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244186_r = func_244192_a("shattered_savanna", SurfaceBuilder.SHATTERED_SAVANNA.func_242929_a(SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244187_s = func_244192_a("soul_sand_valley", SurfaceBuilder.field_237190_ae_.func_242929_a(SurfaceBuilder.field_237184_N_));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244188_t = func_244192_a("stone", SurfaceBuilder.DEFAULT.func_242929_a(SurfaceBuilder.STONE_STONE_GRAVEL_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244189_u = func_244192_a("swamp", SurfaceBuilder.SWAMP.func_242929_a(SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244190_v = func_244192_a("warped_forest", SurfaceBuilder.field_237189_ad_.func_242929_a(SurfaceBuilder.field_237186_Q_));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> field_244191_w = func_244192_a("wooded_badlands", SurfaceBuilder.WOODED_BADLANDS.func_242929_a(SurfaceBuilder.RED_SAND_WHITE_TERRACOTTA_GRAVEL_CONFIG));

    private static <SC extends ISurfaceBuilderConfig> ConfiguredSurfaceBuilder<SC> func_244192_a(String p_244192_0_, ConfiguredSurfaceBuilder<SC> p_244192_1_)
    {
        return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_SURFACE_BUILDER, p_244192_0_, p_244192_1_);
    }
}
