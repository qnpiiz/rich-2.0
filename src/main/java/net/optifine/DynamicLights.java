package net.optifine;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.optifine.config.ConnectedParser;
import net.optifine.config.EntityTypeNameLocator;
import net.optifine.config.IObjectLocator;
import net.optifine.config.ItemLocator;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.util.PropertiesOrdered;

public class DynamicLights
{
    private static DynamicLightsMap mapDynamicLights = new DynamicLightsMap();
    private static Map<String, Integer> mapEntityLightLevels = new HashMap<>();
    private static Map<Item, Integer> mapItemLightLevels = new HashMap<>();
    private static long timeUpdateMs = 0L;
    private static final double MAX_DIST = 7.5D;
    private static final double MAX_DIST_SQ = 56.25D;
    private static final int LIGHT_LEVEL_MAX = 15;
    private static final int LIGHT_LEVEL_FIRE = 15;
    private static final int LIGHT_LEVEL_BLAZE = 10;
    private static final int LIGHT_LEVEL_MAGMA_CUBE = 8;
    private static final int LIGHT_LEVEL_MAGMA_CUBE_CORE = 13;
    private static final int LIGHT_LEVEL_GLOWSTONE_DUST = 8;
    private static final int LIGHT_LEVEL_PRISMARINE_CRYSTALS = 8;
    private static final DataParameter<ItemStack> PARAMETER_ITEM_STACK = (DataParameter)Reflector.EntityItem_ITEM.getValue();
    private static boolean initialized;

    public static void entityAdded(Entity entityIn, WorldRenderer renderGlobal)
    {
    }

    public static void entityRemoved(Entity entityIn, WorldRenderer renderGlobal)
    {
        synchronized (mapDynamicLights)
        {
            DynamicLight dynamiclight = mapDynamicLights.remove(entityIn.getEntityId());

            if (dynamiclight != null)
            {
                dynamiclight.updateLitChunks(renderGlobal);
            }
        }
    }

    public static void update(WorldRenderer renderGlobal)
    {
        long i = System.currentTimeMillis();

        if (i >= timeUpdateMs + 50L)
        {
            timeUpdateMs = i;

            if (!initialized)
            {
                initialize();
            }

            synchronized (mapDynamicLights)
            {
                updateMapDynamicLights(renderGlobal);

                if (mapDynamicLights.size() > 0)
                {
                    List<DynamicLight> list = mapDynamicLights.valueList();

                    for (int j = 0; j < list.size(); ++j)
                    {
                        DynamicLight dynamiclight = list.get(j);
                        dynamiclight.update(renderGlobal);
                    }
                }
            }
        }
    }

    private static void initialize()
    {
        initialized = true;
        mapEntityLightLevels.clear();
        mapItemLightLevels.clear();
        String[] astring = ReflectorForge.getForgeModIds();

        for (int i = 0; i < astring.length; ++i)
        {
            String s = astring[i];

            try
            {
                ResourceLocation resourcelocation = new ResourceLocation(s, "optifine/dynamic_lights.properties");
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                loadModConfiguration(inputstream, resourcelocation.toString(), s);
            }
            catch (IOException ioexception)
            {
            }
        }

        if (mapEntityLightLevels.size() > 0)
        {
            Config.dbg("DynamicLights entities: " + mapEntityLightLevels.size());
        }

        if (mapItemLightLevels.size() > 0)
        {
            Config.dbg("DynamicLights items: " + mapItemLightLevels.size());
        }
    }

    private static void loadModConfiguration(InputStream in, String path, String modId)
    {
        if (in != null)
        {
            try
            {
                Properties properties = new PropertiesOrdered();
                properties.load(in);
                in.close();
                Config.dbg("DynamicLights: Parsing " + path);
                ConnectedParser connectedparser = new ConnectedParser("DynamicLights");
                loadModLightLevels(properties.getProperty("entities"), mapEntityLightLevels, new EntityTypeNameLocator(), connectedparser, path, modId);
                loadModLightLevels(properties.getProperty("items"), mapItemLightLevels, new ItemLocator(), connectedparser, path, modId);
            }
            catch (IOException ioexception)
            {
                Config.warn("DynamicLights: Error reading " + path);
            }
        }
    }

