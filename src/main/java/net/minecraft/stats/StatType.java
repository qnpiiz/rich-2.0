package net.minecraft.stats;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class StatType<T> implements Iterable<Stat<T>>
{
    private final Registry<T> registry;
    private final Map<T, Stat<T>> map = new IdentityHashMap<>();
    @Nullable
    private ITextComponent field_242169_c;

    public StatType(Registry<T> registry)
    {
        this.registry = registry;
    }

    public boolean contains(T stat)
    {
        return this.map.containsKey(stat);
    }

    public Stat<T> get(T p_199077_1_, IStatFormatter formatter)
    {
        return this.map.computeIfAbsent(p_199077_1_, (p_199075_2_) ->
        {
            return new Stat<>(this, p_199075_2_, formatter);
        });
    }

    public Registry<T> getRegistry()
    {
        return this.registry;
    }

    public Iterator<Stat<T>> iterator()
    {
        return this.map.values().iterator();
    }

    public Stat<T> get(T stat)
    {
        return this.get(stat, IStatFormatter.DEFAULT);
    }

    public String getTranslationKey()
    {
        return "stat_type." + Registry.STATS.getKey(this).toString().replace(':', '.');
    }

    public ITextComponent func_242170_d()
    {
        if (this.field_242169_c == null)
        {
            this.field_242169_c = new TranslationTextComponent(this.getTranslationKey());
        }

        return this.field_242169_c;
    }
}
