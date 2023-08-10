package net.optifine.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.Dimension;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.math.vector.Vector4f;
import net.optifine.util.ArrayUtils;
import net.optifine.util.CompoundIntKey;
import net.optifine.util.CompoundKey;
import net.optifine.util.DynamicDimension;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class ShadersFramebuffer
{
    private String name;
    private int width;
    private int height;
    private int usedColorBuffers;
    private int usedDepthBuffers;
    private int maxDrawBuffers;
    private boolean[] depthFilterNearest;
    private boolean[] depthFilterHardware;
    private boolean[] colorFilterNearest;
    private DynamicDimension[] colorBufferSizes;
    private int[] buffersFormat;
    private int[] colorTextureUnits;
    private int[] depthTextureUnits;
    private int[] colorImageUnits;
    private int glFramebuffer;
    private FlipTextures colorTexturesFlip;
    private IntBuffer depthTextures;
    private final DrawBuffers drawBuffers;
    private DrawBuffers activeDrawBuffers;
    private int[] drawColorTextures;
    private int[] drawColorTexturesMap;
    private boolean[] dirtyColorTextures;
    private Map<CompoundKey, FixedFramebuffer> fixedFramebuffers = new HashMap<>();

    public ShadersFramebuffer(String name, int width, int height, int usedColorBuffers, int usedDepthBuffers, int maxDrawBuffers, boolean[] depthFilterNearest, boolean[] depthFilterHardware, boolean[] colorFilterNearest, DynamicDimension[] colorBufferSizes, int[] buffersFormat, int[] colorTextureUnits, int[] depthTextureUnits, int[] colorImageUnits, DrawBuffers drawBuffers)
    {
        this.name = name;
        this.width = width;
        this.height = height;
        this.usedColorBuffers = usedColorBuffers;
        this.usedDepthBuffers = usedDepthBuffers;
        this.maxDrawBuffers = maxDrawBuffers;
        this.depthFilterNearest = depthFilterNearest;
        this.depthFilterHardware = depthFilterHardware;
        this.colorFilterNearest = colorFilterNearest;
        this.colorBufferSizes = colorBufferSizes;
        this.buffersFormat = buffersFormat;
        this.colorTextureUnits = colorTextureUnits;
        this.depthTextureUnits = depthTextureUnits;
        this.colorImageUnits = colorImageUnits;
        this.drawBuffers = drawBuffers;
    }

    public void setup()
    {
        if (this.exists())
        {
            this.delete();
        }

        this.colorTexturesFlip = new FlipTextures(this.name + "ColorTexturesFlip", this.usedColorBuffers);
        this.depthTextures = BufferUtils.createIntBuffer(this.usedDepthBuffers);
        this.drawColorTextures = new int[this.usedColorBuffers];
        this.drawColorTexturesMap = new int[this.usedColorBuffers];
        this.dirtyColorTextures = new boolean[this.maxDrawBuffers];
        Arrays.fill(this.drawColorTextures, 0);
        Arrays.fill(this.drawColorTexturesMap, -1);
        Arrays.fill(this.dirtyColorTextures, false);

        for (int i = 0; i < this.drawBuffers.limit(); ++i)
        {
            this.drawBuffers.put(i, 36064 + i);
        }

        this.glFramebuffer = EXTFramebufferObject.glGenFramebuffersEXT();
        this.bindFramebuffer();
        GL30.glDrawBuffers(0);
        GL30.glReadBuffer(0);
        GL30.glGenTextures((IntBuffer)((Buffer)this.depthTextures).clear().limit(this.usedDepthBuffers));
        this.colorTexturesFlip.clear().limit(this.usedColorBuffers).genTextures();
        ((Buffer)this.depthTextures).position(0);
        this.colorTexturesFlip.position(0);

        for (int k = 0; k < this.usedDepthBuffers; ++k)
        {
            GlStateManager.bindTexture(this.depthTextures.get(k));
            GL30.glTexParameteri(3553, 10242, 33071);
            GL30.glTexParameteri(3553, 10243, 33071);
            int j = this.depthFilterNearest[k] ? 9728 : 9729;
            GL30.glTexParameteri(3553, 10241, j);
            GL30.glTexParameteri(3553, 10240, j);

            if (this.depthFilterHardware[k])
            {
                GL30.glTexParameteri(3553, 34892, 34894);
            }

            GL30.glTexImage2D(3553, 0, 6402, this.width, this.height, 0, 6402, 5126, (FloatBuffer)null);
        }

        this.setFramebufferTexture2D(36160, 36096, 3553, this.depthTextures.get(0), 0);
        Shaders.checkGLError("FBS " + this.name + " depth");

        for (int l = 0; l < this.usedColorBuffers; ++l)
        {
            GlStateManager.bindTexture(this.colorTexturesFlip.getA(l));
            GL30.glTexParameteri(3553, 10242, 33071);
            GL30.glTexParameteri(3553, 10243, 33071);
            int k1 = this.colorFilterNearest[l] ? 9728 : 9729;
            GL30.glTexParameteri(3553, 10241, k1);
            GL30.glTexParameteri(3553, 10240, k1);
            Dimension dimension = this.colorBufferSizes[l] != null ? this.colorBufferSizes[l].getDimension(this.width, this.height) : new Dimension(this.width, this.height);
            GL30.glTexImage2D(3553, 0, this.buffersFormat[l], dimension.width, dimension.height, 0, Shaders.getPixelFormat(this.buffersFormat[l]), 33639, (ByteBuffer)null);
            this.setFramebufferTexture2D(36160, 36064 + l, 3553, this.colorTexturesFlip.getA(l), 0);
            Shaders.checkGLError("FBS " + this.name + " colorA");
        }

        for (int i1 = 0; i1 < this.usedColorBuffers; ++i1)
        {
            GlStateManager.bindTexture(this.colorTexturesFlip.getB(i1));
            GL30.glTexParameteri(3553, 10242, 33071);
            GL30.glTexParameteri(3553, 10243, 33071);
            int l1 = this.colorFilterNearest[i1] ? 9728 : 9729;
            GL30.glTexParameteri(3553, 10241, l1);
            GL30.glTexParameteri(3553, 10240, l1);
            Dimension dimension1 = this.colorBufferSizes[i1] != null ? this.colorBufferSizes[i1].getDimension(this.width, this.height) : new Dimension(this.width, this.height);
            GL30.glTexImage2D(3553, 0, this.buffersFormat[i1], dimension1.width, dimension1.height, 0, Shaders.getPixelFormat(this.buffersFormat[i1]), 33639, (ByteBuffer)null);
            Shaders.checkGLError("FBS " + this.name + " colorB");
        }

        GlStateManager.bindTexture(0);

        if (this.usedColorBuffers > 0)
        {
            this.setDrawBuffers(this.drawBuffers);
            GL30.glReadBuffer(0);
        }

        int j1 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);

        if (j1 != 36053)
        {
            Shaders.printChatAndLogError("[Shaders] Error creating framebuffer: " + this.name + ", status: " + j1);
        }
        else
        {
            SMCLog.info("Framebuffer created: " + this.name);
        }
    }

    public void delete()
    {
        if (this.glFramebuffer != 0)
        {
            EXTFramebufferObject.glDeleteFramebuffersEXT(this.glFramebuffer);
            this.glFramebuffer = 0;
        }

        if (this.colorTexturesFlip != null)
        {
            this.colorTexturesFlip.deleteTextures();
            this.colorTexturesFlip = null;
        }

        if (this.depthTextures != null)
        {
            GlStateManager.deleteTextures(this.depthTextures);
            this.depthTextures = null;
        }

        this.drawBuffers.position(0).fill(0);

        for (FixedFramebuffer fixedframebuffer : this.fixedFramebuffers.values())
        {
            fixedframebuffer.delete();
        }

        this.fixedFramebuffers.clear();
    }

    public String getName()
    {
        return this.name;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public int getGlFramebuffer()
    {
        return this.glFramebuffer;
    }

    public boolean exists()
    {
        return this.glFramebuffer != 0;
    }

    public void bindFramebuffer()
    {
        GlState.bindFramebuffer(this);
    }

    public void setColorTextures(boolean main)
    {
        for (int i = 0; i < this.usedColorBuffers; ++i)
        {
            this.setFramebufferTexture2D(36160, 36064 + i, 3553, this.colorTexturesFlip.get(main, i), 0);
        }
    }

    public void setDepthTexture()
    {
        this.setFramebufferTexture2D(36160, 36096, 3553, this.depthTextures.get(0), 0);
    }

    public void setColorBuffersFiltering(int minFilter, int magFilter)
    {
        GlStateManager.activeTexture(33984);

        for (int i = 0; i < this.usedColorBuffers; ++i)
        {
            GlStateManager.bindTexture(this.colorTexturesFlip.getA(i));
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
            GlStateManager.bindTexture(this.colorTexturesFlip.getB(i));
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
        }

        GlStateManager.bindTexture(0);
    }

    public void setFramebufferTexture2D(int target, int attachment, int texTarget, int texture, int level)
    {
        int i = attachment - 36064;

        if (this.isColorBufferIndex(i))
        {
            if (this.colorBufferSizes[i] != null)
            {
                if (this.isColorExtendedIndex(i))
                {
                    return;
                }

                texture = 0;
            }

            this.drawColorTextures[i] = texture;

            if (i >= this.maxDrawBuffers)
            {
                int j = this.drawColorTexturesMap[i];

                if (!this.isDrawBufferIndex(j))
                {
                    return;
                }

                attachment = 36064 + j;
            }
        }

        this.bindFramebuffer();
        EXTFramebufferObject.glFramebufferTexture2DEXT(target, attachment, texTarget, texture, level);
    }

    public boolean isColorBufferIndex(int index)
    {
        return index >= 0 && index < this.usedColorBuffers;
    }

    public boolean isColorExtendedIndex(int index)
    {
        return index >= this.maxDrawBuffers && index < this.usedColorBuffers;
    }

    public boolean isDrawBufferIndex(int index)
    {
        return index >= 0 && index < this.maxDrawBuffers;
    }

    private void setDrawColorTexturesMap(int[] newColorTexturesMap)
    {
        this.bindFramebuffer();

        for (int i = 0; i < this.maxDrawBuffers; ++i)
        {
            if (this.dirtyColorTextures[i])
            {
                int j = this.drawColorTextures[i];
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, j, 0);
                this.dirtyColorTextures[i] = false;
            }
        }

        this.drawColorTexturesMap = newColorTexturesMap;

        for (int l = this.maxDrawBuffers; l < this.drawColorTexturesMap.length; ++l)
        {
            int i1 = this.drawColorTexturesMap[l];

            if (i1 >= 0)
            {
                int k = this.drawColorTextures[l];
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i1, 3553, k, 0);
                this.dirtyColorTextures[i1] = true;
            }
        }
    }

    public void setDrawBuffers(DrawBuffers drawBuffersIn)
    {
        if (drawBuffersIn == null)
        {
            drawBuffersIn = Shaders.drawBuffersNone;
        }

        this.setDrawColorTexturesMap(drawBuffersIn.getAttachmentMappings());
        this.activeDrawBuffers = drawBuffersIn;
        this.bindFramebuffer();
        GL30.glDrawBuffers(drawBuffersIn.getGlDrawBuffers());
        Shaders.checkGLError("setDrawBuffers");
    }

    public void setDrawBuffers()
    {
        this.setDrawBuffers(this.drawBuffers);
    }

    public DrawBuffers getDrawBuffers()
    {
        return this.activeDrawBuffers;
    }

    public void bindDepthTextures(int[] depthTextureImageUnits)
    {
        for (int i = 0; i < this.usedDepthBuffers; ++i)
        {
            GlStateManager.activeTexture(33984 + depthTextureImageUnits[i]);
            GlStateManager.bindTexture(this.depthTextures.get(i));
        }

        GlStateManager.activeTexture(33984);
    }

    public void bindColorTextures(int startColorBuffer)
    {
        for (int i = startColorBuffer; i < this.usedColorBuffers; ++i)
        {
            GlStateManager.activeTexture(33984 + this.colorTextureUnits[i]);
            GlStateManager.bindTexture(this.colorTexturesFlip.getA(i));
            this.bindColorImage(i, true);
        }
    }

    public void bindColorImages(boolean main)
    {
        if (this.colorImageUnits != null)
        {
            for (int i = 0; i < this.usedColorBuffers; ++i)
            {
                this.bindColorImage(i, main);
            }
        }
    }

    public void bindColorImage(int index, boolean main)
    {
        if (this.colorImageUnits != null)
        {
            if (index >= 0 && index < this.colorImageUnits.length)
            {
                int i = Shaders.getImageFormat(this.buffersFormat[index]);
                GlStateManager.bindImageTexture(this.colorImageUnits[index], this.colorTexturesFlip.get(main, index), 0, false, 0, 35002, i);
            }

            GlStateManager.activeTexture(33984);
        }
    }

    public void generateDepthMipmaps(boolean[] depthMipmapEnabled)
    {
        for (int i = 0; i < this.usedDepthBuffers; ++i)
        {
            if (depthMipmapEnabled[i])
            {
                GlStateManager.activeTexture(33984 + this.depthTextureUnits[i]);
                GlStateManager.bindTexture(this.depthTextures.get(i));
                GL30.glGenerateMipmap(3553);
                GL30.glTexParameteri(3553, 10241, this.depthFilterNearest[i] ? 9984 : 9987);
            }
        }

        GlStateManager.activeTexture(33984);
    }

    public void generateColorMipmaps(boolean main, boolean[] colorMipmapEnabled)
    {
        for (int i = 0; i < this.usedColorBuffers; ++i)
        {
            if (colorMipmapEnabled[i])
            {
                GlStateManager.activeTexture(33984 + this.colorTextureUnits[i]);
                GlStateManager.bindTexture(this.colorTexturesFlip.get(main, i));
                GL30.glGenerateMipmap(3553);
                GL30.glTexParameteri(3553, 10241, this.colorFilterNearest[i] ? 9984 : 9987);
            }
        }

        GlStateManager.activeTexture(33984);
    }

    public void genCompositeMipmap(int compositeMipmapSetting)
    {
        if (Shaders.hasGlGenMipmap)
        {
            for (int i = 0; i < this.usedColorBuffers; ++i)
            {
                if ((compositeMipmapSetting & 1 << i) != 0)
                {
                    GlStateManager.activeTexture(33984 + this.colorTextureUnits[i]);
                    GL30.glTexParameteri(3553, 10241, 9987);
                    GL30.glGenerateMipmap(3553);
                }
            }

            GlStateManager.activeTexture(33984);
        }
    }

    public void flipColorTextures(boolean[] toggleColorTextures)
    {
        for (int i = 0; i < this.colorTexturesFlip.limit(); ++i)
        {
            if (toggleColorTextures[i])
            {
                this.flipColorTexture(i);
            }
        }
    }

    public void flipColorTexture(int index)
    {
        this.colorTexturesFlip.flip(index);
        GlStateManager.activeTexture(33984 + this.colorTextureUnits[index]);
        GlStateManager.bindTexture(this.colorTexturesFlip.getA(index));
        this.bindColorImage(index, true);
        this.setFramebufferTexture2D(36160, 36064 + index, 3553, this.colorTexturesFlip.getB(index), 0);
        GlStateManager.activeTexture(33984);
    }

    public void clearColorBuffers(boolean[] buffersClear, Vector4f[] clearColors)
    {
        for (int i = 0; i < this.usedColorBuffers; ++i)
        {
            if (buffersClear[i])
            {
                Vector4f vector4f = clearColors[i];

                if (vector4f != null)
                {
                    GL30.glClearColor(vector4f.getX(), vector4f.getY(), vector4f.getZ(), vector4f.getW());
                }

                if (this.colorBufferSizes[i] != null)
                {
                    if (this.colorTexturesFlip.isChanged(i))
                    {
                        this.clearColorBufferFixedSize(i, false);
                    }

                    this.clearColorBufferFixedSize(i, true);
                }
                else
                {
                    if (this.colorTexturesFlip.isChanged(i))
                    {
                        this.setFramebufferTexture2D(36160, 36064 + i, 3553, this.colorTexturesFlip.getB(i), 0);
                        this.setDrawBuffers(Shaders.drawBuffersColorAtt[i]);
                        GL30.glClear(16384);
                        this.setFramebufferTexture2D(36160, 36064 + i, 3553, this.colorTexturesFlip.getA(i), 0);
                    }

                    this.setDrawBuffers(Shaders.drawBuffersColorAtt[i]);
                    GL30.glClear(16384);
                }
            }
        }
    }

    private void clearColorBufferFixedSize(int i, boolean main)
    {
        Dimension dimension = this.colorBufferSizes[i].getDimension(this.width, this.height);

        if (dimension != null)
        {
            FixedFramebuffer fixedframebuffer = this.getFixedFramebuffer(dimension.width, dimension.height, Shaders.drawBuffersColorAtt[i], main);
            fixedframebuffer.bindFramebuffer();
            GL30.glClear(16384);
        }
    }

    public void clearDepthBuffer(Vector4f col)
    {
        this.setFramebufferTexture2D(36160, 36096, 3553, this.depthTextures.get(0), 0);
        GL30.glClearColor(col.getX(), col.getY(), col.getZ(), col.getW());
        GL30.glClear(256);
    }

    public String toString()
    {
        return this.name + ", width: " + this.width + ", height: " + this.height + ", usedColorBuffers: " + this.usedColorBuffers + ", usedDepthBuffers: " + this.usedDepthBuffers + ", maxDrawBuffers: " + this.maxDrawBuffers;
    }

    public FixedFramebuffer getFixedFramebuffer(int width, int height, DrawBuffers dbs, boolean main)
    {
        IntBuffer intbuffer = dbs.getGlDrawBuffers();
        int i = dbs.limit();
        int[] aint = new int[i];
        int[] aint1 = new int[i];

        for (int j = 0; j < aint.length; ++j)
        {
            int k = dbs.get(j);
            int l = k - 36064;

            if (this.isColorBufferIndex(l))
            {
                aint[j] = this.colorTexturesFlip.get(main, l);
                aint1[j] = intbuffer.get(j);
            }
        }

        CompoundKey compoundkey = new CompoundKey(new CompoundIntKey(aint), new CompoundIntKey(aint1));
        FixedFramebuffer fixedframebuffer = this.fixedFramebuffers.get(compoundkey);

        if (fixedframebuffer == null)
        {
            String s = this.name + ", [" + ArrayUtils.arrayToString(aint) + "], [" + ArrayUtils.arrayToString(aint1) + "]";
            fixedframebuffer = new FixedFramebuffer(s, width, height, aint, aint1, this.depthFilterNearest[0], this.depthFilterHardware[0]);
            fixedframebuffer.setup();
            this.fixedFramebuffers.put(compoundkey, fixedframebuffer);
        }

        return fixedframebuffer;
    }
}
