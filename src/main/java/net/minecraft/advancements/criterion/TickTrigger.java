package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

public class TickTrigger extends AbstractCriterionTrigger<TickTrigger.Instance>
{
    public static final ResourceLocation ID = new ResourceLocation("tick");

    public ResourceLocation getId()
    {
        return ID;
    }

    public TickTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        return new TickTrigger.Instance(entityPredicate);
    }

    public void trigger(ServerPlayerEntity player)
    {
        this.triggerListeners(player, (instance) ->
        {
            return true;
        });
    }

    public static class Instance extends CriterionInstance
    {
        public Instance(EntityPredicate.AndPredicate player)
        {
            super(TickTrigger.ID, player);
        }
    }
}
