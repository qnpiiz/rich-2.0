package net.minecraft.world.gen.trunkplacer;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class TrunkPlacerType<P extends AbstractTrunkPlacer>
{
    public static final TrunkPlacerType<StraightTrunkPlacer> STRAIGHT_TRUNK_PLACER = register("straight_trunk_placer", StraightTrunkPlacer.field_236903_a_);
    public static final TrunkPlacerType<ForkyTrunkPlacer> FORKING_TRUNK_PLACER = register("forking_trunk_placer", ForkyTrunkPlacer.field_236896_a_);
    public static final TrunkPlacerType<GiantTrunkPlacer> GIANT_TRUNK_PLACER = register("giant_trunk_placer", GiantTrunkPlacer.field_236898_a_);
    public static final TrunkPlacerType<MegaJungleTrunkPlacer> MEGA_TRUNK_PLACER = register("mega_jungle_trunk_placer", MegaJungleTrunkPlacer.field_236901_b_);
    public static final TrunkPlacerType<DarkOakTrunkPlacer> DARK_OAK_TRUNK_PLACER = register("dark_oak_trunk_placer", DarkOakTrunkPlacer.field_236882_a_);
    public static final TrunkPlacerType<FancyTrunkPlacer> FANCY_TRUNK_PLACER = register("fancy_trunk_placer", FancyTrunkPlacer.field_236884_a_);
    private final Codec<P> field_236926_g_;

    private static <P extends AbstractTrunkPlacer> TrunkPlacerType<P> register(String p_236928_0_, Codec<P> p_236928_1_)
    {
        return Registry.register(Registry.TRUNK_REPLACER, p_236928_0_, new TrunkPlacerType<>(p_236928_1_));
    }

    private TrunkPlacerType(Codec<P> p_i232061_1_)
    {
        this.field_236926_g_ = p_i232061_1_;
    }

    public Codec<P> func_236927_a_()
    {
        return this.field_236926_g_;
    }
}
