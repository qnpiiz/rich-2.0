package net.minecraft.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.JSONUtils;

public class Inverted implements ILootCondition
{
    private final ILootCondition term;

    private Inverted(ILootCondition term)
    {
        this.term = term;
    }

    public LootConditionType func_230419_b_()
    {
        return LootConditionManager.INVERTED;
    }

    public final boolean test(LootContext p_test_1_)
    {
        return !this.term.test(p_test_1_);
    }

    public Set < LootParameter<? >> getRequiredParameters()
    {
        return this.term.getRequiredParameters();
    }

    public void func_225580_a_(ValidationTracker p_225580_1_)
    {
        ILootCondition.super.func_225580_a_(p_225580_1_);
        this.term.func_225580_a_(p_225580_1_);
    }

    public static ILootCondition.IBuilder builder(ILootCondition.IBuilder p_215979_0_)
    {
        Inverted inverted = new Inverted(p_215979_0_.build());
        return () ->
        {
            return inverted;
        };
    }

    public static class Serializer implements ILootSerializer<Inverted>
    {
        public void serialize(JsonObject p_230424_1_, Inverted p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            p_230424_1_.add("term", p_230424_3_.serialize(p_230424_2_.term));
        }

        public Inverted deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_)
        {
            ILootCondition ilootcondition = JSONUtils.deserializeClass(p_230423_1_, "term", p_230423_2_, ILootCondition.class);
            return new Inverted(ilootcondition);
        }
    }
}
