package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DebugChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;

public abstract class BiomeGeneratorTypeScreens
{
    public static final BiomeGeneratorTypeScreens field_239066_a_ = new BiomeGeneratorTypeScreens("default")
    {
        protected ChunkGenerator func_241869_a(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_)
        {
            return new NoiseChunkGenerator(new OverworldBiomeProvider(p_241869_3_, false, false, p_241869_1_), p_241869_3_, () ->
            {
                return p_241869_2_.getOrThrow(DimensionSettings.field_242734_c);
            });
        }
    };
    private static final BiomeGeneratorTypeScreens field_239070_e_ = new BiomeGeneratorTypeScreens("flat")
    {
        protected ChunkGenerator func_241869_a(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_)
        {
            return new FlatChunkGenerator(FlatGenerationSettings.func_242869_a(p_241869_1_));
        }
    };
    private static final BiomeGeneratorTypeScreens field_239071_f_ = new BiomeGeneratorTypeScreens("large_biomes")
    {
        protected ChunkGenerator func_241869_a(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_)
        {
            return new NoiseChunkGenerator(new OverworldBiomeProvider(p_241869_3_, false, true, p_241869_1_), p_241869_3_, () ->
            {
                return p_241869_2_.getOrThrow(DimensionSettings.field_242734_c);
            });
        }
    };
    public static final BiomeGeneratorTypeScreens field_239067_b_ = new BiomeGeneratorTypeScreens("amplified")
    {
        protected ChunkGenerator func_241869_a(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_)
        {
            return new NoiseChunkGenerator(new OverworldBiomeProvider(p_241869_3_, false, false, p_241869_1_), p_241869_3_, () ->
            {
                return p_241869_2_.getOrThrow(DimensionSettings.field_242735_d);
            });
        }
    };
    private static final BiomeGeneratorTypeScreens field_239072_g_ = new BiomeGeneratorTypeScreens("single_biome_surface")
    {
        protected ChunkGenerator func_241869_a(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_)
        {
            return new NoiseChunkGenerator(new SingleBiomeProvider(p_241869_1_.getOrThrow(Biomes.PLAINS)), p_241869_3_, () ->
            {
                return p_241869_2_.getOrThrow(DimensionSettings.field_242734_c);
            });
        }
    };
    private static final BiomeGeneratorTypeScreens field_239073_h_ = new BiomeGeneratorTypeScreens("single_biome_caves")
    {
        public DimensionGeneratorSettings func_241220_a_(DynamicRegistries.Impl p_241220_1_, long p_241220_2_, boolean p_241220_4_, boolean p_241220_5_)
        {
            Registry<Biome> registry = p_241220_1_.getRegistry(Registry.BIOME_KEY);
            Registry<DimensionType> registry1 = p_241220_1_.getRegistry(Registry.DIMENSION_TYPE_KEY);
            Registry<DimensionSettings> registry2 = p_241220_1_.getRegistry(Registry.NOISE_SETTINGS_KEY);
            return new DimensionGeneratorSettings(p_241220_2_, p_241220_4_, p_241220_5_, DimensionGeneratorSettings.func_241520_a_(DimensionType.getDefaultSimpleRegistry(registry1, registry, registry2, p_241220_2_), () ->
            {
                return registry1.getOrThrow(DimensionType.OVERWORLD_CAVES);
            }, this.func_241869_a(registry, registry2, p_241220_2_)));
        }
        protected ChunkGenerator func_241869_a(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_)
        {
            return new NoiseChunkGenerator(new SingleBiomeProvider(p_241869_1_.getOrThrow(Biomes.PLAINS)), p_241869_3_, () ->
            {
                return p_241869_2_.getOrThrow(DimensionSettings.field_242738_g);
            });
        }
    };
    private static final BiomeGeneratorTypeScreens field_239074_i_ = new BiomeGeneratorTypeScreens("single_biome_floating_islands")
    {
        protected ChunkGenerator func_241869_a(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_)
        {
            return new NoiseChunkGenerator(new SingleBiomeProvider(p_241869_1_.getOrThrow(Biomes.PLAINS)), p_241869_3_, () ->
            {
                return p_241869_2_.getOrThrow(DimensionSettings.field_242739_h);
            });
        }
    };
    private static final BiomeGeneratorTypeScreens field_239075_j_ = new BiomeGeneratorTypeScreens("debug_all_block_states")
    {
        protected ChunkGenerator func_241869_a(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_)
        {
            return new DebugChunkGenerator(p_241869_1_);
        }
    };
    protected static final List<BiomeGeneratorTypeScreens> field_239068_c_ = Lists.newArrayList(field_239066_a_, field_239070_e_, field_239071_f_, field_239067_b_, field_239072_g_, field_239073_h_, field_239074_i_, field_239075_j_);
    protected static final Map<Optional<BiomeGeneratorTypeScreens>, BiomeGeneratorTypeScreens.IFactory> field_239069_d_ = ImmutableMap.of(Optional.of(field_239070_e_), (p_239089_0_, p_239089_1_) ->
    {
        ChunkGenerator chunkgenerator = p_239089_1_.func_236225_f_();
        return new CreateFlatWorldScreen(p_239089_0_, (p_239083_2_) -> {
            p_239089_0_.field_238934_c_.func_239043_a_(new DimensionGeneratorSettings(p_239089_1_.getSeed(), p_239089_1_.doesGenerateFeatures(), p_239089_1_.hasBonusChest(), DimensionGeneratorSettings.func_242749_a(p_239089_0_.field_238934_c_.func_239055_b_().getRegistry(Registry.DIMENSION_TYPE_KEY), p_239089_1_.func_236224_e_(), new FlatChunkGenerator(p_239083_2_))));
        }, chunkgenerator instanceof FlatChunkGenerator ? ((FlatChunkGenerator)chunkgenerator).func_236073_g_() : FlatGenerationSettings.func_242869_a(p_239089_0_.field_238934_c_.func_239055_b_().getRegistry(Registry.BIOME_KEY)));
    }, Optional.of(field_239072_g_), (p_239087_0_, p_239087_1_) ->
    {
        return new CreateBuffetWorldScreen(p_239087_0_, p_239087_0_.field_238934_c_.func_239055_b_(), (p_239088_2_) -> {
            p_239087_0_.field_238934_c_.func_239043_a_(func_243452_a(p_239087_0_.field_238934_c_.func_239055_b_(), p_239087_1_, field_239072_g_, p_239088_2_));
        }, func_243451_a(p_239087_0_.field_238934_c_.func_239055_b_(), p_239087_1_));
    }, Optional.of(field_239073_h_), (p_239085_0_, p_239085_1_) ->
    {
        return new CreateBuffetWorldScreen(p_239085_0_, p_239085_0_.field_238934_c_.func_239055_b_(), (p_239086_2_) -> {
            p_239085_0_.field_238934_c_.func_239043_a_(func_243452_a(p_239085_0_.field_238934_c_.func_239055_b_(), p_239085_1_, field_239073_h_, p_239086_2_));
        }, func_243451_a(p_239085_0_.field_238934_c_.func_239055_b_(), p_239085_1_));
    }, Optional.of(field_239074_i_), (p_239081_0_, p_239081_1_) ->
    {
        return new CreateBuffetWorldScreen(p_239081_0_, p_239081_0_.field_238934_c_.func_239055_b_(), (p_239082_2_) -> {
            p_239081_0_.field_238934_c_.func_239043_a_(func_243452_a(p_239081_0_.field_238934_c_.func_239055_b_(), p_239081_1_, field_239074_i_, p_239082_2_));
        }, func_243451_a(p_239081_0_.field_238934_c_.func_239055_b_(), p_239081_1_));
    });
    private final ITextComponent field_239076_k_;

