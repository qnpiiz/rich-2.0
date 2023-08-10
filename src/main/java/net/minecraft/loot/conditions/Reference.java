package net.minecraft.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reference implements ILootCondition
{
    private static final Logger field_227561_a_ = LogManager.getLogger();
    private final ResourceLocation field_227562_b_;

    private Reference(ResourceLocation p_i225894_1_)
    {
        this.field_227562_b_ = p_i225894_1_;
    }

    public LootConditionType func_230419_b_()
    {
        return LootConditionManager.REFERENCE;
    }

    public void func_225580_a_(ValidationTracker p_225580_1_)
    {
        if (p_225580_1_.func_227536_b_(this.field_227562_b_))
        {
            p_225580_1_.addProblem("Condition " + this.field_227562_b_ + " is recursively called");
        }
        else
        {
            ILootCondition.super.func_225580_a_(p_225580_1_);
            ILootCondition ilootcondition = p_225580_1_.func_227541_d_(this.field_227562_b_);

            if (ilootcondition == null)
            {
                p_225580_1_.addProblem("Unknown condition table called " + this.field_227562_b_);
            }
            else
            {
                ilootcondition.func_225580_a_(p_225580_1_.func_227531_a_(".{" + this.field_227562_b_ + "}", this.field_227562_b_));
            }
        }
    }

    public boolean test(LootContext p_test_1_)
    {
        ILootCondition ilootcondition = p_test_1_.getLootCondition(this.field_227562_b_);

        if (p_test_1_.addCondition(ilootcondition))
        {
            boolean flag;

            try
            {
                flag = ilootcondition.test(p_test_1_);
            }
            finally
            {
                p_test_1_.removeCondition(ilootcondition);
            }

            return flag;
        }
        else
        {
            field_227561_a_.warn("Detected infinite loop in loot tables");
            return false;
        }
    }

    public static class Serializer implements ILootSerializer<Reference>
    {
        public void serialize(JsonObject p_230424_1_, Reference p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            p_230424_1_.addProperty("name", p_230424_2_.field_227562_b_.toString());
        }

        public Reference deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_)
        {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(p_230423_1_, "name"));
            return new Reference(resourcelocation);
        }
    }
}
