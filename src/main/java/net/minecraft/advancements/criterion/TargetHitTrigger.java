package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

public class TargetHitTrigger extends AbstractCriterionTrigger<TargetHitTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("target_hit");

    public ResourceLocation getId()
    {
        return ID;
    }

    public TargetHitTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("signal_strength"));
        EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.deserializeJSONObject(json, "projectile", conditionsParser);
        return new TargetHitTrigger.Instance(entityPredicate, minmaxbounds$intbound, entitypredicate$andpredicate);
    }

    public void test(ServerPlayerEntity player, Entity projectile, Vector3d vector, int signalStrength)
    {
        LootContext lootcontext = EntityPredicate.getLootContext(player, projectile);
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(lootcontext, vector, signalStrength);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final MinMaxBounds.IntBound signalStrength;
        private final EntityPredicate.AndPredicate projectile;

        public Instance(EntityPredicate.AndPredicate player, MinMaxBounds.IntBound signalStrength, EntityPredicate.AndPredicate projectile)
        {
            super(TargetHitTrigger.ID, player);
            this.signalStrength = signalStrength;
            this.projectile = projectile;
        }

        public static TargetHitTrigger.Instance create(MinMaxBounds.IntBound signalStrength, EntityPredicate.AndPredicate projectile)
        {
            return new TargetHitTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, signalStrength, projectile);
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("signal_strength", this.signalStrength.serialize());
            jsonobject.add("projectile", this.projectile.serializeConditions(conditions));
            return jsonobject;
        }

        public boolean test(LootContext context, Vector3d vector, int signalStrength)
        {
            if (!this.signalStrength.test(signalStrength))
            {
                return false;
            }
            else
            {
                return this.projectile.testContext(context);
            }
        }
    }
}
