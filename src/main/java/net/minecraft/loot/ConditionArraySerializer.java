package net.minecraft.loot;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.loot.conditions.ILootCondition;

public class ConditionArraySerializer
{
    public static final ConditionArraySerializer field_235679_a_ = new ConditionArraySerializer();
    private final Gson field_235680_b_ = LootSerializers.func_237386_a_().create();

    public final JsonElement func_235681_a_(ILootCondition[] p_235681_1_)
    {
        return this.field_235680_b_.toJsonTree(p_235681_1_);
    }
}
