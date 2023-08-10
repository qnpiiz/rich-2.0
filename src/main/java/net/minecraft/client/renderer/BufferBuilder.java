package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.blaze3d.vertex.DefaultColorVertexBuilder;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.IVertexConsumer;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.optifine.Config;
import net.optifine.SmartAnimations;
import net.optifine.render.MultiTextureBuilder;
import net.optifine.render.MultiTextureData;
import net.optifine.render.RenderEnv;
import net.optifine.render.VertexPosition;
import net.optifine.shaders.SVertexBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BufferBuilder extends DefaultColorVertexBuilder implements IVertexConsumer
{
    private static final Logger LOGGER = LogManager.getLogger();
    private ByteBuffer byteBuffer;
    private final List<BufferBuilder.DrawState> drawStates = Lists.newArrayList();
    private int drawStateIndex = 0;
    private int renderedBytes = 0;
    private int nextElementBytes = 0;
    private int uploadedBytes = 0;
    private int vertexCount;
    @Nullable
    private VertexFormatElement vertexFormatElement;
    private int vertexFormatIndex;
    private int drawMode;
    private VertexFormat vertexFormat;
    private boolean fastFormat;
    private boolean fullFormat;
    private boolean isDrawing;
    private RenderType renderType;
    private boolean renderBlocks;
    private TextureAtlasSprite[] quadSprites = null;
    private TextureAtlasSprite[] quadSpritesPrev = null;
    private TextureAtlasSprite quadSprite = null;
    private MultiTextureBuilder multiTextureBuilder = new MultiTextureBuilder();
    public SVertexBuilder sVertexBuilder;
    public RenderEnv renderEnv = null;
    public BitSet animatedSprites = null;
    public BitSet animatedSpritesCached = new BitSet();
    private ByteBuffer byteBufferTriangles;
    private Vector3f tempVec3f = new Vector3f();
    private float[] tempFloat4 = new float[4];
    private int[] tempInt4 = new int[4];
    private IntBuffer intBuffer;
    private FloatBuffer floatBuffer;
    private IRenderTypeBuffer.Impl renderTypeBuffer;
    private FloatBuffer floatBufferSort;
    private VertexPosition[] quadVertexPositions;
    private Vector3f midBlock = new Vector3f();

    public BufferBuilder(int bufferSizeIn)
    {
        this.byteBuffer = GLAllocation.createDirectByteBuffer(bufferSizeIn * 4);
        this.intBuffer = this.byteBuffer.asIntBuffer();
        this.floatBuffer = this.byteBuffer.asFloatBuffer();
        SVertexBuilder.initVertexBuilder(this);
    }

    protected void growBuffer()
    {
        this.growBuffer(this.vertexFormat.getSize());
    }

    private void growBuffer(int increaseAmount)
    {
        if (this.nextElementBytes + increaseAmount > this.byteBuffer.capacity())
        {
            int i = this.byteBuffer.capacity();
            int j = i + roundUpPositive(increaseAmount);
            LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", i, j);
            ByteBuffer bytebuffer = GLAllocation.createDirectByteBuffer(j);
            ((Buffer)this.byteBuffer).position(0);
            bytebuffer.put(this.byteBuffer);
            ((Buffer)bytebuffer).rewind();
            this.byteBuffer = bytebuffer;
            this.intBuffer = this.byteBuffer.asIntBuffer();
            this.floatBuffer = this.byteBuffer.asFloatBuffer();

            if (this.quadSprites != null)
            {
                TextureAtlasSprite[] atextureatlassprite = this.quadSprites;
                int k = this.getBufferQuadSize();
                this.quadSprites = new TextureAtlasSprite[k];
                System.arraycopy(atextureatlassprite, 0, this.quadSprites, 0, Math.min(atextureatlassprite.length, this.quadSprites.length));
                this.quadSpritesPrev = null;
            }
        }
    }

    private static int roundUpPositive(int xIn)
    {
        int i = 2097152;

        if (xIn == 0)
        {
            return i;
        }
        else
        {
            if (xIn < 0)
            {
                i *= -1;
            }

            int j = xIn % i;
            return j == 0 ? xIn : xIn + i - j;
        }
    }

    public void sortVertexData(float cameraX, float cameraY, float cameraZ)
    {
        ((Buffer)this.byteBuffer).clear();
        FloatBuffer floatbuffer = this.byteBuffer.asFloatBuffer();
        FloatBuffer floatbuffer1 = floatbuffer.slice();
        int i = this.vertexCount / 4;
        float[] afloat = new float[i];

        for (int j = 0; j < i; ++j)
        {
            afloat[j] = getDistanceSq(floatbuffer, cameraX, cameraY, cameraZ, this.vertexFormat.getIntegerSize(), this.renderedBytes / 4 + j * this.vertexFormat.getSize());
        }

        int[] aint = new int[i];

        for (int k = 0; k < aint.length; aint[k] = k++)
        {
        }

        IntArrays.mergeSort(aint, (p_lambda$sortVertexData$0_1_, p_lambda$sortVertexData$0_2_) ->
        {
            return Floats.compare(afloat[p_lambda$sortVertexData$0_2_], afloat[p_lambda$sortVertexData$0_1_]);
        });
        BitSet bitset = new BitSet();
        FloatBuffer floatbuffer2 = this.getFloatBufferSort(this.vertexFormat.getIntegerSize() * 4);

        for (int l = bitset.nextClearBit(0); l < aint.length; l = bitset.nextClearBit(l + 1))
        {
            int i1 = aint[l];

            if (i1 != l)
            {
                this.limitToVertex(floatbuffer, i1);
                ((Buffer)floatbuffer2).clear();
                floatbuffer2.put(floatbuffer);
                int j1 = i1;

                for (int k1 = aint[i1]; j1 != l; k1 = aint[k1])
                {
                    this.limitToVertex(floatbuffer, k1);
                    ((Buffer)floatbuffer1).clear();
                    ((Buffer)floatbuffer1).position(floatbuffer.position());
                    ((Buffer)floatbuffer1).limit(floatbuffer.limit());
                    this.limitToVertex(floatbuffer, j1);
                    floatbuffer.put(floatbuffer1);
                    bitset.set(j1);
                    j1 = k1;
                }

                this.limitToVertex(floatbuffer, l);
                ((Buffer)floatbuffer2).flip();
                floatbuffer.put(floatbuffer2);
            }

            bitset.set(l);
        }

        if (this.quadSprites != null)
        {
            TextureAtlasSprite[] atextureatlassprite = new TextureAtlasSprite[this.vertexCount / 4];
            int l1 = this.vertexFormat.getSize() / 4 * 4;

            for (int i2 = 0; i2 < aint.length; ++i2)
            {
                int j2 = aint[i2];
                atextureatlassprite[i2] = this.quadSprites[j2];
            }

            System.arraycopy(atextureatlassprite, 0, this.quadSprites, 0, atextureatlassprite.length);
        }
    }

    private void limitToVertex(FloatBuffer floatBufferIn, int indexIn)
    {
        int i = this.vertexFormat.getIntegerSize() * 4;
        ((Buffer)floatBufferIn).limit(this.renderedBytes / 4 + (indexIn + 1) * i);
        ((Buffer)floatBufferIn).position(this.renderedBytes / 4 + indexIn * i);
    }

    public BufferBuilder.State getVertexState()
    {
        ((Buffer)this.byteBuffer).limit(this.nextElementBytes);
        ((Buffer)this.byteBuffer).position(this.renderedBytes);
        ByteBuffer bytebuffer = ByteBuffer.allocate(this.vertexCount * this.vertexFormat.getSize());
        bytebuffer.put(this.byteBuffer);
        ((Buffer)this.byteBuffer).clear();
        TextureAtlasSprite[] atextureatlassprite = this.getQuadSpritesCopy();
        return new BufferBuilder.State(bytebuffer, this.vertexFormat, atextureatlassprite);
    }

    private TextureAtlasSprite[] getQuadSpritesCopy()
    {
        if (this.quadSprites == null)
        {
            return null;
        }
        else
        {
            int i = this.vertexCount / 4;
            TextureAtlasSprite[] atextureatlassprite = new TextureAtlasSprite[i];
            System.arraycopy(this.quadSprites, 0, atextureatlassprite, 0, i);
            return atextureatlassprite;
        }
    }

    private static float getDistanceSq(FloatBuffer floatBufferIn, float x, float y, float z, int integerSize, int offset)
    {
        float f = floatBufferIn.get(offset + integerSize * 0 + 0);
        float f1 = floatBufferIn.get(offset + integerSize * 0 + 1);
        float f2 = floatBufferIn.get(offset + integerSize * 0 + 2);
        float f3 = floatBufferIn.get(offset + integerSize * 1 + 0);
        float f4 = floatBufferIn.get(offset + integerSize * 1 + 1);
        float f5 = floatBufferIn.get(offset + integerSize * 1 + 2);
        float f6 = floatBufferIn.get(offset + integerSize * 2 + 0);
        float f7 = floatBufferIn.get(offset + integerSize * 2 + 1);
        float f8 = floatBufferIn.get(offset + integerSize * 2 + 2);
        float f9 = floatBufferIn.get(offset + integerSize * 3 + 0);
        float f10 = floatBufferIn.get(offset + integerSize * 3 + 1);
        float f11 = floatBufferIn.get(offset + integerSize * 3 + 2);
        float f12 = (f + f3 + f6 + f9) * 0.25F - x;
        float f13 = (f1 + f4 + f7 + f10) * 0.25F - y;
        float f14 = (f2 + f5 + f8 + f11) * 0.25F - z;
        return f12 * f12 + f13 * f13 + f14 * f14;
    }

    public void setVertexState(BufferBuilder.State state)
    {
        ((Buffer)state.stateByteBuffer).clear();
        int i = state.stateByteBuffer.capacity();
        this.growBuffer(i);
        ((Buffer)this.byteBuffer).limit(this.byteBuffer.capacity());
        ((Buffer)this.byteBuffer).position(this.renderedBytes);
        this.byteBuffer.put(state.stateByteBuffer);
        ((Buffer)this.byteBuffer).clear();
        VertexFormat vertexformat = state.stateVertexFormat;
        this.setVertexFormat(vertexformat);
        this.vertexCount = i / vertexformat.getSize();
        this.nextElementBytes = this.renderedBytes + this.vertexCount * vertexformat.getSize();

        if (state.stateQuadSprites != null)
        {
            if (this.quadSprites == null)
            {
                this.quadSprites = this.quadSpritesPrev;
            }

            if (this.quadSprites == null || this.quadSprites.length < this.getBufferQuadSize())
            {
                this.quadSprites = new TextureAtlasSprite[this.getBufferQuadSize()];
            }

            TextureAtlasSprite[] atextureatlassprite = state.stateQuadSprites;
            System.arraycopy(atextureatlassprite, 0, this.quadSprites, 0, atextureatlassprite.length);
        }
        else
        {
            if (this.quadSprites != null)
            {
                this.quadSpritesPrev = this.quadSprites;
            }

            this.quadSprites = null;
        }
    }

    public void begin(int glMode, VertexFormat format)
    {
        if (this.isDrawing)
        {
            throw new IllegalStateException("Already building!");
        }
        else
        {
            this.isDrawing = true;
            this.drawMode = glMode;
            this.setVertexFormat(format);
            this.vertexFormatElement = format.getElements().get(0);
            this.vertexFormatIndex = 0;
            ((Buffer)this.byteBuffer).clear();

            if (Config.isShaders())
            {
                SVertexBuilder.endSetVertexFormat(this);
            }

            if (Config.isMultiTexture())
            {
                this.initQuadSprites();
            }

            if (SmartAnimations.isActive())
            {
                if (this.animatedSprites == null)
                {
                    this.animatedSprites = this.animatedSpritesCached;
                }

                this.animatedSprites.clear();
            }
            else if (this.animatedSprites != null)
            {
                this.animatedSprites = null;
            }
        }
    }

    public IVertexBuilder tex(float u, float v)
    {
        if (this.quadSprite != null && this.quadSprites != null)
        {
            u = this.quadSprite.toSingleU(u);
            v = this.quadSprite.toSingleV(v);
            this.quadSprites[this.vertexCount / 4] = this.quadSprite;
        }

        return IVertexConsumer.super.tex(u, v);
    }

    private void setVertexFormat(VertexFormat vertexFormatIn)
    {
        if (this.vertexFormat != vertexFormatIn)
        {
            this.vertexFormat = vertexFormatIn;
            boolean flag = vertexFormatIn == DefaultVertexFormats.ENTITY;
            boolean flag1 = vertexFormatIn == DefaultVertexFormats.BLOCK;
            this.fastFormat = flag || flag1;
            this.fullFormat = flag;
        }
    }

    public void finishDrawing()
    {
        if (!this.isDrawing)
        {
            throw new IllegalStateException("Not building!");
        }
        else
        {
            this.isDrawing = false;
            MultiTextureData multitexturedata = this.multiTextureBuilder.build(this.vertexCount, this.renderType, this.quadSprites);
            this.drawStates.add(new BufferBuilder.DrawState(this.vertexFormat, this.vertexCount, this.drawMode, multitexturedata));
            this.renderType = null;
            this.renderBlocks = false;

            if (this.quadSprites != null)
            {
                this.quadSpritesPrev = this.quadSprites;
            }

            this.quadSprites = null;
            this.quadSprite = null;
            this.renderedBytes += this.vertexCount * this.vertexFormat.getSize();
            this.vertexCount = 0;
            this.vertexFormatElement = null;
            this.vertexFormatIndex = 0;
        }
    }

    public void putByte(int indexIn, byte byteIn)
    {
        this.byteBuffer.put(this.nextElementBytes + indexIn, byteIn);
    }

    public void putShort(int indexIn, short shortIn)
    {
        this.byteBuffer.putShort(this.nextElementBytes + indexIn, shortIn);
    }

    public void putFloat(int indexIn, float floatIn)
    {
        this.byteBuffer.putFloat(this.nextElementBytes + indexIn, floatIn);
    }

    public void endVertex()
    {
        if (this.vertexFormatIndex != 0)
        {
            throw new IllegalStateException("Not filled all elements of the vertex");
        }
        else
        {
            ++this.vertexCount;
            this.growBuffer();

            if (Config.isShaders())
            {
                SVertexBuilder.endAddVertex(this);
            }
        }
    }

    public void nextVertexFormatIndex()
    {
        ImmutableList<VertexFormatElement> immutablelist = this.vertexFormat.getElements();
        this.vertexFormatIndex = (this.vertexFormatIndex + 1) % immutablelist.size();
        this.nextElementBytes += this.vertexFormatElement.getSize();
        VertexFormatElement vertexformatelement = immutablelist.get(this.vertexFormatIndex);
        this.vertexFormatElement = vertexformatelement;

        if (vertexformatelement.getUsage() == VertexFormatElement.Usage.PADDING)
        {
            this.nextVertexFormatIndex();
        }

        if (this.defaultColor && this.vertexFormatElement.getUsage() == VertexFormatElement.Usage.COLOR)
        {
            IVertexConsumer.super.color(this.defaultRed, this.defaultGreen, this.defaultBlue, this.defaultAlpha);
        }
    }

    public IVertexBuilder color(int red, int green, int blue, int alpha)
    {
        if (this.defaultColor)
        {
            throw new IllegalStateException();
        }
        else
        {
            return IVertexConsumer.super.color(red, green, blue, alpha);
        }
    }

    public void addVertex(float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ)
    {
        if (this.defaultColor)
        {
            throw new IllegalStateException();
        }
        else
        {
            if (this.fastFormat)
            {
                this.putFloat(0, x);
                this.putFloat(4, y);
                this.putFloat(8, z);
                this.putByte(12, (byte)((int)(red * 255.0F)));
                this.putByte(13, (byte)((int)(green * 255.0F)));
                this.putByte(14, (byte)((int)(blue * 255.0F)));
                this.putByte(15, (byte)((int)(alpha * 255.0F)));
                this.putFloat(16, texU);
                this.putFloat(20, texV);
                int i;

                if (this.fullFormat)
                {
                    this.putShort(24, (short)(overlayUV & 65535));
                    this.putShort(26, (short)(overlayUV >> 16 & 65535));
                    i = 28;
                }
                else
                {
                    i = 24;
                }

                this.putShort(i + 0, (short)(lightmapUV & 65535));
                this.putShort(i + 2, (short)(lightmapUV >> 16 & 65535));
                this.putByte(i + 4, IVertexConsumer.normalInt(normalX));
                this.putByte(i + 5, IVertexConsumer.normalInt(normalY));
                this.putByte(i + 6, IVertexConsumer.normalInt(normalZ));
                this.nextElementBytes += this.vertexFormat.getSize();
                this.endVertex();
            }
            else
            {
                super.addVertex(x, y, z, red, green, blue, alpha, texU, texV, overlayUV, lightmapUV, normalX, normalY, normalZ);
            }
        }
    }

    public Pair<BufferBuilder.DrawState, ByteBuffer> getNextBuffer()
    {
        BufferBuilder.DrawState bufferbuilder$drawstate = this.drawStates.get(this.drawStateIndex++);
        ((Buffer)this.byteBuffer).position(this.uploadedBytes);
        this.uploadedBytes += bufferbuilder$drawstate.getVertexCount() * bufferbuilder$drawstate.getFormat().getSize();
        ((Buffer)this.byteBuffer).limit(this.uploadedBytes);

        if (this.drawStateIndex == this.drawStates.size() && this.vertexCount == 0)
        {
            this.reset();
        }

        ByteBuffer bytebuffer = this.byteBuffer.slice();
        bytebuffer.order(this.byteBuffer.order());
        ((Buffer)this.byteBuffer).clear();

        if (bufferbuilder$drawstate.drawMode == 7 && Config.isQuadsToTriangles())
        {
            if (this.byteBufferTriangles == null)
            {
                this.byteBufferTriangles = GLAllocation.createDirectByteBuffer(this.byteBuffer.capacity() * 2);
            }

            if (this.byteBufferTriangles.capacity() < this.byteBuffer.capacity() * 2)
            {
                this.byteBufferTriangles = GLAllocation.createDirectByteBuffer(this.byteBuffer.capacity() * 2);
            }

            VertexFormat vertexformat = bufferbuilder$drawstate.getFormat();
            int i = bufferbuilder$drawstate.getVertexCount();
            quadsToTriangles(bytebuffer, vertexformat, i, this.byteBufferTriangles);
            int j = i * 6 / 4;
            BufferBuilder.DrawState bufferbuilder$drawstate1 = new BufferBuilder.DrawState(vertexformat, j, 4);
            return Pair.of(bufferbuilder$drawstate1, this.byteBufferTriangles);
        }
        else
        {
            return Pair.of(bufferbuilder$drawstate, bytebuffer);
        }
    }

    public void reset()
    {
        if (this.renderedBytes != this.uploadedBytes)
        {
            LOGGER.warn("Bytes mismatch " + this.renderedBytes + " " + this.uploadedBytes);
        }

        this.discard();
    }

    public void discard()
    {
        this.renderedBytes = 0;
        this.uploadedBytes = 0;
        this.nextElementBytes = 0;
        this.drawStates.clear();
        this.drawStateIndex = 0;
        this.quadSprite = null;
    }

    public VertexFormatElement getCurrentElement()
    {
        if (this.vertexFormatElement == null)
        {
            throw new IllegalStateException("BufferBuilder not started");
        }
        else
        {
            return this.vertexFormatElement;
        }
    }

    public boolean isDrawing()
    {
        return this.isDrawing;
    }

    public void putSprite(TextureAtlasSprite p_putSprite_1_)
    {
        if (this.animatedSprites != null && p_putSprite_1_ != null && p_putSprite_1_.isTerrain() && p_putSprite_1_.getAnimationIndex() >= 0)
        {
            this.animatedSprites.set(p_putSprite_1_.getAnimationIndex());
        }

        if (this.quadSprites != null)
        {
            int i = this.vertexCount / 4;
            this.quadSprites[i] = p_putSprite_1_;
        }
    }

    public void setSprite(TextureAtlasSprite p_setSprite_1_)
    {
        if (this.animatedSprites != null && p_setSprite_1_ != null && p_setSprite_1_.isTerrain() && p_setSprite_1_.getAnimationIndex() >= 0)
        {
            this.animatedSprites.set(p_setSprite_1_.getAnimationIndex());
        }

        if (this.quadSprites != null)
        {
            this.quadSprite = p_setSprite_1_;
        }
    }

    public boolean isMultiTexture()
    {
        return this.quadSprites != null;
    }

    public void setRenderType(RenderType p_setRenderType_1_)
    {
        this.renderType = p_setRenderType_1_;
    }

    public RenderType getRenderType()
    {
        return this.renderType;
    }

    public void setRenderBlocks(boolean p_setRenderBlocks_1_)
    {
        this.renderBlocks = p_setRenderBlocks_1_;

        if (Config.isMultiTexture())
        {
            this.initQuadSprites();
        }
    }

    public void setBlockLayer(RenderType p_setBlockLayer_1_)
    {
        this.renderType = p_setBlockLayer_1_;
        this.renderBlocks = true;
    }

    private void initQuadSprites()
    {
        if (this.renderBlocks)
        {
            if (this.renderType != null)
            {
                if (this.quadSprites == null)
                {
                    if (this.isDrawing)
                    {
                        if (this.vertexCount > 0)
                        {
                            int i = this.drawMode;
                            VertexFormat vertexformat = this.vertexFormat;
                            RenderType rendertype = this.renderType;
                            boolean flag = this.renderBlocks;
                            this.renderType.finish(this, 0, 0, 0);
                            this.begin(i, vertexformat);
                            this.renderType = rendertype;
                            this.renderBlocks = flag;
                        }

                        this.quadSprites = this.quadSpritesPrev;

                        if (this.quadSprites == null || this.quadSprites.length < this.getBufferQuadSize())
                        {
                            this.quadSprites = new TextureAtlasSprite[this.getBufferQuadSize()];
                        }
                    }
                }
            }
        }
    }

    private int getBufferQuadSize()
    {
        return this.byteBuffer.capacity() / this.vertexFormat.getSize();
    }

    public RenderEnv getRenderEnv(BlockState p_getRenderEnv_1_, BlockPos p_getRenderEnv_2_)
    {
        if (this.renderEnv == null)
        {
            this.renderEnv = new RenderEnv(p_getRenderEnv_1_, p_getRenderEnv_2_);
            return this.renderEnv;
        }
        else
        {
            this.renderEnv.reset(p_getRenderEnv_1_, p_getRenderEnv_2_);
            return this.renderEnv;
        }
    }

    private static void quadsToTriangles(ByteBuffer p_quadsToTriangles_0_, VertexFormat p_quadsToTriangles_1_, int p_quadsToTriangles_2_, ByteBuffer p_quadsToTriangles_3_)
    {
        int i = p_quadsToTriangles_1_.getSize();
        int j = p_quadsToTriangles_0_.limit();
        ((Buffer)p_quadsToTriangles_0_).rewind();
        ((Buffer)p_quadsToTriangles_3_).clear();

        for (int k = 0; k < p_quadsToTriangles_2_; k += 4)
        {
            ((Buffer)p_quadsToTriangles_0_).limit((k + 3) * i);
            ((Buffer)p_quadsToTriangles_0_).position(k * i);
            p_quadsToTriangles_3_.put(p_quadsToTriangles_0_);
            ((Buffer)p_quadsToTriangles_0_).limit((k + 1) * i);
            ((Buffer)p_quadsToTriangles_0_).position(k * i);
            p_quadsToTriangles_3_.put(p_quadsToTriangles_0_);
            ((Buffer)p_quadsToTriangles_0_).limit((k + 2 + 2) * i);
            ((Buffer)p_quadsToTriangles_0_).position((k + 2) * i);
            p_quadsToTriangles_3_.put(p_quadsToTriangles_0_);
        }

        ((Buffer)p_quadsToTriangles_0_).limit(j);
        ((Buffer)p_quadsToTriangles_0_).rewind();
        ((Buffer)p_quadsToTriangles_3_).flip();
    }

    public int getDrawMode()
    {
        return this.drawMode;
    }

    public int getVertexCount()
    {
        return this.vertexCount;
    }

    public Vector3f getTempVec3f(Vector3f p_getTempVec3f_1_)
    {
        this.tempVec3f.set(p_getTempVec3f_1_.getX(), p_getTempVec3f_1_.getY(), p_getTempVec3f_1_.getZ());
        return this.tempVec3f;
    }

    public Vector3f getTempVec3f(float p_getTempVec3f_1_, float p_getTempVec3f_2_, float p_getTempVec3f_3_)
    {
        this.tempVec3f.set(p_getTempVec3f_1_, p_getTempVec3f_2_, p_getTempVec3f_3_);
        return this.tempVec3f;
    }

    public float[] getTempFloat4(float p_getTempFloat4_1_, float p_getTempFloat4_2_, float p_getTempFloat4_3_, float p_getTempFloat4_4_)
    {
        this.tempFloat4[0] = p_getTempFloat4_1_;
        this.tempFloat4[1] = p_getTempFloat4_2_;
        this.tempFloat4[2] = p_getTempFloat4_3_;
        this.tempFloat4[3] = p_getTempFloat4_4_;
        return this.tempFloat4;
    }

    public int[] getTempInt4(int p_getTempInt4_1_, int p_getTempInt4_2_, int p_getTempInt4_3_, int p_getTempInt4_4_)
    {
        this.tempInt4[0] = p_getTempInt4_1_;
        this.tempInt4[1] = p_getTempInt4_2_;
        this.tempInt4[2] = p_getTempInt4_3_;
        this.tempInt4[3] = p_getTempInt4_4_;
        return this.tempInt4;
    }

    public ByteBuffer getByteBuffer()
    {
        return this.byteBuffer;
    }

    public FloatBuffer getFloatBuffer()
    {
        return this.floatBuffer;
    }

    public IntBuffer getIntBuffer()
    {
        return this.intBuffer;
    }

    public int getBufferIntSize()
    {
        return this.vertexCount * this.vertexFormat.getIntegerSize();
    }

    private FloatBuffer getFloatBufferSort(int p_getFloatBufferSort_1_)
    {
        if (this.floatBufferSort == null || this.floatBufferSort.capacity() < p_getFloatBufferSort_1_)
        {
            this.floatBufferSort = GLAllocation.createDirectFloatBuffer(p_getFloatBufferSort_1_);
        }

        return this.floatBufferSort;
    }

    public IRenderTypeBuffer.Impl getRenderTypeBuffer()
    {
        return this.renderTypeBuffer;
    }

    public void setRenderTypeBuffer(IRenderTypeBuffer.Impl p_setRenderTypeBuffer_1_)
    {
        this.renderTypeBuffer = p_setRenderTypeBuffer_1_;
    }

    public void addVertexText(float p_addVertexText_1_, float p_addVertexText_2_, float p_addVertexText_3_, int p_addVertexText_4_, int p_addVertexText_5_, int p_addVertexText_6_, int p_addVertexText_7_, float p_addVertexText_8_, float p_addVertexText_9_, int p_addVertexText_10_, int p_addVertexText_11_)
    {
        if (this.vertexFormat.getSize() != DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP.getSize())
        {
            throw new IllegalStateException("Invalid text vertex format: " + this.vertexFormat);
        }
        else
        {
            this.putFloat(0, p_addVertexText_1_);
            this.putFloat(4, p_addVertexText_2_);
            this.putFloat(8, p_addVertexText_3_);
            this.putByte(12, (byte)p_addVertexText_4_);
            this.putByte(13, (byte)p_addVertexText_5_);
            this.putByte(14, (byte)p_addVertexText_6_);
            this.putByte(15, (byte)p_addVertexText_7_);
            this.putFloat(16, p_addVertexText_8_);
            this.putFloat(20, p_addVertexText_9_);
            this.putShort(24, (short)p_addVertexText_10_);
            this.putShort(26, (short)p_addVertexText_11_);
            this.nextElementBytes += this.vertexFormat.getSize();
            this.endVertex();
        }
    }

    public void setQuadVertexPositions(VertexPosition[] p_setQuadVertexPositions_1_)
    {
        this.quadVertexPositions = p_setQuadVertexPositions_1_;
    }

    public VertexPosition[] getQuadVertexPositions()
    {
        return this.quadVertexPositions;
    }

    public void setMidBlock(float p_setMidBlock_1_, float p_setMidBlock_2_, float p_setMidBlock_3_)
    {
        this.midBlock.set(p_setMidBlock_1_, p_setMidBlock_2_, p_setMidBlock_3_);
    }

    public Vector3f getMidBlock()
    {
        return this.midBlock;
    }

    public void putBulkData(ByteBuffer p_putBulkData_1_)
    {
        if (Config.isShaders())
        {
            SVertexBuilder.beginAddVertexData(this, p_putBulkData_1_);
        }

        this.growBuffer(p_putBulkData_1_.limit() + this.vertexFormat.getSize());
        ((Buffer)this.byteBuffer).position(this.vertexCount * this.vertexFormat.getSize());
        this.byteBuffer.put(p_putBulkData_1_);
        this.vertexCount += p_putBulkData_1_.limit() / this.vertexFormat.getSize();
        this.nextElementBytes += p_putBulkData_1_.limit();

        if (Config.isShaders())
        {
            SVertexBuilder.endAddVertexData(this);
        }
    }

    public VertexFormat getVertexFormat()
    {
        return this.vertexFormat;
    }

    public int getStartPosition()
    {
        return this.renderedBytes;
    }

    public int getIntStartPosition()
    {
        return this.renderedBytes / 4;
    }

    public static final class DrawState
    {
        private final VertexFormat format;
        private final int vertexCount;
        private final int drawMode;
        private MultiTextureData multiTextureData;

        private DrawState(VertexFormat p_i242109_1_, int p_i242109_2_, int p_i242109_3_, MultiTextureData p_i242109_4_)
        {
            this(p_i242109_1_, p_i242109_2_, p_i242109_3_);
            this.multiTextureData = p_i242109_4_;
        }

        public MultiTextureData getMultiTextureData()
        {
            return this.multiTextureData;
        }

        private DrawState(VertexFormat formatIn, int vertexCountIn, int drawModeIn)
        {
            this.format = formatIn;
            this.vertexCount = vertexCountIn;
            this.drawMode = drawModeIn;
        }

        public VertexFormat getFormat()
        {
            return this.format;
        }

        public int getVertexCount()
        {
            return this.vertexCount;
        }

        public int getDrawMode()
        {
            return this.drawMode;
        }
    }

    public static class State
    {
        private final ByteBuffer stateByteBuffer;
        private final VertexFormat stateVertexFormat;
        private TextureAtlasSprite[] stateQuadSprites;

        public State(ByteBuffer p_i242121_1_, VertexFormat p_i242121_2_, TextureAtlasSprite[] p_i242121_3_)
        {
            this(p_i242121_1_, p_i242121_2_);
            this.stateQuadSprites = p_i242121_3_;
        }

        private State(ByteBuffer byteBufferIn, VertexFormat vertexFormatIn)
        {
            this.stateByteBuffer = byteBufferIn;
            this.stateVertexFormat = vertexFormatIn;
        }
    }
}