    private static <T> void loadModLightLevels(String prop, Map<T, Integer> mapLightLevels, IObjectLocator<T> ol, ConnectedParser cp, String path, String modId)
    {
        if (prop != null)
        {
            String[] astring = Config.tokenize(prop, " ");

            for (int i = 0; i < astring.length; ++i)
            {
                String s = astring[i];
                String[] astring1 = Config.tokenize(s, ":");

                if (astring1.length != 2)
                {
                    cp.warn("Invalid entry: " + s + ", in:" + path);
                }
                else
                {
                    String s1 = astring1[0];
                    String s2 = astring1[1];
                    String s3 = modId + ":" + s1;
                    ResourceLocation resourcelocation = new ResourceLocation(s3);
                    T t = ol.getObject(resourcelocation);

                    if (t == null)
                    {
                        cp.warn("Object not found: " + s3);
                    }
                    else
                    {
                        int j = cp.parseInt(s2, -1);

                        if (j >= 0 && j <= 15)
                        {
                            mapLightLevels.put(t, new Integer(j));
                        }
                        else
                        {
                            cp.warn("Invalid light level: " + s);
                        }
                    }
                }
            }
        }
    }

    private static void updateMapDynamicLights(WorldRenderer renderGlobal)
    {
        ClientWorld clientworld = renderGlobal.getWorld();

        if (clientworld != null)
        {
            for (Entity entity : clientworld.getAllEntities())
            {
                int i = getLightLevel(entity);

                if (i > 0)
                {
                    int j = entity.getEntityId();
                    DynamicLight dynamiclight = mapDynamicLights.get(j);

                    if (dynamiclight == null)
                    {
                        dynamiclight = new DynamicLight(entity);
                        mapDynamicLights.put(j, dynamiclight);
                    }
                }
                else
                {
                    int k = entity.getEntityId();
                    DynamicLight dynamiclight1 = mapDynamicLights.remove(k);

                    if (dynamiclight1 != null)
                    {
                        dynamiclight1.updateLitChunks(renderGlobal);
                    }
                }
            }
        }
    }

    public static int getCombinedLight(BlockPos pos, int combinedLight)
    {
        double d0 = getLightLevel(pos);
        return getCombinedLight(d0, combinedLight);
    }

    public static int getCombinedLight(Entity entity, int combinedLight)
    {
        double d0 = getLightLevel(entity.getPosition());

        if (entity == Config.getMinecraft().player)
        {
            double d1 = (double)getLightLevel(entity);
            d0 = Math.max(d0, d1);
        }

        return getCombinedLight(d0, combinedLight);
    }

    public static int getCombinedLight(double lightPlayer, int combinedLight)
    {
        if (lightPlayer > 0.0D)
        {
            int i = (int)(lightPlayer * 16.0D);
            int j = combinedLight & 255;

            if (i > j)
            {
                combinedLight = combinedLight & -256;
                combinedLight = combinedLight | i;
            }
        }

        return combinedLight;
    }

    public static double getLightLevel(BlockPos pos)
    {
        double d0 = 0.0D;

        synchronized (mapDynamicLights)
        {
            List<DynamicLight> list = mapDynamicLights.valueList();
            int i = list.size();

            for (int j = 0; j < i; ++j)
            {
                DynamicLight dynamiclight = list.get(j);
                int k = dynamiclight.getLastLightLevel();

                if (k > 0)
                {
                    double d1 = dynamiclight.getLastPosX();
                    double d2 = dynamiclight.getLastPosY();
                    double d3 = dynamiclight.getLastPosZ();
                    double d4 = (double)pos.getX() - d1;
                    double d5 = (double)pos.getY() - d2;
                    double d6 = (double)pos.getZ() - d3;
                    double d7 = d4 * d4 + d5 * d5 + d6 * d6;

                    if (!(d7 > 56.25D))
                    {
                        double d8 = Math.sqrt(d7);
                        double d9 = 1.0D - d8 / 7.5D;
                        double d10 = d9 * (double)k;

                        if (d10 > d0)
                        {
                            d0 = d10;
                        }
                    }
                }
            }
        }

        return Config.limit(d0, 0.0D, 15.0D);
    }

