package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class SummonedEntityTrigger extends AbstractCriterionTrigger<SummonedEntityTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("summoned_entity");

    public ResourceLocation getId()
    {
        return ID;
    }

    public SummonedEntityTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.deserializeJSONObject(json, "entity", conditionsParser);
        return new SummonedEntityTrigger.Instance(entityPredicate, entitypredicate$andpredicate);
    }

    public void trigger(ServerPlayerEntity player, Entity entity)
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
            super(SummonedEntityTrigger.ID, player);
            this.entity = entity;
        }

        public static SummonedEntityTrigger.Instance summonedEntity(EntityPredicate.Builder entityBuilder)
        {
            return new SummonedEntityTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, EntityPredicate.AndPredicate.createAndFromEntityCondition(entityBuilder.build()));
        }

        public boolean test(LootContext lootContext)
        {
            return this.entity.testContext(lootContext);
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("entity", this.entity.serializeConditions(conditions));
            return jsonobject;
        }
    }
}
