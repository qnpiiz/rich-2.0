package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class DefaultFlowersFeature extends FlowersFeature<BlockClusterFeatureConfig>
{
    public DefaultFlowersFeature(Codec<BlockClusterFeatureConfig> p_i231945_1_)
    {
        super(p_i231945_1_);
    }

    public boolean isValidPosition(IWorld world, BlockPos pos, BlockClusterFeatureConfig config)
    {
        return !config.blacklist.contains(world.getBlockState(pos));
    }

    public int getFlowerCount(BlockClusterFeatureConfig config)
    {
        return config.tryCount;
    }

    public BlockPos getNearbyPos(Random rand, BlockPos pos, BlockClusterFeatureConfig config)
    {
        return pos.add(rand.nextInt(config.xSpread) - rand.nextInt(config.xSpread), rand.nextInt(config.ySpread) - rand.nextInt(config.ySpread), rand.nextInt(config.zSpread) - rand.nextInt(config.zSpread));
    }

    public BlockState getFlowerToPlace(Random rand, BlockPos pos, BlockClusterFeatureConfig confgi)
    {
        return confgi.stateProvider.getBlockState(rand, pos);
    }
}
