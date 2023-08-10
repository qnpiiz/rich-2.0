package net.optifine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.optifine.model.ModelUtils;

public class SmartLeaves
{
    private static IBakedModel modelLeavesCullAcacia = null;
    private static IBakedModel modelLeavesCullBirch = null;
    private static IBakedModel modelLeavesCullDarkOak = null;
    private static IBakedModel modelLeavesCullJungle = null;
    private static IBakedModel modelLeavesCullOak = null;
    private static IBakedModel modelLeavesCullSpruce = null;
    private static List generalQuadsCullAcacia = null;
    private static List generalQuadsCullBirch = null;
    private static List generalQuadsCullDarkOak = null;
    private static List generalQuadsCullJungle = null;
    private static List generalQuadsCullOak = null;
    private static List generalQuadsCullSpruce = null;
    private static IBakedModel modelLeavesDoubleAcacia = null;
    private static IBakedModel modelLeavesDoubleBirch = null;
    private static IBakedModel modelLeavesDoubleDarkOak = null;
    private static IBakedModel modelLeavesDoubleJungle = null;
    private static IBakedModel modelLeavesDoubleOak = null;
    private static IBakedModel modelLeavesDoubleSpruce = null;
    private static final Random RANDOM = new Random();

    public static IBakedModel getLeavesModel(IBakedModel model, BlockState stateIn)
    {
        if (!Config.isTreesSmart())
        {
            return model;
        }
        else
        {
            List list = model.getQuads(stateIn, (Direction)null, RANDOM);

            if (list == generalQuadsCullAcacia)
            {
                return modelLeavesDoubleAcacia;
            }
            else if (list == generalQuadsCullBirch)
            {
                return modelLeavesDoubleBirch;
            }
            else if (list == generalQuadsCullDarkOak)
            {
                return modelLeavesDoubleDarkOak;
            }
            else if (list == generalQuadsCullJungle)
            {
                return modelLeavesDoubleJungle;
            }
            else if (list == generalQuadsCullOak)
            {
                return modelLeavesDoubleOak;
            }
            else
            {
                return list == generalQuadsCullSpruce ? modelLeavesDoubleSpruce : model;
            }
        }
    }

    public static boolean isSameLeaves(BlockState state1, BlockState state2)
    {
        if (state1 == state2)
        {
            return true;
        }
        else
        {
            Block block = state1.getBlock();
            Block block1 = state2.getBlock();
            return block == block1;
        }
    }

    public static void updateLeavesModels()
    {
        List list = new ArrayList();
        modelLeavesCullAcacia = getModelCull("acacia", list);
        modelLeavesCullBirch = getModelCull("birch", list);
        modelLeavesCullDarkOak = getModelCull("dark_oak", list);
        modelLeavesCullJungle = getModelCull("jungle", list);
        modelLeavesCullOak = getModelCull("oak", list);
        modelLeavesCullSpruce = getModelCull("spruce", list);
        generalQuadsCullAcacia = getGeneralQuadsSafe(modelLeavesCullAcacia);
        generalQuadsCullBirch = getGeneralQuadsSafe(modelLeavesCullBirch);
        generalQuadsCullDarkOak = getGeneralQuadsSafe(modelLeavesCullDarkOak);
        generalQuadsCullJungle = getGeneralQuadsSafe(modelLeavesCullJungle);
        generalQuadsCullOak = getGeneralQuadsSafe(modelLeavesCullOak);
        generalQuadsCullSpruce = getGeneralQuadsSafe(modelLeavesCullSpruce);
        modelLeavesDoubleAcacia = getModelDoubleFace(modelLeavesCullAcacia);
        modelLeavesDoubleBirch = getModelDoubleFace(modelLeavesCullBirch);
        modelLeavesDoubleDarkOak = getModelDoubleFace(modelLeavesCullDarkOak);
        modelLeavesDoubleJungle = getModelDoubleFace(modelLeavesCullJungle);
        modelLeavesDoubleOak = getModelDoubleFace(modelLeavesCullOak);
        modelLeavesDoubleSpruce = getModelDoubleFace(modelLeavesCullSpruce);

        if (list.size() > 0)
        {
            Config.dbg("Enable face culling: " + Config.arrayToString(list.toArray()));
        }
    }

