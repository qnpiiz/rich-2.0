package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.IDecoratable;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature<FC extends IFeatureConfig, F extends Feature<FC>> implements IDecoratable < ConfiguredFeature <? , ? >>
{
    public static final Codec < ConfiguredFeature <? , ? >> field_242763_a = Registry.FEATURE.dispatch((p_236266_0_) ->
    {
        return p_236266_0_.feature;
    }, Feature::getCodec);
    public static final Codec < Supplier < ConfiguredFeature <? , ? >>> field_236264_b_ = RegistryKeyCodec.create(Registry.CONFIGURED_FEATURE_KEY, field_242763_a);
    public static final Codec < List < Supplier < ConfiguredFeature <? , ? >>> > field_242764_c = RegistryKeyCodec.getValueCodecs(Registry.CONFIGURED_FEATURE_KEY, field_242763_a);
    public static final Logger LOGGER = LogManager.getLogger();
    public final F feature;
    public final FC config;

    public ConfiguredFeature(F featureIn, FC configIn)
    {
        this.feature = featureIn;
        this.config = configIn;
    }

    public F func_242766_b()
    {
        return this.feature;
    }

    public FC func_242767_c()
    {
        return this.config;
    }

    public ConfiguredFeature <? , ? > withPlacement(ConfiguredPlacement<?> p_227228_1_)
    {
        return Feature.DECORATED.withConfiguration(new DecoratedFeatureConfig(() ->
        {
            return this;
        }, p_227228_1_));
    }

    public ConfiguredRandomFeatureList withChance(float p_227227_1_)
    {
        return new ConfiguredRandomFeatureList(this, p_227227_1_);
    }

    public boolean func_242765_a(ISeedReader p_242765_1_, ChunkGenerator p_242765_2_, Random p_242765_3_, BlockPos p_242765_4_)
    {
        return this.feature.func_241855_a(p_242765_1_, p_242765_2_, p_242765_3_, p_242765_4_, this.config);
    }

    public Stream < ConfiguredFeature <? , ? >> func_242768_d()
    {
        return Stream.concat(Stream.of(this), this.config.func_241856_an_());
    }
}
