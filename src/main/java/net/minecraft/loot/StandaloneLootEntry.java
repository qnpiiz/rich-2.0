package net.minecraft.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;

public abstract class StandaloneLootEntry extends LootEntry
{
    /** The weight of the entry. */
    protected final int weight;

    /** The quality of the entry. */
    protected final int quality;

    /** Functions that are ran on the entry. */
    protected final ILootFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> combinedFunctions;
    private final ILootGenerator generator = new StandaloneLootEntry.Generator()
    {
        public void func_216188_a(Consumer<ItemStack> p_216188_1_, LootContext p_216188_2_)
        {
            StandaloneLootEntry.this.func_216154_a(ILootFunction.func_215858_a(StandaloneLootEntry.this.combinedFunctions, p_216188_1_, p_216188_2_), p_216188_2_);
        }
    };

    protected StandaloneLootEntry(int weightIn, int qualityIn, ILootCondition[] conditionsIn, ILootFunction[] functionsIn)
    {
        super(conditionsIn);
        this.weight = weightIn;
        this.quality = qualityIn;
        this.functions = functionsIn;
        this.combinedFunctions = LootFunctionManager.combine(functionsIn);
    }

    public void func_225579_a_(ValidationTracker p_225579_1_)
    {
        super.func_225579_a_(p_225579_1_);

        for (int i = 0; i < this.functions.length; ++i)
        {
            this.functions[i].func_225580_a_(p_225579_1_.func_227534_b_(".functions[" + i + "]"));
        }
    }

    protected abstract void func_216154_a(Consumer<ItemStack> stackConsumer, LootContext context);

    public boolean expand(LootContext p_expand_1_, Consumer<ILootGenerator> p_expand_2_)
    {
        if (this.test(p_expand_1_))
        {
            p_expand_2_.accept(this.generator);
            return true;
        }
        else
        {
            return false;
        }
    }

    public static StandaloneLootEntry.Builder<?> builder(StandaloneLootEntry.ILootEntryBuilder entryBuilderIn)
    {
        return new StandaloneLootEntry.BuilderImpl(entryBuilderIn);
    }

    public abstract static class Builder<T extends StandaloneLootEntry.Builder<T>> extends LootEntry.Builder<T> implements ILootFunctionConsumer<T>
    {
        protected int weight = 1;
        protected int quality = 0;
        private final List<ILootFunction> functions = Lists.newArrayList();

        public T acceptFunction(ILootFunction.IBuilder functionBuilder)
        {
            this.functions.add(functionBuilder.build());
            return this.func_212845_d_();
        }

        protected ILootFunction[] getFunctions()
        {
            return this.functions.toArray(new ILootFunction[0]);
        }

        public T weight(int weightIn)
        {
            this.weight = weightIn;
            return this.func_212845_d_();
        }

        public T quality(int qualityIn)
        {
            this.quality = qualityIn;
            return this.func_212845_d_();
        }
    }

    static class BuilderImpl extends StandaloneLootEntry.Builder<StandaloneLootEntry.BuilderImpl>
    {
        private final StandaloneLootEntry.ILootEntryBuilder builder;

        public BuilderImpl(StandaloneLootEntry.ILootEntryBuilder builder)
        {
            this.builder = builder;
        }

        protected StandaloneLootEntry.BuilderImpl func_212845_d_()
        {
            return this;
        }

        public LootEntry build()
        {
            return this.builder.build(this.weight, this.quality, this.func_216079_f(), this.getFunctions());
        }
    }

    public abstract class Generator implements ILootGenerator
    {
        protected Generator()
        {
        }

        public int getEffectiveWeight(float luck)
        {
            return Math.max(MathHelper.floor((float)StandaloneLootEntry.this.weight + (float)StandaloneLootEntry.this.quality * luck), 0);
        }
    }

    @FunctionalInterface
    public interface ILootEntryBuilder
    {
        StandaloneLootEntry build(int p_build_1_, int p_build_2_, ILootCondition[] p_build_3_, ILootFunction[] p_build_4_);
    }

    public abstract static class Serializer<T extends StandaloneLootEntry> extends LootEntry.Serializer<T>
    {
        public void doSerialize(JsonObject object, T context, JsonSerializationContext conditions)
        {
            if (context.weight != 1)
            {
                object.addProperty("weight", context.weight);
            }

            if (context.quality != 0)
            {
                object.addProperty("quality", context.quality);
            }

            if (!ArrayUtils.isEmpty((Object[])context.functions))
            {
                object.add("functions", conditions.serialize(context.functions));
            }
        }

        public final T deserialize(JsonObject object, JsonDeserializationContext context, ILootCondition[] conditions)
        {
            int i = JSONUtils.getInt(object, "weight", 1);
            int j = JSONUtils.getInt(object, "quality", 0);
            ILootFunction[] ailootfunction = JSONUtils.deserializeClass(object, "functions", new ILootFunction[0], context, ILootFunction[].class);
            return this.deserialize(object, context, i, j, conditions, ailootfunction);
        }

        protected abstract T deserialize(JsonObject object, JsonDeserializationContext context, int weight, int quality, ILootCondition[] conditions, ILootFunction[] functions);
    }
}
