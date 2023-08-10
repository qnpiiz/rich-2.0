package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class FlowingFluidBlock extends Block implements IBucketPickupHandler
{
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_0_15;
    protected final FlowingFluid fluid;
    private final List<FluidState> fluidStatesCache;
    public static final VoxelShape LAVA_COLLISION_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);

    protected FlowingFluidBlock(FlowingFluid fluidIn, AbstractBlock.Properties builder)
    {
        super(builder);
        this.fluid = fluidIn;
        this.fluidStatesCache = Lists.newArrayList();
        this.fluidStatesCache.add(fluidIn.getStillFluidState(false));

        for (int i = 1; i < 8; ++i)
        {
            this.fluidStatesCache.add(fluidIn.getFlowingFluidState(8 - i, false));
        }

        this.fluidStatesCache.add(fluidIn.getFlowingFluidState(8, true));
        this.setDefaultState(this.stateContainer.getBaseState().with(LEVEL, Integer.valueOf(0)));
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return context.func_216378_a(LAVA_COLLISION_SHAPE, pos, true) && state.get(LEVEL) == 0 && context.func_230426_a_(worldIn.getFluidState(pos.up()), this.fluid) ? LAVA_COLLISION_SHAPE : VoxelShapes.empty();
    }

    /**
     * Returns whether or not this block is of a type that needs random ticking. Called for ref-counting purposes by
     * ExtendedBlockStorage in order to broadly cull a chunk from the random chunk update list for efficiency's sake.
     */
    public boolean ticksRandomly(BlockState state)
    {
        return state.getFluidState().ticksRandomly();
    }

    /**
     * Performs a random tick on a block.
     */
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        state.getFluidState().randomTick(worldIn, pos, random);
    }

    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return false;
    }

    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        return !this.fluid.isIn(FluidTags.LAVA);
    }

    public FluidState getFluidState(BlockState state)
    {
        int i = state.get(LEVEL);
        return this.fluidStatesCache.get(Math.min(i, 8));
    }

    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side)
    {
        return adjacentBlockState.getFluidState().getFluid().isEquivalentTo(this.fluid);
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
     */
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.INVISIBLE;
    }

    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return Collections.emptyList();
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.empty();
    }

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if (this.reactWithNeighbors(worldIn, pos, state))
        {
            worldIn.getPendingFluidTicks().scheduleTick(pos, state.getFluidState().getFluid(), this.fluid.getTickRate(worldIn));
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
        if (stateIn.getFluidState().isSource() || facingState.getFluidState().isSource())
        {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, stateIn.getFluidState().getFluid(), this.fluid.getTickRate(worldIn));
        }

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (this.reactWithNeighbors(worldIn, pos, state))
        {
            worldIn.getPendingFluidTicks().scheduleTick(pos, state.getFluidState().getFluid(), this.fluid.getTickRate(worldIn));
        }
    }

    private boolean reactWithNeighbors(World worldIn, BlockPos pos, BlockState state)
    {
        if (this.fluid.isIn(FluidTags.LAVA))
        {
            boolean flag = worldIn.getBlockState(pos.down()).isIn(Blocks.SOUL_SOIL);

            for (Direction direction : Direction.values())
            {
                if (direction != Direction.DOWN)
                {
                    BlockPos blockpos = pos.offset(direction);

                    if (worldIn.getFluidState(blockpos).isTagged(FluidTags.WATER))
                    {
                        Block block = worldIn.getFluidState(pos).isSource() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
                        worldIn.setBlockState(pos, block.getDefaultState());
                        this.triggerMixEffects(worldIn, pos);
                        return false;
                    }

                    if (flag && worldIn.getBlockState(blockpos).isIn(Blocks.BLUE_ICE))
                    {
                        worldIn.setBlockState(pos, Blocks.BASALT.getDefaultState());
                        this.triggerMixEffects(worldIn, pos);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void triggerMixEffects(IWorld worldIn, BlockPos pos)
    {
        worldIn.playEvent(1501, pos, 0);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(LEVEL);
    }

    public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state)
    {
        if (state.get(LEVEL) == 0)
        {
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
            return this.fluid;
        }
        else
        {
            return Fluids.EMPTY;
        }
    }
}
