package net.optifine.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.BlockPartRotation;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.optifine.Config;

public class BlockModelUtils
{
    private static final float VERTEX_COORD_ACCURACY = 1.0E-6F;
    private static final Random RANDOM = new Random(0L);

    public static IBakedModel makeModelCube(String spriteName, int tintIndex)
    {
        TextureAtlasSprite textureatlassprite = Config.getTextureMap().getUploadedSprite(spriteName);
        return makeModelCube(textureatlassprite, tintIndex);
    }

    public static IBakedModel makeModelCube(TextureAtlasSprite sprite, int tintIndex)
    {
        List list = new ArrayList();
        Direction[] adirection = Direction.VALUES;
        Map<Direction, List<BakedQuad>> map = new HashMap<>();

        for (int i = 0; i < adirection.length; ++i)
        {
            Direction direction = adirection[i];
            List list1 = new ArrayList();
            list1.add(makeBakedQuad(direction, sprite, tintIndex));
            map.put(direction, list1);
        }

        ItemOverrideList itemoverridelist = ItemOverrideList.EMPTY;
        IBakedModel ibakedmodel = new SimpleBakedModel(list, map, true, true, true, sprite, ItemCameraTransforms.DEFAULT, itemoverridelist);
        return ibakedmodel;
    }

    public static IBakedModel joinModelsCube(IBakedModel modelBase, IBakedModel modelAdd)
    {
        List<BakedQuad> list = new ArrayList<>();
        list.addAll(modelBase.getQuads((BlockState)null, (Direction)null, RANDOM));
        list.addAll(modelAdd.getQuads((BlockState)null, (Direction)null, RANDOM));
        Direction[] adirection = Direction.VALUES;
        Map<Direction, List<BakedQuad>> map = new HashMap<>();

        for (int i = 0; i < adirection.length; ++i)
        {
            Direction direction = adirection[i];
            List list1 = new ArrayList();
            list1.addAll(modelBase.getQuads((BlockState)null, direction, RANDOM));
            list1.addAll(modelAdd.getQuads((BlockState)null, direction, RANDOM));
            map.put(direction, list1);
        }

        boolean flag = modelBase.isAmbientOcclusion();
        boolean flag1 = modelBase.isBuiltInRenderer();
        TextureAtlasSprite textureatlassprite = modelBase.getParticleTexture();
        ItemCameraTransforms itemcameratransforms = modelBase.getItemCameraTransforms();
        ItemOverrideList itemoverridelist = modelBase.getOverrides();
        IBakedModel ibakedmodel = new SimpleBakedModel(list, map, flag, flag1, true, textureatlassprite, itemcameratransforms, itemoverridelist);
        return ibakedmodel;
    }

    public static BakedQuad makeBakedQuad(Direction facing, TextureAtlasSprite sprite, int tintIndex)
    {
        Vector3f vector3f = new Vector3f(0.0F, 0.0F, 0.0F);
        Vector3f vector3f1 = new Vector3f(16.0F, 16.0F, 16.0F);
        BlockFaceUV blockfaceuv = new BlockFaceUV(new float[] {0.0F, 0.0F, 16.0F, 16.0F}, 0);
        BlockPartFace blockpartface = new BlockPartFace(facing, tintIndex, "#" + facing.getString(), blockfaceuv);
        ModelRotation modelrotation = ModelRotation.X0_Y0;
        BlockPartRotation blockpartrotation = null;
        boolean flag = true;
        ResourceLocation resourcelocation = sprite.getName();
        FaceBakery facebakery = new FaceBakery();
        return facebakery.bakeQuad(vector3f, vector3f1, blockpartface, sprite, facing, modelrotation, blockpartrotation, flag, resourcelocation);
    }

    public static IBakedModel makeModel(String modelName, String spriteOldName, String spriteNewName)
    {
        AtlasTexture atlastexture = Config.getTextureMap();
        TextureAtlasSprite textureatlassprite = atlastexture.getUploadedSprite(spriteOldName);
        TextureAtlasSprite textureatlassprite1 = atlastexture.getUploadedSprite(spriteNewName);
        return makeModel(modelName, textureatlassprite, textureatlassprite1);
    }

    public static IBakedModel makeModel(String modelName, TextureAtlasSprite spriteOld, TextureAtlasSprite spriteNew)
    {
        if (spriteOld != null && spriteNew != null)
        {
            ModelManager modelmanager = Config.getModelManager();

            if (modelmanager == null)
            {
                return null;
            }
            else
            {
                ModelResourceLocation modelresourcelocation = new ModelResourceLocation(modelName, "");
                IBakedModel ibakedmodel = modelmanager.getModel(modelresourcelocation);

                if (ibakedmodel != null && ibakedmodel != modelmanager.getMissingModel())
                {
                    IBakedModel ibakedmodel1 = ModelUtils.duplicateModel(ibakedmodel);
                    Direction[] adirection = Direction.VALUES;

                    for (int i = 0; i < adirection.length; ++i)
                    {
                        Direction direction = adirection[i];
                        List<BakedQuad> list = ibakedmodel1.getQuads((BlockState)null, direction, RANDOM);
                        replaceTexture(list, spriteOld, spriteNew);
                    }

                    List<BakedQuad> list1 = ibakedmodel1.getQuads((BlockState)null, (Direction)null, RANDOM);
                    replaceTexture(list1, spriteOld, spriteNew);
                    return ibakedmodel1;
                }
                else
                {
                    return null;
                }
            }
        }
        else
        {
            return null;
        }
    }

    private static void replaceTexture(List<BakedQuad> quads, TextureAtlasSprite spriteOld, TextureAtlasSprite spriteNew)
    {
        List<BakedQuad> list = new ArrayList<>();

        for (BakedQuad bakedquad : quads)
        {
            if (bakedquad.getSprite() == spriteOld)
            {
                bakedquad = new BakedQuadRetextured(bakedquad, spriteNew);
            }

            list.add(bakedquad);
        }

        quads.clear();
        quads.addAll(list);
    }

    public static void snapVertexPosition(Vector3f pos)
    {
        pos.set(snapVertexCoord(pos.getX()), snapVertexCoord(pos.getY()), snapVertexCoord(pos.getZ()));
    }

    private static float snapVertexCoord(float x)
    {
        if (x > -1.0E-6F && x < 1.0E-6F)
        {
            return 0.0F;
        }
        else
        {
            return x > 0.999999F && x < 1.000001F ? 1.0F : x;
        }
    }

    public static AxisAlignedBB getOffsetBoundingBox(AxisAlignedBB aabb, AbstractBlock.OffsetType offsetType, BlockPos pos)
    {
        int i = pos.getX();
        int j = pos.getZ();
        long k = (long)(i * 3129871) ^ (long)j * 116129781L;
        k = k * k * 42317861L + k * 11L;
        double d0 = ((double)((float)(k >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
        double d1 = ((double)((float)(k >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;
        double d2 = 0.0D;

        if (offsetType == AbstractBlock.OffsetType.XYZ)
        {
            d2 = ((double)((float)(k >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
        }

        return aabb.offset(d0, d2, d1);
    }
}
