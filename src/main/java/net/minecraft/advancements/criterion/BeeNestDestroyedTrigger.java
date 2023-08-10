package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BeeNestDestroyedTrigger extends AbstractCriterionTrigger<BeeNestDestroyedTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("bee_nest_destroyed");

    public ResourceLocation getId()
    {
        return ID;
    }

    public BeeNestDestroyedTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        Block block = deserializeBlock(json);
        ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
        MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("num_bees_inside"));
        return new BeeNestDestroyedTrigger.Instance(entityPredicate, block, itempredicate, minmaxbounds$intbound);
    }

    @Nullable
    private static Block deserializeBlock(JsonObject json)
    {
        if (json.has("block"))
        {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "block"));
            return Registry.BLOCK.getOptional(resourcelocation).orElseThrow(() ->
            {
                return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
            });
        }
        else
        {
            return null;
        }
    }

    public void test(ServerPlayerEntity player, Block block, ItemStack stack, int beesContained)
    {
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(block, stack, beesContained);
        });
    }

    public static class Instance extends CriterionInstance
    {
        @Nullable
        private final Block block;
        private final ItemPredicate itemPredicate;
        private final MinMaxBounds.IntBound beesContained;

        public Instance(EntityPredicate.AndPredicate entityCondition, @Nullable Block block, ItemPredicate itemCondition, MinMaxBounds.IntBound beesContained)
        {
            super(BeeNestDestroyedTrigger.ID, entityCondition);
            this.block = block;
            this.itemPredicate = itemCondition;
            this.beesContained = beesContained;
        }

        public static BeeNestDestroyedTrigger.Instance createNewInstance(Block block, ItemPredicate.Builder itemConditionBuilder, MinMaxBounds.IntBound beesContained)
        {
            return new BeeNestDestroyedTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, block, itemConditionBuilder.build(), beesContained);
        }

        public boolean test(Block block, ItemStack stack, int beesContained)
        {
            if (this.block != null && block != this.block)
            {
                return false;
            }
            else
            {
                return !this.itemPredicate.test(stack) ? false : this.beesContained.test(beesContained);
            }
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);

            if (this.block != null)
            {
                jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
            }

            jsonobject.add("item", this.itemPredicate.serialize());
            jsonobject.add("num_bees_inside", this.beesContained.serialize());
            return jsonobject;
        }
    }
}
