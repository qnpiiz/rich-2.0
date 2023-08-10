package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class VillagerTradeTrigger extends AbstractCriterionTrigger<VillagerTradeTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("villager_trade");

    public ResourceLocation getId()
    {
        return ID;
    }

    public VillagerTradeTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.deserializeJSONObject(json, "villager", conditionsParser);
        ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
        return new VillagerTradeTrigger.Instance(entityPredicate, entitypredicate$andpredicate, itempredicate);
    }

    public void test(ServerPlayerEntity player, AbstractVillagerEntity villager, ItemStack stack)
    {
        LootContext lootcontext = EntityPredicate.getLootContext(player, villager);
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(lootcontext, stack);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final EntityPredicate.AndPredicate villager;
        private final ItemPredicate item;

        public Instance(EntityPredicate.AndPredicate player, EntityPredicate.AndPredicate villager, ItemPredicate stack)
        {
            super(VillagerTradeTrigger.ID, player);
            this.villager = villager;
            this.item = stack;
        }

        public static VillagerTradeTrigger.Instance any()
        {
            return new VillagerTradeTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, EntityPredicate.AndPredicate.ANY_AND, ItemPredicate.ANY);
        }

        public boolean test(LootContext context, ItemStack stack)
        {
            if (!this.villager.testContext(context))
            {
                return false;
            }
            else
            {
                return this.item.test(stack);
            }
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("item", this.item.serialize());
            jsonobject.add("villager", this.villager.serializeConditions(conditions));
            return jsonobject;
        }
    }
}
