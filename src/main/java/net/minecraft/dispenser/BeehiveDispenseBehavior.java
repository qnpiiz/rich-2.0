package net.minecraft.dispenser;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.IShearable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BeehiveDispenseBehavior extends OptionalDispenseBehavior
{
    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
    {
        World world = source.getWorld();

        if (!world.isRemote())
        {
            BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
            this.setSuccessful(shearComb((ServerWorld)world, blockpos) || shear((ServerWorld)world, blockpos));

            if (this.isSuccessful() && stack.attemptDamageItem(1, world.getRandom(), (ServerPlayerEntity)null))
            {
                stack.setCount(0);
            }
        }

        return stack;
    }

    private static boolean shearComb(ServerWorld world, BlockPos pos)
    {
        BlockState blockstate = world.getBlockState(pos);

        if (blockstate.isIn(BlockTags.BEEHIVES))
        {
            int i = blockstate.get(BeehiveBlock.HONEY_LEVEL);

            if (i >= 5)
            {
                world.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
                BeehiveBlock.dropHoneyComb(world, pos);
                ((BeehiveBlock)blockstate.getBlock()).takeHoney(world, blockstate, pos, (PlayerEntity)null, BeehiveTileEntity.State.BEE_RELEASED);
                return true;
            }
        }

        return false;
    }

    private static boolean shear(ServerWorld world, BlockPos pos)
    {
        for (LivingEntity livingentity : world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos), EntityPredicates.NOT_SPECTATING))
        {
            if (livingentity instanceof IShearable)
            {
                IShearable ishearable = (IShearable)livingentity;

                if (ishearable.isShearable())
                {
                    ishearable.shear(SoundCategory.BLOCKS);
                    return true;
                }
            }
        }

        return false;
    }
}
