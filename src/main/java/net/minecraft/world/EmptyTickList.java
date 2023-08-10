package net.minecraft.world;

import net.minecraft.util.math.BlockPos;

public class EmptyTickList<T> implements ITickList<T>
{
    private static final EmptyTickList<Object> INSTANCE = new EmptyTickList<>();

    public static <T> EmptyTickList<T> get()
    {
        return (EmptyTickList<T>) INSTANCE;
    }

    public boolean isTickScheduled(BlockPos pos, T itemIn)
    {
        return false;
    }

    public void scheduleTick(BlockPos pos, T itemIn, int scheduledTime)
    {
    }

    public void scheduleTick(BlockPos pos, T itemIn, int scheduledTime, TickPriority priority)
    {
    }

    /**
     * Checks if this position/item is scheduled to be updated this tick
     */
    public boolean isTickPending(BlockPos pos, T obj)
    {
        return false;
    }
}
