package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.WoodType;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TrappedChestTileEntity;
import net.minecraft.util.ResourceLocation;

public class Atlases
{
    public static final ResourceLocation SHULKER_BOX_ATLAS = new ResourceLocation("textures/atlas/shulker_boxes.png");
    public static final ResourceLocation BED_ATLAS = new ResourceLocation("textures/atlas/beds.png");
    public static final ResourceLocation BANNER_ATLAS = new ResourceLocation("textures/atlas/banner_patterns.png");
    public static final ResourceLocation SHIELD_ATLAS = new ResourceLocation("textures/atlas/shield_patterns.png");
    public static final ResourceLocation SIGN_ATLAS = new ResourceLocation("textures/atlas/signs.png");
    public static final ResourceLocation CHEST_ATLAS = new ResourceLocation("textures/atlas/chest.png");
    private static final RenderType SHULKER_BOX_TYPE = RenderType.getEntityCutoutNoCull(SHULKER_BOX_ATLAS);
    private static final RenderType BED_TYPE = RenderType.getEntitySolid(BED_ATLAS);
    private static final RenderType BANNER_TYPE = RenderType.getEntityNoOutline(BANNER_ATLAS);
    private static final RenderType SHIELD_TYPE = RenderType.getEntityNoOutline(SHIELD_ATLAS);
    private static final RenderType SIGN_TYPE = RenderType.getEntityCutoutNoCull(SIGN_ATLAS);
    private static final RenderType CHEST_TYPE = RenderType.getEntityCutout(CHEST_ATLAS);
    private static final RenderType SOLID_BLOCK_TYPE = RenderType.getEntitySolid(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
    private static final RenderType CUTOUT_BLOCK_TYPE = RenderType.getEntityCutout(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
    private static final RenderType ITEM_ENTITY_TRANSLUCENT_CULL_BLOCK_TYPE = RenderType.getItemEntityTranslucentCull(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
    private static final RenderType TRANSLUCENT_CULL_BLOCK_TYPE = RenderType.getEntityTranslucentCull(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
    public static final RenderMaterial DEFAULT_SHULKER_TEXTURE = new RenderMaterial(SHULKER_BOX_ATLAS, new ResourceLocation("entity/shulker/shulker"));
    public static final List<RenderMaterial> SHULKER_TEXTURES = Stream.of("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black").map((shulkerColor) ->
    {
        return new RenderMaterial(SHULKER_BOX_ATLAS, new ResourceLocation("entity/shulker/shulker_" + shulkerColor));
    }).collect(ImmutableList.toImmutableList());
    public static final Map<WoodType, RenderMaterial> SIGN_MATERIALS = WoodType.getValues().collect(Collectors.toMap(Function.identity(), Atlases::getSignMaterial));
    public static final RenderMaterial[] BED_TEXTURES = Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map((color) ->
    {
        return new RenderMaterial(BED_ATLAS, new ResourceLocation("entity/bed/" + color.getTranslationKey()));
    }).toArray((renderMaterialID) ->
    {
        return new RenderMaterial[renderMaterialID];
    });
    public static final RenderMaterial CHEST_TRAPPED_MATERIAL = getChestMaterial("trapped");
    public static final RenderMaterial CHEST_TRAPPED_LEFT_MATERIAL = getChestMaterial("trapped_left");
    public static final RenderMaterial CHEST_TRAPPED_RIGHT_MATERIAL = getChestMaterial("trapped_right");
    public static final RenderMaterial CHEST_XMAS_MATERIAL = getChestMaterial("christmas");
    public static final RenderMaterial CHEST_XMAS_LEFT_MATERIAL = getChestMaterial("christmas_left");
    public static final RenderMaterial CHEST_XMAS_RIGHT_MATERIAL = getChestMaterial("christmas_right");
    public static final RenderMaterial CHEST_MATERIAL = getChestMaterial("normal");
    public static final RenderMaterial CHEST_LEFT_MATERIAL = getChestMaterial("normal_left");
    public static final RenderMaterial CHEST_RIGHT_MATERIAL = getChestMaterial("normal_right");
    public static final RenderMaterial ENDER_CHEST_MATERIAL = getChestMaterial("ender");

    public static RenderType getBannerType()
    {
        return BANNER_TYPE;
    }

    public static RenderType getShieldType()
    {
        return SHIELD_TYPE;
    }

    public static RenderType getBedType()
    {
        return BED_TYPE;
    }

    public static RenderType getShulkerBoxType()
    {
        return SHULKER_BOX_TYPE;
    }

    public static RenderType getSignType()
    {
        return SIGN_TYPE;
    }

    public static RenderType getChestType()
    {
        return CHEST_TYPE;
    }

    public static RenderType getSolidBlockType()
    {
        return SOLID_BLOCK_TYPE;
    }

    public static RenderType getCutoutBlockType()
    {
        return CUTOUT_BLOCK_TYPE;
    }

    public static RenderType getItemEntityTranslucentCullType()
    {
        return ITEM_ENTITY_TRANSLUCENT_CULL_BLOCK_TYPE;
    }

    public static RenderType getTranslucentCullBlockType()
    {
        return TRANSLUCENT_CULL_BLOCK_TYPE;
    }

    public static void collectAllMaterials(Consumer<RenderMaterial> materialConsumer)
    {
        materialConsumer.accept(DEFAULT_SHULKER_TEXTURE);
        SHULKER_TEXTURES.forEach(materialConsumer);

        for (BannerPattern bannerpattern : BannerPattern.values())
        {
            materialConsumer.accept(new RenderMaterial(BANNER_ATLAS, bannerpattern.getTextureLocation(true)));
            materialConsumer.accept(new RenderMaterial(SHIELD_ATLAS, bannerpattern.getTextureLocation(false)));
        }

        SIGN_MATERIALS.values().forEach(materialConsumer);

        for (RenderMaterial rendermaterial : BED_TEXTURES)
        {
            materialConsumer.accept(rendermaterial);
        }

        materialConsumer.accept(CHEST_TRAPPED_MATERIAL);
        materialConsumer.accept(CHEST_TRAPPED_LEFT_MATERIAL);
        materialConsumer.accept(CHEST_TRAPPED_RIGHT_MATERIAL);
        materialConsumer.accept(CHEST_XMAS_MATERIAL);
        materialConsumer.accept(CHEST_XMAS_LEFT_MATERIAL);
        materialConsumer.accept(CHEST_XMAS_RIGHT_MATERIAL);
        materialConsumer.accept(CHEST_MATERIAL);
        materialConsumer.accept(CHEST_LEFT_MATERIAL);
        materialConsumer.accept(CHEST_RIGHT_MATERIAL);
        materialConsumer.accept(ENDER_CHEST_MATERIAL);
    }

    public static RenderMaterial getSignMaterial(WoodType woodType)
    {
        return new RenderMaterial(SIGN_ATLAS, new ResourceLocation("entity/signs/" + woodType.getName()));
    }

    private static RenderMaterial getChestMaterial(String chestName)
    {
        return new RenderMaterial(CHEST_ATLAS, new ResourceLocation("entity/chest/" + chestName));
    }

    public static RenderMaterial getChestMaterial(TileEntity tileEntity, ChestType chestType, boolean holiday)
    {
        if (holiday)
        {
            return getChestMaterial(chestType, CHEST_XMAS_MATERIAL, CHEST_XMAS_LEFT_MATERIAL, CHEST_XMAS_RIGHT_MATERIAL);
        }
        else if (tileEntity instanceof TrappedChestTileEntity)
        {
            return getChestMaterial(chestType, CHEST_TRAPPED_MATERIAL, CHEST_TRAPPED_LEFT_MATERIAL, CHEST_TRAPPED_RIGHT_MATERIAL);
        }
        else
        {
            return tileEntity instanceof EnderChestTileEntity ? ENDER_CHEST_MATERIAL : getChestMaterial(chestType, CHEST_MATERIAL, CHEST_LEFT_MATERIAL, CHEST_RIGHT_MATERIAL);
        }
    }

    private static RenderMaterial getChestMaterial(ChestType chestType, RenderMaterial doubleMaterial, RenderMaterial leftMaterial, RenderMaterial rightMaterial)
    {
        switch (chestType)
        {
            case LEFT:
                return leftMaterial;

            case RIGHT:
                return rightMaterial;

            case SINGLE:
            default:
                return doubleMaterial;
        }
    }
}
