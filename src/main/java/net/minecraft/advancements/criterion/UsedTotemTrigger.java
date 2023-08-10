package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class UsedTotemTrigger extends AbstractCriterionTrigger<UsedTotemTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("used_totem");

    public ResourceLocation getId()
    {
        return ID;
    }

    public UsedTotemTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
        return new UsedTotemTrigger.Instance(entityPredicate, itempredicate);
    }

    public void trigger(ServerPlayerEntity player, ItemStack item)
    {
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(item);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final ItemPredicate item;

        public Instance(EntityPredicate.AndPredicate player, ItemPredicate item)
        {
            super(UsedTotemTrigger.ID, player);
            this.item = item;
        }

        public static UsedTotemTrigger.Instance usedTotem(IItemProvider item)
        {
            return new UsedTotemTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, ItemPredicate.Builder.create().item(item).build());
        }

        public boolean test(ItemStack item)
        {
            return this.item.test(item);
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("item", this.item.serialize());
            return jsonobject;
        }
    }
}
