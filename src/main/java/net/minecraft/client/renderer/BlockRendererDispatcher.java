package net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Random;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;
import net.optifine.reflect.Reflector;

public class BlockRendererDispatcher implements IResourceManagerReloadListener
{
    private final BlockModelShapes blockModelShapes;
    private final BlockModelRenderer blockModelRenderer;
    private final FluidBlockRenderer fluidRenderer;
    private final Random random = new Random();
    private final BlockColors blockColors;

    public BlockRendererDispatcher(BlockModelShapes shapes, BlockColors colors)
    {
        this.blockModelShapes = shapes;
        this.blockColors = colors;

        if (Reflector.ForgeBlockModelRenderer_Constructor.exists())
        {
            this.blockModelRenderer = (BlockModelRenderer)Reflector.newInstance(Reflector.ForgeBlockModelRenderer_Constructor, this.blockColors);
        }
        else
        {
            this.blockModelRenderer = new BlockModelRenderer(this.blockColors);
        }

        this.fluidRenderer = new FluidBlockRenderer();
    }

    public BlockModelShapes getBlockModelShapes()
    {
        return this.blockModelShapes;
    }

    public void renderBlockDamage(BlockState blockStateIn, BlockPos posIn, IBlockDisplayReader lightReaderIn, MatrixStack matrixStackIn, IVertexBuilder vertexBuilderIn)
    {
        this.renderBlockDamage(blockStateIn, posIn, lightReaderIn, matrixStackIn, vertexBuilderIn, EmptyModelData.INSTANCE);
    }

    public void renderBlockDamage(BlockState p_renderBlockDamage_1_, BlockPos p_renderBlockDamage_2_, IBlockDisplayReader p_renderBlockDamage_3_, MatrixStack p_renderBlockDamage_4_, IVertexBuilder p_renderBlockDamage_5_, IModelData p_renderBlockDamage_6_)
    {
        if (p_renderBlockDamage_1_.getRenderType() == BlockRenderType.MODEL)
        {
            IBakedModel ibakedmodel = this.blockModelShapes.getModel(p_renderBlockDamage_1_);
            long i = p_renderBlockDamage_1_.getPositionRandom(p_renderBlockDamage_2_);
            this.blockModelRenderer.renderModel(p_renderBlockDamage_3_, ibakedmodel, p_renderBlockDamage_1_, p_renderBlockDamage_2_, p_renderBlockDamage_4_, p_renderBlockDamage_5_, true, this.random, i, OverlayTexture.NO_OVERLAY, p_renderBlockDamage_6_);
        }
    }

    public boolean renderModel(BlockState blockStateIn, BlockPos posIn, IBlockDisplayReader lightReaderIn, MatrixStack matrixStackIn, IVertexBuilder vertexBuilderIn, boolean checkSides, Random rand)
    {
        return this.renderModel(blockStateIn, posIn, lightReaderIn, matrixStackIn, vertexBuilderIn, checkSides, rand, EmptyModelData.INSTANCE);
    }

    public boolean renderModel(BlockState p_renderModel_1_, BlockPos p_renderModel_2_, IBlockDisplayReader p_renderModel_3_, MatrixStack p_renderModel_4_, IVertexBuilder p_renderModel_5_, boolean p_renderModel_6_, Random p_renderModel_7_, IModelData p_renderModel_8_)
    {
        try
        {
            BlockRenderType blockrendertype = p_renderModel_1_.getRenderType();
            return blockrendertype != BlockRenderType.MODEL ? false : this.blockModelRenderer.renderModel(p_renderModel_3_, this.getModelForState(p_renderModel_1_), p_renderModel_1_, p_renderModel_2_, p_renderModel_4_, p_renderModel_5_, p_renderModel_6_, p_renderModel_7_, p_renderModel_1_.getPositionRandom(p_renderModel_2_), OverlayTexture.NO_OVERLAY, p_renderModel_8_);
        }
        catch (Throwable throwable1)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Tesselating block in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, p_renderModel_2_, p_renderModel_1_);
            throw new ReportedException(crashreport);
        }
    }

    public boolean renderFluid(BlockPos posIn, IBlockDisplayReader lightReaderIn, IVertexBuilder vertexBuilderIn, FluidState fluidStateIn)
    {
        try
        {
            return this.fluidRenderer.render(lightReaderIn, posIn, vertexBuilderIn, fluidStateIn);
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating liquid in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, posIn, (BlockState)null);
            throw new ReportedException(crashreport);
        }
    }

    public BlockModelRenderer getBlockModelRenderer()
    {
        return this.blockModelRenderer;
    }

    public IBakedModel getModelForState(BlockState state)
    {
        return this.blockModelShapes.getModel(state);
    }

    public void renderBlock(BlockState blockStateIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferTypeIn, int combinedLightIn, int combinedOverlayIn)
    {
        this.renderBlock(blockStateIn, matrixStackIn, bufferTypeIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
    }

    public void renderBlock(BlockState p_renderBlock_1_, MatrixStack p_renderBlock_2_, IRenderTypeBuffer p_renderBlock_3_, int p_renderBlock_4_, int p_renderBlock_5_, IModelData p_renderBlock_6_)
    {
        BlockRenderType blockrendertype = p_renderBlock_1_.getRenderType();

        if (blockrendertype != BlockRenderType.INVISIBLE)
        {
            switch (blockrendertype)
            {
                case MODEL:
                    IBakedModel ibakedmodel = this.getModelForState(p_renderBlock_1_);
                    int i = this.blockColors.getColor(p_renderBlock_1_, (IBlockDisplayReader)null, (BlockPos)null, 0);
                    float f = (float)(i >> 16 & 255) / 255.0F;
                    float f1 = (float)(i >> 8 & 255) / 255.0F;
                    float f2 = (float)(i & 255) / 255.0F;
                    this.blockModelRenderer.renderModel(p_renderBlock_2_.getLast(), p_renderBlock_3_.getBuffer(RenderTypeLookup.func_239220_a_(p_renderBlock_1_, false)), p_renderBlock_1_, ibakedmodel, f, f1, f2, p_renderBlock_4_, p_renderBlock_5_, p_renderBlock_6_);
                    break;

                case ENTITYBLOCK_ANIMATED:
                    if (Reflector.IForgeItem_getItemStackTileEntityRenderer.exists())
                    {
                        ItemStack itemstack = new ItemStack(p_renderBlock_1_.getBlock());
                        ItemStackTileEntityRenderer itemstacktileentityrenderer = (ItemStackTileEntityRenderer)Reflector.call(itemstack.getItem(), Reflector.IForgeItem_getItemStackTileEntityRenderer);
                        itemstacktileentityrenderer.func_239207_a_(itemstack, ItemCameraTransforms.TransformType.NONE, p_renderBlock_2_, p_renderBlock_3_, p_renderBlock_4_, p_renderBlock_5_);
                    }
                    else
                    {
                        ItemStackTileEntityRenderer.instance.func_239207_a_(new ItemStack(p_renderBlock_1_.getBlock()), ItemCameraTransforms.TransformType.NONE, p_renderBlock_2_, p_renderBlock_3_, p_renderBlock_4_, p_renderBlock_5_);
                    }
            }
        }
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        this.fluidRenderer.initAtlasSprites();
    }

    public IResourceType getResourceType()
    {
        return VanillaResourceType.MODELS;
    }
}
