package net.minecraft.client.renderer.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.optifine.Config;
import net.optifine.IRandomEntity;
import net.optifine.RandomEntities;
import net.optifine.entity.model.anim.ModelUpdater;
import net.optifine.model.ModelSprite;
import net.optifine.render.BoxVertexPositions;
import net.optifine.render.VertexPosition;
import net.optifine.shaders.Shaders;

public class ModelRenderer
{
    public float textureWidth = 64.0F;
    public float textureHeight = 32.0F;
    private int textureOffsetX;
    private int textureOffsetY;
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    public boolean mirror;
    public boolean showModel = true;
    public final ObjectList<ModelRenderer.ModelBox> cubeList = new ObjectArrayList<>();
    public final ObjectList<ModelRenderer> childModels = new ObjectArrayList<>();
    public List spriteList = new ArrayList();
    public boolean mirrorV = false;
    public float scaleX = 1.0F;
    public float scaleY = 1.0F;
    public float scaleZ = 1.0F;
    private ResourceLocation textureLocation = null;
    private String id = null;
    private ModelUpdater modelUpdater;
    private WorldRenderer renderGlobal = Config.getRenderGlobal();

    public ModelRenderer(Model model)
    {
        model.accept(this);
        this.setTextureSize(model.textureWidth, model.textureHeight);
    }

    public ModelRenderer(Model model, int texOffX, int texOffY)
    {
        this(model.textureWidth, model.textureHeight, texOffX, texOffY);
        model.accept(this);
    }

    public ModelRenderer(int textureWidthIn, int textureHeightIn, int textureOffsetXIn, int textureOffsetYIn)
    {
        this.setTextureSize(textureWidthIn, textureHeightIn);
        this.setTextureOffset(textureOffsetXIn, textureOffsetYIn);
    }

    private ModelRenderer()
    {
    }

    public ModelRenderer getModelAngleCopy()
    {
        ModelRenderer modelrenderer = new ModelRenderer();
        modelrenderer.copyModelAngles(this);
        return modelrenderer;
    }

    public void copyModelAngles(ModelRenderer modelRendererIn)
    {
        this.rotateAngleX = modelRendererIn.rotateAngleX;
        this.rotateAngleY = modelRendererIn.rotateAngleY;
        this.rotateAngleZ = modelRendererIn.rotateAngleZ;
        this.rotationPointX = modelRendererIn.rotationPointX;
        this.rotationPointY = modelRendererIn.rotationPointY;
        this.rotationPointZ = modelRendererIn.rotationPointZ;
    }

    /**
     * Sets the current box's rotation points and rotation angles to another box.
     */
    public void addChild(ModelRenderer renderer)
    {
        this.childModels.add(renderer);
    }

    public ModelRenderer setTextureOffset(int x, int y)
    {
        this.textureOffsetX = x;
        this.textureOffsetY = y;
        return this;
    }

