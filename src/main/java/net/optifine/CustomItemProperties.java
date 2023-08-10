package net.optifine;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemModelGenerator;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.optifine.config.IParserInt;
import net.optifine.config.NbtTagValue;
import net.optifine.config.ParserEnchantmentId;
import net.optifine.config.RangeInt;
import net.optifine.config.RangeListInt;
import net.optifine.render.Blender;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;
import org.lwjgl.opengl.GL11;

public class CustomItemProperties
{
    public String name = null;
    public String basePath = null;
    public int type = 1;
    public int[] items = null;
    public String texture = null;
    public Map<String, String> mapTextures = null;
    public String model = null;
    public Map<String, String> mapModels = null;
    public RangeListInt damage = null;
    public boolean damagePercent = false;
    public int damageMask = 0;
    public RangeListInt stackSize = null;
    public int[] enchantmentIds = null;
    public RangeListInt enchantmentLevels = null;
    public NbtTagValue[] nbtTagValues = null;
    public int hand = 0;
    public int blend = 1;
    public float speed = 0.0F;
    public float rotation = 0.0F;
    public int layer = 0;
    public float duration = 1.0F;
    public int weight = 0;
    public ResourceLocation textureLocation = null;
    public Map mapTextureLocations = null;
    public TextureAtlasSprite sprite = null;
    public Map mapSprites = null;
    public IBakedModel bakedModelTexture = null;
    public Map<String, IBakedModel> mapBakedModelsTexture = null;
    public IBakedModel bakedModelFull = null;
    public Map<String, IBakedModel> mapBakedModelsFull = null;
    private int textureWidth = 0;
    private int textureHeight = 0;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_ENCHANTMENT = 2;
    public static final int TYPE_ARMOR = 3;
    public static final int TYPE_ELYTRA = 4;
    public static final int HAND_ANY = 0;
    public static final int HAND_MAIN = 1;
    public static final int HAND_OFF = 2;
    public static final String INVENTORY = "inventory";

    public CustomItemProperties(Properties props, String path)
    {
        this.name = parseName(path);
        this.basePath = parseBasePath(path);
        this.type = this.parseType(props.getProperty("type"));
        this.items = this.parseItems(props.getProperty("items"), props.getProperty("matchItems"));
        this.mapModels = parseModels(props, this.basePath);
        this.model = parseModel(props.getProperty("model"), path, this.basePath, this.type, this.mapModels);
        this.mapTextures = parseTextures(props, this.basePath);
        boolean flag = this.mapModels == null && this.model == null;
        this.texture = parseTexture(props.getProperty("texture"), props.getProperty("tile"), props.getProperty("source"), path, this.basePath, this.type, this.mapTextures, flag);
        String s = props.getProperty("damage");

        if (s != null)
        {
            this.damagePercent = s.contains("%");
            s = s.replace("%", "");
            this.damage = this.parseRangeListInt(s);
            this.damageMask = this.parseInt(props.getProperty("damageMask"), 0);
        }

        this.stackSize = this.parseRangeListInt(props.getProperty("stackSize"));
        this.enchantmentIds = this.parseInts(getProperty(props, "enchantmentIDs", "enchantments"), new ParserEnchantmentId());
        this.enchantmentLevels = this.parseRangeListInt(props.getProperty("enchantmentLevels"));
        this.nbtTagValues = this.parseNbtTagValues(props);
        this.hand = this.parseHand(props.getProperty("hand"));
        this.blend = Blender.parseBlend(props.getProperty("blend"));
        this.speed = this.parseFloat(props.getProperty("speed"), 0.0F);
        this.rotation = this.parseFloat(props.getProperty("rotation"), 0.0F);
        this.layer = this.parseInt(props.getProperty("layer"), 0);
        this.weight = this.parseInt(props.getProperty("weight"), 0);
        this.duration = this.parseFloat(props.getProperty("duration"), 1.0F);
    }

    private static String getProperty(Properties props, String... names)
    {
        for (int i = 0; i < names.length; ++i)
        {
            String s = names[i];
            String s1 = props.getProperty(s);

            if (s1 != null)
            {
                return s1;
            }
        }

        return null;
    }

    private static String parseName(String path)
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

    private static String parseBasePath(String path)
    {
        int i = path.lastIndexOf(47);
        return i < 0 ? "" : path.substring(0, i);
    }

