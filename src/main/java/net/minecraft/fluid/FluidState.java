package net.minecraft.fluid;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.Property;
import net.minecraft.state.StateHolder;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public final class FluidState extends StateHolder<Fluid, FluidState>
{
    public static final Codec<FluidState> field_237213_a_ = func_235897_a_(Registry.FLUID, Fluid::getDefaultState).stable();

    public FluidState(Fluid p_i232145_1_, ImmutableMap < Property<?>, Comparable<? >> p_i232145_2_, MapCodec<FluidState> p_i232145_3_)
    {
        super(p_i232145_1_, p_i232145_2_, p_i232145_3_);
    }

    public Fluid getFluid()
    {
        return this.instance;
    }

    public boolean isSource()
    {
        return this.getFluid().isSource(this);
    }

    public boolean isEmpty()
    {
        return this.getFluid().isEmpty();
    }

    public float getActualHeight(IBlockReader p_215679_1_, BlockPos p_215679_2_)
    {
        return this.getFluid().getActualHeight(this, p_215679_1_, p_215679_2_);
    }

    public float getHeight()
    {
        return this.getFluid().getHeight(this);
    }

    public int getLevel()
    {
        return this.getFluid().getLevel(this);
    }

    public boolean shouldRenderSides(IBlockReader worldIn, BlockPos pos)
    {
        for (int i = -1; i <= 1; ++i)
        {
            for (int j = -1; j <= 1; ++j)
            {
                BlockPos blockpos = pos.add(i, 0, j);
                FluidState fluidstate = worldIn.getFluidState(blockpos);

                if (!fluidstate.getFluid().isEquivalentTo(this.getFluid()) && !worldIn.getBlockState(blockpos).isOpaqueCube(worldIn, blockpos))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public void tick(World worldIn, BlockPos pos)
    {
        this.getFluid().tick(worldIn, pos, this);
    }

    public void animateTick(World p_206881_1_, BlockPos p_206881_2_, Random p_206881_3_)
    {
        this.getFluid().animateTick(p_206881_1_, p_206881_2_, this, p_206881_3_);
    }

    public boolean ticksRandomly()
    {
        return this.getFluid().ticksRandomly();
    }

    public void randomTick(World worldIn, BlockPos pos, Random random)
    {
        this.getFluid().randomTick(worldIn, pos, this, random);
    }

    public Vector3d getFlow(IBlockReader p_215673_1_, BlockPos p_215673_2_)
    {
        return this.getFluid().getFlow(p_215673_1_, p_215673_2_, this);
    }

    public BlockState getBlockState()
    {
        return this.getFluid().getBlockState(this);
    }

    @Nullable
    public IParticleData getDripParticleData()
    {
        return this.getFluid().getDripParticleData();
    }

    public boolean isTagged(ITag<Fluid> tagIn)
    {
        return this.getFluid().isIn(tagIn);
    }

    public float getExplosionResistance()
    {
        return this.getFluid().getExplosionResistance();
    }

    public boolean canDisplace(IBlockReader p_215677_1_, BlockPos p_215677_2_, Fluid p_215677_3_, Direction p_215677_4_)
    {
        return this.getFluid().canDisplace(this, p_215677_1_, p_215677_2_, p_215677_3_, p_215677_4_);
    }

    public VoxelShape getShape(IBlockReader p_215676_1_, BlockPos p_215676_2_)
    {
        return this.getFluid().func_215664_b(this, p_215676_1_, p_215676_2_);
    }
}
