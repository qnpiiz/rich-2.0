package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class RedstoneTorchBlock extends TorchBlock
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private static final Map<IBlockReader, List<RedstoneTorchBlock.Toggle>> BURNED_TORCHES = new WeakHashMap<>();

    protected RedstoneTorchBlock(AbstractBlock.Properties properties)
    {
        super(properties, RedstoneParticleData.REDSTONE_DUST);
        this.setDefaultState(this.stateContainer.getBaseState().with(LIT, Boolean.valueOf(true)));
    }

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        for (Direction direction : Direction.values())
        {
            worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
        }
    }

    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!isMoving)
        {
            for (Direction direction : Direction.values())
            {
                worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
            }
        }
    }

    /**
     * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        return blockState.get(LIT) && Direction.UP != side ? 15 : 0;
    }

    protected boolean shouldBeOff(World worldIn, BlockPos pos, BlockState state)
    {
        return worldIn.isSidePowered(pos.down(), Direction.DOWN);
    }

    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        boolean flag = this.shouldBeOff(worldIn, pos, state);
        List<RedstoneTorchBlock.Toggle> list = BURNED_TORCHES.get(worldIn);

        while (list != null && !list.isEmpty() && worldIn.getGameTime() - (list.get(0)).time > 60L)
        {
            list.remove(0);
        }

        if (state.get(LIT))
        {
            if (flag)
            {
                worldIn.setBlockState(pos, state.with(LIT, Boolean.valueOf(false)), 3);

                if (isBurnedOut(worldIn, pos, true))
                {
                    worldIn.playEvent(1502, pos, 0);
                    worldIn.getPendingBlockTicks().scheduleTick(pos, worldIn.getBlockState(pos).getBlock(), 160);
                }
            }
        }
        else if (!flag && !isBurnedOut(worldIn, pos, false))
        {
            worldIn.setBlockState(pos, state.with(LIT, Boolean.valueOf(true)), 3);
        }
    }

    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (state.get(LIT) == this.shouldBeOff(worldIn, pos, state) && !worldIn.getPendingBlockTicks().isTickPending(pos, this))
        {
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 2);
        }
    }

    /**
     * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        return side == Direction.DOWN ? blockState.getWeakPower(blockAccess, pos, side) : 0;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
     */
    public boolean canProvidePower(BlockState state)
    {
        return true;
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.get(LIT))
        {
            double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
            double d1 = (double)pos.getY() + 0.7D + (rand.nextDouble() - 0.5D) * 0.2D;
            double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
            worldIn.addParticle(this.particleData, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(LIT);
    }

    private static boolean isBurnedOut(World world, BlockPos worldIn, boolean pos)
    {
        List<RedstoneTorchBlock.Toggle> list = BURNED_TORCHES.computeIfAbsent(world, (reader) ->
        {
            return Lists.newArrayList();
        });

        if (pos)
        {
            list.add(new RedstoneTorchBlock.Toggle(worldIn.toImmutable(), world.getGameTime()));
        }

        int i = 0;

        for (int j = 0; j < list.size(); ++j)
        {
            RedstoneTorchBlock.Toggle redstonetorchblock$toggle = list.get(j);

            if (redstonetorchblock$toggle.pos.equals(worldIn))
            {
                ++i;

                if (i >= 8)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static class Toggle
    {
        private final BlockPos pos;
        private final long time;

        public Toggle(BlockPos pos, long time)
        {
            this.pos = pos;
            this.time = time;
        }
    }
}
