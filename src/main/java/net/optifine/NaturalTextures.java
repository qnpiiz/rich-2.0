package net.optifine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.optifine.util.TextureUtils;

public class NaturalTextures
{
    private static NaturalProperties[] propertiesByIndex = new NaturalProperties[0];

    public static void update()
    {
        propertiesByIndex = new NaturalProperties[0];

        if (Config.isNaturalTextures())
        {
            String s = "optifine/natural.properties";

            try
            {
                ResourceLocation resourcelocation = new ResourceLocation(s);

                if (!Config.hasResource(resourcelocation))
                {
                    Config.dbg("NaturalTextures: configuration \"" + s + "\" not found");
                    return;
                }

                boolean flag = Config.isFromDefaultResourcePack(resourcelocation);
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                ArrayList arraylist = new ArrayList(256);
                String s1 = Config.readInputStream(inputstream);
                inputstream.close();
                String[] astring = Config.tokenize(s1, "\n\r");

                if (flag)
                {
                    Config.dbg("Natural Textures: Parsing default configuration \"" + s + "\"");
                    Config.dbg("Natural Textures: Valid only for textures from default resource pack");
                }
                else
                {
                    Config.dbg("Natural Textures: Parsing configuration \"" + s + "\"");
                }

                int i = 0;
                AtlasTexture atlastexture = TextureUtils.getTextureMapBlocks();

                for (int j = 0; j < astring.length; ++j)
                {
                    String s2 = astring[j].trim();

                    if (!s2.startsWith("#"))
                    {
                        String[] astring1 = Config.tokenize(s2, "=");

                        if (astring1.length != 2)
                        {
                            Config.warn("Natural Textures: Invalid \"" + s + "\" line: " + s2);
                        }
                        else
                        {
                            String s3 = astring1[0].trim();
                            String s4 = astring1[1].trim();
                            TextureAtlasSprite textureatlassprite = atlastexture.getUploadedSprite("minecraft:block/" + s3);

                            if (textureatlassprite == null)
                            {
                                Config.warn("Natural Textures: Texture not found: \"" + s + "\" line: " + s2);
                            }
                            else
                            {
                                int k = textureatlassprite.getIndexInMap();

                                if (k < 0)
                                {
                                    Config.warn("Natural Textures: Invalid \"" + s + "\" line: " + s2);
                                }
                                else
                                {
                                    if (flag && !Config.isFromDefaultResourcePack(new ResourceLocation("textures/block/" + s3 + ".png")))
                                    {
                                        return;
                                    }

                                    NaturalProperties naturalproperties = new NaturalProperties(s4);

                                    if (naturalproperties.isValid())
                                    {
                                        while (arraylist.size() <= k)
                                        {
                                            arraylist.add((Object)null);
                                        }

                                        arraylist.set(k, naturalproperties);
                                        ++i;
                                    }
                                }
                            }
                        }
                    }
                }

                propertiesByIndex = (NaturalProperties[]) arraylist.toArray(new NaturalProperties[arraylist.size()]);

                if (i > 0)
                {
                    Config.dbg("NaturalTextures: " + i);
                }
            }
            catch (FileNotFoundException filenotfoundexception)
            {
                Config.warn("NaturalTextures: configuration \"" + s + "\" not found");
                return;
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }

    public static BakedQuad getNaturalTexture(BlockPos blockPosIn, BakedQuad quad)
    {
        TextureAtlasSprite textureatlassprite = quad.getSprite();

        if (textureatlassprite == null)
        {
            return quad;
        }
        else
        {
            NaturalProperties naturalproperties = getNaturalProperties(textureatlassprite);

            if (naturalproperties == null)
            {
                return quad;
            }
            else
            {
                int i = ConnectedTextures.getSide(quad.getFace());
                int j = Config.getRandom(blockPosIn, i);
                int k = 0;
                boolean flag = false;

                if (naturalproperties.rotation > 1)
                {
                    k = j & 3;
                }

                if (naturalproperties.rotation == 2)
                {
                    k = k / 2 * 2;
                }

                if (naturalproperties.flip)
                {
                    flag = (j & 4) != 0;
                }

                return naturalproperties.getQuad(quad, k, flag);
            }
        }
    }

    public static NaturalProperties getNaturalProperties(TextureAtlasSprite icon)
    {
        if (!(icon instanceof TextureAtlasSprite))
        {
            return null;
        }
        else
        {
            int i = icon.getIndexInMap();
            return i >= 0 && i < propertiesByIndex.length ? propertiesByIndex[i] : null;
        }
    }
}
