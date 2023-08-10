package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class PlayerHurtEntityTrigger extends AbstractCriterionTrigger<PlayerHurtEntityTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("player_hurt_entity");

    public ResourceLocation getId()
    {
        return ID;
    }

    public PlayerHurtEntityTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        DamagePredicate damagepredicate = DamagePredicate.deserialize(json.get("damage"));
        EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.deserializeJSONObject(json, "entity", conditionsParser);
        return new PlayerHurtEntityTrigger.Instance(entityPredicate, damagepredicate, entitypredicate$andpredicate);
    }

    public void trigger(ServerPlayerEntity player, Entity entityIn, DamageSource source, float amountDealt, float amountTaken, boolean blocked)
    {
        LootContext lootcontext = EntityPredicate.getLootContext(player, entityIn);
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(player, lootcontext, source, amountDealt, amountTaken, blocked);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final DamagePredicate damage;
        private final EntityPredicate.AndPredicate entity;

        public Instance(EntityPredicate.AndPredicate player, DamagePredicate damage, EntityPredicate.AndPredicate entity)
        {
            super(PlayerHurtEntityTrigger.ID, player);
            this.damage = damage;
            this.entity = entity;
        }

        public static PlayerHurtEntityTrigger.Instance forDamage(DamagePredicate.Builder builder)
        {
            return new PlayerHurtEntityTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, builder.build(), EntityPredicate.AndPredicate.ANY_AND);
        }

        public boolean test(ServerPlayerEntity player, LootContext context, DamageSource damage, float dealt, float taken, boolean blocked)
        {
            if (!this.damage.test(player, damage, dealt, taken, blocked))
            {
                return false;
            }
            else
            {
                return this.entity.testContext(context);
            }
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("damage", this.damage.serialize());
            jsonobject.add("entity", this.entity.serializeConditions(conditions));
            return jsonobject;
        }
    }
}
