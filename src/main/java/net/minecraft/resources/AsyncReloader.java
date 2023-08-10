package net.minecraft.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;

public class AsyncReloader<S> implements IAsyncReloader
{
    protected final IResourceManager resourceManager;
    protected final CompletableFuture<Unit> allAsyncCompleted = new CompletableFuture<>();
    protected final CompletableFuture<List<S>> resultListFuture;
    private final Set<IFutureReloadListener> taskSet;
    private final int taskCount;
    private int syncScheduled;
    private int syncCompleted;
    private final AtomicInteger asyncScheduled = new AtomicInteger();
    private final AtomicInteger asyncCompleted = new AtomicInteger();

    public static AsyncReloader<Void> create(IResourceManager resourceManager, List<IFutureReloadListener> listeners, Executor backgroundExecutor, Executor gameExecutor, CompletableFuture<Unit> alsoWaitedFor)
    {
        return new AsyncReloader<>(backgroundExecutor, gameExecutor, resourceManager, listeners, (stage, resourceManager2, preparationsProfiler, p_219561_4_, p_219561_5_) ->
        {
            return preparationsProfiler.reload(stage, resourceManager2, EmptyProfiler.INSTANCE, EmptyProfiler.INSTANCE, backgroundExecutor, p_219561_5_);
        }, alsoWaitedFor);
    }

    protected AsyncReloader(Executor backgroundExecutor, final Executor gameExecutor, IResourceManager resourceManager, List<IFutureReloadListener> listeners, AsyncReloader.IStateFactory<S> stateFactory, CompletableFuture<Unit> alsoWaitedFor)
    {
        this.resourceManager = resourceManager;
        this.taskCount = listeners.size();
        this.asyncScheduled.incrementAndGet();
        alsoWaitedFor.thenRun(this.asyncCompleted::incrementAndGet);
        List<CompletableFuture<S>> list = Lists.newArrayList();
        CompletableFuture<?> completablefuture = alsoWaitedFor;
        this.taskSet = Sets.newHashSet(listeners);

        for (final IFutureReloadListener ifuturereloadlistener : listeners)
        {
            final CompletableFuture<?> completablefuture1 = completablefuture;
            CompletableFuture<S> completablefuture2 = stateFactory.create(new IFutureReloadListener.IStage()
            {
                public <T> CompletableFuture<T> markCompleteAwaitingOthers(T backgroundResult)
                {
                    gameExecutor.execute(() ->
                    {
                        AsyncReloader.this.taskSet.remove(ifuturereloadlistener);

                        if (AsyncReloader.this.taskSet.isEmpty())
                        {
                            AsyncReloader.this.allAsyncCompleted.complete(Unit.INSTANCE);
                        }
                    });
                    return AsyncReloader.this.allAsyncCompleted.thenCombine(completablefuture1, (unit, instance) ->
                    {
                        return backgroundResult;
                    });
                }
            }, resourceManager, ifuturereloadlistener, (runnable) ->
            {
                this.asyncScheduled.incrementAndGet();
                backgroundExecutor.execute(() -> {
                    runnable.run();
                    this.asyncCompleted.incrementAndGet();
                });
            }, (runnable) ->
            {
                ++this.syncScheduled;
                gameExecutor.execute(() -> {
                    runnable.run();
                    ++this.syncCompleted;
                });
            });
            list.add(completablefuture2);
            completablefuture = completablefuture2;
        }

        this.resultListFuture = Util.gather(list);
    }

    public CompletableFuture<Unit> onceDone()
    {
        return this.resultListFuture.thenApply((result) ->
        {
            return Unit.INSTANCE;
        });
    }

    public float estimateExecutionSpeed()
    {
        int i = this.taskCount - this.taskSet.size();
        float f = (float)(this.asyncCompleted.get() * 2 + this.syncCompleted * 2 + i * 1);
        float f1 = (float)(this.asyncScheduled.get() * 2 + this.syncScheduled * 2 + this.taskCount * 1);
        return f / f1;
    }

    public boolean asyncPartDone()
    {
        return this.allAsyncCompleted.isDone();
    }

    public boolean fullyDone()
    {
        return this.resultListFuture.isDone();
    }

    public void join()
    {
        if (this.resultListFuture.isCompletedExceptionally())
        {
            this.resultListFuture.join();
        }
    }

    public interface IStateFactory<S>
    {
        CompletableFuture<S> create(IFutureReloadListener.IStage p_create_1_, IResourceManager p_create_2_, IFutureReloadListener p_create_3_, Executor p_create_4_, Executor p_create_5_);
    }
}
