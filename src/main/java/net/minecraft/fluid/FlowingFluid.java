package net.minecraft.fluid;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class FlowingFluid extends Fluid
{
    public static final BooleanProperty FALLING = BlockStateProperties.FALLING;
    public static final IntegerProperty LEVEL_1_8 = BlockStateProperties.LEVEL_1_8;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>> field_212756_e = ThreadLocal.withInitial(() ->
    {
        Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>(200)
        {
            protected void rehash(int p_rehash_1_)
            {
            }
        };
        object2bytelinkedopenhashmap.defaultReturnValue((byte)127);
        return object2bytelinkedopenhashmap;
    });
    private final Map<FluidState, VoxelShape> field_215669_f = Maps.newIdentityHashMap();

    protected void fillStateContainer(StateContainer.Builder<Fluid, FluidState> builder)
    {
        builder.add(FALLING);
    }

    public Vector3d getFlow(IBlockReader blockReader, BlockPos pos, FluidState fluidState)
    {
        double d0 = 0.0D;
        double d1 = 0.0D;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            blockpos$mutable.setAndMove(pos, direction);
            FluidState fluidstate = blockReader.getFluidState(blockpos$mutable);

            if (this.isSameOrEmpty(fluidstate))
            {
                float f = fluidstate.getHeight();
                float f1 = 0.0F;

                if (f == 0.0F)
                {
                    if (!blockReader.getBlockState(blockpos$mutable).getMaterial().blocksMovement())
                    {
                        BlockPos blockpos = blockpos$mutable.down();
                        FluidState fluidstate1 = blockReader.getFluidState(blockpos);

                        if (this.isSameOrEmpty(fluidstate1))
                        {
                            f = fluidstate1.getHeight();

                            if (f > 0.0F)
                            {
                                f1 = fluidState.getHeight() - (f - 0.8888889F);
                            }
                        }
                    }
                }
                else if (f > 0.0F)
                {
                    f1 = fluidState.getHeight() - f;
                }

                if (f1 != 0.0F)
                {
                    d0 += (double)((float)direction.getXOffset() * f1);
                    d1 += (double)((float)direction.getZOffset() * f1);
                }
            }
        }

        Vector3d vector3d = new Vector3d(d0, 0.0D, d1);

        if (fluidState.get(FALLING))
        {
            for (Direction direction1 : Direction.Plane.HORIZONTAL)
            {
                blockpos$mutable.setAndMove(pos, direction1);

                if (this.causesDownwardCurrent(blockReader, blockpos$mutable, direction1) || this.causesDownwardCurrent(blockReader, blockpos$mutable.up(), direction1))
                {
                    vector3d = vector3d.normalize().add(0.0D, -6.0D, 0.0D);
                    break;
                }
            }
        }

        return vector3d.normalize();
    }

    private boolean isSameOrEmpty(FluidState state)
    {
        return state.isEmpty() || state.getFluid().isEquivalentTo(this);
    }

    protected boolean causesDownwardCurrent(IBlockReader worldIn, BlockPos neighborPos, Direction side)
    {
        BlockState blockstate = worldIn.getBlockState(neighborPos);
        FluidState fluidstate = worldIn.getFluidState(neighborPos);

        if (fluidstate.getFluid().isEquivalentTo(this))
        {
            return false;
        }
        else if (side == Direction.UP)
        {
            return true;
        }
        else
        {
            return blockstate.getMaterial() == Material.ICE ? false : blockstate.isSolidSide(worldIn, neighborPos, side);
        }
    }

    protected void flowAround(IWorld worldIn, BlockPos pos, FluidState stateIn)
    {
        if (!stateIn.isEmpty())
        {
            BlockState blockstate = worldIn.getBlockState(pos);
            BlockPos blockpos = pos.down();
            BlockState blockstate1 = worldIn.getBlockState(blockpos);
            FluidState fluidstate = this.calculateCorrectFlowingState(worldIn, blockpos, blockstate1);

            if (this.canFlow(worldIn, pos, blockstate, Direction.DOWN, blockpos, blockstate1, worldIn.getFluidState(blockpos), fluidstate.getFluid()))
            {
                this.flowInto(worldIn, blockpos, blockstate1, Direction.DOWN, fluidstate);

                if (this.getNumHorizontallyAdjacentSources(worldIn, pos) >= 3)
                {
                    this.func_207937_a(worldIn, pos, stateIn, blockstate);
                }
            }
            else if (stateIn.isSource() || !this.func_211759_a(worldIn, fluidstate.getFluid(), pos, blockstate, blockpos, blockstate1))
            {
                this.func_207937_a(worldIn, pos, stateIn, blockstate);
            }
        }
    }

    private void func_207937_a(IWorld p_207937_1_, BlockPos p_207937_2_, FluidState p_207937_3_, BlockState p_207937_4_)
    {
        int i = p_207937_3_.getLevel() - this.getLevelDecreasePerBlock(p_207937_1_);

        if (p_207937_3_.get(FALLING))
        {
            i = 7;
        }

        if (i > 0)
        {
            Map<Direction, FluidState> map = this.func_205572_b(p_207937_1_, p_207937_2_, p_207937_4_);

            for (Entry<Direction, FluidState> entry : map.entrySet())
            {
                Direction direction = entry.getKey();
                FluidState fluidstate = entry.getValue();
                BlockPos blockpos = p_207937_2_.offset(direction);
                BlockState blockstate = p_207937_1_.getBlockState(blockpos);

                if (this.canFlow(p_207937_1_, p_207937_2_, p_207937_4_, direction, blockpos, blockstate, p_207937_1_.getFluidState(blockpos), fluidstate.getFluid()))
                {
                    this.flowInto(p_207937_1_, blockpos, blockstate, direction, fluidstate);
                }
            }
        }
    }

    protected FluidState calculateCorrectFlowingState(IWorldReader worldIn, BlockPos pos, BlockState blockStateIn)
    {
        int i = 0;
        int j = 0;

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos blockpos = pos.offset(direction);
            BlockState blockstate = worldIn.getBlockState(blockpos);
            FluidState fluidstate = blockstate.getFluidState();

            if (fluidstate.getFluid().isEquivalentTo(this) && this.doesSideHaveHoles(direction, worldIn, pos, blockStateIn, blockpos, blockstate))
            {
                if (fluidstate.isSource())
                {
                    ++j;
                }

                i = Math.max(i, fluidstate.getLevel());
            }
        }

        if (this.canSourcesMultiply() && j >= 2)
        {
            BlockState blockstate1 = worldIn.getBlockState(pos.down());
            FluidState fluidstate1 = blockstate1.getFluidState();

            if (blockstate1.getMaterial().isSolid() || this.isSameAs(fluidstate1))
            {
                return this.getStillFluidState(false);
            }
        }

        BlockPos blockpos1 = pos.up();
        BlockState blockstate2 = worldIn.getBlockState(blockpos1);
        FluidState fluidstate2 = blockstate2.getFluidState();

        if (!fluidstate2.isEmpty() && fluidstate2.getFluid().isEquivalentTo(this) && this.doesSideHaveHoles(Direction.UP, worldIn, pos, blockStateIn, blockpos1, blockstate2))
        {
            return this.getFlowingFluidState(8, true);
        }
        else
        {
            int k = i - this.getLevelDecreasePerBlock(worldIn);
            return k <= 0 ? Fluids.EMPTY.getDefaultState() : this.getFlowingFluidState(k, false);
        }
    }

    private boolean doesSideHaveHoles(Direction p_212751_1_, IBlockReader p_212751_2_, BlockPos p_212751_3_, BlockState p_212751_4_, BlockPos p_212751_5_, BlockState p_212751_6_)
    {
        Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap;

        if (!p_212751_4_.getBlock().isVariableOpacity() && !p_212751_6_.getBlock().isVariableOpacity())
        {
            object2bytelinkedopenhashmap = field_212756_e.get();
        }
        else
        {
            object2bytelinkedopenhashmap = null;
        }

        Block.RenderSideCacheKey block$rendersidecachekey;

        if (object2bytelinkedopenhashmap != null)
        {
            block$rendersidecachekey = new Block.RenderSideCacheKey(p_212751_4_, p_212751_6_, p_212751_1_);
            byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block$rendersidecachekey);

            if (b0 != 127)
            {
                return b0 != 0;
            }
        }
        else
        {
            block$rendersidecachekey = null;
        }

        VoxelShape voxelshape1 = p_212751_4_.getCollisionShape(p_212751_2_, p_212751_3_);
        VoxelShape voxelshape = p_212751_6_.getCollisionShape(p_212751_2_, p_212751_5_);
        boolean flag = !VoxelShapes.doAdjacentCubeSidesFillSquare(voxelshape1, voxelshape, p_212751_1_);

        if (object2bytelinkedopenhashmap != null)
        {
            if (object2bytelinkedopenhashmap.size() == 200)
            {
                object2bytelinkedopenhashmap.removeLastByte();
            }

            object2bytelinkedopenhashmap.putAndMoveToFirst(block$rendersidecachekey, (byte)(flag ? 1 : 0));
        }

        return flag;
    }

    public abstract Fluid getFlowingFluid();

    public FluidState getFlowingFluidState(int level, boolean falling)
    {
        return this.getFlowingFluid().getDefaultState().with(LEVEL_1_8, Integer.valueOf(level)).with(FALLING, Boolean.valueOf(falling));
    }

    public abstract Fluid getStillFluid();

    public FluidState getStillFluidState(boolean falling)
    {
        return this.getStillFluid().getDefaultState().with(FALLING, Boolean.valueOf(falling));
    }

    protected abstract boolean canSourcesMultiply();

    protected void flowInto(IWorld worldIn, BlockPos pos, BlockState blockStateIn, Direction direction, FluidState fluidStateIn)
    {
        if (blockStateIn.getBlock() instanceof ILiquidContainer)
        {
            ((ILiquidContainer)blockStateIn.getBlock()).receiveFluid(worldIn, pos, blockStateIn, fluidStateIn);
        }
        else
        {
            if (!blockStateIn.isAir())
            {
                this.beforeReplacingBlock(worldIn, pos, blockStateIn);
            }

            worldIn.setBlockState(pos, fluidStateIn.getBlockState(), 3);
        }
    }

    protected abstract void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state);

    private static short func_212752_a(BlockPos p_212752_0_, BlockPos p_212752_1_)
    {
        int i = p_212752_1_.getX() - p_212752_0_.getX();
        int j = p_212752_1_.getZ() - p_212752_0_.getZ();
        return (short)((i + 128 & 255) << 8 | j + 128 & 255);
    }

    protected int func_205571_a(IWorldReader p_205571_1_, BlockPos p_205571_2_, int p_205571_3_, Direction p_205571_4_, BlockState p_205571_5_, BlockPos p_205571_6_, Short2ObjectMap<Pair<BlockState, FluidState>> p_205571_7_, Short2BooleanMap p_205571_8_)
    {
        int i = 1000;

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            if (direction != p_205571_4_)
            {
                BlockPos blockpos = p_205571_2_.offset(direction);
                short short1 = func_212752_a(p_205571_6_, blockpos);
                Pair<BlockState, FluidState> pair = p_205571_7_.computeIfAbsent(short1, (p_212748_2_) ->
                {
                    BlockState blockstate1 = p_205571_1_.getBlockState(blockpos);
                    return Pair.of(blockstate1, blockstate1.getFluidState());
                });
                BlockState blockstate = pair.getFirst();
                FluidState fluidstate = pair.getSecond();

                if (this.func_211760_a(p_205571_1_, this.getFlowingFluid(), p_205571_2_, p_205571_5_, direction, blockpos, blockstate, fluidstate))
                {
                    boolean flag = p_205571_8_.computeIfAbsent(short1, (p_212749_4_) ->
                    {
                        BlockPos blockpos1 = blockpos.down();
                        BlockState blockstate1 = p_205571_1_.getBlockState(blockpos1);
                        return this.func_211759_a(p_205571_1_, this.getFlowingFluid(), blockpos, blockstate, blockpos1, blockstate1);
                    });

                    if (flag)
                    {
                        return p_205571_3_;
                    }

                    if (p_205571_3_ < this.getSlopeFindDistance(p_205571_1_))
                    {
                        int j = this.func_205571_a(p_205571_1_, blockpos, p_205571_3_ + 1, direction.getOpposite(), blockstate, p_205571_6_, p_205571_7_, p_205571_8_);

                        if (j < i)
                        {
                            i = j;
                        }
                    }
                }
            }
        }

        return i;
    }

    private boolean func_211759_a(IBlockReader p_211759_1_, Fluid p_211759_2_, BlockPos p_211759_3_, BlockState p_211759_4_, BlockPos p_211759_5_, BlockState p_211759_6_)
    {
        if (!this.doesSideHaveHoles(Direction.DOWN, p_211759_1_, p_211759_3_, p_211759_4_, p_211759_5_, p_211759_6_))
        {
            return false;
        }
        else
        {
            return p_211759_6_.getFluidState().getFluid().isEquivalentTo(this) ? true : this.isBlocked(p_211759_1_, p_211759_5_, p_211759_6_, p_211759_2_);
        }
    }

    private boolean func_211760_a(IBlockReader p_211760_1_, Fluid p_211760_2_, BlockPos p_211760_3_, BlockState p_211760_4_, Direction p_211760_5_, BlockPos p_211760_6_, BlockState p_211760_7_, FluidState p_211760_8_)
    {
        return !this.isSameAs(p_211760_8_) && this.doesSideHaveHoles(p_211760_5_, p_211760_1_, p_211760_3_, p_211760_4_, p_211760_6_, p_211760_7_) && this.isBlocked(p_211760_1_, p_211760_6_, p_211760_7_, p_211760_2_);
    }

    private boolean isSameAs(FluidState stateIn)
    {
        return stateIn.getFluid().isEquivalentTo(this) && stateIn.isSource();
    }

    protected abstract int getSlopeFindDistance(IWorldReader worldIn);

    /**
     * Returns the number of immediately adjacent source blocks of the same fluid that lie on the horizontal plane.
     */
    private int getNumHorizontallyAdjacentSources(IWorldReader worldIn, BlockPos pos)
    {
        int i = 0;

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos blockpos = pos.offset(direction);
            FluidState fluidstate = worldIn.getFluidState(blockpos);

            if (this.isSameAs(fluidstate))
            {
                ++i;
            }
        }

        return i;
    }

    protected Map<Direction, FluidState> func_205572_b(IWorldReader p_205572_1_, BlockPos p_205572_2_, BlockState p_205572_3_)
    {
        int i = 1000;
        Map<Direction, FluidState> map = Maps.newEnumMap(Direction.class);
        Short2ObjectMap<Pair<BlockState, FluidState>> short2objectmap = new Short2ObjectOpenHashMap<>();
        Short2BooleanMap short2booleanmap = new Short2BooleanOpenHashMap();

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos blockpos = p_205572_2_.offset(direction);
            short short1 = func_212752_a(p_205572_2_, blockpos);
            Pair<BlockState, FluidState> pair = short2objectmap.computeIfAbsent(short1, (p_212755_2_) ->
            {
                BlockState blockstate1 = p_205572_1_.getBlockState(blockpos);
                return Pair.of(blockstate1, blockstate1.getFluidState());
            });
            BlockState blockstate = pair.getFirst();
            FluidState fluidstate = pair.getSecond();
            FluidState fluidstate1 = this.calculateCorrectFlowingState(p_205572_1_, blockpos, blockstate);

            if (this.func_211760_a(p_205572_1_, fluidstate1.getFluid(), p_205572_2_, p_205572_3_, direction, blockpos, blockstate, fluidstate))
            {
                BlockPos blockpos1 = blockpos.down();
                boolean flag = short2booleanmap.computeIfAbsent(short1, (p_212753_5_) ->
                {
                    BlockState blockstate1 = p_205572_1_.getBlockState(blockpos1);
                    return this.func_211759_a(p_205572_1_, this.getFlowingFluid(), blockpos, blockstate, blockpos1, blockstate1);
                });
                int j;

                if (flag)
                {
                    j = 0;
                }
                else
                {
                    j = this.func_205571_a(p_205572_1_, blockpos, 1, direction.getOpposite(), blockstate, p_205572_2_, short2objectmap, short2booleanmap);
                }

                if (j < i)
                {
                    map.clear();
                }

                if (j <= i)
                {
                    map.put(direction, fluidstate1);
                    i = j;
                }
            }
        }

        return map;
    }

    private boolean isBlocked(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn)
    {
        Block block = state.getBlock();

        if (block instanceof ILiquidContainer)
        {
            return ((ILiquidContainer)block).canContainFluid(worldIn, pos, state, fluidIn);
        }
        else if (!(block instanceof DoorBlock) && !block.isIn(BlockTags.SIGNS) && block != Blocks.LADDER && block != Blocks.SUGAR_CANE && block != Blocks.BUBBLE_COLUMN)
        {
            Material material = state.getMaterial();

            if (material != Material.PORTAL && material != Material.STRUCTURE_VOID && material != Material.OCEAN_PLANT && material != Material.SEA_GRASS)
            {
                return !material.blocksMovement();
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    protected boolean canFlow(IBlockReader worldIn, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, Fluid fluidIn)
    {
        return toFluidState.canDisplace(worldIn, toPos, fluidIn, direction) && this.doesSideHaveHoles(direction, worldIn, fromPos, fromBlockState, toPos, toBlockState) && this.isBlocked(worldIn, toPos, toBlockState, fluidIn);
    }

    protected abstract int getLevelDecreasePerBlock(IWorldReader worldIn);

    protected int func_215667_a(World world, BlockPos pos, FluidState p_215667_3_, FluidState p_215667_4_)
    {
        return this.getTickRate(world);
    }

    public void tick(World worldIn, BlockPos pos, FluidState state)
    {
        if (!state.isSource())
        {
            FluidState fluidstate = this.calculateCorrectFlowingState(worldIn, pos, worldIn.getBlockState(pos));
            int i = this.func_215667_a(worldIn, pos, state, fluidstate);

            if (fluidstate.isEmpty())
            {
                state = fluidstate;
                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            }
            else if (!fluidstate.equals(state))
            {
                state = fluidstate;
                BlockState blockstate = fluidstate.getBlockState();
                worldIn.setBlockState(pos, blockstate, 2);
                worldIn.getPendingFluidTicks().scheduleTick(pos, fluidstate.getFluid(), i);
                worldIn.notifyNeighborsOfStateChange(pos, blockstate.getBlock());
            }
        }

        this.flowAround(worldIn, pos, state);
    }

    protected static int getLevelFromState(FluidState state)
    {
        return state.isSource() ? 0 : 8 - Math.min(state.getLevel(), 8) + (state.get(FALLING) ? 8 : 0);
    }

    private static boolean isFullHeight(FluidState p_215666_0_, IBlockReader p_215666_1_, BlockPos p_215666_2_)
    {
        return p_215666_0_.getFluid().isEquivalentTo(p_215666_1_.getFluidState(p_215666_2_.up()).getFluid());
    }

    public float getActualHeight(FluidState p_215662_1_, IBlockReader p_215662_2_, BlockPos p_215662_3_)
    {
        return isFullHeight(p_215662_1_, p_215662_2_, p_215662_3_) ? 1.0F : p_215662_1_.getHeight();
    }

    public float getHeight(FluidState p_223407_1_)
    {
        return (float)p_223407_1_.getLevel() / 9.0F;
    }

    public VoxelShape func_215664_b(FluidState p_215664_1_, IBlockReader p_215664_2_, BlockPos p_215664_3_)
    {
        return p_215664_1_.getLevel() == 9 && isFullHeight(p_215664_1_, p_215664_2_, p_215664_3_) ? VoxelShapes.fullCube() : this.field_215669_f.computeIfAbsent(p_215664_1_, (p_215668_2_) ->
        {
            return VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, (double)p_215668_2_.getActualHeight(p_215664_2_, p_215664_3_), 1.0D);
        });
    }
}
