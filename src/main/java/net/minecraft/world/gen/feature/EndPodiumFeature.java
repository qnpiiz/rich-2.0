package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class EndPodiumFeature extends Feature<NoFeatureConfig>
{
    public static final BlockPos END_PODIUM_LOCATION = BlockPos.ZERO;
    private final boolean activePortal;

    public EndPodiumFeature(boolean activePortalIn)
    {
        super(NoFeatureConfig.field_236558_a_);
        this.activePortal = activePortalIn;
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_)
    {
        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(p_241855_4_.getX() - 4, p_241855_4_.getY() - 1, p_241855_4_.getZ() - 4), new BlockPos(p_241855_4_.getX() + 4, p_241855_4_.getY() + 32, p_241855_4_.getZ() + 4)))
        {
            boolean flag = blockpos.withinDistance(p_241855_4_, 2.5D);

            if (flag || blockpos.withinDistance(p_241855_4_, 3.5D))
            {
                if (blockpos.getY() < p_241855_4_.getY())
                {
                    if (flag)
                    {
                        this.setBlockState(p_241855_1_, blockpos, Blocks.BEDROCK.getDefaultState());
                    }
                    else if (blockpos.getY() < p_241855_4_.getY())
                    {
                        this.setBlockState(p_241855_1_, blockpos, Blocks.END_STONE.getDefaultState());
                    }
                }
                else if (blockpos.getY() > p_241855_4_.getY())
                {
                    this.setBlockState(p_241855_1_, blockpos, Blocks.AIR.getDefaultState());
                }
                else if (!flag)
                {
                    this.setBlockState(p_241855_1_, blockpos, Blocks.BEDROCK.getDefaultState());
                }
                else if (this.activePortal)
                {
                    this.setBlockState(p_241855_1_, new BlockPos(blockpos), Blocks.END_PORTAL.getDefaultState());
                }
                else
                {
                    this.setBlockState(p_241855_1_, new BlockPos(blockpos), Blocks.AIR.getDefaultState());
                }
            }
        }

        for (int i = 0; i < 4; ++i)
        {
            this.setBlockState(p_241855_1_, p_241855_4_.up(i), Blocks.BEDROCK.getDefaultState());
        }

        BlockPos blockpos1 = p_241855_4_.up(2);

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            this.setBlockState(p_241855_1_, blockpos1.offset(direction), Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, direction));
        }

        return true;
    }
}
