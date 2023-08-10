package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.shaders.MultiTexID;
import net.optifine.shaders.ShadersTex;

public abstract class Texture implements AutoCloseable
{
    protected int glTextureId = -1;
    protected boolean blur;
    protected boolean mipmap;
    public MultiTexID multiTex;
    private boolean blurMipmapSet;
    private boolean lastBlur;
    private boolean lastMipmap;

    public void setBlurMipmapDirect(boolean blurIn, boolean mipmapIn)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        if (!this.blurMipmapSet || this.blur != blurIn || this.mipmap != mipmapIn)
        {
            this.blurMipmapSet = true;
            this.blur = blurIn;
            this.mipmap = mipmapIn;
            int i;
            int j;

            if (blurIn)
            {
                i = mipmapIn ? 9987 : 9729;
                j = 9729;
            }
            else
            {
                int k = Config.getMipmapType();
                i = mipmapIn ? k : 9728;
                j = 9728;
            }

            GlStateManager.bindTexture(this.getGlTextureId());
            GlStateManager.texParameter(3553, 10241, i);
            GlStateManager.texParameter(3553, 10240, j);
        }
    }

    public int getGlTextureId()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        if (this.glTextureId == -1)
        {
            this.glTextureId = TextureUtil.generateTextureId();
        }

        return this.glTextureId;
    }

    public void deleteGlTexture()
    {
        if (!RenderSystem.isOnRenderThread())
        {
            RenderSystem.recordRenderCall(() ->
            {
                ShadersTex.deleteTextures(this, this.glTextureId);
                this.blurMipmapSet = false;

                if (this.glTextureId != -1)
                {
                    TextureUtil.releaseTextureId(this.glTextureId);
                    this.glTextureId = -1;
                }
            });
        }
        else if (this.glTextureId != -1)
        {
            ShadersTex.deleteTextures(this, this.glTextureId);
            this.blurMipmapSet = false;
            TextureUtil.releaseTextureId(this.glTextureId);
            this.glTextureId = -1;
        }
    }

    public abstract void loadTexture(IResourceManager manager) throws IOException;

    public void bindTexture()
    {
        if (!RenderSystem.isOnRenderThreadOrInit())
        {
            RenderSystem.recordRenderCall(() ->
            {
                GlStateManager.bindTexture(this.getGlTextureId());
            });
        }
        else
        {
            GlStateManager.bindTexture(this.getGlTextureId());
        }
    }

    public void loadTexture(TextureManager textureManagerIn, IResourceManager resourceManagerIn, ResourceLocation resourceLocationIn, Executor executorIn)
    {
        textureManagerIn.loadTexture(resourceLocationIn, this);
    }

    public void close()
    {
    }

    public MultiTexID getMultiTexID()
    {
        return ShadersTex.getMultiTexID(this);
    }

    public void setBlurMipmap(boolean p_setBlurMipmap_1_, boolean p_setBlurMipmap_2_)
    {
        this.lastBlur = this.blur;
        this.lastMipmap = this.mipmap;
        this.setBlurMipmapDirect(p_setBlurMipmap_1_, p_setBlurMipmap_2_);
    }

    public void restoreLastBlurMipmap()
    {
        this.setBlurMipmapDirect(this.lastBlur, this.lastMipmap);
    }
}
