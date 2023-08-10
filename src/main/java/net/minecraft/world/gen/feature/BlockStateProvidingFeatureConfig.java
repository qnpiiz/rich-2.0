package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;

public class BlockStateProvidingFeatureConfig implements IFeatureConfig
{
    public static final Codec<BlockStateProvidingFeatureConfig> field_236453_a_ = BlockStateProvider.CODEC.fieldOf("state_provider").xmap(BlockStateProvidingFeatureConfig::new, (p_236454_0_) ->
    {
        return p_236454_0_.field_227268_a_;
    }).codec();
    public final BlockStateProvider field_227268_a_;

    public BlockStateProvidingFeatureConfig(BlockStateProvider p_i225830_1_)
    {
        this.field_227268_a_ = p_i225830_1_;
    }
}
