package net.minecraft.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.JSONUtils;

public class RandomChanceWithLooting implements ILootCondition
{
    private final float chance;
    private final float lootingMultiplier;

    private RandomChanceWithLooting(float chanceIn, float lootingMultiplierIn)
    {
        this.chance = chanceIn;
        this.lootingMultiplier = lootingMultiplierIn;
    }

    public LootConditionType func_230419_b_()
    {
        return LootConditionManager.RANDOM_CHANCE_WITH_LOOTING;
    }

    public Set < LootParameter<? >> getRequiredParameters()
    {
        return ImmutableSet.of(LootParameters.KILLER_ENTITY);
    }

    public boolean test(LootContext p_test_1_)
    {
        Entity entity = p_test_1_.get(LootParameters.KILLER_ENTITY);
        int i = 0;

        if (entity instanceof LivingEntity)
        {
            i = EnchantmentHelper.getLootingModifier((LivingEntity)entity);
        }

        return p_test_1_.getRandom().nextFloat() < this.chance + (float)i * this.lootingMultiplier;
    }

    public static ILootCondition.IBuilder builder(float chanceIn, float lootingMultiplierIn)
    {
        return () ->
        {
            return new RandomChanceWithLooting(chanceIn, lootingMultiplierIn);
        };
    }

    public static class Serializer implements ILootSerializer<RandomChanceWithLooting>
    {
        public void serialize(JsonObject p_230424_1_, RandomChanceWithLooting p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            p_230424_1_.addProperty("chance", p_230424_2_.chance);
            p_230424_1_.addProperty("looting_multiplier", p_230424_2_.lootingMultiplier);
        }

        public RandomChanceWithLooting deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_)
        {
            return new RandomChanceWithLooting(JSONUtils.getFloat(p_230423_1_, "chance"), JSONUtils.getFloat(p_230423_1_, "looting_multiplier"));
        }
    }
}
