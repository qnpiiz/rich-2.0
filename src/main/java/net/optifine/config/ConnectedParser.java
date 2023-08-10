package net.optifine.config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.optifine.Config;
import net.optifine.ConnectedProperties;
import net.optifine.util.BiomeUtils;
import net.optifine.util.BlockUtils;
import net.optifine.util.EntityTypeUtils;
import net.optifine.util.ItemUtils;

public class ConnectedParser
{
    private String context = null;
    public static final MatchProfession[] PROFESSIONS_INVALID = new MatchProfession[0];
    public static final DyeColor[] DYE_COLORS_INVALID = new DyeColor[0];
    private static Map<ResourceLocation, BiomeId> MAP_BIOMES_COMPACT = null;
    private static final INameGetter<Enum> NAME_GETTER_ENUM = new INameGetter<Enum>()
    {
        public String getName(Enum en)
        {
            return en.name();
        }
    };
    private static final INameGetter<DyeColor> NAME_GETTER_DYE_COLOR = new INameGetter<DyeColor>()
    {
        public String getName(DyeColor col)
        {
            return col.getString();
        }
    };

    public ConnectedParser(String context)
    {
        this.context = context;
    }

    public String parseName(String path)
    {
        String s = path;
        int i = path.lastIndexOf(47);

        if (i >= 0)
        {
            s = path.substring(i + 1);
        }

        int j = s.lastIndexOf(46);

        if (j >= 0)
        {
            s = s.substring(0, j);
        }

        return s;
    }

    public String parseBasePath(String path)
    {
        int i = path.lastIndexOf(47);
        return i < 0 ? "" : path.substring(0, i);
    }

    public MatchBlock[] parseMatchBlocks(String propMatchBlocks)
    {
        if (propMatchBlocks == null)
        {
            return null;
        }
        else
        {
            List list = new ArrayList();
            String[] astring = Config.tokenize(propMatchBlocks, " ");

            for (int i = 0; i < astring.length; ++i)
            {
                String s = astring[i];
                MatchBlock[] amatchblock = this.parseMatchBlock(s);

                if (amatchblock != null)
                {
                    list.addAll(Arrays.asList(amatchblock));
                }
            }

            return (MatchBlock[]) list.toArray(new MatchBlock[list.size()]);
        }
    }

    public BlockState parseBlockState(String str, BlockState def)
    {
        MatchBlock[] amatchblock = this.parseMatchBlock(str);

        if (amatchblock == null)
        {
            return def;
        }
        else if (amatchblock.length != 1)
        {
            return def;
        }
        else
        {
            MatchBlock matchblock = amatchblock[0];
            int i = matchblock.getBlockId();
            Block block = Registry.BLOCK.getByValue(i);
            return block.getDefaultState();
        }
    }

    public MatchBlock[] parseMatchBlock(String blockStr)
    {
        if (blockStr == null)
        {
            return null;
        }
        else
        {
            blockStr = blockStr.trim();

            if (blockStr.length() <= 0)
            {
                return null;
            }
            else
            {
                String[] astring = Config.tokenize(blockStr, ":");
                String s = "minecraft";
                int i = 0;

                if (astring.length > 1 && this.isFullBlockName(astring))
                {
                    s = astring[0];
                    i = 1;
                }
                else
                {
                    s = "minecraft";
                    i = 0;
                }

                String s1 = astring[i];
                String[] astring1 = Arrays.copyOfRange(astring, i + 1, astring.length);
                Block[] ablock = this.parseBlockPart(s, s1);

                if (ablock == null)
                {
                    return null;
                }
                else
                {
                    MatchBlock[] amatchblock = new MatchBlock[ablock.length];

                    for (int j = 0; j < ablock.length; ++j)
                    {
                        Block block = ablock[j];
                        int k = Registry.BLOCK.getId(block);
                        int[] aint = null;

                        if (astring1.length > 0)
                        {
                            aint = this.parseBlockMetadatas(block, astring1);

                            if (aint == null)
                            {
                                return null;
                            }
                        }

                        MatchBlock matchblock = new MatchBlock(k, aint);
                        amatchblock[j] = matchblock;
                    }

                    return amatchblock;
                }
            }
        }
    }

