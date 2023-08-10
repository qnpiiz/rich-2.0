package net.minecraft.world.gen.blockstateprovider;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public abstract class BlockStateProvider
{
    public static final Codec<BlockStateProvider> CODEC = Registry.BLOCK_STATE_PROVIDER_TYPE.dispatch(BlockStateProvider::getProviderType, BlockStateProviderType::getCodec);

    protected abstract BlockStateProviderType<?> getProviderType();

    public abstract BlockState getBlockState(Random randomIn, BlockPos blockPosIn);
}
