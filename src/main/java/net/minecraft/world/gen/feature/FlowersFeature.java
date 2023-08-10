package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;

public abstract class FlowersFeature<U extends IFeatureConfig> extends Feature<U>
{
    public FlowersFeature(Codec<U> p_i231922_1_)
    {
        super(p_i231922_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, U p_241855_5_)
    {
        BlockState blockstate = this.getFlowerToPlace(p_241855_3_, p_241855_4_, p_241855_5_);
        int i = 0;

        for (int j = 0; j < this.getFlowerCount(p_241855_5_); ++j)
        {
            BlockPos blockpos = this.getNearbyPos(p_241855_3_, p_241855_4_, p_241855_5_);

            if (p_241855_1_.isAirBlock(blockpos) && blockpos.getY() < 255 && blockstate.isValidPosition(p_241855_1_, blockpos) && this.isValidPosition(p_241855_1_, blockpos, p_241855_5_))
            {
                p_241855_1_.setBlockState(blockpos, blockstate, 2);
                ++i;
            }
        }

        return i > 0;
    }

    public abstract boolean isValidPosition(IWorld world, BlockPos pos, U config);

    public abstract int getFlowerCount(U config);

    public abstract BlockPos getNearbyPos(Random rand, BlockPos pos, U config);

    public abstract BlockState getFlowerToPlace(Random rand, BlockPos pos, U confgi);
}