    private int parseType(String str)
    {
        if (str == null)
        {
            return 1;
        }
        else if (str.equals("item"))
        {
            return 1;
        }
        else if (str.equals("enchantment"))
        {
            return 2;
        }
        else if (str.equals("armor"))
        {
            return 3;
        }
        else if (str.equals("elytra"))
        {
            return 4;
        }
        else
        {
            Config.warn("Unknown method: " + str);
            return 0;
        }
    }

    private int[] parseItems(String str, String str2)
    {
        if (str == null)
        {
            str = str2;
        }

        if (str == null)
        {
            return null;
        }
        else
        {
            str = str.trim();
            Set set = new TreeSet();
            String[] astring = Config.tokenize(str, " ");

            for (int i = 0; i < astring.length; ++i)
            {
                String s = astring[i];
                Item item = this.getItemByName(s);

                if (item == null)
                {
                    Config.warn("Item not found: " + s);
                }
                else
                {
                    int j = Item.getIdFromItem(item);

                    if (j < 0)
                    {
                        Config.warn("Item ID not found: " + s);
                    }
                    else
                    {
                        set.add(new Integer(j));
                    }
                }
            }

            Integer[] ainteger = (Integer[]) set.toArray(new Integer[set.size()]);
            int[] aint = new int[ainteger.length];

            for (int k = 0; k < aint.length; ++k)
            {
                aint[k] = ainteger[k];
            }

            return aint;
        }
    }

    private Item getItemByName(String name)
    {
        ResourceLocation resourcelocation = new ResourceLocation(name);
        return !Registry.ITEM.containsKey(resourcelocation) ? null : Registry.ITEM.getOrDefault(resourcelocation);
    }

    private static String parseTexture(String texStr, String texStr2, String texStr3, String path, String basePath, int type, Map<String, String> mapTexs, boolean textureFromPath)
    {
        if (texStr == null)
        {
            texStr = texStr2;
        }

        if (texStr == null)
        {
            texStr = texStr3;
        }

        if (texStr != null)
        {
            String s2 = ".png";

            if (texStr.endsWith(s2))
            {
                texStr = texStr.substring(0, texStr.length() - s2.length());
            }

            return fixTextureName(texStr, basePath);
        }
        else if (type == 3)
        {
            return null;
        }
        else
        {
            if (mapTexs != null)
            {
                String s = mapTexs.get("texture.bow_standby");

                if (s != null)
                {
                    return s;
                }
            }

            if (!textureFromPath)
            {
                return null;
            }
            else
            {
                String s1 = path;
                int i = path.lastIndexOf(47);

                if (i >= 0)
                {
                    s1 = path.substring(i + 1);
                }

                int j = s1.lastIndexOf(46);

                if (j >= 0)
                {
                    s1 = s1.substring(0, j);
                }

                return fixTextureName(s1, basePath);
            }
        }
    }

    private static Map parseTextures(Properties props, String basePath)
    {
        String s = "texture.";
        Map map = getMatchingProperties(props, s);

        if (map.size() <= 0)
        {
            return null;
        }
        else
        {
            Set set = map.keySet();
            Map map1 = new LinkedHashMap();

            for (Iterator it = set.iterator(); it.hasNext();)
            {
            	String key = (String)it.next();
                String s2 = (String)map.get(key);
                s2 = fixTextureName(s2, basePath);
                map1.put(key, s2);
            }

            return map1;
        }
    }

    private static String fixTextureName(String iconName, String basePath)
    {
        iconName = TextureUtils.fixResourcePath(iconName, basePath);

        if (!iconName.startsWith(basePath) && !iconName.startsWith("textures/") && !iconName.startsWith("optifine/"))
        {
            iconName = basePath + "/" + iconName;
        }

        if (iconName.endsWith(".png"))
        {
            iconName = iconName.substring(0, iconName.length() - 4);
        }

        if (iconName.startsWith("/"))
        {
            iconName = iconName.substring(1);
        }

        return iconName;
    }

    private static String parseModel(String modelStr, String path, String basePath, int type, Map<String, String> mapModelNames)
    {
        if (modelStr != null)
        {
            String s1 = ".json";

            if (modelStr.endsWith(s1))
            {
                modelStr = modelStr.substring(0, modelStr.length() - s1.length());
            }

            return fixModelName(modelStr, basePath);
        }
        else if (type == 3)
        {
            return null;
        }
        else
        {
            if (mapModelNames != null)
            {
                String s = mapModelNames.get("model.bow_standby");

                if (s != null)
                {
                    return s;
                }
            }

            return modelStr;
        }
    }

