package net.minecraft.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;

public final class EffectUtils
{
    public static String getPotionDurationString(EffectInstance effect, float durationFactor)
    {
        if (effect.getIsPotionDurationMax())
        {
            return "**:**";
        }
        else
        {
            int i = MathHelper.floor((float)effect.getDuration() * durationFactor);
            return StringUtils.ticksToElapsedTime(i);
        }
    }

    public static boolean hasMiningSpeedup(LivingEntity entity)
    {
        return entity.isPotionActive(Effects.HASTE) || entity.isPotionActive(Effects.CONDUIT_POWER);
    }

    public static int getMiningSpeedup(LivingEntity entity)
    {
        int i = 0;
        int j = 0;

        if (entity.isPotionActive(Effects.HASTE))
        {
            i = entity.getActivePotionEffect(Effects.HASTE).getAmplifier();
        }

        if (entity.isPotionActive(Effects.CONDUIT_POWER))
        {
            j = entity.getActivePotionEffect(Effects.CONDUIT_POWER).getAmplifier();
        }

        return Math.max(i, j);
    }

    public static boolean canBreatheUnderwater(LivingEntity entity)
    {
        return entity.isPotionActive(Effects.WATER_BREATHING) || entity.isPotionActive(Effects.CONDUIT_POWER);
    }
}
