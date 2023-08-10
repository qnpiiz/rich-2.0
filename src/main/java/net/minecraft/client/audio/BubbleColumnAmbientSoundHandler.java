package net.minecraft.client.audio;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class BubbleColumnAmbientSoundHandler implements IAmbientSoundHandler
{
    private final ClientPlayerEntity player;
    private boolean prevTickInColumn;
    private boolean firstTick = true;

    public BubbleColumnAmbientSoundHandler(ClientPlayerEntity player)
    {
        this.player = player;
    }

    public void tick()
    {
        World world = this.player.world;
        BlockState blockstate = world.getStatesInArea(this.player.getBoundingBox().grow(0.0D, (double) - 0.4F, 0.0D).shrink(0.001D)).filter((state) ->
        {
            return state.isIn(Blocks.BUBBLE_COLUMN);
        }).findFirst().orElse((BlockState)null);

        if (blockstate != null)
        {
            if (!this.prevTickInColumn && !this.firstTick && blockstate.isIn(Blocks.BUBBLE_COLUMN) && !this.player.isSpectator())
            {
                boolean flag = blockstate.get(BubbleColumnBlock.DRAG);

                if (flag)
                {
                    this.player.playSound(SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 1.0F, 1.0F);
                }
                else
                {
                    this.player.playSound(SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE, 1.0F, 1.0F);
                }
            }

            this.prevTickInColumn = true;
        }
        else
        {
            this.prevTickInColumn = false;
        }

        this.firstTick = false;
    }
}
