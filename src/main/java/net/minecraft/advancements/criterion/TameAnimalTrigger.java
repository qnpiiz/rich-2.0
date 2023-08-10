package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class TameAnimalTrigger extends AbstractCriterionTrigger<TameAnimalTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("tame_animal");

    public ResourceLocation getId()
    {
        return ID;
    }

    public TameAnimalTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.deserializeJSONObject(json, "entity", conditionsParser);
        return new TameAnimalTrigger.Instance(entityPredicate, entitypredicate$andpredicate);
    }

    public void trigger(ServerPlayerEntity player, AnimalEntity entity)
    {
        LootContext lootcontext = EntityPredicate.getLootContext(player, entity);
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(lootcontext);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final EntityPredicate.AndPredicate entity;

        public Instance(EntityPredicate.AndPredicate player, EntityPredicate.AndPredicate entity)
        {
            super(TameAnimalTrigger.ID, player);
            this.entity = entity;
        }

        public static TameAnimalTrigger.Instance any()
        {
            return new TameAnimalTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, EntityPredicate.AndPredicate.ANY_AND);
        }

        public static TameAnimalTrigger.Instance create(EntityPredicate entityCondition)
        {
            return new TameAnimalTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, EntityPredicate.AndPredicate.createAndFromEntityCondition(entityCondition));
        }

        public boolean test(LootContext context)
        {
            return this.entity.testContext(context);
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("entity", this.entity.serializeConditions(conditions));
            return jsonobject;
        }
    }
}
