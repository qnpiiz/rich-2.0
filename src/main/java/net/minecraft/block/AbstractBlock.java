package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.Property;
import net.minecraft.state.StateHolder;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.BlockVoxelShape;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyBlockReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractBlock
{
    protected static final Direction[] UPDATE_ORDER = new Direction[] {Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
    protected final Material material;
    protected final boolean canCollide;
    protected final float blastResistance;

    /**
     * Flags whether or not this block is of a type that needs random ticking. Ref-counted by ExtendedBlockStorage in
     * order to broadly cull a chunk from the random chunk update list for efficiency's sake.
     */
    protected final boolean ticksRandomly;
    protected final SoundType soundType;

    /**
     * Determines how much velocity is maintained while moving on top of this block
     */
    protected final float slipperiness;
    protected final float speedFactor;
    protected final float jumpFactor;
    protected final boolean variableOpacity;
    protected final AbstractBlock.Properties properties;
    @Nullable
    protected ResourceLocation lootTable;

    public AbstractBlock(AbstractBlock.Properties properties)
    {
        this.material = properties.material;
        this.canCollide = properties.blocksMovement;
        this.lootTable = properties.lootTable;
        this.blastResistance = properties.resistance;
        this.ticksRandomly = properties.ticksRandomly;
        this.soundType = properties.soundType;
        this.slipperiness = properties.slipperiness;
        this.speedFactor = properties.speedFactor;
        this.jumpFactor = properties.jumpFactor;
        this.variableOpacity = properties.variableOpacity;
        this.properties = properties;
    }

    @Deprecated

    /**
     * performs updates on diagonal neighbors of the target position and passes in the flags. The flags can be
     * referenced from the docs for {@link IWorldWriter#setBlockState(IBlockState, BlockPos, int)}.
     */
    public void updateDiagonalNeighbors(BlockState state, IWorld worldIn, BlockPos pos, int flags, int recursionLeft)
    {
    }

    @Deprecated
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        switch (type)
        {
            case LAND:
                return !state.hasOpaqueCollisionShape(worldIn, pos);

            case WATER:
                return worldIn.getFluidState(pos).isTagged(FluidTags.WATER);

            case AIR:
                return !state.hasOpaqueCollisionShape(worldIn, pos);

            default:
                return false;
        }
    }

    @Deprecated

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder
     * immediately returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return stateIn;
    }

    @Deprecated
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side)
    {
        return false;
    }

    @Deprecated
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        DebugPacketSender.func_218806_a(worldIn, pos);
    }

    @Deprecated
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
    }

    @Deprecated
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (this.isTileEntityProvider() && !state.isIn(newState.getBlock()))
        {
            worldIn.removeTileEntity(pos);
        }
    }

    @Deprecated
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        return ActionResultType.PASS;
    }

    @Deprecated

    /**
     * Called on server when World#addBlockEvent is called. If server returns true, then also called on the client. On
     * the Server, this may perform additional changes to the world, like pistons replacing the block with an extended
     * base. On the client, the update may involve replacing tile entities or effects such as sounds or particles
     * @deprecated call via {@link IBlockState#onBlockEventReceived(World,BlockPos,int,int)} whenever possible.
     * Implementing/overriding is fine.
     */
    public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param)
    {
        return false;
    }

    @Deprecated

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
     */
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Deprecated
    public boolean isTransparent(BlockState state)
    {
        return false;
    }

    @Deprecated

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
     */
    public boolean canProvidePower(BlockState state)
    {
        return false;
    }

    @Deprecated

    /**
     * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
     */
    public PushReaction getPushReaction(BlockState state)
    {
        return this.material.getPushReaction();
    }

    @Deprecated
    public FluidState getFluidState(BlockState state)
    {
        return Fluids.EMPTY.getDefaultState();
    }

    @Deprecated

    /**
     * @deprecated call via {@link IBlockState#hasComparatorInputOverride()} whenever possible. Implementing/overriding
     * is fine.
     */
    public boolean hasComparatorInputOverride(BlockState state)
    {
        return false;
    }

    /**
     * Get the OffsetType for this Block. Determines if the model is rendered slightly offset.
     */
    public AbstractBlock.OffsetType getOffsetType()
    {
        return AbstractBlock.OffsetType.NONE;
    }

    @Deprecated

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
     * fine.
     */
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state;
    }

    @Deprecated

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
     */
    public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
        return state;
    }

    @Deprecated
    public boolean isReplaceable(BlockState state, BlockItemUseContext useContext)
    {
        return this.material.isReplaceable() && (useContext.getItem().isEmpty() || useContext.getItem().getItem() != this.asItem());
    }

    @Deprecated
    public boolean isReplaceable(BlockState state, Fluid fluid)
    {
        return this.material.isReplaceable() || !this.material.isSolid();
    }

    @Deprecated
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        ResourceLocation resourcelocation = this.getLootTable();

        if (resourcelocation == LootTables.EMPTY)
        {
            return Collections.emptyList();
        }
        else
        {
            LootContext lootcontext = builder.withParameter(LootParameters.BLOCK_STATE, state).build(LootParameterSets.BLOCK);
            ServerWorld serverworld = lootcontext.getWorld();
            LootTable loottable = serverworld.getServer().getLootTableManager().getLootTableFromLocation(resourcelocation);
            return loottable.generate(lootcontext);
        }
    }

    @Deprecated

    /**
     * Return a random long to be passed to {@link IBakedModel#getQuads}, used for random model rotations
     */
    public long getPositionRandom(BlockState state, BlockPos pos)
    {
        return MathHelper.getPositionRandom(pos);
    }

    @Deprecated
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return state.getShape(worldIn, pos);
    }

    @Deprecated
    public VoxelShape getCollisionShape(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return this.getCollisionShape(state, reader, pos, ISelectionContext.dummy());
    }

    @Deprecated
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return VoxelShapes.empty();
    }

    @Deprecated
    public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        if (state.isOpaqueCube(worldIn, pos))
        {
            return worldIn.getMaxLightLevel();
        }
        else
        {
            return state.propagatesSkylightDown(worldIn, pos) ? 0 : 1;
        }
    }

    @Nullable
    @Deprecated
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos)
    {
        return null;
    }

    @Deprecated
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return true;
    }

    @Deprecated
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return state.hasOpaqueCollisionShape(worldIn, pos) ? 0.2F : 1.0F;
    }

    @Deprecated

    /**
     * @deprecated call via {@link IBlockState#getComparatorInputOverride(World,BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos)
    {
        return 0;
    }

    @Deprecated
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.fullCube();
    }

    @Deprecated
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.canCollide ? state.getShape(worldIn, pos) : VoxelShapes.empty();
    }

    @Deprecated
    public VoxelShape getRayTraceShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context)
    {
        return this.getCollisionShape(state, reader, pos, context);
    }

    @Deprecated

    /**
     * Performs a random tick on a block.
     */
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        this.tick(state, worldIn, pos, random);
    }

    @Deprecated
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
    }

    @Deprecated

    /**
     * Get the hardness of this Block relative to the ability of the given player
     * @deprecated call via {@link IBlockState#getPlayerRelativeBlockHardness(EntityPlayer,World,BlockPos)} whenever
     * possible. Implementing/overriding is fine.
     */
    public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos)
    {
        float f = state.getBlockHardness(worldIn, pos);

        if (f == -1.0F)
        {
            return 0.0F;
        }
        else
        {
            int i = player.func_234569_d_(state) ? 30 : 100;
            return player.getDigSpeed(state) / f / (float)i;
        }
    }

    @Deprecated

    /**
     * Perform side-effects from block dropping, such as creating silverfish
     */
    public void spawnAdditionalDrops(BlockState state, ServerWorld worldIn, BlockPos pos, ItemStack stack)
    {
    }

    @Deprecated
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player)
    {
    }

    @Deprecated

    /**
     * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        return 0;
    }

    @Deprecated
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
    }

    @Deprecated

    /**
     * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        return 0;
    }

    public final boolean isTileEntityProvider()
    {
        return this instanceof ITileEntityProvider;
    }

    public final ResourceLocation getLootTable()
    {
        if (this.lootTable == null)
        {
            ResourceLocation resourcelocation = Registry.BLOCK.getKey(this.getSelf());
            this.lootTable = new ResourceLocation(resourcelocation.getNamespace(), "blocks/" + resourcelocation.getPath());
        }

        return this.lootTable;
    }

    @Deprecated
    public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile)
    {
    }

    public abstract Item asItem();

    protected abstract Block getSelf();

    public MaterialColor getMaterialColor()
    {
        return this.properties.blockColors.apply(this.getSelf().getDefaultState());
    }

    public abstract static class AbstractBlockState extends StateHolder<Block, BlockState>
    {
        private final int lightLevel;
        private final boolean transparent;
        private final boolean isAir;
        private final Material material;
        private final MaterialColor materialColor;
        private final float hardness;
        private final boolean requiresTool;
        private final boolean isSolid;
        private final AbstractBlock.IPositionPredicate isNormalCube;
        private final AbstractBlock.IPositionPredicate blocksVisionChecker;
        private final AbstractBlock.IPositionPredicate blocksVision;
        private final AbstractBlock.IPositionPredicate needsPostProcessing;
        private final AbstractBlock.IPositionPredicate emissiveRendering;
        @Nullable
        protected AbstractBlock.AbstractBlockState.Cache cache;

        protected AbstractBlockState(Block block, ImmutableMap < Property<?>, Comparable<? >> propertyValueMap, MapCodec<BlockState> stateCodec)
        {
            super(block, propertyValueMap, stateCodec);
            AbstractBlock.Properties abstractblock$properties = block.properties;
            this.lightLevel = abstractblock$properties.lightLevel.applyAsInt(this.getSelf());
            this.transparent = block.isTransparent(this.getSelf());
            this.isAir = abstractblock$properties.isAir;
            this.material = abstractblock$properties.material;
            this.materialColor = abstractblock$properties.blockColors.apply(this.getSelf());
            this.hardness = abstractblock$properties.hardness;
            this.requiresTool = abstractblock$properties.requiresTool;
            this.isSolid = abstractblock$properties.isSolid;
            this.isNormalCube = abstractblock$properties.isOpaque;
            this.blocksVisionChecker = abstractblock$properties.suffocates;
            this.blocksVision = abstractblock$properties.blocksVision;
            this.needsPostProcessing = abstractblock$properties.needsPostProcessing;
            this.emissiveRendering = abstractblock$properties.emmissiveRendering;
        }

        public void cacheState()
        {
            if (!this.getBlock().isVariableOpacity())
            {
                this.cache = new AbstractBlock.AbstractBlockState.Cache(this.getSelf());
            }
        }

        public Block getBlock()
        {
            return this.instance;
        }

        public Material getMaterial()
        {
            return this.material;
        }

        public boolean canEntitySpawn(IBlockReader worldIn, BlockPos pos, EntityType<?> type)
        {
            return this.getBlock().properties.allowsSpawn.test(this.getSelf(), worldIn, pos, type);
        }

        public boolean propagatesSkylightDown(IBlockReader worldIn, BlockPos pos)
        {
            return this.cache != null ? this.cache.propagatesSkylightDown : this.getBlock().propagatesSkylightDown(this.getSelf(), worldIn, pos);
        }

        public int getOpacity(IBlockReader worldIn, BlockPos pos)
        {
            return this.cache != null ? this.cache.opacity : this.getBlock().getOpacity(this.getSelf(), worldIn, pos);
        }

        public VoxelShape getFaceOcclusionShape(IBlockReader worldIn, BlockPos pos, Direction directionIn)
        {
            return this.cache != null && this.cache.renderShapes != null ? this.cache.renderShapes[directionIn.ordinal()] : VoxelShapes.getFaceShape(this.getRenderShapeTrue(worldIn, pos), directionIn);
        }

        public VoxelShape getRenderShapeTrue(IBlockReader reader, BlockPos pos)
        {
            return this.getBlock().getRenderShape(this.getSelf(), reader, pos);
        }

        public boolean isCollisionShapeLargerThanFullBlock()
        {
            return this.cache == null || this.cache.isCollisionShapeLargerThanFullBlock;
        }

        public boolean isTransparent()
        {
            return this.transparent;
        }

        public int getLightValue()
        {
            return this.lightLevel;
        }

        public boolean isAir()
        {
            return this.isAir;
        }

        public MaterialColor getMaterialColor(IBlockReader worldIn, BlockPos pos)
        {
            return this.materialColor;
        }

        public BlockState rotate(Rotation rot)
        {
            return this.getBlock().rotate(this.getSelf(), rot);
        }

        public BlockState mirror(Mirror mirrorIn)
        {
            return this.getBlock().mirror(this.getSelf(), mirrorIn);
        }

        public BlockRenderType getRenderType()
        {
            return this.getBlock().getRenderType(this.getSelf());
        }

        public boolean isEmissiveRendering(IBlockReader reader, BlockPos pos)
        {
            return this.emissiveRendering.test(this.getSelf(), reader, pos);
        }

        public float getAmbientOcclusionLightValue(IBlockReader reader, BlockPos pos)
        {
            return this.getBlock().getAmbientOcclusionLightValue(this.getSelf(), reader, pos);
        }

        public boolean isNormalCube(IBlockReader reader, BlockPos pos)
        {
            return this.isNormalCube.test(this.getSelf(), reader, pos);
        }

        public boolean canProvidePower()
        {
            return this.getBlock().canProvidePower(this.getSelf());
        }

        public int getWeakPower(IBlockReader blockAccess, BlockPos pos, Direction side)
        {
            return this.getBlock().getWeakPower(this.getSelf(), blockAccess, pos, side);
        }

        public boolean hasComparatorInputOverride()
        {
            return this.getBlock().hasComparatorInputOverride(this.getSelf());
        }

        public int getComparatorInputOverride(World worldIn, BlockPos pos)
        {
            return this.getBlock().getComparatorInputOverride(this.getSelf(), worldIn, pos);
        }

        public float getBlockHardness(IBlockReader worldIn, BlockPos pos)
        {
            return this.hardness;
        }

        public float getPlayerRelativeBlockHardness(PlayerEntity player, IBlockReader worldIn, BlockPos pos)
        {
            return this.getBlock().getPlayerRelativeBlockHardness(this.getSelf(), player, worldIn, pos);
        }

        public int getStrongPower(IBlockReader blockAccess, BlockPos pos, Direction side)
        {
            return this.getBlock().getStrongPower(this.getSelf(), blockAccess, pos, side);
        }

        public PushReaction getPushReaction()
        {
            return this.getBlock().getPushReaction(this.getSelf());
        }

        public boolean isOpaqueCube(IBlockReader worldIn, BlockPos pos)
        {
            if (this.cache != null)
            {
                return this.cache.opaqueCube;
            }
            else
            {
                BlockState blockstate = this.getSelf();
                return blockstate.isSolid() ? Block.isOpaque(blockstate.getRenderShapeTrue(worldIn, pos)) : false;
            }
        }

        public boolean isSolid()
        {
            return this.isSolid;
        }

        public boolean isSideInvisible(BlockState state, Direction face)
        {
            return this.getBlock().isSideInvisible(this.getSelf(), state, face);
        }

        public VoxelShape getShape(IBlockReader worldIn, BlockPos pos)
        {
            return this.getShape(worldIn, pos, ISelectionContext.dummy());
        }

        public VoxelShape getShape(IBlockReader worldIn, BlockPos pos, ISelectionContext context)
        {
            return this.getBlock().getShape(this.getSelf(), worldIn, pos, context);
        }

        public VoxelShape getCollisionShape(IBlockReader worldIn, BlockPos pos)
        {
            return this.cache != null ? this.cache.collisionShape : this.getCollisionShape(worldIn, pos, ISelectionContext.dummy());
        }

        public VoxelShape getCollisionShape(IBlockReader worldIn, BlockPos pos, ISelectionContext context)
        {
            return this.getBlock().getCollisionShape(this.getSelf(), worldIn, pos, context);
        }

        public VoxelShape getRenderShape(IBlockReader worldIn, BlockPos pos)
        {
            return this.getBlock().getCollisionShape(this.getSelf(), worldIn, pos);
        }

        public VoxelShape getRaytraceShape(IBlockReader worldIn, BlockPos pos, ISelectionContext context)
        {
            return this.getBlock().getRayTraceShape(this.getSelf(), worldIn, pos, context);
        }

        public VoxelShape getRayTraceShape(IBlockReader reader, BlockPos pos)
        {
            return this.getBlock().getRaytraceShape(this.getSelf(), reader, pos);
        }

        public final boolean canSpawnMobs(IBlockReader reader, BlockPos pos, Entity entity)
        {
            return this.isTopSolid(reader, pos, entity, Direction.UP);
        }

        public final boolean isTopSolid(IBlockReader reader, BlockPos pos, Entity entityIn, Direction direction)
        {
            return Block.doesSideFillSquare(this.getCollisionShape(reader, pos, ISelectionContext.forEntity(entityIn)), direction);
        }

        public Vector3d getOffset(IBlockReader access, BlockPos pos)
        {
            AbstractBlock.OffsetType abstractblock$offsettype = this.getBlock().getOffsetType();

            if (abstractblock$offsettype == AbstractBlock.OffsetType.NONE)
            {
                return Vector3d.ZERO;
            }
            else
            {
                long i = MathHelper.getCoordinateRandom(pos.getX(), 0, pos.getZ());
                return new Vector3d(((double)((float)(i & 15L) / 15.0F) - 0.5D) * 0.5D, abstractblock$offsettype == AbstractBlock.OffsetType.XYZ ? ((double)((float)(i >> 4 & 15L) / 15.0F) - 1.0D) * 0.2D : 0.0D, ((double)((float)(i >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D);
            }
        }

        public boolean receiveBlockEvent(World world, BlockPos pos, int id, int param)
        {
            return this.getBlock().eventReceived(this.getSelf(), world, pos, id, param);
        }

        public void neighborChanged(World worldIn, BlockPos posIn, Block blockIn, BlockPos fromPosIn, boolean isMoving)
        {
            this.getBlock().neighborChanged(this.getSelf(), worldIn, posIn, blockIn, fromPosIn, isMoving);
        }

        public final void updateNeighbours(IWorld world, BlockPos pos, int flag)
        {
            this.updateNeighbours(world, pos, flag, 512);
        }

        public final void updateNeighbours(IWorld world, BlockPos pos, int flag, int recursionLeft)
        {
            this.getBlock();
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

            for (Direction direction : AbstractBlock.UPDATE_ORDER)
            {
                blockpos$mutable.setAndMove(pos, direction);
                BlockState blockstate = world.getBlockState(blockpos$mutable);
                BlockState blockstate1 = blockstate.updatePostPlacement(direction.getOpposite(), this.getSelf(), world, blockpos$mutable, pos);
                Block.replaceBlockState(blockstate, blockstate1, world, blockpos$mutable, flag, recursionLeft);
            }
        }

        public final void updateDiagonalNeighbors(IWorld worldIn, BlockPos pos, int flags)
        {
            this.updateDiagonalNeighbors(worldIn, pos, flags, 512);
        }

        public void updateDiagonalNeighbors(IWorld world, BlockPos pos, int flags, int recursionLeft)
        {
            this.getBlock().updateDiagonalNeighbors(this.getSelf(), world, pos, flags, recursionLeft);
        }

        public void onBlockAdded(World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
        {
            this.getBlock().onBlockAdded(this.getSelf(), worldIn, pos, oldState, isMoving);
        }

        public void onReplaced(World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
        {
            this.getBlock().onReplaced(this.getSelf(), worldIn, pos, newState, isMoving);
        }

        public void tick(ServerWorld worldIn, BlockPos posIn, Random randomIn)
        {
            this.getBlock().tick(this.getSelf(), worldIn, posIn, randomIn);
        }

        public void randomTick(ServerWorld worldIn, BlockPos posIn, Random randomIn)
        {
            this.getBlock().randomTick(this.getSelf(), worldIn, posIn, randomIn);
        }

        public void onEntityCollision(World worldIn, BlockPos pos, Entity entityIn)
        {
            this.getBlock().onEntityCollision(this.getSelf(), worldIn, pos, entityIn);
        }

        public void spawnAdditionalDrops(ServerWorld worldIn, BlockPos pos, ItemStack stack)
        {
            this.getBlock().spawnAdditionalDrops(this.getSelf(), worldIn, pos, stack);
        }

        public List<ItemStack> getDrops(LootContext.Builder builder)
        {
            return this.getBlock().getDrops(this.getSelf(), builder);
        }

        public ActionResultType onBlockActivated(World worldIn, PlayerEntity player, Hand handIn, BlockRayTraceResult resultIn)
        {
            return this.getBlock().onBlockActivated(this.getSelf(), worldIn, resultIn.getPos(), player, handIn, resultIn);
        }

        public void onBlockClicked(World worldIn, BlockPos pos, PlayerEntity player)
        {
            this.getBlock().onBlockClicked(this.getSelf(), worldIn, pos, player);
        }

        public boolean isSuffocating(IBlockReader blockReaderIn, BlockPos blockPosIn)
        {
            return this.blocksVisionChecker.test(this.getSelf(), blockReaderIn, blockPosIn);
        }

        public boolean causesSuffocation(IBlockReader worldIn, BlockPos pos)
        {
            return this.blocksVision.test(this.getSelf(), worldIn, pos);
        }

        public BlockState updatePostPlacement(Direction face, BlockState queried, IWorld worldIn, BlockPos currentPos, BlockPos offsetPos)
        {
            return this.getBlock().updatePostPlacement(this.getSelf(), face, queried, worldIn, currentPos, offsetPos);
        }

        public boolean allowsMovement(IBlockReader worldIn, BlockPos pos, PathType type)
        {
            return this.getBlock().allowsMovement(this.getSelf(), worldIn, pos, type);
        }

        public boolean isReplaceable(BlockItemUseContext useContext)
        {
            return this.getBlock().isReplaceable(this.getSelf(), useContext);
        }

        public boolean isReplaceable(Fluid fluidIn)
        {
            return this.getBlock().isReplaceable(this.getSelf(), fluidIn);
        }

        public boolean isValidPosition(IWorldReader worldIn, BlockPos pos)
        {
            return this.getBlock().isValidPosition(this.getSelf(), worldIn, pos);
        }

        public boolean blockNeedsPostProcessing(IBlockReader worldIn, BlockPos pos)
        {
            return this.needsPostProcessing.test(this.getSelf(), worldIn, pos);
        }

        @Nullable
        public INamedContainerProvider getContainer(World worldIn, BlockPos pos)
        {
            return this.getBlock().getContainer(this.getSelf(), worldIn, pos);
        }

        public boolean isIn(ITag<Block> tag)
        {
            return this.getBlock().isIn(tag);
        }

        public boolean isInAndMatches(ITag<Block> tag, Predicate<AbstractBlock.AbstractBlockState> predicate)
        {
            return this.getBlock().isIn(tag) && predicate.test(this);
        }

        public boolean isIn(Block tagIn)
        {
            return this.getBlock().matchesBlock(tagIn);
        }

        public FluidState getFluidState()
        {
            return this.getBlock().getFluidState(this.getSelf());
        }

        public boolean ticksRandomly()
        {
            return this.getBlock().ticksRandomly(this.getSelf());
        }

        public long getPositionRandom(BlockPos pos)
        {
            return this.getBlock().getPositionRandom(this.getSelf(), pos);
        }

        public SoundType getSoundType()
        {
            return this.getBlock().getSoundType(this.getSelf());
        }

        public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile)
        {
            this.getBlock().onProjectileCollision(worldIn, state, hit, projectile);
        }

        public boolean isSolidSide(IBlockReader blockReaderIn, BlockPos blockPosIn, Direction directionIn)
        {
            return this.func_242698_a(blockReaderIn, blockPosIn, directionIn, BlockVoxelShape.FULL);
        }

        public boolean func_242698_a(IBlockReader blockReader, BlockPos pos, Direction direction, BlockVoxelShape blockVoxelShape)
        {
            return this.cache != null ? this.cache.isSolidSide(direction, blockVoxelShape) : blockVoxelShape.func_241854_a(this.getSelf(), blockReader, pos, direction);
        }

        public boolean hasOpaqueCollisionShape(IBlockReader reader, BlockPos pos)
        {
            return this.cache != null ? this.cache.opaqueCollisionShape : Block.isOpaque(this.getCollisionShape(reader, pos));
        }

        protected abstract BlockState getSelf();

        public boolean getRequiresTool()
        {
            return this.requiresTool;
        }

        static final class Cache
        {
            private static final Direction[] DIRECTIONS = Direction.values();
            private static final int shapeValueLength = BlockVoxelShape.values().length;
            protected final boolean opaqueCube;
            private final boolean propagatesSkylightDown;
            private final int opacity;
            @Nullable
            private final VoxelShape[] renderShapes;
            protected final VoxelShape collisionShape;
            protected final boolean isCollisionShapeLargerThanFullBlock;
            private final boolean[] solidSides;
            protected final boolean opaqueCollisionShape;

            private Cache(BlockState stateIn)
            {
                Block block = stateIn.getBlock();
                this.opaqueCube = stateIn.isOpaqueCube(EmptyBlockReader.INSTANCE, BlockPos.ZERO);
                this.propagatesSkylightDown = block.propagatesSkylightDown(stateIn, EmptyBlockReader.INSTANCE, BlockPos.ZERO);
                this.opacity = block.getOpacity(stateIn, EmptyBlockReader.INSTANCE, BlockPos.ZERO);

                if (!stateIn.isSolid())
                {
                    this.renderShapes = null;
                }
                else
                {
                    this.renderShapes = new VoxelShape[DIRECTIONS.length];
                    VoxelShape voxelshape = block.getRenderShape(stateIn, EmptyBlockReader.INSTANCE, BlockPos.ZERO);

                    for (Direction direction : DIRECTIONS)
                    {
                        this.renderShapes[direction.ordinal()] = VoxelShapes.getFaceShape(voxelshape, direction);
                    }
                }

                this.collisionShape = block.getCollisionShape(stateIn, EmptyBlockReader.INSTANCE, BlockPos.ZERO, ISelectionContext.dummy());
                this.isCollisionShapeLargerThanFullBlock = Arrays.stream(Direction.Axis.values()).anyMatch((axis) ->
                {
                    return this.collisionShape.getStart(axis) < 0.0D || this.collisionShape.getEnd(axis) > 1.0D;
                });
                this.solidSides = new boolean[DIRECTIONS.length * shapeValueLength];

                for (Direction direction1 : DIRECTIONS)
                {
                    for (BlockVoxelShape blockvoxelshape : BlockVoxelShape.values())
                    {
                        this.solidSides[func_242701_b(direction1, blockvoxelshape)] = blockvoxelshape.func_241854_a(stateIn, EmptyBlockReader.INSTANCE, BlockPos.ZERO, direction1);
                    }
                }

                this.opaqueCollisionShape = Block.isOpaque(stateIn.getCollisionShape(EmptyBlockReader.INSTANCE, BlockPos.ZERO));
            }

            public boolean isSolidSide(Direction direction, BlockVoxelShape blockVoxelShape)
            {
                return this.solidSides[func_242701_b(direction, blockVoxelShape)];
            }

            private static int func_242701_b(Direction direction, BlockVoxelShape blockVoxelShape)
            {
                return direction.ordinal() * shapeValueLength + blockVoxelShape.ordinal();
            }
        }
    }

    public interface IExtendedPositionPredicate<A>
    {
        boolean test(BlockState p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_, A p_test_4_);
    }

    public interface IPositionPredicate
    {
        boolean test(BlockState p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_);
    }

    public static enum OffsetType
    {
        NONE,
        XZ,
        XYZ;
    }

    public static class Properties
    {
        private Material material;
        private Function<BlockState, MaterialColor> blockColors;
        private boolean blocksMovement = true;
        private SoundType soundType = SoundType.STONE;
        private ToIntFunction<BlockState> lightLevel = (light) ->
        {
            return 0;
        };
        private float resistance;
        private float hardness;
        private boolean requiresTool;
        private boolean ticksRandomly;
        private float slipperiness = 0.6F;
        private float speedFactor = 1.0F;
        private float jumpFactor = 1.0F;
        private ResourceLocation lootTable;
        private boolean isSolid = true;
        private boolean isAir;
        private AbstractBlock.IExtendedPositionPredicate < EntityType<? >> allowsSpawn = (state, reader, pos, entityType) ->
        {
            return state.isSolidSide(reader, pos, Direction.UP) && state.getLightValue() < 14;
        };
        private AbstractBlock.IPositionPredicate isOpaque = (state, reader, pos) ->
        {
            return state.getMaterial().isOpaque() && state.hasOpaqueCollisionShape(reader, pos);
        };
        private AbstractBlock.IPositionPredicate suffocates = (state, reader, pos) ->
        {
            return this.material.blocksMovement() && state.hasOpaqueCollisionShape(reader, pos);
        };
        private AbstractBlock.IPositionPredicate blocksVision = this.suffocates;
        private AbstractBlock.IPositionPredicate needsPostProcessing = (state, reader, pos) ->
        {
            return false;
        };
        private AbstractBlock.IPositionPredicate emmissiveRendering = (state, reader, pos) ->
        {
            return false;
        };
        private boolean variableOpacity;

        private Properties(Material materialIn, MaterialColor mapColorIn)
        {
            this(materialIn, (state) ->
            {
                return mapColorIn;
            });
        }

        private Properties(Material material, Function<BlockState, MaterialColor> stateColorFunction)
        {
            this.material = material;
            this.blockColors = stateColorFunction;
        }

        public static AbstractBlock.Properties create(Material materialIn)
        {
            return create(materialIn, materialIn.getColor());
        }

        public static AbstractBlock.Properties create(Material materialIn, DyeColor color)
        {
            return create(materialIn, color.getMapColor());
        }

        public static AbstractBlock.Properties create(Material materialIn, MaterialColor mapColorIn)
        {
            return new AbstractBlock.Properties(materialIn, mapColorIn);
        }

        public static AbstractBlock.Properties create(Material material, Function<BlockState, MaterialColor> stateColorFunction)
        {
            return new AbstractBlock.Properties(material, stateColorFunction);
        }

        public static AbstractBlock.Properties from(AbstractBlock blockIn)
        {
            AbstractBlock.Properties abstractblock$properties = new AbstractBlock.Properties(blockIn.material, blockIn.properties.blockColors);
            abstractblock$properties.material = blockIn.properties.material;
            abstractblock$properties.hardness = blockIn.properties.hardness;
            abstractblock$properties.resistance = blockIn.properties.resistance;
            abstractblock$properties.blocksMovement = blockIn.properties.blocksMovement;
            abstractblock$properties.ticksRandomly = blockIn.properties.ticksRandomly;
            abstractblock$properties.lightLevel = blockIn.properties.lightLevel;
            abstractblock$properties.blockColors = blockIn.properties.blockColors;
            abstractblock$properties.soundType = blockIn.properties.soundType;
            abstractblock$properties.slipperiness = blockIn.properties.slipperiness;
            abstractblock$properties.speedFactor = blockIn.properties.speedFactor;
            abstractblock$properties.variableOpacity = blockIn.properties.variableOpacity;
            abstractblock$properties.isSolid = blockIn.properties.isSolid;
            abstractblock$properties.isAir = blockIn.properties.isAir;
            abstractblock$properties.requiresTool = blockIn.properties.requiresTool;
            return abstractblock$properties;
        }

        public AbstractBlock.Properties doesNotBlockMovement()
        {
            this.blocksMovement = false;
            this.isSolid = false;
            return this;
        }

        public AbstractBlock.Properties notSolid()
        {
            this.isSolid = false;
            return this;
        }

        public AbstractBlock.Properties slipperiness(float slipperinessIn)
        {
            this.slipperiness = slipperinessIn;
            return this;
        }

        public AbstractBlock.Properties speedFactor(float factor)
        {
            this.speedFactor = factor;
            return this;
        }

        public AbstractBlock.Properties jumpFactor(float factor)
        {
            this.jumpFactor = factor;
            return this;
        }

        public AbstractBlock.Properties sound(SoundType soundTypeIn)
        {
            this.soundType = soundTypeIn;
            return this;
        }

        public AbstractBlock.Properties setLightLevel(ToIntFunction<BlockState> stateLightFunction)
        {
            this.lightLevel = stateLightFunction;
            return this;
        }

        public AbstractBlock.Properties hardnessAndResistance(float hardnessIn, float resistanceIn)
        {
            this.hardness = hardnessIn;
            this.resistance = Math.max(0.0F, resistanceIn);
            return this;
        }

        public AbstractBlock.Properties zeroHardnessAndResistance()
        {
            return this.hardnessAndResistance(0.0F);
        }

        public AbstractBlock.Properties hardnessAndResistance(float hardnessAndResistance)
        {
            this.hardnessAndResistance(hardnessAndResistance, hardnessAndResistance);
            return this;
        }

        public AbstractBlock.Properties tickRandomly()
        {
            this.ticksRandomly = true;
            return this;
        }

        public AbstractBlock.Properties variableOpacity()
        {
            this.variableOpacity = true;
            return this;
        }

        public AbstractBlock.Properties noDrops()
        {
            this.lootTable = LootTables.EMPTY;
            return this;
        }

        public AbstractBlock.Properties lootFrom(Block blockIn)
        {
            this.lootTable = blockIn.getLootTable();
            return this;
        }

        public AbstractBlock.Properties setAir()
        {
            this.isAir = true;
            return this;
        }

        public AbstractBlock.Properties setAllowsSpawn(AbstractBlock.IExtendedPositionPredicate < EntityType<? >> spawnPredicate)
        {
            this.allowsSpawn = spawnPredicate;
            return this;
        }

        public AbstractBlock.Properties setOpaque(AbstractBlock.IPositionPredicate opaquePredicate)
        {
            this.isOpaque = opaquePredicate;
            return this;
        }

        public AbstractBlock.Properties setSuffocates(AbstractBlock.IPositionPredicate suffocatesPredicate)
        {
            this.suffocates = suffocatesPredicate;
            return this;
        }

        public AbstractBlock.Properties setBlocksVision(AbstractBlock.IPositionPredicate blocksVisionPredicate)
        {
            this.blocksVision = blocksVisionPredicate;
            return this;
        }

        public AbstractBlock.Properties setNeedsPostProcessing(AbstractBlock.IPositionPredicate postProcessingPredicate)
        {
            this.needsPostProcessing = postProcessingPredicate;
            return this;
        }

        public AbstractBlock.Properties setEmmisiveRendering(AbstractBlock.IPositionPredicate emmisiveRenderPredicate)
        {
            this.emmissiveRendering = emmisiveRenderPredicate;
            return this;
        }

        public AbstractBlock.Properties setRequiresTool()
        {
            this.requiresTool = true;
            return this;
        }
    }
}
