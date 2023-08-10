package net.minecraft.block;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class WoodButtonBlock extends AbstractButtonBlock
{
    protected WoodButtonBlock(AbstractBlock.Properties properties)
    {
        super(true, properties);
    }

    protected SoundEvent getSoundEvent(boolean isOn)
    {
        return isOn ? SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON : SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF;
    }
}