    public boolean isFullBlockName(String[] parts)
    {
        if (parts.length <= 1)
        {
            return false;
        }
        else
        {
            String s = parts[1];

            if (s.length() < 1)
            {
                return false;
            }
            else
            {
                return !s.contains("=");
            }
        }
    }

    public boolean startsWithDigit(String str)
    {
        if (str == null)
        {
            return false;
        }
        else if (str.length() < 1)
        {
            return false;
        }
        else
        {
            char c0 = str.charAt(0);
            return Character.isDigit(c0);
        }
    }

    public Block[] parseBlockPart(String domain, String blockPart)
    {
        String s = domain + ":" + blockPart;
        ResourceLocation resourcelocation = new ResourceLocation(s);
        Block block = BlockUtils.getBlock(resourcelocation);

        if (block == null)
        {
            this.warn("Block not found for name: " + s);
            return null;
        }
        else
        {
            return new Block[] {block};
        }
    }

    public int[] parseBlockMetadatas(Block block, String[] params)
    {
        if (params.length <= 0)
        {
            return null;
        }
        else
        {
            BlockState blockstate = block.getDefaultState();
            Collection collection = blockstate.getProperties();
            Map<Property, List<Comparable>> map = new HashMap<>();

            for (int i = 0; i < params.length; ++i)
            {
                String s = params[i];

                if (s.length() > 0)
                {
                    String[] astring = Config.tokenize(s, "=");

                    if (astring.length != 2)
                    {
                        this.warn("Invalid block property: " + s);
                        return null;
                    }

                    String s1 = astring[0];
                    String s2 = astring[1];
                    Property property = ConnectedProperties.getProperty(s1, collection);

                    if (property == null)
                    {
                        this.warn("Property not found: " + s1 + ", block: " + block);
                        return null;
                    }

                    List<Comparable> list = map.get(s1);

                    if (list == null)
                    {
                        list = new ArrayList<>();
                        map.put(property, list);
                    }

                    String[] astring1 = Config.tokenize(s2, ",");

                    for (int j = 0; j < astring1.length; ++j)
                    {
                        String s3 = astring1[j];
                        Comparable comparable = parsePropertyValue(property, s3);

                        if (comparable == null)
                        {
                            this.warn("Property value not found: " + s3 + ", property: " + s1 + ", block: " + block);
                            return null;
                        }

                        list.add(comparable);
                    }
                }
            }

            if (map.isEmpty())
            {
                return null;
            }
            else
            {
                List<Integer> list1 = new ArrayList<>();
                int k = BlockUtils.getMetadataCount(block);

                for (int l = 0; l < k; ++l)
                {
                    try
                    {
                        BlockState blockstate1 = BlockUtils.getBlockState(block, l);

                        if (this.matchState(blockstate1, map))
                        {
                            list1.add(l);
                        }
                    }
                    catch (IllegalArgumentException illegalargumentexception)
                    {
                    }
                }

                if (list1.size() == k)
                {
                    return null;
                }
                else
                {
                    int[] aint = new int[list1.size()];

                    for (int i1 = 0; i1 < aint.length; ++i1)
                    {
                        aint[i1] = list1.get(i1);
                    }

                    return aint;
                }
            }
        }
    }

    public static Comparable parsePropertyValue(Property prop, String valStr)
    {
        Class oclass = prop.getValueClass();
        Comparable comparable = parseValue(valStr, oclass);

        if (comparable == null)
        {
            Collection collection = prop.getAllowedValues();
            comparable = getPropertyValue(valStr, collection);
        }

        return comparable;
    }

    public static Comparable getPropertyValue(String value, Collection propertyValues)
    {
        for (Comparable comparable :(Set<Comparable>)(Set<?>) propertyValues)
        {
            if (getValueName(comparable).equals(value))
            {
                return comparable;
            }
        }

        return null;
    }

