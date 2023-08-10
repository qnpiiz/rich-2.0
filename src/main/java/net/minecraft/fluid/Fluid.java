package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class Fluid
{
    public static final ObjectIntIdentityMap<FluidState> STATE_REGISTRY = new ObjectIntIdentityMap<>();
    protected final StateContainer<Fluid, FluidState> stateContainer;
    private FluidState defaultState;

    protected Fluid()
    {
        StateContainer.Builder<Fluid, FluidState> builder = new StateContainer.Builder<>(this);
        this.fillStateContainer(builder);
        this.stateContainer = builder.func_235882_a_(Fluid::getDefaultState, FluidState::new);
        this.setDefaultState(this.stateContainer.getBaseState());
    }

    protected void fillStateContainer(StateContainer.Builder<Fluid, FluidState> builder)
    {
    }

    public StateContainer<Fluid, FluidState> getStateContainer()
    {
        return this.stateContainer;
    }

    protected final void setDefaultState(FluidState state)
    {
        this.defaultState = state;
    }

    public final FluidState getDefaultState()
    {
        return this.defaultState;
    }

    public abstract Item getFilledBucket();

    protected void animateTick(World worldIn, BlockPos pos, FluidState state, Random random)
    {
    }

    protected void tick(World worldIn, BlockPos pos, FluidState state)
    {
    }

    protected void randomTick(World world, BlockPos pos, FluidState state, Random random)
    {
    }

    @Nullable
    protected IParticleData getDripParticleData()
    {
        return null;
    }

    protected abstract boolean canDisplace(FluidState fluidState, IBlockReader blockReader, BlockPos pos, Fluid fluid, Direction direction);

    protected abstract Vector3d getFlow(IBlockReader blockReader, BlockPos pos, FluidState fluidState);

    public abstract int getTickRate(IWorldReader p_205569_1_);

    protected boolean ticksRandomly()
    {
        return false;
    }

    protected boolean isEmpty()
    {
        return false;
    }

    protected abstract float getExplosionResistance();

    public abstract float getActualHeight(FluidState p_215662_1_, IBlockReader p_215662_2_, BlockPos p_215662_3_);

    public abstract float getHeight(FluidState p_223407_1_);

    protected abstract BlockState getBlockState(FluidState state);

    public abstract boolean isSource(FluidState state);

    public abstract int getLevel(FluidState state);

    public boolean isEquivalentTo(Fluid fluidIn)
    {
        return fluidIn == this;
    }

    public boolean isIn(ITag<Fluid> tagIn)
    {
        return tagIn.contains(this);
    }

    public abstract VoxelShape func_215664_b(FluidState p_215664_1_, IBlockReader p_215664_2_, BlockPos p_215664_3_);
}