    private static List getGeneralQuadsSafe(IBakedModel model)
    {
        return model == null ? null : model.getQuads((BlockState)null, (Direction)null, RANDOM);
    }

    static IBakedModel getModelCull(String type, List updatedTypes)
    {
        ModelManager modelmanager = Config.getModelManager();

        if (modelmanager == null)
        {
            return null;
        }
        else
        {
            ResourceLocation resourcelocation = new ResourceLocation("blockstates/" + type + "_leaves.json");

            if (!Config.isFromDefaultResourcePack(resourcelocation))
            {
                return null;
            }
            else
            {
                ResourceLocation resourcelocation1 = new ResourceLocation("models/block/" + type + "_leaves.json");

                if (!Config.isFromDefaultResourcePack(resourcelocation1))
                {
                    return null;
                }
                else
                {
                    ModelResourceLocation modelresourcelocation = new ModelResourceLocation(type + "_leaves", "normal");
                    IBakedModel ibakedmodel = modelmanager.getModel(modelresourcelocation);

                    if (ibakedmodel != null && ibakedmodel != modelmanager.getMissingModel())
                    {
                        List list = ibakedmodel.getQuads((BlockState)null, (Direction)null, RANDOM);

                        if (list.size() == 0)
                        {
                            return ibakedmodel;
                        }
                        else if (list.size() != 6)
                        {
                            return null;
                        }
                        else
                        {
                            for (BakedQuad bakedquad : (List<BakedQuad>)list)
                            {
                                List list1 = ibakedmodel.getQuads((BlockState)null, bakedquad.getFace(), RANDOM);

                                if (list1.size() > 0)
                                {
                                    return null;
                                }

                                list1.add(bakedquad);
                            }

                            list.clear();
                            updatedTypes.add(type + "_leaves");
                            return ibakedmodel;
                        }
                    }
                    else
                    {
                        return null;
                    }
                }
            }
        }
    }

    private static IBakedModel getModelDoubleFace(IBakedModel model)
    {
        if (model == null)
        {
            return null;
        }
        else if (model.getQuads((BlockState)null, (Direction)null, RANDOM).size() > 0)
        {
            Config.warn("SmartLeaves: Model is not cube, general quads: " + model.getQuads((BlockState)null, (Direction)null, RANDOM).size() + ", model: " + model);
            return model;
        }
        else
        {
            Direction[] adirection = Direction.VALUES;

            for (int i = 0; i < adirection.length; ++i)
            {
                Direction direction = adirection[i];
                List<BakedQuad> list = model.getQuads((BlockState)null, direction, RANDOM);

                if (list.size() != 1)
                {
                    Config.warn("SmartLeaves: Model is not cube, side: " + direction + ", quads: " + list.size() + ", model: " + model);
                    return model;
                }
            }

            IBakedModel ibakedmodel = ModelUtils.duplicateModel(model);
            List[] alist = new List[adirection.length];

            for (int k = 0; k < adirection.length; ++k)
            {
                Direction direction1 = adirection[k];
                List<BakedQuad> list1 = ibakedmodel.getQuads((BlockState)null, direction1, RANDOM);
                BakedQuad bakedquad = list1.get(0);
                BakedQuad bakedquad1 = new BakedQuad((int[])bakedquad.getVertexData().clone(), bakedquad.getTintIndex(), bakedquad.getFace(), bakedquad.getSprite(), bakedquad.applyDiffuseLighting());
                int[] aint = bakedquad1.getVertexData();
                int[] aint1 = (int[])aint.clone();
                int j = aint.length / 4;
                System.arraycopy(aint, 0 * j, aint1, 3 * j, j);
                System.arraycopy(aint, 1 * j, aint1, 2 * j, j);
                System.arraycopy(aint, 2 * j, aint1, 1 * j, j);
                System.arraycopy(aint, 3 * j, aint1, 0 * j, j);
                System.arraycopy(aint1, 0, aint, 0, aint1.length);
                list1.add(bakedquad1);
            }

            return ibakedmodel;
        }
    }
}
