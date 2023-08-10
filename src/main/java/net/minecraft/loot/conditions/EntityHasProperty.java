package net.minecraft.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.vector.Vector3d;

public class EntityHasProperty implements ILootCondition
{
    private final EntityPredicate predicate;
    private final LootContext.EntityTarget target;

    private EntityHasProperty(EntityPredicate predicateIn, LootContext.EntityTarget targetIn)
    {
        this.predicate = predicateIn;
        this.target = targetIn;
    }

    public LootConditionType func_230419_b_()
    {
        return LootConditionManager.ENTITY_PROPERTIES;
    }

    public Set < LootParameter<? >> getRequiredParameters()
    {
        return ImmutableSet.of(LootParameters.field_237457_g_, this.target.getParameter());
    }

    public boolean test(LootContext p_test_1_)
    {
        Entity entity = p_test_1_.get(this.target.getParameter());
        Vector3d vector3d = p_test_1_.get(LootParameters.field_237457_g_);
        return this.predicate.test(p_test_1_.getWorld(), vector3d, entity);
    }

    public static ILootCondition.IBuilder builder(LootContext.EntityTarget targetIn)
    {
        return builder(targetIn, EntityPredicate.Builder.create());
    }

    public static ILootCondition.IBuilder builder(LootContext.EntityTarget targetIn, EntityPredicate.Builder predicateBuilderIn)
    {
        return () ->
        {
            return new EntityHasProperty(predicateBuilderIn.build(), targetIn);
        };
    }

    public static ILootCondition.IBuilder func_237477_a_(LootContext.EntityTarget p_237477_0_, EntityPredicate p_237477_1_)
    {
        return () ->
        {
            return new EntityHasProperty(p_237477_1_, p_237477_0_);
        };
    }

    public static class Serializer implements ILootSerializer<EntityHasProperty>
    {
        public void serialize(JsonObject p_230424_1_, EntityHasProperty p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            p_230424_1_.add("predicate", p_230424_2_.predicate.serialize());
            p_230424_1_.add("entity", p_230424_3_.serialize(p_230424_2_.target));
        }

        public EntityHasProperty deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_)
        {
            EntityPredicate entitypredicate = EntityPredicate.deserialize(p_230423_1_.get("predicate"));
            return new EntityHasProperty(entitypredicate, JSONUtils.deserializeClass(p_230423_1_, "entity", p_230423_2_, LootContext.EntityTarget.class));
        }
    }
}
