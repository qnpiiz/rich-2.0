package net.minecraft.block;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class RedstoneOreBlock extends Block
{
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public RedstoneOreBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(LIT, Boolean.valueOf(false)));
    }

    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player)
    {
        activate(state, worldIn, pos);
        super.onBlockClicked(state, worldIn, pos, player);
    }

    /**
     * Called when the given entity walks on this Block
     */
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
    {
        activate(worldIn.getBlockState(pos), worldIn, pos);
        super.onEntityWalk(worldIn, pos, entityIn);
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (worldIn.isRemote)
        {
            spawnParticles(worldIn, pos);
        }
        else
        {
            activate(state, worldIn, pos);
        }

        ItemStack itemstack = player.getHeldItem(handIn);
        return itemstack.getItem() instanceof BlockItem && (new BlockItemUseContext(player, handIn, itemstack, hit)).canPlace() ? ActionResultType.PASS : ActionResultType.SUCCESS;
    }

    private static void activate(BlockState state, World world, BlockPos pos)
    {
        spawnParticles(world, pos);

        if (!state.get(LIT))
        {
            world.setBlockState(pos, state.with(LIT, Boolean.valueOf(true)), 3);
        }
    }

    /**
     * Returns whether or not this block is of a type that needs random ticking. Called for ref-counting purposes by
     * ExtendedBlockStorage in order to broadly cull a chunk from the random chunk update list for efficiency's sake.
     */
    public boolean ticksRandomly(BlockState state)
    {
        return state.get(LIT);
    }

    /**
     * Performs a random tick on a block.
     */
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        if (state.get(LIT))
        {
            worldIn.setBlockState(pos, state.with(LIT, Boolean.valueOf(false)), 3);
        }
    }

    /**
     * Perform side-effects from block dropping, such as creating silverfish
     */
    public void spawnAdditionalDrops(BlockState state, ServerWorld worldIn, BlockPos pos, ItemStack stack)
    {
        super.spawnAdditionalDrops(state, worldIn, pos, stack);

        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0)
        {
            int i = 1 + worldIn.rand.nextInt(5);
            this.dropXpOnBlockBreak(worldIn, pos, i);
        }
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
            spawnParticles(worldIn, pos);
        }
    }

    private static void spawnParticles(World world, BlockPos worldIn)
    {
        double d0 = 0.5625D;
        Random random = world.rand;

        for (Direction direction : Direction.values())
        {
            BlockPos blockpos = worldIn.offset(direction);

            if (!world.getBlockState(blockpos).isOpaqueCube(world, blockpos))
            {
                Direction.Axis direction$axis = direction.getAxis();
                double d1 = direction$axis == Direction.Axis.X ? 0.5D + 0.5625D * (double)direction.getXOffset() : (double)random.nextFloat();
                double d2 = direction$axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double)direction.getYOffset() : (double)random.nextFloat();
                double d3 = direction$axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double)direction.getZOffset() : (double)random.nextFloat();
                world.addParticle(RedstoneParticleData.REDSTONE_DUST, (double)worldIn.getX() + d1, (double)worldIn.getY() + d2, (double)worldIn.getZ() + d3, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(LIT);
    }
}
