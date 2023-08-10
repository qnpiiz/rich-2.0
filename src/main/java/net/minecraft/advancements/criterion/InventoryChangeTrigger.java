package net.minecraft.advancements.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.potion.Potion;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class InventoryChangeTrigger extends AbstractCriterionTrigger<InventoryChangeTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("inventory_changed");

    public ResourceLocation getId()
    {
        return ID;
    }

    public InventoryChangeTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        JsonObject jsonobject = JSONUtils.getJsonObject(json, "slots", new JsonObject());
        MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(jsonobject.get("occupied"));
        MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(jsonobject.get("full"));
        MinMaxBounds.IntBound minmaxbounds$intbound2 = MinMaxBounds.IntBound.fromJson(jsonobject.get("empty"));
        ItemPredicate[] aitempredicate = ItemPredicate.deserializeArray(json.get("items"));
        return new InventoryChangeTrigger.Instance(entityPredicate, minmaxbounds$intbound, minmaxbounds$intbound1, minmaxbounds$intbound2, aitempredicate);
    }

    public void test(ServerPlayerEntity player, PlayerInventory inventory, ItemStack stack)
    {
        int i = 0;
        int j = 0;
        int k = 0;

        for (int l = 0; l < inventory.getSizeInventory(); ++l)
        {
            ItemStack itemstack = inventory.getStackInSlot(l);

            if (itemstack.isEmpty())
            {
                ++j;
            }
            else
            {
                ++k;

                if (itemstack.getCount() >= itemstack.getMaxStackSize())
                {
                    ++i;
                }
            }
        }

        this.trigger(player, inventory, stack, i, j, k);
    }

    private void trigger(ServerPlayerEntity player, PlayerInventory inventory, ItemStack stack, int full, int empty, int occupied)
    {
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(inventory, stack, full, empty, occupied);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final MinMaxBounds.IntBound occupied;
        private final MinMaxBounds.IntBound full;
        private final MinMaxBounds.IntBound empty;
        private final ItemPredicate[] items;

        public Instance(EntityPredicate.AndPredicate player, MinMaxBounds.IntBound occupied, MinMaxBounds.IntBound full, MinMaxBounds.IntBound empty, ItemPredicate[] items)
        {
            super(InventoryChangeTrigger.ID, player);
            this.occupied = occupied;
            this.full = full;
            this.empty = empty;
            this.items = items;
        }

        public static InventoryChangeTrigger.Instance forItems(ItemPredicate... itemConditions)
        {
            return new InventoryChangeTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, itemConditions);
        }

        public static InventoryChangeTrigger.Instance forItems(IItemProvider... items)
        {
            ItemPredicate[] aitempredicate = new ItemPredicate[items.length];

            for (int i = 0; i < items.length; ++i)
            {
                aitempredicate[i] = new ItemPredicate((ITag<Item>)null, items[i].asItem(), MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, EnchantmentPredicate.enchantments, EnchantmentPredicate.enchantments, (Potion)null, NBTPredicate.ANY);
            }

            return forItems(aitempredicate);
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);

            if (!this.occupied.isUnbounded() || !this.full.isUnbounded() || !this.empty.isUnbounded())
            {
                JsonObject jsonobject1 = new JsonObject();
                jsonobject1.add("occupied", this.occupied.serialize());
                jsonobject1.add("full", this.full.serialize());
                jsonobject1.add("empty", this.empty.serialize());
                jsonobject.add("slots", jsonobject1);
            }

            if (this.items.length > 0)
            {
                JsonArray jsonarray = new JsonArray();

                for (ItemPredicate itempredicate : this.items)
                {
                    jsonarray.add(itempredicate.serialize());
                }

                jsonobject.add("items", jsonarray);
            }

            return jsonobject;
        }

        public boolean test(PlayerInventory inventory, ItemStack stack, int full, int empty, int occupied)
        {
            if (!this.full.test(full))
            {
                return false;
            }
            else if (!this.empty.test(empty))
            {
                return false;
            }
            else if (!this.occupied.test(occupied))
            {
                return false;
            }
            else
            {
                int i = this.items.length;

                if (i == 0)
                {
                    return true;
                }
                else if (i != 1)
                {
                    List<ItemPredicate> list = new ObjectArrayList<>(this.items);
                    int j = inventory.getSizeInventory();

                    for (int k = 0; k < j; ++k)
                    {
                        if (list.isEmpty())
                        {
                            return true;
                        }

                        ItemStack itemstack = inventory.getStackInSlot(k);

                        if (!itemstack.isEmpty())
                        {
                            list.removeIf((predicate) ->
                            {
                                return predicate.test(itemstack);
                            });
                        }
                    }

                    return list.isEmpty();
                }
                else
                {
                    return !stack.isEmpty() && this.items[0].test(stack);
                }
            }
        }
    }
}
