package net.minecraft.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.state.Property;
import net.minecraft.util.Util;

public class FinishedVariantBlockState implements IFinishedBlockState
{
    private final Block field_240115_a_;
    private final List<BlockModelDefinition> field_240116_b_;
    private final Set < Property<? >> field_240117_c_ = Sets.newHashSet();
    private final List<BlockStateVariantBuilder> field_240118_d_ = Lists.newArrayList();

    private FinishedVariantBlockState(Block p_i232529_1_, List<BlockModelDefinition> p_i232529_2_)
    {
        this.field_240115_a_ = p_i232529_1_;
        this.field_240116_b_ = p_i232529_2_;
    }

    public FinishedVariantBlockState func_240125_a_(BlockStateVariantBuilder p_240125_1_)
    {
        p_240125_1_.func_230527_b_().forEach((p_240122_1_) ->
        {
            if (this.field_240115_a_.getStateContainer().getProperty(p_240122_1_.getName()) != p_240122_1_)
            {
                throw new IllegalStateException("Property " + p_240122_1_ + " is not defined for block " + this.field_240115_a_);
            }
            else if (!this.field_240117_c_.add(p_240122_1_))
            {
                throw new IllegalStateException("Values of property " + p_240122_1_ + " already defined for block " + this.field_240115_a_);
            }
        });
        this.field_240118_d_.add(p_240125_1_);
        return this;
    }

    public JsonElement get()
    {
        Stream<Pair<VariantPropertyBuilder, List<BlockModelDefinition>>> stream = Stream.of(Pair.of(VariantPropertyBuilder.func_240187_a_(), this.field_240116_b_));

        for (BlockStateVariantBuilder blockstatevariantbuilder : this.field_240118_d_)
        {
            Map<VariantPropertyBuilder, List<BlockModelDefinition>> map = blockstatevariantbuilder.func_240132_a_();
            stream = stream.flatMap((p_240130_1_) ->
            {
                return map.entrySet().stream().map((p_240124_1_) -> {
                    VariantPropertyBuilder variantpropertybuilder = ((VariantPropertyBuilder)p_240130_1_.getFirst()).func_240189_a_(p_240124_1_.getKey());
                    List<BlockModelDefinition> list = func_240127_a_((List)p_240130_1_.getSecond(), p_240124_1_.getValue());
                    return Pair.of(variantpropertybuilder, list);
                });
            });
        }

        Map<String, JsonElement> map1 = new TreeMap<>();
        stream.forEach((p_240129_1_) ->
        {
            JsonElement jsonelement = map1.put(p_240129_1_.getFirst().func_240191_b_(), BlockModelDefinition.serialize(p_240129_1_.getSecond()));
        });
        JsonObject jsonobject = new JsonObject();
        jsonobject.add("variants", Util.make(new JsonObject(), (p_240128_1_) ->
        {
            map1.forEach(p_240128_1_::add);
        }));
        return jsonobject;
    }

    private static List<BlockModelDefinition> func_240127_a_(List<BlockModelDefinition> p_240127_0_, List<BlockModelDefinition> p_240127_1_)
    {
        Builder<BlockModelDefinition> builder = ImmutableList.builder();
        p_240127_0_.forEach((p_240126_2_) ->
        {
            p_240127_1_.forEach((p_240123_2_) -> {
                builder.add(BlockModelDefinition.mergeDefinitions(p_240126_2_, p_240123_2_));
            });
        });
        return builder.build();
    }

    public Block func_230524_a_()
    {
        return this.field_240115_a_;
    }

    public static FinishedVariantBlockState func_240119_a_(Block p_240119_0_)
    {
        return new FinishedVariantBlockState(p_240119_0_, ImmutableList.of(BlockModelDefinition.getNewModelDefinition()));
    }

    public static FinishedVariantBlockState func_240120_a_(Block p_240120_0_, BlockModelDefinition p_240120_1_)
    {
        return new FinishedVariantBlockState(p_240120_0_, ImmutableList.of(p_240120_1_));
    }

    public static FinishedVariantBlockState func_240121_a_(Block p_240121_0_, BlockModelDefinition... p_240121_1_)
    {
        return new FinishedVariantBlockState(p_240121_0_, ImmutableList.copyOf(p_240121_1_));
    }
}
