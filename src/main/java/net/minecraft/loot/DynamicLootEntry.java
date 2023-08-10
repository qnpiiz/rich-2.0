package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class DynamicLootEntry extends StandaloneLootEntry
{
    private final ResourceLocation name;

    private DynamicLootEntry(ResourceLocation p_i51260_1_, int p_i51260_2_, int p_i51260_3_, ILootCondition[] p_i51260_4_, ILootFunction[] p_i51260_5_)
    {
        super(p_i51260_2_, p_i51260_3_, p_i51260_4_, p_i51260_5_);
        this.name = p_i51260_1_;
    }

    public LootPoolEntryType func_230420_a_()
    {
        return LootEntryManager.DYNAMIC;
    }

    public void func_216154_a(Consumer<ItemStack> stackConsumer, LootContext context)
    {
        context.generateDynamicDrop(this.name, stackConsumer);
    }

    public static StandaloneLootEntry.Builder<?> func_216162_a(ResourceLocation p_216162_0_)
    {
        return builder((p_216164_1_, p_216164_2_, p_216164_3_, p_216164_4_) ->
        {
            return new DynamicLootEntry(p_216162_0_, p_216164_1_, p_216164_2_, p_216164_3_, p_216164_4_);
        });
    }

    public static class Serializer extends StandaloneLootEntry.Serializer<DynamicLootEntry>
    {
        public void doSerialize(JsonObject object, DynamicLootEntry context, JsonSerializationContext conditions)
        {
            super.doSerialize(object, context, conditions);
            object.addProperty("name", context.name.toString());
        }

        protected DynamicLootEntry deserialize(JsonObject object, JsonDeserializationContext context, int weight, int quality, ILootCondition[] conditions, ILootFunction[] functions)
        {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(object, "name"));
            return new DynamicLootEntry(resourcelocation, weight, quality, conditions, functions);
        }
    }
}
