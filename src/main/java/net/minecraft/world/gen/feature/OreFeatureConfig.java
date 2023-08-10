package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;

public class OreFeatureConfig implements IFeatureConfig
{
    public static final Codec<OreFeatureConfig> field_236566_a_ = RecordCodecBuilder.create((p_236568_0_) ->
    {
        return p_236568_0_.group(RuleTest.field_237127_c_.fieldOf("target").forGetter((p_236570_0_) -> {
            return p_236570_0_.target;
        }), BlockState.CODEC.fieldOf("state").forGetter((p_236569_0_) -> {
            return p_236569_0_.state;
        }), Codec.intRange(0, 64).fieldOf("size").forGetter((p_236567_0_) -> {
            return p_236567_0_.size;
        })).apply(p_236568_0_, OreFeatureConfig::new);
    });
    public final RuleTest target;
    public final int size;
    public final BlockState state;

    public OreFeatureConfig(RuleTest p_i241989_1_, BlockState p_i241989_2_, int p_i241989_3_)
    {
        this.size = p_i241989_3_;
        this.state = p_i241989_2_;
        this.target = p_i241989_1_;
    }

    public static final class FillerBlockType
    {
        public static final RuleTest field_241882_a = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
        public static final RuleTest field_241883_b = new BlockMatchRuleTest(Blocks.NETHERRACK);
        public static final RuleTest field_241884_c = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);
    }
}
