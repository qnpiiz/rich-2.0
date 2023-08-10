package com.mojang.blaze3d;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.IRenderCall;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Empty3i
{
    private final List<ConcurrentLinkedQueue<IRenderCall>> linkedRenderCalls = ImmutableList.of(new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>());
    private volatile int x;
    private volatile int y;
    private volatile int z;

    public Empty3i()
    {
        this.x = this.y = this.z + 1;
    }
}
