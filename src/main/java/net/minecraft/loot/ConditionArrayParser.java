package net.minecraft.loot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConditionArrayParser
{
    private static final Logger field_234045_a_ = LogManager.getLogger();
    private final ResourceLocation field_234046_b_;
    private final LootPredicateManager field_234047_c_;
    private final Gson field_234048_d_ = LootSerializers.func_237386_a_().create();

    public ConditionArrayParser(ResourceLocation p_i231549_1_, LootPredicateManager p_i231549_2_)
    {
        this.field_234046_b_ = p_i231549_1_;
        this.field_234047_c_ = p_i231549_2_;
    }

    public final ILootCondition[] func_234050_a_(JsonArray p_234050_1_, String p_234050_2_, LootParameterSet p_234050_3_)
    {
        ILootCondition[] ailootcondition = this.field_234048_d_.fromJson(p_234050_1_, ILootCondition[].class);
        ValidationTracker validationtracker = new ValidationTracker(p_234050_3_, this.field_234047_c_::func_227517_a_, (p_234052_0_) ->
        {
            return null;
        });

        for (ILootCondition ilootcondition : ailootcondition)
        {
            ilootcondition.func_225580_a_(validationtracker);
            validationtracker.getProblems().forEach((p_234051_1_, p_234051_2_) ->
            {
                field_234045_a_.warn("Found validation problem in advancement trigger {}/{}: {}", p_234050_2_, p_234051_1_, p_234051_2_);
            });
        }

        return ailootcondition;
    }

    public ResourceLocation func_234049_a_()
    {
        return this.field_234046_b_;
    }
}
