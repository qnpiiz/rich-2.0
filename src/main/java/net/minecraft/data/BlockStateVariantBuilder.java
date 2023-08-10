package net.minecraft.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.state.Property;

public abstract class BlockStateVariantBuilder
{
    private final Map<VariantPropertyBuilder, List<BlockModelDefinition>> field_240131_a_ = Maps.newHashMap();

    protected void func_240140_a_(VariantPropertyBuilder p_240140_1_, List<BlockModelDefinition> p_240140_2_)
    {
        List<BlockModelDefinition> list = this.field_240131_a_.put(p_240140_1_, p_240140_2_);

        if (list != null)
        {
            throw new IllegalStateException("Value " + p_240140_1_ + " is already defined");
        }
    }

    Map<VariantPropertyBuilder, List<BlockModelDefinition>> func_240132_a_()
    {
        this.func_240141_c_();
        return ImmutableMap.copyOf(this.field_240131_a_);
    }

    private void func_240141_c_()
    {
        List < Property<? >> list = this.func_230527_b_();
        Stream<VariantPropertyBuilder> stream = Stream.of(VariantPropertyBuilder.func_240187_a_());

        for (Property<?> property : list)
        {
            stream = stream.flatMap((p_240138_1_) ->
            {
                return property.func_241491_c_().map(p_240138_1_::func_240188_a_);
            });
        }

        List<VariantPropertyBuilder> list1 = stream.filter((p_240139_1_) ->
        {
            return !this.field_240131_a_.containsKey(p_240139_1_);
        }).collect(Collectors.toList());

        if (!list1.isEmpty())
        {
            throw new IllegalStateException("Missing definition for properties: " + list1);
        }
    }

    abstract List < Property<? >> func_230527_b_();

