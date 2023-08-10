package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ShotCrossbowTrigger extends AbstractCriterionTrigger<ShotCrossbowTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("shot_crossbow");

    public ResourceLocation getId()
    {
        return ID;
    }

    public ShotCrossbowTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
        return new ShotCrossbowTrigger.Instance(entityPredicate, itempredicate);
    }

    public void test(ServerPlayerEntity shooter, ItemStack stack)
    {
        this.triggerListeners(shooter, (instance) ->
        {
            return instance.test(stack);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final ItemPredicate itemPredicate;

        public Instance(EntityPredicate.AndPredicate player, ItemPredicate itemPredicate)
        {
            super(ShotCrossbowTrigger.ID, player);
            this.itemPredicate = itemPredicate;
        }

        public static ShotCrossbowTrigger.Instance create(IItemProvider itemProvider)
        {
            return new ShotCrossbowTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, ItemPredicate.Builder.create().item(itemProvider).build());
        }

        public boolean test(ItemStack stack)
        {
            return this.itemPredicate.test(stack);
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("item", this.itemPredicate.serialize());
            return jsonobject;
        }
    }
}
