package net.minecraft.loot.conditions;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.JSONUtils;

public class Alternative implements ILootCondition
{
    private final ILootCondition[] conditions;
    private final Predicate<LootContext> field_215963_b;

    private Alternative(ILootCondition[] conditionsIn)
    {
        this.conditions = conditionsIn;
        this.field_215963_b = LootConditionManager.or(conditionsIn);
    }

    public LootConditionType func_230419_b_()
    {
        return LootConditionManager.ALTERNATIVE;
    }

    public final boolean test(LootContext p_test_1_)
    {
        return this.field_215963_b.test(p_test_1_);
    }

    public void func_225580_a_(ValidationTracker p_225580_1_)
    {
        ILootCondition.super.func_225580_a_(p_225580_1_);

        for (int i = 0; i < this.conditions.length; ++i)
        {
            this.conditions[i].func_225580_a_(p_225580_1_.func_227534_b_(".term[" + i + "]"));
        }
    }

    public static Alternative.Builder builder(ILootCondition.IBuilder... buildersIn)
    {
        return new Alternative.Builder(buildersIn);
    }

    public static class Builder implements ILootCondition.IBuilder
    {
        private final List<ILootCondition> conditions = Lists.newArrayList();

        public Builder(ILootCondition.IBuilder... buildersIn)
        {
            for (ILootCondition.IBuilder ilootcondition$ibuilder : buildersIn)
            {
                this.conditions.add(ilootcondition$ibuilder.build());
            }
        }

        public Alternative.Builder alternative(ILootCondition.IBuilder builderIn)
        {
            this.conditions.add(builderIn.build());
            return this;
        }

        public ILootCondition build()
        {
            return new Alternative(this.conditions.toArray(new ILootCondition[0]));
        }
    }

    public static class Serializer implements ILootSerializer<Alternative>
    {
        public void serialize(JsonObject p_230424_1_, Alternative p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            p_230424_1_.add("terms", p_230424_3_.serialize(p_230424_2_.conditions));
        }

        public Alternative deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_)
        {
            ILootCondition[] ailootcondition = JSONUtils.deserializeClass(p_230423_1_, "terms", p_230423_2_, ILootCondition[].class);
            return new Alternative(ailootcondition);
        }
    }
}
