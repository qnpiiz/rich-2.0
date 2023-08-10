package net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.optifine.BetterSnow;
import net.optifine.BlockPosM;
import net.optifine.Config;
import net.optifine.CustomColors;
import net.optifine.EmissiveTextures;
import net.optifine.model.BlockModelCustomizer;
import net.optifine.model.ListQuadsOverlay;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.render.LightCacheOF;
import net.optifine.render.RenderEnv;
import net.optifine.render.RenderTypes;
import net.optifine.shaders.SVertexBuilder;
import net.optifine.shaders.Shaders;
import net.optifine.util.BlockUtils;

public class BlockModelRenderer
{
    private final BlockColors blockColors;
    private static final ThreadLocal<BlockModelRenderer.Cache> CACHE_COMBINED_LIGHT = ThreadLocal.withInitial(() ->
    {
        return new BlockModelRenderer.Cache();
    });
    private static float aoLightValueOpaque = 0.2F;
    private static boolean separateAoLightValue = false;
    private static final LightCacheOF LIGHT_CACHE_OF = new LightCacheOF();
    private static final RenderType[] OVERLAY_LAYERS = new RenderType[] {RenderTypes.CUTOUT, RenderTypes.CUTOUT_MIPPED, RenderTypes.TRANSLUCENT};
    private boolean forgeModelData = Reflector.ForgeHooksClient.exists();

    public BlockModelRenderer(BlockColors blockColorsIn)
    {
        this.blockColors = blockColorsIn;
    }

    public boolean renderModel(IBlockDisplayReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixIn, IVertexBuilder buffer, boolean checkSides, Random randomIn, long rand, int combinedOverlayIn)
    {
        return this.renderModel(worldIn, modelIn, stateIn, posIn, matrixIn, buffer, checkSides, randomIn, rand, combinedOverlayIn, EmptyModelData.INSTANCE);
    }

