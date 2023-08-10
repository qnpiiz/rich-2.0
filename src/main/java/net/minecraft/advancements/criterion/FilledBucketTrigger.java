package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class FilledBucketTrigger extends AbstractCriterionTrigger<FilledBucketTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("filled_bucket");

    public ResourceLocation getId()
    {
        return ID;
    }

    public FilledBucketTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
        return new FilledBucketTrigger.Instance(entityPredicate, itempredicate);
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack)
    {
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(stack);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final ItemPredicate item;

        public Instance(EntityPredicate.AndPredicate player, ItemPredicate itemCondition)
        {
            super(FilledBucketTrigger.ID, player);
            this.item = itemCondition;
        }

        public static FilledBucketTrigger.Instance forItem(ItemPredicate itemCondition)
        {
            return new FilledBucketTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, itemCondition);
        }

        public boolean test(ItemStack stack)
        {
            return this.item.test(stack);
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("item", this.item.serialize());
            return jsonobject;
        }
    }
}
