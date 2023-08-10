package net.minecraft.loot;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.loot.conditions.ILootCondition;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativesLootEntry extends ParentedLootEntry
{
    AlternativesLootEntry(LootEntry[] p_i51263_1_, ILootCondition[] p_i51263_2_)
    {
        super(p_i51263_1_, p_i51263_2_);
    }

    public LootPoolEntryType func_230420_a_()
    {
        return LootEntryManager.ALTERNATIVE;
    }

    protected ILootEntry combineChildren(ILootEntry[] entries)
    {
        switch (entries.length)
        {
            case 0:
                return field_216139_a;

            case 1:
                return entries[0];

            case 2:
                return entries[0].alternate(entries[1]);

            default:
                return (p_216150_1_, p_216150_2_) ->
                {
                    for (ILootEntry ilootentry : entries)
                    {
                        if (ilootentry.expand(p_216150_1_, p_216150_2_))
                        {
                            return true;
                        }
                    }

                    return false;
                };
        }
    }

    public void func_225579_a_(ValidationTracker p_225579_1_)
    {
        super.func_225579_a_(p_225579_1_);

        for (int i = 0; i < this.entries.length - 1; ++i)
        {
            if (ArrayUtils.isEmpty((Object[])this.entries[i].conditions))
            {
                p_225579_1_.addProblem("Unreachable entry!");
            }
        }
    }

    public static AlternativesLootEntry.Builder builder(LootEntry.Builder<?>... p_216149_0_)
    {
        return new AlternativesLootEntry.Builder(p_216149_0_);
    }

    public static class Builder extends LootEntry.Builder<AlternativesLootEntry.Builder>
    {
        private final List<LootEntry> lootEntries = Lists.newArrayList();

        public Builder(LootEntry.Builder<?>... p_i50579_1_)
        {
            for (LootEntry.Builder<?> builder : p_i50579_1_)
            {
                this.lootEntries.add(builder.build());
            }
        }

        protected AlternativesLootEntry.Builder func_212845_d_()
        {
            return this;
        }

        public AlternativesLootEntry.Builder alternatively(LootEntry.Builder<?> p_216080_1_)
        {
            this.lootEntries.add(p_216080_1_.build());
            return this;
        }

        public LootEntry build()
        {
            return new AlternativesLootEntry(this.lootEntries.toArray(new LootEntry[0]), this.func_216079_f());
        }
    }
}
