package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class PlacedBlockTrigger extends AbstractCriterionTrigger<PlacedBlockTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("placed_block");

    public ResourceLocation getId()
    {
        return ID;
    }

    public PlacedBlockTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        Block block = deserializeBlock(json);
        StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.deserializeProperties(json.get("state"));

        if (block != null)
        {
            statepropertiespredicate.forEachNotPresent(block.getStateContainer(), (property) ->
            {
                throw new JsonSyntaxException("Block " + block + " has no property " + property + ":");
            });
        }

        LocationPredicate locationpredicate = LocationPredicate.deserialize(json.get("location"));
        ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
        return new PlacedBlockTrigger.Instance(entityPredicate, block, statepropertiespredicate, locationpredicate, itempredicate);
    }

    @Nullable
    private static Block deserializeBlock(JsonObject object)
    {
        if (object.has("block"))
        {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(object, "block"));
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

    public void trigger(ServerPlayerEntity player, BlockPos pos, ItemStack item)
    {
        BlockState blockstate = player.getServerWorld().getBlockState(pos);
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(blockstate, pos, player.getServerWorld(), item);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final Block block;
        private final StatePropertiesPredicate properties;
        private final LocationPredicate location;
        private final ItemPredicate item;

        public Instance(EntityPredicate.AndPredicate player, @Nullable Block block, StatePropertiesPredicate properties, LocationPredicate location, ItemPredicate item)
        {
            super(PlacedBlockTrigger.ID, player);
            this.block = block;
            this.properties = properties;
            this.location = location;
            this.item = item;
        }

        public static PlacedBlockTrigger.Instance placedBlock(Block block)
        {
            return new PlacedBlockTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, block, StatePropertiesPredicate.EMPTY, LocationPredicate.ANY, ItemPredicate.ANY);
        }

        public boolean test(BlockState state, BlockPos pos, ServerWorld world, ItemStack item)
        {
            if (this.block != null && !state.isIn(this.block))
            {
                return false;
            }
            else if (!this.properties.matches(state))
            {
                return false;
            }
            else if (!this.location.test(world, (float)pos.getX(), (float)pos.getY(), (float)pos.getZ()))
            {
                return false;
            }
            else
            {
                return this.item.test(item);
            }
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);

            if (this.block != null)
            {
                jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
            }

            jsonobject.add("state", this.properties.toJsonElement());
            jsonobject.add("location", this.location.serialize());
            jsonobject.add("item", this.item.serialize());
            return jsonobject;
        }
    }
}
