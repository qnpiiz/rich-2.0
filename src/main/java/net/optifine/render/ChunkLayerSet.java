package net.optifine.render;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.client.renderer.RenderType;

public class ChunkLayerSet implements Set<RenderType>
{
    private boolean[] layers = new boolean[RenderType.CHUNK_RENDER_TYPES.length];

    public boolean add(RenderType renderType)
    {
        this.layers[renderType.ordinal()] = true;
        return false;
    }

    public boolean contains(RenderType renderType)
    {
        return this.layers[renderType.ordinal()];
    }

    public boolean contains(Object obj)
    {
        return obj instanceof RenderType ? this.contains((RenderType)obj) : false;
    }

    public int size()
    {
        throw new UnsupportedOperationException("Not supported");
    }

    public boolean isEmpty()
    {
        throw new UnsupportedOperationException("Not supported");
    }

    public Iterator<RenderType> iterator()
    {
        throw new UnsupportedOperationException("Not supported");
    }

    public Object[] toArray()
    {
        throw new UnsupportedOperationException("Not supported");
    }

    public <T> T[] toArray(T[] a)
    {
        throw new UnsupportedOperationException("Not supported");
    }

    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException("Not supported");
    }

    public boolean containsAll(Collection<?> c)
    {
        throw new UnsupportedOperationException("Not supported");
    }

    public boolean addAll(Collection <? extends RenderType > c)
    {
        throw new UnsupportedOperationException("Not supported");
    }

    public boolean retainAll(Collection<?> c)
    {
        throw new UnsupportedOperationException("Not supported");
    }

    public boolean removeAll(Collection<?> c)
    {
        throw new UnsupportedOperationException("Not supported");
    }

    public void clear()
    {
        throw new UnsupportedOperationException("Not supported");
    }
}
