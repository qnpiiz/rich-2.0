package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class NetherTravelTrigger extends AbstractCriterionTrigger<NetherTravelTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("nether_travel");

    public ResourceLocation getId()
    {
        return ID;
    }

    public NetherTravelTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        LocationPredicate locationpredicate = LocationPredicate.deserialize(json.get("entered"));
        LocationPredicate locationpredicate1 = LocationPredicate.deserialize(json.get("exited"));
        DistancePredicate distancepredicate = DistancePredicate.deserialize(json.get("distance"));
        return new NetherTravelTrigger.Instance(entityPredicate, locationpredicate, locationpredicate1, distancepredicate);
    }

    public void trigger(ServerPlayerEntity player, Vector3d enteredNetherPosition)
    {
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(player.getServerWorld(), enteredNetherPosition, player.getPosX(), player.getPosY(), player.getPosZ());
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final LocationPredicate entered;
        private final LocationPredicate exited;
        private final DistancePredicate distance;

        public Instance(EntityPredicate.AndPredicate player, LocationPredicate entered, LocationPredicate exited, DistancePredicate distance)
        {
            super(NetherTravelTrigger.ID, player);
            this.entered = entered;
            this.exited = exited;
            this.distance = distance;
        }

        public static NetherTravelTrigger.Instance forDistance(DistancePredicate distance)
        {
            return new NetherTravelTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, LocationPredicate.ANY, LocationPredicate.ANY, distance);
        }

        public boolean test(ServerWorld world, Vector3d enteredNetherPosition, double x, double y, double z)
        {
            if (!this.entered.test(world, enteredNetherPosition.x, enteredNetherPosition.y, enteredNetherPosition.z))
            {
                return false;
            }
            else if (!this.exited.test(world, x, y, z))
            {
                return false;
            }
            else
            {
                return this.distance.test(enteredNetherPosition.x, enteredNetherPosition.y, enteredNetherPosition.z, x, y, z);
            }
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("entered", this.entered.serialize());
            jsonobject.add("exited", this.exited.serialize());
            jsonobject.add("distance", this.distance.serialize());
            return jsonobject;
        }
    }
}
