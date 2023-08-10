package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class DragonEggBlock extends FallingBlock
{
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public DragonEggBlock(AbstractBlock.Properties properties)
    {
        super(properties);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        this.teleport(state, worldIn, pos);
        return ActionResultType.func_233537_a_(worldIn.isRemote);
    }

    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player)
    {
        this.teleport(state, worldIn, pos);
    }

    private void teleport(BlockState state, World world, BlockPos pos)
    {
        for (int i = 0; i < 1000; ++i)
        {
            BlockPos blockpos = pos.add(world.rand.nextInt(16) - world.rand.nextInt(16), world.rand.nextInt(8) - world.rand.nextInt(8), world.rand.nextInt(16) - world.rand.nextInt(16));

            if (world.getBlockState(blockpos).isAir())
            {
                if (world.isRemote)
                {
                    for (int j = 0; j < 128; ++j)
                    {
                        double d0 = world.rand.nextDouble();
                        float f = (world.rand.nextFloat() - 0.5F) * 0.2F;
                        float f1 = (world.rand.nextFloat() - 0.5F) * 0.2F;
                        float f2 = (world.rand.nextFloat() - 0.5F) * 0.2F;
                        double d1 = MathHelper.lerp(d0, (double)blockpos.getX(), (double)pos.getX()) + (world.rand.nextDouble() - 0.5D) + 0.5D;
                        double d2 = MathHelper.lerp(d0, (double)blockpos.getY(), (double)pos.getY()) + world.rand.nextDouble() - 0.5D;
                        double d3 = MathHelper.lerp(d0, (double)blockpos.getZ(), (double)pos.getZ()) + (world.rand.nextDouble() - 0.5D) + 0.5D;
                        world.addParticle(ParticleTypes.PORTAL, d1, d2, d3, (double)f, (double)f1, (double)f2);
                    }
                }
                else
                {
                    world.setBlockState(blockpos, state, 2);
                    world.removeBlock(pos, false);
                }

                return;
            }
        }
    }

    protected int getFallDelay()
    {
        return 5;
    }

    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        return false;
    }
}
