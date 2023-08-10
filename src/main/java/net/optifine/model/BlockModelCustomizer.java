package net.optifine.model;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.optifine.BetterGrass;
import net.optifine.Config;
import net.optifine.ConnectedTextures;
import net.optifine.NaturalTextures;
import net.optifine.SmartLeaves;
import net.optifine.render.RenderEnv;
import net.optifine.render.RenderTypes;

public class BlockModelCustomizer
{
    private static final List<BakedQuad> NO_QUADS = ImmutableList.of();

    public static IBakedModel getRenderModel(IBakedModel modelIn, BlockState stateIn, RenderEnv renderEnv)
    {
        if (renderEnv.isSmartLeaves())
        {
            modelIn = SmartLeaves.getLeavesModel(modelIn, stateIn);
        }

        return modelIn;
    }

    public static List<BakedQuad> getRenderQuads(List<BakedQuad> quads, IBlockDisplayReader worldIn, BlockState stateIn, BlockPos posIn, Direction enumfacing, RenderType layer, long rand, RenderEnv renderEnv)
    {
        if (enumfacing != null)
        {
            if (renderEnv.isSmartLeaves() && SmartLeaves.isSameLeaves(worldIn.getBlockState(posIn.offset(enumfacing)), stateIn))
            {
                return NO_QUADS;
            }

            if (!renderEnv.isBreakingAnimation(quads) && Config.isBetterGrass())
            {
                quads = BetterGrass.getFaceQuads(worldIn, stateIn, posIn, enumfacing, quads);
            }
        }

        List<BakedQuad> list = renderEnv.getListQuadsCustomizer();
        list.clear();

        for (int i = 0; i < quads.size(); ++i)
        {
            BakedQuad bakedquad = quads.get(i);
            BakedQuad[] abakedquad = getRenderQuads(bakedquad, worldIn, stateIn, posIn, enumfacing, rand, renderEnv);

            if (i == 0 && quads.size() == 1 && abakedquad.length == 1 && abakedquad[0] == bakedquad && bakedquad.getQuadEmissive() == null)
            {
                return quads;
            }

            for (int j = 0; j < abakedquad.length; ++j)
            {
                BakedQuad bakedquad1 = abakedquad[j];
                list.add(bakedquad1);

                if (bakedquad1.getQuadEmissive() != null)
                {
                    renderEnv.getListQuadsOverlay(getEmissiveLayer(layer)).addQuad(bakedquad1.getQuadEmissive(), stateIn);
                    renderEnv.setOverlaysRendered(true);
                }
            }
        }

        return list;
    }

    private static RenderType getEmissiveLayer(RenderType layer)
    {
        return layer != null && layer != RenderTypes.SOLID ? layer : RenderTypes.CUTOUT_MIPPED;
    }

    private static BakedQuad[] getRenderQuads(BakedQuad quad, IBlockDisplayReader worldIn, BlockState stateIn, BlockPos posIn, Direction enumfacing, long rand, RenderEnv renderEnv)
    {
        if (renderEnv.isBreakingAnimation(quad))
        {
            return renderEnv.getArrayQuadsCtm(quad);
        }
        else
        {
            BakedQuad bakedquad = quad;

            if (Config.isConnectedTextures())
            {
                BakedQuad[] abakedquad = ConnectedTextures.getConnectedTexture(worldIn, stateIn, posIn, quad, renderEnv);

                if (abakedquad.length != 1 || abakedquad[0] != quad)
                {
                    return abakedquad;
                }
            }

            if (Config.isNaturalTextures())
            {
                quad = NaturalTextures.getNaturalTexture(posIn, quad);

                if (quad != bakedquad)
                {
                    return renderEnv.getArrayQuadsCtm(quad);
                }
            }

            return renderEnv.getArrayQuadsCtm(quad);
        }
    }
}
