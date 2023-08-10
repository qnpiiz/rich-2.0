package net.minecraft.block;

import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TargetBlock extends Block
{
    private static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;

    public TargetBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(POWER, Integer.valueOf(0)));
    }

    public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile)
    {
        int i = getPowerFromHitVec(worldIn, state, hit, projectile);
        Entity entity = projectile.func_234616_v_();

        if (entity instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entity;
            serverplayerentity.addStat(Stats.field_232863_aD_);
            CriteriaTriggers.TARGET_HIT.test(serverplayerentity, projectile, hit.getHitVec(), i);
        }
    }

    private static int getPowerFromHitVec(IWorld world, BlockState state, BlockRayTraceResult result, Entity entity)
    {
        int i = getPowerFromHitVec(result, result.getHitVec());
        int j = entity instanceof AbstractArrowEntity ? 20 : 8;

        if (!world.getPendingBlockTicks().isTickScheduled(result.getPos(), state.getBlock()))
        {
            powerTarget(world, state, i, result.getPos(), j);
        }

        return i;
    }

    private static int getPowerFromHitVec(BlockRayTraceResult result, Vector3d vector)
    {
        Direction direction = result.getFace();
        double d0 = Math.abs(MathHelper.frac(vector.x) - 0.5D);
        double d1 = Math.abs(MathHelper.frac(vector.y) - 0.5D);
        double d2 = Math.abs(MathHelper.frac(vector.z) - 0.5D);
        Direction.Axis direction$axis = direction.getAxis();
        double d3;

        if (direction$axis == Direction.Axis.Y)
        {
            d3 = Math.max(d0, d2);
        }
        else if (direction$axis == Direction.Axis.Z)
        {
            d3 = Math.max(d0, d1);
        }
        else
        {
            d3 = Math.max(d1, d2);
        }

        return Math.max(1, MathHelper.ceil(15.0D * MathHelper.clamp((0.5D - d3) / 0.5D, 0.0D, 1.0D)));
    }

    private static void powerTarget(IWorld world, BlockState state, int power, BlockPos pos, int waitTime)
    {
        world.setBlockState(pos, state.with(POWER, Integer.valueOf(power)), 3);
        world.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), waitTime);
    }

    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (state.get(POWER) != 0)
        {
            worldIn.setBlockState(pos, state.with(POWER, Integer.valueOf(0)), 3);
        }
    }

    /**
     * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        return blockState.get(POWER);
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
     */
    public boolean canProvidePower(BlockState state)
    {
        return true;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(POWER);
    }

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if (!worldIn.isRemote() && !state.isIn(oldState.getBlock()))
        {
            if (state.get(POWER) > 0 && !worldIn.getPendingBlockTicks().isTickScheduled(pos, this))
            {
                worldIn.setBlockState(pos, state.with(POWER, Integer.valueOf(0)), 18);
            }
        }
    }
}
