package net.minecraft.potion;

import com.google.common.collect.ComparisonChain;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EffectInstance implements Comparable<EffectInstance>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final Effect potion;
    private int duration;
    private int amplifier;
    private boolean isSplashPotion;
    private boolean ambient;

    /** True if potion effect duration is at maximum, false otherwise. */
    private boolean isPotionDurationMax;
    private boolean showParticles;
    private boolean showIcon;
    @Nullable

    /** A hidden effect which is not shown to the player. */
    private EffectInstance hiddenEffects;

    public EffectInstance(Effect potionIn)
    {
        this(potionIn, 0, 0);
    }

    public EffectInstance(Effect potionIn, int durationIn)
    {
        this(potionIn, durationIn, 0);
    }

    public EffectInstance(Effect potionIn, int durationIn, int amplifierIn)
    {
        this(potionIn, durationIn, amplifierIn, false, true);
    }

    public EffectInstance(Effect potionIn, int durationIn, int amplifierIn, boolean ambientIn, boolean showParticlesIn)
    {
        this(potionIn, durationIn, amplifierIn, ambientIn, showParticlesIn, showParticlesIn);
    }

    public EffectInstance(Effect potionIn, int durationIn, int amplifierIn, boolean ambientIn, boolean p_i48980_5_, boolean p_i48980_6_)
    {
        this(potionIn, durationIn, amplifierIn, ambientIn, p_i48980_5_, p_i48980_6_, (EffectInstance)null);
    }

    public EffectInstance(Effect p_i230050_1_, int p_i230050_2_, int p_i230050_3_, boolean p_i230050_4_, boolean p_i230050_5_, boolean p_i230050_6_, @Nullable EffectInstance p_i230050_7_)
    {
        this.potion = p_i230050_1_;
        this.duration = p_i230050_2_;
        this.amplifier = p_i230050_3_;
        this.ambient = p_i230050_4_;
        this.showParticles = p_i230050_5_;
        this.showIcon = p_i230050_6_;
        this.hiddenEffects = p_i230050_7_;
    }

    public EffectInstance(EffectInstance other)
    {
        this.potion = other.potion;
        this.func_230117_a_(other);
    }

    void func_230117_a_(EffectInstance p_230117_1_)
    {
        this.duration = p_230117_1_.duration;
        this.amplifier = p_230117_1_.amplifier;
        this.ambient = p_230117_1_.ambient;
        this.showParticles = p_230117_1_.showParticles;
        this.showIcon = p_230117_1_.showIcon;
    }

    public boolean combine(EffectInstance other)
    {
        if (this.potion != other.potion)
        {
            LOGGER.warn("This method should only be called for matching effects!");
        }

        boolean flag = false;

        if (other.amplifier > this.amplifier)
        {
            if (other.duration < this.duration)
            {
                EffectInstance effectinstance = this.hiddenEffects;
                this.hiddenEffects = new EffectInstance(this);
                this.hiddenEffects.hiddenEffects = effectinstance;
            }

            this.amplifier = other.amplifier;
            this.duration = other.duration;
            flag = true;
        }
        else if (other.duration > this.duration)
        {
            if (other.amplifier == this.amplifier)
            {
                this.duration = other.duration;
                flag = true;
            }
            else if (this.hiddenEffects == null)
            {
                this.hiddenEffects = new EffectInstance(other);
            }
            else
            {
                this.hiddenEffects.combine(other);
            }
        }

        if (!other.ambient && this.ambient || flag)
        {
            this.ambient = other.ambient;
            flag = true;
        }

        if (other.showParticles != this.showParticles)
        {
            this.showParticles = other.showParticles;
            flag = true;
        }

        if (other.showIcon != this.showIcon)
        {
            this.showIcon = other.showIcon;
            flag = true;
        }

        return flag;
    }

    public Effect getPotion()
    {
        return this.potion;
    }

    public int getDuration()
    {
        return this.duration;
    }

    public int getAmplifier()
    {
        return this.amplifier;
    }

    /**
     * Gets whether this potion effect originated from a beacon
     */
    public boolean isAmbient()
    {
        return this.ambient;
    }

    /**
     * Gets whether this potion effect will show ambient particles or not.
     */
    public boolean doesShowParticles()
    {
        return this.showParticles;
    }

    public boolean isShowIcon()
    {
        return this.showIcon;
    }

    public boolean tick(LivingEntity entityIn, Runnable p_76455_2_)
    {
        if (this.duration > 0)
        {
            if (this.potion.isReady(this.duration, this.amplifier))
            {
                this.performEffect(entityIn);
            }

            this.deincrementDuration();

            if (this.duration == 0 && this.hiddenEffects != null)
            {
                this.func_230117_a_(this.hiddenEffects);
                this.hiddenEffects = this.hiddenEffects.hiddenEffects;
                p_76455_2_.run();
            }
        }

        return this.duration > 0;
    }

    private int deincrementDuration()
    {
        if (this.hiddenEffects != null)
        {
            this.hiddenEffects.deincrementDuration();
        }

        return --this.duration;
    }

    public void performEffect(LivingEntity entityIn)
    {
        if (this.duration > 0)
        {
            this.potion.performEffect(entityIn, this.amplifier);
        }
    }

    public String getEffectName()
    {
        return this.potion.getName();
    }

    public String toString()
    {
        String s;

        if (this.amplifier > 0)
        {
            s = this.getEffectName() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
        }
        else
        {
            s = this.getEffectName() + ", Duration: " + this.duration;
        }

        if (this.isSplashPotion)
        {
            s = s + ", Splash: true";
        }

        if (!this.showParticles)
        {
            s = s + ", Particles: false";
        }

        if (!this.showIcon)
        {
            s = s + ", Show Icon: false";
        }

        return s;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof EffectInstance))
        {
            return false;
        }
        else
        {
            EffectInstance effectinstance = (EffectInstance)p_equals_1_;
            return this.duration == effectinstance.duration && this.amplifier == effectinstance.amplifier && this.isSplashPotion == effectinstance.isSplashPotion && this.ambient == effectinstance.ambient && this.potion.equals(effectinstance.potion);
        }
    }

    public int hashCode()
    {
        int i = this.potion.hashCode();
        i = 31 * i + this.duration;
        i = 31 * i + this.amplifier;
        i = 31 * i + (this.isSplashPotion ? 1 : 0);
        return 31 * i + (this.ambient ? 1 : 0);
    }

    /**
     * Write a custom potion effect to a potion item's NBT data.
     */
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putByte("Id", (byte)Effect.getId(this.getPotion()));
        this.writeInternal(nbt);
        return nbt;
    }

    private void writeInternal(CompoundNBT nbt)
    {
        nbt.putByte("Amplifier", (byte)this.getAmplifier());
        nbt.putInt("Duration", this.getDuration());
        nbt.putBoolean("Ambient", this.isAmbient());
        nbt.putBoolean("ShowParticles", this.doesShowParticles());
        nbt.putBoolean("ShowIcon", this.isShowIcon());

        if (this.hiddenEffects != null)
        {
            CompoundNBT compoundnbt = new CompoundNBT();
            this.hiddenEffects.write(compoundnbt);
            nbt.put("HiddenEffect", compoundnbt);
        }
    }

    /**
     * Read a custom potion effect from a potion item's NBT data.
     */
    public static EffectInstance read(CompoundNBT nbt)
    {
        int i = nbt.getByte("Id");
        Effect effect = Effect.get(i);
        return effect == null ? null : readInternal(effect, nbt);
    }

    private static EffectInstance readInternal(Effect effect, CompoundNBT nbt)
    {
        int i = nbt.getByte("Amplifier");
        int j = nbt.getInt("Duration");
        boolean flag = nbt.getBoolean("Ambient");
        boolean flag1 = true;

        if (nbt.contains("ShowParticles", 1))
        {
            flag1 = nbt.getBoolean("ShowParticles");
        }

        boolean flag2 = flag1;

        if (nbt.contains("ShowIcon", 1))
        {
            flag2 = nbt.getBoolean("ShowIcon");
        }

        EffectInstance effectinstance = null;

        if (nbt.contains("HiddenEffect", 10))
        {
            effectinstance = readInternal(effect, nbt.getCompound("HiddenEffect"));
        }

        return new EffectInstance(effect, j, i < 0 ? 0 : i, flag, flag1, flag2, effectinstance);
    }

    /**
     * Toggle the isPotionDurationMax field.
     */
    public void setPotionDurationMax(boolean maxDuration)
    {
        this.isPotionDurationMax = maxDuration;
    }

    /**
     * Get the value of the isPotionDurationMax field.
     */
    public boolean getIsPotionDurationMax()
    {
        return this.isPotionDurationMax;
    }

    public int compareTo(EffectInstance p_compareTo_1_)
    {
        int i = 32147;
        return (this.getDuration() <= 32147 || p_compareTo_1_.getDuration() <= 32147) && (!this.isAmbient() || !p_compareTo_1_.isAmbient()) ? ComparisonChain.start().compare(this.isAmbient(), p_compareTo_1_.isAmbient()).compare(this.getDuration(), p_compareTo_1_.getDuration()).compare(this.getPotion().getLiquidColor(), p_compareTo_1_.getPotion().getLiquidColor()).result() : ComparisonChain.start().compare(this.isAmbient(), p_compareTo_1_.isAmbient()).compare(this.getPotion().getLiquidColor(), p_compareTo_1_.getPotion().getLiquidColor()).result();
    }
}
