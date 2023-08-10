package net.minecraft.loot;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.functions.EnchantRandomly;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.loot.functions.SetNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class PiglinBarteringAddition implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>
{
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_accept_1_)
    {
        p_accept_1_.accept(LootTables.PIGLIN_BARTERING, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BOOK).weight(5).acceptFunction((new EnchantRandomly.Builder()).func_237424_a_(Enchantments.SOUL_SPEED))).addEntry(ItemLootEntry.builder(Items.IRON_BOOTS).weight(8).acceptFunction((new EnchantRandomly.Builder()).func_237424_a_(Enchantments.SOUL_SPEED))).addEntry(ItemLootEntry.builder(Items.POTION).weight(8).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (nbt) ->
        {
            nbt.putString("Potion", "minecraft:fire_resistance");
        })))).addEntry(ItemLootEntry.builder(Items.SPLASH_POTION).weight(8).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (nbt) ->
        {
            nbt.putString("Potion", "minecraft:fire_resistance");
        })))).addEntry(ItemLootEntry.builder(Items.POTION).weight(10).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (nbt) ->
        {
            nbt.putString("Potion", "minecraft:water");
        })))).addEntry(ItemLootEntry.builder(Items.IRON_NUGGET).weight(10).acceptFunction(SetCount.builder(RandomValueRange.of(10.0F, 36.0F)))).addEntry(ItemLootEntry.builder(Items.ENDER_PEARL).weight(10).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 4.0F)))).addEntry(ItemLootEntry.builder(Items.STRING).weight(20).acceptFunction(SetCount.builder(RandomValueRange.of(3.0F, 9.0F)))).addEntry(ItemLootEntry.builder(Items.QUARTZ).weight(20).acceptFunction(SetCount.builder(RandomValueRange.of(5.0F, 12.0F)))).addEntry(ItemLootEntry.builder(Items.OBSIDIAN).weight(40)).addEntry(ItemLootEntry.builder(Items.CRYING_OBSIDIAN).weight(40).acceptFunction(SetCount.builder(RandomValueRange.of(1.0F, 3.0F)))).addEntry(ItemLootEntry.builder(Items.FIRE_CHARGE).weight(40)).addEntry(ItemLootEntry.builder(Items.LEATHER).weight(40).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 4.0F)))).addEntry(ItemLootEntry.builder(Items.SOUL_SAND).weight(40).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 8.0F)))).addEntry(ItemLootEntry.builder(Items.NETHER_BRICK).weight(40).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 8.0F)))).addEntry(ItemLootEntry.builder(Items.SPECTRAL_ARROW).weight(40).acceptFunction(SetCount.builder(RandomValueRange.of(6.0F, 12.0F)))).addEntry(ItemLootEntry.builder(Items.GRAVEL).weight(40).acceptFunction(SetCount.builder(RandomValueRange.of(8.0F, 16.0F)))).addEntry(ItemLootEntry.builder(Items.BLACKSTONE).weight(40).acceptFunction(SetCount.builder(RandomValueRange.of(8.0F, 16.0F))))));
    }
}
