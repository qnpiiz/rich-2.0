package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;

public interface IItemPropertyGetter
{
    float call(ItemStack p_call_1_, @Nullable ClientWorld p_call_2_, @Nullable LivingEntity p_call_3_);
}