    public ModelRenderer addBox(String partName, float x, float y, float z, int width, int height, int depth, float delta, int texX, int texY)
    {
        this.setTextureOffset(texX, texY);
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, (float)width, (float)height, (float)depth, delta, delta, delta, this.mirror, false);
        return this;
    }

    public ModelRenderer addBox(float x, float y, float z, float width, float height, float depth)
    {
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, 0.0F, 0.0F, 0.0F, this.mirror, false);
        return this;
    }

    public ModelRenderer addBox(float x, float y, float z, float width, float height, float depth, boolean mirrorIn)
    {
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, 0.0F, 0.0F, 0.0F, mirrorIn, false);
        return this;
    }

    public void addBox(float x, float y, float z, float width, float height, float depth, float delta)
    {
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, delta, delta, delta, this.mirror, false);
    }

    public void addBox(float x, float y, float z, float width, float height, float depth, float deltaX, float deltaY, float deltaZ)
    {
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, deltaX, deltaY, deltaZ, this.mirror, false);
    }

    public void addBox(float x, float y, float z, float width, float height, float depth, float delta, boolean mirrorIn)
    {
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, delta, delta, delta, mirrorIn, false);
    }

    private void addBox(int texOffX, int texOffY, float x, float y, float z, float width, float height, float depth, float deltaX, float deltaY, float deltaZ, boolean mirorIn, boolean p_228305_13_)
    {
        this.cubeList.add(new ModelRenderer.ModelBox(texOffX, texOffY, x, y, z, width, height, depth, deltaX, deltaY, deltaZ, mirorIn, this.textureWidth, this.textureHeight));
    }

    public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn)
    {
        this.rotationPointX = rotationPointXIn;
        this.rotationPointY = rotationPointYIn;
        this.rotationPointZ = rotationPointZIn;
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn)
    {
        this.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        if (this.showModel && (!this.cubeList.isEmpty() || !this.childModels.isEmpty() || !this.spriteList.isEmpty()))
        {
            RenderType rendertype = null;
            IRenderTypeBuffer.Impl irendertypebuffer$impl = null;

            if (this.textureLocation != null)
            {
                if (this.renderGlobal.renderOverlayEyes)
                {
                    return;
                }

                irendertypebuffer$impl = bufferIn.getRenderTypeBuffer();

                if (irendertypebuffer$impl != null)
                {
                    IVertexBuilder ivertexbuilder = bufferIn.getSecondaryBuilder();
                    rendertype = irendertypebuffer$impl.getLastRenderType();
                    bufferIn = irendertypebuffer$impl.getBuffer(this.textureLocation, bufferIn);

                    if (ivertexbuilder != null)
                    {
                        bufferIn = VertexBuilderUtils.newDelegate(ivertexbuilder, bufferIn);
                    }
                }
            }

            if (this.modelUpdater != null)
            {
                this.modelUpdater.update();
            }

            matrixStackIn.push();
            this.translateRotate(matrixStackIn);
            this.doRender(matrixStackIn.getLast(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            int j = this.childModels.size();

            for (int i = 0; i < j; ++i)
            {
                ModelRenderer modelrenderer = this.childModels.get(i);
                modelrenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }

            int k = this.spriteList.size();

            for (int l = 0; l < k; ++l)
            {
                ModelSprite modelsprite = (ModelSprite)this.spriteList.get(l);
                modelsprite.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }

            matrixStackIn.pop();

            if (rendertype != null)
            {
                irendertypebuffer$impl.getBuffer(rendertype);
            }
        }
    }

    public void translateRotate(MatrixStack matrixStackIn)
    {
        matrixStackIn.translate((double)(this.rotationPointX / 16.0F), (double)(this.rotationPointY / 16.0F), (double)(this.rotationPointZ / 16.0F));

        if (this.rotateAngleZ != 0.0F)
        {
            matrixStackIn.rotate(Vector3f.ZP.rotation(this.rotateAngleZ));
        }

        if (this.rotateAngleY != 0.0F)
        {
            matrixStackIn.rotate(Vector3f.YP.rotation(this.rotateAngleY));
        }

        if (this.rotateAngleX != 0.0F)
        {
            matrixStackIn.rotate(Vector3f.XP.rotation(this.rotateAngleX));
        }
    }

    private void doRender(MatrixStack.Entry matrixEntryIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        Matrix4f matrix4f = matrixEntryIn.getMatrix();
        Matrix3f matrix3f = matrixEntryIn.getNormal();
        boolean flag = Config.isShaders() && Shaders.useVelocityAttrib && Config.isMinecraftThread();
        int i = this.cubeList.size();

        for (int j = 0; j < i; ++j)
        {
            ModelRenderer.ModelBox modelrenderer$modelbox = this.cubeList.get(j);
            VertexPosition[][] avertexposition = (VertexPosition[][])null;

            if (flag)
            {
                IRandomEntity irandomentity = RandomEntities.getRandomEntityRendered();

                if (irandomentity != null)
                {
                    avertexposition = modelrenderer$modelbox.getBoxVertexPositions(irandomentity.getId());
                }
            }

            int i1 = modelrenderer$modelbox.quads.length;

            for (int k = 0; k < i1; ++k)
            {
                ModelRenderer.TexturedQuad modelrenderer$texturedquad = modelrenderer$modelbox.quads[k];

                if (modelrenderer$texturedquad != null)
                {
                    if (avertexposition != null)
                    {
                        bufferIn.setQuadVertexPositions(avertexposition[k]);
                    }

                    Vector3f vector3f = bufferIn.getTempVec3f(modelrenderer$texturedquad.normal);
                    vector3f.transform(matrix3f);
                    float f = vector3f.getX();
                    float f1 = vector3f.getY();
                    float f2 = vector3f.getZ();

                    for (int l = 0; l < 4; ++l)
                    {
                        ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex = modelrenderer$texturedquad.vertexPositions[l];
                        float f3 = modelrenderer$positiontexturevertex.position.getX() / 16.0F;
                        float f4 = modelrenderer$positiontexturevertex.position.getY() / 16.0F;
                        float f5 = modelrenderer$positiontexturevertex.position.getZ() / 16.0F;
                        float f6 = matrix4f.getTransformX(f3, f4, f5, 1.0F);
                        float f7 = matrix4f.getTransformY(f3, f4, f5, 1.0F);
                        float f8 = matrix4f.getTransformZ(f3, f4, f5, 1.0F);
                        bufferIn.addVertex(f6, f7, f8, red, green, blue, alpha, modelrenderer$positiontexturevertex.textureU, modelrenderer$positiontexturevertex.textureV, packedOverlayIn, packedLightIn, f, f1, f2);
                    }
                }
            }
        }
    }

    /**
     * Returns the model renderer with the new texture parameters.
     */
    public ModelRenderer setTextureSize(int textureWidthIn, int textureHeightIn)
    {
        this.textureWidth = (float)textureWidthIn;
        this.textureHeight = (float)textureHeightIn;
        return this;
    }

    public ModelRenderer.ModelBox getRandomCube(Random randomIn)
    {
        return this.cubeList.get(randomIn.nextInt(this.cubeList.size()));
    }

    public void addSprite(float p_addSprite_1_, float p_addSprite_2_, float p_addSprite_3_, int p_addSprite_4_, int p_addSprite_5_, int p_addSprite_6_, float p_addSprite_7_)
    {
        this.spriteList.add(new ModelSprite(this, this.textureOffsetX, this.textureOffsetY, p_addSprite_1_, p_addSprite_2_, p_addSprite_3_, p_addSprite_4_, p_addSprite_5_, p_addSprite_6_, p_addSprite_7_));
    }

    public ResourceLocation getTextureLocation()
    {
        return this.textureLocation;
    }

    public void setTextureLocation(ResourceLocation p_setTextureLocation_1_)
    {
        this.textureLocation = p_setTextureLocation_1_;
    }

    public String getId()
    {
        return this.id;
    }

    public void setId(String p_setId_1_)
    {
        this.id = p_setId_1_;
    }

    public void addBox(int[][] p_addBox_1_, float p_addBox_2_, float p_addBox_3_, float p_addBox_4_, float p_addBox_5_, float p_addBox_6_, float p_addBox_7_, float p_addBox_8_)
    {
        this.cubeList.add(new ModelRenderer.ModelBox(p_addBox_1_, p_addBox_2_, p_addBox_3_, p_addBox_4_, p_addBox_5_, p_addBox_6_, p_addBox_7_, p_addBox_8_, p_addBox_8_, p_addBox_8_, this.mirror, this.textureWidth, this.textureHeight));
    }

    public ModelRenderer getChild(int p_getChild_1_)
    {
        if (this.childModels == null)
        {
            return null;
        }
        else
        {
            return p_getChild_1_ >= 0 && p_getChild_1_ < this.childModels.size() ? this.childModels.get(p_getChild_1_) : null;
        }
    }

    public ModelRenderer getChild(String p_getChild_1_)
    {
        if (p_getChild_1_ == null)
        {
            return null;
        }
        else
        {
            if (this.childModels != null)
            {
                for (int i = 0; i < this.childModels.size(); ++i)
                {
                    ModelRenderer modelrenderer = this.childModels.get(i);

                    if (p_getChild_1_.equals(modelrenderer.getId()))
                    {
                        return modelrenderer;
                    }
                }
            }

            return null;
        }
    }

    public ModelRenderer getChildDeep(String p_getChildDeep_1_)
    {
        if (p_getChildDeep_1_ == null)
        {
            return null;
        }
        else
        {
            ModelRenderer modelrenderer = this.getChild(p_getChildDeep_1_);

            if (modelrenderer != null)
            {
                return modelrenderer;
            }
            else
            {
                if (this.childModels != null)
                {
                    for (int i = 0; i < this.childModels.size(); ++i)
                    {
                        ModelRenderer modelrenderer1 = this.childModels.get(i);
                        ModelRenderer modelrenderer2 = modelrenderer1.getChildDeep(p_getChildDeep_1_);

                        if (modelrenderer2 != null)
                        {
                            return modelrenderer2;
                        }
                    }
                }

                return null;
            }
        }
    }

    public void setModelUpdater(ModelUpdater p_setModelUpdater_1_)
    {
        this.modelUpdater = p_setModelUpdater_1_;
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("id: " + this.id + ", boxes: " + (this.cubeList != null ? this.cubeList.size() : null) + ", submodels: " + (this.childModels != null ? this.childModels.size() : null));
        return stringbuffer.toString();
    }

    public static class ModelBox
    {
        private final ModelRenderer.TexturedQuad[] quads;
        public final float posX1;
        public final float posY1;
        public final float posZ1;
        public final float posX2;
        public final float posY2;
        public final float posZ2;
        private BoxVertexPositions boxVertexPositions;

        public ModelBox(int texOffX, int texOffY, float x, float y, float z, float width, float height, float depth, float deltaX, float deltaY, float deltaZ, boolean mirorIn, float texWidth, float texHeight)
        {
            this.posX1 = x;
            this.posY1 = y;
            this.posZ1 = z;
            this.posX2 = x + width;
            this.posY2 = y + height;
            this.posZ2 = z + depth;
            this.quads = new ModelRenderer.TexturedQuad[6];
            float f = x + width;
            float f1 = y + height;
            float f2 = z + depth;
            x = x - deltaX;
            y = y - deltaY;
            z = z - deltaZ;
            f = f + deltaX;
            f1 = f1 + deltaY;
            f2 = f2 + deltaZ;

            if (mirorIn)
            {
                float f3 = f;
                f = x;
                x = f3;
            }

            ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex7 = new ModelRenderer.PositionTextureVertex(x, y, z, 0.0F, 0.0F);
            ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex = new ModelRenderer.PositionTextureVertex(f, y, z, 0.0F, 8.0F);
            ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex1 = new ModelRenderer.PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
            ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex2 = new ModelRenderer.PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
            ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex3 = new ModelRenderer.PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
            ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex4 = new ModelRenderer.PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
            ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex5 = new ModelRenderer.PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
            ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex6 = new ModelRenderer.PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);
            float f4 = (float)texOffX;
            float f5 = (float)texOffX + depth;
            float f6 = (float)texOffX + depth + width;
            float f7 = (float)texOffX + depth + width + width;
            float f8 = (float)texOffX + depth + width + depth;
            float f9 = (float)texOffX + depth + width + depth + width;
            float f10 = (float)texOffY;
            float f11 = (float)texOffY + depth;
            float f12 = (float)texOffY + depth + height;
            this.quads[2] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[] {modelrenderer$positiontexturevertex4, modelrenderer$positiontexturevertex3, modelrenderer$positiontexturevertex7, modelrenderer$positiontexturevertex}, f5, f10, f6, f11, texWidth, texHeight, mirorIn, Direction.DOWN);
            this.quads[3] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[] {modelrenderer$positiontexturevertex1, modelrenderer$positiontexturevertex2, modelrenderer$positiontexturevertex6, modelrenderer$positiontexturevertex5}, f6, f11, f7, f10, texWidth, texHeight, mirorIn, Direction.UP);
            this.quads[1] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[] {modelrenderer$positiontexturevertex7, modelrenderer$positiontexturevertex3, modelrenderer$positiontexturevertex6, modelrenderer$positiontexturevertex2}, f4, f11, f5, f12, texWidth, texHeight, mirorIn, Direction.WEST);
            this.quads[4] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[] {modelrenderer$positiontexturevertex, modelrenderer$positiontexturevertex7, modelrenderer$positiontexturevertex2, modelrenderer$positiontexturevertex1}, f5, f11, f6, f12, texWidth, texHeight, mirorIn, Direction.NORTH);
            this.quads[0] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[] {modelrenderer$positiontexturevertex4, modelrenderer$positiontexturevertex, modelrenderer$positiontexturevertex1, modelrenderer$positiontexturevertex5}, f6, f11, f8, f12, texWidth, texHeight, mirorIn, Direction.EAST);
            this.quads[5] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[] {modelrenderer$positiontexturevertex3, modelrenderer$positiontexturevertex4, modelrenderer$positiontexturevertex5, modelrenderer$positiontexturevertex6}, f8, f11, f9, f12, texWidth, texHeight, mirorIn, Direction.SOUTH);
        }

        public ModelBox(int[][] p_i242122_1_, float p_i242122_2_, float p_i242122_3_, float p_i242122_4_, float p_i242122_5_, float p_i242122_6_, float p_i242122_7_, float p_i242122_8_, float p_i242122_9_, float p_i242122_10_, boolean p_i242122_11_, float p_i242122_12_, float p_i242122_13_)
        {
            this.posX1 = p_i242122_2_;
            this.posY1 = p_i242122_3_;
            this.posZ1 = p_i242122_4_;
            this.posX2 = p_i242122_2_ + p_i242122_5_;
            this.posY2 = p_i242122_3_ + p_i242122_6_;
            this.posZ2 = p_i242122_4_ + p_i242122_7_;
            this.quads = new ModelRenderer.TexturedQuad[6];
            float f = p_i242122_2_ + p_i242122_5_;
            float f1 = p_i242122_3_ + p_i242122_6_;
            float f2 = p_i242122_4_ + p_i242122_7_;
            p_i242122_2_ = p_i242122_2_ - p_i242122_8_;
            p_i242122_3_ = p_i242122_3_ - p_i242122_9_;
            p_i242122_4_ = p_i242122_4_ - p_i242122_10_;
            f = f + p_i242122_8_;
            f1 = f1 + p_i242122_9_;
            f2 = f2 + p_i242122_10_;

            if (p_i242122_11_)
            {
                float f3 = f;
                f = p_i242122_2_;
                p_i242122_2_ = f3;
            }

            ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex7 = new ModelRenderer.PositionTextureVertex(p_i242122_2_, p_i242122_3_, p_i242122_4_, 0.0F, 0.0F);
            ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex = new ModelRenderer.PositionTextureVertex(f, p_i242122_3_, p_i242122_4_, 0.0F, 8.0F);
            ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex1 = new ModelRenderer.PositionTextureVertex(f, f1, p_i242122_4_, 8.0F, 8.0F);
            ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex2 = new ModelRenderer.PositionTextureVertex(p_i242122_2_, f1, p_i242122_4_, 8.0F, 0.0F);
            ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex3 = new ModelRenderer.PositionTextureVertex(p_i242122_2_, p_i242122_3_, f2, 0.0F, 0.0F);
            ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex4 = new ModelRenderer.PositionTextureVertex(f, p_i242122_3_, f2, 0.0F, 8.0F);
            ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex5 = new ModelRenderer.PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
            ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex6 = new ModelRenderer.PositionTextureVertex(p_i242122_2_, f1, f2, 8.0F, 0.0F);
            this.quads[2] = this.makeTexturedQuad(new ModelRenderer.PositionTextureVertex[] {modelrenderer$positiontexturevertex4, modelrenderer$positiontexturevertex3, modelrenderer$positiontexturevertex7, modelrenderer$positiontexturevertex}, p_i242122_1_[1], true, p_i242122_12_, p_i242122_13_, p_i242122_11_, Direction.DOWN);
            this.quads[3] = this.makeTexturedQuad(new ModelRenderer.PositionTextureVertex[] {modelrenderer$positiontexturevertex1, modelrenderer$positiontexturevertex2, modelrenderer$positiontexturevertex6, modelrenderer$positiontexturevertex5}, p_i242122_1_[0], true, p_i242122_12_, p_i242122_13_, p_i242122_11_, Direction.UP);
            this.quads[1] = this.makeTexturedQuad(new ModelRenderer.PositionTextureVertex[] {modelrenderer$positiontexturevertex7, modelrenderer$positiontexturevertex3, modelrenderer$positiontexturevertex6, modelrenderer$positiontexturevertex2}, p_i242122_1_[5], false, p_i242122_12_, p_i242122_13_, p_i242122_11_, Direction.WEST);
            this.quads[4] = this.makeTexturedQuad(new ModelRenderer.PositionTextureVertex[] {modelrenderer$positiontexturevertex, modelrenderer$positiontexturevertex7, modelrenderer$positiontexturevertex2, modelrenderer$positiontexturevertex1}, p_i242122_1_[2], false, p_i242122_12_, p_i242122_13_, p_i242122_11_, Direction.NORTH);
            this.quads[0] = this.makeTexturedQuad(new ModelRenderer.PositionTextureVertex[] {modelrenderer$positiontexturevertex4, modelrenderer$positiontexturevertex, modelrenderer$positiontexturevertex1, modelrenderer$positiontexturevertex5}, p_i242122_1_[4], false, p_i242122_12_, p_i242122_13_, p_i242122_11_, Direction.EAST);
            this.quads[5] = this.makeTexturedQuad(new ModelRenderer.PositionTextureVertex[] {modelrenderer$positiontexturevertex3, modelrenderer$positiontexturevertex4, modelrenderer$positiontexturevertex5, modelrenderer$positiontexturevertex6}, p_i242122_1_[3], false, p_i242122_12_, p_i242122_13_, p_i242122_11_, Direction.SOUTH);
        }

        private ModelRenderer.TexturedQuad makeTexturedQuad(ModelRenderer.PositionTextureVertex[] p_makeTexturedQuad_1_, int[] p_makeTexturedQuad_2_, boolean p_makeTexturedQuad_3_, float p_makeTexturedQuad_4_, float p_makeTexturedQuad_5_, boolean p_makeTexturedQuad_6_, Direction p_makeTexturedQuad_7_)
        {
            if (p_makeTexturedQuad_2_ == null)
            {
                return null;
            }
            else
            {
                return p_makeTexturedQuad_3_ ? new ModelRenderer.TexturedQuad(p_makeTexturedQuad_1_, (float)p_makeTexturedQuad_2_[2], (float)p_makeTexturedQuad_2_[3], (float)p_makeTexturedQuad_2_[0], (float)p_makeTexturedQuad_2_[1], p_makeTexturedQuad_4_, p_makeTexturedQuad_5_, p_makeTexturedQuad_6_, p_makeTexturedQuad_7_) : new ModelRenderer.TexturedQuad(p_makeTexturedQuad_1_, (float)p_makeTexturedQuad_2_[0], (float)p_makeTexturedQuad_2_[1], (float)p_makeTexturedQuad_2_[2], (float)p_makeTexturedQuad_2_[3], p_makeTexturedQuad_4_, p_makeTexturedQuad_5_, p_makeTexturedQuad_6_, p_makeTexturedQuad_7_);
            }
        }

        public VertexPosition[][] getBoxVertexPositions(int p_getBoxVertexPositions_1_)
        {
            if (this.boxVertexPositions == null)
            {
                this.boxVertexPositions = new BoxVertexPositions();
            }

            return this.boxVertexPositions.get(p_getBoxVertexPositions_1_);
        }
    }

    static class PositionTextureVertex
    {
        public final Vector3f position;
        public final float textureU;
        public final float textureV;

        public PositionTextureVertex(float x, float y, float z, float texU, float texV)
        {
            this(new Vector3f(x, y, z), texU, texV);
        }

        public ModelRenderer.PositionTextureVertex setTextureUV(float texU, float texV)
        {
            return new ModelRenderer.PositionTextureVertex(this.position, texU, texV);
        }

        public PositionTextureVertex(Vector3f posIn, float texU, float texV)
        {
            this.position = posIn;
            this.textureU = texU;
            this.textureV = texV;
        }
    }

    static class TexturedQuad
    {
        public final ModelRenderer.PositionTextureVertex[] vertexPositions;
        public final Vector3f normal;

        public TexturedQuad(ModelRenderer.PositionTextureVertex[] positionsIn, float u1, float v1, float u2, float v2, float texWidth, float texHeight, boolean mirrorIn, Direction directionIn)
        {
            this.vertexPositions = positionsIn;
            float f = 0.0F / texWidth;
            float f1 = 0.0F / texHeight;

            if (Config.isAntialiasing())
            {
                f = 0.05F / texWidth;
                f1 = 0.05F / texHeight;

                if (u2 < u1)
                {
                    f = -f;
                }

                if (v2 < v1)
                {
                    f1 = -f1;
                }
            }

            positionsIn[0] = positionsIn[0].setTextureUV(u2 / texWidth - f, v1 / texHeight + f1);
            positionsIn[1] = positionsIn[1].setTextureUV(u1 / texWidth + f, v1 / texHeight + f1);
            positionsIn[2] = positionsIn[2].setTextureUV(u1 / texWidth + f, v2 / texHeight - f1);
            positionsIn[3] = positionsIn[3].setTextureUV(u2 / texWidth - f, v2 / texHeight - f1);

            if (mirrorIn)
            {
                int i = positionsIn.length;

                for (int j = 0; j < i / 2; ++j)
                {
                    ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex = positionsIn[j];
                    positionsIn[j] = positionsIn[i - 1 - j];
                    positionsIn[i - 1 - j] = modelrenderer$positiontexturevertex;
                }
            }

            this.normal = directionIn.toVector3f();

            if (mirrorIn)
            {
                this.normal.mul(-1.0F, 1.0F, 1.0F);
            }
        }
    }
}
