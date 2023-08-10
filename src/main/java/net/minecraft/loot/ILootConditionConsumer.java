package net.minecraft.loot;

import net.minecraft.loot.conditions.ILootCondition;

public interface ILootConditionConsumer<T>
{
    T acceptCondition(ILootCondition.IBuilder conditionBuilder);

    T cast();
}
