package net.optifine.render;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.Buffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.MathHelper;
import net.optifine.Config;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersTex;

public class MultiTextureRenderer
{
    private static IntBuffer bufferPositions = Config.createDirectIntBuffer(1024);
    private static IntBuffer bufferCounts = Config.createDirectIntBuffer(1024);
    private static boolean shaders;

    public static void draw(int drawMode, MultiTextureData multiTextureData)
    {
        shaders = Config.isShaders();
        SpriteRenderData[] aspriterenderdata = multiTextureData.getSpriteRenderDatas();

        for (int i = 0; i < aspriterenderdata.length; ++i)
        {
            SpriteRenderData spriterenderdata = aspriterenderdata[i];
            draw(drawMode, spriterenderdata);
        }
    }

    private static void draw(int drawMode, SpriteRenderData srd)
    {
        TextureAtlasSprite textureatlassprite = srd.getSprite();
        int[] aint = srd.getPositions();
        int[] aint1 = srd.getCounts();
        GlStateManager.bindTexture(textureatlassprite.glSpriteTextureId);

        if (shaders)
        {
            int i = textureatlassprite.spriteNormal != null ? textureatlassprite.spriteNormal.glSpriteTextureId : 0;
            int j = textureatlassprite.spriteSpecular != null ? textureatlassprite.spriteSpecular.glSpriteTextureId : 0;
            AtlasTexture atlastexture = textureatlassprite.getAtlasTexture();
            ShadersTex.bindNSTextures(i, j, atlastexture.isNormalBlend(), atlastexture.isSpecularBlend(), atlastexture.isMipmaps());

            if (Shaders.uniform_spriteBounds.isDefined())
            {
                Shaders.uniform_spriteBounds.setValue(textureatlassprite.getMinU(), textureatlassprite.getMinV(), textureatlassprite.getMaxU(), textureatlassprite.getMaxV());
            }
        }

        if (bufferPositions.capacity() < aint.length)
        {
            int k = MathHelper.smallestEncompassingPowerOfTwo(aint.length);
            bufferPositions = Config.createDirectIntBuffer(k);
            bufferCounts = Config.createDirectIntBuffer(k);
        }

        ((Buffer)bufferPositions).clear();
        ((Buffer)bufferCounts).clear();
        bufferPositions.put(aint);
        bufferCounts.put(aint1);
        ((Buffer)bufferPositions).flip();
        ((Buffer)bufferCounts).flip();
        GlStateManager.glMultiDrawArrays(drawMode, bufferPositions, bufferCounts);
    }
}
