package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.trunkplacer.AbstractTrunkPlacer;

public class BaseTreeFeatureConfig implements IFeatureConfig
{
    public static final Codec<BaseTreeFeatureConfig> CODEC = RecordCodecBuilder.create((p_236683_0_) ->
    {
        return p_236683_0_.group(BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter((p_236693_0_) -> {
            return p_236693_0_.trunkProvider;
        }), BlockStateProvider.CODEC.fieldOf("leaves_provider").forGetter((p_236692_0_) -> {
            return p_236692_0_.leavesProvider;
        }), FoliagePlacer.field_236749_d_.fieldOf("foliage_placer").forGetter((p_236691_0_) -> {
            return p_236691_0_.field_236677_f_;
        }), AbstractTrunkPlacer.field_236905_c_.fieldOf("trunk_placer").forGetter((p_236690_0_) -> {
            return p_236690_0_.field_236678_g_;
        }), AbstractFeatureSizeType.field_236704_a_.fieldOf("minimum_size").forGetter((p_236689_0_) -> {
            return p_236689_0_.field_236679_h_;
        }), TreeDecorator.field_236874_c_.listOf().fieldOf("decorators").forGetter((p_236688_0_) -> {
            return p_236688_0_.decorators;
        }), Codec.INT.fieldOf("max_water_depth").orElse(0).forGetter((p_236687_0_) -> {
            return p_236687_0_.field_236680_i_;
        }), Codec.BOOL.fieldOf("ignore_vines").orElse(false).forGetter((p_236686_0_) -> {
            return p_236686_0_.field_236681_j_;
        }), Heightmap.Type.field_236078_g_.fieldOf("heightmap").forGetter((p_236684_0_) -> {
            return p_236684_0_.field_236682_l_;
        })).apply(p_236683_0_, BaseTreeFeatureConfig::new);
    });
    public final BlockStateProvider trunkProvider;
    public final BlockStateProvider leavesProvider;
    public final List<TreeDecorator> decorators;
    public transient boolean forcePlacement;
    public final FoliagePlacer field_236677_f_;
    public final AbstractTrunkPlacer field_236678_g_;
    public final AbstractFeatureSizeType field_236679_h_;
    public final int field_236680_i_;
    public final boolean field_236681_j_;
    public final Heightmap.Type field_236682_l_;

    protected BaseTreeFeatureConfig(BlockStateProvider p_i232020_1_, BlockStateProvider p_i232020_2_, FoliagePlacer p_i232020_3_, AbstractTrunkPlacer p_i232020_4_, AbstractFeatureSizeType p_i232020_5_, List<TreeDecorator> p_i232020_6_, int p_i232020_7_, boolean p_i232020_8_, Heightmap.Type p_i232020_9_)
    {
        this.trunkProvider = p_i232020_1_;
        this.leavesProvider = p_i232020_2_;
        this.decorators = p_i232020_6_;
        this.field_236677_f_ = p_i232020_3_;
        this.field_236679_h_ = p_i232020_5_;
        this.field_236678_g_ = p_i232020_4_;
        this.field_236680_i_ = p_i232020_7_;
        this.field_236681_j_ = p_i232020_8_;
        this.field_236682_l_ = p_i232020_9_;
    }

    public void forcePlacement()
    {
        this.forcePlacement = true;
    }

    public BaseTreeFeatureConfig func_236685_a_(List<TreeDecorator> p_236685_1_)
    {
        return new BaseTreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.field_236677_f_, this.field_236678_g_, this.field_236679_h_, p_236685_1_, this.field_236680_i_, this.field_236681_j_, this.field_236682_l_);
    }

    public static class Builder
    {
        public final BlockStateProvider trunkProvider;
        public final BlockStateProvider leavesProvider;
        private final FoliagePlacer field_236694_c_;
        private final AbstractTrunkPlacer field_236695_d_;
        private final AbstractFeatureSizeType field_236696_e_;
        private List<TreeDecorator> decorators = ImmutableList.of();
        private int field_236697_g_;
        private boolean field_236698_h_;
        private Heightmap.Type field_236699_i_ = Heightmap.Type.OCEAN_FLOOR;

        public Builder(BlockStateProvider p_i232021_1_, BlockStateProvider p_i232021_2_, FoliagePlacer p_i232021_3_, AbstractTrunkPlacer p_i232021_4_, AbstractFeatureSizeType p_i232021_5_)
        {
            this.trunkProvider = p_i232021_1_;
            this.leavesProvider = p_i232021_2_;
            this.field_236694_c_ = p_i232021_3_;
            this.field_236695_d_ = p_i232021_4_;
            this.field_236696_e_ = p_i232021_5_;
        }

        public BaseTreeFeatureConfig.Builder func_236703_a_(List<TreeDecorator> p_236703_1_)
        {
            this.decorators = p_236703_1_;
            return this;
        }

        public BaseTreeFeatureConfig.Builder func_236701_a_(int p_236701_1_)
        {
            this.field_236697_g_ = p_236701_1_;
            return this;
        }

        public BaseTreeFeatureConfig.Builder setIgnoreVines()
        {
            this.field_236698_h_ = true;
            return this;
        }

        public BaseTreeFeatureConfig.Builder func_236702_a_(Heightmap.Type p_236702_1_)
        {
            this.field_236699_i_ = p_236702_1_;
            return this;
        }

        public BaseTreeFeatureConfig build()
        {
            return new BaseTreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.field_236694_c_, this.field_236695_d_, this.field_236696_e_, this.decorators, this.field_236697_g_, this.field_236698_h_, this.field_236699_i_);
        }
    }
}
