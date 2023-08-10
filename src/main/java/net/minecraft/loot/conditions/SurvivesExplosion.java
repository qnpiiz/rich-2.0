package net.minecraft.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;

public class SurvivesExplosion implements ILootCondition
{
    private static final SurvivesExplosion INSTANCE = new SurvivesExplosion();

    private SurvivesExplosion()
    {
    }

    public LootConditionType func_230419_b_()
    {
        return LootConditionManager.SURVIVES_EXPLOSION;
    }

    public Set < LootParameter<? >> getRequiredParameters()
    {
        return ImmutableSet.of(LootParameters.EXPLOSION_RADIUS);
    }

    public boolean test(LootContext p_test_1_)
    {
        Float f = p_test_1_.get(LootParameters.EXPLOSION_RADIUS);

        if (f != null)
        {
            Random random = p_test_1_.getRandom();
            float f1 = 1.0F / f;
            return random.nextFloat() <= f1;
        }
        else
        {
            return true;
        }
    }

    public static ILootCondition.IBuilder builder()
    {
        return () ->
        {
            return INSTANCE;
        };
    }

    public static class Serializer implements ILootSerializer<SurvivesExplosion>
    {
        public void serialize(JsonObject p_230424_1_, SurvivesExplosion p_230424_2_, JsonSerializationContext p_230424_3_)
        {
        }

        public SurvivesExplosion deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_)
        {
            return SurvivesExplosion.INSTANCE;
        }
    }
}
