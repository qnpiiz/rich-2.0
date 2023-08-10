package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class UsedEnderEyeTrigger extends AbstractCriterionTrigger<UsedEnderEyeTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("used_ender_eye");

    public ResourceLocation getId()
    {
        return ID;
    }

    public UsedEnderEyeTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.fromJson(json.get("distance"));
        return new UsedEnderEyeTrigger.Instance(entityPredicate, minmaxbounds$floatbound);
    }

    public void trigger(ServerPlayerEntity player, BlockPos pos)
    {
        double d0 = player.getPosX() - (double)pos.getX();
        double d1 = player.getPosZ() - (double)pos.getZ();
        double d2 = d0 * d0 + d1 * d1;
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(d2);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final MinMaxBounds.FloatBound distance;

        public Instance(EntityPredicate.AndPredicate player, MinMaxBounds.FloatBound distance)
        {
            super(UsedEnderEyeTrigger.ID, player);
            this.distance = distance;
        }

        public boolean test(double distanceSq)
        {
            return this.distance.testSquared(distanceSq);
        }
    }
}
