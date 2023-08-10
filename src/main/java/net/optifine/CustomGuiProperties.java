package net.optifine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Properties;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.passive.horse.MuleEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.DropperTileEntity;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TrappedChestTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.optifine.config.BiomeId;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchProfession;
import net.optifine.config.Matches;
import net.optifine.config.NbtTagValue;
import net.optifine.config.RangeListInt;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;

public class CustomGuiProperties
{
    private String fileName = null;
    private String basePath = null;
    private CustomGuiProperties.EnumContainer container = null;
    private Map<ResourceLocation, ResourceLocation> textureLocations = null;
    private NbtTagValue nbtName = null;
    private BiomeId[] biomes = null;
    private RangeListInt heights = null;
    private Boolean large = null;
    private Boolean trapped = null;
    private Boolean christmas = null;
    private Boolean ender = null;
    private RangeListInt levels = null;
    private MatchProfession[] professions = null;
    private CustomGuiProperties.EnumVariant[] variants = null;
    private DyeColor[] colors = null;
    private static final CustomGuiProperties.EnumVariant[] VARIANTS_HORSE = new CustomGuiProperties.EnumVariant[] {CustomGuiProperties.EnumVariant.HORSE, CustomGuiProperties.EnumVariant.DONKEY, CustomGuiProperties.EnumVariant.MULE, CustomGuiProperties.EnumVariant.LLAMA};
    private static final CustomGuiProperties.EnumVariant[] VARIANTS_DISPENSER = new CustomGuiProperties.EnumVariant[] {CustomGuiProperties.EnumVariant.DISPENSER, CustomGuiProperties.EnumVariant.DROPPER};
    private static final CustomGuiProperties.EnumVariant[] VARIANTS_INVALID = new CustomGuiProperties.EnumVariant[0];
    private static final DyeColor[] COLORS_INVALID = new DyeColor[0];
    private static final ResourceLocation ANVIL_GUI_TEXTURE = new ResourceLocation("textures/gui/container/anvil.png");
    private static final ResourceLocation BEACON_GUI_TEXTURE = new ResourceLocation("textures/gui/container/beacon.png");
    private static final ResourceLocation BREWING_STAND_GUI_TEXTURE = new ResourceLocation("textures/gui/container/brewing_stand.png");
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/crafting_table.png");
    private static final ResourceLocation HORSE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/horse.png");
    private static final ResourceLocation DISPENSER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/dispenser.png");
    private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");
    private static final ResourceLocation FURNACE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/furnace.png");
    private static final ResourceLocation HOPPER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/hopper.png");
    private static final ResourceLocation INVENTORY_GUI_TEXTURE = new ResourceLocation("textures/gui/container/inventory.png");
    private static final ResourceLocation SHULKER_BOX_GUI_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static final ResourceLocation VILLAGER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager2.png");

    public CustomGuiProperties(Properties props, String path)
    {
        ConnectedParser connectedparser = new ConnectedParser("CustomGuis");
        this.fileName = connectedparser.parseName(path);
        this.basePath = connectedparser.parseBasePath(path);
        this.container = (CustomGuiProperties.EnumContainer)connectedparser.parseEnum(props.getProperty("container"), CustomGuiProperties.EnumContainer.values(), "container");
        this.textureLocations = parseTextureLocations(props, "texture", this.container, "textures/gui/", this.basePath);
        this.nbtName = connectedparser.parseNbtTagValue("name", props.getProperty("name"));
        this.biomes = connectedparser.parseBiomes(props.getProperty("biomes"));
        this.heights = connectedparser.parseRangeListInt(props.getProperty("heights"));
        this.large = connectedparser.parseBooleanObject(props.getProperty("large"));
        this.trapped = connectedparser.parseBooleanObject(props.getProperty("trapped"));
        this.christmas = connectedparser.parseBooleanObject(props.getProperty("christmas"));
        this.ender = connectedparser.parseBooleanObject(props.getProperty("ender"));
        this.levels = connectedparser.parseRangeListInt(props.getProperty("levels"));
        this.professions = connectedparser.parseProfessions(props.getProperty("professions"));
        CustomGuiProperties.EnumVariant[] acustomguiproperties$enumvariant = getContainerVariants(this.container);
        this.variants = (CustomGuiProperties.EnumVariant[])connectedparser.parseEnums(props.getProperty("variants"), acustomguiproperties$enumvariant, "variants", VARIANTS_INVALID);
        this.colors = parseEnumDyeColors(props.getProperty("colors"));
    }

    private static CustomGuiProperties.EnumVariant[] getContainerVariants(CustomGuiProperties.EnumContainer cont)
    {
        if (cont == CustomGuiProperties.EnumContainer.HORSE)
        {
            return VARIANTS_HORSE;
        }
        else
        {
            return cont == CustomGuiProperties.EnumContainer.DISPENSER ? VARIANTS_DISPENSER : new CustomGuiProperties.EnumVariant[0];
        }
    }