    private static Map parseModels(Properties props, String basePath)
    {
        String s = "model.";
        Map map = getMatchingProperties(props, s);

        if (map.size() <= 0)
        {
            return null;
        }
        else
        {
            Set set = map.keySet();
            Map map1 = new LinkedHashMap();

            for (String s1 : (Set<String>)(Set<?>)set)
            {
                String s2 = (String)map.get(s1);
                s2 = fixModelName(s2, basePath);
                map1.put(s1, s2);
            }

            return map1;
        }
    }

    private static String fixModelName(String modelName, String basePath)
    {
        modelName = TextureUtils.fixResourcePath(modelName, basePath);
        boolean flag = modelName.startsWith("block/") || modelName.startsWith("item/");

        if (!modelName.startsWith(basePath) && !flag && !modelName.startsWith("optifine/"))
        {
            modelName = basePath + "/" + modelName;
        }

        String s = ".json";

        if (modelName.endsWith(s))
        {
            modelName = modelName.substring(0, modelName.length() - s.length());
        }

        if (modelName.startsWith("/"))
        {
            modelName = modelName.substring(1);
        }

        return modelName;
    }

    private int parseInt(String str, int defVal)
    {
        if (str == null)
        {
            return defVal;
        }
        else
        {
            str = str.trim();
            int i = Config.parseInt(str, Integer.MIN_VALUE);

            if (i == Integer.MIN_VALUE)
            {
                Config.warn("Invalid integer: " + str);
                return defVal;
            }
            else
            {
                return i;
            }
        }
    }

    private float parseFloat(String str, float defVal)
    {
        if (str == null)
        {
            return defVal;
        }
        else
        {
            str = str.trim();
            float f = Config.parseFloat(str, Float.MIN_VALUE);

            if (f == Float.MIN_VALUE)
            {
                Config.warn("Invalid float: " + str);
                return defVal;
            }
            else
            {
                return f;
            }
        }
    }

    private int[] parseInts(String str, IParserInt parser)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            String[] astring = Config.tokenize(str, " ");
            List<Integer> list = new ArrayList<>();

            for (int i = 0; i < astring.length; ++i)
            {
                String s = astring[i];
                int j = parser.parse(s, Integer.MIN_VALUE);

                if (j == Integer.MIN_VALUE)
                {
                    Config.warn("Invalid value: " + s);
                }
                else
                {
                    list.add(j);
                }
            }

