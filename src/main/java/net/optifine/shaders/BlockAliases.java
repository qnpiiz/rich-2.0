package net.optifine.shaders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.optifine.Config;
import net.optifine.ConnectedProperties;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.util.BlockUtils;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;

public class BlockAliases
{
    private static BlockAlias[][] blockAliases = (BlockAlias[][])null;
    private static boolean hasAliasMetadata = false;
    private static PropertiesOrdered blockLayerPropertes = null;
    private static boolean updateOnResourcesReloaded;
    private static List<List<BlockAlias>> legacyAliases;

    public static int getAliasBlockId(BlockState blockState)
    {
        int i = blockState.getBlockId();
        int j = blockState.getMetadata();
        BlockAlias blockalias = getBlockAlias(i, j);
        return blockalias != null ? blockalias.getAliasBlockId() : -1;
    }

    public static boolean hasAliasMetadata()
    {
        return hasAliasMetadata;
    }

    public static int getAliasMetadata(BlockState blockState)
    {
        if (!hasAliasMetadata)
        {
            return 0;
        }
        else
        {
            int i = blockState.getBlockId();
            int j = blockState.getMetadata();
            BlockAlias blockalias = getBlockAlias(i, j);
            return blockalias != null ? blockalias.getAliasMetadata() : 0;
        }
    }

    public static BlockAlias getBlockAlias(int blockId, int metadata)
    {
        if (blockAliases == null)
        {
            return null;
        }
        else if (blockId >= 0 && blockId < blockAliases.length)
        {
            BlockAlias[] ablockalias = blockAliases[blockId];

            if (ablockalias == null)
            {
                return null;
            }
            else
            {
                for (int i = 0; i < ablockalias.length; ++i)
                {
                    BlockAlias blockalias = ablockalias[i];

                    if (blockalias.matches(blockId, metadata))
                    {
                        return blockalias;
                    }
                }

                return null;
            }
        }
        else
        {
            return null;
        }
    }

    public static BlockAlias[] getBlockAliases(int blockId)
    {
        if (blockAliases == null)
        {
            return null;
        }
        else
        {
            return blockId >= 0 && blockId < blockAliases.length ? blockAliases[blockId] : null;
        }
    }

    public static void resourcesReloaded()
    {
        if (updateOnResourcesReloaded)
        {
            updateOnResourcesReloaded = false;
            update(Shaders.getShaderPack());
        }
    }

    public static void update(IShaderPack shaderPack)
    {
        reset();

        if (shaderPack != null)
        {
            if (!(shaderPack instanceof ShaderPackNone))
            {
                if (Reflector.Loader_getActiveModList.exists() && Minecraft.getInstance().getResourceManager() == null)
                {
                    Config.dbg("[Shaders] Delayed loading of block mappings after resources are loaded");
                    updateOnResourcesReloaded = true;
                }
                else
                {
                    List<List<BlockAlias>> list = new ArrayList<>();
                    String s = "/shaders/block.properties";
                    InputStream inputstream = shaderPack.getResourceAsStream(s);

                    if (inputstream != null)
                    {
                        loadBlockAliases(inputstream, s, list);
                    }

                    loadModBlockAliases(list);

                    if (list.size() <= 0)
                    {
                        list = getLegacyAliases();
                        hasAliasMetadata = true;
                    }

                    blockAliases = toBlockAliasArrays(list);
                }
            }
        }
    }

    private static void loadModBlockAliases(List<List<BlockAlias>> listBlockAliases)
    {
        String[] astring = ReflectorForge.getForgeModIds();

        for (int i = 0; i < astring.length; ++i)
        {
            String s = astring[i];

            try
            {
                ResourceLocation resourcelocation = new ResourceLocation(s, "shaders/block.properties");
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                loadBlockAliases(inputstream, resourcelocation.toString(), listBlockAliases);
            }
            catch (IOException ioexception)
            {
            }
        }
    }

