package net.minecraft.data;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public interface IFinishedRecipe
{
    void serialize(JsonObject json);

default JsonObject getRecipeJson()
    {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", Registry.RECIPE_SERIALIZER.getKey(this.getSerializer()).toString());
        this.serialize(jsonobject);
        return jsonobject;
    }

    /**
     * Gets the ID for the recipe.
     */
    ResourceLocation getID();

    IRecipeSerializer<?> getSerializer();

    @Nullable

    /**
     * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
     */
    JsonObject getAdvancementJson();

    @Nullable

    /**
     * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson} is
     * non-null.
     */
    ResourceLocation getAdvancementID();
}
