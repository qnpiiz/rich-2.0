package net.optifine;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.render.RenderTypes;
import net.optifine.shaders.BlockAliases;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;

public class CustomBlockLayers
{
    private static RenderType[] renderLayers = null;
    public static boolean active = false;

    public static RenderType getRenderLayer(IBlockReader worldReader, BlockState blockState, BlockPos blockPos)
    {
        if (renderLayers == null)
        {
            return null;
        }
        else if (blockState.isOpaqueCube(worldReader, blockPos))
        {
            return null;
        }
        else
        {
            int i = blockState.getBlockId();
            return i > 0 && i < renderLayers.length ? renderLayers[i] : null;
        }
    }

    public static void update()
    {
        renderLayers = null;
        active = false;
        List<RenderType> list = new ArrayList<>();
        String s = "optifine/block.properties";
        Properties properties = ResUtils.readProperties(s, "CustomBlockLayers");

        if (properties != null)
        {
            readLayers(s, properties, list);
        }

        if (Config.isShaders())
        {
            PropertiesOrdered propertiesordered = BlockAliases.getBlockLayerPropertes();

            if (propertiesordered != null)
            {
                String s1 = "shaders/block.properties";
                readLayers(s1, propertiesordered, list);
            }
        }

        if (!list.isEmpty())
        {
            renderLayers = list.toArray(new RenderType[list.size()]);
            active = true;
        }
    }

    private static void readLayers(String pathProps, Properties props, List<RenderType> list)
    {
        Config.dbg("CustomBlockLayers: " + pathProps);
        readLayer("solid", RenderTypes.SOLID, props, list);
        readLayer("cutout", RenderTypes.CUTOUT, props, list);
        readLayer("cutout_mipped", RenderTypes.CUTOUT_MIPPED, props, list);
        readLayer("translucent", RenderTypes.TRANSLUCENT, props, list);
    }

    private static void readLayer(String name, RenderType layer, Properties props, List<RenderType> listLayers)
    {
        String s = "layer." + name;
        String s1 = props.getProperty(s);

        if (s1 != null)
        {
            ConnectedParser connectedparser = new ConnectedParser("CustomBlockLayers");
            MatchBlock[] amatchblock = connectedparser.parseMatchBlocks(s1);

            if (amatchblock != null)
            {
                for (int i = 0; i < amatchblock.length; ++i)
                {
                    MatchBlock matchblock = amatchblock[i];
                    int j = matchblock.getBlockId();

                    if (j > 0)
                    {
                        while (listLayers.size() < j + 1)
                        {
                            listLayers.add((RenderType)null);
                        }

                        if (listLayers.get(j) != null)
                        {
                            Config.warn("CustomBlockLayers: Block layer is already set, block: " + j + ", layer: " + name);
                        }

                        listLayers.set(j, layer);
                    }
                }
            }
        }
    }

    public static boolean isActive()
    {
        return active;
    }
}
