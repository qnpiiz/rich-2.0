package net.minecraft.world;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;

public interface IBlockReader
{
    @Nullable
    TileEntity getTileEntity(BlockPos pos);

    BlockState getBlockState(BlockPos pos);

    FluidState getFluidState(BlockPos pos);

default int getLightValue(BlockPos pos)
    {
        return this.getBlockState(pos).getLightValue();
    }

default int getMaxLightLevel()
    {
        return 15;
    }

default int getHeight()
    {
        return 256;
    }

default Stream<BlockState> func_234853_a_(AxisAlignedBB p_234853_1_)
    {
        return BlockPos.getAllInBox(p_234853_1_).map(this::getBlockState);
    }

default BlockRayTraceResult rayTraceBlocks(RayTraceContext context)
    {
        return doRayTrace(context, (p_217297_1_, p_217297_2_) ->
        {
            BlockState blockstate = this.getBlockState(p_217297_2_);
            FluidState fluidstate = this.getFluidState(p_217297_2_);
            Vector3d vector3d = p_217297_1_.getStartVec();
            Vector3d vector3d1 = p_217297_1_.getEndVec();
            VoxelShape voxelshape = p_217297_1_.getBlockShape(blockstate, this, p_217297_2_);
            BlockRayTraceResult blockraytraceresult = this.rayTraceBlocks(vector3d, vector3d1, p_217297_2_, voxelshape, blockstate);
            VoxelShape voxelshape1 = p_217297_1_.getFluidShape(fluidstate, this, p_217297_2_);
            BlockRayTraceResult blockraytraceresult1 = voxelshape1.rayTrace(vector3d, vector3d1, p_217297_2_);
            double d0 = blockraytraceresult == null ? Double.MAX_VALUE : p_217297_1_.getStartVec().squareDistanceTo(blockraytraceresult.getHitVec());
            double d1 = blockraytraceresult1 == null ? Double.MAX_VALUE : p_217297_1_.getStartVec().squareDistanceTo(blockraytraceresult1.getHitVec());
            return d0 <= d1 ? blockraytraceresult : blockraytraceresult1;
        }, (p_217302_0_) ->
        {
            Vector3d vector3d = p_217302_0_.getStartVec().subtract(p_217302_0_.getEndVec());
            return BlockRayTraceResult.createMiss(p_217302_0_.getEndVec(), Direction.getFacingFromVector(vector3d.x, vector3d.y, vector3d.z), new BlockPos(p_217302_0_.getEndVec()));
        });
    }

    @Nullable

default BlockRayTraceResult rayTraceBlocks(Vector3d startVec, Vector3d endVec, BlockPos pos, VoxelShape shape, BlockState state)
    {
        BlockRayTraceResult blockraytraceresult = shape.rayTrace(startVec, endVec, pos);

        if (blockraytraceresult != null)
        {
            BlockRayTraceResult blockraytraceresult1 = state.getRayTraceShape(this, pos).rayTrace(startVec, endVec, pos);

            if (blockraytraceresult1 != null && blockraytraceresult1.getHitVec().subtract(startVec).lengthSquared() < blockraytraceresult.getHitVec().subtract(startVec).lengthSquared())
            {
                return blockraytraceresult.withFace(blockraytraceresult1.getFace());
            }
        }

        return blockraytraceresult;
    }

default double func_242402_a(VoxelShape p_242402_1_, Supplier<VoxelShape> p_242402_2_)
    {
        if (!p_242402_1_.isEmpty())
        {
            return p_242402_1_.getEnd(Direction.Axis.Y);
        }
        else
        {
            double d0 = p_242402_2_.get().getEnd(Direction.Axis.Y);
            return d0 >= 1.0D ? d0 - 1.0D : Double.NEGATIVE_INFINITY;
        }
    }

default double func_242403_h(BlockPos p_242403_1_)
    {
        return this.func_242402_a(this.getBlockState(p_242403_1_).getCollisionShape(this, p_242403_1_), () ->
        {
            BlockPos blockpos = p_242403_1_.down();
            return this.getBlockState(blockpos).getCollisionShape(this, blockpos);
        });
    }

    static <T> T doRayTrace(RayTraceContext context, BiFunction<RayTraceContext, BlockPos, T> rayTracer, Function<RayTraceContext, T> missFactory)
    {
        Vector3d vector3d = context.getStartVec();
        Vector3d vector3d1 = context.getEndVec();

        if (vector3d.equals(vector3d1))
        {
            return missFactory.apply(context);
        }
        else
        {
            double d0 = MathHelper.lerp(-1.0E-7D, vector3d1.x, vector3d.x);
            double d1 = MathHelper.lerp(-1.0E-7D, vector3d1.y, vector3d.y);
            double d2 = MathHelper.lerp(-1.0E-7D, vector3d1.z, vector3d.z);
            double d3 = MathHelper.lerp(-1.0E-7D, vector3d.x, vector3d1.x);
            double d4 = MathHelper.lerp(-1.0E-7D, vector3d.y, vector3d1.y);
            double d5 = MathHelper.lerp(-1.0E-7D, vector3d.z, vector3d1.z);
            int i = MathHelper.floor(d3);
            int j = MathHelper.floor(d4);
            int k = MathHelper.floor(d5);
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(i, j, k);
            T t = rayTracer.apply(context, blockpos$mutable);

            if (t != null)
            {
                return t;
            }
            else
            {
                double d6 = d0 - d3;
                double d7 = d1 - d4;
                double d8 = d2 - d5;
                int l = MathHelper.signum(d6);
                int i1 = MathHelper.signum(d7);
                int j1 = MathHelper.signum(d8);
                double d9 = l == 0 ? Double.MAX_VALUE : (double)l / d6;
                double d10 = i1 == 0 ? Double.MAX_VALUE : (double)i1 / d7;
                double d11 = j1 == 0 ? Double.MAX_VALUE : (double)j1 / d8;
                double d12 = d9 * (l > 0 ? 1.0D - MathHelper.frac(d3) : MathHelper.frac(d3));
                double d13 = d10 * (i1 > 0 ? 1.0D - MathHelper.frac(d4) : MathHelper.frac(d4));
                double d14 = d11 * (j1 > 0 ? 1.0D - MathHelper.frac(d5) : MathHelper.frac(d5));

                while (d12 <= 1.0D || d13 <= 1.0D || d14 <= 1.0D)
                {
                    if (d12 < d13)
                    {
                        if (d12 < d14)
                        {
                            i += l;
                            d12 += d9;
                        }
                        else
                        {
                            k += j1;
                            d14 += d11;
                        }
                    }
                    else if (d13 < d14)
                    {
                        j += i1;
                        d13 += d10;
                    }
                    else
                    {
                        k += j1;
                        d14 += d11;
                    }

                    T t1 = rayTracer.apply(context, blockpos$mutable.setPos(i, j, k));

                    if (t1 != null)
                    {
                        return t1;
                    }
                }

                return missFactory.apply(context);
            }
        }
    }
}
