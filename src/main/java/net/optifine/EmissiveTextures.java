package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.optifine.render.RenderUtils;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.TextureUtils;

public class EmissiveTextures
{
    private static String suffixEmissive = null;
    private static String suffixEmissivePng = null;
    private static boolean active = false;
    private static boolean render = false;
    private static boolean hasEmissive = false;
    private static boolean renderEmissive = false;
    private static final String SUFFIX_PNG = ".png";
    private static final ResourceLocation LOCATION_TEXTURE_EMPTY = TextureUtils.LOCATION_TEXTURE_EMPTY;
    private static final ResourceLocation LOCATION_SPRITE_EMPTY = TextureUtils.LOCATION_SPRITE_EMPTY;
    private static TextureManager textureManager;
    private static int countRecursive = 0;

    public static boolean isActive()
    {
        return active;
    }

    public static String getSuffixEmissive()
    {
        return suffixEmissive;
    }

    public static void beginRender()
    {
        if (render)
        {
            ++countRecursive;
        }
        else
        {
            render = true;
            hasEmissive = false;
        }
    }

    public static ResourceLocation getEmissiveTexture(ResourceLocation locationIn)
    {
        if (!render)
        {
            return locationIn;
        }
        else
        {
            Texture texture = textureManager.getTexture(locationIn);

            if (texture instanceof AtlasTexture)
            {
                return locationIn;
            }
            else
            {
                ResourceLocation resourcelocation = null;

                if (texture instanceof SimpleTexture)
                {
                    resourcelocation = ((SimpleTexture)texture).locationEmissive;
                }

                if (!renderEmissive)
                {
                    if (resourcelocation != null)
                    {
                        hasEmissive = true;
                    }

                    return locationIn;
                }
                else
                {
                    if (resourcelocation == null)
                    {
                        resourcelocation = LOCATION_TEXTURE_EMPTY;
                    }

                    return resourcelocation;
                }
            }
        }
    }

    public static TextureAtlasSprite getEmissiveSprite(TextureAtlasSprite sprite)
    {
        if (!render)
        {
            return sprite;
        }
        else
        {
            TextureAtlasSprite textureatlassprite = sprite.spriteEmissive;

            if (!renderEmissive)
            {
                if (textureatlassprite != null)
                {
                    hasEmissive = true;
                }

                return sprite;
            }
            else
            {
                if (textureatlassprite == null)
                {
                    textureatlassprite = sprite.getAtlasTexture().getSprite(LOCATION_SPRITE_EMPTY);
                }

                return textureatlassprite;
            }
        }
    }

    public static BakedQuad getEmissiveQuad(BakedQuad quad)
    {
        if (!render)
        {
            return quad;
        }
        else
        {
            BakedQuad bakedquad = quad.getQuadEmissive();

            if (!renderEmissive)
            {
                if (bakedquad != null)
                {
                    hasEmissive = true;
                }

                return quad;
            }
            else
            {
                return bakedquad;
            }
        }
    }

    public static boolean hasEmissive()
    {
        return countRecursive > 0 ? false : hasEmissive;
    }

    public static void beginRenderEmissive()
    {
        renderEmissive = true;
    }

    public static boolean isRenderEmissive()
    {
        return renderEmissive;
    }

    public static void endRenderEmissive()
    {
        RenderUtils.flushRenderBuffers();
        renderEmissive = false;
    }

    public static void endRender()
    {
        if (countRecursive > 0)
        {
            --countRecursive;
        }
        else
        {
            render = false;
            hasEmissive = false;
        }
    }

    public static void update()
    {
        textureManager = Minecraft.getInstance().getTextureManager();
        active = false;
        suffixEmissive = null;
        suffixEmissivePng = null;

        if (Config.isEmissiveTextures())
        {
            try
            {
                String s = "optifine/emissive.properties";
                ResourceLocation resourcelocation = new ResourceLocation(s);
                InputStream inputstream = Config.getResourceStream(resourcelocation);

                if (inputstream == null)
                {
                    return;
                }

                dbg("Loading " + s);
                Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                suffixEmissive = properties.getProperty("suffix.emissive");

                if (suffixEmissive != null)
                {
                    suffixEmissivePng = suffixEmissive + ".png";
                }

                active = suffixEmissive != null;
            }
            catch (FileNotFoundException filenotfoundexception)
            {
                return;
            }
            catch (IOException ioexception)
            {
                ioexception.printStackTrace();
            }
        }
    }

    public static void updateIcons(AtlasTexture textureMap, Set<ResourceLocation> locations)
    {
        if (active)
        {
            for (ResourceLocation resourcelocation : locations)
            {
                checkEmissive(textureMap, resourcelocation);
            }
        }
    }

    private static void checkEmissive(AtlasTexture textureMap, ResourceLocation locSprite)
    {
        String s = getSuffixEmissive();

        if (s != null)
        {
            if (!locSprite.getPath().endsWith(s))
            {
                ResourceLocation resourcelocation = new ResourceLocation(locSprite.getNamespace(), locSprite.getPath() + s);
                ResourceLocation resourcelocation1 = textureMap.getSpritePath(resourcelocation);

                if (Config.hasResource(resourcelocation1))
                {
                    TextureAtlasSprite textureatlassprite = textureMap.registerSprite(locSprite);
                    TextureAtlasSprite textureatlassprite1 = textureMap.registerSprite(resourcelocation);
                    textureatlassprite1.isSpriteEmissive = true;
                    textureatlassprite.spriteEmissive = textureatlassprite1;
                    textureMap.registerSprite(LOCATION_SPRITE_EMPTY);
                }
            }
        }
    }

    public static void refreshIcons(AtlasTexture textureMap)
    {
        for (TextureAtlasSprite textureatlassprite : textureMap.getRegisteredSprites())
        {
            refreshIcon(textureatlassprite, textureMap);
        }
    }

    private static void refreshIcon(TextureAtlasSprite sprite, AtlasTexture textureMap)
    {
        if (sprite.spriteEmissive != null)
        {
            TextureAtlasSprite textureatlassprite = textureMap.getUploadedSprite(sprite.getName());

            if (textureatlassprite != null)
            {
                TextureAtlasSprite textureatlassprite1 = textureMap.getUploadedSprite(sprite.spriteEmissive.getName());

                if (textureatlassprite1 != null)
                {
                    textureatlassprite1.isSpriteEmissive = true;
                    textureatlassprite.spriteEmissive = textureatlassprite1;
                }
            }
        }
    }

    private static void dbg(String str)
    {
        Config.dbg("EmissiveTextures: " + str);
    }

    private static void warn(String str)
    {
        Config.warn("EmissiveTextures: " + str);
    }

    public static boolean isEmissive(ResourceLocation loc)
    {
        return suffixEmissivePng == null ? false : loc.getPath().endsWith(suffixEmissivePng);
    }

    public static void loadTexture(ResourceLocation loc, SimpleTexture tex)
    {
        if (loc != null && tex != null)
        {
            tex.isEmissive = false;
            tex.locationEmissive = null;

            if (suffixEmissivePng != null)
            {
                String s = loc.getPath();

                if (s.endsWith(".png"))
                {
                    if (s.endsWith(suffixEmissivePng))
                    {
                        tex.isEmissive = true;
                    }
                    else
                    {
                        String s1 = s.substring(0, s.length() - ".png".length()) + suffixEmissivePng;
                        ResourceLocation resourcelocation = new ResourceLocation(loc.getNamespace(), s1);

                        if (Config.hasResource(resourcelocation))
                        {
                            tex.locationEmissive = resourcelocation;
                        }
                    }
                }
            }
        }
    }
}
