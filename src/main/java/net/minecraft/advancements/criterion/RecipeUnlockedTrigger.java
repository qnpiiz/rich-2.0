package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class RecipeUnlockedTrigger extends AbstractCriterionTrigger<RecipeUnlockedTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("recipe_unlocked");

    public ResourceLocation getId()
    {
        return ID;
    }

    public RecipeUnlockedTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "recipe"));
        return new RecipeUnlockedTrigger.Instance(entityPredicate, resourcelocation);
    }

    public void trigger(ServerPlayerEntity player, IRecipe<?> recipe)
    {
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(recipe);
        });
    }

    public static RecipeUnlockedTrigger.Instance create(ResourceLocation recipeID)
    {
        return new RecipeUnlockedTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, recipeID);
    }

    public static class Instance extends CriterionInstance
    {
        private final ResourceLocation recipe;

        public Instance(EntityPredicate.AndPredicate player, ResourceLocation recipeID)
        {
            super(RecipeUnlockedTrigger.ID, player);
            this.recipe = recipeID;
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.addProperty("recipe", this.recipe.toString());
            return jsonobject;
        }

        public boolean test(IRecipe<?> recipe)
        {
            return this.recipe.equals(recipe.getId());
        }
    }
}
