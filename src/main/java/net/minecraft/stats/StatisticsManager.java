package net.minecraft.stats;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;

public class StatisticsManager
{
    protected final Object2IntMap < Stat<? >> statsData = Object2IntMaps.synchronize(new Object2IntOpenHashMap<>());

    public StatisticsManager()
    {
        this.statsData.defaultReturnValue(0);
    }

    public void increment(PlayerEntity player, Stat<?> stat, int amount)
    {
        int i = (int)Math.min((long)this.getValue(stat) + (long)amount, 2147483647L);
        this.setValue(player, stat, i);
    }

    /**
     * Triggers the logging of an achievement and attempts to announce to server
     */
    public void setValue(PlayerEntity playerIn, Stat<?> statIn, int p_150873_3_)
    {
        this.statsData.put(statIn, p_150873_3_);
    }

    public <T> int getValue(StatType<T> p_199060_1_, T p_199060_2_)
    {
        return p_199060_1_.contains(p_199060_2_) ? this.getValue(p_199060_1_.get(p_199060_2_)) : 0;
    }

    /**
     * Reads the given stat and returns its value as an int.
     */
    public int getValue(Stat<?> stat)
    {
        return this.statsData.getInt(stat);
    }
}
