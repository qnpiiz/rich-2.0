package net.optifine.shaders;

import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import net.optifine.util.ArrayUtils;
import net.optifine.util.BufferUtil;
import org.lwjgl.BufferUtils;

public class DrawBuffers
{
    private String name;
    private final int maxColorBuffers;
    private final int maxDrawBuffers;
    private final IntBuffer drawBuffers;
    private int[] attachmentMappings;
    private IntBuffer glDrawBuffers;

    public DrawBuffers(String name, int maxColorBuffers, int maxDrawBuffers)
    {
        this.name = name;
        this.maxColorBuffers = maxColorBuffers;
        this.maxDrawBuffers = maxDrawBuffers;
        this.drawBuffers = IntBuffer.wrap(new int[maxDrawBuffers]);
    }

    public int get(int index)
    {
        return this.drawBuffers.get(index);
    }

    public DrawBuffers put(int attachment)
    {
        this.resetMappings();
        this.drawBuffers.put(attachment);
        return this;
    }

    public DrawBuffers put(int index, int attachment)
    {
        this.resetMappings();
        this.drawBuffers.put(index, attachment);
        return this;
    }

    public int position()
    {
        return this.drawBuffers.position();
    }

    public DrawBuffers position(int newPosition)
    {
        this.resetMappings();
        ((Buffer)this.drawBuffers).position(newPosition);
        return this;
    }

    public int limit()
    {
        return this.drawBuffers.limit();
    }

    public DrawBuffers limit(int newLimit)
    {
        this.resetMappings();
        ((Buffer)this.drawBuffers).limit(newLimit);
        return this;
    }

    public int capacity()
    {
        return this.drawBuffers.capacity();
    }

    public DrawBuffers fill(int val)
    {
        for (int i = 0; i < this.drawBuffers.limit(); ++i)
        {
            this.drawBuffers.put(i, val);
        }

        this.resetMappings();
        return this;
    }

    private void resetMappings()
    {
        this.attachmentMappings = null;
        this.glDrawBuffers = null;
    }

    public int[] getAttachmentMappings()
    {
        if (this.attachmentMappings == null)
        {
            this.attachmentMappings = makeAttachmentMappings(this.drawBuffers, this.maxColorBuffers, this.maxDrawBuffers);
        }

        return this.attachmentMappings;
    }

    private static int[] makeAttachmentMappings(IntBuffer drawBuffers, int maxColorBuffers, int maxDrawBuffers)
    {
        int[] aint = new int[maxColorBuffers];
        Arrays.fill(aint, -1);

        for (int i = 0; i < drawBuffers.limit(); ++i)
        {
            int j = drawBuffers.get(i);
            int k = j - 36064;

            if (k >= 0 && k < maxDrawBuffers)
            {
                aint[k] = k;
            }
        }

        for (int i1 = 0; i1 < drawBuffers.limit(); ++i1)
        {
            int j1 = drawBuffers.get(i1);
            int k1 = j1 - 36064;

            if (k1 >= maxDrawBuffers && k1 < maxColorBuffers)
            {
                int l = getMappingIndex(k1, maxDrawBuffers, aint);

                if (l < 0)
                {
                    throw new RuntimeException("Too many draw buffers, mapping: " + ArrayUtils.arrayToString(aint));
                }

                aint[k1] = l;
            }
        }

        return aint;
    }

    private static int getMappingIndex(int ai, int maxDrawBuffers, int[] attachmentMappings)
    {
        if (ai < maxDrawBuffers)
        {
            return ai;
        }
        else if (attachmentMappings[ai] >= 0)
        {
            return attachmentMappings[ai];
        }
        else
        {
            for (int i = 0; i < maxDrawBuffers; ++i)
            {
                if (!ArrayUtils.contains(attachmentMappings, i))
                {
                    return i;
                }
            }

            return -1;
        }
    }

    public IntBuffer getGlDrawBuffers()
    {
        if (this.glDrawBuffers == null)
        {
            this.glDrawBuffers = makeGlDrawBuffers(this.drawBuffers, this.getAttachmentMappings());
        }

        return this.glDrawBuffers;
    }

    private static IntBuffer makeGlDrawBuffers(IntBuffer drawBuffers, int[] attachmentMappings)
    {
        IntBuffer intbuffer = BufferUtils.createIntBuffer(drawBuffers.capacity());

        for (int i = 0; i < drawBuffers.limit(); ++i)
        {
            int j = drawBuffers.get(i);
            int k = j - 36064;
            int l = 0;

            if (k >= 0 && k < attachmentMappings.length)
            {
                l = 36064 + attachmentMappings[k];
            }

            intbuffer.put(i, l);
        }

        ((Buffer)intbuffer).limit(drawBuffers.limit());
        ((Buffer)intbuffer).position(drawBuffers.position());
        return intbuffer;
    }

    public String getInfo(boolean glBuffers)
    {
        StringBuffer stringbuffer = new StringBuffer();

        for (int i = 0; i < this.drawBuffers.limit(); ++i)
        {
            int j = this.drawBuffers.get(i);
            int k = j - 36064;

            if (glBuffers)
            {
                int[] aint = this.getAttachmentMappings();

                if (k >= 0 && k < aint.length)
                {
                    k = aint[k];
                }
            }

            String s = this.getIndexName(k);
            stringbuffer.append(s);
        }

        return stringbuffer.toString();
    }

    private String getIndexName(int ai)
    {
        return ai >= 0 && ai < this.maxColorBuffers ? "" + ai : "N";
    }

    public int indexOf(int att)
    {
        for (int i = 0; i < this.limit(); ++i)
        {
            if (this.get(i) == att)
            {
                return i;
            }
        }

        return -1;
    }

    public String toString()
    {
        return "" + this.name + ": " + BufferUtil.getBufferString(this.drawBuffers) + ", mapping: " + ArrayUtils.arrayToString(this.attachmentMappings) + ", glDrawBuffers: " + BufferUtil.getBufferString(this.glDrawBuffers);
    }
}
