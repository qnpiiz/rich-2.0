package net.optifine.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import net.optifine.util.ArrayUtils;
import net.optifine.util.BufferUtil;
import org.lwjgl.BufferUtils;

public class FlipTextures
{
    private String name;
    private IntBuffer texturesA;
    private IntBuffer texturesB;
    private boolean[] flips;
    private boolean[] changed;

    public FlipTextures(String name, int capacity)
    {
        this.name = name;
        this.texturesA = BufferUtils.createIntBuffer(capacity);
        this.texturesB = BufferUtils.createIntBuffer(capacity);
        this.flips = new boolean[capacity];
        this.changed = new boolean[capacity];
    }

    public int capacity()
    {
        return this.texturesA.capacity();
    }

    public int position()
    {
        return this.texturesA.position();
    }

    public int limit()
    {
        return this.texturesA.limit();
    }

    public FlipTextures position(int position)
    {
        ((Buffer)this.texturesA).position(position);
        ((Buffer)this.texturesB).position(position);
        return this;
    }

    public FlipTextures limit(int limit)
    {
        ((Buffer)this.texturesA).limit(limit);
        ((Buffer)this.texturesB).limit(limit);
        return this;
    }

    public int get(boolean main, int index)
    {
        return main ? this.getA(index) : this.getB(index);
    }

    public int getA(int index)
    {
        return this.get(index, this.flips[index]);
    }

    public int getB(int index)
    {
        return this.get(index, !this.flips[index]);
    }

    private int get(int index, boolean flipped)
    {
        IntBuffer intbuffer = flipped ? this.texturesB : this.texturesA;
        return intbuffer.get(index);
    }

    public void flip(int index)
    {
        this.flips[index] = !this.flips[index];
        this.changed[index] = true;
    }

    public boolean isChanged(int index)
    {
        return this.changed[index];
    }

    public void reset()
    {
        Arrays.fill(this.flips, false);
        Arrays.fill(this.changed, false);
    }

    public void genTextures()
    {
        GlStateManager.genTextures(this.texturesA);
        GlStateManager.genTextures(this.texturesB);
    }

    public void deleteTextures()
    {
        GlStateManager.deleteTextures(this.texturesA);
        GlStateManager.deleteTextures(this.texturesB);
        this.reset();
    }

    public void fill(int val)
    {
        int i = this.limit();

        for (int j = 0; j < i; ++j)
        {
            this.texturesA.put(j, val);
            this.texturesB.put(j, val);
        }
    }

    public FlipTextures clear()
    {
        this.position(0);
        this.limit(this.capacity());
        return this;
    }

    public String toString()
    {
        return "" + this.name + ", A: " + BufferUtil.getBufferString(this.texturesA) + ", B: " + BufferUtil.getBufferString(this.texturesB) + ", flips: [" + ArrayUtils.arrayToString(this.flips, this.limit()) + "], changed: [" + ArrayUtils.arrayToString(this.changed, this.limit()) + "]";
    }
}
