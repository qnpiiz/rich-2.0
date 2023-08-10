package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractPlantBlock extends Block
{
    protected final Direction growthDirection;
    protected final boolean breaksInWater;
    protected final VoxelShape shape;

    protected AbstractPlantBlock(AbstractBlock.Properties properties, Direction growthDirection, VoxelShape shape, boolean breaksInWater)
    {
        super(properties);
        this.growthDirection = growthDirection;
        this.shape = shape;
        this.breaksInWater = breaksInWater;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState blockstate = context.getWorld().getBlockState(context.getPos().offset(this.growthDirection));
        return !blockstate.isIn(this.getTopPlantBlock()) && !blockstate.isIn(this.getBodyPlantBlock()) ? this.grow(context.getWorld()) : this.getBodyPlantBlock().getDefaultState();
    }

    public BlockState grow(IWorld world)
    {
        return this.getDefaultState();
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockPos blockpos = pos.offset(this.growthDirection.getOpposite());
        BlockState blockstate = worldIn.getBlockState(blockpos);
        Block block = blockstate.getBlock();

        if (!this.canGrowOn(block))
        {
            return false;
        }
        else
        {
            return block == this.getTopPlantBlock() || block == this.getBodyPlantBlock() || blockstate.isSolidSide(worldIn, blockpos, this.growthDirection);
        }
    }

    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (!state.isValidPosition(worldIn, pos))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    protected boolean canGrowOn(Block block)
    {
        return true;
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.shape;
    }

    protected abstract AbstractTopPlantBlock getTopPlantBlock();

    protected abstract Block getBodyPlantBlock();
}
