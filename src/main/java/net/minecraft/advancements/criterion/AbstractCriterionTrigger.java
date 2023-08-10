package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.LootContext;

public abstract class AbstractCriterionTrigger<T extends CriterionInstance> implements ICriterionTrigger<T>
{
    private final Map<PlayerAdvancements, Set<ICriterionTrigger.Listener<T>>> triggerListeners = Maps.newIdentityHashMap();

    public final void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<T> listener)
    {
        this.triggerListeners.computeIfAbsent(playerAdvancementsIn, (playerAdvancements) ->
        {
            return Sets.newHashSet();
        }).add(listener);
    }

    public final void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<T> listener)
    {
        Set<ICriterionTrigger.Listener<T>> set = this.triggerListeners.get(playerAdvancementsIn);

        if (set != null)
        {
            set.remove(listener);

            if (set.isEmpty())
            {
                this.triggerListeners.remove(playerAdvancementsIn);
            }
        }
    }

    public final void removeAllListeners(PlayerAdvancements playerAdvancementsIn)
    {
        this.triggerListeners.remove(playerAdvancementsIn);
    }

    protected abstract T deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser);

    public final T deserialize(JsonObject object, ConditionArrayParser conditions)
    {
        EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.deserializeJSONObject(object, "player", conditions);
        return this.deserializeTrigger(object, entitypredicate$andpredicate, conditions);
    }

    protected void triggerListeners(ServerPlayerEntity serverPlayer, Predicate<T> testTrigger)
    {
        PlayerAdvancements playeradvancements = serverPlayer.getAdvancements();
        Set<ICriterionTrigger.Listener<T>> set = this.triggerListeners.get(playeradvancements);

        if (set != null && !set.isEmpty())
        {
            LootContext lootcontext = EntityPredicate.getLootContext(serverPlayer, serverPlayer);
            List<ICriterionTrigger.Listener<T>> list = null;

            for (ICriterionTrigger.Listener<T> listener : set)
            {
                T t = listener.getCriterionInstance();

                if (t.getPlayerCondition().testContext(lootcontext) && testTrigger.test(t))
                {
                    if (list == null)
                    {
                        list = Lists.newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null)
            {
                for (ICriterionTrigger.Listener<T> listener1 : list)
                {
                    listener1.grantCriterion(playeradvancements);
                }
            }
        }
    }
}
