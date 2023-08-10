package net.optifine.shaders;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.vector.Vector3f;
import net.optifine.Config;
import net.optifine.render.VertexPosition;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class SVertexBuilder
{
    int vertexSize;
    int offsetNormal;
    int offsetUV;
    int offsetUVCenter;
    boolean hasNormal;
    boolean hasTangent;
    boolean hasUV;
    boolean hasUVCenter;
    long[] entityData = new long[10];
    int entityDataIndex = 0;

    public SVertexBuilder()
    {
        this.entityData[this.entityDataIndex] = 0L;
    }

    public static void initVertexBuilder(BufferBuilder wrr)
    {
        wrr.sVertexBuilder = new SVertexBuilder();
    }

    public void pushEntity(long data)
    {
        ++this.entityDataIndex;
        this.entityData[this.entityDataIndex] = data;
    }

    public void popEntity()
    {
        this.entityData[this.entityDataIndex] = 0L;
        --this.entityDataIndex;
    }

    public static void pushEntity(BlockState blockState, IVertexBuilder ivb)
    {
        if (ivb instanceof BufferBuilder)
        {
            BufferBuilder bufferbuilder = (BufferBuilder)ivb;
            int i = BlockAliases.getAliasBlockId(blockState);
            int j = BlockAliases.getAliasMetadata(blockState);
            int k = BlockAliases.getRenderType(blockState);
            int l = ((k & 65535) << 16) + (i & 65535);
            int i1 = j & 65535;
            bufferbuilder.sVertexBuilder.pushEntity(((long)i1 << 32) + (long)l);
        }
    }

    public static void popEntity(IVertexBuilder ivb)
    {
        if (ivb instanceof BufferBuilder)
        {
            BufferBuilder bufferbuilder = (BufferBuilder)ivb;
            bufferbuilder.sVertexBuilder.popEntity();
        }
    }

    public static boolean popEntity(boolean value, BufferBuilder wrr)
    {
        wrr.sVertexBuilder.popEntity();
        return value;
    }

    public static void endSetVertexFormat(BufferBuilder wrr)
    {
        SVertexBuilder svertexbuilder = wrr.sVertexBuilder;
        VertexFormat vertexformat = wrr.getVertexFormat();
        svertexbuilder.vertexSize = vertexformat.getSize() / 4;
        svertexbuilder.hasNormal = vertexformat.hasNormal();
        svertexbuilder.hasTangent = svertexbuilder.hasNormal;
        svertexbuilder.hasUV = vertexformat.hasUV(0);
        svertexbuilder.offsetNormal = svertexbuilder.hasNormal ? vertexformat.getNormalOffset() / 4 : 0;
        svertexbuilder.offsetUV = svertexbuilder.hasUV ? vertexformat.getUvOffsetById(0) / 4 : 0;
        svertexbuilder.offsetUVCenter = 8;
    }

    public static void beginAddVertex(BufferBuilder wrr)
    {
        if (wrr.getVertexCount() == 0)
        {
            endSetVertexFormat(wrr);
        }
    }

    public static void endAddVertex(BufferBuilder wrr)
    {
        SVertexBuilder svertexbuilder = wrr.sVertexBuilder;

        if (svertexbuilder.vertexSize == 18)
        {
            if (wrr.getDrawMode() == 7 && wrr.getVertexCount() % 4 == 0)
            {
                svertexbuilder.calcNormal(wrr, wrr.getBufferIntSize() - 4 * svertexbuilder.vertexSize);
            }

            long i = svertexbuilder.entityData[svertexbuilder.entityDataIndex];
            int j = wrr.getBufferIntSize() - 18 + 13;
            j = j + wrr.getIntStartPosition();
            wrr.getIntBuffer().put(j, (int)i);
            wrr.getIntBuffer().put(j + 1, (int)(i >> 32));
        }
    }

    public static void beginAddVertexData(BufferBuilder wrr, int[] data)
    {
        if (wrr.getVertexCount() == 0)
        {
            endSetVertexFormat(wrr);
        }

        SVertexBuilder svertexbuilder = wrr.sVertexBuilder;

        if (svertexbuilder.vertexSize == 18)
        {
            long i = svertexbuilder.entityData[svertexbuilder.entityDataIndex];

            for (int j = 13; j + 1 < data.length; j += 18)
            {
                data[j] = (int)i;
                data[j + 1] = (int)(i >> 32);
            }
        }
    }

    public static void beginAddVertexData(BufferBuilder wrr, ByteBuffer byteBuffer)
    {
        if (wrr.getVertexCount() == 0)
        {
            endSetVertexFormat(wrr);
        }

        SVertexBuilder svertexbuilder = wrr.sVertexBuilder;

        if (svertexbuilder.vertexSize == 18)
        {
            long i = svertexbuilder.entityData[svertexbuilder.entityDataIndex];
            int j = byteBuffer.limit() / 4;

            for (int k = 13; k + 1 < j; k += 18)
            {
                int l = (int)i;
                int i1 = (int)(i >> 32);
                byteBuffer.putInt(k * 4, l);
                byteBuffer.putInt((k + 1) * 4, i1);
            }
        }
    }

    public static void endAddVertexData(BufferBuilder wrr)
    {
        SVertexBuilder svertexbuilder = wrr.sVertexBuilder;

        if (svertexbuilder.vertexSize == 18 && wrr.getDrawMode() == 7 && wrr.getVertexCount() % 4 == 0)
        {
            svertexbuilder.calcNormal(wrr, wrr.getBufferIntSize() - 4 * svertexbuilder.vertexSize);
        }
    }

    public void calcNormal(BufferBuilder wrr, int baseIndex)
    {
        baseIndex = baseIndex + wrr.getIntStartPosition();
        FloatBuffer floatbuffer = wrr.getFloatBuffer();
        IntBuffer intbuffer = wrr.getIntBuffer();
        float f = floatbuffer.get(baseIndex + 0 * this.vertexSize);
        float f1 = floatbuffer.get(baseIndex + 0 * this.vertexSize + 1);
        float f2 = floatbuffer.get(baseIndex + 0 * this.vertexSize + 2);
        float f3 = floatbuffer.get(baseIndex + 0 * this.vertexSize + this.offsetUV);
        float f4 = floatbuffer.get(baseIndex + 0 * this.vertexSize + this.offsetUV + 1);
        float f5 = floatbuffer.get(baseIndex + 1 * this.vertexSize);
        float f6 = floatbuffer.get(baseIndex + 1 * this.vertexSize + 1);
        float f7 = floatbuffer.get(baseIndex + 1 * this.vertexSize + 2);
        float f8 = floatbuffer.get(baseIndex + 1 * this.vertexSize + this.offsetUV);
        float f9 = floatbuffer.get(baseIndex + 1 * this.vertexSize + this.offsetUV + 1);
        float f10 = floatbuffer.get(baseIndex + 2 * this.vertexSize);
        float f11 = floatbuffer.get(baseIndex + 2 * this.vertexSize + 1);
        float f12 = floatbuffer.get(baseIndex + 2 * this.vertexSize + 2);
        float f13 = floatbuffer.get(baseIndex + 2 * this.vertexSize + this.offsetUV);
        float f14 = floatbuffer.get(baseIndex + 2 * this.vertexSize + this.offsetUV + 1);
        float f15 = floatbuffer.get(baseIndex + 3 * this.vertexSize);
        float f16 = floatbuffer.get(baseIndex + 3 * this.vertexSize + 1);
        float f17 = floatbuffer.get(baseIndex + 3 * this.vertexSize + 2);
        float f18 = floatbuffer.get(baseIndex + 3 * this.vertexSize + this.offsetUV);
        float f19 = floatbuffer.get(baseIndex + 3 * this.vertexSize + this.offsetUV + 1);
        float f20 = f10 - f;
        float f21 = f11 - f1;
        float f22 = f12 - f2;
        float f23 = f15 - f5;
        float f24 = f16 - f6;
        float f25 = f17 - f7;
        float f30 = f21 * f25 - f24 * f22;
        float f31 = f22 * f23 - f25 * f20;
        float f32 = f20 * f24 - f23 * f21;
        float f33 = f30 * f30 + f31 * f31 + f32 * f32;
        float f34 = (double)f33 != 0.0D ? (float)(1.0D / Math.sqrt((double)f33)) : 1.0F;
        f30 = f30 * f34;
        f31 = f31 * f34;
        f32 = f32 * f34;
        f20 = f5 - f;
        f21 = f6 - f1;
        f22 = f7 - f2;
        float f26 = f8 - f3;
        float f27 = f9 - f4;
        f23 = f10 - f;
        f24 = f11 - f1;
        f25 = f12 - f2;
        float f28 = f13 - f3;
        float f29 = f14 - f4;
        float f35 = f26 * f29 - f28 * f27;
        float f36 = f35 != 0.0F ? 1.0F / f35 : 1.0F;
        float f37 = (f29 * f20 - f27 * f23) * f36;
        float f38 = (f29 * f21 - f27 * f24) * f36;
        float f39 = (f29 * f22 - f27 * f25) * f36;
        float f40 = (f26 * f23 - f28 * f20) * f36;
        float f41 = (f26 * f24 - f28 * f21) * f36;
        float f42 = (f26 * f25 - f28 * f22) * f36;
        f33 = f37 * f37 + f38 * f38 + f39 * f39;
        f34 = (double)f33 != 0.0D ? (float)(1.0D / Math.sqrt((double)f33)) : 1.0F;
        f37 = f37 * f34;
        f38 = f38 * f34;
        f39 = f39 * f34;
        f33 = f40 * f40 + f41 * f41 + f42 * f42;
        f34 = (double)f33 != 0.0D ? (float)(1.0D / Math.sqrt((double)f33)) : 1.0F;
        f40 = f40 * f34;
        f41 = f41 * f34;
        f42 = f42 * f34;
        float f43 = f32 * f38 - f31 * f39;
        float f44 = f30 * f39 - f32 * f37;
        float f45 = f31 * f37 - f30 * f38;
        float f46 = f40 * f43 + f41 * f44 + f42 * f45 < 0.0F ? -1.0F : 1.0F;
        int i = (int)(f30 * 127.0F) & 255;
        int j = (int)(f31 * 127.0F) & 255;
        int k = (int)(f32 * 127.0F) & 255;
        int l = (k << 16) + (j << 8) + i;
        intbuffer.put(baseIndex + 0 * this.vertexSize + this.offsetNormal, l);
        intbuffer.put(baseIndex + 1 * this.vertexSize + this.offsetNormal, l);
        intbuffer.put(baseIndex + 2 * this.vertexSize + this.offsetNormal, l);
        intbuffer.put(baseIndex + 3 * this.vertexSize + this.offsetNormal, l);
        int i1 = ((int)(f37 * 32767.0F) & 65535) + (((int)(f38 * 32767.0F) & 65535) << 16);
        int j1 = ((int)(f39 * 32767.0F) & 65535) + (((int)(f46 * 32767.0F) & 65535) << 16);
        intbuffer.put(baseIndex + 0 * this.vertexSize + 11, i1);
        intbuffer.put(baseIndex + 0 * this.vertexSize + 11 + 1, j1);
        intbuffer.put(baseIndex + 1 * this.vertexSize + 11, i1);
        intbuffer.put(baseIndex + 1 * this.vertexSize + 11 + 1, j1);
        intbuffer.put(baseIndex + 2 * this.vertexSize + 11, i1);
        intbuffer.put(baseIndex + 2 * this.vertexSize + 11 + 1, j1);
        intbuffer.put(baseIndex + 3 * this.vertexSize + 11, i1);
        intbuffer.put(baseIndex + 3 * this.vertexSize + 11 + 1, j1);
        float f47 = (f3 + f8 + f13 + f18) / 4.0F;
        float f48 = (f4 + f9 + f14 + f19) / 4.0F;
        floatbuffer.put(baseIndex + 0 * this.vertexSize + 9, f47);
        floatbuffer.put(baseIndex + 0 * this.vertexSize + 9 + 1, f48);
        floatbuffer.put(baseIndex + 1 * this.vertexSize + 9, f47);
        floatbuffer.put(baseIndex + 1 * this.vertexSize + 9 + 1, f48);
        floatbuffer.put(baseIndex + 2 * this.vertexSize + 9, f47);
        floatbuffer.put(baseIndex + 2 * this.vertexSize + 9 + 1, f48);
        floatbuffer.put(baseIndex + 3 * this.vertexSize + 9, f47);
        floatbuffer.put(baseIndex + 3 * this.vertexSize + 9 + 1, f48);

        if (Shaders.useVelocityAttrib)
        {
            VertexPosition[] avertexposition = wrr.getQuadVertexPositions();
            int k1 = Config.getWorldRenderer().getFrameCount();
            this.setVelocity(floatbuffer, baseIndex, 0, avertexposition, k1, f, f1, f2);
            this.setVelocity(floatbuffer, baseIndex, 1, avertexposition, k1, f5, f6, f7);
            this.setVelocity(floatbuffer, baseIndex, 2, avertexposition, k1, f10, f11, f12);
            this.setVelocity(floatbuffer, baseIndex, 3, avertexposition, k1, f15, f16, f17);
            wrr.setQuadVertexPositions((VertexPosition[])null);
        }

        if (wrr.getVertexFormat() == DefaultVertexFormats.BLOCK)
        {
            Vector3f vector3f = wrr.getMidBlock();
            float f51 = vector3f.getX();
            float f49 = vector3f.getY();
            float f50 = vector3f.getZ();
            this.setMidBlock(intbuffer, baseIndex, 0, f51 - f, f49 - f1, f50 - f2);
            this.setMidBlock(intbuffer, baseIndex, 1, f51 - f5, f49 - f6, f50 - f7);
            this.setMidBlock(intbuffer, baseIndex, 2, f51 - f10, f49 - f11, f50 - f12);
            this.setMidBlock(intbuffer, baseIndex, 3, f51 - f15, f49 - f16, f50 - f17);
        }
    }

    public void setMidBlock(IntBuffer intBuffer, int baseIndex, int vertex, float mbx, float mby, float mbz)
    {
        int i = (int)(mbx * 64.0F) & 255;
        int j = (int)(mby * 64.0F) & 255;
        int k = (int)(mbz * 64.0F) & 255;
        int l = (k << 16) + (j << 8) + i;
        intBuffer.put(baseIndex + vertex * this.vertexSize + 8, l);
    }

    public void setVelocity(FloatBuffer floatBuffer, int baseIndex, int vertex, VertexPosition[] vps, int frameId, float x, float y, float z)
    {
        float f = 0.0F;
        float f1 = 0.0F;
        float f2 = 0.0F;

        if (vps != null && vps.length == 4)
        {
            VertexPosition vertexposition = vps[vertex];
            vertexposition.setPosition(frameId, x, y, z);

            if (vertexposition.isVelocityValid())
            {
                f = vertexposition.getVelocityX();
                f1 = vertexposition.getVelocityY();
                f2 = vertexposition.getVelocityZ();
            }
        }

        int i = baseIndex + vertex * this.vertexSize + 15;
        floatBuffer.put(i + 0, f);
        floatBuffer.put(i + 1, f1);
        floatBuffer.put(i + 2, f2);
    }

    public static void calcNormalChunkLayer(BufferBuilder wrr)
    {
        if (wrr.getVertexFormat().hasNormal() && wrr.getDrawMode() == 7 && wrr.getVertexCount() % 4 == 0)
        {
            SVertexBuilder svertexbuilder = wrr.sVertexBuilder;
            endSetVertexFormat(wrr);
            int i = wrr.getVertexCount() * svertexbuilder.vertexSize;

            for (int j = 0; j < i; j += svertexbuilder.vertexSize * 4)
            {
                svertexbuilder.calcNormal(wrr, j);
            }
        }
    }

    public static boolean preDrawArrays(VertexFormat vf, ByteBuffer bb)
    {
        int i = vf.getSize();

        if (i != 72)
        {
            return false;
        }
        else
        {
            ((Buffer)bb).position(36);
            GL20.glVertexAttribPointer(Shaders.midTexCoordAttrib, 2, GL11.GL_FLOAT, false, i, bb);
            ((Buffer)bb).position(44);
            GL20.glVertexAttribPointer(Shaders.tangentAttrib, 4, GL11.GL_SHORT, false, i, bb);
            ((Buffer)bb).position(52);
            GL20.glVertexAttribPointer(Shaders.entityAttrib, 3, GL11.GL_SHORT, false, i, bb);
            ((Buffer)bb).position(60);
            GL20.glVertexAttribPointer(Shaders.velocityAttrib, 3, GL11.GL_FLOAT, false, i, bb);
            ((Buffer)bb).position(0);
            GL20.glEnableVertexAttribArray(Shaders.midTexCoordAttrib);
            GL20.glEnableVertexAttribArray(Shaders.tangentAttrib);
            GL20.glEnableVertexAttribArray(Shaders.entityAttrib);
            GL20.glEnableVertexAttribArray(Shaders.velocityAttrib);
            return true;
        }
    }

    public static void postDrawArrays()
    {
        GL20.glDisableVertexAttribArray(Shaders.midTexCoordAttrib);
        GL20.glDisableVertexAttribArray(Shaders.tangentAttrib);
        GL20.glDisableVertexAttribArray(Shaders.entityAttrib);
        GL20.glDisableVertexAttribArray(Shaders.velocityAttrib);
    }
}
