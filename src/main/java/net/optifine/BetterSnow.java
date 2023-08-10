package net.optifine;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.FourWayBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.block.TallGrassBlock;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;

public class BetterSnow
{
    private static IBakedModel modelSnowLayer = null;

    public static void update()
    {
        modelSnowLayer = Config.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModel(Blocks.SNOW.getDefaultState());
    }

    public static IBakedModel getModelSnowLayer()
    {
        return modelSnowLayer;
    }

    public static BlockState getStateSnowLayer()
    {
        return Blocks.SNOW.getDefaultState();
    }

    public static boolean shouldRender(IBlockDisplayReader lightReader, BlockState blockState, BlockPos blockPos)
    {
        if (!(lightReader instanceof IBlockReader))
        {
            return false;
        }
        else
        {
            return !checkBlock(lightReader, blockState, blockPos) ? false : hasSnowNeighbours(lightReader, blockPos);
        }
    }

    private static boolean hasSnowNeighbours(IBlockReader blockAccess, BlockPos pos)
    {
        Block block = Blocks.SNOW;

        if (blockAccess.getBlockState(pos.north()).getBlock() == block || blockAccess.getBlockState(pos.south()).getBlock() == block || blockAccess.getBlockState(pos.west()).getBlock() == block || blockAccess.getBlockState(pos.east()).getBlock() == block)
        {
            BlockState blockstate = blockAccess.getBlockState(pos.down());

            if (blockstate.isOpaqueCube(blockAccess, pos))
            {
                return true;
            }

            Block block1 = blockstate.getBlock();

            if (block1 instanceof StairsBlock)
            {
                return blockstate.get(StairsBlock.HALF) == Half.TOP;
            }

            if (block1 instanceof SlabBlock)
            {
                return blockstate.get(SlabBlock.TYPE) == SlabType.TOP;
            }
        }

        return false;
    }

    private static boolean checkBlock(IBlockReader blockAccess, BlockState blockState, BlockPos blockPos)
    {
        if (blockState.isOpaqueCube(blockAccess, blockPos))
        {
            return false;
        }
        else
        {
            Block block = blockState.getBlock();

            if (block == Blocks.SNOW_BLOCK)
            {
                return false;
            }
            else if (!(block instanceof BushBlock) || !(block instanceof DoublePlantBlock) && !(block instanceof FlowerBlock) && !(block instanceof MushroomBlock) && !(block instanceof SaplingBlock) && !(block instanceof TallGrassBlock))
            {
                if (!(block instanceof FenceBlock) && !(block instanceof FenceGateBlock) && !(block instanceof FlowerPotBlock) && !(block instanceof FourWayBlock) && !(block instanceof SugarCaneBlock) && !(block instanceof WallBlock))
                {
                    if (block instanceof RedstoneTorchBlock)
                    {
                        return true;
                    }
                    else if (block instanceof StairsBlock)
                    {
                        return blockState.get(StairsBlock.HALF) == Half.TOP;
                    }
                    else if (block instanceof SlabBlock)
                    {
                        return blockState.get(SlabBlock.TYPE) == SlabType.TOP;
                    }
                    else if (block instanceof AbstractButtonBlock)
                    {
                        return blockState.get(AbstractButtonBlock.FACE) != AttachFace.FLOOR;
                    }
                    else if (block instanceof HopperBlock)
                    {
                        return true;
                    }
                    else if (block instanceof LadderBlock)
                    {
                        return true;
                    }
                    else if (block instanceof LeverBlock)
                    {
                        return true;
                    }
                    else if (block instanceof TurtleEggBlock)
                    {
                        return true;
                    }
                    else
                    {
                        return block instanceof VineBlock;
                    }
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }
    }
}
