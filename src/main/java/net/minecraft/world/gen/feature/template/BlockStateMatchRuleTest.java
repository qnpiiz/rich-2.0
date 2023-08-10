package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;

public class BlockStateMatchRuleTest extends RuleTest
{
    public static final Codec<BlockStateMatchRuleTest> field_237079_a_ = BlockState.CODEC.fieldOf("block_state").xmap(BlockStateMatchRuleTest::new, (p_237080_0_) ->
    {
        return p_237080_0_.state;
    }).codec();
    private final BlockState state;

    public BlockStateMatchRuleTest(BlockState state)
    {
        this.state = state;
    }

    public boolean test(BlockState p_215181_1_, Random p_215181_2_)
    {
        return p_215181_1_ == this.state;
    }

    protected IRuleTestType<?> getType()
    {
        return IRuleTestType.BLOCKSTATE_MATCH;
    }
}
