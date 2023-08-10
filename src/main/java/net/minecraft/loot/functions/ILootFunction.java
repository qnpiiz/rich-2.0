package net.minecraft.loot.functions;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.IParameterized;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunctionType;

public interface ILootFunction extends IParameterized, BiFunction<ItemStack, LootContext, ItemStack>
{
    LootFunctionType getFunctionType();

    static Consumer<ItemStack> func_215858_a(BiFunction<ItemStack, LootContext, ItemStack> p_215858_0_, Consumer<ItemStack> stackConsumer, LootContext context)
    {
        return (stack) ->
        {
            stackConsumer.accept(p_215858_0_.apply(stack, context));
        };
    }

    public interface IBuilder
    {
        ILootFunction build();
    }
}
