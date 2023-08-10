package net.minecraft.world.gen.foliageplacer;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class FoliagePlacerType<P extends FoliagePlacer>
{
    public static final FoliagePlacerType<BlobFoliagePlacer> BLOB = func_236773_a_("blob_foliage_placer", BlobFoliagePlacer.field_236738_a_);
    public static final FoliagePlacerType<SpruceFoliagePlacer> SPRUCE = func_236773_a_("spruce_foliage_placer", SpruceFoliagePlacer.field_236790_a_);
    public static final FoliagePlacerType<PineFoliagePlacer> PINE = func_236773_a_("pine_foliage_placer", PineFoliagePlacer.field_236784_a_);
    public static final FoliagePlacerType<AcaciaFoliagePlacer> ACACIA = func_236773_a_("acacia_foliage_placer", AcaciaFoliagePlacer.field_236736_a_);
    public static final FoliagePlacerType<BushFoliagePlacer> field_236766_e_ = func_236773_a_("bush_foliage_placer", BushFoliagePlacer.field_236743_c_);
    public static final FoliagePlacerType<FancyFoliagePlacer> field_236767_f_ = func_236773_a_("fancy_foliage_placer", FancyFoliagePlacer.field_236747_c_);
    public static final FoliagePlacerType<JungleFoliagePlacer> field_236768_g_ = func_236773_a_("jungle_foliage_placer", JungleFoliagePlacer.field_236774_a_);
    public static final FoliagePlacerType<MegaPineFoliagePlacer> field_236769_h_ = func_236773_a_("mega_pine_foliage_placer", MegaPineFoliagePlacer.field_236778_a_);
    public static final FoliagePlacerType<DarkOakFoliagePlacer> field_236770_i_ = func_236773_a_("dark_oak_foliage_placer", DarkOakFoliagePlacer.field_236745_a_);
    private final Codec<P> field_236771_j_;

    private static <P extends FoliagePlacer> FoliagePlacerType<P> func_236773_a_(String p_236773_0_, Codec<P> p_236773_1_)
    {
        return Registry.register(Registry.FOLIAGE_PLACER_TYPE, p_236773_0_, new FoliagePlacerType<>(p_236773_1_));
    }

    private FoliagePlacerType(Codec<P> p_i232036_1_)
    {
        this.field_236771_j_ = p_i232036_1_;
    }

    public Codec<P> func_236772_a_()
    {
        return this.field_236771_j_;
    }
}
