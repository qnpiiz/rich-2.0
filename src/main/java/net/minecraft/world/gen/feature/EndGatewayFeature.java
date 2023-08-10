package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class EndGatewayFeature extends Feature<EndGatewayConfig>
{
    public EndGatewayFeature(Codec<EndGatewayConfig> p_i231951_1_)
    {
        super(p_i231951_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, EndGatewayConfig p_241855_5_)
    {
        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(p_241855_4_.add(-1, -2, -1), p_241855_4_.add(1, 2, 1)))
        {
            boolean flag = blockpos.getX() == p_241855_4_.getX();
            boolean flag1 = blockpos.getY() == p_241855_4_.getY();
            boolean flag2 = blockpos.getZ() == p_241855_4_.getZ();
            boolean flag3 = Math.abs(blockpos.getY() - p_241855_4_.getY()) == 2;

            if (flag && flag1 && flag2)
            {
                BlockPos blockpos1 = blockpos.toImmutable();
                this.setBlockState(p_241855_1_, blockpos1, Blocks.END_GATEWAY.getDefaultState());
                p_241855_5_.func_214700_b().ifPresent((p_236280_3_) ->
                {
                    TileEntity tileentity = p_241855_1_.getTileEntity(blockpos1);

                    if (tileentity instanceof EndGatewayTileEntity)
                    {
                        EndGatewayTileEntity endgatewaytileentity = (EndGatewayTileEntity)tileentity;
                        endgatewaytileentity.setExitPortal(p_236280_3_, p_241855_5_.func_214701_c());
                        tileentity.markDirty();
                    }
                });
            }
            else if (flag1)
            {
                this.setBlockState(p_241855_1_, blockpos, Blocks.AIR.getDefaultState());
            }
            else if (flag3 && flag && flag2)
            {
                this.setBlockState(p_241855_1_, blockpos, Blocks.BEDROCK.getDefaultState());
            }
            else if ((flag || flag2) && !flag3)
            {
                this.setBlockState(p_241855_1_, blockpos, Blocks.BEDROCK.getDefaultState());
            }
            else
            {
                this.setBlockState(p_241855_1_, blockpos, Blocks.AIR.getDefaultState());
            }
        }

        return true;
    }
}
