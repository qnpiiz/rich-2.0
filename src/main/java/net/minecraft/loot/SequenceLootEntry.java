package net.minecraft.loot;

import net.minecraft.loot.conditions.ILootCondition;

public class SequenceLootEntry extends ParentedLootEntry
{
    SequenceLootEntry(LootEntry[] children, ILootCondition[] conditions)
    {
        super(children, conditions);
    }

    public LootPoolEntryType func_230420_a_()
    {
        return LootEntryManager.SEQUENCE;
    }

    protected ILootEntry combineChildren(ILootEntry[] entries)
    {
        switch (entries.length)
        {
            case 0:
                return field_216140_b;

            case 1:
                return entries[0];

            case 2:
                return entries[0].sequence(entries[1]);

            default:
                return (context, generatorConsumer) ->
                {
                    for (ILootEntry ilootentry : entries)
                    {
                        if (!ilootentry.expand(context, generatorConsumer))
                        {
                            return false;
                        }
                    }

                    return true;
                };
        }
    }
}
