package net.minecraft.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.util.JSONUtils;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootEntry implements ILootEntry
{
    /** Conditions for the loot entry to be applied. */
    protected final ILootCondition[] conditions;
    private final Predicate<LootContext> field_216143_c;

    protected LootEntry(ILootCondition[] p_i51254_1_)
    {
        this.conditions = p_i51254_1_;
        this.field_216143_c = LootConditionManager.and(p_i51254_1_);
    }

    public void func_225579_a_(ValidationTracker p_225579_1_)
    {
        for (int i = 0; i < this.conditions.length; ++i)
        {
            this.conditions[i].func_225580_a_(p_225579_1_.func_227534_b_(".condition[" + i + "]"));
        }
    }

    protected final boolean test(LootContext p_216141_1_)
    {
        return this.field_216143_c.test(p_216141_1_);
    }

    public abstract LootPoolEntryType func_230420_a_();

    public abstract static class Builder<T extends LootEntry.Builder<T>> implements ILootConditionConsumer<T>
    {
        private final List<ILootCondition> field_216082_a = Lists.newArrayList();

        protected abstract T func_212845_d_();

        public T acceptCondition(ILootCondition.IBuilder conditionBuilder)
        {
            this.field_216082_a.add(conditionBuilder.build());
            return this.func_212845_d_();
        }

        public final T cast()
        {
            return this.func_212845_d_();
        }

        protected ILootCondition[] func_216079_f()
        {
            return this.field_216082_a.toArray(new ILootCondition[0]);
        }

        public AlternativesLootEntry.Builder alternatively(LootEntry.Builder<?> p_216080_1_)
        {
            return new AlternativesLootEntry.Builder(this, p_216080_1_);
        }

        public abstract LootEntry build();
    }

    public abstract static class Serializer<T extends LootEntry> implements ILootSerializer<T>
    {
        public final void serialize(JsonObject p_230424_1_, T p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            if (!ArrayUtils.isEmpty((Object[])p_230424_2_.conditions))
            {
                p_230424_1_.add("conditions", p_230424_3_.serialize(p_230424_2_.conditions));
            }

            this.doSerialize(p_230424_1_, p_230424_2_, p_230424_3_);
        }

        public final T deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_)
        {
            ILootCondition[] ailootcondition = JSONUtils.deserializeClass(p_230423_1_, "conditions", new ILootCondition[0], p_230423_2_, ILootCondition[].class);
            return this.deserialize(p_230423_1_, p_230423_2_, ailootcondition);
        }

        public abstract void doSerialize(JsonObject object, T context, JsonSerializationContext conditions);

        public abstract T deserialize(JsonObject object, JsonDeserializationContext context, ILootCondition[] conditions);
    }
}
