package net.minecraft.block;

import java.util.Random;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractTopPlantBlock extends AbstractPlantBlock implements IGrowable
{
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_25;
    private final double growthChance;

    protected AbstractTopPlantBlock(AbstractBlock.Properties properties, Direction direction, VoxelShape shape, boolean waterloggable, double growthChance)
    {
        super(properties, direction, shape, waterloggable);
        this.growthChance = growthChance;
        this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
    }

    public BlockState grow(IWorld world)
    {
        return this.getDefaultState().with(AGE, Integer.valueOf(world.getRandom().nextInt(25)));
    }

    /**
     * Returns whether or not this block is of a type that needs random ticking. Called for ref-counting purposes by
     * ExtendedBlockStorage in order to broadly cull a chunk from the random chunk update list for efficiency's sake.
     */
    public boolean ticksRandomly(BlockState state)
    {
        return state.get(AGE) < 25;
    }

    /**
     * Performs a random tick on a block.
     */
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        if (state.get(AGE) < 25 && random.nextDouble() < this.growthChance)
        {
            BlockPos blockpos = pos.offset(this.growthDirection);

            if (this.canGrowIn(worldIn.getBlockState(blockpos)))
            {
                worldIn.setBlockState(blockpos, state.func_235896_a_(AGE));
            }
        }
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder
     * immediately returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == this.growthDirection.getOpposite() && !stateIn.isValidPosition(worldIn, currentPos))
        {
            worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
        }

        if (facing != this.growthDirection || !facingState.isIn(this) && !facingState.isIn(this.getBodyPlantBlock()))
        {
            if (this.breaksInWater)
            {
                worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
            }

            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
        else
        {
            return this.getBodyPlantBlock().getDefaultState();
        }
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(AGE);
    }

    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        return this.canGrowIn(worldIn.getBlockState(pos.offset(this.growthDirection)));
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state)
    {
        return true;
    }

    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state)
    {
        BlockPos blockpos = pos.offset(this.growthDirection);
        int i = Math.min(state.get(AGE) + 1, 25);
        int j = this.getGrowthAmount(rand);

        for (int k = 0; k < j && this.canGrowIn(worldIn.getBlockState(blockpos)); ++k)
        {
            worldIn.setBlockState(blockpos, state.with(AGE, Integer.valueOf(i)));
            blockpos = blockpos.offset(this.growthDirection);
            i = Math.min(i + 1, 25);
        }
    }

    /**
     * Used to determine how much to grow the plant when using bonemeal. Kelp always returns 1, where as the nether
     * vines return a random value at least 1.
     */
    protected abstract int getGrowthAmount(Random rand);

    protected abstract boolean canGrowIn(BlockState state);

    protected AbstractTopPlantBlock getTopPlantBlock()
    {
        return this;
    }
}
