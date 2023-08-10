package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class Criterion
{
    private final ICriterionInstance criterionInstance;

    public Criterion(ICriterionInstance criterionInstance)
    {
        this.criterionInstance = criterionInstance;
    }

    public Criterion()
    {
        this.criterionInstance = null;
    }

    public void serializeToNetwork(PacketBuffer buffer)
    {
    }

    public static Criterion deserializeCriterion(JsonObject json, ConditionArrayParser conditionParser)
    {
        ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "trigger"));
        ICriterionTrigger<?> icriteriontrigger = CriteriaTriggers.get(resourcelocation);

        if (icriteriontrigger == null)
        {
            throw new JsonSyntaxException("Invalid criterion trigger: " + resourcelocation);
        }
        else
        {
            ICriterionInstance icriterioninstance = icriteriontrigger.deserialize(JSONUtils.getJsonObject(json, "conditions", new JsonObject()), conditionParser);
            return new Criterion(icriterioninstance);
        }
    }

    public static Criterion criterionFromNetwork(PacketBuffer buffer)
    {
        return new Criterion();
    }

    public static Map<String, Criterion> deserializeAll(JsonObject json, ConditionArrayParser conditionParser)
    {
        Map<String, Criterion> map = Maps.newHashMap();

        for (Entry<String, JsonElement> entry : json.entrySet())
        {
            map.put(entry.getKey(), deserializeCriterion(JSONUtils.getJsonObject(entry.getValue(), "criterion"), conditionParser));
        }

        return map;
    }

    public static Map<String, Criterion> criteriaFromNetwork(PacketBuffer bus)
    {
        Map<String, Criterion> map = Maps.newHashMap();
        int i = bus.readVarInt();

        for (int j = 0; j < i; ++j)
        {
            map.put(bus.readString(32767), criterionFromNetwork(bus));
        }

        return map;
    }

    /**
     * Write {@code criteria} to {@code buf}.

     * @see #criteriaFromNetwork(PacketBuffer)
     */
    public static void serializeToNetwork(Map<String, Criterion> criteria, PacketBuffer buf)
    {
        buf.writeVarInt(criteria.size());

        for (Entry<String, Criterion> entry : criteria.entrySet())
        {
            buf.writeString(entry.getKey());
            entry.getValue().serializeToNetwork(buf);
        }
    }

    @Nullable
    public ICriterionInstance getCriterionInstance()
    {
        return this.criterionInstance;
    }

    public JsonElement serialize()
    {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("trigger", this.criterionInstance.getId().toString());
        JsonObject jsonobject1 = this.criterionInstance.serialize(ConditionArraySerializer.field_235679_a_);

        if (jsonobject1.size() != 0)
        {
            jsonobject.add("conditions", jsonobject1);
        }

        return jsonobject;
    }
}
