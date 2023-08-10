package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class PositionTrigger extends AbstractCriterionTrigger<PositionTrigger.Instance>
{
    private final ResourceLocation id;

    public PositionTrigger(ResourceLocation id)
    {
        this.id = id;
    }

    public ResourceLocation getId()
    {
        return this.id;
    }

    public PositionTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        JsonObject jsonobject = JSONUtils.getJsonObject(json, "location", json);
        LocationPredicate locationpredicate = LocationPredicate.deserialize(jsonobject);
        return new PositionTrigger.Instance(this.id, entityPredicate, locationpredicate);
    }

    public void trigger(ServerPlayerEntity player)
    {
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(player.getServerWorld(), player.getPosX(), player.getPosY(), player.getPosZ());
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final LocationPredicate location;

        public Instance(ResourceLocation id, EntityPredicate.AndPredicate player, LocationPredicate location)
        {
            super(id, player);
            this.location = location;
        }

        public static PositionTrigger.Instance forLocation(LocationPredicate location)
        {
            return new PositionTrigger.Instance(CriteriaTriggers.LOCATION.id, EntityPredicate.AndPredicate.ANY_AND, location);
        }

        public static PositionTrigger.Instance sleptInBed()
        {
            return new PositionTrigger.Instance(CriteriaTriggers.SLEPT_IN_BED.id, EntityPredicate.AndPredicate.ANY_AND, LocationPredicate.ANY);
        }

        public static PositionTrigger.Instance villageHero()
        {
            return new PositionTrigger.Instance(CriteriaTriggers.HERO_OF_THE_VILLAGE.id, EntityPredicate.AndPredicate.ANY_AND, LocationPredicate.ANY);
        }

        public boolean test(ServerWorld world, double x, double y, double z)
        {
            return this.location.test(world, x, y, z);
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("location", this.location.serialize());
            return jsonobject;
        }
    }
}
