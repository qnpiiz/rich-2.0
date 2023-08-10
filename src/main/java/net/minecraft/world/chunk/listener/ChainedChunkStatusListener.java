package net.minecraft.world.chunk.listener;

import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

public class ChainedChunkStatusListener implements IChunkStatusListener
{
    private final IChunkStatusListener delegate;
    private final DelegatedTaskExecutor<Runnable> executor;

    public ChainedChunkStatusListener(IChunkStatusListener delegate, Executor executor)
    {
        this.delegate = delegate;
        this.executor = DelegatedTaskExecutor.create(executor, "progressListener");
    }

    public void start(ChunkPos center)
    {
        this.executor.enqueue(() ->
        {
            this.delegate.start(center);
        });
    }

    public void statusChanged(ChunkPos chunkPosition, @Nullable ChunkStatus newStatus)
    {
        this.executor.enqueue(() ->
        {
            this.delegate.statusChanged(chunkPosition, newStatus);
        });
    }

    public void stop()
    {
        this.executor.enqueue(this.delegate::stop);
    }
}
