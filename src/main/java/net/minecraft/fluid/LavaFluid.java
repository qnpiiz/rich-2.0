package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class LavaFluid extends FlowingFluid
{
    public Fluid getFlowingFluid()
    {
        return Fluids.FLOWING_LAVA;
    }

    public Fluid getStillFluid()
    {
        return Fluids.LAVA;
    }

    public Item getFilledBucket()
    {
        return Items.LAVA_BUCKET;
    }

    public void animateTick(World worldIn, BlockPos pos, FluidState state, Random random)
    {
        BlockPos blockpos = pos.up();

        if (worldIn.getBlockState(blockpos).isAir() && !worldIn.getBlockState(blockpos).isOpaqueCube(worldIn, blockpos))
        {
            if (random.nextInt(100) == 0)
            {
                double d0 = (double)pos.getX() + random.nextDouble();
                double d1 = (double)pos.getY() + 1.0D;
                double d2 = (double)pos.getZ() + random.nextDouble();
                worldIn.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }

            if (random.nextInt(200) == 0)
            {
                worldIn.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        }
    }

    public void randomTick(World world, BlockPos pos, FluidState state, Random random)
    {
        if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK))
        {
            int i = random.nextInt(3);

            if (i > 0)
            {
                BlockPos blockpos = pos;

                for (int j = 0; j < i; ++j)
                {
                    blockpos = blockpos.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);

                    if (!world.isBlockPresent(blockpos))
                    {
                        return;
                    }

                    BlockState blockstate = world.getBlockState(blockpos);

                    if (blockstate.isAir())
                    {
                        if (this.isSurroundingBlockFlammable(world, blockpos))
                        {
                            world.setBlockState(blockpos, AbstractFireBlock.getFireForPlacement(world, blockpos));
                            return;
                        }
                    }
                    else if (blockstate.getMaterial().blocksMovement())
                    {
                        return;
                    }
                }
            }
            else
            {
                for (int k = 0; k < 3; ++k)
                {
                    BlockPos blockpos1 = pos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);

                    if (!world.isBlockPresent(blockpos1))
                    {
                        return;
                    }

                    if (world.isAirBlock(blockpos1.up()) && this.getCanBlockBurn(world, blockpos1))
                    {
                        world.setBlockState(blockpos1.up(), AbstractFireBlock.getFireForPlacement(world, blockpos1));
                    }
                }
            }
        }
    }

    private boolean isSurroundingBlockFlammable(IWorldReader worldIn, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            if (this.getCanBlockBurn(worldIn, pos.offset(direction)))
            {
                return true;
            }
        }

        return false;
    }

    private boolean getCanBlockBurn(IWorldReader worldIn, BlockPos pos)
    {
        return pos.getY() >= 0 && pos.getY() < 256 && !worldIn.isBlockLoaded(pos) ? false : worldIn.getBlockState(pos).getMaterial().isFlammable();
    }

    @Nullable
    public IParticleData getDripParticleData()
    {
        return ParticleTypes.DRIPPING_LAVA;
    }

    protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state)
    {
        this.triggerEffects(worldIn, pos);
    }

    public int getSlopeFindDistance(IWorldReader worldIn)
    {
        return worldIn.getDimensionType().isUltrawarm() ? 4 : 2;
    }

    public BlockState getBlockState(FluidState state)
    {
        return Blocks.LAVA.getDefaultState().with(FlowingFluidBlock.LEVEL, Integer.valueOf(getLevelFromState(state)));
    }

    public boolean isEquivalentTo(Fluid fluidIn)
    {
        return fluidIn == Fluids.LAVA || fluidIn == Fluids.FLOWING_LAVA;
    }

    public int getLevelDecreasePerBlock(IWorldReader worldIn)
    {
        return worldIn.getDimensionType().isUltrawarm() ? 1 : 2;
    }

    public boolean canDisplace(FluidState fluidState, IBlockReader blockReader, BlockPos pos, Fluid fluid, Direction direction)
    {
        return fluidState.getActualHeight(blockReader, pos) >= 0.44444445F && fluid.isIn(FluidTags.WATER);
    }

    public int getTickRate(IWorldReader p_205569_1_)
    {
        return p_205569_1_.getDimensionType().isUltrawarm() ? 10 : 30;
    }

    public int func_215667_a(World world, BlockPos pos, FluidState p_215667_3_, FluidState p_215667_4_)
    {
        int i = this.getTickRate(world);

        if (!p_215667_3_.isEmpty() && !p_215667_4_.isEmpty() && !p_215667_3_.get(FALLING) && !p_215667_4_.get(FALLING) && p_215667_4_.getActualHeight(world, pos) > p_215667_3_.getActualHeight(world, pos) && world.getRandom().nextInt(4) != 0)
        {
            i *= 4;
        }

        return i;
    }

    private void triggerEffects(IWorld world, BlockPos pos)
    {
        world.playEvent(1501, pos, 0);
    }

    protected boolean canSourcesMultiply()
    {
        return false;
    }

    protected void flowInto(IWorld worldIn, BlockPos pos, BlockState blockStateIn, Direction direction, FluidState fluidStateIn)
    {
        if (direction == Direction.DOWN)
        {
            FluidState fluidstate = worldIn.getFluidState(pos);

            if (this.isIn(FluidTags.LAVA) && fluidstate.isTagged(FluidTags.WATER))
            {
                if (blockStateIn.getBlock() instanceof FlowingFluidBlock)
                {
                    worldIn.setBlockState(pos, Blocks.STONE.getDefaultState(), 3);
                }

                this.triggerEffects(worldIn, pos);
                return;
            }
        }

        super.flowInto(worldIn, pos, blockStateIn, direction, fluidStateIn);
    }

    protected boolean ticksRandomly()
    {
        return true;
    }

    protected float getExplosionResistance()
    {
        return 100.0F;
    }

    public static class Flowing extends LavaFluid
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

    public static class Source extends LavaFluid
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
