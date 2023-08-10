package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.extensions.IForgeVertexBuilder;
import net.optifine.Config;
import net.optifine.IRandomEntity;
import net.optifine.RandomEntities;
import net.optifine.reflect.Reflector;
import net.optifine.render.RenderEnv;
import net.optifine.render.VertexPosition;
import net.optifine.shaders.Shaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

public interface IVertexBuilder extends IForgeVertexBuilder
{
    Logger LOGGER = LogManager.getLogger();
    ThreadLocal<RenderEnv> RENDER_ENV = ThreadLocal.withInitial(() ->
    {
        return new RenderEnv(Blocks.AIR.getDefaultState(), new BlockPos(0, 0, 0));
    });
    boolean FORGE = Reflector.ForgeHooksClient.exists();

default RenderEnv getRenderEnv(BlockState p_getRenderEnv_1_, BlockPos p_getRenderEnv_2_)
    {
        RenderEnv renderenv = RENDER_ENV.get();
        renderenv.reset(p_getRenderEnv_1_, p_getRenderEnv_2_);
        return renderenv;
    }

    IVertexBuilder pos(double x, double y, double z);

    IVertexBuilder color(int red, int green, int blue, int alpha);

    default IVertexBuilder color(Color color) {
        return this.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    default IVertexBuilder color(int color) {
        return this.color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >> 24 & 0xFF);
    }

    IVertexBuilder tex(float u, float v);

    IVertexBuilder overlay(int u, int v);

    IVertexBuilder lightmap(int u, int v);

    IVertexBuilder normal(float x, float y, float z);

