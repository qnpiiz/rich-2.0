package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class SetAttributes extends LootFunction
{
    private final List<SetAttributes.Modifier> modifiers;

    private SetAttributes(ILootCondition[] p_i51228_1_, List<SetAttributes.Modifier> p_i51228_2_)
    {
        super(p_i51228_1_);
        this.modifiers = ImmutableList.copyOf(p_i51228_2_);
    }

    public LootFunctionType getFunctionType()
    {
        return LootFunctionManager.SET_ATTRIBUTES;
    }

    public ItemStack doApply(ItemStack stack, LootContext context)
    {
        Random random = context.getRandom();

        for (SetAttributes.Modifier setattributes$modifier : this.modifiers)
        {
            UUID uuid = setattributes$modifier.uuid;

            if (uuid == null)
            {
                uuid = UUID.randomUUID();
            }

            EquipmentSlotType equipmentslottype = Util.getRandomObject(setattributes$modifier.slots, random);
            stack.addAttributeModifier(setattributes$modifier.attributeName, new AttributeModifier(uuid, setattributes$modifier.modifierName, (double)setattributes$modifier.amount.generateFloat(random), setattributes$modifier.operation), equipmentslottype);
        }

        return stack;
    }

    static class Modifier
    {
        private final String modifierName;
        private final Attribute attributeName;
        private final AttributeModifier.Operation operation;
        private final RandomValueRange amount;
        @Nullable
        private final UUID uuid;
        private final EquipmentSlotType[] slots;

        private Modifier(String p_i232172_1_, Attribute p_i232172_2_, AttributeModifier.Operation p_i232172_3_, RandomValueRange p_i232172_4_, EquipmentSlotType[] p_i232172_5_, @Nullable UUID p_i232172_6_)
        {
            this.modifierName = p_i232172_1_;
            this.attributeName = p_i232172_2_;
            this.operation = p_i232172_3_;
            this.amount = p_i232172_4_;
            this.uuid = p_i232172_6_;
            this.slots = p_i232172_5_;
        }

        public JsonObject serialize(JsonSerializationContext context)
        {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("name", this.modifierName);
            jsonobject.addProperty("attribute", Registry.ATTRIBUTE.getKey(this.attributeName).toString());
            jsonobject.addProperty("operation", func_216244_a(this.operation));
            jsonobject.add("amount", context.serialize(this.amount));

            if (this.uuid != null)
            {
                jsonobject.addProperty("id", this.uuid.toString());
            }

            if (this.slots.length == 1)
            {
                jsonobject.addProperty("slot", this.slots[0].getName());
            }
            else
            {
                JsonArray jsonarray = new JsonArray();

                for (EquipmentSlotType equipmentslottype : this.slots)
                {
                    jsonarray.add(new JsonPrimitive(equipmentslottype.getName()));
                }

                jsonobject.add("slot", jsonarray);
            }

            return jsonobject;
        }

        public static SetAttributes.Modifier deserialize(JsonObject jsonObj, JsonDeserializationContext context)
        {
            String s = JSONUtils.getString(jsonObj, "name");
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonObj, "attribute"));
            Attribute attribute = Registry.ATTRIBUTE.getOrDefault(resourcelocation);

            if (attribute == null)
            {
                throw new JsonSyntaxException("Unknown attribute: " + resourcelocation);
            }
            else
            {
                AttributeModifier.Operation attributemodifier$operation = func_216246_a(JSONUtils.getString(jsonObj, "operation"));
                RandomValueRange randomvaluerange = JSONUtils.deserializeClass(jsonObj, "amount", context, RandomValueRange.class);
                UUID uuid = null;
                EquipmentSlotType[] aequipmentslottype;

                if (JSONUtils.isString(jsonObj, "slot"))
                {
                    aequipmentslottype = new EquipmentSlotType[] {EquipmentSlotType.fromString(JSONUtils.getString(jsonObj, "slot"))};
                }
                else
                {
                    if (!JSONUtils.isJsonArray(jsonObj, "slot"))
                    {
                        throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
                    }

                    JsonArray jsonarray = JSONUtils.getJsonArray(jsonObj, "slot");
                    aequipmentslottype = new EquipmentSlotType[jsonarray.size()];
                    int i = 0;

                    for (JsonElement jsonelement : jsonarray)
                    {
                        aequipmentslottype[i++] = EquipmentSlotType.fromString(JSONUtils.getString(jsonelement, "slot"));
                    }

                    if (aequipmentslottype.length == 0)
                    {
                        throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
                    }
                }

                if (jsonObj.has("id"))
                {
                    String s1 = JSONUtils.getString(jsonObj, "id");

                    try
                    {
                        uuid = UUID.fromString(s1);
                    }
                    catch (IllegalArgumentException illegalargumentexception)
                    {
                        throw new JsonSyntaxException("Invalid attribute modifier id '" + s1 + "' (must be UUID format, with dashes)");
                    }
                }

                return new SetAttributes.Modifier(s, attribute, attributemodifier$operation, randomvaluerange, aequipmentslottype, uuid);
            }
        }

        private static String func_216244_a(AttributeModifier.Operation p_216244_0_)
        {
            switch (p_216244_0_)
            {
                case ADDITION:
                    return "addition";

                case MULTIPLY_BASE:
                    return "multiply_base";

                case MULTIPLY_TOTAL:
                    return "multiply_total";

                default:
                    throw new IllegalArgumentException("Unknown operation " + p_216244_0_);
            }
        }

        private static AttributeModifier.Operation func_216246_a(String p_216246_0_)
        {
            byte b0 = -1;

            switch (p_216246_0_.hashCode())
            {
                case -1226589444:
                    if (p_216246_0_.equals("addition"))
                    {
                        b0 = 0;
                    }

                    break;

                case -78229492:
                    if (p_216246_0_.equals("multiply_base"))
                    {
                        b0 = 1;
                    }

                    break;

                case 1886894441:
                    if (p_216246_0_.equals("multiply_total"))
                    {
                        b0 = 2;
                    }
            }

            switch (b0)
            {
                case 0:
                    return AttributeModifier.Operation.ADDITION;

                case 1:
                    return AttributeModifier.Operation.MULTIPLY_BASE;

                case 2:
                    return AttributeModifier.Operation.MULTIPLY_TOTAL;

                default:
                    throw new JsonSyntaxException("Unknown attribute modifier operation " + p_216246_0_);
            }
        }
    }

    public static class Serializer extends LootFunction.Serializer<SetAttributes>
    {
        public void serialize(JsonObject p_230424_1_, SetAttributes p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
            JsonArray jsonarray = new JsonArray();

            for (SetAttributes.Modifier setattributes$modifier : p_230424_2_.modifiers)
            {
                jsonarray.add(setattributes$modifier.serialize(p_230424_3_));
            }

            p_230424_1_.add("modifiers", jsonarray);
        }

        public SetAttributes deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn)
        {
            JsonArray jsonarray = JSONUtils.getJsonArray(object, "modifiers");
            List<SetAttributes.Modifier> list = Lists.newArrayListWithExpectedSize(jsonarray.size());

            for (JsonElement jsonelement : jsonarray)
            {
                list.add(SetAttributes.Modifier.deserialize(JSONUtils.getJsonObject(jsonelement, "modifier"), deserializationContext));
            }

            if (list.isEmpty())
            {
                throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
            }
            else
            {
                return new SetAttributes(conditionsIn, list);
            }
        }
    }
}