    public boolean renderModel(IBlockDisplayReader p_renderModel_1_, IBakedModel p_renderModel_2_, BlockState p_renderModel_3_, BlockPos p_renderModel_4_, MatrixStack p_renderModel_5_, IVertexBuilder p_renderModel_6_, boolean p_renderModel_7_, Random p_renderModel_8_, long p_renderModel_9_, int p_renderModel_11_, IModelData p_renderModel_12_)
    {
        boolean flag = Minecraft.isAmbientOcclusionEnabled() && ReflectorForge.getLightValue(p_renderModel_3_, p_renderModel_1_, p_renderModel_4_) == 0 && p_renderModel_2_.isAmbientOcclusion();

        if (this.forgeModelData)
        {
            p_renderModel_12_ = p_renderModel_2_.getModelData(p_renderModel_1_, p_renderModel_4_, p_renderModel_3_, p_renderModel_12_);
        }

        Vector3d vector3d = p_renderModel_3_.getOffset(p_renderModel_1_, p_renderModel_4_);
        p_renderModel_5_.translate(vector3d.x, vector3d.y, vector3d.z);

        try
        {
            if (Config.isShaders())
            {
                SVertexBuilder.pushEntity(p_renderModel_3_, p_renderModel_6_);
            }

            if (!Config.isAlternateBlocks())
            {
                p_renderModel_9_ = 0L;
            }

            RenderEnv renderenv = p_renderModel_6_.getRenderEnv(p_renderModel_3_, p_renderModel_4_);
            p_renderModel_2_ = BlockModelCustomizer.getRenderModel(p_renderModel_2_, p_renderModel_3_, renderenv);
            boolean flag1 = flag ? this.renderModelSmooth(p_renderModel_1_, p_renderModel_2_, p_renderModel_3_, p_renderModel_4_, p_renderModel_5_, p_renderModel_6_, p_renderModel_7_, p_renderModel_8_, p_renderModel_9_, p_renderModel_11_, p_renderModel_12_) : this.renderModelFlat(p_renderModel_1_, p_renderModel_2_, p_renderModel_3_, p_renderModel_4_, p_renderModel_5_, p_renderModel_6_, p_renderModel_7_, p_renderModel_8_, p_renderModel_9_, p_renderModel_11_, p_renderModel_12_);

            if (flag1)
            {
                this.renderOverlayModels(p_renderModel_1_, p_renderModel_2_, p_renderModel_3_, p_renderModel_4_, p_renderModel_5_, p_renderModel_6_, p_renderModel_11_, p_renderModel_7_, p_renderModel_8_, p_renderModel_9_, renderenv, flag, vector3d);
            }

            if (Config.isShaders())
            {
                SVertexBuilder.popEntity(p_renderModel_6_);
            }

            return flag1;
        }
        catch (Throwable throwable1)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Tesselating block model");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, p_renderModel_4_, p_renderModel_3_);
            crashreportcategory.addDetail("Using AO", flag);
            throw new ReportedException(crashreport);
        }
    }

    public boolean renderModelSmooth(IBlockDisplayReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixStackIn, IVertexBuilder buffer, boolean checkSides, Random randomIn, long rand, int combinedOverlayIn)
    {
        return this.renderModelSmooth(worldIn, modelIn, stateIn, posIn, matrixStackIn, buffer, checkSides, randomIn, rand, combinedOverlayIn, EmptyModelData.INSTANCE);
    }

    public boolean renderModelSmooth(IBlockDisplayReader p_renderModelSmooth_1_, IBakedModel p_renderModelSmooth_2_, BlockState p_renderModelSmooth_3_, BlockPos p_renderModelSmooth_4_, MatrixStack p_renderModelSmooth_5_, IVertexBuilder p_renderModelSmooth_6_, boolean p_renderModelSmooth_7_, Random p_renderModelSmooth_8_, long p_renderModelSmooth_9_, int p_renderModelSmooth_11_, IModelData p_renderModelSmooth_12_)
    {
        boolean flag = false;
        RenderEnv renderenv = p_renderModelSmooth_6_.getRenderEnv(p_renderModelSmooth_3_, p_renderModelSmooth_4_);
        RenderType rendertype = p_renderModelSmooth_6_.getRenderType();

        for (Direction direction : Direction.VALUES)
        {
            if (!p_renderModelSmooth_7_ || BlockUtils.shouldSideBeRendered(p_renderModelSmooth_3_, p_renderModelSmooth_1_, p_renderModelSmooth_4_, direction, renderenv))
            {
                p_renderModelSmooth_8_.setSeed(p_renderModelSmooth_9_);
                List<BakedQuad> list = this.forgeModelData ? p_renderModelSmooth_2_.getQuads(p_renderModelSmooth_3_, direction, p_renderModelSmooth_8_, p_renderModelSmooth_12_) : p_renderModelSmooth_2_.getQuads(p_renderModelSmooth_3_, direction, p_renderModelSmooth_8_);
                list = BlockModelCustomizer.getRenderQuads(list, p_renderModelSmooth_1_, p_renderModelSmooth_3_, p_renderModelSmooth_4_, direction, rendertype, p_renderModelSmooth_9_, renderenv);
                this.renderQuadsSmooth(p_renderModelSmooth_1_, p_renderModelSmooth_3_, p_renderModelSmooth_4_, p_renderModelSmooth_5_, p_renderModelSmooth_6_, list, p_renderModelSmooth_11_, renderenv);
                flag = true;
            }
        }

        p_renderModelSmooth_8_.setSeed(p_renderModelSmooth_9_);
        List<BakedQuad> list1 = this.forgeModelData ? p_renderModelSmooth_2_.getQuads(p_renderModelSmooth_3_, (Direction)null, p_renderModelSmooth_8_, p_renderModelSmooth_12_) : p_renderModelSmooth_2_.getQuads(p_renderModelSmooth_3_, (Direction)null, p_renderModelSmooth_8_);

        if (!list1.isEmpty())
        {
            list1 = BlockModelCustomizer.getRenderQuads(list1, p_renderModelSmooth_1_, p_renderModelSmooth_3_, p_renderModelSmooth_4_, (Direction)null, rendertype, p_renderModelSmooth_9_, renderenv);
            this.renderQuadsSmooth(p_renderModelSmooth_1_, p_renderModelSmooth_3_, p_renderModelSmooth_4_, p_renderModelSmooth_5_, p_renderModelSmooth_6_, list1, p_renderModelSmooth_11_, renderenv);
            flag = true;
        }

        return flag;
    }

    public boolean renderModelFlat(IBlockDisplayReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixStackIn, IVertexBuilder buffer, boolean checkSides, Random randomIn, long rand, int combinedOverlayIn)
    {
        return this.renderModelFlat(worldIn, modelIn, stateIn, posIn, matrixStackIn, buffer, checkSides, randomIn, rand, combinedOverlayIn, EmptyModelData.INSTANCE);
    }

    public boolean renderModelFlat(IBlockDisplayReader p_renderModelFlat_1_, IBakedModel p_renderModelFlat_2_, BlockState p_renderModelFlat_3_, BlockPos p_renderModelFlat_4_, MatrixStack p_renderModelFlat_5_, IVertexBuilder p_renderModelFlat_6_, boolean p_renderModelFlat_7_, Random p_renderModelFlat_8_, long p_renderModelFlat_9_, int p_renderModelFlat_11_, IModelData p_renderModelFlat_12_)
    {
        boolean flag = false;
        RenderEnv renderenv = p_renderModelFlat_6_.getRenderEnv(p_renderModelFlat_3_, p_renderModelFlat_4_);
        RenderType rendertype = p_renderModelFlat_6_.getRenderType();

        for (Direction direction : Direction.VALUES)
        {
            if (!p_renderModelFlat_7_ || BlockUtils.shouldSideBeRendered(p_renderModelFlat_3_, p_renderModelFlat_1_, p_renderModelFlat_4_, direction, renderenv))
            {
                p_renderModelFlat_8_.setSeed(p_renderModelFlat_9_);
                List<BakedQuad> list = this.forgeModelData ? p_renderModelFlat_2_.getQuads(p_renderModelFlat_3_, direction, p_renderModelFlat_8_, p_renderModelFlat_12_) : p_renderModelFlat_2_.getQuads(p_renderModelFlat_3_, direction, p_renderModelFlat_8_);
                int i = WorldRenderer.getPackedLightmapCoords(p_renderModelFlat_1_, p_renderModelFlat_3_, p_renderModelFlat_4_.offset(direction));
                list = BlockModelCustomizer.getRenderQuads(list, p_renderModelFlat_1_, p_renderModelFlat_3_, p_renderModelFlat_4_, direction, rendertype, p_renderModelFlat_9_, renderenv);
                this.renderQuadsFlat(p_renderModelFlat_1_, p_renderModelFlat_3_, p_renderModelFlat_4_, i, p_renderModelFlat_11_, false, p_renderModelFlat_5_, p_renderModelFlat_6_, list, renderenv);
                flag = true;
            }
        }

        p_renderModelFlat_8_.setSeed(p_renderModelFlat_9_);
        List<BakedQuad> list1 = this.forgeModelData ? p_renderModelFlat_2_.getQuads(p_renderModelFlat_3_, (Direction)null, p_renderModelFlat_8_, p_renderModelFlat_12_) : p_renderModelFlat_2_.getQuads(p_renderModelFlat_3_, (Direction)null, p_renderModelFlat_8_);

        if (!list1.isEmpty())
        {
            list1 = BlockModelCustomizer.getRenderQuads(list1, p_renderModelFlat_1_, p_renderModelFlat_3_, p_renderModelFlat_4_, (Direction)null, rendertype, p_renderModelFlat_9_, renderenv);
            this.renderQuadsFlat(p_renderModelFlat_1_, p_renderModelFlat_3_, p_renderModelFlat_4_, -1, p_renderModelFlat_11_, true, p_renderModelFlat_5_, p_renderModelFlat_6_, list1, renderenv);
            flag = true;
        }

        return flag;
    }

    private void renderQuadsSmooth(IBlockDisplayReader p_renderQuadsSmooth_1_, BlockState p_renderQuadsSmooth_2_, BlockPos p_renderQuadsSmooth_3_, MatrixStack p_renderQuadsSmooth_4_, IVertexBuilder p_renderQuadsSmooth_5_, List<BakedQuad> p_renderQuadsSmooth_6_, int p_renderQuadsSmooth_7_, RenderEnv p_renderQuadsSmooth_8_)
    {
        float[] afloat = p_renderQuadsSmooth_8_.getQuadBounds();
        BitSet bitset = p_renderQuadsSmooth_8_.getBoundsFlags();
        BlockModelRenderer.AmbientOcclusionFace blockmodelrenderer$ambientocclusionface = p_renderQuadsSmooth_8_.getAoFace();
        int i = p_renderQuadsSmooth_6_.size();

        for (int j = 0; j < i; ++j)
        {
            BakedQuad bakedquad = p_renderQuadsSmooth_6_.get(j);
            this.fillQuadBounds(p_renderQuadsSmooth_1_, p_renderQuadsSmooth_2_, p_renderQuadsSmooth_3_, bakedquad.getVertexData(), bakedquad.getFace(), afloat, bitset);
            blockmodelrenderer$ambientocclusionface.renderBlockModel(p_renderQuadsSmooth_1_, p_renderQuadsSmooth_2_, p_renderQuadsSmooth_3_, bakedquad.getFace(), afloat, bitset, bakedquad.applyDiffuseLighting());

            if (bakedquad.getSprite().isSpriteEmissive)
            {
                blockmodelrenderer$ambientocclusionface.setMaxBlockLight();
            }

            this.renderQuadSmooth(p_renderQuadsSmooth_1_, p_renderQuadsSmooth_2_, p_renderQuadsSmooth_3_, p_renderQuadsSmooth_5_, p_renderQuadsSmooth_4_.getLast(), bakedquad, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3], blockmodelrenderer$ambientocclusionface.vertexBrightness[0], blockmodelrenderer$ambientocclusionface.vertexBrightness[1], blockmodelrenderer$ambientocclusionface.vertexBrightness[2], blockmodelrenderer$ambientocclusionface.vertexBrightness[3], p_renderQuadsSmooth_7_, p_renderQuadsSmooth_8_);
        }
    }

    private void renderQuadSmooth(IBlockDisplayReader p_renderQuadSmooth_1_, BlockState p_renderQuadSmooth_2_, BlockPos p_renderQuadSmooth_3_, IVertexBuilder p_renderQuadSmooth_4_, MatrixStack.Entry p_renderQuadSmooth_5_, BakedQuad p_renderQuadSmooth_6_, float p_renderQuadSmooth_7_, float p_renderQuadSmooth_8_, float p_renderQuadSmooth_9_, float p_renderQuadSmooth_10_, int p_renderQuadSmooth_11_, int p_renderQuadSmooth_12_, int p_renderQuadSmooth_13_, int p_renderQuadSmooth_14_, int p_renderQuadSmooth_15_, RenderEnv p_renderQuadSmooth_16_)
    {
        int i = CustomColors.getColorMultiplier(p_renderQuadSmooth_6_, p_renderQuadSmooth_2_, p_renderQuadSmooth_1_, p_renderQuadSmooth_3_, p_renderQuadSmooth_16_);
        float f;
        float f1;
        float f2;

        if (!p_renderQuadSmooth_6_.hasTintIndex() && i == -1)
        {
            f = 1.0F;
            f1 = 1.0F;
            f2 = 1.0F;
        }
        else
        {
            int j = i != -1 ? i : this.blockColors.getColor(p_renderQuadSmooth_2_, p_renderQuadSmooth_1_, p_renderQuadSmooth_3_, p_renderQuadSmooth_6_.getTintIndex());
            f = (float)(j >> 16 & 255) / 255.0F;
            f1 = (float)(j >> 8 & 255) / 255.0F;
            f2 = (float)(j & 255) / 255.0F;
        }

        p_renderQuadSmooth_4_.addQuad(p_renderQuadSmooth_5_, p_renderQuadSmooth_6_, p_renderQuadSmooth_4_.getTempFloat4(p_renderQuadSmooth_7_, p_renderQuadSmooth_8_, p_renderQuadSmooth_9_, p_renderQuadSmooth_10_), f, f1, f2, p_renderQuadSmooth_4_.getTempInt4(p_renderQuadSmooth_11_, p_renderQuadSmooth_12_, p_renderQuadSmooth_13_, p_renderQuadSmooth_14_), p_renderQuadSmooth_15_, true);
    }

    private void fillQuadBounds(IBlockDisplayReader blockReaderIn, BlockState stateIn, BlockPos posIn, int[] vertexData, Direction face, @Nullable float[] quadBounds, BitSet boundsFlags)
    {
        float f = 32.0F;
        float f1 = 32.0F;
        float f2 = 32.0F;
        float f3 = -32.0F;
        float f4 = -32.0F;
        float f5 = -32.0F;
        int i = vertexData.length / 4;

        for (int j = 0; j < 4; ++j)
        {
            float f6 = Float.intBitsToFloat(vertexData[j * i]);
            float f7 = Float.intBitsToFloat(vertexData[j * i + 1]);
            float f8 = Float.intBitsToFloat(vertexData[j * i + 2]);
            f = Math.min(f, f6);
            f1 = Math.min(f1, f7);
            f2 = Math.min(f2, f8);
            f3 = Math.max(f3, f6);
            f4 = Math.max(f4, f7);
            f5 = Math.max(f5, f8);
        }

        if (quadBounds != null)
        {
            quadBounds[Direction.WEST.getIndex()] = f;
            quadBounds[Direction.EAST.getIndex()] = f3;
            quadBounds[Direction.DOWN.getIndex()] = f1;
            quadBounds[Direction.UP.getIndex()] = f4;
            quadBounds[Direction.NORTH.getIndex()] = f2;
            quadBounds[Direction.SOUTH.getIndex()] = f5;
            int k = Direction.VALUES.length;
            quadBounds[Direction.WEST.getIndex() + k] = 1.0F - f;
            quadBounds[Direction.EAST.getIndex() + k] = 1.0F - f3;
            quadBounds[Direction.DOWN.getIndex() + k] = 1.0F - f1;
            quadBounds[Direction.UP.getIndex() + k] = 1.0F - f4;
            quadBounds[Direction.NORTH.getIndex() + k] = 1.0F - f2;
            quadBounds[Direction.SOUTH.getIndex() + k] = 1.0F - f5;
        }

        float f9 = 1.0E-4F;
        float f10 = 0.9999F;

        switch (face)
        {
            case DOWN:
                boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, f1 == f4 && (f1 < 1.0E-4F || stateIn.hasOpaqueCollisionShape(blockReaderIn, posIn)));
                break;

            case UP:
                boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, f1 == f4 && (f4 > 0.9999F || stateIn.hasOpaqueCollisionShape(blockReaderIn, posIn)));
                break;

            case NORTH:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, f2 == f5 && (f2 < 1.0E-4F || stateIn.hasOpaqueCollisionShape(blockReaderIn, posIn)));
                break;

            case SOUTH:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, f2 == f5 && (f5 > 0.9999F || stateIn.hasOpaqueCollisionShape(blockReaderIn, posIn)));
                break;

            case WEST:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, f == f3 && (f < 1.0E-4F || stateIn.hasOpaqueCollisionShape(blockReaderIn, posIn)));
                break;

            case EAST:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, f == f3 && (f3 > 0.9999F || stateIn.hasOpaqueCollisionShape(blockReaderIn, posIn)));
        }
    }

    private void renderQuadsFlat(IBlockDisplayReader p_renderQuadsFlat_1_, BlockState p_renderQuadsFlat_2_, BlockPos p_renderQuadsFlat_3_, int p_renderQuadsFlat_4_, int p_renderQuadsFlat_5_, boolean p_renderQuadsFlat_6_, MatrixStack p_renderQuadsFlat_7_, IVertexBuilder p_renderQuadsFlat_8_, List<BakedQuad> p_renderQuadsFlat_9_, RenderEnv p_renderQuadsFlat_10_)
    {
        BitSet bitset = p_renderQuadsFlat_10_.getBoundsFlags();
        int i = p_renderQuadsFlat_9_.size();

        for (int j = 0; j < i; ++j)
        {
            BakedQuad bakedquad = p_renderQuadsFlat_9_.get(j);

            if (p_renderQuadsFlat_6_)
            {
                this.fillQuadBounds(p_renderQuadsFlat_1_, p_renderQuadsFlat_2_, p_renderQuadsFlat_3_, bakedquad.getVertexData(), bakedquad.getFace(), (float[])null, bitset);
                BlockPos blockpos = bitset.get(0) ? p_renderQuadsFlat_3_.offset(bakedquad.getFace()) : p_renderQuadsFlat_3_;
                p_renderQuadsFlat_4_ = WorldRenderer.getPackedLightmapCoords(p_renderQuadsFlat_1_, p_renderQuadsFlat_2_, blockpos);
            }

            if (bakedquad.getSprite().isSpriteEmissive)
            {
                p_renderQuadsFlat_4_ = LightTexture.MAX_BRIGHTNESS;
            }

            float f = p_renderQuadsFlat_1_.func_230487_a_(bakedquad.getFace(), bakedquad.applyDiffuseLighting());
            this.renderQuadSmooth(p_renderQuadsFlat_1_, p_renderQuadsFlat_2_, p_renderQuadsFlat_3_, p_renderQuadsFlat_8_, p_renderQuadsFlat_7_.getLast(), bakedquad, f, f, f, f, p_renderQuadsFlat_4_, p_renderQuadsFlat_4_, p_renderQuadsFlat_4_, p_renderQuadsFlat_4_, p_renderQuadsFlat_5_, p_renderQuadsFlat_10_);
        }
    }

    public void renderModelBrightnessColor(MatrixStack.Entry matrixEntry, IVertexBuilder buffer, @Nullable BlockState state, IBakedModel modelIn, float red, float green, float blue, int combinedLightIn, int combinedOverlayIn)
    {
        this.renderModel(matrixEntry, buffer, state, modelIn, red, green, blue, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
    }

    public void renderModel(MatrixStack.Entry p_renderModel_1_, IVertexBuilder p_renderModel_2_, @Nullable BlockState p_renderModel_3_, IBakedModel p_renderModel_4_, float p_renderModel_5_, float p_renderModel_6_, float p_renderModel_7_, int p_renderModel_8_, int p_renderModel_9_, IModelData p_renderModel_10_)
    {
        Random random = new Random();
        long i = 42L;

        for (Direction direction : Direction.VALUES)
        {
            random.setSeed(42L);

            if (this.forgeModelData)
            {
                renderModelBrightnessColorQuads(p_renderModel_1_, p_renderModel_2_, p_renderModel_5_, p_renderModel_6_, p_renderModel_7_, p_renderModel_4_.getQuads(p_renderModel_3_, direction, random, p_renderModel_10_), p_renderModel_8_, p_renderModel_9_);
            }
            else
            {
                renderModelBrightnessColorQuads(p_renderModel_1_, p_renderModel_2_, p_renderModel_5_, p_renderModel_6_, p_renderModel_7_, p_renderModel_4_.getQuads(p_renderModel_3_, direction, random), p_renderModel_8_, p_renderModel_9_);
            }
        }

        random.setSeed(42L);

        if (this.forgeModelData)
        {
            renderModelBrightnessColorQuads(p_renderModel_1_, p_renderModel_2_, p_renderModel_5_, p_renderModel_6_, p_renderModel_7_, p_renderModel_4_.getQuads(p_renderModel_3_, (Direction)null, random, p_renderModel_10_), p_renderModel_8_, p_renderModel_9_);
        }
        else
        {
            renderModelBrightnessColorQuads(p_renderModel_1_, p_renderModel_2_, p_renderModel_5_, p_renderModel_6_, p_renderModel_7_, p_renderModel_4_.getQuads(p_renderModel_3_, (Direction)null, random), p_renderModel_8_, p_renderModel_9_);
        }
    }

    private static void renderModelBrightnessColorQuads(MatrixStack.Entry matrixEntry, IVertexBuilder buffer, float red, float green, float blue, List<BakedQuad> listQuads, int combinedLightIn, int combinedOverlayIn)
    {
        boolean flag = EmissiveTextures.isActive();
        Iterator iterator = listQuads.iterator();

        while (true)
        {
            BakedQuad bakedquad;

            do
            {
                if (!iterator.hasNext())
                {
                    return;
                }

                bakedquad = (BakedQuad)iterator.next();

                if (!flag)
                {
                    break;
                }

                bakedquad = EmissiveTextures.getEmissiveQuad(bakedquad);
            }
            while (bakedquad == null);

            float f;
            float f1;
            float f2;

            if (bakedquad.hasTintIndex())
            {
                f = MathHelper.clamp(red, 0.0F, 1.0F);
                f1 = MathHelper.clamp(green, 0.0F, 1.0F);
                f2 = MathHelper.clamp(blue, 0.0F, 1.0F);
            }
            else
            {
                f = 1.0F;
                f1 = 1.0F;
                f2 = 1.0F;
            }

            buffer.addQuad(matrixEntry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn);
        }
    }

    public static void enableCache()
    {
        CACHE_COMBINED_LIGHT.get().enable();
    }

    public static void disableCache()
    {
        CACHE_COMBINED_LIGHT.get().disable();
    }

    public static float fixAoLightValue(float p_fixAoLightValue_0_)
    {
        return p_fixAoLightValue_0_ == 0.2F ? aoLightValueOpaque : p_fixAoLightValue_0_;
    }

    public static void updateAoLightValue()
    {
        aoLightValueOpaque = 1.0F - Config.getAmbientOcclusionLevel() * 0.8F;
        separateAoLightValue = Config.isShaders() && Shaders.isSeparateAo();
    }

    public static boolean isSeparateAoLightValue()
    {
        return separateAoLightValue;
    }

    private void renderOverlayModels(IBlockDisplayReader p_renderOverlayModels_1_, IBakedModel p_renderOverlayModels_2_, BlockState p_renderOverlayModels_3_, BlockPos p_renderOverlayModels_4_, MatrixStack p_renderOverlayModels_5_, IVertexBuilder p_renderOverlayModels_6_, int p_renderOverlayModels_7_, boolean p_renderOverlayModels_8_, Random p_renderOverlayModels_9_, long p_renderOverlayModels_10_, RenderEnv p_renderOverlayModels_12_, boolean p_renderOverlayModels_13_, Vector3d p_renderOverlayModels_14_)
    {
        if (p_renderOverlayModels_12_.isOverlaysRendered())
        {
            for (int i = 0; i < OVERLAY_LAYERS.length; ++i)
            {
                RenderType rendertype = OVERLAY_LAYERS[i];
                ListQuadsOverlay listquadsoverlay = p_renderOverlayModels_12_.getListQuadsOverlay(rendertype);

                if (listquadsoverlay.size() > 0)
                {
                    RegionRenderCacheBuilder regionrendercachebuilder = p_renderOverlayModels_12_.getRegionRenderCacheBuilder();

                    if (regionrendercachebuilder != null)
                    {
                        BufferBuilder bufferbuilder = regionrendercachebuilder.getBuilder(rendertype);

                        if (!bufferbuilder.isDrawing())
                        {
                            bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
                        }

                        for (int j = 0; j < listquadsoverlay.size(); ++j)
                        {
                            BakedQuad bakedquad = listquadsoverlay.getQuad(j);
                            List<BakedQuad> list = listquadsoverlay.getListQuadsSingle(bakedquad);
                            BlockState blockstate = listquadsoverlay.getBlockState(j);

                            if (bakedquad.getQuadEmissive() != null)
                            {
                                listquadsoverlay.addQuad(bakedquad.getQuadEmissive(), blockstate);
                            }

                            p_renderOverlayModels_12_.reset(blockstate, p_renderOverlayModels_4_);

                            if (p_renderOverlayModels_13_)
                            {
                                this.renderQuadsSmooth(p_renderOverlayModels_1_, blockstate, p_renderOverlayModels_4_, p_renderOverlayModels_5_, bufferbuilder, list, p_renderOverlayModels_7_, p_renderOverlayModels_12_);
                            }
                            else
                            {
                                int k = WorldRenderer.getPackedLightmapCoords(p_renderOverlayModels_1_, blockstate, p_renderOverlayModels_4_.offset(bakedquad.getFace()));
                                this.renderQuadsFlat(p_renderOverlayModels_1_, blockstate, p_renderOverlayModels_4_, k, p_renderOverlayModels_7_, false, p_renderOverlayModels_5_, bufferbuilder, list, p_renderOverlayModels_12_);
                            }
                        }
                    }

                    listquadsoverlay.clear();
                }
            }
        }

        if (Config.isBetterSnow() && !p_renderOverlayModels_12_.isBreakingAnimation() && BetterSnow.shouldRender(p_renderOverlayModels_1_, p_renderOverlayModels_3_, p_renderOverlayModels_4_))
        {
            IBakedModel ibakedmodel = BetterSnow.getModelSnowLayer();
            BlockState blockstate1 = BetterSnow.getStateSnowLayer();
            p_renderOverlayModels_5_.translate(-p_renderOverlayModels_14_.x, -p_renderOverlayModels_14_.y, -p_renderOverlayModels_14_.z);
            this.renderModel(p_renderOverlayModels_1_, ibakedmodel, blockstate1, p_renderOverlayModels_4_, p_renderOverlayModels_5_, p_renderOverlayModels_6_, p_renderOverlayModels_8_, p_renderOverlayModels_9_, p_renderOverlayModels_10_, p_renderOverlayModels_7_);
        }
    }

    public static class AmbientOcclusionFace
    {
        private final float[] vertexColorMultiplier = new float[4];
        private final int[] vertexBrightness = new int[4];
        private BlockPosM blockPos = new BlockPosM();

        public AmbientOcclusionFace()
        {
            this((BlockModelRenderer)null);
        }

        public AmbientOcclusionFace(BlockModelRenderer p_i46235_1_)
        {
        }

        public void setMaxBlockLight()
        {
            int i = LightTexture.MAX_BRIGHTNESS;
            this.vertexBrightness[0] = i;
            this.vertexBrightness[1] = i;
            this.vertexBrightness[2] = i;
            this.vertexBrightness[3] = i;
            this.vertexColorMultiplier[0] = 1.0F;
            this.vertexColorMultiplier[1] = 1.0F;
            this.vertexColorMultiplier[2] = 1.0F;
            this.vertexColorMultiplier[3] = 1.0F;
        }

        public void renderBlockModel(IBlockDisplayReader reader, BlockState state, BlockPos pos, Direction direction, float[] vertexes, BitSet bitSet, boolean applyDiffuseLighting)
        {
            BlockPos blockpos = bitSet.get(0) ? pos.offset(direction) : pos;
            BlockModelRenderer.NeighborInfo blockmodelrenderer$neighborinfo = BlockModelRenderer.NeighborInfo.getNeighbourInfo(direction);
            BlockPosM blockposm = this.blockPos;
            LightCacheOF lightcacheof = BlockModelRenderer.LIGHT_CACHE_OF;
            blockposm.setPosOffset(blockpos, blockmodelrenderer$neighborinfo.corners[0]);
            BlockState blockstate = reader.getBlockState(blockposm);
            int i = LightCacheOF.getPackedLight(blockstate, reader, blockposm);
            float f = LightCacheOF.getBrightness(blockstate, reader, blockposm);
            blockposm.setPosOffset(blockpos, blockmodelrenderer$neighborinfo.corners[1]);
            BlockState blockstate1 = reader.getBlockState(blockposm);
            int j = LightCacheOF.getPackedLight(blockstate1, reader, blockposm);
            float f1 = LightCacheOF.getBrightness(blockstate1, reader, blockposm);
            blockposm.setPosOffset(blockpos, blockmodelrenderer$neighborinfo.corners[2]);
            BlockState blockstate2 = reader.getBlockState(blockposm);
            int k = LightCacheOF.getPackedLight(blockstate2, reader, blockposm);
            float f2 = LightCacheOF.getBrightness(blockstate2, reader, blockposm);
            blockposm.setPosOffset(blockpos, blockmodelrenderer$neighborinfo.corners[3]);
            BlockState blockstate3 = reader.getBlockState(blockposm);
            int l = LightCacheOF.getPackedLight(blockstate3, reader, blockposm);
            float f3 = LightCacheOF.getBrightness(blockstate3, reader, blockposm);
            blockposm.setPosOffset(blockpos, blockmodelrenderer$neighborinfo.corners[0], direction);
            boolean flag = reader.getBlockState(blockposm).getOpacity(reader, blockposm) == 0;
            blockposm.setPosOffset(blockpos, blockmodelrenderer$neighborinfo.corners[1], direction);
            boolean flag1 = reader.getBlockState(blockposm).getOpacity(reader, blockposm) == 0;
            blockposm.setPosOffset(blockpos, blockmodelrenderer$neighborinfo.corners[2], direction);
            boolean flag2 = reader.getBlockState(blockposm).getOpacity(reader, blockposm) == 0;
            blockposm.setPosOffset(blockpos, blockmodelrenderer$neighborinfo.corners[3], direction);
            boolean flag3 = reader.getBlockState(blockposm).getOpacity(reader, blockposm) == 0;
            float f4;
            int i1;

            if (!flag2 && !flag)
            {
                f4 = f;
                i1 = i;
            }
            else
            {
                blockposm.setPosOffset(blockpos, blockmodelrenderer$neighborinfo.corners[0], blockmodelrenderer$neighborinfo.corners[2]);
                BlockState blockstate4 = reader.getBlockState(blockposm);
                f4 = LightCacheOF.getBrightness(blockstate4, reader, blockposm);
                i1 = LightCacheOF.getPackedLight(blockstate4, reader, blockposm);
            }

            int j1;
            float f26;

            if (!flag3 && !flag)
            {
                f26 = f;
                j1 = i;
            }
            else
            {
                blockposm.setPosOffset(blockpos, blockmodelrenderer$neighborinfo.corners[0], blockmodelrenderer$neighborinfo.corners[3]);
                BlockState blockstate5 = reader.getBlockState(blockposm);
                f26 = LightCacheOF.getBrightness(blockstate5, reader, blockposm);
                j1 = LightCacheOF.getPackedLight(blockstate5, reader, blockposm);
            }

            int k1;
            float f27;

            if (!flag2 && !flag1)
            {
                f27 = f;
                k1 = i;
            }
            else
            {
                blockposm.setPosOffset(blockpos, blockmodelrenderer$neighborinfo.corners[1], blockmodelrenderer$neighborinfo.corners[2]);
                BlockState blockstate6 = reader.getBlockState(blockposm);
                f27 = LightCacheOF.getBrightness(blockstate6, reader, blockposm);
                k1 = LightCacheOF.getPackedLight(blockstate6, reader, blockposm);
            }

            int l1;
            float f28;

            if (!flag3 && !flag1)
            {
                f28 = f;
                l1 = i;
            }
            else
            {
                blockposm.setPosOffset(blockpos, blockmodelrenderer$neighborinfo.corners[1], blockmodelrenderer$neighborinfo.corners[3]);
                BlockState blockstate7 = reader.getBlockState(blockposm);
                f28 = LightCacheOF.getBrightness(blockstate7, reader, blockposm);
                l1 = LightCacheOF.getPackedLight(blockstate7, reader, blockposm);
            }

            int i3 = LightCacheOF.getPackedLight(state, reader, pos);
            blockposm.setPosOffset(pos, direction);
            BlockState blockstate8 = reader.getBlockState(blockposm);

            if (bitSet.get(0) || !blockstate8.isOpaqueCube(reader, blockposm))
            {
                i3 = LightCacheOF.getPackedLight(blockstate8, reader, blockposm);
            }

            float f5 = bitSet.get(0) ? LightCacheOF.getBrightness(reader.getBlockState(blockpos), reader, blockpos) : LightCacheOF.getBrightness(reader.getBlockState(pos), reader, pos);
            BlockModelRenderer.VertexTranslations blockmodelrenderer$vertextranslations = BlockModelRenderer.VertexTranslations.getVertexTranslations(direction);

            if (bitSet.get(1) && blockmodelrenderer$neighborinfo.doNonCubicWeight)
            {
                float f29 = (f3 + f + f26 + f5) * 0.25F;
                float f31 = (f2 + f + f4 + f5) * 0.25F;
                float f32 = (f2 + f1 + f27 + f5) * 0.25F;
                float f33 = (f3 + f1 + f28 + f5) * 0.25F;
                float f10 = vertexes[blockmodelrenderer$neighborinfo.vert0Weights[0].shape] * vertexes[blockmodelrenderer$neighborinfo.vert0Weights[1].shape];
                float f11 = vertexes[blockmodelrenderer$neighborinfo.vert0Weights[2].shape] * vertexes[blockmodelrenderer$neighborinfo.vert0Weights[3].shape];
                float f12 = vertexes[blockmodelrenderer$neighborinfo.vert0Weights[4].shape] * vertexes[blockmodelrenderer$neighborinfo.vert0Weights[5].shape];
                float f13 = vertexes[blockmodelrenderer$neighborinfo.vert0Weights[6].shape] * vertexes[blockmodelrenderer$neighborinfo.vert0Weights[7].shape];
                float f14 = vertexes[blockmodelrenderer$neighborinfo.vert1Weights[0].shape] * vertexes[blockmodelrenderer$neighborinfo.vert1Weights[1].shape];
                float f15 = vertexes[blockmodelrenderer$neighborinfo.vert1Weights[2].shape] * vertexes[blockmodelrenderer$neighborinfo.vert1Weights[3].shape];
                float f16 = vertexes[blockmodelrenderer$neighborinfo.vert1Weights[4].shape] * vertexes[blockmodelrenderer$neighborinfo.vert1Weights[5].shape];
                float f17 = vertexes[blockmodelrenderer$neighborinfo.vert1Weights[6].shape] * vertexes[blockmodelrenderer$neighborinfo.vert1Weights[7].shape];
                float f18 = vertexes[blockmodelrenderer$neighborinfo.vert2Weights[0].shape] * vertexes[blockmodelrenderer$neighborinfo.vert2Weights[1].shape];
                float f19 = vertexes[blockmodelrenderer$neighborinfo.vert2Weights[2].shape] * vertexes[blockmodelrenderer$neighborinfo.vert2Weights[3].shape];
                float f20 = vertexes[blockmodelrenderer$neighborinfo.vert2Weights[4].shape] * vertexes[blockmodelrenderer$neighborinfo.vert2Weights[5].shape];
                float f21 = vertexes[blockmodelrenderer$neighborinfo.vert2Weights[6].shape] * vertexes[blockmodelrenderer$neighborinfo.vert2Weights[7].shape];
                float f22 = vertexes[blockmodelrenderer$neighborinfo.vert3Weights[0].shape] * vertexes[blockmodelrenderer$neighborinfo.vert3Weights[1].shape];
                float f23 = vertexes[blockmodelrenderer$neighborinfo.vert3Weights[2].shape] * vertexes[blockmodelrenderer$neighborinfo.vert3Weights[3].shape];
                float f24 = vertexes[blockmodelrenderer$neighborinfo.vert3Weights[4].shape] * vertexes[blockmodelrenderer$neighborinfo.vert3Weights[5].shape];
                float f25 = vertexes[blockmodelrenderer$neighborinfo.vert3Weights[6].shape] * vertexes[blockmodelrenderer$neighborinfo.vert3Weights[7].shape];
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f29 * f10 + f31 * f11 + f32 * f12 + f33 * f13;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f29 * f14 + f31 * f15 + f32 * f16 + f33 * f17;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f29 * f18 + f31 * f19 + f32 * f20 + f33 * f21;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f29 * f22 + f31 * f23 + f32 * f24 + f33 * f25;
                int i2 = this.getAoBrightness(l, i, j1, i3);
                int j2 = this.getAoBrightness(k, i, i1, i3);
                int k2 = this.getAoBrightness(k, j, k1, i3);
                int l2 = this.getAoBrightness(l, j, l1, i3);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.vert0] = this.getVertexBrightness(i2, j2, k2, l2, f10, f11, f12, f13);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.vert1] = this.getVertexBrightness(i2, j2, k2, l2, f14, f15, f16, f17);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.vert2] = this.getVertexBrightness(i2, j2, k2, l2, f18, f19, f20, f21);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.vert3] = this.getVertexBrightness(i2, j2, k2, l2, f22, f23, f24, f25);
            }
            else
            {
                float f6 = (f3 + f + f26 + f5) * 0.25F;
                float f7 = (f2 + f + f4 + f5) * 0.25F;
                float f8 = (f2 + f1 + f27 + f5) * 0.25F;
                float f9 = (f3 + f1 + f28 + f5) * 0.25F;
                this.vertexBrightness[blockmodelrenderer$vertextranslations.vert0] = this.getAoBrightness(l, i, j1, i3);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.vert1] = this.getAoBrightness(k, i, i1, i3);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.vert2] = this.getAoBrightness(k, j, k1, i3);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.vert3] = this.getAoBrightness(l, j, l1, i3);
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f6;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f7;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f8;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f9;
            }

            float f30 = reader.func_230487_a_(direction, applyDiffuseLighting);

            for (int j3 = 0; j3 < this.vertexColorMultiplier.length; ++j3)
            {
                this.vertexColorMultiplier[j3] *= f30;
            }
        }

        private int getAoBrightness(int br1, int br2, int br3, int br4)
        {
            if (br1 == 0)
            {
                br1 = br4;
            }

            if (br2 == 0)
            {
                br2 = br4;
            }

            if (br3 == 0)
            {
                br3 = br4;
            }

            return br1 + br2 + br3 + br4 >> 2 & 16711935;
        }

        private int getVertexBrightness(int b1, int b2, int b3, int b4, float w1, float w2, float w3, float w4)
        {
            int i = (int)((float)(b1 >> 16 & 255) * w1 + (float)(b2 >> 16 & 255) * w2 + (float)(b3 >> 16 & 255) * w3 + (float)(b4 >> 16 & 255) * w4) & 255;
            int j = (int)((float)(b1 & 255) * w1 + (float)(b2 & 255) * w2 + (float)(b3 & 255) * w3 + (float)(b4 & 255) * w4) & 255;
            return i << 16 | j;
        }
    }

    static class Cache
    {
        private boolean enabled;
        private final Long2IntLinkedOpenHashMap packedLightCache = Util.make(() ->
        {
            Long2IntLinkedOpenHashMap long2intlinkedopenhashmap = new Long2IntLinkedOpenHashMap(100, 0.25F)
            {
                protected void rehash(int p_rehash_1_)
                {
                }
            };
            long2intlinkedopenhashmap.defaultReturnValue(Integer.MAX_VALUE);
            return long2intlinkedopenhashmap;
        });
        private final Long2FloatLinkedOpenHashMap brightnessCache = Util.make(() ->
        {
            Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = new Long2FloatLinkedOpenHashMap(100, 0.25F)
            {
                protected void rehash(int p_rehash_1_)
                {
                }
            };
            long2floatlinkedopenhashmap.defaultReturnValue(Float.NaN);
            return long2floatlinkedopenhashmap;
        });

        private Cache()
        {
        }

        public void enable()
        {
            this.enabled = true;
        }

        public void disable()
        {
            this.enabled = false;
            this.packedLightCache.clear();
            this.brightnessCache.clear();
        }

        public int getPackedLight(BlockState blockStateIn, IBlockDisplayReader lightReaderIn, BlockPos blockPosIn)
        {
            long i = blockPosIn.toLong();

            if (this.enabled)
            {
                int j = this.packedLightCache.get(i);

                if (j != Integer.MAX_VALUE)
                {
                    return j;
                }
            }

            int k = WorldRenderer.getPackedLightmapCoords(lightReaderIn, blockStateIn, blockPosIn);

            if (this.enabled)
            {
                if (this.packedLightCache.size() == 100)
                {
                    this.packedLightCache.removeFirstInt();
                }

                this.packedLightCache.put(i, k);
            }

            return k;
        }

        public float getBrightness(BlockState blockStateIn, IBlockDisplayReader lightReaderIn, BlockPos blockPosIn)
        {
            long i = blockPosIn.toLong();

            if (this.enabled)
            {
                float f = this.brightnessCache.get(i);

                if (!Float.isNaN(f))
                {
                    return f;
                }
            }

            float f1 = blockStateIn.getAmbientOcclusionLightValue(lightReaderIn, blockPosIn);

            if (this.enabled)
            {
                if (this.brightnessCache.size() == 100)
                {
                    this.brightnessCache.removeFirstFloat();
                }

                this.brightnessCache.put(i, f1);
            }

            return f1;
        }
    }

    public static enum NeighborInfo
    {
        DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.SOUTH}),
        UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.SOUTH}),
        NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST}),
        SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST}),
        WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH}),
        EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH});

        private final Direction[] corners;
        private final boolean doNonCubicWeight;
        private final BlockModelRenderer.Orientation[] vert0Weights;
        private final BlockModelRenderer.Orientation[] vert1Weights;
        private final BlockModelRenderer.Orientation[] vert2Weights;
        private final BlockModelRenderer.Orientation[] vert3Weights;
        private static final BlockModelRenderer.NeighborInfo[] VALUES = Util.make(new BlockModelRenderer.NeighborInfo[6], (p_lambda$static$0_0_) -> {
            p_lambda$static$0_0_[Direction.DOWN.getIndex()] = DOWN;
            p_lambda$static$0_0_[Direction.UP.getIndex()] = UP;
            p_lambda$static$0_0_[Direction.NORTH.getIndex()] = NORTH;
            p_lambda$static$0_0_[Direction.SOUTH.getIndex()] = SOUTH;
            p_lambda$static$0_0_[Direction.WEST.getIndex()] = WEST;
            p_lambda$static$0_0_[Direction.EAST.getIndex()] = EAST;
        });

        private NeighborInfo(Direction[] cornersIn, float brightness, boolean doNonCubicWeightIn, BlockModelRenderer.Orientation[] vert0WeightsIn, BlockModelRenderer.Orientation[] vert1WeightsIn, BlockModelRenderer.Orientation[] vert2WeightsIn, BlockModelRenderer.Orientation[] vert3WeightsIn)
        {
            this.corners = cornersIn;
            this.doNonCubicWeight = doNonCubicWeightIn;
            this.vert0Weights = vert0WeightsIn;
            this.vert1Weights = vert1WeightsIn;
            this.vert2Weights = vert2WeightsIn;
            this.vert3Weights = vert3WeightsIn;
        }

        public static BlockModelRenderer.NeighborInfo getNeighbourInfo(Direction facing)
        {
            return VALUES[facing.getIndex()];
        }
    }

    public static enum Orientation
    {
        DOWN(Direction.DOWN, false),
        UP(Direction.UP, false),
        NORTH(Direction.NORTH, false),
        SOUTH(Direction.SOUTH, false),
        WEST(Direction.WEST, false),
        EAST(Direction.EAST, false),
        FLIP_DOWN(Direction.DOWN, true),
        FLIP_UP(Direction.UP, true),
        FLIP_NORTH(Direction.NORTH, true),
        FLIP_SOUTH(Direction.SOUTH, true),
        FLIP_WEST(Direction.WEST, true),
        FLIP_EAST(Direction.EAST, true);

        private final int shape;

        private Orientation(Direction facingIn, boolean flip)
        {
            this.shape = facingIn.getIndex() + (flip ? Direction.values().length : 0);
        }
    }

    static enum VertexTranslations
    {
        DOWN(0, 1, 2, 3),
        UP(2, 3, 0, 1),
        NORTH(3, 0, 1, 2),
        SOUTH(0, 1, 2, 3),
        WEST(3, 0, 1, 2),
        EAST(1, 2, 3, 0);

        private final int vert0;
        private final int vert1;
        private final int vert2;
        private final int vert3;
        private static final BlockModelRenderer.VertexTranslations[] VALUES = Util.make(new BlockModelRenderer.VertexTranslations[6], (p_lambda$static$0_0_) -> {
            p_lambda$static$0_0_[Direction.DOWN.getIndex()] = DOWN;
            p_lambda$static$0_0_[Direction.UP.getIndex()] = UP;
            p_lambda$static$0_0_[Direction.NORTH.getIndex()] = NORTH;
            p_lambda$static$0_0_[Direction.SOUTH.getIndex()] = SOUTH;
            p_lambda$static$0_0_[Direction.WEST.getIndex()] = WEST;
            p_lambda$static$0_0_[Direction.EAST.getIndex()] = EAST;
        });

        private VertexTranslations(int vert0In, int vert1In, int vert2In, int vert3In)
        {
            this.vert0 = vert0In;
            this.vert1 = vert1In;
            this.vert2 = vert2In;
            this.vert3 = vert3In;
        }

        public static BlockModelRenderer.VertexTranslations getVertexTranslations(Direction facingIn)
        {
            return VALUES[facingIn.getIndex()];
        }
    }
}
