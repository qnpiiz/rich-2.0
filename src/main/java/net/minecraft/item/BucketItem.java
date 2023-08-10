package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BucketItem extends Item
{
    private final Fluid containedBlock;

    public BucketItem(Fluid containedFluidIn, Item.Properties builder)
    {
        super(builder);
        this.containedBlock = containedFluidIn;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, this.containedBlock == Fluids.EMPTY ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);

        if (raytraceresult.getType() == RayTraceResult.Type.MISS)
        {
            return ActionResult.resultPass(itemstack);
        }
        else if (raytraceresult.getType() != RayTraceResult.Type.BLOCK)
        {
            return ActionResult.resultPass(itemstack);
        }
        else
        {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)raytraceresult;
            BlockPos blockpos = blockraytraceresult.getPos();
            Direction direction = blockraytraceresult.getFace();
            BlockPos blockpos1 = blockpos.offset(direction);

            if (worldIn.isBlockModifiable(playerIn, blockpos) && playerIn.canPlayerEdit(blockpos1, direction, itemstack))
            {
                if (this.containedBlock == Fluids.EMPTY)
                {
                    BlockState blockstate1 = worldIn.getBlockState(blockpos);

                    if (blockstate1.getBlock() instanceof IBucketPickupHandler)
                    {
                        Fluid fluid = ((IBucketPickupHandler)blockstate1.getBlock()).pickupFluid(worldIn, blockpos, blockstate1);

                        if (fluid != Fluids.EMPTY)
                        {
                            playerIn.addStat(Stats.ITEM_USED.get(this));
                            playerIn.playSound(fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                            ItemStack itemstack1 = DrinkHelper.fill(itemstack, playerIn, new ItemStack(fluid.getFilledBucket()));

                            if (!worldIn.isRemote)
                            {
                                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)playerIn, new ItemStack(fluid.getFilledBucket()));
                            }

                            return ActionResult.func_233538_a_(itemstack1, worldIn.isRemote());
                        }
                    }

                    return ActionResult.resultFail(itemstack);
                }
                else
                {
                    BlockState blockstate = worldIn.getBlockState(blockpos);
                    BlockPos blockpos2 = blockstate.getBlock() instanceof ILiquidContainer && this.containedBlock == Fluids.WATER ? blockpos : blockpos1;

                    if (this.tryPlaceContainedLiquid(playerIn, worldIn, blockpos2, blockraytraceresult))
                    {
                        this.onLiquidPlaced(worldIn, itemstack, blockpos2);

                        if (playerIn instanceof ServerPlayerEntity)
                        {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerIn, blockpos2, itemstack);
                        }

                        playerIn.addStat(Stats.ITEM_USED.get(this));
                        return ActionResult.func_233538_a_(this.emptyBucket(itemstack, playerIn), worldIn.isRemote());
                    }
                    else
                    {
                        return ActionResult.resultFail(itemstack);
                    }
                }
            }
            else
            {
                return ActionResult.resultFail(itemstack);
            }
        }
    }

    protected ItemStack emptyBucket(ItemStack stack, PlayerEntity player)
    {
        return !player.abilities.isCreativeMode ? new ItemStack(Items.BUCKET) : stack;
    }

    public void onLiquidPlaced(World worldIn, ItemStack p_203792_2_, BlockPos pos)
    {
    }

    public boolean tryPlaceContainedLiquid(@Nullable PlayerEntity player, World worldIn, BlockPos posIn, @Nullable BlockRayTraceResult rayTrace)
    {
        if (!(this.containedBlock instanceof FlowingFluid))
        {
            return false;
        }
        else
        {
            BlockState blockstate = worldIn.getBlockState(posIn);
            Block block = blockstate.getBlock();
            Material material = blockstate.getMaterial();
            boolean flag = blockstate.isReplaceable(this.containedBlock);
            boolean flag1 = blockstate.isAir() || flag || block instanceof ILiquidContainer && ((ILiquidContainer)block).canContainFluid(worldIn, posIn, blockstate, this.containedBlock);

            if (!flag1)
            {
                return rayTrace != null && this.tryPlaceContainedLiquid(player, worldIn, rayTrace.getPos().offset(rayTrace.getFace()), (BlockRayTraceResult)null);
            }
            else if (worldIn.getDimensionType().isUltrawarm() && this.containedBlock.isIn(FluidTags.WATER))
            {
                int i = posIn.getX();
                int j = posIn.getY();
                int k = posIn.getZ();
                worldIn.playSound(player, posIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

                for (int l = 0; l < 8; ++l)
                {
                    worldIn.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
                }

                return true;
            }
            else if (block instanceof ILiquidContainer && this.containedBlock == Fluids.WATER)
            {
                ((ILiquidContainer)block).receiveFluid(worldIn, posIn, blockstate, ((FlowingFluid)this.containedBlock).getStillFluidState(false));
                this.playEmptySound(player, worldIn, posIn);
                return true;
            }
            else
            {
                if (!worldIn.isRemote && flag && !material.isLiquid())
                {
                    worldIn.destroyBlock(posIn, true);
                }

                if (!worldIn.setBlockState(posIn, this.containedBlock.getDefaultState().getBlockState(), 11) && !blockstate.getFluidState().isSource())
                {
                    return false;
                }
                else
                {
                    this.playEmptySound(player, worldIn, posIn);
                    return true;
                }
            }
        }
    }

    protected void playEmptySound(@Nullable PlayerEntity player, IWorld worldIn, BlockPos pos)
    {
        SoundEvent soundevent = this.containedBlock.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
        worldIn.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }
}
