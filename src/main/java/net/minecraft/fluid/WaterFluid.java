package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class WaterFluid extends FlowingFluid
{
    public Fluid getFlowingFluid()
    {
        return Fluids.FLOWING_WATER;
    }

    public Fluid getStillFluid()
    {
        return Fluids.WATER;
    }

    public Item getFilledBucket()
    {
        return Items.WATER_BUCKET;
    }

    public void animateTick(World worldIn, BlockPos pos, FluidState state, Random random)
    {
        if (!state.isSource() && !state.get(FALLING))
        {
            if (random.nextInt(64) == 0)
            {
                worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
            }
        }
        else if (random.nextInt(10) == 0)
        {
            worldIn.addParticle(ParticleTypes.UNDERWATER, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + random.nextDouble(), (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Nullable
    public IParticleData getDripParticleData()
    {
        return ParticleTypes.DRIPPING_WATER;
    }

    protected boolean canSourcesMultiply()
    {
        return true;
    }

    protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state)
    {
        TileEntity tileentity = state.getBlock().isTileEntityProvider() ? worldIn.getTileEntity(pos) : null;
        Block.spawnDrops(state, worldIn, pos, tileentity);
    }

    public int getSlopeFindDistance(IWorldReader worldIn)
    {
        return 4;
    }

    public BlockState getBlockState(FluidState state)
    {
        return Blocks.WATER.getDefaultState().with(FlowingFluidBlock.LEVEL, Integer.valueOf(getLevelFromState(state)));
    }

    public boolean isEquivalentTo(Fluid fluidIn)
    {
        return fluidIn == Fluids.WATER || fluidIn == Fluids.FLOWING_WATER;
    }

    public int getLevelDecreasePerBlock(IWorldReader worldIn)
    {
        return 1;
    }

    public int getTickRate(IWorldReader p_205569_1_)
    {
        return 5;
    }

    public boolean canDisplace(FluidState fluidState, IBlockReader blockReader, BlockPos pos, Fluid fluid, Direction direction)
    {
        return direction == Direction.DOWN && !fluid.isIn(FluidTags.WATER);
    }

    protected float getExplosionResistance()
    {
        return 100.0F;
    }

    public static class Flowing extends WaterFluid
    {
        protected void fillStateContainer(StateContainer.Builder<Fluid, FluidState> builder)
        {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        public int getLevel(FluidState state)
        {
            return state.get(LEVEL_1_8);
        }

        public boolean isSource(FluidState state)
        {
            return false;
        }
    }

    public static class Source extends WaterFluid
    {
        public int getLevel(FluidState state)
        {
            return 8;
        }

        public boolean isSource(FluidState state)
        {
            return true;
        }
    }
}
