package net.minecraft.data;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class SmithingRecipeBuilder
{
    private final Ingredient base;
    private final Ingredient addition;
    private final Item output;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private final IRecipeSerializer<?> serializer;

    public SmithingRecipeBuilder(IRecipeSerializer<?> serializer, Ingredient base, Ingredient addition, Item output)
    {
        this.serializer = serializer;
        this.base = base;
        this.addition = addition;
        this.output = output;
    }

    public static SmithingRecipeBuilder smithingRecipe(Ingredient base, Ingredient addition, Item output)
    {
        return new SmithingRecipeBuilder(IRecipeSerializer.SMITHING, base, addition, output);
    }

    public SmithingRecipeBuilder addCriterion(String name, ICriterionInstance criterion)
    {
        this.advancementBuilder.withCriterion(name, criterion);
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer, String id)
    {
        this.build(consumer, new ResourceLocation(id));
    }

    public void build(Consumer<IFinishedRecipe> recipe, ResourceLocation id)
    {
        this.validate(id);
        this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
        recipe.accept(new SmithingRecipeBuilder.Result(id, this.serializer, this.base, this.addition, this.output, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.output.getGroup().getPath() + "/" + id.getPath())));
    }

    private void validate(ResourceLocation id)
    {
        if (this.advancementBuilder.getCriteria().isEmpty())
        {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }

    public static class Result implements IFinishedRecipe
    {
        private final ResourceLocation id;
        private final Ingredient base;
        private final Ingredient addition;
        private final Item output;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementId;
        private final IRecipeSerializer<?> serializer;

        public Result(ResourceLocation id, IRecipeSerializer<?> serializer, Ingredient base, Ingredient addition, Item output, Advancement.Builder advancementBuilder, ResourceLocation advancementId)
        {
            this.id = id;
            this.serializer = serializer;
            this.base = base;
            this.addition = addition;
            this.output = output;
            this.advancementBuilder = advancementBuilder;
            this.advancementId = advancementId;
        }

        public void serialize(JsonObject json)
        {
            json.add("base", this.base.serialize());
            json.add("addition", this.addition.serialize());
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", Registry.ITEM.getKey(this.output).toString());
            json.add("result", jsonobject);
        }

        public ResourceLocation getID()
        {
            return this.id;
        }

        public IRecipeSerializer<?> getSerializer()
        {
            return this.serializer;
        }

        @Nullable
        public JsonObject getAdvancementJson()
        {
            return this.advancementBuilder.serialize();
        }

        @Nullable
        public ResourceLocation getAdvancementID()
        {
            return this.advancementId;
        }
    }
}
