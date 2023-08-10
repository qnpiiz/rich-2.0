package net.minecraft.loot;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class LootEntryManager
{
    public static final LootPoolEntryType EMPTY = register("empty", new EmptyLootEntry.Serializer());
    public static final LootPoolEntryType ITEM = register("item", new ItemLootEntry.Serializer());
    public static final LootPoolEntryType LOOT_TABLE = register("loot_table", new TableLootEntry.Serializer());
    public static final LootPoolEntryType DYNAMIC = register("dynamic", new DynamicLootEntry.Serializer());
    public static final LootPoolEntryType TAG = register("tag", new TagLootEntry.Serializer());
    public static final LootPoolEntryType ALTERNATIVE = register("alternatives", ParentedLootEntry.getSerializer(AlternativesLootEntry::new));
    public static final LootPoolEntryType SEQUENCE = register("sequence", ParentedLootEntry.getSerializer(SequenceLootEntry::new));
    public static final LootPoolEntryType GROUP = register("group", ParentedLootEntry.getSerializer(GroupLootEntry::new));

    private static LootPoolEntryType register(String name, ILootSerializer <? extends LootEntry > serializer)
    {
        return Registry.register(Registry.LOOT_POOL_ENTRY_TYPE, new ResourceLocation(name), new LootPoolEntryType(serializer));
    }

    public static Object func_237418_a_()
    {
        return LootTypesManager.getLootTypeRegistryWrapper(Registry.LOOT_POOL_ENTRY_TYPE, "entry", "type", LootEntry::func_230420_a_).getSerializer();
    }
}
