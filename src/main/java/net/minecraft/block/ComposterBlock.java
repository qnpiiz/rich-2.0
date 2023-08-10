package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ComposterBlock extends Block implements ISidedInventoryProvider
{
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_0_8;
    public static final Object2FloatMap<IItemProvider> CHANCES = new Object2FloatOpenHashMap<>();
    private static final VoxelShape OUT_SHAPE = VoxelShapes.fullCube();
    private static final VoxelShape[] SHAPE = Util.make(new VoxelShape[9], (shapes) ->
    {
        for (int i = 0; i < 8; ++i)
        {
            shapes[i] = VoxelShapes.combineAndSimplify(OUT_SHAPE, Block.makeCuboidShape(2.0D, (double)Math.max(2, 1 + i * 2), 2.0D, 14.0D, 16.0D, 14.0D), IBooleanFunction.ONLY_FIRST);
        }

        shapes[8] = shapes[7];
    });

    public static void init()
    {
        CHANCES.defaultReturnValue(-1.0F);
        float f = 0.3F;
        float f1 = 0.5F;
        float f2 = 0.65F;
        float f3 = 0.85F;
        float f4 = 1.0F;
        registerCompostable(0.3F, Items.JUNGLE_LEAVES);
        registerCompostable(0.3F, Items.OAK_LEAVES);
        registerCompostable(0.3F, Items.SPRUCE_LEAVES);
        registerCompostable(0.3F, Items.DARK_OAK_LEAVES);
        registerCompostable(0.3F, Items.ACACIA_LEAVES);
        registerCompostable(0.3F, Items.BIRCH_LEAVES);
        registerCompostable(0.3F, Items.OAK_SAPLING);
        registerCompostable(0.3F, Items.SPRUCE_SAPLING);
        registerCompostable(0.3F, Items.BIRCH_SAPLING);
        registerCompostable(0.3F, Items.JUNGLE_SAPLING);
        registerCompostable(0.3F, Items.ACACIA_SAPLING);
        registerCompostable(0.3F, Items.DARK_OAK_SAPLING);
        registerCompostable(0.3F, Items.BEETROOT_SEEDS);
        registerCompostable(0.3F, Items.DRIED_KELP);
        registerCompostable(0.3F, Items.GRASS);
        registerCompostable(0.3F, Items.KELP);
        registerCompostable(0.3F, Items.MELON_SEEDS);
        registerCompostable(0.3F, Items.PUMPKIN_SEEDS);
        registerCompostable(0.3F, Items.SEAGRASS);
        registerCompostable(0.3F, Items.SWEET_BERRIES);
        registerCompostable(0.3F, Items.WHEAT_SEEDS);
        registerCompostable(0.5F, Items.DRIED_KELP_BLOCK);
        registerCompostable(0.5F, Items.TALL_GRASS);
        registerCompostable(0.5F, Items.CACTUS);
        registerCompostable(0.5F, Items.SUGAR_CANE);
        registerCompostable(0.5F, Items.VINE);
        registerCompostable(0.5F, Items.NETHER_SPROUTS);
        registerCompostable(0.5F, Items.WEEPING_VINES);
        registerCompostable(0.5F, Items.TWISTING_VINES);
        registerCompostable(0.5F, Items.MELON_SLICE);
        registerCompostable(0.65F, Items.SEA_PICKLE);
        registerCompostable(0.65F, Items.LILY_PAD);
        registerCompostable(0.65F, Items.PUMPKIN);
        registerCompostable(0.65F, Items.CARVED_PUMPKIN);
        registerCompostable(0.65F, Items.MELON);
        registerCompostable(0.65F, Items.APPLE);
        registerCompostable(0.65F, Items.BEETROOT);
        registerCompostable(0.65F, Items.CARROT);
        registerCompostable(0.65F, Items.COCOA_BEANS);
        registerCompostable(0.65F, Items.POTATO);
        registerCompostable(0.65F, Items.WHEAT);
        registerCompostable(0.65F, Items.BROWN_MUSHROOM);
        registerCompostable(0.65F, Items.RED_MUSHROOM);
        registerCompostable(0.65F, Items.MUSHROOM_STEM);
        registerCompostable(0.65F, Items.CRIMSON_FUNGUS);
        registerCompostable(0.65F, Items.WARPED_FUNGUS);
        registerCompostable(0.65F, Items.NETHER_WART);
        registerCompostable(0.65F, Items.CRIMSON_ROOTS);
        registerCompostable(0.65F, Items.WARPED_ROOTS);
        registerCompostable(0.65F, Items.SHROOMLIGHT);
        registerCompostable(0.65F, Items.DANDELION);
        registerCompostable(0.65F, Items.POPPY);
        registerCompostable(0.65F, Items.BLUE_ORCHID);
        registerCompostable(0.65F, Items.ALLIUM);
        registerCompostable(0.65F, Items.AZURE_BLUET);
        registerCompostable(0.65F, Items.RED_TULIP);
        registerCompostable(0.65F, Items.ORANGE_TULIP);
        registerCompostable(0.65F, Items.WHITE_TULIP);
        registerCompostable(0.65F, Items.PINK_TULIP);
        registerCompostable(0.65F, Items.OXEYE_DAISY);
        registerCompostable(0.65F, Items.CORNFLOWER);
        registerCompostable(0.65F, Items.LILY_OF_THE_VALLEY);
        registerCompostable(0.65F, Items.WITHER_ROSE);
        registerCompostable(0.65F, Items.FERN);
        registerCompostable(0.65F, Items.SUNFLOWER);
        registerCompostable(0.65F, Items.LILAC);
        registerCompostable(0.65F, Items.ROSE_BUSH);
        registerCompostable(0.65F, Items.PEONY);
        registerCompostable(0.65F, Items.LARGE_FERN);
        registerCompostable(0.85F, Items.HAY_BLOCK);
        registerCompostable(0.85F, Items.BROWN_MUSHROOM_BLOCK);
        registerCompostable(0.85F, Items.RED_MUSHROOM_BLOCK);
        registerCompostable(0.85F, Items.NETHER_WART_BLOCK);
        registerCompostable(0.85F, Items.WARPED_WART_BLOCK);
        registerCompostable(0.85F, Items.BREAD);
        registerCompostable(0.85F, Items.BAKED_POTATO);
        registerCompostable(0.85F, Items.COOKIE);
        registerCompostable(1.0F, Items.CAKE);
        registerCompostable(1.0F, Items.PUMPKIN_PIE);
    }

    private static void registerCompostable(float chance, IItemProvider itemIn)
    {
        CHANCES.put(itemIn.asItem(), chance);
    }

    public ComposterBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(LEVEL, Integer.valueOf(0)));
    }

    public static void playEvent(World world, BlockPos pos, boolean success)
    {
        BlockState blockstate = world.getBlockState(pos);
        world.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), success ? SoundEvents.BLOCK_COMPOSTER_FILL_SUCCESS : SoundEvents.BLOCK_COMPOSTER_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
        double d0 = blockstate.getShape(world, pos).max(Direction.Axis.Y, 0.5D, 0.5D) + 0.03125D;
        double d1 = (double)0.13125F;
        double d2 = (double)0.7375F;
        Random random = world.getRandom();

        for (int i = 0; i < 10; ++i)
        {
            double d3 = random.nextGaussian() * 0.02D;
            double d4 = random.nextGaussian() * 0.02D;
            double d5 = random.nextGaussian() * 0.02D;
            world.addParticle(ParticleTypes.COMPOSTER, (double)pos.getX() + (double)0.13125F + (double)0.7375F * (double)random.nextFloat(), (double)pos.getY() + d0 + (double)random.nextFloat() * (1.0D - d0), (double)pos.getZ() + (double)0.13125F + (double)0.7375F * (double)random.nextFloat(), d3, d4, d5);
        }
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE[state.get(LEVEL)];
    }

    public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return OUT_SHAPE;
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE[0];
    }

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if (state.get(LEVEL) == 7)
        {
            worldIn.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 20);
        }
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        int i = state.get(LEVEL);
        ItemStack itemstack = player.getHeldItem(handIn);

        if (i < 8 && CHANCES.containsKey(itemstack.getItem()))
        {
            if (i < 7 && !worldIn.isRemote)
            {
                BlockState blockstate = attemptCompost(state, worldIn, pos, itemstack);
                worldIn.playEvent(1500, pos, state != blockstate ? 1 : 0);

                if (!player.abilities.isCreativeMode)
                {
                    itemstack.shrink(1);
                }
            }

            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        else if (i == 8)
        {
            empty(state, worldIn, pos);
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        else
        {
            return ActionResultType.PASS;
        }
    }

    public static BlockState attemptFill(BlockState state, ServerWorld world, ItemStack stack, BlockPos pos)
    {
        int i = state.get(LEVEL);

        if (i < 7 && CHANCES.containsKey(stack.getItem()))
        {
            BlockState blockstate = attemptCompost(state, world, pos, stack);
            stack.shrink(1);
            return blockstate;
        }
        else
        {
            return state;
        }
    }

    public static BlockState empty(BlockState state, World world, BlockPos pos)
    {
        if (!world.isRemote)
        {
            float f = 0.7F;
            double d0 = (double)(world.rand.nextFloat() * 0.7F) + (double)0.15F;
            double d1 = (double)(world.rand.nextFloat() * 0.7F) + (double)0.060000002F + 0.6D;
            double d2 = (double)(world.rand.nextFloat() * 0.7F) + (double)0.15F;
            ItemEntity itementity = new ItemEntity(world, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, new ItemStack(Items.BONE_MEAL));
            itementity.setDefaultPickupDelay();
            world.addEntity(itementity);
        }

        BlockState blockstate = resetFillState(state, world, pos);
        world.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return blockstate;
    }

    private static BlockState resetFillState(BlockState state, IWorld world, BlockPos pos)
    {
        BlockState blockstate = state.with(LEVEL, Integer.valueOf(0));
        world.setBlockState(pos, blockstate, 3);
        return blockstate;
    }

    private static BlockState attemptCompost(BlockState state, IWorld world, BlockPos pos, ItemStack stack)
    {
        int i = state.get(LEVEL);
        float f = CHANCES.getFloat(stack.getItem());

        if ((i != 0 || !(f > 0.0F)) && !(world.getRandom().nextDouble() < (double)f))
        {
            return state;
        }
        else
        {
            int j = i + 1;
            BlockState blockstate = state.with(LEVEL, Integer.valueOf(j));
            world.setBlockState(pos, blockstate, 3);

            if (j == 7)
            {
                world.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 20);
            }

            return blockstate;
        }
    }

    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (state.get(LEVEL) == 7)
        {
            worldIn.setBlockState(pos, state.func_235896_a_(LEVEL), 3);
            worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_COMPOSTER_READY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
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
        return blockState.get(LEVEL);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(LEVEL);
    }

    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        return false;
    }

    public ISidedInventory createInventory(BlockState state, IWorld world, BlockPos pos)
    {
        int i = state.get(LEVEL);

        if (i == 8)
        {
            return new ComposterBlock.FullInventory(state, world, pos, new ItemStack(Items.BONE_MEAL));
        }
        else
        {
            return (ISidedInventory)(i < 7 ? new ComposterBlock.PartialInventory(state, world, pos) : new ComposterBlock.EmptyInventory());
        }
    }

    static class EmptyInventory extends Inventory implements ISidedInventory
    {
        public EmptyInventory()
        {
            super(0);
        }

        public int[] getSlotsForFace(Direction side)
        {
            return new int[0];
        }

        public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction)
        {
            return false;
        }

        public boolean canExtractItem(int index, ItemStack stack, Direction direction)
        {
            return false;
        }
    }

    static class FullInventory extends Inventory implements ISidedInventory
    {
        private final BlockState state;
        private final IWorld world;
        private final BlockPos pos;
        private boolean extracted;

        public FullInventory(BlockState state, IWorld world, BlockPos pos, ItemStack stack)
        {
            super(stack);
            this.state = state;
            this.world = world;
            this.pos = pos;
        }

        public int getInventoryStackLimit()
        {
            return 1;
        }

        public int[] getSlotsForFace(Direction side)
        {
            return side == Direction.DOWN ? new int[] {0} : new int[0];
        }

        public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction)
        {
            return false;
        }

        public boolean canExtractItem(int index, ItemStack stack, Direction direction)
        {
            return !this.extracted && direction == Direction.DOWN && stack.getItem() == Items.BONE_MEAL;
        }

        public void markDirty()
        {
            ComposterBlock.resetFillState(this.state, this.world, this.pos);
            this.extracted = true;
        }
    }

    static class PartialInventory extends Inventory implements ISidedInventory
    {
        private final BlockState state;
        private final IWorld world;
        private final BlockPos pos;
        private boolean inserted;

        public PartialInventory(BlockState state, IWorld world, BlockPos pos)
        {
            super(1);
            this.state = state;
            this.world = world;
            this.pos = pos;
        }

        public int getInventoryStackLimit()
        {
            return 1;
        }

        public int[] getSlotsForFace(Direction side)
        {
            return side == Direction.UP ? new int[] {0} : new int[0];
        }

        public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction)
        {
            return !this.inserted && direction == Direction.UP && ComposterBlock.CHANCES.containsKey(itemStackIn.getItem());
        }

        public boolean canExtractItem(int index, ItemStack stack, Direction direction)
        {
            return false;
        }

        public void markDirty()
        {
            ItemStack itemstack = this.getStackInSlot(0);

            if (!itemstack.isEmpty())
            {
                this.inserted = true;
                BlockState blockstate = ComposterBlock.attemptCompost(this.state, this.world, this.pos, itemstack);
                this.world.playEvent(1500, this.pos, blockstate != this.state ? 1 : 0);
                this.removeStackFromSlot(0);
            }
        }
    }
}
