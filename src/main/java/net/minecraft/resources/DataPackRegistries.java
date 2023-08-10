package net.minecraft.resources;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.command.Commands;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.loot.LootTableManager;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.Unit;

public class DataPackRegistries implements AutoCloseable
{
    private static final CompletableFuture<Unit> field_240951_a_ = CompletableFuture.completedFuture(Unit.INSTANCE);
    private final IReloadableResourceManager resourceManager = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA);
    private final Commands commands;
    private final RecipeManager recipeManager = new RecipeManager();
    private final NetworkTagManager tagManager = new NetworkTagManager();
    private final LootPredicateManager lootPredicateManager = new LootPredicateManager();
    private final LootTableManager lootTableManager = new LootTableManager(this.lootPredicateManager);
    private final AdvancementManager advancementManager = new AdvancementManager(this.lootPredicateManager);
    private final FunctionReloader functionReloader;

    public DataPackRegistries(Commands.EnvironmentType envType, int permissionsLevel)
    {
        this.commands = new Commands(envType);
        this.functionReloader = new FunctionReloader(permissionsLevel, this.commands.getDispatcher());
        this.resourceManager.addReloadListener(this.tagManager);
        this.resourceManager.addReloadListener(this.lootPredicateManager);
        this.resourceManager.addReloadListener(this.recipeManager);
        this.resourceManager.addReloadListener(this.lootTableManager);
        this.resourceManager.addReloadListener(this.functionReloader);
        this.resourceManager.addReloadListener(this.advancementManager);
    }

    public FunctionReloader getFunctionReloader()
    {
        return this.functionReloader;
    }

    public LootPredicateManager getLootPredicateManager()
    {
        return this.lootPredicateManager;
    }

    public LootTableManager getLootTableManager()
    {
        return this.lootTableManager;
    }

    public ITagCollectionSupplier func_244358_d()
    {
        return this.tagManager.getTagCollectionSupplier();
    }

    public RecipeManager getRecipeManager()
    {
        return this.recipeManager;
    }

    public Commands getCommandManager()
    {
        return this.commands;
    }

    public AdvancementManager getAdvancementManager()
    {
        return this.advancementManager;
    }

    public IResourceManager getResourceManager()
    {
        return this.resourceManager;
    }

    public static CompletableFuture<DataPackRegistries> func_240961_a_(List<IResourcePack> p_240961_0_, Commands.EnvironmentType p_240961_1_, int p_240961_2_, Executor p_240961_3_, Executor p_240961_4_)
    {
        DataPackRegistries datapackregistries = new DataPackRegistries(p_240961_1_, p_240961_2_);
        CompletableFuture<Unit> completablefuture = datapackregistries.resourceManager.reloadResourcesAndThen(p_240961_3_, p_240961_4_, p_240961_0_, field_240951_a_);
        return completablefuture.whenComplete((p_240963_1_, p_240963_2_) ->
        {
            if (p_240963_2_ != null)
            {
                datapackregistries.close();
            }
        }).thenApply((p_240962_1_) ->
        {
            return datapackregistries;
        });
    }

    public void updateTags()
    {
        this.tagManager.getTagCollectionSupplier().updateTags();
    }

    public void close()
    {
        this.resourceManager.close();
    }
}
