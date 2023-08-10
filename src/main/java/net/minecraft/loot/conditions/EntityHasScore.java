package net.minecraft.loot.conditions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.JSONUtils;

public class EntityHasScore implements ILootCondition
{
    private final Map<String, RandomValueRange> scores;
    private final LootContext.EntityTarget target;

    private EntityHasScore(Map<String, RandomValueRange> scoreIn, LootContext.EntityTarget targetIn)
    {
        this.scores = ImmutableMap.copyOf(scoreIn);
        this.target = targetIn;
    }

    public LootConditionType func_230419_b_()
    {
        return LootConditionManager.ENTITY_SCORES;
    }

    public Set < LootParameter<? >> getRequiredParameters()
    {
        return ImmutableSet.of(this.target.getParameter());
    }

    public boolean test(LootContext p_test_1_)
    {
        Entity entity = p_test_1_.get(this.target.getParameter());

        if (entity == null)
        {
            return false;
        }
        else
        {
            Scoreboard scoreboard = entity.world.getScoreboard();

            for (Entry<String, RandomValueRange> entry : this.scores.entrySet())
            {
                if (!this.entityScoreMatch(entity, scoreboard, entry.getKey(), entry.getValue()))
                {
                    return false;
                }
            }

            return true;
        }
    }

    protected boolean entityScoreMatch(Entity entityIn, Scoreboard scoreboardIn, String objectiveStr, RandomValueRange rand)
    {
        ScoreObjective scoreobjective = scoreboardIn.getObjective(objectiveStr);

        if (scoreobjective == null)
        {
            return false;
        }
        else
        {
            String s = entityIn.getScoreboardName();
            return !scoreboardIn.entityHasObjective(s, scoreobjective) ? false : rand.isInRange(scoreboardIn.getOrCreateScore(s, scoreobjective).getScorePoints());
        }
    }

    public static class Serializer implements ILootSerializer<EntityHasScore>
    {
        public void serialize(JsonObject p_230424_1_, EntityHasScore p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            JsonObject jsonobject = new JsonObject();

            for (Entry<String, RandomValueRange> entry : p_230424_2_.scores.entrySet())
            {
                jsonobject.add(entry.getKey(), p_230424_3_.serialize(entry.getValue()));
            }

            p_230424_1_.add("scores", jsonobject);
            p_230424_1_.add("entity", p_230424_3_.serialize(p_230424_2_.target));
        }

        public EntityHasScore deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_)
        {
            Set<Entry<String, JsonElement>> set = JSONUtils.getJsonObject(p_230423_1_, "scores").entrySet();
            Map<String, RandomValueRange> map = Maps.newLinkedHashMap();

            for (Entry<String, JsonElement> entry : set)
            {
                map.put(entry.getKey(), JSONUtils.deserializeClass(entry.getValue(), "score", p_230423_2_, RandomValueRange.class));
            }

            return new EntityHasScore(map, JSONUtils.deserializeClass(p_230423_1_, "entity", p_230423_2_, LootContext.EntityTarget.class));
        }
    }
}
