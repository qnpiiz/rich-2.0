package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public interface IParticleRenderType
{
    IParticleRenderType TERRAIN_SHEET = new IParticleRenderType()
    {
        public void beginRender(BufferBuilder bufferBuilder, TextureManager textureManager)
        {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            bufferBuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }
        public void finishRender(Tessellator tesselator)
        {
            tesselator.draw();
        }
        public String toString()
        {
            return "TERRAIN_SHEET";
        }
    };
    IParticleRenderType PARTICLE_SHEET_OPAQUE = new IParticleRenderType()
    {
        public void beginRender(BufferBuilder bufferBuilder, TextureManager textureManager)
        {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            textureManager.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
            bufferBuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }
        public void finishRender(Tessellator tesselator)
        {
            tesselator.draw();
        }
        public String toString()
        {
            return "PARTICLE_SHEET_OPAQUE";
        }
    };
    IParticleRenderType PARTICLE_SHEET_TRANSLUCENT = new IParticleRenderType()
    {
        public void beginRender(BufferBuilder bufferBuilder, TextureManager textureManager)
        {
            RenderSystem.depthMask(true);
            textureManager.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.alphaFunc(516, 0.003921569F);
            bufferBuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }
        public void finishRender(Tessellator tesselator)
        {
            tesselator.draw();
        }
        public String toString()
        {
            return "PARTICLE_SHEET_TRANSLUCENT";
        }
    };
    IParticleRenderType PARTICLE_SHEET_LIT = new IParticleRenderType()
    {
        public void beginRender(BufferBuilder bufferBuilder, TextureManager textureManager)
        {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            textureManager.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
            bufferBuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }
        public void finishRender(Tessellator tesselator)
        {
            tesselator.draw();
        }
        public String toString()
        {
            return "PARTICLE_SHEET_LIT";
        }
    };
    IParticleRenderType CUSTOM = new IParticleRenderType()
    {
        public void beginRender(BufferBuilder bufferBuilder, TextureManager textureManager)
        {
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
        }
        public void finishRender(Tessellator tesselator)
        {
        }
        public String toString()
        {
            return "CUSTOM";
        }
    };
    IParticleRenderType NO_RENDER = new IParticleRenderType()
    {
        public void beginRender(BufferBuilder bufferBuilder, TextureManager textureManager)
        {
        }
        public void finishRender(Tessellator tesselator)
        {
        }
        public String toString()
        {
            return "NO_RENDER";
        }
    };

    void beginRender(BufferBuilder bufferBuilder, TextureManager textureManager);

    void finishRender(Tessellator tesselator);
}
