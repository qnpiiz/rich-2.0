package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;

public class RandomPatchFeature extends Feature<BlockClusterFeatureConfig>
{
    public RandomPatchFeature(Codec<BlockClusterFeatureConfig> p_i231979_1_)
    {
        super(p_i231979_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, BlockClusterFeatureConfig p_241855_5_)
    {
        BlockState blockstate = p_241855_5_.stateProvider.getBlockState(p_241855_3_, p_241855_4_);
        BlockPos blockpos;

        if (p_241855_5_.field_227298_k_)
        {
            blockpos = p_241855_1_.getHeight(Heightmap.Type.WORLD_SURFACE_WG, p_241855_4_);
        }
        else
        {
            blockpos = p_241855_4_;
        }

        int i = 0;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (int j = 0; j < p_241855_5_.tryCount; ++j)
        {
            blockpos$mutable.setAndOffset(blockpos, p_241855_3_.nextInt(p_241855_5_.xSpread + 1) - p_241855_3_.nextInt(p_241855_5_.xSpread + 1), p_241855_3_.nextInt(p_241855_5_.ySpread + 1) - p_241855_3_.nextInt(p_241855_5_.ySpread + 1), p_241855_3_.nextInt(p_241855_5_.zSpread + 1) - p_241855_3_.nextInt(p_241855_5_.zSpread + 1));
            BlockPos blockpos1 = blockpos$mutable.down();
            BlockState blockstate1 = p_241855_1_.getBlockState(blockpos1);

            if ((p_241855_1_.isAirBlock(blockpos$mutable) || p_241855_5_.isReplaceable && p_241855_1_.getBlockState(blockpos$mutable).getMaterial().isReplaceable()) && blockstate.isValidPosition(p_241855_1_, blockpos$mutable) && (p_241855_5_.whitelist.isEmpty() || p_241855_5_.whitelist.contains(blockstate1.getBlock())) && !p_241855_5_.blacklist.contains(blockstate1) && (!p_241855_5_.requiresWater || p_241855_1_.getFluidState(blockpos1.west()).isTagged(FluidTags.WATER) || p_241855_1_.getFluidState(blockpos1.east()).isTagged(FluidTags.WATER) || p_241855_1_.getFluidState(blockpos1.north()).isTagged(FluidTags.WATER) || p_241855_1_.getFluidState(blockpos1.south()).isTagged(FluidTags.WATER)))
            {
                p_241855_5_.blockPlacer.place(p_241855_1_, blockpos$mutable, blockstate, p_241855_3_);
                ++i;
            }
        }

        return i > 0;
    }
}
