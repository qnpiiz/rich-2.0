package net.minecraft.entity.ai.goal;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class CatSitOnBlockGoal extends MoveToBlockGoal
{
    private final CatEntity cat;

    public CatSitOnBlockGoal(CatEntity cat, double speed)
    {
        super(cat, speed, 8);
        this.cat = cat;
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute()
    {
        return this.cat.isTamed() && !this.cat.isSitting() && super.shouldExecute();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        super.startExecuting();
        this.cat.setSleeping(false);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        super.resetTask();
        this.cat.setSleeping(false);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick()
    {
        super.tick();
        this.cat.setSleeping(this.getIsAboveDestination());
    }

    /**
     * Return true to set given position as destination
     */
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos)
    {
        if (!worldIn.isAirBlock(pos.up()))
        {
            return false;
        }
        else
        {
            BlockState blockstate = worldIn.getBlockState(pos);

            if (blockstate.isIn(Blocks.CHEST))
            {
                return ChestTileEntity.getPlayersUsing(worldIn, pos) < 1;
            }
            else
            {
                return blockstate.isIn(Blocks.FURNACE) && blockstate.get(FurnaceBlock.LIT) ? true : blockstate.isInAndMatches(BlockTags.BEDS, (state) ->
                {
                    return state.<BedPart>func_235903_d_(BedBlock.PART).map((bedPart) -> {
                        return bedPart != BedPart.HEAD;
                    }).orElse(true);
                });
            }
        }
    }
}