    private BiomeGeneratorTypeScreens(String p_i232324_1_)
    {
        this.field_239076_k_ = new TranslationTextComponent("generator." + p_i232324_1_);
    }

    private static DimensionGeneratorSettings func_243452_a(DynamicRegistries p_243452_0_, DimensionGeneratorSettings p_243452_1_, BiomeGeneratorTypeScreens p_243452_2_, Biome p_243452_3_)
    {
        BiomeProvider biomeprovider = new SingleBiomeProvider(p_243452_3_);
        Registry<DimensionType> registry = p_243452_0_.getRegistry(Registry.DIMENSION_TYPE_KEY);
        Registry<DimensionSettings> registry1 = p_243452_0_.getRegistry(Registry.NOISE_SETTINGS_KEY);
        Supplier<DimensionSettings> supplier;

        if (p_243452_2_ == field_239073_h_)
        {
            supplier = () ->
            {
                return registry1.getOrThrow(DimensionSettings.field_242738_g);
            };
        }
        else if (p_243452_2_ == field_239074_i_)
        {
            supplier = () ->
            {
                return registry1.getOrThrow(DimensionSettings.field_242739_h);
            };
        }
        else
        {
            supplier = () ->
            {
                return registry1.getOrThrow(DimensionSettings.field_242734_c);
            };
        }

        return new DimensionGeneratorSettings(p_243452_1_.getSeed(), p_243452_1_.doesGenerateFeatures(), p_243452_1_.hasBonusChest(), DimensionGeneratorSettings.func_242749_a(registry, p_243452_1_.func_236224_e_(), new NoiseChunkGenerator(biomeprovider, p_243452_1_.getSeed(), supplier)));
    }

    private static Biome func_243451_a(DynamicRegistries p_243451_0_, DimensionGeneratorSettings p_243451_1_)
    {
        return p_243451_1_.func_236225_f_().getBiomeProvider().getBiomes().stream().findFirst().orElse(p_243451_0_.getRegistry(Registry.BIOME_KEY).getOrThrow(Biomes.PLAINS));
    }

    public static Optional<BiomeGeneratorTypeScreens> func_239079_a_(DimensionGeneratorSettings p_239079_0_)
    {
        ChunkGenerator chunkgenerator = p_239079_0_.func_236225_f_();

        if (chunkgenerator instanceof FlatChunkGenerator)
        {
            return Optional.of(field_239070_e_);
        }
        else
        {
            return chunkgenerator instanceof DebugChunkGenerator ? Optional.of(field_239075_j_) : Optional.empty();
        }
    }

    public ITextComponent func_239077_a_()
    {
        return this.field_239076_k_;
    }

    public DimensionGeneratorSettings func_241220_a_(DynamicRegistries.Impl p_241220_1_, long p_241220_2_, boolean p_241220_4_, boolean p_241220_5_)
    {
        Registry<Biome> registry = p_241220_1_.getRegistry(Registry.BIOME_KEY);
        Registry<DimensionType> registry1 = p_241220_1_.getRegistry(Registry.DIMENSION_TYPE_KEY);
        Registry<DimensionSettings> registry2 = p_241220_1_.getRegistry(Registry.NOISE_SETTINGS_KEY);
        return new DimensionGeneratorSettings(p_241220_2_, p_241220_4_, p_241220_5_, DimensionGeneratorSettings.func_242749_a(registry1, DimensionType.getDefaultSimpleRegistry(registry1, registry, registry2, p_241220_2_), this.func_241869_a(registry, registry2, p_241220_2_)));
    }

    protected abstract ChunkGenerator func_241869_a(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_);

    public interface IFactory
    {
        Screen createEditScreen(CreateWorldScreen p_createEditScreen_1_, DimensionGeneratorSettings p_createEditScreen_2_);
    }
}
