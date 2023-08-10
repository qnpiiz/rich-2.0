package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;

public class BlockMatchRuleTest extends RuleTest
{
    public static final Codec<BlockMatchRuleTest> field_237075_a_ = Registry.BLOCK.fieldOf("block").xmap(BlockMatchRuleTest::new, (p_237076_0_) ->
    {
        return p_237076_0_.block;
    }).codec();
    private final Block block;

    public BlockMatchRuleTest(Block block)
    {
        this.block = block;
    }

    public boolean test(BlockState p_215181_1_, Random p_215181_2_)
    {
        return p_215181_1_.isIn(this.block);
    }

    protected IRuleTestType<?> getType()
    {
        return IRuleTestType.BLOCK_MATCH;
    }
}
