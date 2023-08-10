package net.minecraft.loot;

import com.google.gson.GsonBuilder;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.LootFunctionManager;

public class LootSerializers
{
    public static GsonBuilder func_237386_a_()
    {
        return (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(BinomialRange.class, new BinomialRange.Serializer()).registerTypeAdapter(ConstantRange.class, new ConstantRange.Serializer()).registerTypeHierarchyAdapter(ILootCondition.class, LootConditionManager.func_237474_a_()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer());
    }

    public static GsonBuilder func_237387_b_()
    {
        return func_237386_a_().registerTypeAdapter(IntClamper.class, new IntClamper.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, LootEntryManager.func_237418_a_()).registerTypeHierarchyAdapter(ILootFunction.class, LootFunctionManager.func_237450_a_());
    }

    public static GsonBuilder func_237388_c_()
    {
        return func_237387_b_().registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer());
    }
}
