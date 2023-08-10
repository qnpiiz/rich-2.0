package net.minecraft.resources;

import java.util.concurrent.CompletableFuture;
import net.minecraft.util.Unit;

public interface IAsyncReloader
{
    CompletableFuture<Unit> onceDone();

    float estimateExecutionSpeed();

    boolean asyncPartDone();

    boolean fullyDone();

    void join();
}