    public static int getLightLevel(ItemStack itemStack)
    {
        if (itemStack == null)
        {
            return 0;
        }
        else
        {
            Item item = itemStack.getItem();

            if (item instanceof BlockItem)
            {
                BlockItem blockitem = (BlockItem)item;
                Block block = blockitem.getBlock();

                if (block != null)
                {
                    return block.getDefaultState().getLightValue();
                }
            }

            if (item == Items.LAVA_BUCKET)
            {
                return Blocks.LAVA.getDefaultState().getLightValue();
            }
            else if (item != Items.BLAZE_ROD && item != Items.BLAZE_POWDER)
            {
                if (item == Items.GLOWSTONE_DUST)
                {
                    return 8;
                }
                else if (item == Items.PRISMARINE_CRYSTALS)
                {
                    return 8;
                }
                else if (item == Items.MAGMA_CREAM)
                {
                    return 8;
                }
                else if (item == Items.NETHER_STAR)
                {
                    return Blocks.BEACON.getDefaultState().getLightValue() / 2;
                }
                else
                {
                    if (!mapItemLightLevels.isEmpty())
                    {
                        Integer integer = mapItemLightLevels.get(item);

                        if (integer != null)
                        {
                            return integer;
                        }
                    }

                    return 0;
                }
            }
            else
            {
                return 10;
            }
        }
    }

    public static int getLightLevel(Entity entity)
    {
        if (entity == Config.getMinecraft().getRenderViewEntity() && !Config.isDynamicHandLight())
        {
            return 0;
        }
        else
        {
            if (entity instanceof PlayerEntity)
            {
                PlayerEntity playerentity = (PlayerEntity)entity;

                if (playerentity.isSpectator())
                {
                    return 0;
                }
            }

            if (entity.isBurning())
            {
                return 15;
            }
            else
            {
                if (!mapEntityLightLevels.isEmpty())
                {
                    String s = EntityTypeNameLocator.getEntityTypeName(entity);
                    Integer integer = mapEntityLightLevels.get(s);

                    if (integer != null)
                    {
                        return integer;
                    }
                }

                if (entity instanceof DamagingProjectileEntity)
                {
                    return 15;
                }
                else if (entity instanceof TNTEntity)
                {
                    return 15;
                }
                else if (entity instanceof BlazeEntity)
                {
                    BlazeEntity blazeentity = (BlazeEntity)entity;
                    return blazeentity.isBurning() ? 15 : 10;
                }
                else if (entity instanceof MagmaCubeEntity)
                {
                    MagmaCubeEntity magmacubeentity = (MagmaCubeEntity)entity;
                    return (double)magmacubeentity.squishFactor > 0.6D ? 13 : 8;
                }
                else
                {
                    if (entity instanceof CreeperEntity)
                    {
                        CreeperEntity creeperentity = (CreeperEntity)entity;

                        if ((double)creeperentity.getCreeperFlashIntensity(0.0F) > 0.001D)
                        {
                            return 15;
                        }
                    }

                    if (entity instanceof LivingEntity)
                    {
                        LivingEntity livingentity = (LivingEntity)entity;
                        ItemStack itemstack3 = livingentity.getHeldItemMainhand();
                        int i = getLightLevel(itemstack3);
                        ItemStack itemstack = livingentity.getHeldItemOffhand();
                        int j = getLightLevel(itemstack);
                        ItemStack itemstack1 = livingentity.getItemStackFromSlot(EquipmentSlotType.HEAD);
                        int k = getLightLevel(itemstack1);
                        int l = Math.max(i, j);
                        return Math.max(l, k);
                    }
                    else if (entity instanceof ItemEntity)
                    {
                        ItemEntity itementity = (ItemEntity)entity;
                        ItemStack itemstack2 = getItemStack(itementity);
                        return getLightLevel(itemstack2);
                    }
                    else
                    {
                        return 0;
                    }
                }
            }
        }
    }

    public static void removeLights(WorldRenderer renderGlobal)
    {
        synchronized (mapDynamicLights)
        {
            List<DynamicLight> list = mapDynamicLights.valueList();

            for (int i = 0; i < list.size(); ++i)
            {
                DynamicLight dynamiclight = list.get(i);
                dynamiclight.updateLitChunks(renderGlobal);
            }

            mapDynamicLights.clear();
        }
    }

    public static void clear()
    {
        synchronized (mapDynamicLights)
        {
            mapDynamicLights.clear();
        }
    }

    public static int getCount()
    {
        synchronized (mapDynamicLights)
        {
            return mapDynamicLights.size();
        }
    }

    public static ItemStack getItemStack(ItemEntity entityItem)
    {
        return entityItem.getDataManager().get(PARAMETER_ITEM_STACK);
    }
}