    private static Object getValueName(Comparable obj)
    {
        if (obj instanceof IStringSerializable)
        {
            IStringSerializable istringserializable = (IStringSerializable)obj;
            return istringserializable.getString();
        }
        else
        {
            return obj.toString();
        }
    }

    public static Comparable parseValue(String str, Class cls)
    {
        if (cls == String.class)
        {
            return str;
        }
        else if (cls == Boolean.class)
        {
            return Boolean.valueOf(str);
        }
        else if (cls == Float.class)
        {
            return Float.valueOf(str);
        }
        else if (cls == Double.class)
        {
            return Double.valueOf(str);
        }
        else if (cls == Integer.class)
        {
            return Integer.valueOf(str);
        }
        else
        {
            return cls == Long.class ? Long.valueOf(str) : null;
        }
    }

    public boolean matchState(BlockState bs, Map<Property, List<Comparable>> mapPropValues)
    {
        for (Property property : mapPropValues.keySet())
        {
            List<Comparable> list = mapPropValues.get(property);
            Comparable comparable = bs.get(property);

            if (comparable == null)
            {
                return false;
            }

            if (!list.contains(comparable))
            {
                return false;
            }
        }

        return true;
    }

    public BiomeId[] parseBiomes(String str)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            str = str.trim();
            boolean flag = false;

            if (str.startsWith("!"))
            {
                flag = true;
                str = str.substring(1);
            }

            String[] astring = Config.tokenize(str, " ");
            List<BiomeId> list = new ArrayList<>();

            for (int i = 0; i < astring.length; ++i)
            {
                String s = astring[i];
                BiomeId biomeid = this.getBiomeId(s);

                if (biomeid == null)
                {
                    this.warn("Biome not found: " + s);
                }
                else
                {
                    list.add(biomeid);
                }
            }

            if (flag)
            {
                Set<ResourceLocation> set = new HashSet<>(BiomeUtils.getLocations());

                for (BiomeId biomeid1 : list)
                {
                    set.remove(biomeid1.getResourceLocation());
                }

                list = BiomeUtils.getBiomeIds(set);
            }

