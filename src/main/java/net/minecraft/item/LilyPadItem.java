package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;

public class LilyPadItem extends BlockItem
{
    public LilyPadItem(Block blockIn, Item.Properties builder)
    {
        super(blockIn, builder);
    }

    /**
     * Called when this item is used when targetting a Block
     */
    public ActionResultType onItemUse(ItemUseContext context)
    {
        return ActionResultType.PASS;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        BlockRayTraceResult blockraytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
        BlockRayTraceResult blockraytraceresult1 = blockraytraceresult.withPosition(blockraytraceresult.getPos().up());
        ActionResultType actionresulttype = super.onItemUse(new ItemUseContext(playerIn, handIn, blockraytraceresult1));
        return new ActionResult<>(actionresulttype, playerIn.getHeldItem(handIn));
    }
}
