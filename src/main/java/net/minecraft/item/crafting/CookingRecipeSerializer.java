package net.minecraft.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class CookingRecipeSerializer<T extends AbstractCookingRecipe> implements IRecipeSerializer<T>
{
    private final int cookingTime;
    private final CookingRecipeSerializer.IFactory<T> factory;

    public CookingRecipeSerializer(CookingRecipeSerializer.IFactory<T> factory, int cookingTime)
    {
        this.cookingTime = cookingTime;
        this.factory = factory;
    }

    public T read(ResourceLocation recipeId, JsonObject json)
    {
        String s = JSONUtils.getString(json, "group", "");
        JsonElement jsonelement = (JsonElement)(JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json, "ingredient") : JSONUtils.getJsonObject(json, "ingredient"));
        Ingredient ingredient = Ingredient.deserialize(jsonelement);
        String s1 = JSONUtils.getString(json, "result");
        ResourceLocation resourcelocation = new ResourceLocation(s1);
        ItemStack itemstack = new ItemStack(Registry.ITEM.getOptional(resourcelocation).orElseThrow(() ->
        {
            return new IllegalStateException("Item: " + s1 + " does not exist");
        }));
        float f = JSONUtils.getFloat(json, "experience", 0.0F);
        int i = JSONUtils.getInt(json, "cookingtime", this.cookingTime);
        return this.factory.create(recipeId, s, ingredient, itemstack, f, i);
    }

    public T read(ResourceLocation recipeId, PacketBuffer buffer)
    {
        String s = buffer.readString(32767);
        Ingredient ingredient = Ingredient.read(buffer);
        ItemStack itemstack = buffer.readItemStack();
        float f = buffer.readFloat();
        int i = buffer.readVarInt();
        return this.factory.create(recipeId, s, ingredient, itemstack, f, i);
    }

    public void write(PacketBuffer buffer, T recipe)
    {
        buffer.writeString(recipe.group);
        recipe.ingredient.write(buffer);
        buffer.writeItemStack(recipe.result);
        buffer.writeFloat(recipe.experience);
        buffer.writeVarInt(recipe.cookTime);
    }

    interface IFactory<T extends AbstractCookingRecipe>
    {
        T create(ResourceLocation p_create_1_, String p_create_2_, Ingredient p_create_3_, ItemStack p_create_4_, float p_create_5_, int p_create_6_);
    }
}
