package net.minecraft.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;

public class HealthBoostEffect extends Effect
{
    public HealthBoostEffect(EffectType type, int liquidColor)
    {
        super(type, liquidColor);
    }

    public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier)
    {
        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);

        if (entityLivingBaseIn.getHealth() > entityLivingBaseIn.getMaxHealth())
        {
            entityLivingBaseIn.setHealth(entityLivingBaseIn.getMaxHealth());
        }
    }
}
