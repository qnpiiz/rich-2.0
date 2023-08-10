package net.optifine.util;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class BufferUtil
{
    public static String getBufferHex(BufferBuilder bb)
    {
        int i = bb.getDrawMode();
        String s = "";
        int j = -1;

        if (i == 7)
        {
            s = "quad";
            j = 4;
        }
        else
        {
            if (i != 4)
            {
                return "Invalid draw mode: " + i;
            }

            s = "triangle";
            j = 3;
        }

        StringBuffer stringbuffer = new StringBuffer();
        int k = bb.getVertexCount();

        for (int l = 0; l < k; ++l)
        {
            if (l % j == 0)
            {
                stringbuffer.append(s + " " + l / j + "\n");
            }

            String s1 = getVertexHex(l, bb);
            stringbuffer.append(s1);
            stringbuffer.append("\n");
        }

        return stringbuffer.toString();
    }

    private static String getVertexHex(int vertex, BufferBuilder bb)
    {
        StringBuffer stringbuffer = new StringBuffer();
        ByteBuffer bytebuffer = bb.getByteBuffer();
        VertexFormat vertexformat = bb.getVertexFormat();
        int i = bb.getStartPosition() + vertex * vertexformat.getSize();

        for (VertexFormatElement vertexformatelement : vertexformat.getElements())
        {
            if (vertexformatelement.getElementCount() > 0)
            {
                stringbuffer.append("(");
            }

            for (int j = 0; j < vertexformatelement.getElementCount(); ++j)
            {
                if (j > 0)
                {
                    stringbuffer.append(" ");
                }

                switch (vertexformatelement.getType())
                {
                    case FLOAT:
                        stringbuffer.append(bytebuffer.getFloat(i));
                        break;

                    case UBYTE:
                    case BYTE:
                        stringbuffer.append((int)bytebuffer.get(i));
                        break;

                    case USHORT:
                    case SHORT:
                        stringbuffer.append((int)bytebuffer.getShort(i));
                        break;

                    case UINT:
                    case INT:
                        stringbuffer.append((int)bytebuffer.getShort(i));
                        break;

                    default:
                        stringbuffer.append("??");
                }

                i += vertexformatelement.getType().getSize();
            }

            if (vertexformatelement.getElementCount() > 0)
            {
                stringbuffer.append(")");
            }
        }

        return stringbuffer.toString();
    }

    public static String getBufferString(IntBuffer buf)
    {
        if (buf == null)
        {
            return "null";
        }
        else
        {
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append("(pos=" + buf.position() + " lim=" + buf.limit() + " cap=" + buf.capacity() + ")");
            stringbuffer.append("[");
            int i = Math.min(buf.limit(), 1024);

            for (int j = 0; j < i; ++j)
            {
                if (j > 0)
                {
                    stringbuffer.append(", ");
                }

                stringbuffer.append(buf.get(j));
            }

            stringbuffer.append("]");
            return stringbuffer.toString();
        }
    }

    public static int[] toArray(IntBuffer buf)
    {
        int[] aint = new int[buf.limit()];

        for (int i = 0; i < aint.length; ++i)
        {
            aint[i] = buf.get(i);
        }

        return aint;
    }
}
