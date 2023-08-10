package net.optifine;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.render.Blender;
import net.optifine.shaders.RenderStage;
import net.optifine.shaders.Shaders;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;
import net.optifine.util.WorldUtils;

public class CustomSky
{
    private static CustomSkyLayer[][] worldSkyLayers = (CustomSkyLayer[][])null;

    public static void reset()
    {
        worldSkyLayers = (CustomSkyLayer[][])null;
    }

    public static void update()
    {
        reset();

        if (Config.isCustomSky())
        {
            worldSkyLayers = readCustomSkies();
        }
    }

    private static CustomSkyLayer[][] readCustomSkies()
    {
        CustomSkyLayer[][] acustomskylayer = new CustomSkyLayer[10][0];
        String s = "optifine/sky/world";
        int i = -1;

        for (int j = 0; j < acustomskylayer.length; ++j)
        {
            String s1 = s + j;
            List list = new ArrayList();

            for (int k = 0; k < 1000; ++k)
            {
                String s2 = s1 + "/sky" + k + ".properties";
                int l = 0;

                try
                {
                    ResourceLocation resourcelocation = new ResourceLocation(s2);
                    InputStream inputstream = Config.getResourceStream(resourcelocation);

                    if (inputstream == null)
                    {
                        ++l;

                        if (l > 10)
                        {
                            break;
                        }
                    }

                    Properties properties = new PropertiesOrdered();
                    properties.load(inputstream);
                    inputstream.close();
                    Config.dbg("CustomSky properties: " + s2);
                    String s3 = k + ".png";
                    CustomSkyLayer customskylayer = new CustomSkyLayer(properties, s3);

                    if (customskylayer.isValid(s2))
                    {
                        String s4 = StrUtils.addSuffixCheck(customskylayer.source, ".png");
                        ResourceLocation resourcelocation1 = new ResourceLocation(s4);
                        Texture texture = TextureUtils.getTexture(resourcelocation1);

                        if (texture == null)
                        {
                            Config.log("CustomSky: Texture not found: " + resourcelocation1);
                        }
                        else
                        {
                            customskylayer.textureId = texture.getGlTextureId();
                            list.add(customskylayer);
                            inputstream.close();
                        }
                    }
                }
                catch (FileNotFoundException filenotfoundexception)
                {
                    ++l;

                    if (l > 10)
                    {
                        break;
                    }
                }
                catch (IOException ioexception)
                {
                    ioexception.printStackTrace();
                }
            }

            if (list.size() > 0)
            {
                CustomSkyLayer[] acustomskylayer2 = (CustomSkyLayer[]) list.toArray(new CustomSkyLayer[list.size()]);
                acustomskylayer[j] = acustomskylayer2;
                i = j;
            }
        }

        if (i < 0)
        {
            return (CustomSkyLayer[][])null;
        }
        else
        {
            int i1 = i + 1;
            CustomSkyLayer[][] acustomskylayer1 = new CustomSkyLayer[i1][0];

            for (int j1 = 0; j1 < acustomskylayer1.length; ++j1)
            {
                acustomskylayer1[j1] = acustomskylayer[j1];
            }

            return acustomskylayer1;
        }
    }

    public static void renderSky(World world, TextureManager re, MatrixStack matrixStackIn, float partialTicks)
    {
        if (worldSkyLayers != null)
        {
            if (Config.isShaders())
            {
                Shaders.setRenderStage(RenderStage.CUSTOM_SKY);
            }

            int i = WorldUtils.getDimensionId(world);

            if (i >= 0 && i < worldSkyLayers.length)
            {
                CustomSkyLayer[] acustomskylayer = worldSkyLayers[i];

                if (acustomskylayer != null)
                {
                    long j = world.getDayTime();
                    int k = (int)(j % 24000L);
                    float f = world.func_242415_f(partialTicks);
                    float f1 = world.getRainStrength(partialTicks);
                    float f2 = world.getThunderStrength(partialTicks);

                    if (f1 > 0.0F)
                    {
                        f2 /= f1;
                    }

                    for (int l = 0; l < acustomskylayer.length; ++l)
                    {
                        CustomSkyLayer customskylayer = acustomskylayer[l];

                        if (customskylayer.isActive(world, k))
                        {
                            customskylayer.render(world, matrixStackIn, k, f, f1, f2);
                        }
                    }

                    float f3 = 1.0F - f1;
                    Blender.clearBlend(f3);
                }
            }
        }
    }

    public static boolean hasSkyLayers(World world)
    {
        if (worldSkyLayers == null)
        {
            return false;
        }
        else
        {
            int i = WorldUtils.getDimensionId(world);

            if (i >= 0 && i < worldSkyLayers.length)
            {
                CustomSkyLayer[] acustomskylayer = worldSkyLayers[i];

                if (acustomskylayer == null)
                {
                    return false;
                }
                else
                {
                    return acustomskylayer.length > 0;
                }
            }
            else
            {
                return false;
            }
        }
    }
}