    private static DyeColor[] parseEnumDyeColors(String str)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            str = str.toLowerCase();
            String[] astring = Config.tokenize(str, " ");
            DyeColor[] adyecolor = new DyeColor[astring.length];

            for (int i = 0; i < astring.length; ++i)
            {
                String s = astring[i];
                DyeColor dyecolor = parseEnumDyeColor(s);

                if (dyecolor == null)
                {
                    warn("Invalid color: " + s);
                    return COLORS_INVALID;
                }

                adyecolor[i] = dyecolor;
            }

            return adyecolor;
        }
    }

    private static DyeColor parseEnumDyeColor(String str)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            DyeColor[] adyecolor = DyeColor.values();

            for (int i = 0; i < adyecolor.length; ++i)
            {
                DyeColor dyecolor = adyecolor[i];

                if (dyecolor.getString().equals(str))
                {
                    return dyecolor;
                }

                if (dyecolor.getTranslationKey().equals(str))
                {
                    return dyecolor;
                }
            }

            return null;
        }
    }

    private static ResourceLocation parseTextureLocation(String str, String basePath)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            str = str.trim();
            String s = TextureUtils.fixResourcePath(str, basePath);

            if (!s.endsWith(".png"))
            {
                s = s + ".png";
            }

            return new ResourceLocation(basePath + "/" + s);
        }
    }

    private static Map<ResourceLocation, ResourceLocation> parseTextureLocations(Properties props, String property, CustomGuiProperties.EnumContainer container, String pathPrefix, String basePath)
    {
        Map<ResourceLocation, ResourceLocation> map = new HashMap<>();
        String s = props.getProperty(property);

        if (s != null)
        {
            ResourceLocation resourcelocation = getGuiTextureLocation(container);
            ResourceLocation resourcelocation1 = parseTextureLocation(s, basePath);

            if (resourcelocation != null && resourcelocation1 != null)
            {
                map.put(resourcelocation, resourcelocation1);
            }
        }

        String s5 = property + ".";

        for (String s1 : (Set<String>)(Set<?>)props.keySet())
        {
            if (s1.startsWith(s5))
            {
                String s2 = s1.substring(s5.length());
                s2 = s2.replace('\\', '/');
                s2 = StrUtils.removePrefixSuffix(s2, "/", ".png");
                String s3 = pathPrefix + s2 + ".png";
                String s4 = props.getProperty(s1);
                ResourceLocation resourcelocation2 = new ResourceLocation(s3);
                ResourceLocation resourcelocation3 = parseTextureLocation(s4, basePath);
                map.put(resourcelocation2, resourcelocation3);
            }
        }

        return map;
    }

    private static ResourceLocation getGuiTextureLocation(CustomGuiProperties.EnumContainer container)
    {
        if (container == null)
        {
            return null;
        }
        else
        {
            switch (container)
            {
                case ANVIL:
                    return ANVIL_GUI_TEXTURE;

                case BEACON:
                    return BEACON_GUI_TEXTURE;

                case BREWING_STAND:
                    return BREWING_STAND_GUI_TEXTURE;

                case CHEST:
                    return CHEST_GUI_TEXTURE;

                case CRAFTING:
                    return CRAFTING_TABLE_GUI_TEXTURE;

                case CREATIVE:
                    return null;

                case DISPENSER:
                    return DISPENSER_GUI_TEXTURE;

                case ENCHANTMENT:
                    return ENCHANTMENT_TABLE_GUI_TEXTURE;

                case FURNACE:
                    return FURNACE_GUI_TEXTURE;

                case HOPPER:
                    return HOPPER_GUI_TEXTURE;

                case HORSE:
                    return HORSE_GUI_TEXTURE;

                case INVENTORY:
                    return INVENTORY_GUI_TEXTURE;

                case SHULKER_BOX:
                    return SHULKER_BOX_GUI_TEXTURE;

                case VILLAGER:
                    return VILLAGER_GUI_TEXTURE;

                default:
                    return null;
            }
        }
    }

    public boolean isValid(String path)
    {
        if (this.fileName != null && this.fileName.length() > 0)
        {
            if (this.basePath == null)
            {
                warn("No base path found: " + path);
                return false;
            }
            else if (this.container == null)
            {
                warn("No container found: " + path);
                return false;
            }
            else if (this.textureLocations.isEmpty())
            {
                warn("No texture found: " + path);
                return false;
            }
            else if (this.professions == ConnectedParser.PROFESSIONS_INVALID)
            {
                warn("Invalid professions or careers: " + path);
                return false;
            }
            else if (this.variants == VARIANTS_INVALID)
            {
                warn("Invalid variants: " + path);
                return false;
            }
            else if (this.colors == COLORS_INVALID)
            {
                warn("Invalid colors: " + path);
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            warn("No name found: " + path);
            return false;
        }
    }

    private static void warn(String str)
    {
        Config.warn("[CustomGuis] " + str);
    }

    private boolean matchesGeneral(CustomGuiProperties.EnumContainer ec, BlockPos pos, IWorldReader blockAccess)
    {
        if (this.container != ec)
        {
            return false;
        }
        else
        {
            if (this.biomes != null)
            {
                Biome biome = blockAccess.getBiome(pos);

                if (!Matches.biome(biome, this.biomes))
                {
                    return false;
                }
            }

            return this.heights == null || this.heights.isInRange(pos.getY());
        }
    }

    public boolean matchesPos(CustomGuiProperties.EnumContainer ec, BlockPos pos, IWorldReader blockAccess, Screen screen)
    {
        if (!this.matchesGeneral(ec, pos, blockAccess))
        {
            return false;
        }
        else
        {
            if (this.nbtName != null)
            {
                String s = getName(screen);

                if (!this.nbtName.matchesValue(s))
                {
                    return false;
                }
            }

            switch (ec)
            {
                case BEACON:
                    return this.matchesBeacon(pos, blockAccess);

                case CHEST:
                    return this.matchesChest(pos, blockAccess);

                case DISPENSER:
                    return this.matchesDispenser(pos, blockAccess);

                case SHULKER_BOX:
                    return this.matchesShulker(pos, blockAccess);

                default:
                    return true;
            }
        }
    }

    public static String getName(Screen screen)
    {
        ITextComponent itextcomponent = screen.getTitle();
        return itextcomponent == null ? null : itextcomponent.getUnformattedComponentText();
    }

    private boolean matchesBeacon(BlockPos pos, IBlockDisplayReader blockAccess)
    {
        TileEntity tileentity = blockAccess.getTileEntity(pos);

        if (!(tileentity instanceof BeaconTileEntity))
        {
            return false;
        }
        else
        {
            BeaconTileEntity beacontileentity = (BeaconTileEntity)tileentity;

            if (this.levels != null)
            {
                int i = beacontileentity.getLevels();

                if (!this.levels.isInRange(i))
                {
                    return false;
                }
            }

            return true;
        }
    }

    private boolean matchesChest(BlockPos pos, IBlockDisplayReader blockAccess)
    {
        TileEntity tileentity = blockAccess.getTileEntity(pos);

        if (tileentity instanceof ChestTileEntity)
        {
            ChestTileEntity chesttileentity = (ChestTileEntity)tileentity;
            return this.matchesChest(chesttileentity, pos, blockAccess);
        }
        else if (tileentity instanceof EnderChestTileEntity)
        {
            EnderChestTileEntity enderchesttileentity = (EnderChestTileEntity)tileentity;
            return this.matchesEnderChest(enderchesttileentity, pos, blockAccess);
        }
        else
        {
            return false;
        }
    }

    private boolean matchesChest(ChestTileEntity tec, BlockPos pos, IBlockDisplayReader blockAccess)
    {
        BlockState blockstate = blockAccess.getBlockState(pos);
        ChestType chesttype = blockstate.hasProperty(ChestBlock.TYPE) ? blockstate.get(ChestBlock.TYPE) : ChestType.SINGLE;
        boolean flag = chesttype != ChestType.SINGLE;
        boolean flag1 = tec instanceof TrappedChestTileEntity;
        boolean flag2 = CustomGuis.isChristmas;
        boolean flag3 = false;
        return this.matchesChest(flag, flag1, flag2, flag3);
    }

    private boolean matchesEnderChest(EnderChestTileEntity teec, BlockPos pos, IBlockDisplayReader blockAccess)
    {
        return this.matchesChest(false, false, false, true);
    }

    private boolean matchesChest(boolean isLarge, boolean isTrapped, boolean isChristmas, boolean isEnder)
    {
        if (this.large != null && this.large != isLarge)
        {
            return false;
        }
        else if (this.trapped != null && this.trapped != isTrapped)
        {
            return false;
        }
        else if (this.christmas != null && this.christmas != isChristmas)
        {
            return false;
        }
        else
        {
            return this.ender == null || this.ender == isEnder;
        }
    }

    private boolean matchesDispenser(BlockPos pos, IBlockDisplayReader blockAccess)
    {
        TileEntity tileentity = blockAccess.getTileEntity(pos);

        if (!(tileentity instanceof DispenserTileEntity))
        {
            return false;
        }
        else
        {
            DispenserTileEntity dispensertileentity = (DispenserTileEntity)tileentity;

            if (this.variants != null)
            {
                CustomGuiProperties.EnumVariant customguiproperties$enumvariant = this.getDispenserVariant(dispensertileentity);

                if (!Config.equalsOne(customguiproperties$enumvariant, this.variants))
                {
                    return false;
                }
            }

            return true;
        }
    }

    private CustomGuiProperties.EnumVariant getDispenserVariant(DispenserTileEntity ted)
    {
        return ted instanceof DropperTileEntity ? CustomGuiProperties.EnumVariant.DROPPER : CustomGuiProperties.EnumVariant.DISPENSER;
    }

    private boolean matchesShulker(BlockPos pos, IBlockDisplayReader blockAccess)
    {
        TileEntity tileentity = blockAccess.getTileEntity(pos);

        if (!(tileentity instanceof ShulkerBoxTileEntity))
        {
            return false;
        }
        else
        {
            ShulkerBoxTileEntity shulkerboxtileentity = (ShulkerBoxTileEntity)tileentity;

            if (this.colors != null)
            {
                DyeColor dyecolor = shulkerboxtileentity.getColor();

                if (!Config.equalsOne(dyecolor, this.colors))
                {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean matchesEntity(CustomGuiProperties.EnumContainer ec, Entity entity, IWorldReader blockAccess)
    {
        if (!this.matchesGeneral(ec, entity.getPosition(), blockAccess))
        {
            return false;
        }
        else
        {
            if (this.nbtName != null)
            {
                String s = entity.getScoreboardName();

                if (!this.nbtName.matchesValue(s))
                {
                    return false;
                }
            }

            switch (ec)
            {
                case HORSE:
                    return this.matchesHorse(entity, blockAccess);

                case VILLAGER:
                    return this.matchesVillager(entity, blockAccess);

                default:
                    return true;
            }
        }
    }

    private boolean matchesVillager(Entity entity, IBlockDisplayReader blockAccess)
    {
        if (!(entity instanceof VillagerEntity))
        {
            return false;
        }
        else
        {
            VillagerEntity villagerentity = (VillagerEntity)entity;

            if (this.professions != null)
            {
                VillagerData villagerdata = villagerentity.getVillagerData();
                VillagerProfession villagerprofession = villagerdata.getProfession();
                int i = villagerdata.getLevel();

                if (!MatchProfession.matchesOne(villagerprofession, i, this.professions))
                {
                    return false;
                }
            }

            return true;
        }
    }

    private boolean matchesHorse(Entity entity, IBlockDisplayReader blockAccess)
    {
        if (!(entity instanceof AbstractHorseEntity))
        {
            return false;
        }
        else
        {
            AbstractHorseEntity abstracthorseentity = (AbstractHorseEntity)entity;

            if (this.variants != null)
            {
                CustomGuiProperties.EnumVariant customguiproperties$enumvariant = this.getHorseVariant(abstracthorseentity);

                if (!Config.equalsOne(customguiproperties$enumvariant, this.variants))
                {
                    return false;
                }
            }

            if (this.colors != null && abstracthorseentity instanceof LlamaEntity)
            {
                LlamaEntity llamaentity = (LlamaEntity)abstracthorseentity;
                DyeColor dyecolor = llamaentity.getColor();

                if (!Config.equalsOne(dyecolor, this.colors))
                {
                    return false;
                }
            }

            return true;
        }
    }

    private CustomGuiProperties.EnumVariant getHorseVariant(AbstractHorseEntity entity)
    {
        if (entity instanceof HorseEntity)
        {
            return CustomGuiProperties.EnumVariant.HORSE;
        }
        else if (entity instanceof DonkeyEntity)
        {
            return CustomGuiProperties.EnumVariant.DONKEY;
        }
        else if (entity instanceof MuleEntity)
        {
            return CustomGuiProperties.EnumVariant.MULE;
        }
        else
        {
            return entity instanceof LlamaEntity ? CustomGuiProperties.EnumVariant.LLAMA : null;
        }
    }

    public CustomGuiProperties.EnumContainer getContainer()
    {
        return this.container;
    }

    public ResourceLocation getTextureLocation(ResourceLocation loc)
    {
        ResourceLocation resourcelocation = this.textureLocations.get(loc);
        return resourcelocation == null ? loc : resourcelocation;
    }

    public String toString()
    {
        return "name: " + this.fileName + ", container: " + this.container + ", textures: " + this.textureLocations;
    }

    public static enum EnumContainer
    {
        ANVIL,
        BEACON,
        BREWING_STAND,
        CHEST,
        CRAFTING,
        DISPENSER,
        ENCHANTMENT,
        FURNACE,
        HOPPER,
        HORSE,
        VILLAGER,
        SHULKER_BOX,
        CREATIVE,
        INVENTORY;
    }

    private static enum EnumVariant
    {
        HORSE,
        DONKEY,
        MULE,
        LLAMA,
        DISPENSER,
        DROPPER;
    }
}
