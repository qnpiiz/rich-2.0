package net.minecraft.client.renderer.model.multipart;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;

public class AndCondition implements ICondition
{
    private final Iterable <? extends ICondition > conditions;

    public AndCondition(Iterable <? extends ICondition > conditionsIn)
    {
        this.conditions = conditionsIn;
    }

    public Predicate<BlockState> getPredicate(StateContainer<Block, BlockState> p_getPredicate_1_)
    {
        List<Predicate<BlockState>> list = Streams.stream(this.conditions).map((condition) ->
        {
            return condition.getPredicate(p_getPredicate_1_);
        }).collect(Collectors.toList());
        return (state) ->
        {
            return list.stream().allMatch((predicate) -> {
                return predicate.test(state);
            });
        };
    }
}
