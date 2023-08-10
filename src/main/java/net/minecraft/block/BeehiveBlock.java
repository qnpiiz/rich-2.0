package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BeehiveBlock extends ContainerBlock
{
    private static final Direction[] GENERATE_DIRECTIONS = new Direction[] {Direction.WEST, Direction.EAST, Direction.SOUTH};
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final IntegerProperty HONEY_LEVEL = BlockStateProperties.HONEY_LEVEL;

    public BeehiveBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(HONEY_LEVEL, Integer.valueOf(0)).with(FACING, Direction.NORTH));
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
        return blockState.get(HONEY_LEVEL);
    }

    /**
     * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
     * Block.removedByPlayer
     */
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        super.harvestBlock(worldIn, player, pos, state, te, stack);

        if (!worldIn.isRemote && te instanceof BeehiveTileEntity)
        {
            BeehiveTileEntity beehivetileentity = (BeehiveTileEntity)te;

            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0)
            {
                beehivetileentity.angerBees(player, state, BeehiveTileEntity.State.EMERGENCY);
                worldIn.updateComparatorOutputLevel(pos, this);
                this.angerNearbyBees(worldIn, pos);
            }

            CriteriaTriggers.BEE_NEST_DESTROYED.test((ServerPlayerEntity)player, state.getBlock(), stack, beehivetileentity.getBeeCount());
        }
    }

    private void angerNearbyBees(World world, BlockPos pos)
    {
        List<BeeEntity> list = world.getEntitiesWithinAABB(BeeEntity.class, (new AxisAlignedBB(pos)).grow(8.0D, 6.0D, 8.0D));

        if (!list.isEmpty())
        {
            List<PlayerEntity> list1 = world.getEntitiesWithinAABB(PlayerEntity.class, (new AxisAlignedBB(pos)).grow(8.0D, 6.0D, 8.0D));
            int i = list1.size();

            for (BeeEntity beeentity : list)
            {
                if (beeentity.getAttackTarget() == null)
                {
                    beeentity.setAttackTarget(list1.get(world.rand.nextInt(i)));
                }
            }
        }
    }

    public static void dropHoneyComb(World world, BlockPos pos)
    {
        spawnAsEntity(world, pos, new ItemStack(Items.HONEYCOMB, 3));
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        ItemStack itemstack = player.getHeldItem(handIn);
        int i = state.get(HONEY_LEVEL);
        boolean flag = false;

        if (i >= 5)
        {
            if (itemstack.getItem() == Items.SHEARS)
            {
                worldIn.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                dropHoneyComb(worldIn, pos);
                itemstack.damageItem(1, player, (playerEntity) ->
                {
                    playerEntity.sendBreakAnimation(handIn);
                });
                flag = true;
            }
            else if (itemstack.getItem() == Items.GLASS_BOTTLE)
            {
                itemstack.shrink(1);
                worldIn.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                if (itemstack.isEmpty())
                {
                    player.setHeldItem(handIn, new ItemStack(Items.HONEY_BOTTLE));
                }
                else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.HONEY_BOTTLE)))
                {
                    player.dropItem(new ItemStack(Items.HONEY_BOTTLE), false);
                }

                flag = true;
            }
        }

        if (flag)
        {
            if (!CampfireBlock.isSmokingBlockAt(worldIn, pos))
            {
                if (this.hasBees(worldIn, pos))
                {
                    this.angerNearbyBees(worldIn, pos);
                }

                this.takeHoney(worldIn, state, pos, player, BeehiveTileEntity.State.EMERGENCY);
            }
            else
            {
                this.takeHoney(worldIn, state, pos);
            }

            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        else
        {
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
        }
    }

    private boolean hasBees(World world, BlockPos pos)
    {
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof BeehiveTileEntity)
        {
            BeehiveTileEntity beehivetileentity = (BeehiveTileEntity)tileentity;
            return !beehivetileentity.hasNoBees();
        }
        else
        {
            return false;
        }
    }

    public void takeHoney(World world, BlockState state, BlockPos pos, @Nullable PlayerEntity player, BeehiveTileEntity.State tileState)
    {
        this.takeHoney(world, state, pos);
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof BeehiveTileEntity)
        {
            BeehiveTileEntity beehivetileentity = (BeehiveTileEntity)tileentity;
            beehivetileentity.angerBees(player, state, tileState);
        }
    }

    public void takeHoney(World world, BlockState state, BlockPos pos)
    {
        world.setBlockState(pos, state.with(HONEY_LEVEL, Integer.valueOf(0)), 3);
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.get(HONEY_LEVEL) >= 5)
        {
            for (int i = 0; i < rand.nextInt(1) + 1; ++i)
            {
                this.addHoneyParticle(worldIn, pos, stateIn);
            }
        }
    }

    private void addHoneyParticle(World world, BlockPos pos, BlockState state)
    {
        if (state.getFluidState().isEmpty() && !(world.rand.nextFloat() < 0.3F))
        {
            VoxelShape voxelshape = state.getCollisionShape(world, pos);
            double d0 = voxelshape.getEnd(Direction.Axis.Y);

            if (d0 >= 1.0D && !state.isIn(BlockTags.IMPERMEABLE))
            {
                double d1 = voxelshape.getStart(Direction.Axis.Y);

                if (d1 > 0.0D)
                {
                    this.addHoneyParticle(world, pos, voxelshape, (double)pos.getY() + d1 - 0.05D);
                }
                else
                {
                    BlockPos blockpos = pos.down();
                    BlockState blockstate = world.getBlockState(blockpos);
                    VoxelShape voxelshape1 = blockstate.getCollisionShape(world, blockpos);
                    double d2 = voxelshape1.getEnd(Direction.Axis.Y);

                    if ((d2 < 1.0D || !blockstate.hasOpaqueCollisionShape(world, blockpos)) && blockstate.getFluidState().isEmpty())
                    {
                        this.addHoneyParticle(world, pos, voxelshape, (double)pos.getY() - 0.05D);
                    }
                }
            }
        }
    }

    private void addHoneyParticle(World world, BlockPos pos, VoxelShape shape, double y)
    {
        this.addHoneyParticle(world, (double)pos.getX() + shape.getStart(Direction.Axis.X), (double)pos.getX() + shape.getEnd(Direction.Axis.X), (double)pos.getZ() + shape.getStart(Direction.Axis.Z), (double)pos.getZ() + shape.getEnd(Direction.Axis.Z), y);
    }

    private void addHoneyParticle(World particleData, double x1, double x2, double z1, double z2, double y)
    {
        particleData.addParticle(ParticleTypes.DRIPPING_HONEY, MathHelper.lerp(particleData.rand.nextDouble(), x1, x2), y, MathHelper.lerp(particleData.rand.nextDouble(), z1, z2), 0.0D, 0.0D, 0.0D);
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(HONEY_LEVEL, FACING);
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

    @Nullable
    public TileEntity createNewTileEntity(IBlockReader worldIn)
    {
        return new BeehiveTileEntity();
    }

    /**
     * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually
     * collect this block
     */
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if (!worldIn.isRemote && player.isCreative() && worldIn.getGameRules().getBoolean(GameRules.DO_TILE_DROPS))
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof BeehiveTileEntity)
            {
                BeehiveTileEntity beehivetileentity = (BeehiveTileEntity)tileentity;
                ItemStack itemstack = new ItemStack(this);
                int i = state.get(HONEY_LEVEL);
                boolean flag = !beehivetileentity.hasNoBees();

                if (!flag && i == 0)
                {
                    return;
                }

                if (flag)
                {
                    CompoundNBT compoundnbt = new CompoundNBT();
                    compoundnbt.put("Bees", beehivetileentity.getBees());
                    itemstack.setTagInfo("BlockEntityTag", compoundnbt);
                }

                CompoundNBT compoundnbt1 = new CompoundNBT();
                compoundnbt1.putInt("honey_level", i);
                itemstack.setTagInfo("BlockStateTag", compoundnbt1);
                ItemEntity itementity = new ItemEntity(worldIn, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemstack);
                itementity.setDefaultPickupDelay();
                worldIn.addEntity(itementity);
            }
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        Entity entity = builder.get(LootParameters.THIS_ENTITY);

        if (entity instanceof TNTEntity || entity instanceof CreeperEntity || entity instanceof WitherSkullEntity || entity instanceof WitherEntity || entity instanceof TNTMinecartEntity)
        {
            TileEntity tileentity = builder.get(LootParameters.BLOCK_ENTITY);

            if (tileentity instanceof BeehiveTileEntity)
            {
                BeehiveTileEntity beehivetileentity = (BeehiveTileEntity)tileentity;
                beehivetileentity.angerBees((PlayerEntity)null, state, BeehiveTileEntity.State.EMERGENCY);
            }
        }

        return super.getDrops(state, builder);
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder
     * immediately returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (worldIn.getBlockState(facingPos).getBlock() instanceof FireBlock)
        {
            TileEntity tileentity = worldIn.getTileEntity(currentPos);

            if (tileentity instanceof BeehiveTileEntity)
            {
                BeehiveTileEntity beehivetileentity = (BeehiveTileEntity)tileentity;
                beehivetileentity.angerBees((PlayerEntity)null, stateIn, BeehiveTileEntity.State.EMERGENCY);
            }
        }

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public static Direction getGenerationDirection(Random rand)
    {
        return Util.getRandomObject(GENERATE_DIRECTIONS, rand);
    }
}
