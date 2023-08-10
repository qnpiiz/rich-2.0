package net.minecraft.block;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class StoneButtonBlock extends AbstractButtonBlock
{
    protected StoneButtonBlock(AbstractBlock.Properties properties)
    {
        super(false, properties);
    }

    protected SoundEvent getSoundEvent(boolean isOn)
    {
        return isOn ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
    }
}
