package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;

public final class MissingTextureSprite extends TextureAtlasSprite
{
    private static final ResourceLocation LOCATION = new ResourceLocation("missingno");
    @Nullable
    private static DynamicTexture dynamicTexture;
    private static final LazyValue<NativeImage> IMAGE = new LazyValue<>(() ->
    {
        NativeImage nativeimage = new NativeImage(16, 16, false);
        int i = -16777216;
        int j = -524040;

        for (int k = 0; k < 16; ++k)
        {
            for (int l = 0; l < 16; ++l)
            {
                if (k < 8 ^ l < 8)
                {
                    nativeimage.setPixelRGBA(l, k, -524040);
                }
                else
                {
                    nativeimage.setPixelRGBA(l, k, -16777216);
                }
            }
        }

        nativeimage.untrack();
        return nativeimage;
    });
    private static final TextureAtlasSprite.Info spriteInfo = new TextureAtlasSprite.Info(LOCATION, 16, 16, new AnimationMetadataSection(Lists.newArrayList(new AnimationFrame(0, -1)), 16, 16, 1, false));

    public MissingTextureSprite(AtlasTexture p_i242120_1_, TextureAtlasSprite.Info p_i242120_2_, int p_i242120_3_, int p_i242120_4_, int p_i242120_5_, int p_i242120_6_, int p_i242120_7_)
    {
        super(p_i242120_1_, p_i242120_2_, p_i242120_3_, p_i242120_4_, p_i242120_5_, p_i242120_6_, p_i242120_7_, makeMissingImage(p_i242120_2_.getSpriteWidth(), p_i242120_2_.getSpriteHeight()));
    }

    private MissingTextureSprite(AtlasTexture atlasTextureIn, int mipmapLevelIn, int atlasWidthIn, int atlasHeightIn, int xIn, int yIn)
    {
        super(atlasTextureIn, spriteInfo, mipmapLevelIn, atlasWidthIn, atlasHeightIn, xIn, yIn, IMAGE.getValue());
    }

    public static MissingTextureSprite create(AtlasTexture atlasTextureIn, int mipmapLevelIn, int atlasWidthIn, int atlasHeightIn, int xIn, int yIn)
    {
        return new MissingTextureSprite(atlasTextureIn, mipmapLevelIn, atlasWidthIn, atlasHeightIn, xIn, yIn);
    }

    public static ResourceLocation getLocation()
    {
        return LOCATION;
    }

    public static TextureAtlasSprite.Info getSpriteInfo()
    {
        return spriteInfo;
    }

    public void close()
    {
        super.close();
    }

    public static DynamicTexture getDynamicTexture()
    {
        if (dynamicTexture == null)
        {
            dynamicTexture = new DynamicTexture(IMAGE.getValue());
            Minecraft.getInstance().getTextureManager().loadTexture(LOCATION, dynamicTexture);
        }

        return dynamicTexture;
    }

    private static NativeImage makeMissingImage(int p_makeMissingImage_0_, int p_makeMissingImage_1_)
    {
        int i = p_makeMissingImage_0_ / 2;
        int j = p_makeMissingImage_1_ / 2;
        NativeImage nativeimage = new NativeImage(p_makeMissingImage_0_, p_makeMissingImage_1_, false);
        int k = -16777216;
        int l = -524040;

        for (int i1 = 0; i1 < p_makeMissingImage_1_; ++i1)
        {
            for (int j1 = 0; j1 < p_makeMissingImage_0_; ++j1)
            {
                if (i1 < j ^ j1 < i)
                {
                    nativeimage.setPixelRGBA(j1, i1, l);
                }
                else
                {
                    nativeimage.setPixelRGBA(j1, i1, k);
                }
            }
        }

        return nativeimage;
    }
}
