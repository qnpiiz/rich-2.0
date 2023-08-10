package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MooshroomRenderer;
import net.minecraft.client.renderer.entity.model.CowModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.optifine.Config;

public class MooshroomMushroomLayer<T extends MooshroomEntity> extends LayerRenderer<T, CowModel<T>>
{
    private ModelRenderer modelRendererMushroom;
    private static final ResourceLocation LOCATION_MUSHROOM_RED = new ResourceLocation("textures/entity/cow/red_mushroom.png");
    private static final ResourceLocation LOCATION_MUSHROOM_BROWN = new ResourceLocation("textures/entity/cow/brown_mushroom.png");
    private static boolean hasTextureMushroomRed = false;
    private static boolean hasTextureMushroomBrown = false;

    public MooshroomMushroomLayer(IEntityRenderer<T, CowModel<T>> rendererIn)
    {
        super(rendererIn);
        IEntityRenderer<T, CowModel<T>> mooshroomrenderer = rendererIn;
        this.modelRendererMushroom = new ModelRenderer(mooshroomrenderer.getEntityModel());
        this.modelRendererMushroom.setTextureSize(16, 16);
        this.modelRendererMushroom.rotationPointX = 8.0F;
        this.modelRendererMushroom.rotationPointZ = 8.0F;
        this.modelRendererMushroom.rotateAngleY = MathHelper.PI / 4.0F;
        int[][] aint = new int[][] {null, null, {16, 16, 0, 0}, {16, 16, 0, 0}, null, null};
        this.modelRendererMushroom.addBox(aint, -10.0F, 0.0F, 0.0F, 20.0F, 16.0F, 0.0F, 0.0F);
        int[][] aint1 = new int[][] {null, null, null, null, {16, 16, 0, 0}, {16, 16, 0, 0}};
        this.modelRendererMushroom.addBox(aint1, 0.0F, 0.0F, -10.0F, 0.0F, 16.0F, 20.0F, 0.0F);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if (!entitylivingbaseIn.isChild() && !entitylivingbaseIn.isInvisible())
        {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
            BlockState blockstate = entitylivingbaseIn.getMooshroomType().getRenderState();
            ResourceLocation resourcelocation = this.getCustomMushroom(blockstate);
            IVertexBuilder ivertexbuilder = null;

            if (resourcelocation != null)
            {
                ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutout(resourcelocation));
            }

            int i = LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0F);
            matrixStackIn.push();
            matrixStackIn.translate((double)0.2F, (double) - 0.35F, 0.5D);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-48.0F));
            matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
            matrixStackIn.translate(-0.5D, -0.5D, -0.5D);

            if (resourcelocation != null)
            {
                this.modelRendererMushroom.render(matrixStackIn, ivertexbuilder, packedLightIn, i);
            }
            else
            {
                blockrendererdispatcher.renderBlock(blockstate, matrixStackIn, bufferIn, packedLightIn, i);
            }

            matrixStackIn.pop();
            matrixStackIn.push();
            matrixStackIn.translate((double)0.2F, (double) - 0.35F, 0.5D);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(42.0F));
            matrixStackIn.translate((double)0.1F, 0.0D, (double) - 0.6F);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-48.0F));
            matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
            matrixStackIn.translate(-0.5D, -0.5D, -0.5D);

            if (resourcelocation != null)
            {
                this.modelRendererMushroom.render(matrixStackIn, ivertexbuilder, packedLightIn, i);
            }
            else
            {
                blockrendererdispatcher.renderBlock(blockstate, matrixStackIn, bufferIn, packedLightIn, i);
            }

            matrixStackIn.pop();
            matrixStackIn.push();
            this.getEntityModel().getHead().translateRotate(matrixStackIn);
            matrixStackIn.translate(0.0D, (double) - 0.7F, (double) - 0.2F);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-78.0F));
            matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
            matrixStackIn.translate(-0.5D, -0.5D, -0.5D);

            if (resourcelocation != null)
            {
                this.modelRendererMushroom.render(matrixStackIn, ivertexbuilder, packedLightIn, i);
            }
            else
            {
                blockrendererdispatcher.renderBlock(blockstate, matrixStackIn, bufferIn, packedLightIn, i);
            }

            matrixStackIn.pop();
        }
    }

    private ResourceLocation getCustomMushroom(BlockState p_getCustomMushroom_1_)
    {
        Block block = p_getCustomMushroom_1_.getBlock();

        if (block == Blocks.RED_MUSHROOM && hasTextureMushroomRed)
        {
            return LOCATION_MUSHROOM_RED;
        }
        else
        {
            return block == Blocks.BROWN_MUSHROOM && hasTextureMushroomBrown ? LOCATION_MUSHROOM_BROWN : null;
        }
    }

    public static void update()
    {
        hasTextureMushroomRed = Config.hasResource(LOCATION_MUSHROOM_RED);
        hasTextureMushroomBrown = Config.hasResource(LOCATION_MUSHROOM_BROWN);
    }
}
