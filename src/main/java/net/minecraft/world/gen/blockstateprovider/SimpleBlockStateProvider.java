package net.minecraft.world.gen.blockstateprovider;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class SimpleBlockStateProvider extends BlockStateProvider
{
    public static final Codec<SimpleBlockStateProvider> CODEC = BlockState.CODEC.fieldOf("state").xmap(SimpleBlockStateProvider::new, (provider) ->
    {
        return provider.state;
    }).codec();
    private final BlockState state;

    public SimpleBlockStateProvider(BlockState state)
    {
        this.state = state;
    }

    protected BlockStateProviderType<?> getProviderType()
    {
        return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
    }

    public BlockState getBlockState(Random randomIn, BlockPos blockPosIn)
    {
        return this.state;
    }
}
