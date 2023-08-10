package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class PlayerEntityInteractionTrigger extends AbstractCriterionTrigger<PlayerEntityInteractionTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("player_interacted_with_entity");

    public ResourceLocation getId()
    {
        return ID;
    }

    protected PlayerEntityInteractionTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
        EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.deserializeJSONObject(json, "entity", conditionsParser);
        return new PlayerEntityInteractionTrigger.Instance(entityPredicate, itempredicate, entitypredicate$andpredicate);
    }

    public void test(ServerPlayerEntity player, ItemStack stack, Entity entity)
    {
        LootContext lootcontext = EntityPredicate.getLootContext(player, entity);
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(stack, lootcontext);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final ItemPredicate stack;
        private final EntityPredicate.AndPredicate entity;

        public Instance(EntityPredicate.AndPredicate player, ItemPredicate stack, EntityPredicate.AndPredicate entity)
        {
            super(PlayerEntityInteractionTrigger.ID, player);
            this.stack = stack;
            this.entity = entity;
        }

        public static PlayerEntityInteractionTrigger.Instance create(EntityPredicate.AndPredicate player, ItemPredicate.Builder stack, EntityPredicate.AndPredicate entity)
        {
            return new PlayerEntityInteractionTrigger.Instance(player, stack.build(), entity);
        }

        public boolean test(ItemStack stack, LootContext context)
        {
            return !this.stack.test(stack) ? false : this.entity.testContext(context);
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("item", this.stack.serialize());
            jsonobject.add("entity", this.entity.serializeConditions(conditions));
            return jsonobject;
        }
    }
}
