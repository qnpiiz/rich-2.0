package net.minecraft.item.crafting;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.util.ResourceLocation;

public class RecipeBook
{
    protected final Set<ResourceLocation> recipes = Sets.newHashSet();
    protected final Set<ResourceLocation> newRecipes = Sets.newHashSet();
    private final RecipeBookStatus field_242138_c = new RecipeBookStatus();

    public void copyFrom(RecipeBook that)
    {
        this.recipes.clear();
        this.newRecipes.clear();
        this.field_242138_c.func_242150_a(that.field_242138_c);
        this.recipes.addAll(that.recipes);
        this.newRecipes.addAll(that.newRecipes);
    }

    public void unlock(IRecipe<?> recipe)
    {
        if (!recipe.isDynamic())
        {
            this.unlock(recipe.getId());
        }
    }

    protected void unlock(ResourceLocation resourceLocation)
    {
        this.recipes.add(resourceLocation);
    }

    public boolean isUnlocked(@Nullable IRecipe<?> recipe)
    {
        return recipe == null ? false : this.recipes.contains(recipe.getId());
    }

    public boolean isUnlocked(ResourceLocation id)
    {
        return this.recipes.contains(id);
    }

    public void lock(IRecipe<?> recipe)
    {
        this.lock(recipe.getId());
    }

    protected void lock(ResourceLocation resourceLocation)
    {
        this.recipes.remove(resourceLocation);
        this.newRecipes.remove(resourceLocation);
    }

    public boolean isNew(IRecipe<?> recipe)
    {
        return this.newRecipes.contains(recipe.getId());
    }

    public void markSeen(IRecipe<?> recipe)
    {
        this.newRecipes.remove(recipe.getId());
    }

    public void markNew(IRecipe<?> recipe)
    {
        this.markNew(recipe.getId());
    }

    protected void markNew(ResourceLocation resourceLocation)
    {
        this.newRecipes.add(resourceLocation);
    }

    public boolean func_242142_a(RecipeBookCategory p_242142_1_)
    {
        return this.field_242138_c.func_242151_a(p_242142_1_);
    }

    public void func_242143_a(RecipeBookCategory p_242143_1_, boolean p_242143_2_)
    {
        this.field_242138_c.func_242152_a(p_242143_1_, p_242143_2_);
    }

    public boolean func_242141_a(RecipeBookContainer<?> p_242141_1_)
    {
        return this.func_242145_b(p_242141_1_.func_241850_m());
    }

    public boolean func_242145_b(RecipeBookCategory p_242145_1_)
    {
        return this.field_242138_c.func_242158_b(p_242145_1_);
    }

    public void func_242146_b(RecipeBookCategory p_242146_1_, boolean p_242146_2_)
    {
        this.field_242138_c.func_242159_b(p_242146_1_, p_242146_2_);
    }

    public void func_242140_a(RecipeBookStatus p_242140_1_)
    {
        this.field_242138_c.func_242150_a(p_242140_1_);
    }

    public RecipeBookStatus func_242139_a()
    {
        return this.field_242138_c.func_242149_a();
    }

    public void func_242144_a(RecipeBookCategory p_242144_1_, boolean p_242144_2_, boolean p_242144_3_)
    {
        this.field_242138_c.func_242152_a(p_242144_1_, p_242144_2_);
        this.field_242138_c.func_242159_b(p_242144_1_, p_242144_3_);
    }
}
