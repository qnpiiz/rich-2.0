package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class RightClickBlockWithItemTrigger extends AbstractCriterionTrigger<RightClickBlockWithItemTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("item_used_on_block");

    public ResourceLocation getId()
    {
        return ID;
    }

    public RightClickBlockWithItemTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        LocationPredicate locationpredicate = LocationPredicate.deserialize(json.get("location"));
        ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
        return new RightClickBlockWithItemTrigger.Instance(entityPredicate, locationpredicate, itempredicate);
    }

    public void test(ServerPlayerEntity player, BlockPos pos, ItemStack stack)
    {
        BlockState blockstate = player.getServerWorld().getBlockState(pos);
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(blockstate, player.getServerWorld(), pos, stack);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final LocationPredicate location;
        private final ItemPredicate stack;

        public Instance(EntityPredicate.AndPredicate player, LocationPredicate location, ItemPredicate stack)
        {
            super(RightClickBlockWithItemTrigger.ID, player);
            this.location = location;
            this.stack = stack;
        }

        public static RightClickBlockWithItemTrigger.Instance create(LocationPredicate.Builder locationBuilder, ItemPredicate.Builder stackBuilder)
        {
            return new RightClickBlockWithItemTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, locationBuilder.build(), stackBuilder.build());
        }

        public boolean test(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack)
        {
            return !this.location.test(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) ? false : this.stack.test(stack);
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("location", this.location.serialize());
            jsonobject.add("item", this.stack.serialize());
            return jsonobject;
        }
    }
}
