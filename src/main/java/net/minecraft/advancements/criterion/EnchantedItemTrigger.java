package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class EnchantedItemTrigger extends AbstractCriterionTrigger<EnchantedItemTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("enchanted_item");

    public ResourceLocation getId()
    {
        return ID;
    }

    public EnchantedItemTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
        MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("levels"));
        return new EnchantedItemTrigger.Instance(entityPredicate, itempredicate, minmaxbounds$intbound);
    }

    public void trigger(ServerPlayerEntity player, ItemStack item, int levelsSpent)
    {
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(item, levelsSpent);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final ItemPredicate item;
        private final MinMaxBounds.IntBound levels;

        public Instance(EntityPredicate.AndPredicate player, ItemPredicate item, MinMaxBounds.IntBound level)
        {
            super(EnchantedItemTrigger.ID, player);
            this.item = item;
            this.levels = level;
        }

        public static EnchantedItemTrigger.Instance any()
        {
            return new EnchantedItemTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, ItemPredicate.ANY, MinMaxBounds.IntBound.UNBOUNDED);
        }

        public boolean test(ItemStack item, int levelsIn)
        {
            if (!this.item.test(item))
            {
                return false;
            }
            else
            {
                return this.levels.test(levelsIn);
            }
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("item", this.item.serialize());
            jsonobject.add("levels", this.levels.serialize());
            return jsonobject;
        }
    }
}