    void endVertex();

default void addVertex(float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ)
    {
        this.pos((double)x, (double)y, (double)z);
        this.color(red, green, blue, alpha);
        this.tex(texU, texV);
        this.overlay(overlayUV);
        this.lightmap(lightmapUV);
        this.normal(normalX, normalY, normalZ);
        this.endVertex();
    }

default IVertexBuilder color(float red, float green, float blue, float alpha)
    {
        return this.color((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
    }

default IVertexBuilder lightmap(int lightmapUV)
    {
        return this.lightmap(lightmapUV & 65535, lightmapUV >> 16 & 65535);
    }

default IVertexBuilder overlay(int overlayUV)
    {
        return this.overlay(overlayUV & 65535, overlayUV >> 16 & 65535);
    }

default void addQuad(MatrixStack.Entry matrixEntryIn, BakedQuad quadIn, float redIn, float greenIn, float blueIn, int combinedLightIn, int combinedOverlayIn)
    {
        this.addQuad(matrixEntryIn, quadIn, this.getTempFloat4(1.0F, 1.0F, 1.0F, 1.0F), redIn, greenIn, blueIn, this.getTempInt4(combinedLightIn, combinedLightIn, combinedLightIn, combinedLightIn), combinedOverlayIn, false);
    }

default void addVertexData(MatrixStack.Entry p_addVertexData_1_, BakedQuad p_addVertexData_2_, float[] p_addVertexData_3_, float p_addVertexData_4_, float p_addVertexData_5_, float p_addVertexData_6_, float p_addVertexData_7_, int[] p_addVertexData_8_, int p_addVertexData_9_, boolean p_addVertexData_10_)
    {
        this.addQuad(p_addVertexData_1_, p_addVertexData_2_, p_addVertexData_3_, p_addVertexData_4_, p_addVertexData_5_, p_addVertexData_6_, p_addVertexData_7_, p_addVertexData_8_, p_addVertexData_9_, p_addVertexData_10_);
    }

default void addQuad(MatrixStack.Entry matrixEntryIn, BakedQuad quadIn, float[] colorMuls, float redIn, float greenIn, float blueIn, int[] combinedLightsIn, int combinedOverlayIn, boolean mulColor)
    {
        this.addQuad(matrixEntryIn, quadIn, colorMuls, redIn, greenIn, blueIn, 1.0F, combinedLightsIn, combinedOverlayIn, mulColor);
    }

default void addQuad(MatrixStack.Entry p_addQuad_1_, BakedQuad p_addQuad_2_, float[] p_addQuad_3_, float p_addQuad_4_, float p_addQuad_5_, float p_addQuad_6_, float p_addQuad_7_, int[] p_addQuad_8_, int p_addQuad_9_, boolean p_addQuad_10_)
    {
        int[] aint = this.isMultiTexture() ? p_addQuad_2_.getVertexDataSingle() : p_addQuad_2_.getVertexData();
        this.putSprite(p_addQuad_2_.getSprite());
        boolean flag = BlockModelRenderer.isSeparateAoLightValue();
        Vector3i vector3i = p_addQuad_2_.getFace().getDirectionVec();
        float f = (float)vector3i.getX();
        float f1 = (float)vector3i.getY();
        float f2 = (float)vector3i.getZ();
        Matrix4f matrix4f = p_addQuad_1_.getMatrix();
        Matrix3f matrix3f = p_addQuad_1_.getNormal();
        float f3 = matrix3f.getTransformX(f, f1, f2);
        float f4 = matrix3f.getTransformY(f, f1, f2);
        float f5 = matrix3f.getTransformZ(f, f1, f2);
        int i = 8;
        int j = DefaultVertexFormats.BLOCK.getIntegerSize();
        int k = aint.length / j;
        boolean flag1 = Config.isShaders() && Shaders.useVelocityAttrib && Config.isMinecraftThread();

        if (flag1)
        {
            IRandomEntity irandomentity = RandomEntities.getRandomEntityRendered();

            if (irandomentity != null)
            {
                VertexPosition[] avertexposition = p_addQuad_2_.getVertexPositions(irandomentity.getId());
                this.setQuadVertexPositions(avertexposition);
            }
        }

        for (int i1 = 0; i1 < k; ++i1)
        {
            int j1 = i1 * j;
            float f6 = Float.intBitsToFloat(aint[j1 + 0]);
            float f7 = Float.intBitsToFloat(aint[j1 + 1]);
            float f8 = Float.intBitsToFloat(aint[j1 + 2]);
            float f12 = 1.0F;
            float f13 = flag ? 1.0F : p_addQuad_3_[i1];
            float f9;
            float f10;
            float f11;

            if (p_addQuad_10_)
            {
                int l = aint[j1 + 3];
                float f14 = (float)(l & 255) / 255.0F;
                float f15 = (float)(l >> 8 & 255) / 255.0F;
                float f16 = (float)(l >> 16 & 255) / 255.0F;
                f9 = f14 * f13 * p_addQuad_4_;
                f10 = f15 * f13 * p_addQuad_5_;
                f11 = f16 * f13 * p_addQuad_6_;

                if (FORGE)
                {
                    float f17 = (float)(l >> 24 & 255) / 255.0F;
                    f12 = f17 * p_addQuad_7_;
                }
            }
            else
            {
                f9 = f13 * p_addQuad_4_;
                f10 = f13 * p_addQuad_5_;
                f11 = f13 * p_addQuad_6_;

                if (FORGE)
                {
                    f12 = p_addQuad_7_;
                }
            }

            int k1 = p_addQuad_8_[i1];

            if (FORGE)
            {
                k1 = this.applyBakedLighting(p_addQuad_8_[i1], aint, j1);
            }

            float f19 = Float.intBitsToFloat(aint[j1 + 4]);
            float f20 = Float.intBitsToFloat(aint[j1 + 5]);
            float f21 = matrix4f.getTransformX(f6, f7, f8, 1.0F);
            float f22 = matrix4f.getTransformY(f6, f7, f8, 1.0F);
            float f18 = matrix4f.getTransformZ(f6, f7, f8, 1.0F);

            if (FORGE)
            {
                Vector3f vector3f = this.applyBakedNormals(aint, j1, p_addQuad_1_.getNormal());

                if (vector3f != null)
                {
                    f3 = vector3f.getX();
                    f4 = vector3f.getY();
                    f5 = vector3f.getZ();
                }
            }

            if (flag)
            {
                f12 = p_addQuad_3_[i1];
            }

            this.addVertex(f21, f22, f18, f9, f10, f11, f12, f19, f20, p_addQuad_9_, k1, f3, f4, f5);
        }
    }

default IVertexBuilder pos(Matrix4f matrixIn, double x, double y, double z)
    {
        float f = matrixIn.getTransformX((float) x, (float) y, (float) z, 1.0F);
        float f1 = matrixIn.getTransformY((float) x, (float) y, (float) z, 1.0F);
        float f2 = matrixIn.getTransformZ((float) x, (float) y, (float) z, 1.0F);

        return this.pos((double)f, (double)f1, (double)f2);
    }

    default IVertexBuilder pos(MatrixStack matrixIn, double x, double y, double z)
    {
        return this.pos(matrixIn.getLast().getMatrix(), x, y, z);
    }

    default IVertexBuilder pos(MatrixStack matrixIn, double x, double y)
    {
        return this.pos(matrixIn, x, y, 0);
    }

default IVertexBuilder normal(Matrix3f matrixIn, float x, float y, float z)
    {
        float f = matrixIn.getTransformX(x, y, z);
        float f1 = matrixIn.getTransformY(x, y, z);
        float f2 = matrixIn.getTransformZ(x, y, z);
        return this.normal(f, f1, f2);
    }

default void putSprite(TextureAtlasSprite p_putSprite_1_)
    {
    }

default void setSprite(TextureAtlasSprite p_setSprite_1_)
    {
    }

default boolean isMultiTexture()
    {
        return false;
    }

default void setRenderType(RenderType p_setRenderType_1_)
    {
    }

default RenderType getRenderType()
    {
        return null;
    }

default void setRenderBlocks(boolean p_setRenderBlocks_1_)
    {
    }

default Vector3f getTempVec3f(Vector3f p_getTempVec3f_1_)
    {
        return p_getTempVec3f_1_.copy();
    }

default Vector3f getTempVec3f(float p_getTempVec3f_1_, float p_getTempVec3f_2_, float p_getTempVec3f_3_)
    {
        return new Vector3f(p_getTempVec3f_1_, p_getTempVec3f_2_, p_getTempVec3f_3_);
    }

default float[] getTempFloat4(float p_getTempFloat4_1_, float p_getTempFloat4_2_, float p_getTempFloat4_3_, float p_getTempFloat4_4_)
    {
        return new float[] {p_getTempFloat4_1_, p_getTempFloat4_2_, p_getTempFloat4_3_, p_getTempFloat4_4_};
    }

default int[] getTempInt4(int p_getTempInt4_1_, int p_getTempInt4_2_, int p_getTempInt4_3_, int p_getTempInt4_4_)
    {
        return new int[] {p_getTempInt4_1_, p_getTempInt4_2_, p_getTempInt4_3_, p_getTempInt4_4_};
    }

default IRenderTypeBuffer.Impl getRenderTypeBuffer()
    {
        return null;
    }

default void setQuadVertexPositions(VertexPosition[] p_setQuadVertexPositions_1_)
    {
    }

default void setMidBlock(float p_setMidBlock_1_, float p_setMidBlock_2_, float p_setMidBlock_3_)
    {
    }

default IVertexBuilder getSecondaryBuilder()
    {
        return null;
    }

default int applyBakedLighting(int p_applyBakedLighting_1_, int[] p_applyBakedLighting_2_, int p_applyBakedLighting_3_)
    {
        int i = getLightOffset(0);
        int j = LightTexture.getLightBlock(p_applyBakedLighting_2_[p_applyBakedLighting_3_ + i]);
        int k = LightTexture.getLightSky(p_applyBakedLighting_2_[p_applyBakedLighting_3_ + i]);

        if (j == 0 && k == 0)
        {
            return p_applyBakedLighting_1_;
        }
        else
        {
            int l = LightTexture.getLightBlock(p_applyBakedLighting_1_);
            int i1 = LightTexture.getLightSky(p_applyBakedLighting_1_);
            l = Math.max(l, j);
            i1 = Math.max(i1, k);
            return LightTexture.packLight(l, i1);
        }
    }

    static int getLightOffset(int p_getLightOffset_0_)
    {
        return p_getLightOffset_0_ * 8 + 6;
    }

default Vector3f applyBakedNormals(int[] p_applyBakedNormals_1_, int p_applyBakedNormals_2_, Matrix3f p_applyBakedNormals_3_)
    {
        int i = 7;
        int j = p_applyBakedNormals_1_[p_applyBakedNormals_2_ + i];
        byte b0 = (byte)(j >> 0 & 255);
        byte b1 = (byte)(j >> 8 & 255);
        byte b2 = (byte)(j >> 16 & 255);

        if (b0 == 0 && b1 == 0 && b2 == 0)
        {
            return null;
        }
        else
        {
            Vector3f vector3f = this.getTempVec3f((float)b0 / 127.0F, (float)b1 / 127.0F, (float)b2 / 127.0F);
            vector3f.transform(p_applyBakedNormals_3_);
            return vector3f;
        }
    }
}
