package net.minecraft.world.gen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class ConfiguredSurfaceBuilder<SC extends ISurfaceBuilderConfig>
{
    public static final Codec < ConfiguredSurfaceBuilder<? >> field_237168_a_ = Registry.SURFACE_BUILDER.dispatch((p_237169_0_) ->
    {
        return p_237169_0_.builder;
    }, SurfaceBuilder::func_237202_d_);
    public static final Codec < Supplier < ConfiguredSurfaceBuilder<? >>> field_244393_b_ = RegistryKeyCodec.create(Registry.CONFIGURED_SURFACE_BUILDER_KEY, field_237168_a_);
    public final SurfaceBuilder<SC> builder;
    public final SC config;

    public ConfiguredSurfaceBuilder(SurfaceBuilder<SC> builder, SC config)
    {
        this.builder = builder;
        this.config = config;
    }

    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed)
    {
        this.builder.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, this.config);
    }

    public void setSeed(long seed)
    {
        this.builder.setSeed(seed);
    }

    public SC getConfig()
    {
        return this.config;
    }
}
