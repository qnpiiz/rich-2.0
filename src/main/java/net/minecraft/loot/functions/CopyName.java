package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.INameable;
import net.minecraft.util.JSONUtils;

public class CopyName extends LootFunction
{
    private final CopyName.Source source;

    private CopyName(ILootCondition[] conditionsIn, CopyName.Source sourceIn)
    {
        super(conditionsIn);
        this.source = sourceIn;
    }

    public LootFunctionType getFunctionType()
    {
        return LootFunctionManager.COPY_NAME;
    }

    public Set < LootParameter<? >> getRequiredParameters()
    {
        return ImmutableSet.of(this.source.parameter);
    }

    public ItemStack doApply(ItemStack stack, LootContext context)
    {
        Object object = context.get(this.source.parameter);

        if (object instanceof INameable)
        {
            INameable inameable = (INameable)object;

            if (inameable.hasCustomName())
            {
                stack.setDisplayName(inameable.getDisplayName());
            }
        }

        return stack;
    }

    public static LootFunction.Builder<?> builder(CopyName.Source sourceIn)
    {
        return builder((p_215891_1_) ->
        {
            return new CopyName(p_215891_1_, sourceIn);
        });
    }

    public static class Serializer extends LootFunction.Serializer<CopyName>
    {
        public void serialize(JsonObject p_230424_1_, CopyName p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
            p_230424_1_.addProperty("source", p_230424_2_.source.name);
        }

        public CopyName deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn)
        {
            CopyName.Source copyname$source = CopyName.Source.byName(JSONUtils.getString(object, "source"));
            return new CopyName(conditionsIn, copyname$source);
        }
    }

    public static enum Source
    {
        THIS("this", LootParameters.THIS_ENTITY),
        KILLER("killer", LootParameters.KILLER_ENTITY),
        KILLER_PLAYER("killer_player", LootParameters.LAST_DAMAGE_PLAYER),
        BLOCK_ENTITY("block_entity", LootParameters.BLOCK_ENTITY);

        public final String name;
        public final LootParameter<?> parameter;

        private Source(String nameIn, LootParameter<?> parameterIn)
        {
            this.name = nameIn;
            this.parameter = parameterIn;
        }

        public static CopyName.Source byName(String nameIn)
        {
            for (CopyName.Source copyname$source : values())
            {
                if (copyname$source.name.equals(nameIn))
                {
                    return copyname$source;
                }
            }

            throw new IllegalArgumentException("Invalid name source " + nameIn);
        }
    }
}
