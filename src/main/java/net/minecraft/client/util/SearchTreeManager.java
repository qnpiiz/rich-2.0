package net.minecraft.client.util;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;

public class SearchTreeManager implements IResourceManagerReloadListener
{
    public static final SearchTreeManager.Key<ItemStack> ITEMS = new SearchTreeManager.Key<>();
    public static final SearchTreeManager.Key<ItemStack> TAGS = new SearchTreeManager.Key<>();
    public static final SearchTreeManager.Key<RecipeList> RECIPES = new SearchTreeManager.Key<>();
    private final Map < SearchTreeManager.Key<?>, IMutableSearchTree<? >> trees = Maps.newHashMap();

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        for (IMutableSearchTree<?> imutablesearchtree : this.trees.values())
        {
            imutablesearchtree.recalculate();
        }
    }

    public <T> void add(SearchTreeManager.Key<T> key, IMutableSearchTree<T> value)
    {
        this.trees.put(key, value);
    }

    public <T> IMutableSearchTree<T> get(SearchTreeManager.Key<T> key)
    {
        return (IMutableSearchTree<T>)this.trees.get(key);
    }

    public static class Key<T>
    {
    }
}
