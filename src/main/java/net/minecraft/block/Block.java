package net.minecraft.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockVoxelShape;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block extends AbstractBlock implements IItemProvider
{
    protected static final Logger LOGGER = LogManager.getLogger();
    public static final ObjectIntIdentityMap<BlockState> BLOCK_STATE_IDS = new ObjectIntIdentityMap<>();
    private static final LoadingCache<VoxelShape, Boolean> OPAQUE_CACHE = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build(new CacheLoader<VoxelShape, Boolean>()
    {
        public Boolean load(VoxelShape p_load_1_)
        {
            return !VoxelShapes.compare(VoxelShapes.fullCube(), p_load_1_, IBooleanFunction.NOT_SAME);
        }
    });
    protected final StateContainer<Block, BlockState> stateContainer;
    private BlockState defaultState;
    @Nullable
    private String translationKey;
    @Nullable
    private Item item;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>> SHOULD_SIDE_RENDER_CACHE = ThreadLocal.withInitial(() ->
    {
        Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>(2048, 0.25F)
        {
            protected void rehash(int p_rehash_1_)
            {
            }
        };
        object2bytelinkedopenhashmap.defaultReturnValue((byte)127);
        return object2bytelinkedopenhashmap;
    });

    public static int getStateId(@Nullable BlockState state)
    {
        if (state == null)
        {
            return 0;
        }
        else
        {
            int i = BLOCK_STATE_IDS.getId(state);
            return i == -1 ? 0 : i;
        }
    }

    public static BlockState getStateById(int id)
    {
        BlockState blockstate = BLOCK_STATE_IDS.getByValue(id);
        return blockstate == null ? Blocks.AIR.getDefaultState() : blockstate;
    }

    public static Block getBlockFromItem(@Nullable Item itemIn)
    {
        return itemIn instanceof BlockItem ? ((BlockItem)itemIn).getBlock() : Blocks.AIR;
    }

    public static BlockState nudgeEntitiesWithNewState(BlockState oldState, BlockState newState, World worldIn, BlockPos pos)
    {
        VoxelShape voxelshape = VoxelShapes.combine(oldState.getCollisionShape(worldIn, pos), newState.getCollisionShape(worldIn, pos), IBooleanFunction.ONLY_SECOND).withOffset((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());

        for (Entity entity : worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, voxelshape.getBoundingBox()))
        {
            double d0 = VoxelShapes.getAllowedOffset(Direction.Axis.Y, entity.getBoundingBox().offset(0.0D, 1.0D, 0.0D), Stream.of(voxelshape), -1.0D);
            entity.setPositionAndUpdate(entity.getPosX(), entity.getPosY() + 1.0D + d0, entity.getPosZ());
        }

        return newState;
    }

    public static VoxelShape makeCuboidShape(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        return VoxelShapes.create(x1 / 16.0D, y1 / 16.0D, z1 / 16.0D, x2 / 16.0D, y2 / 16.0D, z2 / 16.0D);
    }

    public boolean isIn(ITag<Block> tagIn)
    {
        return tagIn.contains(this);
    }

    public boolean matchesBlock(Block block)
    {
        return this == block;
    }

    /**
     * With the provided block state, performs neighbor checks for all neighboring blocks to get an "adjusted"
     * blockstate for placement in the world, if the current state is not valid.
     */
    public static BlockState getValidBlockForPosition(BlockState currentState, IWorld worldIn, BlockPos pos)
    {
        BlockState blockstate = currentState;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (Direction direction : UPDATE_ORDER)
        {
            blockpos$mutable.setAndMove(pos, direction);
            blockstate = blockstate.updatePostPlacement(direction, worldIn.getBlockState(blockpos$mutable), worldIn, pos, blockpos$mutable);
        }

        return blockstate;
    }

    /**
     * Replaces oldState with newState, possibly playing effects and creating drops. Flags are as in {@link
     * World#setBlockState}
     */
    public static void replaceBlock(BlockState oldState, BlockState newState, IWorld worldIn, BlockPos pos, int flags)
    {
        replaceBlockState(oldState, newState, worldIn, pos, flags, 512);
    }

    public static void replaceBlockState(BlockState oldState, BlockState newState, IWorld world, BlockPos pos, int flags, int recursionLeft)
    {
        if (newState != oldState)
        {
            if (newState.isAir())
            {
                if (!world.isRemote())
                {
                    world.destroyBlock(pos, (flags & 32) == 0, (Entity)null, recursionLeft);
                }
            }
            else
            {
                world.setBlockState(pos, newState, flags & -33, recursionLeft);
            }
        }
    }

    public Block(AbstractBlock.Properties properties)
    {
        super(properties);
        StateContainer.Builder<Block, BlockState> builder = new StateContainer.Builder<>(this);
        this.fillStateContainer(builder);
        this.stateContainer = builder.func_235882_a_(Block::getDefaultState, BlockState::new);
        this.setDefaultState(this.stateContainer.getBaseState());
    }

    /**
     * Checks if the provided block is in the hardcoded list of blocks that will not attach to fences/panes/walls
     */
    public static boolean cannotAttach(Block blockIn)
    {
        return blockIn instanceof LeavesBlock || blockIn == Blocks.BARRIER || blockIn == Blocks.CARVED_PUMPKIN || blockIn == Blocks.JACK_O_LANTERN || blockIn == Blocks.MELON || blockIn == Blocks.PUMPKIN || blockIn.isIn(BlockTags.SHULKER_BOXES);
    }

    /**
     * Returns whether or not this block is of a type that needs random ticking. Called for ref-counting purposes by
     * ExtendedBlockStorage in order to broadly cull a chunk from the random chunk update list for efficiency's sake.
     */
    public boolean ticksRandomly(BlockState state)
    {
        return this.ticksRandomly;
    }

    /**
     * ""
     */
    public static boolean shouldSideBeRendered(BlockState adjacentState, IBlockReader blockState, BlockPos blockAccess, Direction pos)
    {
        BlockPos blockpos = blockAccess.offset(pos);
        BlockState blockstate = blockState.getBlockState(blockpos);

        if (adjacentState.isSideInvisible(blockstate, pos))
        {
            return false;
        }
        else if (blockstate.isSolid())
        {
            Block.RenderSideCacheKey block$rendersidecachekey = new Block.RenderSideCacheKey(adjacentState, blockstate, pos);
            Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = SHOULD_SIDE_RENDER_CACHE.get();
            byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block$rendersidecachekey);

            if (b0 != 127)
            {
                return b0 != 0;
            }
            else
            {
                VoxelShape voxelshape = adjacentState.getFaceOcclusionShape(blockState, blockAccess, pos);
                VoxelShape voxelshape1 = blockstate.getFaceOcclusionShape(blockState, blockpos, pos.getOpposite());
                boolean flag = VoxelShapes.compare(voxelshape, voxelshape1, IBooleanFunction.ONLY_FIRST);

                if (object2bytelinkedopenhashmap.size() == 2048)
                {
                    object2bytelinkedopenhashmap.removeLastByte();
                }

                object2bytelinkedopenhashmap.putAndMoveToFirst(block$rendersidecachekey, (byte)(flag ? 1 : 0));
                return flag;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * Returns true if the block in given position has a solid top face
     */
    public static boolean hasSolidSideOnTop(IBlockReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).func_242698_a(worldIn, pos, Direction.UP, BlockVoxelShape.RIGID);
    }

    /**
     * Returns true if the block can stay on position if attached to given side
     */
    public static boolean hasEnoughSolidSide(IWorldReader worldIn, BlockPos pos, Direction directionIn)
    {
        BlockState blockstate = worldIn.getBlockState(pos);
        return directionIn == Direction.DOWN && blockstate.isIn(BlockTags.UNSTABLE_BOTTOM_CENTER) ? false : blockstate.func_242698_a(worldIn, pos, directionIn, BlockVoxelShape.CENTER);
    }

    public static boolean doesSideFillSquare(VoxelShape shape, Direction side)
    {
        VoxelShape voxelshape = shape.project(side);
        return isOpaque(voxelshape);
    }

    /**
     * Gets whether the provided {@link VoxelShape} is opaque
     */
    public static boolean isOpaque(VoxelShape shape)
    {
        return OPAQUE_CACHE.getUnchecked(shape);
    }

    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return !isOpaque(state.getShape(reader, pos)) && state.getFluidState().isEmpty();
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
    }

    /**
     * Called after a player destroys this Block - the posiiton pos may no longer hold the state indicated.
     */
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state)
    {
    }

    public static List<ItemStack> getDrops(BlockState state, ServerWorld worldIn, BlockPos pos, @Nullable TileEntity tileEntityIn)
    {
        LootContext.Builder lootcontext$builder = (new LootContext.Builder(worldIn)).withRandom(worldIn.rand).withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(pos)).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withNullableParameter(LootParameters.BLOCK_ENTITY, tileEntityIn);
        return state.getDrops(lootcontext$builder);
    }

    public static List<ItemStack> getDrops(BlockState state, ServerWorld worldIn, BlockPos pos, @Nullable TileEntity tileEntityIn, @Nullable Entity entityIn, ItemStack stack)
    {
        LootContext.Builder lootcontext$builder = (new LootContext.Builder(worldIn)).withRandom(worldIn.rand).withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(pos)).withParameter(LootParameters.TOOL, stack).withNullableParameter(LootParameters.THIS_ENTITY, entityIn).withNullableParameter(LootParameters.BLOCK_ENTITY, tileEntityIn);
        return state.getDrops(lootcontext$builder);
    }

    public static void spawnDrops(BlockState state, World worldIn, BlockPos pos)
    {
        if (worldIn instanceof ServerWorld)
        {
            getDrops(state, (ServerWorld)worldIn, pos, (TileEntity)null).forEach((stackToSpawn) ->
            {
                spawnAsEntity(worldIn, pos, stackToSpawn);
            });
            state.spawnAdditionalDrops((ServerWorld)worldIn, pos, ItemStack.EMPTY);
        }
    }

    public static void spawnDrops(BlockState state, IWorld worldIn, BlockPos pos, @Nullable TileEntity tileEntityIn)
    {
        if (worldIn instanceof ServerWorld)
        {
            getDrops(state, (ServerWorld)worldIn, pos, tileEntityIn).forEach((stackToSpawn) ->
            {
                spawnAsEntity((ServerWorld)worldIn, pos, stackToSpawn);
            });
            state.spawnAdditionalDrops((ServerWorld)worldIn, pos, ItemStack.EMPTY);
        }
    }

    public static void spawnDrops(BlockState state, World worldIn, BlockPos pos, @Nullable TileEntity tileEntityIn, Entity entityIn, ItemStack stack)
    {
        if (worldIn instanceof ServerWorld)
        {
            getDrops(state, (ServerWorld)worldIn, pos, tileEntityIn, entityIn, stack).forEach((stackToSpawn) ->
            {
                spawnAsEntity(worldIn, pos, stackToSpawn);
            });
            state.spawnAdditionalDrops((ServerWorld)worldIn, pos, stack);
        }
    }

    /**
     * Spawns the given stack into the World at the given position, respecting the doTileDrops gamerule
     */
    public static void spawnAsEntity(World worldIn, BlockPos pos, ItemStack stack)
    {
        if (!worldIn.isRemote && !stack.isEmpty() && worldIn.getGameRules().getBoolean(GameRules.DO_TILE_DROPS))
        {
            float f = 0.5F;
            double d0 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
            double d1 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
            double d2 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
            ItemEntity itementity = new ItemEntity(worldIn, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, stack);
            itementity.setDefaultPickupDelay();
            worldIn.addEntity(itementity);
        }
    }

    /**
     * Spawns the given amount of experience into the World as XP orb entities
     */
    protected void dropXpOnBlockBreak(ServerWorld worldIn, BlockPos pos, int amount)
    {
        if (worldIn.getGameRules().getBoolean(GameRules.DO_TILE_DROPS))
        {
            while (amount > 0)
            {
                int i = ExperienceOrbEntity.getXPSplit(amount);
                amount -= i;
                worldIn.addEntity(new ExperienceOrbEntity(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, i));
            }
        }
    }

    /**
     * Returns how much this block can resist explosions from the passed in entity.
     */
    public float getExplosionResistance()
    {
        return this.blastResistance;
    }

    /**
     * Called when this Block is destroyed by an Explosion
     */
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn)
    {
    }

    /**
     * Called when the given entity walks on this Block
     */
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
    {
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState();
    }

    /**
     * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
     * Block.removedByPlayer
     */
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        player.addStat(Stats.BLOCK_MINED.get(this));
        player.addExhaustion(0.005F);
        spawnDrops(state, worldIn, pos, te, player, stack);
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
    }

    /**
     * Return true if an entity can be spawned inside the block (used to get the player's bed spawn location)
     */
    public boolean canSpawnInBlock()
    {
        return !this.material.isSolid() && !this.material.isLiquid();
    }

    public IFormattableTextComponent getTranslatedName()
    {
        return new TranslationTextComponent(this.getTranslationKey());
    }

    /**
     * Returns the unlocalized name of the block with "tile." appended to the front.
     */
    public String getTranslationKey()
    {
        if (this.translationKey == null)
        {
            this.translationKey = Util.makeTranslationKey("block", Registry.BLOCK.getKey(this));
        }

        return this.translationKey;
    }

    /**
     * Block's chance to react to a living entity falling on it.
     */
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        entityIn.onLivingFall(fallDistance, 1.0F);
    }

    /**
     * Called when an Entity lands on this Block. This method *must* update motionY because the entity will not do that
     * on its own
     */
    public void onLanded(IBlockReader worldIn, Entity entityIn)
    {
        entityIn.setMotion(entityIn.getMotion().mul(1.0D, 0.0D, 1.0D));
    }

    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state)
    {
        return new ItemStack(this);
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(this));
    }

    public float getSlipperiness()
    {
        return this.slipperiness;
    }

    public float getSpeedFactor()
    {
        return this.speedFactor;
    }

    public float getJumpFactor()
    {
        return this.jumpFactor;
    }

    /**
     * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually
     * collect this block
     */
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        worldIn.playEvent(player, 2001, pos, getStateId(state));

        if (this.isIn(BlockTags.GUARDED_BY_PIGLINS))
        {
            PiglinTasks.func_234478_a_(player, false);
        }
    }

    /**
     * Called similar to random ticks, but only when it is raining.
     */
    public void fillWithRain(World worldIn, BlockPos pos)
    {
    }

    /**
     * Return whether this block can drop from an explosion.
     */
    public boolean canDropFromExplosion(Explosion explosionIn)
    {
        return true;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
    }

    public StateContainer<Block, BlockState> getStateContainer()
    {
        return this.stateContainer;
    }

    protected final void setDefaultState(BlockState state)
    {
        this.defaultState = state;
    }

    /**
     * Gets the default state for this block
     */
    public final BlockState getDefaultState()
    {
        return this.defaultState;
    }

    public SoundType getSoundType(BlockState state)
    {
        return this.soundType;
    }

    public Item asItem()
    {
        if (this.item == null)
        {
            this.item = Item.getItemFromBlock(this);
        }

        return this.item;
    }

    public boolean isVariableOpacity()
    {
        return this.variableOpacity;
    }

    public String toString()
    {
        return "Block{" + Registry.BLOCK.getKey(this) + "}";
    }

    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
    }

    protected Block getSelf()
    {
        return this;
    }

    public static final class RenderSideCacheKey
    {
        private final BlockState state;
        private final BlockState adjacentState;
        private final Direction side;

        public RenderSideCacheKey(BlockState state, BlockState adjacentState, Direction side)
        {
            this.state = state;
            this.adjacentState = adjacentState;
            this.side = side;
        }

        public boolean equals(Object p_equals_1_)
        {
            if (this == p_equals_1_)
            {
                return true;
            }
            else if (!(p_equals_1_ instanceof Block.RenderSideCacheKey))
            {
                return false;
            }
            else
            {
                Block.RenderSideCacheKey block$rendersidecachekey = (Block.RenderSideCacheKey)p_equals_1_;
                return this.state == block$rendersidecachekey.state && this.adjacentState == block$rendersidecachekey.adjacentState && this.side == block$rendersidecachekey.side;
            }
        }

        public int hashCode()
        {
            int i = this.state.hashCode();
            i = 31 * i + this.adjacentState.hashCode();
            return 31 * i + this.side.hashCode();
        }
    }
}
