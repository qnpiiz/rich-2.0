package net.minecraft.loot.conditions;

import java.util.function.Predicate;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootTypesManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class LootConditionManager
{
    public static final LootConditionType INVERTED = register("inverted", new Inverted.Serializer());
    public static final LootConditionType ALTERNATIVE = register("alternative", new Alternative.Serializer());
    public static final LootConditionType RANDOM_CHANCE = register("random_chance", new RandomChance.Serializer());
    public static final LootConditionType RANDOM_CHANCE_WITH_LOOTING = register("random_chance_with_looting", new RandomChanceWithLooting.Serializer());
    public static final LootConditionType ENTITY_PROPERTIES = register("entity_properties", new EntityHasProperty.Serializer());
    public static final LootConditionType KILLED_BY_PLAYER = register("killed_by_player", new KilledByPlayer.Serializer());
    public static final LootConditionType ENTITY_SCORES = register("entity_scores", new EntityHasScore.Serializer());
    public static final LootConditionType BLOCK_STATE_PROPERTY = register("block_state_property", new BlockStateProperty.Serializer());
    public static final LootConditionType MATCH_TOOL = register("match_tool", new MatchTool.Serializer());
    public static final LootConditionType TABLE_BONUS = register("table_bonus", new TableBonus.Serializer());
    public static final LootConditionType SURVIVES_EXPLOSION = register("survives_explosion", new SurvivesExplosion.Serializer());
    public static final LootConditionType DAMAGE_SOURCE_PROPERTIES = register("damage_source_properties", new DamageSourceProperties.Serializer());
    public static final LootConditionType LOCATION_CHECK = register("location_check", new LocationCheck.Serializer());
    public static final LootConditionType WEATHER_CHECK = register("weather_check", new WeatherCheck.Serializer());
    public static final LootConditionType REFERENCE = register("reference", new Reference.Serializer());
    public static final LootConditionType TIME_CHECK = register("time_check", new TimeCheck.Serializer());

    private static LootConditionType register(String registryName, ILootSerializer <? extends ILootCondition > serializer)
    {
        return Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(registryName), new LootConditionType(serializer));
    }

    public static Object func_237474_a_()
    {
        return LootTypesManager.getLootTypeRegistryWrapper(Registry.LOOT_CONDITION_TYPE, "condition", "condition", ILootCondition::func_230419_b_).getSerializer();
    }

    public static <T> Predicate<T> and(Predicate<T>[] p_216305_0_)
    {
        switch (p_216305_0_.length)
        {
            case 0:
                return (p_216304_0_) ->
                {
                    return true;
                };
            case 1:
                return p_216305_0_[0];

            case 2:
                return p_216305_0_[0].and(p_216305_0_[1]);

            default:
                return (p_216307_1_) ->
                {
                    for (Predicate<T> predicate : p_216305_0_)
                    {
                        if (!predicate.test(p_216307_1_))
                        {
                            return false;
                        }
                    }

                    return true;
                };
        }
    }

    public static <T> Predicate<T> or(Predicate<T>[] p_216306_0_)
    {
        switch (p_216306_0_.length)
        {
            case 0:
                return (p_216308_0_) ->
                {
                    return false;
                };
            case 1:
                return p_216306_0_[0];

            case 2:
                return p_216306_0_[0].or(p_216306_0_[1]);

            default:
                return (p_216309_1_) ->
                {
                    for (Predicate<T> predicate : p_216306_0_)
                    {
                        if (predicate.test(p_216309_1_))
                        {
                            return true;
                        }
                    }

                    return false;
                };
        }
    }
}
