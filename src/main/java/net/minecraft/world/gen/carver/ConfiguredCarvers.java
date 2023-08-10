package net.minecraft.world.gen.carver;

import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class ConfiguredCarvers
{
    public static final ConfiguredCarver<ProbabilityConfig> field_243767_a = func_243773_a("cave", WorldCarver.CAVE.func_242761_a(new ProbabilityConfig(0.14285715F)));
    public static final ConfiguredCarver<ProbabilityConfig> field_243768_b = func_243773_a("canyon", WorldCarver.CANYON.func_242761_a(new ProbabilityConfig(0.02F)));
    public static final ConfiguredCarver<ProbabilityConfig> field_243769_c = func_243773_a("ocean_cave", WorldCarver.CAVE.func_242761_a(new ProbabilityConfig(0.06666667F)));
    public static final ConfiguredCarver<ProbabilityConfig> field_243770_d = func_243773_a("underwater_canyon", WorldCarver.UNDERWATER_CANYON.func_242761_a(new ProbabilityConfig(0.02F)));
    public static final ConfiguredCarver<ProbabilityConfig> field_243771_e = func_243773_a("underwater_cave", WorldCarver.UNDERWATER_CAVE.func_242761_a(new ProbabilityConfig(0.06666667F)));
    public static final ConfiguredCarver<ProbabilityConfig> field_243772_f = func_243773_a("nether_cave", WorldCarver.field_236240_b_.func_242761_a(new ProbabilityConfig(0.2F)));

    private static <WC extends ICarverConfig> ConfiguredCarver<WC> func_243773_a(String p_243773_0_, ConfiguredCarver<WC> p_243773_1_)
    {
        return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_CARVER, p_243773_0_, p_243773_1_);
    }
}
