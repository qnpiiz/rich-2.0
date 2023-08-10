package net.minecraft.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class TableBonus implements ILootCondition
{
    private final Enchantment enchantment;
    private final float[] chances;

    private TableBonus(Enchantment enchantment, float[] chances)
    {
        this.enchantment = enchantment;
        this.chances = chances;
    }

    public LootConditionType func_230419_b_()
    {
        return LootConditionManager.TABLE_BONUS;
    }

    public Set < LootParameter<? >> getRequiredParameters()
    {
        return ImmutableSet.of(LootParameters.TOOL);
    }

    public boolean test(LootContext p_test_1_)
    {
        ItemStack itemstack = p_test_1_.get(LootParameters.TOOL);
        int i = itemstack != null ? EnchantmentHelper.getEnchantmentLevel(this.enchantment, itemstack) : 0;
        float f = this.chances[Math.min(i, this.chances.length - 1)];
        return p_test_1_.getRandom().nextFloat() < f;
    }

    public static ILootCondition.IBuilder builder(Enchantment enchantmentIn, float... chancesIn)
    {
        return () ->
        {
            return new TableBonus(enchantmentIn, chancesIn);
        };
    }

    public static class Serializer implements ILootSerializer<TableBonus>
    {
        public void serialize(JsonObject p_230424_1_, TableBonus p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            p_230424_1_.addProperty("enchantment", Registry.ENCHANTMENT.getKey(p_230424_2_.enchantment).toString());
            p_230424_1_.add("chances", p_230424_3_.serialize(p_230424_2_.chances));
        }

        public TableBonus deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_)
        {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(p_230423_1_, "enchantment"));
            Enchantment enchantment = Registry.ENCHANTMENT.getOptional(resourcelocation).orElseThrow(() ->
            {
                return new JsonParseException("Invalid enchantment id: " + resourcelocation);
            });
            float[] afloat = JSONUtils.deserializeClass(p_230423_1_, "chances", p_230423_2_, float[].class);
            return new TableBonus(enchantment, afloat);
        }
    }
}
