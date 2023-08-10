package net.minecraft.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.profiler.IProfiler;

public interface IFutureReloadListener
{
    CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor);

default String getSimpleName()
    {
        return this.getClass().getSimpleName();
    }

    public interface IStage
    {
        <T> CompletableFuture<T> markCompleteAwaitingOthers(T backgroundResult);
    }
}
