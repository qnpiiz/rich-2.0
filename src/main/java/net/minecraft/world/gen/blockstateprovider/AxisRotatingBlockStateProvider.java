package net.minecraft.world.gen.blockstateprovider;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class AxisRotatingBlockStateProvider extends BlockStateProvider
{
    public static final Codec<AxisRotatingBlockStateProvider> CODEC = BlockState.CODEC.fieldOf("state").xmap(AbstractBlock.AbstractBlockState::getBlock, Block::getDefaultState).xmap(AxisRotatingBlockStateProvider::new, (provider) ->
    {
        return provider.block;
    }).codec();
    private final Block block;

    public AxisRotatingBlockStateProvider(Block block)
    {
        this.block = block;
    }

    protected BlockStateProviderType<?> getProviderType()
    {
        return BlockStateProviderType.AXIS_ROTATING_STATE_PROVIDER;
    }

    public BlockState getBlockState(Random randomIn, BlockPos blockPosIn)
    {
        Direction.Axis direction$axis = Direction.Axis.getRandomAxis(randomIn);
        return this.block.getDefaultState().with(RotatedPillarBlock.AXIS, direction$axis);
    }
}