    public static <T1 extends Comparable<T1>> BlockStateVariantBuilder.One<T1> func_240133_a_(Property<T1> p_240133_0_)
    {
        return new BlockStateVariantBuilder.One<>(p_240133_0_);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>> BlockStateVariantBuilder.Two<T1, T2> func_240134_a_(Property<T1> p_240134_0_, Property<T2> p_240134_1_)
    {
        return new BlockStateVariantBuilder.Two<>(p_240134_0_, p_240134_1_);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> BlockStateVariantBuilder.Three<T1, T2, T3> func_240135_a_(Property<T1> p_240135_0_, Property<T2> p_240135_1_, Property<T3> p_240135_2_)
    {
        return new BlockStateVariantBuilder.Three<>(p_240135_0_, p_240135_1_, p_240135_2_);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> BlockStateVariantBuilder.Four<T1, T2, T3, T4> func_240136_a_(Property<T1> p_240136_0_, Property<T2> p_240136_1_, Property<T3> p_240136_2_, Property<T4> p_240136_3_)
    {
        return new BlockStateVariantBuilder.Four<>(p_240136_0_, p_240136_1_, p_240136_2_, p_240136_3_);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> BlockStateVariantBuilder.Five<T1, T2, T3, T4, T5> func_240137_a_(Property<T1> p_240137_0_, Property<T2> p_240137_1_, Property<T3> p_240137_2_, Property<T4> p_240137_3_, Property<T5> p_240137_4_)
    {
        return new BlockStateVariantBuilder.Five<>(p_240137_0_, p_240137_1_, p_240137_2_, p_240137_3_, p_240137_4_);
    }

    public static class Five<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> extends BlockStateVariantBuilder
    {
        private final Property<T1> field_240172_a_;
        private final Property<T2> field_240173_b_;
        private final Property<T3> field_240174_c_;
        private final Property<T4> field_240175_d_;
        private final Property<T5> field_240176_e_;

        private Five(Property<T1> p_i232538_1_, Property<T2> p_i232538_2_, Property<T3> p_i232538_3_, Property<T4> p_i232538_4_, Property<T5> p_i232538_5_)
        {
            this.field_240172_a_ = p_i232538_1_;
            this.field_240173_b_ = p_i232538_2_;
            this.field_240174_c_ = p_i232538_3_;
            this.field_240175_d_ = p_i232538_4_;
            this.field_240176_e_ = p_i232538_5_;
        }

        public List < Property<? >> func_230527_b_()
        {
            return ImmutableList.of(this.field_240172_a_, this.field_240173_b_, this.field_240174_c_, this.field_240175_d_, this.field_240176_e_);
        }

        public BlockStateVariantBuilder.Five<T1, T2, T3, T4, T5> func_240178_a_(T1 p_240178_1_, T2 p_240178_2_, T3 p_240178_3_, T4 p_240178_4_, T5 p_240178_5_, List<BlockModelDefinition> p_240178_6_)
        {
            VariantPropertyBuilder variantpropertybuilder = VariantPropertyBuilder.func_240190_a_(this.field_240172_a_.func_241490_b_(p_240178_1_), this.field_240173_b_.func_241490_b_(p_240178_2_), this.field_240174_c_.func_241490_b_(p_240178_3_), this.field_240175_d_.func_241490_b_(p_240178_4_), this.field_240176_e_.func_241490_b_(p_240178_5_));
            this.func_240140_a_(variantpropertybuilder, p_240178_6_);
            return this;
        }

        public BlockStateVariantBuilder.Five<T1, T2, T3, T4, T5> func_240177_a_(T1 p_240177_1_, T2 p_240177_2_, T3 p_240177_3_, T4 p_240177_4_, T5 p_240177_5_, BlockModelDefinition p_240177_6_)
        {
            return this.func_240178_a_(p_240177_1_, p_240177_2_, p_240177_3_, p_240177_4_, p_240177_5_, Collections.singletonList(p_240177_6_));
        }
    }

    public static class Four<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> extends BlockStateVariantBuilder
    {
        private final Property<T1> field_240166_a_;
        private final Property<T2> field_240167_b_;
        private final Property<T3> field_240168_c_;
        private final Property<T4> field_240169_d_;

        private Four(Property<T1> p_i232536_1_, Property<T2> p_i232536_2_, Property<T3> p_i232536_3_, Property<T4> p_i232536_4_)
        {
            this.field_240166_a_ = p_i232536_1_;
            this.field_240167_b_ = p_i232536_2_;
            this.field_240168_c_ = p_i232536_3_;
            this.field_240169_d_ = p_i232536_4_;
        }

        public List < Property<? >> func_230527_b_()
        {
            return ImmutableList.of(this.field_240166_a_, this.field_240167_b_, this.field_240168_c_, this.field_240169_d_);
        }

        public BlockStateVariantBuilder.Four<T1, T2, T3, T4> func_240171_a_(T1 p_240171_1_, T2 p_240171_2_, T3 p_240171_3_, T4 p_240171_4_, List<BlockModelDefinition> p_240171_5_)
        {
            VariantPropertyBuilder variantpropertybuilder = VariantPropertyBuilder.func_240190_a_(this.field_240166_a_.func_241490_b_(p_240171_1_), this.field_240167_b_.func_241490_b_(p_240171_2_), this.field_240168_c_.func_241490_b_(p_240171_3_), this.field_240169_d_.func_241490_b_(p_240171_4_));
            this.func_240140_a_(variantpropertybuilder, p_240171_5_);
            return this;
        }

        public BlockStateVariantBuilder.Four<T1, T2, T3, T4> func_240170_a_(T1 p_240170_1_, T2 p_240170_2_, T3 p_240170_3_, T4 p_240170_4_, BlockModelDefinition p_240170_5_)
        {
            return this.func_240171_a_(p_240170_1_, p_240170_2_, p_240170_3_, p_240170_4_, Collections.singletonList(p_240170_5_));
        }
    }

    @FunctionalInterface
    public interface ITriFunction<P1, P2, P3, R>
    {
        R apply(P1 p_apply_1_, P2 p_apply_2_, P3 p_apply_3_);
    }

    public static class One<T1 extends Comparable<T1>> extends BlockStateVariantBuilder
    {
        private final Property<T1> field_240142_a_;

        private One(Property<T1> p_i232530_1_)
        {
            this.field_240142_a_ = p_i232530_1_;
        }

        public List < Property<? >> func_230527_b_()
        {
            return ImmutableList.of(this.field_240142_a_);
        }

        public BlockStateVariantBuilder.One<T1> func_240144_a_(T1 p_240144_1_, List<BlockModelDefinition> p_240144_2_)
        {
            VariantPropertyBuilder variantpropertybuilder = VariantPropertyBuilder.func_240190_a_(this.field_240142_a_.func_241490_b_(p_240144_1_));
            this.func_240140_a_(variantpropertybuilder, p_240144_2_);
            return this;
        }

        public BlockStateVariantBuilder.One<T1> func_240143_a_(T1 p_240143_1_, BlockModelDefinition p_240143_2_)
        {
            return this.func_240144_a_(p_240143_1_, Collections.singletonList(p_240143_2_));
        }

        public BlockStateVariantBuilder func_240145_a_(Function<T1, BlockModelDefinition> p_240145_1_)
        {
            this.field_240142_a_.getAllowedValues().forEach((p_240146_2_) ->
            {
                this.func_240143_a_(p_240146_2_, p_240145_1_.apply(p_240146_2_));
            });
            return this;
        }
    }

    public static class Three<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> extends BlockStateVariantBuilder
    {
        private final Property<T1> field_240157_a_;
        private final Property<T2> field_240158_b_;
        private final Property<T3> field_240159_c_;

        private Three(Property<T1> p_i232534_1_, Property<T2> p_i232534_2_, Property<T3> p_i232534_3_)
        {
            this.field_240157_a_ = p_i232534_1_;
            this.field_240158_b_ = p_i232534_2_;
            this.field_240159_c_ = p_i232534_3_;
        }

        public List < Property<? >> func_230527_b_()
        {
            return ImmutableList.of(this.field_240157_a_, this.field_240158_b_, this.field_240159_c_);
        }

        public BlockStateVariantBuilder.Three<T1, T2, T3> func_240162_a_(T1 p_240162_1_, T2 p_240162_2_, T3 p_240162_3_, List<BlockModelDefinition> p_240162_4_)
        {
            VariantPropertyBuilder variantpropertybuilder = VariantPropertyBuilder.func_240190_a_(this.field_240157_a_.func_241490_b_(p_240162_1_), this.field_240158_b_.func_241490_b_(p_240162_2_), this.field_240159_c_.func_241490_b_(p_240162_3_));
            this.func_240140_a_(variantpropertybuilder, p_240162_4_);
            return this;
        }

        public BlockStateVariantBuilder.Three<T1, T2, T3> func_240161_a_(T1 p_240161_1_, T2 p_240161_2_, T3 p_240161_3_, BlockModelDefinition p_240161_4_)
        {
            return this.func_240162_a_(p_240161_1_, p_240161_2_, p_240161_3_, Collections.singletonList(p_240161_4_));
        }

        public BlockStateVariantBuilder func_240160_a_(BlockStateVariantBuilder.ITriFunction<T1, T2, T3, BlockModelDefinition> p_240160_1_)
        {
            this.field_240157_a_.getAllowedValues().forEach((p_240163_2_) ->
            {
                this.field_240158_b_.getAllowedValues().forEach((p_240164_3_) -> {
                    this.field_240159_c_.getAllowedValues().forEach((p_240165_4_) -> {
                        this.func_240161_a_((T1)p_240163_2_, (T2)p_240164_3_, p_240165_4_, p_240160_1_.apply((T1)p_240163_2_, (T2)p_240164_3_, p_240165_4_));
                    });
                });
            });
            return this;
        }
    }

    public static class Two<T1 extends Comparable<T1>, T2 extends Comparable<T2>> extends BlockStateVariantBuilder
    {
        private final Property<T1> field_240147_a_;
        private final Property<T2> field_240148_b_;

        private Two(Property<T1> p_i232532_1_, Property<T2> p_i232532_2_)
        {
            this.field_240147_a_ = p_i232532_1_;
            this.field_240148_b_ = p_i232532_2_;
        }

        public List < Property<? >> func_230527_b_()
        {
            return ImmutableList.of(this.field_240147_a_, this.field_240148_b_);
        }

        public BlockStateVariantBuilder.Two<T1, T2> func_240150_a_(T1 p_240150_1_, T2 p_240150_2_, List<BlockModelDefinition> p_240150_3_)
        {
            VariantPropertyBuilder variantpropertybuilder = VariantPropertyBuilder.func_240190_a_(this.field_240147_a_.func_241490_b_(p_240150_1_), this.field_240148_b_.func_241490_b_(p_240150_2_));
            this.func_240140_a_(variantpropertybuilder, p_240150_3_);
            return this;
        }

        public BlockStateVariantBuilder.Two<T1, T2> func_240149_a_(T1 p_240149_1_, T2 p_240149_2_, BlockModelDefinition p_240149_3_)
        {
            return this.func_240150_a_(p_240149_1_, p_240149_2_, Collections.singletonList(p_240149_3_));
        }

        public BlockStateVariantBuilder func_240152_a_(BiFunction<T1, T2, BlockModelDefinition> p_240152_1_)
        {
            this.field_240147_a_.getAllowedValues().forEach((p_240156_2_) ->
            {
                this.field_240148_b_.getAllowedValues().forEach((p_240154_3_) -> {
                    this.func_240149_a_((T1)p_240156_2_, p_240154_3_, p_240152_1_.apply((T1)p_240156_2_, p_240154_3_));
                });
            });
            return this;
        }

        public BlockStateVariantBuilder func_240155_b_(BiFunction<T1, T2, List<BlockModelDefinition>> p_240155_1_)
        {
            this.field_240147_a_.getAllowedValues().forEach((p_240153_2_) ->
            {
                this.field_240148_b_.getAllowedValues().forEach((p_240151_3_) -> {
                    this.func_240150_a_((T1)p_240153_2_, p_240151_3_, p_240155_1_.apply((T1)p_240153_2_, p_240151_3_));
                });
            });
            return this;
        }
    }
}
