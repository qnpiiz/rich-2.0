package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class TNTBlock extends Block
{
    public static final BooleanProperty UNSTABLE = BlockStateProperties.UNSTABLE;

    public TNTBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(UNSTABLE, Boolean.valueOf(false)));
    }

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if (!oldState.isIn(state.getBlock()))
        {
            if (worldIn.isBlockPowered(pos))
            {
                explode(worldIn, pos);
                worldIn.removeBlock(pos, false);
            }
        }
    }

    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (worldIn.isBlockPowered(pos))
        {
            explode(worldIn, pos);
            worldIn.removeBlock(pos, false);
        }
    }

    /**
     * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually
     * collect this block
     */
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if (!worldIn.isRemote() && !player.isCreative() && state.get(UNSTABLE))
        {
            explode(worldIn, pos);
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    /**
     * Called when this Block is destroyed by an Explosion
     */
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn)
    {
        if (!worldIn.isRemote)
        {
            TNTEntity tntentity = new TNTEntity(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, explosionIn.getExplosivePlacedBy());
            tntentity.setFuse((short)(worldIn.rand.nextInt(tntentity.getFuse() / 4) + tntentity.getFuse() / 8));
            worldIn.addEntity(tntentity);
        }
    }

    public static void explode(World world, BlockPos worldIn)
    {
        explode(world, worldIn, (LivingEntity)null);
    }

    private static void explode(World worldIn, BlockPos pos, @Nullable LivingEntity entityIn)
    {
        if (!worldIn.isRemote)
        {
            TNTEntity tntentity = new TNTEntity(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, entityIn);
            worldIn.addEntity(tntentity);
            worldIn.playSound((PlayerEntity)null, tntentity.getPosX(), tntentity.getPosY(), tntentity.getPosZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        ItemStack itemstack = player.getHeldItem(handIn);
        Item item = itemstack.getItem();

        if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE)
        {
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
        }
        else
        {
            explode(worldIn, pos, player);
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);

            if (!player.isCreative())
            {
                if (item == Items.FLINT_AND_STEEL)
                {
                    itemstack.damageItem(1, player, (player1) ->
                    {
                        player1.sendBreakAnimation(handIn);
                    });
                }
                else
                {
                    itemstack.shrink(1);
                }
            }

            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
    }

    public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile)
    {
        if (!worldIn.isRemote)
        {
            Entity entity = projectile.func_234616_v_();

            if (projectile.isBurning())
            {
                BlockPos blockpos = hit.getPos();
                explode(worldIn, blockpos, entity instanceof LivingEntity ? (LivingEntity)entity : null);
                worldIn.removeBlock(blockpos, false);
            }
        }
    }

    /**
     * Return whether this block can drop from an explosion.
     */
    public boolean canDropFromExplosion(Explosion explosionIn)
    {
        return false;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(UNSTABLE);
    }
}
