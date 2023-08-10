package net.minecraft.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class SetLootTable extends LootFunction
{
    private final ResourceLocation field_215928_a;
    private final long field_215929_c;

    private SetLootTable(ILootCondition[] p_i51224_1_, ResourceLocation p_i51224_2_, long p_i51224_3_)
    {
        super(p_i51224_1_);
        this.field_215928_a = p_i51224_2_;
        this.field_215929_c = p_i51224_3_;
    }

    public LootFunctionType getFunctionType()
    {
        return LootFunctionManager.SET_LOOT_TABLE;
    }

    public ItemStack doApply(ItemStack stack, LootContext context)
    {
        if (stack.isEmpty())
        {
            return stack;
        }
        else
        {
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putString("LootTable", this.field_215928_a.toString());

            if (this.field_215929_c != 0L)
            {
                compoundnbt.putLong("LootTableSeed", this.field_215929_c);
            }

            stack.getOrCreateTag().put("BlockEntityTag", compoundnbt);
            return stack;
        }
    }

    public void func_225580_a_(ValidationTracker p_225580_1_)
    {
        if (p_225580_1_.func_227532_a_(this.field_215928_a))
        {
            p_225580_1_.addProblem("Table " + this.field_215928_a + " is recursively called");
        }
        else
        {
            super.func_225580_a_(p_225580_1_);
            LootTable loottable = p_225580_1_.func_227539_c_(this.field_215928_a);

            if (loottable == null)
            {
                p_225580_1_.addProblem("Unknown loot table called " + this.field_215928_a);
            }
            else
            {
                loottable.validate(p_225580_1_.func_227531_a_("->{" + this.field_215928_a + "}", this.field_215928_a));
            }
        }
    }

    public static class Serializer extends LootFunction.Serializer<SetLootTable>
    {
        public void serialize(JsonObject p_230424_1_, SetLootTable p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
            p_230424_1_.addProperty("name", p_230424_2_.field_215928_a.toString());

            if (p_230424_2_.field_215929_c != 0L)
            {
                p_230424_1_.addProperty("seed", p_230424_2_.field_215929_c);
            }
        }

        public SetLootTable deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn)
        {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(object, "name"));
            long i = JSONUtils.getLong(object, "seed", 0L);
            return new SetLootTable(conditionsIn, resourcelocation, i);
        }
    }
}