            Integer[] ainteger = list.toArray(new Integer[list.size()]);
            return Config.toPrimitive(ainteger);
        }
    }

    private RangeListInt parseRangeListInt(String str)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            String[] astring = Config.tokenize(str, " ");
            RangeListInt rangelistint = new RangeListInt();

            for (int i = 0; i < astring.length; ++i)
            {
                String s = astring[i];
                RangeInt rangeint = this.parseRangeInt(s);

                if (rangeint == null)
                {
                    Config.warn("Invalid range list: " + str);
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
        else
        {
            str = str.trim();
            int i = str.length() - str.replace("-", "").length();

            if (i > 1)
            {
                Config.warn("Invalid range: " + str);
                return null;
            }
            else
            {
                String[] astring = Config.tokenize(str, "- ");
                int[] aint = new int[astring.length];

                for (int j = 0; j < astring.length; ++j)
                {
                    String s = astring[j];
                    int k = Config.parseInt(s, -1);

                    if (k < 0)
                    {
                        Config.warn("Invalid range: " + str);
                        return null;
                    }

                    aint[j] = k;
                }

                if (aint.length == 1)
                {
                    int i1 = aint[0];

                    if (str.startsWith("-"))
                    {
                        return new RangeInt(0, i1);
                    }
                    else
                    {
                        return str.endsWith("-") ? new RangeInt(i1, 65535) : new RangeInt(i1, i1);
                    }
                }
                else if (aint.length == 2)
                {
                    int l = Math.min(aint[0], aint[1]);
                    int j1 = Math.max(aint[0], aint[1]);
                    return new RangeInt(l, j1);
                }
                else
                {
                    Config.warn("Invalid range: " + str);
                    return null;
                }
            }
        }
    }

    private NbtTagValue[] parseNbtTagValues(Properties props)
    {
        String s = "nbt.";
        Map map = getMatchingProperties(props, s);

        if (map.size() <= 0)
        {
            return null;
        }
        else
        {
            List list = new ArrayList();

            for (String s1 : (Set<String>)(Set<?>)map.keySet())
            {
                String s2 = (String)map.get(s1);
                String s3 = s1.substring(s.length());
                NbtTagValue nbttagvalue = new NbtTagValue(s3, s2);
                list.add(nbttagvalue);
            }

            NbtTagValue[] anbttagvalue = (NbtTagValue[]) list.toArray(new NbtTagValue[list.size()]);
            return anbttagvalue;
        }
    }

    private static Map getMatchingProperties(Properties props, String keyPrefix)
    {
        Map map = new LinkedHashMap();

        for (String s : (Set<String>)(Set<?>)props.keySet())
        {
            String s1 = props.getProperty(s);

            if (s.startsWith(keyPrefix))
            {
                map.put(s, s1);
            }
        }

        return map;
    }

    private int parseHand(String str)
    {
        if (str == null)
        {
            return 0;
        }
        else
        {
            str = str.toLowerCase();

            if (str.equals("any"))
            {
                return 0;
            }
            else if (str.equals("main"))
            {
                return 1;
            }
            else if (str.equals("off"))
            {
                return 2;
            }
            else
            {
                Config.warn("Invalid hand: " + str);
                return 0;
            }
        }
    }

    public boolean isValid(String path)
    {
        if (this.name != null && this.name.length() > 0)
        {
            if (this.basePath == null)
            {
                Config.warn("No base path found: " + path);
                return false;
            }
            else if (this.type == 0)
            {
                Config.warn("No type defined: " + path);
                return false;
            }
            else
            {
                if (this.type == 4 && this.items == null)
                {
                    this.items = new int[] {Item.getIdFromItem(Items.ELYTRA)};
                }

                if (this.type == 1 || this.type == 3 || this.type == 4)
                {
                    if (this.items == null)
                    {
                        this.items = this.detectItems();
                    }

                    if (this.items == null)
                    {
                        Config.warn("No items defined: " + path);
                        return false;
                    }
                }

                if (this.texture == null && this.mapTextures == null && this.model == null && this.mapModels == null)
                {
                    Config.warn("No texture or model specified: " + path);
                    return false;
                }
                else if (this.type == 2 && this.enchantmentIds == null)
                {
                    Config.warn("No enchantmentIDs specified: " + path);
                    return false;
                }
                else
                {
                    return true;
                }
            }
        }
        else
        {
            Config.warn("No name found: " + path);
            return false;
        }
    }

    private int[] detectItems()
    {
        Item item = this.getItemByName(this.name);

        if (item == null)
        {
            return null;
        }
        else
        {
            int i = Item.getIdFromItem(item);
            return i < 0 ? null : new int[] {i};
        }
    }

    public void updateIcons(AtlasTexture textureMap)
    {
        if (this.texture != null)
        {
            this.textureLocation = this.getTextureLocation(this.texture);

            if (this.type == 1)
            {
                ResourceLocation resourcelocation = this.getSpriteLocation(this.textureLocation);
                this.sprite = textureMap.registerSprite(resourcelocation);
            }
        }

        if (this.mapTextures != null)
        {
            this.mapTextureLocations = new HashMap();
            this.mapSprites = new HashMap();

            for (String s : this.mapTextures.keySet())
            {
                String s1 = this.mapTextures.get(s);
                ResourceLocation resourcelocation1 = this.getTextureLocation(s1);
                this.mapTextureLocations.put(s, resourcelocation1);

                if (this.type == 1)
                {
                    ResourceLocation resourcelocation2 = this.getSpriteLocation(resourcelocation1);
                    TextureAtlasSprite textureatlassprite = textureMap.registerSprite(resourcelocation2);
                    this.mapSprites.put(s, textureatlassprite);
                }
            }
        }
    }

    public void refreshIcons(AtlasTexture textureMap)
    {
        if (this.sprite != null)
        {
            this.sprite = textureMap.getSprite(this.sprite.getName());
        }

        if (this.mapSprites != null)
        {
            for (String s : (Set<String>)this.mapSprites.keySet())
            {
                TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)this.mapSprites.get(s);

                if (textureatlassprite != null)
                {
                    ResourceLocation resourcelocation = textureatlassprite.getName();
                    TextureAtlasSprite textureatlassprite1 = textureMap.getSprite(resourcelocation);

                    if (textureatlassprite1 == null || textureatlassprite1 instanceof MissingTextureSprite)
                    {
                        Config.warn("Missing CIT sprite: " + resourcelocation + ", properties: " + this.basePath);
                    }

                    this.mapSprites.put(s, textureatlassprite1);
                }
            }
        }
    }

    private ResourceLocation getTextureLocation(String texName)
    {
        if (texName == null)
        {
            return null;
        }
        else
        {
            ResourceLocation resourcelocation = new ResourceLocation(texName);
            String s = resourcelocation.getNamespace();
            String s1 = resourcelocation.getPath();

            if (!s1.contains("/"))
            {
                s1 = "textures/item/" + s1;
            }

            String s2 = s1 + ".png";
            ResourceLocation resourcelocation1 = new ResourceLocation(s, s2);
            boolean flag = Config.hasResource(resourcelocation1);

            if (!flag)
            {
                Config.warn("File not found: " + s2);
            }

            return resourcelocation1;
        }
    }

    private ResourceLocation getSpriteLocation(ResourceLocation resLoc)
    {
        String s = resLoc.getPath();
        s = StrUtils.removePrefix(s, "textures/");
        s = StrUtils.removeSuffix(s, ".png");
        return new ResourceLocation(resLoc.getNamespace(), s);
    }

    public void updateModelTexture(AtlasTexture textureMap, ItemModelGenerator itemModelGenerator)
    {
        if (this.texture != null || this.mapTextures != null)
        {
            String[] astring = this.getModelTextures();
            boolean flag = this.isUseTint();
            this.bakedModelTexture = makeBakedModel(textureMap, itemModelGenerator, astring, flag);

            if (this.type == 1 && this.mapTextures != null)
            {
                for (String s : this.mapTextures.keySet())
                {
                    String s1 = this.mapTextures.get(s);
                    String s2 = StrUtils.removePrefix(s, "texture.");

                    if (this.isSubTexture(s2))
                    {
                        String[] astring1 = new String[] {s1};
                        IBakedModel ibakedmodel = makeBakedModel(textureMap, itemModelGenerator, astring1, flag);

                        if (this.mapBakedModelsTexture == null)
                        {
                            this.mapBakedModelsTexture = new HashMap<>();
                        }

                        String s3 = "item/" + s2;
                        this.mapBakedModelsTexture.put(s3, ibakedmodel);
                    }
                }
            }
        }
    }

    private boolean isSubTexture(String path)
    {
        return path.startsWith("bow") || path.startsWith("crossbow") || path.startsWith("fishing_rod") || path.startsWith("shield");
    }

    private boolean isUseTint()
    {
        return true;
    }

    private static IBakedModel makeBakedModel(AtlasTexture textureMap, ItemModelGenerator itemModelGenerator, String[] textures, boolean useTint)
    {
        String[] astring = new String[textures.length];

        for (int i = 0; i < astring.length; ++i)
        {
            String s = textures[i];
            astring[i] = StrUtils.removePrefix(s, "textures/");
        }

        BlockModel blockmodel = makeModelBlock(astring);
        BlockModel blockmodel1 = itemModelGenerator.makeItemModel(CustomItemProperties::getSprite, blockmodel);
        return bakeModel(textureMap, blockmodel1, useTint);
    }

    public static TextureAtlasSprite getSprite(RenderMaterial material)
    {
        AtlasTexture atlastexture = Minecraft.getInstance().getModelManager().getAtlasTexture(material.getAtlasLocation());
        return atlastexture.getSprite(material.getTextureLocation());
    }

    private String[] getModelTextures()
    {
        if (this.type == 1 && this.items.length == 1)
        {
            Item item = Item.getItemById(this.items[0]);
            boolean flag = item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION;

            if (flag && this.damage != null && this.damage.getCountRanges() > 0)
            {
                RangeInt rangeint = this.damage.getRange(0);
                int i = rangeint.getMin();
                boolean flag1 = (i & 16384) != 0;
                String s5 = this.getMapTexture(this.mapTextures, "texture.potion_overlay", "item/potion_overlay");
                String s6 = null;

                if (flag1)
                {
                    s6 = this.getMapTexture(this.mapTextures, "texture.potion_bottle_splash", "item/potion_bottle_splash");
                }
                else
                {
                    s6 = this.getMapTexture(this.mapTextures, "texture.potion_bottle_drinkable", "item/potion_bottle_drinkable");
                }

                return new String[] {s5, s6};
            }

            if (item instanceof ArmorItem)
            {
                ArmorItem armoritem = (ArmorItem)item;

                if (armoritem.getArmorMaterial() == ArmorMaterial.LEATHER)
                {
                    String s = "leather";
                    String s1 = "helmet";
                    EquipmentSlotType equipmentslottype = armoritem.getEquipmentSlot();

                    if (equipmentslottype == EquipmentSlotType.HEAD)
                    {
                        s1 = "helmet";
                    }

                    if (equipmentslottype == EquipmentSlotType.CHEST)
                    {
                        s1 = "chestplate";
                    }

                    if (equipmentslottype == EquipmentSlotType.LEGS)
                    {
                        s1 = "leggings";
                    }

                    if (equipmentslottype == EquipmentSlotType.FEET)
                    {
                        s1 = "boots";
                    }

                    String s2 = s + "_" + s1;
                    String s3 = this.getMapTexture(this.mapTextures, "texture." + s2, "item/" + s2);
                    String s4 = this.getMapTexture(this.mapTextures, "texture." + s2 + "_overlay", "item/" + s2 + "_overlay");
                    return new String[] {s3, s4};
                }
            }
        }

        return new String[] {this.texture};
    }

    private String getMapTexture(Map<String, String> map, String key, String def)
    {
        if (map == null)
        {
            return def;
        }
        else
        {
            String s = map.get(key);
            return s == null ? def : s;
        }
    }

    private static BlockModel makeModelBlock(String[] modelTextures)
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("{\"parent\": \"builtin/generated\",\"textures\": {");

        for (int i = 0; i < modelTextures.length; ++i)
        {
            String s = modelTextures[i];

            if (i > 0)
            {
                stringbuffer.append(", ");
            }

            stringbuffer.append("\"layer" + i + "\": \"" + s + "\"");
        }

        stringbuffer.append("}}");
        String s1 = stringbuffer.toString();
        return BlockModel.deserialize(s1);
    }

    private static IBakedModel bakeModel(AtlasTexture textureMap, BlockModel modelBlockIn, boolean useTint)
    {
        ModelRotation modelrotation = ModelRotation.X0_Y0;
        RenderMaterial rendermaterial = modelBlockIn.resolveTextureName("particle");
        TextureAtlasSprite textureatlassprite = rendermaterial.getSprite();
        SimpleBakedModel.Builder simplebakedmodel$builder = (new SimpleBakedModel.Builder(modelBlockIn, ItemOverrideList.EMPTY, false)).setTexture(textureatlassprite);

        for (BlockPart blockpart : modelBlockIn.getElements())
        {
            for (Direction direction : blockpart.mapFaces.keySet())
            {
                BlockPartFace blockpartface = blockpart.mapFaces.get(direction);

                if (!useTint)
                {
                    blockpartface = new BlockPartFace(blockpartface.cullFace, -1, blockpartface.texture, blockpartface.blockFaceUV);
                }

                RenderMaterial rendermaterial1 = modelBlockIn.resolveTextureName(blockpartface.texture);
                TextureAtlasSprite textureatlassprite1 = rendermaterial1.getSprite();
                BakedQuad bakedquad = makeBakedQuad(blockpart, blockpartface, textureatlassprite1, direction, modelrotation);

                if (blockpartface.cullFace == null)
                {
                    simplebakedmodel$builder.addGeneralQuad(bakedquad);
                }
                else
                {
                    simplebakedmodel$builder.addFaceQuad(Direction.rotateFace(modelrotation.getRotation().getMatrix(), blockpartface.cullFace), bakedquad);
                }
            }
        }

        return simplebakedmodel$builder.build();
    }

    private static BakedQuad makeBakedQuad(BlockPart blockPart, BlockPartFace blockPartFace, TextureAtlasSprite textureAtlasSprite, Direction enumFacing, ModelRotation modelRotation)
    {
        FaceBakery facebakery = new FaceBakery();
        return facebakery.bakeQuad(blockPart.positionFrom, blockPart.positionTo, blockPartFace, textureAtlasSprite, enumFacing, modelRotation, blockPart.partRotation, blockPart.shade, textureAtlasSprite.getName());
    }

    public String toString()
    {
        return "" + this.basePath + "/" + this.name + ", type: " + this.type + ", items: [" + Config.arrayToString(this.items) + "], textture: " + this.texture;
    }

    public float getTextureWidth(TextureManager textureManager)
    {
        if (this.textureWidth <= 0)
        {
            if (this.textureLocation != null)
            {
                Texture texture = textureManager.getTexture(this.textureLocation);
                int i = texture.getGlTextureId();
                int j = GlStateManager.getBoundTexture();
                GlStateManager.bindTexture(i);
                this.textureWidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
                GlStateManager.bindTexture(j);
            }

            if (this.textureWidth <= 0)
            {
                this.textureWidth = 16;
            }
        }

        return (float)this.textureWidth;
    }

    public float getTextureHeight(TextureManager textureManager)
    {
        if (this.textureHeight <= 0)
        {
            if (this.textureLocation != null)
            {
                Texture texture = textureManager.getTexture(this.textureLocation);
                int i = texture.getGlTextureId();
                int j = GlStateManager.getBoundTexture();
                GlStateManager.bindTexture(i);
                this.textureHeight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
                GlStateManager.bindTexture(j);
            }

            if (this.textureHeight <= 0)
            {
                this.textureHeight = 16;
            }
        }

        return (float)this.textureHeight;
    }

    public IBakedModel getBakedModel(ResourceLocation modelLocation, boolean fullModel)
    {
        IBakedModel ibakedmodel;
        Map<String, IBakedModel> map;

        if (fullModel)
        {
            ibakedmodel = this.bakedModelFull;
            map = this.mapBakedModelsFull;
        }
        else
        {
            ibakedmodel = this.bakedModelTexture;
            map = this.mapBakedModelsTexture;
        }

        if (modelLocation != null && map != null)
        {
            String s = modelLocation.getPath();
            IBakedModel ibakedmodel1 = map.get(s);

            if (ibakedmodel1 != null)
            {
                return ibakedmodel1;
            }
        }

        return ibakedmodel;
    }

    public void loadModels(ModelBakery modelBakery)
    {
        if (this.model != null)
        {
            loadItemModel(modelBakery, this.model);
        }

        if (this.type == 1 && this.mapModels != null)
        {
            for (String s : this.mapModels.keySet())
            {
                String s1 = this.mapModels.get(s);
                String s2 = StrUtils.removePrefix(s, "model.");

                if (this.isSubTexture(s2))
                {
                    loadItemModel(modelBakery, s1);
                }
            }
        }
    }

    public void updateModelsFull()
    {
        ModelManager modelmanager = Config.getModelManager();
        IBakedModel ibakedmodel = modelmanager.getMissingModel();

        if (this.model != null)
        {
            ResourceLocation resourcelocation = getModelLocation(this.model);
            ModelResourceLocation modelresourcelocation = new ModelResourceLocation(resourcelocation, "inventory");
            this.bakedModelFull = modelmanager.getModel(modelresourcelocation);

            if (this.bakedModelFull == ibakedmodel)
            {
                Config.warn("Custom Items: Model not found " + modelresourcelocation.getPath());
                this.bakedModelFull = null;
            }
        }

        if (this.type == 1 && this.mapModels != null)
        {
            for (String s : this.mapModels.keySet())
            {
                String s1 = this.mapModels.get(s);
                String s2 = StrUtils.removePrefix(s, "model.");

                if (this.isSubTexture(s2))
                {
                    ResourceLocation resourcelocation1 = getModelLocation(s1);
                    ModelResourceLocation modelresourcelocation1 = new ModelResourceLocation(resourcelocation1, "inventory");
                    IBakedModel ibakedmodel1 = modelmanager.getModel(modelresourcelocation1);

                    if (ibakedmodel1 == ibakedmodel)
                    {
                        Config.warn("Custom Items: Model not found " + modelresourcelocation1.getPath());
                    }
                    else
                    {
                        if (this.mapBakedModelsFull == null)
                        {
                            this.mapBakedModelsFull = new HashMap<>();
                        }

                        String s3 = "item/" + s2;
                        this.mapBakedModelsFull.put(s3, ibakedmodel1);
                    }
                }
            }
        }
    }

    private static void loadItemModel(ModelBakery modelBakery, String model)
    {
        ResourceLocation resourcelocation = getModelLocation(model);
        ModelResourceLocation modelresourcelocation = new ModelResourceLocation(resourcelocation, "inventory");
        modelBakery.loadTopModel(modelresourcelocation);
    }

    private static ResourceLocation getModelLocation(String modelName)
    {
        return new ResourceLocation(modelName);
    }
}
