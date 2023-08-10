package com.mojang.blaze3d.vertex;

import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.math.MathHelper;

public interface IVertexConsumer extends IVertexBuilder
{
    VertexFormatElement getCurrentElement();

    void nextVertexFormatIndex();

    void putByte(int indexIn, byte byteIn);

    void putShort(int indexIn, short shortIn);

    void putFloat(int indexIn, float floatIn);

    default IVertexBuilder pos(double x, double y)
    {
        return pos(x, y, 0);
    }

default IVertexBuilder pos(double x, double y, double z)
    {
        if (this.getCurrentElement().getType() != VertexFormatElement.Type.FLOAT)
        {
            throw new IllegalStateException();
        }
        else
        {
            this.putFloat(0, (float)x);
            this.putFloat(4, (float)y);
            this.putFloat(8, (float)z);
            this.nextVertexFormatIndex();
            return this;
        }
    }

default IVertexBuilder color(int red, int green, int blue, int alpha)
    {
        VertexFormatElement vertexformatelement = this.getCurrentElement();

        if (vertexformatelement.getUsage() != VertexFormatElement.Usage.COLOR)
        {
            return this;
        }
        else if (vertexformatelement.getType() != VertexFormatElement.Type.UBYTE)
        {
            throw new IllegalStateException();
        }
        else
        {
            this.putByte(0, (byte)red);
            this.putByte(1, (byte)green);
            this.putByte(2, (byte)blue);
            this.putByte(3, (byte)alpha);
            this.nextVertexFormatIndex();
            return this;
        }
    }

default IVertexBuilder tex(float u, float v)
    {
        VertexFormatElement vertexformatelement = this.getCurrentElement();

        if (vertexformatelement.getUsage() == VertexFormatElement.Usage.UV && vertexformatelement.getIndex() == 0)
        {
            if (vertexformatelement.getType() != VertexFormatElement.Type.FLOAT)
            {
                throw new IllegalStateException();
            }
            else
            {
                this.putFloat(0, u);
                this.putFloat(4, v);
                this.nextVertexFormatIndex();
                return this;
            }
        }
        else
        {
            return this;
        }
    }

default IVertexBuilder overlay(int u, int v)
    {
        return this.texShort((short)u, (short)v, 1);
    }

default IVertexBuilder lightmap(int u, int v)
    {
        return this.texShort((short)u, (short)v, 2);
    }

default IVertexBuilder texShort(short u, short v, int index)
    {
        VertexFormatElement vertexformatelement = this.getCurrentElement();

        if (vertexformatelement.getUsage() == VertexFormatElement.Usage.UV && vertexformatelement.getIndex() == index)
        {
            if (vertexformatelement.getType() != VertexFormatElement.Type.SHORT)
            {
                throw new IllegalStateException();
            }
            else
            {
                this.putShort(0, u);
                this.putShort(2, v);
                this.nextVertexFormatIndex();
                return this;
            }
        }
        else
        {
            return this;
        }
    }

default IVertexBuilder normal(float x, float y, float z)
    {
        VertexFormatElement vertexformatelement = this.getCurrentElement();

        if (vertexformatelement.getUsage() != VertexFormatElement.Usage.NORMAL)
        {
            return this;
        }
        else if (vertexformatelement.getType() != VertexFormatElement.Type.BYTE)
        {
            throw new IllegalStateException();
        }
        else
        {
            this.putByte(0, normalInt(x));
            this.putByte(1, normalInt(y));
            this.putByte(2, normalInt(z));
            this.nextVertexFormatIndex();
            return this;
        }
    }

    static byte normalInt(float num)
    {
        return (byte)((int)(MathHelper.clamp(num, -1.0F, 1.0F) * 127.0F) & 255);
    }
}
