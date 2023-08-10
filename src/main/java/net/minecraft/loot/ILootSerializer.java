package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public interface ILootSerializer<T>
{
    void serialize(JsonObject p_230424_1_, T p_230424_2_, JsonSerializationContext p_230424_3_);

    T deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_);
}
