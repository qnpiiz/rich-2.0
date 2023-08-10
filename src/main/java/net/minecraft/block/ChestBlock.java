package net.minecraft.block;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ChestBlock extends AbstractChestBlock<ChestTileEntity> implements IWaterLoggable
{
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE_NORTH = Block.makeCuboidShape(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
    protected static final VoxelShape SHAPE_SOUTH = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
    protected static final VoxelShape SHAPE_WEST = Block.makeCuboidShape(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    protected static final VoxelShape SHAPE_EAST = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
    protected static final VoxelShape SHAPE_SINGLE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    private static final TileEntityMerger.ICallback<ChestTileEntity, Optional<IInventory>> INVENTORY_MERGER = new TileEntityMerger.ICallback<ChestTileEntity, Optional<IInventory>>()
    {
        public Optional<IInventory> func_225539_a_(ChestTileEntity p_225539_1_, ChestTileEntity p_225539_2_)
        {
            return Optional.of(new DoubleSidedInventory(p_225539_1_, p_225539_2_));
        }
        public Optional<IInventory> func_225538_a_(ChestTileEntity p_225538_1_)
        {
            return Optional.of(p_225538_1_);
        }
        public Optional<IInventory> func_225537_b_()
        {
            return Optional.empty();
        }
    };
    private static final TileEntityMerger.ICallback<ChestTileEntity, Optional<INamedContainerProvider>> CONTAINER_MERGER = new TileEntityMerger.ICallback<ChestTileEntity, Optional<INamedContainerProvider>>()
    {
        public Optional<INamedContainerProvider> func_225539_a_(final ChestTileEntity p_225539_1_, final ChestTileEntity p_225539_2_)
        {
            final IInventory iinventory = new DoubleSidedInventory(p_225539_1_, p_225539_2_);
            return Optional.of(new INamedContainerProvider()
            {
                @Nullable
                public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_)
                {
                    if (p_225539_1_.canOpen(p_createMenu_3_) && p_225539_2_.canOpen(p_createMenu_3_))
                    {
                        p_225539_1_.fillWithLoot(p_createMenu_2_.player);
                        p_225539_2_.fillWithLoot(p_createMenu_2_.player);
                        return ChestContainer.createGeneric9X6(p_createMenu_1_, p_createMenu_2_, iinventory);
                    }
                    else
                    {
                        return null;
                    }
                }
                public ITextComponent getDisplayName()
                {
                    if (p_225539_1_.hasCustomName())
                    {
                        return p_225539_1_.getDisplayName();
                    }
                    else
                    {
                        return (ITextComponent)(p_225539_2_.hasCustomName() ? p_225539_2_.getDisplayName() : new TranslationTextComponent("container.chestDouble"));
                    }
                }
            });
        }
        public Optional<INamedContainerProvider> func_225538_a_(ChestTileEntity p_225538_1_)
        {
            return Optional.of(p_225538_1_);
        }
        public Optional<INamedContainerProvider> func_225537_b_()
        {
            return Optional.empty();
        }
    };

    protected ChestBlock(AbstractBlock.Properties builder, Supplier < TileEntityType <? extends ChestTileEntity >> tileEntityTypeIn)
    {
        super(builder, tileEntityTypeIn);
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(TYPE, ChestType.SINGLE).with(WATERLOGGED, Boolean.valueOf(false)));
    }

    public static TileEntityMerger.Type getChestMergerType(BlockState state)
    {
        ChestType chesttype = state.get(TYPE);

        if (chesttype == ChestType.SINGLE)
        {
            return TileEntityMerger.Type.SINGLE;
        }
        else
        {
            return chesttype == ChestType.RIGHT ? TileEntityMerger.Type.FIRST : TileEntityMerger.Type.SECOND;
        }
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
     */
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder
     * immediately returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (stateIn.get(WATERLOGGED))
        {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }

        if (facingState.isIn(this) && facing.getAxis().isHorizontal())
        {
            ChestType chesttype = facingState.get(TYPE);

            if (stateIn.get(TYPE) == ChestType.SINGLE && chesttype != ChestType.SINGLE && stateIn.get(FACING) == facingState.get(FACING) && getDirectionToAttached(facingState) == facing.getOpposite())
            {
                return stateIn.with(TYPE, chesttype.opposite());
            }
        }
        else if (getDirectionToAttached(stateIn) == facing)
        {
            return stateIn.with(TYPE, ChestType.SINGLE);
        }

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if (state.get(TYPE) == ChestType.SINGLE)
        {
            return SHAPE_SINGLE;
        }
        else
        {
            switch (getDirectionToAttached(state))
            {
                case NORTH:
                default:
                    return SHAPE_NORTH;

                case SOUTH:
                    return SHAPE_SOUTH;

                case WEST:
                    return SHAPE_WEST;

                case EAST:
                    return SHAPE_EAST;
            }
        }
    }

    /**
     * Returns a facing pointing from the given state to its attached double chest
     */
    public static Direction getDirectionToAttached(BlockState state)
    {
        Direction direction = state.get(FACING);
        return state.get(TYPE) == ChestType.LEFT ? direction.rotateY() : direction.rotateYCCW();
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        ChestType chesttype = ChestType.SINGLE;
        Direction direction = context.getPlacementHorizontalFacing().getOpposite();
        FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        boolean flag = context.hasSecondaryUseForPlayer();
        Direction direction1 = context.getFace();

        if (direction1.getAxis().isHorizontal() && flag)
        {
            Direction direction2 = this.getDirectionToAttach(context, direction1.getOpposite());

            if (direction2 != null && direction2.getAxis() != direction1.getAxis())
            {
                direction = direction2;
                chesttype = direction2.rotateYCCW() == direction1.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
            }
        }

        if (chesttype == ChestType.SINGLE && !flag)
        {
            if (direction == this.getDirectionToAttach(context, direction.rotateY()))
            {
                chesttype = ChestType.LEFT;
            }
            else if (direction == this.getDirectionToAttach(context, direction.rotateYCCW()))
            {
                chesttype = ChestType.RIGHT;
            }
        }

        return this.getDefaultState().with(FACING, direction).with(TYPE, chesttype).with(WATERLOGGED, Boolean.valueOf(fluidstate.getFluid() == Fluids.WATER));
    }

    public FluidState getFluidState(BlockState state)
    {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Nullable

    /**
     * Returns facing pointing to a chest to form a double chest with, null otherwise
     */
    private Direction getDirectionToAttach(BlockItemUseContext context, Direction direction)
    {
        BlockState blockstate = context.getWorld().getBlockState(context.getPos().offset(direction));
        return blockstate.isIn(this) && blockstate.get(TYPE) == ChestType.SINGLE ? blockstate.get(FACING) : null;
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        if (stack.hasDisplayName())
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof ChestTileEntity)
            {
                ((ChestTileEntity)tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!state.isIn(newState.getBlock()))
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof IInventory)
            {
                InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
                worldIn.updateComparatorOutputLevel(pos, this);
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (worldIn.isRemote)
        {
            return ActionResultType.SUCCESS;
        }
        else
        {
            INamedContainerProvider inamedcontainerprovider = this.getContainer(state, worldIn, pos);

            if (inamedcontainerprovider != null)
            {
                player.openContainer(inamedcontainerprovider);
                player.addStat(this.getOpenStat());
                PiglinTasks.func_234478_a_(player, true);
            }

            return ActionResultType.CONSUME;
        }
    }

    protected Stat<ResourceLocation> getOpenStat()
    {
        return Stats.CUSTOM.get(Stats.OPEN_CHEST);
    }

    @Nullable
    public static IInventory getChestInventory(ChestBlock chest, BlockState state, World world, BlockPos pos, boolean override)
    {
        return chest.combine(state, world, pos, override).<Optional<IInventory>>apply(INVENTORY_MERGER).orElse((IInventory)null);
    }

    public TileEntityMerger.ICallbackWrapper <? extends ChestTileEntity > combine(BlockState state, World world, BlockPos pos, boolean override)
    {
        BiPredicate<IWorld, BlockPos> bipredicate;

        if (override)
        {
            bipredicate = (worldIn, posIn) ->
            {
                return false;
            };
        }
        else
        {
            bipredicate = ChestBlock::isBlocked;
        }

        return TileEntityMerger.func_226924_a_(this.tileEntityType.get(), ChestBlock::getChestMergerType, ChestBlock::getDirectionToAttached, FACING, state, world, pos, bipredicate);
    }

    @Nullable
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos)
    {
        return this.combine(state, worldIn, pos, false).<Optional<INamedContainerProvider>>apply(CONTAINER_MERGER).orElse((INamedContainerProvider)null);
    }

    public static TileEntityMerger.ICallback<ChestTileEntity, Float2FloatFunction> getLidRotationCallback(final IChestLid lid)
    {
        return new TileEntityMerger.ICallback<ChestTileEntity, Float2FloatFunction>()
        {
            public Float2FloatFunction func_225539_a_(ChestTileEntity p_225539_1_, ChestTileEntity p_225539_2_)
            {
                return (angle) ->
                {
                    return Math.max(p_225539_1_.getLidAngle(angle), p_225539_2_.getLidAngle(angle));
                };
            }
            public Float2FloatFunction func_225538_a_(ChestTileEntity p_225538_1_)
            {
                return p_225538_1_::getLidAngle;
            }
            public Float2FloatFunction func_225537_b_()
            {
                return lid::getLidAngle;
            }
        };
    }

    public TileEntity createNewTileEntity(IBlockReader worldIn)
    {
        return new ChestTileEntity();
    }

    public static boolean isBlocked(IWorld world, BlockPos pos)
    {
        return isBelowSolidBlock(world, pos) || isCatSittingOn(world, pos);
    }

    private static boolean isBelowSolidBlock(IBlockReader reader, BlockPos worldIn)
    {
        BlockPos blockpos = worldIn.up();
        return reader.getBlockState(blockpos).isNormalCube(reader, blockpos);
    }

    private static boolean isCatSittingOn(IWorld world, BlockPos pos)
    {
        List<CatEntity> list = world.getEntitiesWithinAABB(CatEntity.class, new AxisAlignedBB((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1)));

        if (!list.isEmpty())
        {
            for (CatEntity catentity : list)
            {
                if (catentity.isSleeping())
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @deprecated call via {@link IBlockState#hasComparatorInputOverride()} whenever possible. Implementing/overriding
     * is fine.
     */
    public boolean hasComparatorInputOverride(BlockState state)
    {
        return true;
    }

    /**
     * @deprecated call via {@link IBlockState#getComparatorInputOverride(World,BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos)
    {
        return Container.calcRedstoneFromInventory(getChestInventory(this, blockState, worldIn, pos, false));
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
     * fine.
     */
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
     */
    public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, TYPE, WATERLOGGED);
    }

    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        return false;
    }
}
