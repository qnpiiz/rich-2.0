package net.minecraft.world.gen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public abstract class SurfaceBuilder<C extends ISurfaceBuilderConfig>
{
    private static final BlockState DIRT = Blocks.DIRT.getDefaultState();
    private static final BlockState GRASS_BLOCK = Blocks.GRASS_BLOCK.getDefaultState();
    private static final BlockState PODZOL = Blocks.PODZOL.getDefaultState();
    private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    private static final BlockState STONE = Blocks.STONE.getDefaultState();
    private static final BlockState COARSE_DIRT = Blocks.COARSE_DIRT.getDefaultState();
    private static final BlockState SAND = Blocks.SAND.getDefaultState();
    private static final BlockState RED_SAND = Blocks.RED_SAND.getDefaultState();
    private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
    private static final BlockState MYCELIUM = Blocks.MYCELIUM.getDefaultState();
    private static final BlockState field_237192_q_ = Blocks.SOUL_SAND.getDefaultState();
    private static final BlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
    private static final BlockState END_STONE = Blocks.END_STONE.getDefaultState();
    private static final BlockState field_237193_t_ = Blocks.CRIMSON_NYLIUM.getDefaultState();
    private static final BlockState field_237194_u_ = Blocks.WARPED_NYLIUM.getDefaultState();
    private static final BlockState field_237195_v_ = Blocks.NETHER_WART_BLOCK.getDefaultState();
    private static final BlockState field_237196_w_ = Blocks.WARPED_WART_BLOCK.getDefaultState();
    private static final BlockState field_237197_x_ = Blocks.BLACKSTONE.getDefaultState();
    private static final BlockState field_237198_y_ = Blocks.BASALT.getDefaultState();
    private static final BlockState field_237199_z_ = Blocks.MAGMA_BLOCK.getDefaultState();
    public static final SurfaceBuilderConfig PODZOL_DIRT_GRAVEL_CONFIG = new SurfaceBuilderConfig(PODZOL, DIRT, GRAVEL);
    public static final SurfaceBuilderConfig GRAVEL_CONFIG = new SurfaceBuilderConfig(GRAVEL, GRAVEL, GRAVEL);
    public static final SurfaceBuilderConfig GRASS_DIRT_GRAVEL_CONFIG = new SurfaceBuilderConfig(GRASS_BLOCK, DIRT, GRAVEL);
    public static final SurfaceBuilderConfig STONE_STONE_GRAVEL_CONFIG = new SurfaceBuilderConfig(STONE, STONE, GRAVEL);
    public static final SurfaceBuilderConfig CORASE_DIRT_DIRT_GRAVEL_CONFIG = new SurfaceBuilderConfig(COARSE_DIRT, DIRT, GRAVEL);
    public static final SurfaceBuilderConfig SAND_SAND_GRAVEL_CONFIG = new SurfaceBuilderConfig(SAND, SAND, GRAVEL);
    public static final SurfaceBuilderConfig GRASS_DIRT_SAND_CONFIG = new SurfaceBuilderConfig(GRASS_BLOCK, DIRT, SAND);
    public static final SurfaceBuilderConfig SAND_CONFIG = new SurfaceBuilderConfig(SAND, SAND, SAND);
    public static final SurfaceBuilderConfig RED_SAND_WHITE_TERRACOTTA_GRAVEL_CONFIG = new SurfaceBuilderConfig(RED_SAND, WHITE_TERRACOTTA, GRAVEL);
    public static final SurfaceBuilderConfig MYCELIUM_DIRT_GRAVEL_CONFIG = new SurfaceBuilderConfig(MYCELIUM, DIRT, GRAVEL);
    public static final SurfaceBuilderConfig NETHERRACK_CONFIG = new SurfaceBuilderConfig(NETHERRACK, NETHERRACK, NETHERRACK);
    public static final SurfaceBuilderConfig field_237184_N_ = new SurfaceBuilderConfig(field_237192_q_, field_237192_q_, field_237192_q_);
    public static final SurfaceBuilderConfig END_STONE_CONFIG = new SurfaceBuilderConfig(END_STONE, END_STONE, END_STONE);
    public static final SurfaceBuilderConfig field_237185_P_ = new SurfaceBuilderConfig(field_237193_t_, NETHERRACK, field_237195_v_);
    public static final SurfaceBuilderConfig field_237186_Q_ = new SurfaceBuilderConfig(field_237194_u_, NETHERRACK, field_237196_w_);
    public static final SurfaceBuilderConfig field_237187_R_ = new SurfaceBuilderConfig(field_237197_x_, field_237198_y_, field_237199_z_);
    public static final SurfaceBuilder<SurfaceBuilderConfig> DEFAULT = register("default", new DefaultSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
    public static final SurfaceBuilder<SurfaceBuilderConfig> MOUNTAIN = register("mountain", new MountainSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
    public static final SurfaceBuilder<SurfaceBuilderConfig> SHATTERED_SAVANNA = register("shattered_savanna", new ShatteredSavannaSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
    public static final SurfaceBuilder<SurfaceBuilderConfig> GRAVELLY_MOUNTAIN = register("gravelly_mountain", new GravellyMountainSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
    public static final SurfaceBuilder<SurfaceBuilderConfig> GIANT_TREE_TAIGA = register("giant_tree_taiga", new GiantTreeTaigaSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
    public static final SurfaceBuilder<SurfaceBuilderConfig> SWAMP = register("swamp", new SwampSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
    public static final SurfaceBuilder<SurfaceBuilderConfig> BADLANDS = register("badlands", new BadlandsSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
    public static final SurfaceBuilder<SurfaceBuilderConfig> WOODED_BADLANDS = register("wooded_badlands", new WoodedBadlandsSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
    public static final SurfaceBuilder<SurfaceBuilderConfig> ERODED_BADLANDS = register("eroded_badlands", new ErodedBadlandsSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
    public static final SurfaceBuilder<SurfaceBuilderConfig> FROZEN_OCEAN = register("frozen_ocean", new FrozenOceanSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
    public static final SurfaceBuilder<SurfaceBuilderConfig> NETHER = register("nether", new NetherSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
    public static final SurfaceBuilder<SurfaceBuilderConfig> field_237189_ad_ = register("nether_forest", new NetherForestsSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
    public static final SurfaceBuilder<SurfaceBuilderConfig> field_237190_ae_ = register("soul_sand_valley", new SoulSandValleySurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
    public static final SurfaceBuilder<SurfaceBuilderConfig> field_237191_af_ = register("basalt_deltas", new BasaltDeltasSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
    public static final SurfaceBuilder<SurfaceBuilderConfig> NOPE = register("nope", new NoopSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
    private final Codec<ConfiguredSurfaceBuilder<C>> field_237188_a_;

    private static <C extends ISurfaceBuilderConfig, F extends SurfaceBuilder<C>> F register(String key, F builderIn)
    {
        return Registry.register(Registry.SURFACE_BUILDER, key, builderIn);
    }

    public SurfaceBuilder(Codec<C> p_i232136_1_)
    {
        this.field_237188_a_ = p_i232136_1_.fieldOf("config").xmap(this::func_242929_a, ConfiguredSurfaceBuilder::getConfig).codec();
    }

    public Codec<ConfiguredSurfaceBuilder<C>> func_237202_d_()
    {
        return this.field_237188_a_;
    }

    public ConfiguredSurfaceBuilder<C> func_242929_a(C p_242929_1_)
    {
        return new ConfiguredSurfaceBuilder<>(this, p_242929_1_);
    }

    public abstract void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, C config);

    public void setSeed(long seed)
    {
    }
}