            return list.toArray(new BiomeId[list.size()]);
        }
    }

    public BiomeId getBiomeId(String biomeName)
    {
        biomeName = biomeName.toLowerCase();
        ResourceLocation resourcelocation = new ResourceLocation(biomeName);
        BiomeId biomeid = BiomeUtils.getBiomeId(resourcelocation);

        if (biomeid != null)
        {
            return biomeid;
        }
        else
        {
            String s = biomeName.replace(" ", "").replace("_", "");
            ResourceLocation resourcelocation1 = new ResourceLocation(s);

            if (MAP_BIOMES_COMPACT == null)
            {
                MAP_BIOMES_COMPACT = new HashMap<>();

                for (ResourceLocation resourcelocation2 : BiomeUtils.getLocations())
                {
                    BiomeId biomeid1 = BiomeUtils.getBiomeId(resourcelocation2);

                    if (biomeid1 != null)
                    {
                        String s1 = resourcelocation2.getPath().replace(" ", "").replace("_", "").toLowerCase();
                        ResourceLocation resourcelocation3 = new ResourceLocation(resourcelocation2.getNamespace(), s1);
                        MAP_BIOMES_COMPACT.put(resourcelocation3, biomeid1);
                    }
                }
            }

            biomeid = MAP_BIOMES_COMPACT.get(resourcelocation1);
            return biomeid != null ? biomeid : null;
        }
    }

    public int parseInt(String str, int defVal)
    {
        if (str == null)
        {
            return defVal;
        }
        else
        {
            str = str.trim();
            int i = Config.parseInt(str, -1);

            if (i < 0)
            {
                this.warn("Invalid number: " + str);
                return defVal;
            }
            else
            {
                return i;
            }
        }
    }

    public int[] parseIntList(String str)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            List<Integer> list = new ArrayList<>();
            String[] astring = Config.tokenize(str, " ,");

            for (int i = 0; i < astring.length; ++i)
            {
                String s = astring[i];

                if (s.contains("-"))
                {
                    String[] astring1 = Config.tokenize(s, "-");

                    if (astring1.length != 2)
                    {
                        this.warn("Invalid interval: " + s + ", when parsing: " + str);
                    }
                    else
                    {
                        int k = Config.parseInt(astring1[0], -1);
                        int l = Config.parseInt(astring1[1], -1);

                        if (k >= 0 && l >= 0 && k <= l)
                        {
                            for (int i1 = k; i1 <= l; ++i1)
                            {
                                list.add(i1);
                            }
                        }
                        else
                        {
                            this.warn("Invalid interval: " + s + ", when parsing: " + str);
                        }
                    }
                }
                else
                {
                    int j = Config.parseInt(s, -1);

                    if (j < 0)
                    {
                        this.warn("Invalid number: " + s + ", when parsing: " + str);
                    }
                    else
                    {
                        list.add(j);
                    }
                }
            }

            int[] aint = new int[list.size()];

            for (int j1 = 0; j1 < aint.length; ++j1)
            {
                aint[j1] = list.get(j1);
            }

            return aint;
        }
    }

    public boolean[] parseFaces(String str, boolean[] defVal)
    {
        if (str == null)
        {
            return defVal;
        }
        else
        {
            EnumSet enumset = EnumSet.allOf(Direction.class);
            String[] astring = Config.tokenize(str, " ,");

            for (int i = 0; i < astring.length; ++i)
            {
                String s = astring[i];

                if (s.equals("sides"))
                {
                    enumset.add(Direction.NORTH);
                    enumset.add(Direction.SOUTH);
                    enumset.add(Direction.WEST);
                    enumset.add(Direction.EAST);
                }
                else if (s.equals("all"))
                {
                    enumset.addAll(Arrays.asList(Direction.VALUES));
                }
                else
                {
                    Direction direction = this.parseFace(s);

                    if (direction != null)
                    {
                        enumset.add(direction);
                    }
                }
            }

            boolean[] aboolean = new boolean[Direction.VALUES.length];

            for (int j = 0; j < aboolean.length; ++j)
            {
                aboolean[j] = enumset.contains(Direction.VALUES[j]);
            }

            return aboolean;
        }
    }

    public Direction parseFace(String str)
    {
        str = str.toLowerCase();

        if (!str.equals("bottom") && !str.equals("down"))
        {
            if (!str.equals("top") && !str.equals("up"))
            {
                if (str.equals("north"))
                {
                    return Direction.NORTH;
                }
                else if (str.equals("south"))
                {
                    return Direction.SOUTH;
                }
                else if (str.equals("east"))
                {
                    return Direction.EAST;
                }
                else if (str.equals("west"))
                {
                    return Direction.WEST;
                }
                else
                {
                    Config.warn("Unknown face: " + str);
                    return null;
                }
            }
            else
            {
                return Direction.UP;
            }
        }
        else
        {
            return Direction.DOWN;
        }
    }

    public void dbg(String str)
    {
        Config.dbg("" + this.context + ": " + str);
    }

    public void warn(String str)
    {
        Config.warn("" + this.context + ": " + str);
    }

    public RangeListInt parseRangeListInt(String str)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            RangeListInt rangelistint = new RangeListInt();
            String[] astring = Config.tokenize(str, " ,");

            for (int i = 0; i < astring.length; ++i)
            {
                String s = astring[i];
                RangeInt rangeint = this.parseRangeInt(s);

                if (rangeint == null)
                {
                    return null;
                }

                rangelistint.addRange(rangeint);
            }

            return rangelistint;
        }
    }

    private RangeInt parseRangeInt(String str)
    {
        if (str == null)
        {
            return null;
        }
        else if (str.indexOf(45) >= 0)
        {
            String[] astring = Config.tokenize(str, "-");

            if (astring.length != 2)
            {
                this.warn("Invalid range: " + str);
                return null;
            }
            else
            {
                int j = Config.parseInt(astring[0], -1);
                int k = Config.parseInt(astring[1], -1);

                if (j >= 0 && k >= 0)
                {
                    return new RangeInt(j, k);
                }
                else
                {
                    this.warn("Invalid range: " + str);
                    return null;
                }
            }
        }
        else
        {
            int i = Config.parseInt(str, -1);

            if (i < 0)
            {
                this.warn("Invalid integer: " + str);
                return null;
            }
            else
            {
                return new RangeInt(i, i);
            }
        }
    }

    public boolean parseBoolean(String str, boolean defVal)
    {
        if (str == null)
        {
            return defVal;
        }
        else
        {
            String s = str.toLowerCase().trim();

            if (s.equals("true"))
            {
                return true;
            }
            else if (s.equals("false"))
            {
                return false;
            }
            else
            {
                this.warn("Invalid boolean: " + str);
                return defVal;
            }
        }
    }

    public Boolean parseBooleanObject(String str)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            String s = str.toLowerCase().trim();

            if (s.equals("true"))
            {
                return Boolean.TRUE;
            }
            else if (s.equals("false"))
            {
                return Boolean.FALSE;
            }
            else
            {
                this.warn("Invalid boolean: " + str);
                return null;
            }
        }
    }

    public static int parseColor(String str, int defVal)
    {
        if (str == null)
        {
            return defVal;
        }
        else
        {
            str = str.trim();

            try
            {
                return Integer.parseInt(str, 16) & 16777215;
            }
            catch (NumberFormatException numberformatexception)
            {
                return defVal;
            }
        }
    }

    public static int parseColor4(String str, int defVal)
    {
        if (str == null)
        {
            return defVal;
        }
        else
        {
            str = str.trim();

            try
            {
                return (int)(Long.parseLong(str, 16) & -1L);
            }
            catch (NumberFormatException numberformatexception)
            {
                return defVal;
            }
        }
    }

    public RenderType parseBlockRenderLayer(String str, RenderType def)
    {
        if (str == null)
        {
            return def;
        }
        else
        {
            str = str.toLowerCase().trim();
            RenderType[] arendertype = RenderType.CHUNK_RENDER_TYPES;

            for (int i = 0; i < arendertype.length; ++i)
            {
                RenderType rendertype = arendertype[i];

                if (str.equals(rendertype.getName().toLowerCase()))
                {
                    return rendertype;
                }
            }

            return def;
        }
    }

    public <T> T parseObject(String str, T[] objs, INameGetter nameGetter, String property)
    {
        if (str == null)
        {
            return (T)null;
        }
        else
        {
            String s = str.toLowerCase().trim();

            for (int i = 0; i < objs.length; ++i)
            {
                T t = objs[i];
                String s1 = nameGetter.getName(t);

                if (s1 != null && s1.toLowerCase().equals(s))
                {
                    return t;
                }
            }

            this.warn("Invalid " + property + ": " + str);
            return (T)null;
        }
    }

    public <T> T[] parseObjects(String str, T[] objs, INameGetter nameGetter, String property, T[] errValue)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            str = str.toLowerCase().trim();
            String[] astring = Config.tokenize(str, " ");
            T[] at = (T[])((Object[])Array.newInstance(objs.getClass().getComponentType(), astring.length));

            for (int i = 0; i < astring.length; ++i)
            {
                String s = astring[i];
                T t = this.parseObject(s, objs, nameGetter, property);

                if (t == null)
                {
                    return errValue;
                }

                at[i] = t;
            }

            return at;
        }
    }

    public Enum parseEnum(String str, Enum[] enums, String property)
    {
        return this.parseObject(str, enums, NAME_GETTER_ENUM, property);
    }

    public Enum[] parseEnums(String str, Enum[] enums, String property, Enum[] errValue)
    {
        return this.parseObjects(str, enums, NAME_GETTER_ENUM, property, errValue);
    }

    public DyeColor[] parseDyeColors(String str, String property, DyeColor[] errValue)
    {
        return this.parseObjects(str, DyeColor.values(), NAME_GETTER_DYE_COLOR, property, errValue);
    }

    public Weather[] parseWeather(String str, String property, Weather[] errValue)
    {
        return this.parseObjects(str, Weather.values(), NAME_GETTER_ENUM, property, errValue);
    }

    public NbtTagValue parseNbtTagValue(String path, String value)
    {
        return path != null && value != null ? new NbtTagValue(path, value) : null;
    }

    public MatchProfession[] parseProfessions(String profStr)
    {
        if (profStr == null)
        {
            return null;
        }
        else
        {
            List<MatchProfession> list = new ArrayList<>();
            String[] astring = Config.tokenize(profStr, " ");

            for (int i = 0; i < astring.length; ++i)
            {
                String s = astring[i];
                MatchProfession matchprofession = this.parseProfession(s);

                if (matchprofession == null)
                {
                    this.warn("Invalid profession: " + s);
                    return PROFESSIONS_INVALID;
                }

                list.add(matchprofession);
            }

            return list.isEmpty() ? null : list.toArray(new MatchProfession[list.size()]);
        }
    }

    private MatchProfession parseProfession(String str)
    {
        String s = str;
        String s1 = null;
        int i = str.lastIndexOf(58);

        if (i >= 0)
        {
            String s2 = str.substring(0, i);
            String s3 = str.substring(i + 1);

            if (s3.isEmpty() || s3.matches("[0-9].*"))
            {
                s = s2;
                s1 = s3;
            }
        }

        VillagerProfession villagerprofession = this.parseVillagerProfession(s);

        if (villagerprofession == null)
        {
            return null;
        }
        else
        {
            int[] aint = this.parseIntList(s1);
            return new MatchProfession(villagerprofession, aint);
        }
    }

    private VillagerProfession parseVillagerProfession(String str)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            str = str.toLowerCase();
            ResourceLocation resourcelocation = new ResourceLocation(str);
            Registry<VillagerProfession> registry = Registry.VILLAGER_PROFESSION;
            return !registry.containsKey(resourcelocation) ? null : registry.getOrDefault(resourcelocation);
        }
    }

    public int[] parseItems(String str)
    {
        str = str.trim();
        Set<Integer> set = new TreeSet<>();
        String[] astring = Config.tokenize(str, " ");

        for (int i = 0; i < astring.length; ++i)
        {
            String s = astring[i];
            ResourceLocation resourcelocation = new ResourceLocation(s);
            Item item = ItemUtils.getItem(resourcelocation);

            if (item == null)
            {
                this.warn("Item not found: " + s);
            }
            else
            {
                int j = ItemUtils.getId(item);

                if (j < 0)
                {
                    this.warn("Item has no ID: " + item + ", name: " + s);
                }
                else
                {
                    set.add(new Integer(j));
                }
            }
        }

        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        return Config.toPrimitive(ainteger);
    }

    public int[] parseEntities(String str)
    {
        str = str.trim();
        Set<Integer> set = new TreeSet<>();
        String[] astring = Config.tokenize(str, " ");

        for (int i = 0; i < astring.length; ++i)
        {
            String s = astring[i];
            ResourceLocation resourcelocation = new ResourceLocation(s);
            EntityType entitytype = EntityTypeUtils.getEntityType(resourcelocation);

            if (entitytype == null)
            {
                this.warn("Entity not found: " + s);
            }
            else
            {
                int j = Registry.ENTITY_TYPE.getId(entitytype);

                if (j < 0)
                {
                    this.warn("Entity has no ID: " + entitytype + ", name: " + s);
                }
                else
                {
                    set.add(new Integer(j));
                }
            }
        }

        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        return Config.toPrimitive(ainteger);
    }
}
