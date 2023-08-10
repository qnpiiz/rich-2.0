package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.util.ResourceLocation;

public class ConstructBeaconTrigger extends AbstractCriterionTrigger<ConstructBeaconTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("construct_beacon");

    public ResourceLocation getId()
    {
        return ID;
    }

    public ConstructBeaconTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("level"));
        return new ConstructBeaconTrigger.Instance(entityPredicate, minmaxbounds$intbound);
    }

    public void trigger(ServerPlayerEntity player, BeaconTileEntity beacon)
    {
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(beacon);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final MinMaxBounds.IntBound level;

        public Instance(EntityPredicate.AndPredicate player, MinMaxBounds.IntBound level)
        {
            super(ConstructBeaconTrigger.ID, player);
            this.level = level;
        }

        public static ConstructBeaconTrigger.Instance forLevel(MinMaxBounds.IntBound level)
        {
            return new ConstructBeaconTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, level);
        }

        public boolean test(BeaconTileEntity beacon)
        {
            return this.level.test(beacon.getLevels());
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("level", this.level.serialize());
            return jsonobject;
        }
    }
}