    private static void loadBlockAliases(InputStream in, String path, List<List<BlockAlias>> listBlockAliases)
    {
        if (in != null)
        {
            try
            {
                in = MacroProcessor.process(in, path, true);
                Properties properties = new PropertiesOrdered();
                properties.load(in);
                in.close();
                Config.dbg("[Shaders] Parsing block mappings: " + path);
                ConnectedParser connectedparser = new ConnectedParser("Shaders");

                for (String s : (Set<String>)(Set<?>)properties.keySet())
                {
                    String s1 = properties.getProperty(s);

                    if (s.startsWith("layer."))
                    {
                        if (blockLayerPropertes == null)
                        {
                            blockLayerPropertes = new PropertiesOrdered();
                        }

                        blockLayerPropertes.put(s, s1);
                    }
                    else
                    {
                        String s2 = "block.";

                        if (!s.startsWith(s2))
                        {
                            Config.warn("[Shaders] Invalid block ID: " + s);
                        }
                        else
                        {
                            String s3 = StrUtils.removePrefix(s, s2);
                            int i = Config.parseInt(s3, -1);

                            if (i < 0)
                            {
                                Config.warn("[Shaders] Invalid block ID: " + s);
                            }
                            else
                            {
                                MatchBlock[] amatchblock = connectedparser.parseMatchBlocks(s1);

                                if (amatchblock != null && amatchblock.length >= 1)
                                {
                                    BlockAlias blockalias = new BlockAlias(i, amatchblock);
                                    addToList(listBlockAliases, blockalias);
                                }
                                else
                                {
                                    Config.warn("[Shaders] Invalid block ID mapping: " + s + "=" + s1);
                                }
                            }
                        }
                    }
                }
            }
            catch (IOException ioexception)
            {
                Config.warn("[Shaders] Error reading: " + path);
            }
        }
    }

    private static void addToList(List<List<BlockAlias>> blocksAliases, BlockAlias ba)
    {
        int[] aint = ba.getMatchBlockIds();

        for (int i = 0; i < aint.length; ++i)
        {
            int j = aint[i];

            while (j >= blocksAliases.size())
            {
                blocksAliases.add((List<BlockAlias>)null);
            }

            List<BlockAlias> list = blocksAliases.get(j);

            if (list == null)
            {
                list = new ArrayList<>();
                blocksAliases.set(j, list);
            }

            BlockAlias blockalias = new BlockAlias(ba.getAliasBlockId(), ba.getMatchBlocks(j));
            list.add(blockalias);
        }
    }

    private static BlockAlias[][] toBlockAliasArrays(List<List<BlockAlias>> listBlocksAliases)
    {
        BlockAlias[][] ablockalias = new BlockAlias[listBlocksAliases.size()][];

        for (int i = 0; i < ablockalias.length; ++i)
        {
            List<BlockAlias> list = listBlocksAliases.get(i);

            if (list != null)
            {
                ablockalias[i] = list.toArray(new BlockAlias[list.size()]);
            }
        }

        return ablockalias;
    }

    private static List<List<BlockAlias>> getLegacyAliases()
    {
        if (legacyAliases == null)
        {
            legacyAliases = makeLegacyAliases();
        }

        return legacyAliases;
    }

