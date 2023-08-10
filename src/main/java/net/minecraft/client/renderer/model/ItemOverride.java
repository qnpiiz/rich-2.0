package net.minecraft.client.renderer.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class ItemOverride
{
    private final ResourceLocation location;
    private final Map<ResourceLocation, Float> mapResourceValues;

    public ItemOverride(ResourceLocation locationIn, Map<ResourceLocation, Float> propertyValues)
    {
        this.location = locationIn;
        this.mapResourceValues = propertyValues;
    }

    /**
     * Get the location of the target model
     */
    public ResourceLocation getLocation()
    {
        return this.location;
    }

    boolean matchesOverride(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity)
    {
        Item item = stack.getItem();

        for (Entry<ResourceLocation, Float> entry : this.mapResourceValues.entrySet())
        {
            IItemPropertyGetter iitempropertygetter = ItemModelsProperties.func_239417_a_(item, entry.getKey());

            if (iitempropertygetter == null || iitempropertygetter.call(stack, world, livingEntity) < entry.getValue())
            {
                return false;
            }
        }

        return true;
    }

    public static class Deserializer implements JsonDeserializer<ItemOverride>
    {
        protected Deserializer()
        {
        }

        public ItemOverride deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonobject, "model"));
            Map<ResourceLocation, Float> map = this.makeMapResourceValues(jsonobject);
            return new ItemOverride(resourcelocation, map);
        }

        protected Map<ResourceLocation, Float> makeMapResourceValues(JsonObject json)
        {
            Map<ResourceLocation, Float> map = Maps.newLinkedHashMap();
            JsonObject jsonobject = JSONUtils.getJsonObject(json, "predicate");

            for (Entry<String, JsonElement> entry : jsonobject.entrySet())
            {
                map.put(new ResourceLocation(entry.getKey()), JSONUtils.getFloat(entry.getValue(), entry.getKey()));
            }

            return map;
        }
    }
}
