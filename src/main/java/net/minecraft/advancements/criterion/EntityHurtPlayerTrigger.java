package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class EntityHurtPlayerTrigger extends AbstractCriterionTrigger<EntityHurtPlayerTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("entity_hurt_player");

    public ResourceLocation getId()
    {
        return ID;
    }

    public EntityHurtPlayerTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        DamagePredicate damagepredicate = DamagePredicate.deserialize(json.get("damage"));
        return new EntityHurtPlayerTrigger.Instance(entityPredicate, damagepredicate);
    }

    public void trigger(ServerPlayerEntity player, DamageSource source, float amountDealt, float amountTaken, boolean wasBlocked)
    {
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(player, source, amountDealt, amountTaken, wasBlocked);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final DamagePredicate damage;

        public Instance(EntityPredicate.AndPredicate player, DamagePredicate damageCondition)
        {
            super(EntityHurtPlayerTrigger.ID, player);
            this.damage = damageCondition;
        }

        public static EntityHurtPlayerTrigger.Instance forDamage(DamagePredicate.Builder damageConditionBuilder)
        {
            return new EntityHurtPlayerTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, damageConditionBuilder.build());
        }

        public boolean test(ServerPlayerEntity player, DamageSource source, float amountDealt, float amountTaken, boolean wasBlocked)
        {
            return this.damage.test(player, source, amountDealt, amountTaken, wasBlocked);
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("damage", this.damage.serialize());
            return jsonobject;
        }
    }
}
