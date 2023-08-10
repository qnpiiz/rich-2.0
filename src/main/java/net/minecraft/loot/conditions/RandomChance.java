package net.minecraft.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.util.JSONUtils;

public class RandomChance implements ILootCondition
{
    private final float chance;

    private RandomChance(float chanceIn)
    {
        this.chance = chanceIn;
    }

    public LootConditionType func_230419_b_()
    {
        return LootConditionManager.RANDOM_CHANCE;
    }

    public boolean test(LootContext p_test_1_)
    {
        return p_test_1_.getRandom().nextFloat() < this.chance;
    }

    public static ILootCondition.IBuilder builder(float chanceIn)
    {
        return () ->
        {
            return new RandomChance(chanceIn);
        };
    }

    public static class Serializer implements ILootSerializer<RandomChance>
    {
        public void serialize(JsonObject p_230424_1_, RandomChance p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            p_230424_1_.addProperty("chance", p_230424_2_.chance);
        }

        public RandomChance deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_)
        {
            return new RandomChance(JSONUtils.getFloat(p_230423_1_, "chance"));
        }
    }
}
