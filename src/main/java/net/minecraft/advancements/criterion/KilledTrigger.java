package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class KilledTrigger extends AbstractCriterionTrigger<KilledTrigger.Instance>
{
    private final ResourceLocation id;

    public KilledTrigger(ResourceLocation id)
    {
        this.id = id;
    }

    public ResourceLocation getId()
    {
        return this.id;
    }

    public KilledTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        return new KilledTrigger.Instance(this.id, entityPredicate, EntityPredicate.AndPredicate.deserializeJSONObject(json, "entity", conditionsParser), DamageSourcePredicate.deserialize(json.get("killing_blow")));
    }

    public void trigger(ServerPlayerEntity player, Entity entity, DamageSource source)
    {
        LootContext lootcontext = EntityPredicate.getLootContext(player, entity);
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(player, lootcontext, source);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final EntityPredicate.AndPredicate entity;
        private final DamageSourcePredicate killingBlow;

        public Instance(ResourceLocation criterion, EntityPredicate.AndPredicate player, EntityPredicate.AndPredicate entity, DamageSourcePredicate killingBlow)
        {
            super(criterion, player);
            this.entity = entity;
            this.killingBlow = killingBlow;
        }

        public static KilledTrigger.Instance playerKilledEntity(EntityPredicate.Builder builder)
        {
            return new KilledTrigger.Instance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.AndPredicate.ANY_AND, EntityPredicate.AndPredicate.createAndFromEntityCondition(builder.build()), DamageSourcePredicate.ANY);
        }

        public static KilledTrigger.Instance playerKilledEntity()
        {
            return new KilledTrigger.Instance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.AndPredicate.ANY_AND, EntityPredicate.AndPredicate.ANY_AND, DamageSourcePredicate.ANY);
        }

        public static KilledTrigger.Instance playerKilledEntity(EntityPredicate.Builder entityBuilder, DamageSourcePredicate.Builder sourceBuilder)
        {
            return new KilledTrigger.Instance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.AndPredicate.ANY_AND, EntityPredicate.AndPredicate.createAndFromEntityCondition(entityBuilder.build()), sourceBuilder.build());
        }

        public static KilledTrigger.Instance entityKilledPlayer()
        {
            return new KilledTrigger.Instance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.AndPredicate.ANY_AND, EntityPredicate.AndPredicate.ANY_AND, DamageSourcePredicate.ANY);
        }

        public boolean test(ServerPlayerEntity player, LootContext context, DamageSource source)
        {
            return !this.killingBlow.test(player, source) ? false : this.entity.testContext(context);
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("entity", this.entity.serializeConditions(conditions));
            jsonobject.add("killing_blow", this.killingBlow.serialize());
            return jsonobject;
        }
    }
}
