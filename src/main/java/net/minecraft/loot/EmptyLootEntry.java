package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;

public class EmptyLootEntry extends StandaloneLootEntry
{
    private EmptyLootEntry(int p_i51258_1_, int p_i51258_2_, ILootCondition[] p_i51258_3_, ILootFunction[] p_i51258_4_)
    {
        super(p_i51258_1_, p_i51258_2_, p_i51258_3_, p_i51258_4_);
    }

    public LootPoolEntryType func_230420_a_()
    {
        return LootEntryManager.EMPTY;
    }

    public void func_216154_a(Consumer<ItemStack> stackConsumer, LootContext context)
    {
    }

    public static StandaloneLootEntry.Builder<?> func_216167_a()
    {
        return builder(EmptyLootEntry::new);
    }

    public static class Serializer extends StandaloneLootEntry.Serializer<EmptyLootEntry>
    {
        public EmptyLootEntry deserialize(JsonObject object, JsonDeserializationContext context, int weight, int quality, ILootCondition[] conditions, ILootFunction[] functions)
        {
            return new EmptyLootEntry(weight, quality, conditions, functions);
        }
    }
}
