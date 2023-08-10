package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ItemLootEntry extends StandaloneLootEntry
{
    private final Item item;

    private ItemLootEntry(Item itemIn, int weightIn, int qualityIn, ILootCondition[] conditionsIn, ILootFunction[] functionsIn)
    {
        super(weightIn, qualityIn, conditionsIn, functionsIn);
        this.item = itemIn;
    }

    public LootPoolEntryType func_230420_a_()
    {
        return LootEntryManager.ITEM;
    }

    public void func_216154_a(Consumer<ItemStack> stackConsumer, LootContext context)
    {
        stackConsumer.accept(new ItemStack(this.item));
    }

    public static StandaloneLootEntry.Builder<?> builder(IItemProvider itemIn)
    {
        return builder((p_216169_1_, p_216169_2_, p_216169_3_, p_216169_4_) ->
        {
            return new ItemLootEntry(itemIn.asItem(), p_216169_1_, p_216169_2_, p_216169_3_, p_216169_4_);
        });
    }

    public static class Serializer extends StandaloneLootEntry.Serializer<ItemLootEntry>
    {
        public void doSerialize(JsonObject object, ItemLootEntry context, JsonSerializationContext conditions)
        {
            super.doSerialize(object, context, conditions);
            ResourceLocation resourcelocation = Registry.ITEM.getKey(context.item);

            if (resourcelocation == null)
            {
                throw new IllegalArgumentException("Can't serialize unknown item " + context.item);
            }
            else
            {
                object.addProperty("name", resourcelocation.toString());
            }
        }

        protected ItemLootEntry deserialize(JsonObject object, JsonDeserializationContext context, int weight, int quality, ILootCondition[] conditions, ILootFunction[] functions)
        {
            Item item = JSONUtils.getItem(object, "name");
            return new ItemLootEntry(item, weight, quality, conditions, functions);
        }
    }
}
