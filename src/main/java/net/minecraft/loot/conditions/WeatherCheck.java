package net.minecraft.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.util.JSONUtils;
import net.minecraft.world.server.ServerWorld;

public class WeatherCheck implements ILootCondition
{
    @Nullable
    private final Boolean raining;
    @Nullable
    private final Boolean thundering;

    private WeatherCheck(@Nullable Boolean raining, @Nullable Boolean thundering)
    {
        this.raining = raining;
        this.thundering = thundering;
    }

    public LootConditionType func_230419_b_()
    {
        return LootConditionManager.WEATHER_CHECK;
    }

    public boolean test(LootContext p_test_1_)
    {
        ServerWorld serverworld = p_test_1_.getWorld();

        if (this.raining != null && this.raining != serverworld.isRaining())
        {
            return false;
        }
        else
        {
            return this.thundering == null || this.thundering == serverworld.isThundering();
        }
    }

    public static class Serializer implements ILootSerializer<WeatherCheck>
    {
        public void serialize(JsonObject p_230424_1_, WeatherCheck p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            p_230424_1_.addProperty("raining", p_230424_2_.raining);
            p_230424_1_.addProperty("thundering", p_230424_2_.thundering);
        }

        public WeatherCheck deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_)
        {
            Boolean obool = p_230423_1_.has("raining") ? JSONUtils.getBoolean(p_230423_1_, "raining") : null;
            Boolean obool1 = p_230423_1_.has("thundering") ? JSONUtils.getBoolean(p_230423_1_, "thundering") : null;
            return new WeatherCheck(obool, obool1);
        }
    }
}
