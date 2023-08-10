package net.minecraft.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BlockModelDefinition implements Supplier<JsonElement>
{
    private final Map < BlockModeInfo<?>, BlockModeInfo<?>.Field > infoToInfoFieldMap = Maps.newLinkedHashMap();

    public <T> BlockModelDefinition replaceInfoValue(BlockModeInfo<T> info, T value)
    {
        BlockModeInfo<?>.Field blockmodeinfo = this.infoToInfoFieldMap.put(info, info.getFieldInfo(value));

        if (blockmodeinfo != null)
        {
            throw new IllegalStateException("Replacing value of " + blockmodeinfo + " with " + value);
        }
        else
        {
            return this;
        }
    }

    public static BlockModelDefinition getNewModelDefinition()
    {
        return new BlockModelDefinition();
    }

    public static BlockModelDefinition mergeDefinitions(BlockModelDefinition definition1, BlockModelDefinition definition2)
    {
        BlockModelDefinition blockmodeldefinition = new BlockModelDefinition();
        blockmodeldefinition.infoToInfoFieldMap.putAll(definition1.infoToInfoFieldMap);
        blockmodeldefinition.infoToInfoFieldMap.putAll(definition2.infoToInfoFieldMap);
        return blockmodeldefinition;
    }

    public JsonElement get()
    {
        JsonObject jsonobject = new JsonObject();
        this.infoToInfoFieldMap.values().forEach((field) ->
        {
            field.serialize(jsonobject);
        });
        return jsonobject;
    }

    public static JsonElement serialize(List<BlockModelDefinition> definitions)
    {
        if (definitions.size() == 1)
        {
            return definitions.get(0).get();
        }
        else
        {
            JsonArray jsonarray = new JsonArray();
            definitions.forEach((definition) ->
            {
                jsonarray.add(definition.get());
            });
            return jsonarray;
        }
    }
}
