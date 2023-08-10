package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;

public class LootingEnchantBonus extends LootFunction
{
    private final RandomValueRange count;
    private final int limit;

    private LootingEnchantBonus(ILootCondition[] conditions, RandomValueRange countIn, int limitIn)
    {
        super(conditions);
        this.count = countIn;
        this.limit = limitIn;
    }

    public LootFunctionType getFunctionType()
    {
        return LootFunctionManager.LOOTING_ENCHANT;
    }

    public Set < LootParameter<? >> getRequiredParameters()
    {
        return ImmutableSet.of(LootParameters.KILLER_ENTITY);
    }

    private boolean func_215917_b()
    {
        return this.limit > 0;
    }

    public ItemStack doApply(ItemStack stack, LootContext context)
    {
        Entity entity = context.get(LootParameters.KILLER_ENTITY);

        if (entity instanceof LivingEntity)
        {
            int i = EnchantmentHelper.getLootingModifier((LivingEntity)entity);

            if (i == 0)
            {
                return stack;
            }

            float f = (float)i * this.count.generateFloat(context.getRandom());
            stack.grow(Math.round(f));

            if (this.func_215917_b() && stack.getCount() > this.limit)
            {
                stack.setCount(this.limit);
            }
        }

        return stack;
    }

    public static LootingEnchantBonus.Builder builder(RandomValueRange range)
    {
        return new LootingEnchantBonus.Builder(range);
    }

    public static class Builder extends LootFunction.Builder<LootingEnchantBonus.Builder>
    {
        private final RandomValueRange field_216073_a;
        private int field_216074_b = 0;

        public Builder(RandomValueRange p_i50932_1_)
        {
            this.field_216073_a = p_i50932_1_;
        }

        protected LootingEnchantBonus.Builder doCast()
        {
            return this;
        }

        public LootingEnchantBonus.Builder func_216072_a(int p_216072_1_)
        {
            this.field_216074_b = p_216072_1_;
            return this;
        }

        public ILootFunction build()
        {
            return new LootingEnchantBonus(this.getConditions(), this.field_216073_a, this.field_216074_b);
        }
    }

    public static class Serializer extends LootFunction.Serializer<LootingEnchantBonus>
    {
        public void serialize(JsonObject p_230424_1_, LootingEnchantBonus p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
            p_230424_1_.add("count", p_230424_3_.serialize(p_230424_2_.count));

            if (p_230424_2_.func_215917_b())
            {
                p_230424_1_.add("limit", p_230424_3_.serialize(p_230424_2_.limit));
            }
        }

        public LootingEnchantBonus deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn)
        {
            int i = JSONUtils.getInt(object, "limit", 0);
            return new LootingEnchantBonus(conditionsIn, JSONUtils.deserializeClass(object, "count", deserializationContext, RandomValueRange.class), i);
        }
    }
}
