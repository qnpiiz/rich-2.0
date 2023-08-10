package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

public class LevitationTrigger extends AbstractCriterionTrigger<LevitationTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("levitation");

    public ResourceLocation getId()
    {
        return ID;
    }

    public LevitationTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        DistancePredicate distancepredicate = DistancePredicate.deserialize(json.get("distance"));
        MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("duration"));
        return new LevitationTrigger.Instance(entityPredicate, distancepredicate, minmaxbounds$intbound);
    }

    public void trigger(ServerPlayerEntity player, Vector3d startPos, int duration)
    {
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(player, startPos, duration);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final DistancePredicate distance;
        private final MinMaxBounds.IntBound duration;

        public Instance(EntityPredicate.AndPredicate player, DistancePredicate distance, MinMaxBounds.IntBound duration)
        {
            super(LevitationTrigger.ID, player);
            this.distance = distance;
            this.duration = duration;
        }

        public static LevitationTrigger.Instance forDistance(DistancePredicate distance)
        {
            return new LevitationTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, distance, MinMaxBounds.IntBound.UNBOUNDED);
        }

        public boolean test(ServerPlayerEntity player, Vector3d startPos, int durationIn)
        {
            if (!this.distance.test(startPos.x, startPos.y, startPos.z, player.getPosX(), player.getPosY(), player.getPosZ()))
            {
                return false;
            }
            else
            {
                return this.duration.test(durationIn);
            }
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("distance", this.distance.serialize());
            jsonobject.add("duration", this.duration.serialize());
            return jsonobject;
        }
    }
}
