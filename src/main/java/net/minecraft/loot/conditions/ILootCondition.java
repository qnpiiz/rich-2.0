package net.minecraft.loot.conditions;

import java.util.function.Predicate;
import net.minecraft.loot.IParameterized;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;

public interface ILootCondition extends IParameterized, Predicate<LootContext>
{
    LootConditionType func_230419_b_();

    @FunctionalInterface
    public interface IBuilder
    {
        ILootCondition build();

    default ILootCondition.IBuilder inverted()
        {
            return Inverted.builder(this);
        }

    default Alternative.Builder alternative(ILootCondition.IBuilder builderIn)
        {
            return Alternative.builder(this, builderIn);
        }
    }
}
