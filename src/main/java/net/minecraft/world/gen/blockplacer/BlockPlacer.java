package net.minecraft.world.gen.blockplacer;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public abstract class BlockPlacer
{
    public static final Codec<BlockPlacer> CODEC = Registry.BLOCK_PLACER_TYPE.dispatch(BlockPlacer::getBlockPlacerType, BlockPlacerType::getCodec);

    public abstract void place(IWorld world, BlockPos pos, BlockState state, Random random);

    protected abstract BlockPlacerType<?> getBlockPlacerType();
}
