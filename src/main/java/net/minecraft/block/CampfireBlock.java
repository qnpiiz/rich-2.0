package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CampfireCookingRecipe;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CampfireBlock extends ContainerBlock implements IWaterLoggable
{
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty SIGNAL_FIRE = BlockStateProperties.SIGNAL_FIRE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SMOKING_SHAPE = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    private final boolean smokey;
    private final int fireDamage;

    public CampfireBlock(boolean smokey, int fireDamage, AbstractBlock.Properties properties)
    {
        super(properties);
        this.smokey = smokey;
        this.fireDamage = fireDamage;
        this.setDefaultState(this.stateContainer.getBaseState().with(LIT, Boolean.valueOf(true)).with(SIGNAL_FIRE, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)).with(FACING, Direction.NORTH));
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof CampfireTileEntity)
        {
            CampfireTileEntity campfiretileentity = (CampfireTileEntity)tileentity;
            ItemStack itemstack = player.getHeldItem(handIn);
            Optional<CampfireCookingRecipe> optional = campfiretileentity.findMatchingRecipe(itemstack);

            if (optional.isPresent())
            {
                if (!worldIn.isRemote && campfiretileentity.addItem(player.abilities.isCreativeMode ? itemstack.copy() : itemstack, optional.get().getCookTime()))
                {
                    player.addStat(Stats.INTERACT_WITH_CAMPFIRE);
                    return ActionResultType.SUCCESS;
                }

                return ActionResultType.CONSUME;
            }
        }

        return ActionResultType.PASS;
    }

    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if (!entityIn.isImmuneToFire() && state.get(LIT) && entityIn instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entityIn))
        {
            entityIn.attackEntityFrom(DamageSource.IN_FIRE, (float)this.fireDamage);
        }

        super.onEntityCollision(state, worldIn, pos, entityIn);
    }

    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!state.isIn(newState.getBlock()))
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof CampfireTileEntity)
            {
                InventoryHelper.dropItems(worldIn, pos, ((CampfireTileEntity)tileentity).getInventory());
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        IWorld iworld = context.getWorld();
        BlockPos blockpos = context.getPos();
        boolean flag = iworld.getFluidState(blockpos).getFluid() == Fluids.WATER;
        return this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(flag)).with(SIGNAL_FIRE, Boolean.valueOf(this.isHayBlock(iworld.getBlockState(blockpos.down())))).with(LIT, Boolean.valueOf(!flag)).with(FACING, context.getPlacementHorizontalFacing());
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

        return facing == Direction.DOWN ? stateIn.with(SIGNAL_FIRE, Boolean.valueOf(this.isHayBlock(facingState))) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    /**
     * Returns true if the block of the passed blockstate is a Hay block, otherwise false.
     */
    private boolean isHayBlock(BlockState stateIn)
    {
        return stateIn.isIn(Blocks.HAY_BLOCK);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
     */
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
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
            if (rand.nextInt(10) == 0)
            {
                worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.6F, false);
            }

            if (this.smokey && rand.nextInt(5) == 0)
            {
                for (int i = 0; i < rand.nextInt(1) + 1; ++i)
                {
                    worldIn.addParticle(ParticleTypes.LAVA, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double)(rand.nextFloat() / 2.0F), 5.0E-5D, (double)(rand.nextFloat() / 2.0F));
                }
            }
        }
    }

    public static void extinguish(IWorld world, BlockPos pos, BlockState state)
    {
        if (world.isRemote())
        {
            for (int i = 0; i < 20; ++i)
            {
                spawnSmokeParticles((World)world, pos, state.get(SIGNAL_FIRE), true);
            }
        }

        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof CampfireTileEntity)
        {
            ((CampfireTileEntity)tileentity).dropAllItems();
        }
    }

    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn)
    {
        if (!state.get(BlockStateProperties.WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER)
        {
            boolean flag = state.get(LIT);

            if (flag)
            {
                if (!worldIn.isRemote())
                {
                    worldIn.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }

                extinguish(worldIn, pos, state);
            }

            worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(true)).with(LIT, Boolean.valueOf(false)), 3);
            worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
            return true;
        }
        else
        {
            return false;
        }
    }

    public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile)
    {
        if (!worldIn.isRemote && projectile.isBurning())
        {
            Entity entity = projectile.func_234616_v_();
            boolean flag = entity == null || entity instanceof PlayerEntity || worldIn.getGameRules().getBoolean(GameRules.MOB_GRIEFING);

            if (flag && !state.get(LIT) && !state.get(WATERLOGGED))
            {
                BlockPos blockpos = hit.getPos();
                worldIn.setBlockState(blockpos, state.with(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
            }
        }
    }

    public static void spawnSmokeParticles(World worldIn, BlockPos pos, boolean isSignalFire, boolean spawnExtraSmoke)
    {
        Random random = worldIn.getRandom();
        BasicParticleType basicparticletype = isSignalFire ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
        worldIn.addOptionalParticle(basicparticletype, true, (double)pos.getX() + 0.5D + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + random.nextDouble() + random.nextDouble(), (double)pos.getZ() + 0.5D + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), 0.0D, 0.07D, 0.0D);

        if (spawnExtraSmoke)
        {
            worldIn.addParticle(ParticleTypes.SMOKE, (double)pos.getX() + 0.25D + random.nextDouble() / 2.0D * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + 0.4D, (double)pos.getZ() + 0.25D + random.nextDouble() / 2.0D * (double)(random.nextBoolean() ? 1 : -1), 0.0D, 0.005D, 0.0D);
        }
    }

    public static boolean isSmokingBlockAt(World world, BlockPos pos)
    {
        for (int i = 1; i <= 5; ++i)
        {
            BlockPos blockpos = pos.down(i);
            BlockState blockstate = world.getBlockState(blockpos);

            if (isLit(blockstate))
            {
                return true;
            }

            boolean flag = VoxelShapes.compare(SMOKING_SHAPE, blockstate.getCollisionShape(world, pos, ISelectionContext.dummy()), IBooleanFunction.AND);

            if (flag)
            {
                BlockState blockstate1 = world.getBlockState(blockpos.down());
                return isLit(blockstate1);
            }
        }

        return false;
    }

    public static boolean isLit(BlockState state)
    {
        return state.hasProperty(LIT) && state.isIn(BlockTags.CAMPFIRES) && state.get(LIT);
    }

    public FluidState getFluidState(BlockState state)
    {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
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
        builder.add(LIT, SIGNAL_FIRE, WATERLOGGED, FACING);
    }

    public TileEntity createNewTileEntity(IBlockReader worldIn)
    {
        return new CampfireTileEntity();
    }

    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        return false;
    }

    public static boolean canBeLit(BlockState state)
    {
        return state.isInAndMatches(BlockTags.CAMPFIRES, (stateIn) ->
        {
            return stateIn.hasProperty(BlockStateProperties.WATERLOGGED) && stateIn.hasProperty(BlockStateProperties.LIT);
        }) && !state.get(BlockStateProperties.WATERLOGGED) && !state.get(BlockStateProperties.LIT);
    }
}
