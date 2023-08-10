package net.minecraft.world.gen.trunkplacer;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;

public abstract class AbstractTrunkPlacer
{
    public static final Codec<AbstractTrunkPlacer> field_236905_c_ = Registry.TRUNK_REPLACER.dispatch(AbstractTrunkPlacer::func_230381_a_, TrunkPlacerType::func_236927_a_);
    protected final int field_236906_d_;
    protected final int field_236907_e_;
    protected final int field_236908_f_;

    protected static <P extends AbstractTrunkPlacer> P3<Mu<P>, Integer, Integer, Integer> func_236915_a_(Instance<P> p_236915_0_)
    {
        return p_236915_0_.group(Codec.intRange(0, 32).fieldOf("base_height").forGetter((p_236919_0_) ->
        {
            return p_236919_0_.field_236906_d_;
        }), Codec.intRange(0, 24).fieldOf("height_rand_a").forGetter((p_236918_0_) ->
        {
            return p_236918_0_.field_236907_e_;
        }), Codec.intRange(0, 24).fieldOf("height_rand_b").forGetter((p_236916_0_) ->
        {
            return p_236916_0_.field_236908_f_;
        }));
    }

    public AbstractTrunkPlacer(int p_i232060_1_, int p_i232060_2_, int p_i232060_3_)
    {
        this.field_236906_d_ = p_i232060_1_;
        this.field_236907_e_ = p_i232060_2_;
        this.field_236908_f_ = p_i232060_3_;
    }

    protected abstract TrunkPlacerType<?> func_230381_a_();

    public abstract List<FoliagePlacer.Foliage> func_230382_a_(IWorldGenerationReader p_230382_1_, Random p_230382_2_, int p_230382_3_, BlockPos p_230382_4_, Set<BlockPos> p_230382_5_, MutableBoundingBox p_230382_6_, BaseTreeFeatureConfig p_230382_7_);

    public int func_236917_a_(Random p_236917_1_)
    {
        return this.field_236906_d_ + p_236917_1_.nextInt(this.field_236907_e_ + 1) + p_236917_1_.nextInt(this.field_236908_f_ + 1);
    }

    protected static void func_236913_a_(IWorldWriter p_236913_0_, BlockPos p_236913_1_, BlockState p_236913_2_, MutableBoundingBox p_236913_3_)
    {
        TreeFeature.func_236408_b_(p_236913_0_, p_236913_1_, p_236913_2_);
        p_236913_3_.expandTo(new MutableBoundingBox(p_236913_1_, p_236913_1_));
    }

    private static boolean func_236912_a_(IWorldGenerationBaseReader p_236912_0_, BlockPos p_236912_1_)
    {
        return p_236912_0_.hasBlockState(p_236912_1_, (p_236914_0_) ->
        {
            Block block = p_236914_0_.getBlock();
            return Feature.isDirt(block) && !p_236914_0_.isIn(Blocks.GRASS_BLOCK) && !p_236914_0_.isIn(Blocks.MYCELIUM);
        });
    }

    protected static void func_236909_a_(IWorldGenerationReader p_236909_0_, BlockPos p_236909_1_)
    {
        if (!func_236912_a_(p_236909_0_, p_236909_1_))
        {
            TreeFeature.func_236408_b_(p_236909_0_, p_236909_1_, Blocks.DIRT.getDefaultState());
        }
    }

    protected static boolean func_236911_a_(IWorldGenerationReader p_236911_0_, Random p_236911_1_, BlockPos p_236911_2_, Set<BlockPos> p_236911_3_, MutableBoundingBox p_236911_4_, BaseTreeFeatureConfig p_236911_5_)
    {
        if (TreeFeature.isReplaceableAt(p_236911_0_, p_236911_2_))
        {
            func_236913_a_(p_236911_0_, p_236911_2_, p_236911_5_.trunkProvider.getBlockState(p_236911_1_, p_236911_2_), p_236911_4_);
            p_236911_3_.add(p_236911_2_.toImmutable());
            return true;
        }
        else
        {
            return false;
        }
    }

    protected static void func_236910_a_(IWorldGenerationReader p_236910_0_, Random p_236910_1_, BlockPos.Mutable p_236910_2_, Set<BlockPos> p_236910_3_, MutableBoundingBox p_236910_4_, BaseTreeFeatureConfig p_236910_5_)
    {
        if (TreeFeature.func_236410_c_(p_236910_0_, p_236910_2_))
        {
            func_236911_a_(p_236910_0_, p_236910_1_, p_236910_2_, p_236910_3_, p_236910_4_, p_236910_5_);
        }
    }
}