    private static List<List<BlockAlias>> makeLegacyAliases()
    {
        try
        {
            String s = "flattening_ids.txt";
            Config.dbg("Using legacy block aliases: " + s);
            List<List<BlockAlias>> list = new ArrayList<>();
            List<String> list1 = new ArrayList<>();
            int i = 0;
            InputStream inputstream = Config.getOptiFineResourceStream("/" + s);

            if (inputstream == null)
            {
                return list;
            }
            else
            {
                String[] astring = Config.readLines(inputstream);

                for (int j = 0; j < astring.length; ++j)
                {
                    int k = j + 1;
                    String s1 = astring[j];

                    if (s1.trim().length() > 0)
                    {
                        list1.add(s1);

                        if (!s1.startsWith("#"))
                        {
                            if (s1.startsWith("alias"))
                            {
                                String[] astring1 = Config.tokenize(s1, " ");
                                String s2 = astring1[1];
                                String s3 = astring1[2];
                                String s4 = "{Name:'" + s3 + "'";
                                List<String> list2 = list1.stream().filter((sq) ->
                                {
                                    return sq.startsWith(s4);
                                }).collect(Collectors.toList());

                                if (list2.size() <= 0)
                                {
                                    Config.warn("Block not processed: " + s1);
                                }
                                else
                                {
                                    for (String s5 : list2)
                                    {
                                        String s6 = "{Name:'" + s2 + "'";
                                        String s7 = s5.replace(s4, s6);
                                        list1.add(s7);
                                        addLegacyAlias(s7, k, list);
                                        ++i;
                                    }
                                }
                            }
                            else
                            {
                                addLegacyAlias(s1, k, list);
                                ++i;
                            }
                        }
                    }
                }

                Config.dbg("Legacy block aliases: " + i);
                return list;
            }
        }
        catch (IOException ioexception)
        {
            Config.warn("Error loading legacy block aliases: " + ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return new ArrayList<>();
        }
    }

    private static void addLegacyAlias(String line, int lineNum, List<List<BlockAlias>> listAliases)
    {
        String[] astring = Config.tokenize(line, " ");

        if (astring.length != 4)
        {
            Config.warn("Invalid flattening line: " + line);
        }
        else
        {
            String s = astring[0];
            String s1 = astring[1];
            int i = Config.parseInt(astring[2], Integer.MIN_VALUE);
            int j = Config.parseInt(astring[3], Integer.MIN_VALUE);

            if (i >= 0 && j >= 0)
            {
                try
                {
                    JsonParser jsonparser = new JsonParser();
                    JsonObject jsonobject = jsonparser.parse(s).getAsJsonObject();
                    String s2 = jsonobject.get("Name").getAsString();
                    ResourceLocation resourcelocation = new ResourceLocation(s2);
                    Block block = BlockUtils.getBlock(resourcelocation);

                    if (block == null)
                    {
                        Config.warn("Invalid block name (" + lineNum + "): " + s2);
                        return;
                    }

                    BlockState blockstate = block.getDefaultState();
                    Collection < Property> collection = blockstate.getProperties();
                    Map<Property, Comparable> map = new LinkedHashMap<>();
                    JsonObject jsonobject1 = (JsonObject)jsonobject.get("Properties");

                    if (jsonobject1 != null)
                    {
                        for (Entry<String, JsonElement> entry : jsonobject1.entrySet())
                        {
                            String s3 = entry.getKey();
                            String s4 = entry.getValue().getAsString();
                            Property property = ConnectedProperties.getProperty(s3, collection);

                            if (property == null)
                            {
                                Config.warn("Invalid property (" + lineNum + "): " + s3);
                            }
                            else
                            {
                                Comparable comparable = ConnectedParser.parsePropertyValue(property, s4);

                                if (comparable == null)
                                {
                                    Config.warn("Invalid property value (" + lineNum + "): " + s4);
                                }
                                else
                                {
                                    map.put(property, comparable);
                                }
                            }
                        }
                    }

                    int k = blockstate.getBlockId();

                    while (listAliases.size() <= k)
                    {
                        listAliases.add((List<BlockAlias>)null);
                    }

                    List<BlockAlias> list = listAliases.get(k);

                    if (list == null)
                    {
                        list = new ArrayList<>(BlockUtils.getMetadataCount(block));
                        listAliases.set(k, list);
                    }

                    MatchBlock matchblock = getMatchBlock(blockstate.getBlock(), blockstate.getBlockId(), map);
                    addBlockAlias(list, i, j, matchblock);
                }
                catch (Exception exception)
                {
                    Config.warn("Error parsing: " + line);
                }
            }
            else
            {
                Config.warn("Invalid blockID or metadata (" + lineNum + "): " + i + ":" + j);
            }
        }
    }

    private static void addBlockAlias(List<BlockAlias> listBlockAliases, int aliasBlockId, int aliasMetadata, MatchBlock matchBlock)
    {
        for (BlockAlias blockalias : listBlockAliases)
        {
            if (blockalias.getAliasBlockId() == aliasBlockId && blockalias.getAliasMetadata() == aliasMetadata)
            {
                MatchBlock[] amatchblock = blockalias.getMatchBlocks();

                for (int i = 0; i < amatchblock.length; ++i)
                {
                    MatchBlock matchblock = amatchblock[i];

                    if (matchblock.getBlockId() == matchBlock.getBlockId())
                    {
                        matchblock.addMetadatas(matchBlock.getMetadatas());
                        return;
                    }
                }
            }
        }

        BlockAlias blockalias1 = new BlockAlias(aliasBlockId, aliasMetadata, new MatchBlock[] {matchBlock});
        listBlockAliases.add(blockalias1);
    }

    private static MatchBlock getMatchBlock(Block block, int blockId, Map<Property, Comparable> mapProperties)
    {
        List<BlockState> list = new ArrayList<>();
        Collection<Property> collection = mapProperties.keySet();

        for (BlockState blockstate : BlockUtils.getBlockStates(block))
        {
            boolean flag = true;

            for (Property property : collection)
            {
                if (!blockstate.hasProperty(property))
                {
                    flag = false;
                    break;
                }

                Comparable comparable = mapProperties.get(property);
                Comparable comparable1 = blockstate.get(property);

                if (!comparable.equals(comparable1))
                {
                    flag = false;
                    break;
                }
            }

            if (flag)
            {
                list.add(blockstate);
            }
        }

        Set<Integer> set = new LinkedHashSet<>();

        for (BlockState blockstate1 : list)
        {
            set.add(blockstate1.getMetadata());
        }

        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        int[] aint = Config.toPrimitive(ainteger);
        MatchBlock matchblock = new MatchBlock(blockId, aint);
        return matchblock;
    }

    private static void checkLegacyAliases()
    {
        for (ResourceLocation resourcelocation : Registry.BLOCK.keySet())
        {
            Block block = Registry.BLOCK.getOrDefault(resourcelocation);
            int i = block.getDefaultState().getBlockId();
            BlockAlias[] ablockalias = getBlockAliases(i);

            if (ablockalias == null)
            {
                Config.warn("Block has no alias: " + block);
            }
            else
            {
                for (BlockState blockstate : BlockUtils.getBlockStates(block))
                {
                    int j = blockstate.getMetadata();
                    BlockAlias blockalias = getBlockAlias(i, j);

                    if (blockalias == null)
                    {
                        Config.warn("State has no alias: " + blockstate);
                    }
                }
            }
        }
    }

    public static PropertiesOrdered getBlockLayerPropertes()
    {
        return blockLayerPropertes;
    }

    public static void reset()
    {
        blockAliases = (BlockAlias[][])null;
        hasAliasMetadata = false;
        blockLayerPropertes = null;
    }

    public static int getRenderType(BlockState blockState)
    {
        if (hasAliasMetadata)
        {
            Block block = blockState.getBlock();

            if (block instanceof FlowingFluidBlock)
            {
                return 1;
            }
            else
            {
                BlockRenderType blockrendertype = blockState.getRenderType();
                return blockrendertype != BlockRenderType.ENTITYBLOCK_ANIMATED && blockrendertype != BlockRenderType.MODEL ? blockrendertype.ordinal() : blockrendertype.ordinal() + 1;
            }
        }
        else
        {
            return blockState.getRenderType().ordinal();
        }
    }
}
