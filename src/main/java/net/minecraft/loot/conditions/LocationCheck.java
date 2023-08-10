package net.minecraft.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class LocationCheck implements ILootCondition
{
    private final LocationPredicate predicate;
    private final BlockPos field_227564_b_;

    private LocationCheck(LocationPredicate p_i225895_1_, BlockPos p_i225895_2_)
    {
        this.predicate = p_i225895_1_;
        this.field_227564_b_ = p_i225895_2_;
    }

    public LootConditionType func_230419_b_()
    {
        return LootConditionManager.LOCATION_CHECK;
    }

    public boolean test(LootContext p_test_1_)
    {
        Vector3d vector3d = p_test_1_.get(LootParameters.field_237457_g_);
        return vector3d != null && this.predicate.test(p_test_1_.getWorld(), vector3d.getX() + (double)this.field_227564_b_.getX(), vector3d.getY() + (double)this.field_227564_b_.getY(), vector3d.getZ() + (double)this.field_227564_b_.getZ());
    }

    public static ILootCondition.IBuilder builder(LocationPredicate.Builder p_215975_0_)
    {
        return () ->
        {
            return new LocationCheck(p_215975_0_.build(), BlockPos.ZERO);
        };
    }

    public static ILootCondition.IBuilder func_241547_a_(LocationPredicate.Builder p_241547_0_, BlockPos p_241547_1_)
    {
        return () ->
        {
            return new LocationCheck(p_241547_0_.build(), p_241547_1_);
        };
    }

    public static class Serializer implements ILootSerializer<LocationCheck>
    {
        public void serialize(JsonObject p_230424_1_, LocationCheck p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            p_230424_1_.add("predicate", p_230424_2_.predicate.serialize());

            if (p_230424_2_.field_227564_b_.getX() != 0)
            {
                p_230424_1_.addProperty("offsetX", p_230424_2_.field_227564_b_.getX());
            }

            if (p_230424_2_.field_227564_b_.getY() != 0)
            {
                p_230424_1_.addProperty("offsetY", p_230424_2_.field_227564_b_.getY());
            }

            if (p_230424_2_.field_227564_b_.getZ() != 0)
            {
                p_230424_1_.addProperty("offsetZ", p_230424_2_.field_227564_b_.getZ());
            }
        }

        public LocationCheck deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_)
        {
            LocationPredicate locationpredicate = LocationPredicate.deserialize(p_230423_1_.get("predicate"));
            int i = JSONUtils.getInt(p_230423_1_, "offsetX", 0);
            int j = JSONUtils.getInt(p_230423_1_, "offsetY", 0);
            int k = JSONUtils.getInt(p_230423_1_, "offsetZ", 0);
            return new LocationCheck(locationpredicate, new BlockPos(i, j, k));
        }
    }
}
