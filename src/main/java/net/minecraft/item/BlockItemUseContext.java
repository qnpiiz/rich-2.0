package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class BlockItemUseContext extends ItemUseContext
{
    private final BlockPos offsetPos;
    protected boolean replaceClicked = true;

    public BlockItemUseContext(PlayerEntity p_i241237_1_, Hand p_i241237_2_, ItemStack p_i241237_3_, BlockRayTraceResult p_i241237_4_)
    {
        this(p_i241237_1_.world, p_i241237_1_, p_i241237_2_, p_i241237_3_, p_i241237_4_);
    }

    public BlockItemUseContext(ItemUseContext context)
    {
        this(context.getWorld(), context.getPlayer(), context.getHand(), context.getItem(), context.func_242401_i());
    }

    protected BlockItemUseContext(World worldIn, @Nullable PlayerEntity playerIn, Hand handIn, ItemStack stackIn, BlockRayTraceResult rayTraceResultIn)
    {
        super(worldIn, playerIn, handIn, stackIn, rayTraceResultIn);
        this.offsetPos = rayTraceResultIn.getPos().offset(rayTraceResultIn.getFace());
        this.replaceClicked = worldIn.getBlockState(rayTraceResultIn.getPos()).isReplaceable(this);
    }

    public static BlockItemUseContext func_221536_a(BlockItemUseContext context, BlockPos pos, Direction directionIn)
    {
        return new BlockItemUseContext(context.getWorld(), context.getPlayer(), context.getHand(), context.getItem(), new BlockRayTraceResult(new Vector3d((double)pos.getX() + 0.5D + (double)directionIn.getXOffset() * 0.5D, (double)pos.getY() + 0.5D + (double)directionIn.getYOffset() * 0.5D, (double)pos.getZ() + 0.5D + (double)directionIn.getZOffset() * 0.5D), directionIn, pos, false));
    }

    public BlockPos getPos()
    {
        return this.replaceClicked ? super.getPos() : this.offsetPos;
    }

    public boolean canPlace()
    {
        return this.replaceClicked || this.getWorld().getBlockState(this.getPos()).isReplaceable(this);
    }

    public boolean replacingClickedOnBlock()
    {
        return this.replaceClicked;
    }

    public Direction getNearestLookingDirection()
    {
        return Direction.getFacingDirections(this.getPlayer())[0];
    }

    public Direction[] getNearestLookingDirections()
    {
        Direction[] adirection = Direction.getFacingDirections(this.getPlayer());

        if (this.replaceClicked)
        {
            return adirection;
        }
        else
        {
            Direction direction = this.getFace();
            int i;

            for (i = 0; i < adirection.length && adirection[i] != direction.getOpposite(); ++i)
            {
            }

            if (i > 0)
            {
                System.arraycopy(adirection, 0, adirection, 1, i);
                adirection[0] = direction.getOpposite();
            }

            return adirection;
        }
    }
}
