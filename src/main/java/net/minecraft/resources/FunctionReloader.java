package net.minecraft.resources;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.command.CommandSource;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.TagCollectionReader;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FunctionReloader implements IFutureReloadListener
{
    private static final Logger field_240924_a_ = LogManager.getLogger();
    private static final int field_240925_b_ = "functions/".length();
    private static final int field_240926_c_ = ".mcfunction".length();
    private volatile Map<ResourceLocation, FunctionObject> field_240927_d_ = ImmutableMap.of();
    private final TagCollectionReader<FunctionObject> field_244357_e = new TagCollectionReader<>(this::func_240940_a_, "tags/functions", "function");
    private volatile ITagCollection<FunctionObject> field_240928_e_ = ITagCollection.getEmptyTagCollection();
    private final int field_240929_f_;
    private final CommandDispatcher<CommandSource> field_240930_g_;

    public Optional<FunctionObject> func_240940_a_(ResourceLocation p_240940_1_)
    {
        return Optional.ofNullable(this.field_240927_d_.get(p_240940_1_));
    }

    public Map<ResourceLocation, FunctionObject> func_240931_a_()
    {
        return this.field_240927_d_;
    }

    public ITagCollection<FunctionObject> func_240942_b_()
    {
        return this.field_240928_e_;
    }

    public ITag<FunctionObject> func_240943_b_(ResourceLocation p_240943_1_)
    {
        return this.field_240928_e_.getTagByID(p_240943_1_);
    }

    public FunctionReloader(int p_i232596_1_, CommandDispatcher<CommandSource> p_i232596_2_)
    {
        this.field_240929_f_ = p_i232596_1_;
        this.field_240930_g_ = p_i232596_2_;
    }

    public CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
    {
        CompletableFuture<Map<ResourceLocation, ITag.Builder>> completablefuture = this.field_244357_e.readTagsFromManager(resourceManager, backgroundExecutor);
        CompletableFuture<Map<ResourceLocation, CompletableFuture<FunctionObject>>> completablefuture1 = CompletableFuture.supplyAsync(() ->
        {
            return resourceManager.getAllResourceLocations("functions", (p_240938_0_) -> {
                return p_240938_0_.endsWith(".mcfunction");
            });
        }, backgroundExecutor).thenCompose((p_240933_3_) ->
        {
            Map<ResourceLocation, CompletableFuture<FunctionObject>> map = Maps.newHashMap();
            CommandSource commandsource = new CommandSource(ICommandSource.DUMMY, Vector3d.ZERO, Vector2f.ZERO, (ServerWorld)null, this.field_240929_f_, "", StringTextComponent.EMPTY, (MinecraftServer)null, (Entity)null);

            for (ResourceLocation resourcelocation : p_240933_3_)
            {
                String s = resourcelocation.getPath();
                ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(field_240925_b_, s.length() - field_240926_c_));
                map.put(resourcelocation1, CompletableFuture.supplyAsync(() ->
                {
                    List<String> list = func_240934_a_(resourceManager, resourcelocation);
                    return FunctionObject.func_237140_a_(resourcelocation1, this.field_240930_g_, commandsource, list);
                }, backgroundExecutor));
            }

            CompletableFuture<?>[] completablefuture2 = map.values().toArray(new CompletableFuture[0]);
            return CompletableFuture.allOf(completablefuture2).handle((p_240939_1_, p_240939_2_) -> {
                return map;
            });
        });
        return completablefuture.thenCombine(completablefuture1, Pair::of).thenCompose(stage::markCompleteAwaitingOthers).thenAcceptAsync((p_240937_1_) ->
        {
            Map<ResourceLocation, CompletableFuture<FunctionObject>> map = (Map)p_240937_1_.getSecond();
            Builder<ResourceLocation, FunctionObject> builder = ImmutableMap.builder();
            map.forEach((p_240936_1_, p_240936_2_) -> {
                p_240936_2_.handle((p_240941_2_, p_240941_3_) -> {
                    if (p_240941_3_ != null)
                    {
                        field_240924_a_.error("Failed to load function {}", p_240936_1_, p_240941_3_);
                    }
                    else {
                        builder.put(p_240936_1_, p_240941_2_);
                    }

                    return null;
                }).join();
            });
            this.field_240927_d_ = builder.build();
            this.field_240928_e_ = this.field_244357_e.buildTagCollectionFromMap((Map)p_240937_1_.getFirst());
        }, gameExecutor);
    }

    private static List<String> func_240934_a_(IResourceManager p_240934_0_, ResourceLocation p_240934_1_)
    {
        try (IResource iresource = p_240934_0_.getResource(p_240934_1_))
        {
            return IOUtils.readLines(iresource.getInputStream(), StandardCharsets.UTF_8);
        }
        catch (IOException ioexception)
        {
            throw new CompletionException(ioexception);
        }
    }
}
