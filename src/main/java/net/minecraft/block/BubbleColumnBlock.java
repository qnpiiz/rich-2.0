package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BubbleColumnBlock extends Block implements IBucketPickupHandler
{
    public static final BooleanProperty DRAG = BlockStateProperties.DRAG;

    public BubbleColumnBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(DRAG, Boolean.valueOf(true)));
    }

    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        BlockState blockstate = worldIn.getBlockState(pos.up());

        if (blockstate.isAir())
        {
            entityIn.onEnterBubbleColumnWithAirAbove(state.get(DRAG));

            if (!worldIn.isRemote)
            {
                ServerWorld serverworld = (ServerWorld)worldIn;

                for (int i = 0; i < 2; ++i)
                {
                    serverworld.spawnParticle(ParticleTypes.SPLASH, (double)pos.getX() + worldIn.rand.nextDouble(), (double)(pos.getY() + 1), (double)pos.getZ() + worldIn.rand.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    serverworld.spawnParticle(ParticleTypes.BUBBLE, (double)pos.getX() + worldIn.rand.nextDouble(), (double)(pos.getY() + 1), (double)pos.getZ() + worldIn.rand.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
                }
            }
        }
        else
        {
            entityIn.onEnterBubbleColumn(state.get(DRAG));
        }
    }

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        placeBubbleColumn(worldIn, pos.up(), getDrag(worldIn, pos.down()));
    }

    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        placeBubbleColumn(worldIn, pos.up(), getDrag(worldIn, pos));
    }

    public FluidState getFluidState(BlockState state)
    {
        return Fluids.WATER.getStillFluidState(false);
    }

    public static void placeBubbleColumn(IWorld world, BlockPos pos, boolean drag)
    {
        if (canHoldBubbleColumn(world, pos))
        {
            world.setBlockState(pos, Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, Boolean.valueOf(drag)), 2);
        }
    }

    public static boolean canHoldBubbleColumn(IWorld world, BlockPos pos)
    {
        FluidState fluidstate = world.getFluidState(pos);
        return world.getBlockState(pos).isIn(Blocks.WATER) && fluidstate.getLevel() >= 8 && fluidstate.isSource();
    }

    private static boolean getDrag(IBlockReader reader, BlockPos pos)
    {
        BlockState blockstate = reader.getBlockState(pos);

        if (blockstate.isIn(Blocks.BUBBLE_COLUMN))
        {
            return blockstate.get(DRAG);
        }
        else
        {
            return !blockstate.isIn(Blocks.SOUL_SAND);
        }
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        double d0 = (double)pos.getX();
        double d1 = (double)pos.getY();
        double d2 = (double)pos.getZ();

        if (stateIn.get(DRAG))
        {
            worldIn.addOptionalParticle(ParticleTypes.CURRENT_DOWN, d0 + 0.5D, d1 + 0.8D, d2, 0.0D, 0.0D, 0.0D);

            if (rand.nextInt(200) == 0)
            {
                worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
            }
        }
        else
        {
            worldIn.addOptionalParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0 + 0.5D, d1, d2 + 0.5D, 0.0D, 0.04D, 0.0D);
            worldIn.addOptionalParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0 + (double)rand.nextFloat(), d1 + (double)rand.nextFloat(), d2 + (double)rand.nextFloat(), 0.0D, 0.04D, 0.0D);

            if (rand.nextInt(200) == 0)
            {
                worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
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
        if (!stateIn.isValidPosition(worldIn, currentPos))
        {
            return Blocks.WATER.getDefaultState();
        }
        else
        {
            if (facing == Direction.DOWN)
            {
                worldIn.setBlockState(currentPos, Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, Boolean.valueOf(getDrag(worldIn, facingPos))), 2);
            }
            else if (facing == Direction.UP && !facingState.isIn(Blocks.BUBBLE_COLUMN) && canHoldBubbleColumn(worldIn, facingPos))
            {
                worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 5);
            }

            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockState blockstate = worldIn.getBlockState(pos.down());
        return blockstate.isIn(Blocks.BUBBLE_COLUMN) || blockstate.isIn(Blocks.MAGMA_BLOCK) || blockstate.isIn(Blocks.SOUL_SAND);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.empty();
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

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(DRAG);
    }

    public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state)
    {
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
        return Fluids.WATER;
    }
}
