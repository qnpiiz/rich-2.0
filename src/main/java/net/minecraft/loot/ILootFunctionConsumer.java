package net.minecraft.loot;

import net.minecraft.loot.functions.ILootFunction;

public interface ILootFunctionConsumer<T>
{
    T acceptFunction(ILootFunction.IBuilder functionBuilder);

    T cast();
}
