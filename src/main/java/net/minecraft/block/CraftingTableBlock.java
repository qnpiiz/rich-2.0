package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class CraftingTableBlock extends Block
{
    private static final ITextComponent CONTAINER_NAME = new TranslationTextComponent("container.crafting");

    protected CraftingTableBlock(AbstractBlock.Properties properties)
    {
        super(properties);
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (worldIn.isRemote)
        {
            return ActionResultType.SUCCESS;
        }
        else
        {
            player.openContainer(state.getContainer(worldIn, pos));
            player.addStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return ActionResultType.CONSUME;
        }
    }

    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos)
    {
        return new SimpleNamedContainerProvider((id, inventory, player) ->
        {
            return new WorkbenchContainer(id, inventory, IWorldPosCallable.of(worldIn, pos));
        }, CONTAINER_NAME);
    }
}
