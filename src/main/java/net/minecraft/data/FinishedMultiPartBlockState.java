package net.minecraft.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;

public class FinishedMultiPartBlockState implements IFinishedBlockState
{
    private final Block field_240104_a_;
    private final List<FinishedMultiPartBlockState.Part> field_240105_b_ = Lists.newArrayList();

    private FinishedMultiPartBlockState(Block p_i232524_1_)
    {
        this.field_240104_a_ = p_i232524_1_;
    }

    public Block func_230524_a_()
    {
        return this.field_240104_a_;
    }

    public static FinishedMultiPartBlockState func_240106_a_(Block p_240106_0_)
    {
        return new FinishedMultiPartBlockState(p_240106_0_);
    }

    public FinishedMultiPartBlockState func_240112_a_(List<BlockModelDefinition> p_240112_1_)
    {
        this.field_240105_b_.add(new FinishedMultiPartBlockState.Part(p_240112_1_));
        return this;
    }

    public FinishedMultiPartBlockState func_240111_a_(BlockModelDefinition p_240111_1_)
    {
        return this.func_240112_a_(ImmutableList.of(p_240111_1_));
    }

    public FinishedMultiPartBlockState func_240109_a_(IMultiPartPredicateBuilder p_240109_1_, List<BlockModelDefinition> p_240109_2_)
    {
        this.field_240105_b_.add(new FinishedMultiPartBlockState.ConditionalPart(p_240109_1_, p_240109_2_));
        return this;
    }

    public FinishedMultiPartBlockState func_240110_a_(IMultiPartPredicateBuilder p_240110_1_, BlockModelDefinition... p_240110_2_)
    {
        return this.func_240109_a_(p_240110_1_, ImmutableList.copyOf(p_240110_2_));
    }

    public FinishedMultiPartBlockState func_240108_a_(IMultiPartPredicateBuilder p_240108_1_, BlockModelDefinition p_240108_2_)
    {
        return this.func_240109_a_(p_240108_1_, ImmutableList.of(p_240108_2_));
    }

    public JsonElement get()
    {
        StateContainer<Block, BlockState> statecontainer = this.field_240104_a_.getStateContainer();
        this.field_240105_b_.forEach((p_240107_1_) ->
        {
            p_240107_1_.func_230525_a_(statecontainer);
        });
        JsonArray jsonarray = new JsonArray();
        this.field_240105_b_.stream().map(FinishedMultiPartBlockState.Part::get).forEach(jsonarray::add);
        JsonObject jsonobject = new JsonObject();
        jsonobject.add("multipart", jsonarray);
        return jsonobject;
    }

    static class ConditionalPart extends FinishedMultiPartBlockState.Part
    {
        private final IMultiPartPredicateBuilder field_240113_a_;

        private ConditionalPart(IMultiPartPredicateBuilder p_i232525_1_, List<BlockModelDefinition> p_i232525_2_)
        {
            super(p_i232525_2_);
            this.field_240113_a_ = p_i232525_1_;
        }

        public void func_230525_a_(StateContainer <? , ? > p_230525_1_)
        {
            this.field_240113_a_.func_230523_a_(p_230525_1_);
        }

        public void func_230526_a_(JsonObject p_230526_1_)
        {
            p_230526_1_.add("when", this.field_240113_a_.get());
        }
    }

    static class Part implements Supplier<JsonElement>
    {
        private final List<BlockModelDefinition> field_240114_a_;

        private Part(List<BlockModelDefinition> p_i232527_1_)
        {
            this.field_240114_a_ = p_i232527_1_;
        }

        public void func_230525_a_(StateContainer <? , ? > p_230525_1_)
        {
        }

        public void func_230526_a_(JsonObject p_230526_1_)
        {
        }

        public JsonElement get()
        {
            JsonObject jsonobject = new JsonObject();
            this.func_230526_a_(jsonobject);
            jsonobject.add("apply", BlockModelDefinition.serialize(this.field_240114_a_));
            return jsonobject;
        }
    }
}
