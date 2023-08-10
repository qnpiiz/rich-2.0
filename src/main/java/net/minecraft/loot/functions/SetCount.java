package net.minecraft.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.IRandomRange;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.RandomRanges;
import net.minecraft.loot.conditions.ILootCondition;

public class SetCount extends LootFunction
{
    private final IRandomRange countRange;

    private SetCount(ILootCondition[] p_i51222_1_, IRandomRange p_i51222_2_)
    {
        super(p_i51222_1_);
        this.countRange = p_i51222_2_;
    }

    public LootFunctionType getFunctionType()
    {
        return LootFunctionManager.SET_COUNT;
    }

    public ItemStack doApply(ItemStack stack, LootContext context)
    {
        stack.setCount(this.countRange.generateInt(context.getRandom()));
        return stack;
    }

    public static LootFunction.Builder<?> builder(IRandomRange p_215932_0_)
    {
        return builder((p_215934_1_) ->
        {
            return new SetCount(p_215934_1_, p_215932_0_);
        });
    }

    public static class Serializer extends LootFunction.Serializer<SetCount>
    {
        public void serialize(JsonObject p_230424_1_, SetCount p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
            p_230424_1_.add("count", RandomRanges.serialize(p_230424_2_.countRange, p_230424_3_));
        }

        public SetCount deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn)
        {
            IRandomRange irandomrange = RandomRanges.deserialize(object.get("count"), deserializationContext);
            return new SetCount(conditionsIn, irandomrange);
        }
    }
}
