package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.MapData;
import net.optifine.Config;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.Shaders;

public class ItemFrameRenderer extends EntityRenderer<ItemFrameEntity>
{
    private static final ModelResourceLocation LOCATION_MODEL = new ModelResourceLocation("item_frame", "map=false");
    private static final ModelResourceLocation LOCATION_MODEL_MAP = new ModelResourceLocation("item_frame", "map=true");
    private final Minecraft mc = Minecraft.getInstance();
    private final net.minecraft.client.renderer.ItemRenderer itemRenderer;
    private static double itemRenderDistanceSq = 4096.0D;

    public ItemFrameRenderer(EntityRendererManager renderManagerIn, net.minecraft.client.renderer.ItemRenderer itemRendererIn)
    {
        super(renderManagerIn);
        this.itemRenderer = itemRendererIn;
    }

    public void render(ItemFrameEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.push();
        Direction direction = entityIn.getHorizontalFacing();
        Vector3d vector3d = this.getRenderOffset(entityIn, partialTicks);
        matrixStackIn.translate(-vector3d.getX(), -vector3d.getY(), -vector3d.getZ());
        double d0 = 0.46875D;
        matrixStackIn.translate((double)direction.getXOffset() * 0.46875D, (double)direction.getYOffset() * 0.46875D, (double)direction.getZOffset() * 0.46875D);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(entityIn.rotationPitch));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - entityIn.rotationYaw));
        boolean flag = entityIn.isInvisible();

        if (!flag)
        {
            BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
            ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
            ModelResourceLocation modelresourcelocation = entityIn.getDisplayedItem().getItem() instanceof FilledMapItem ? LOCATION_MODEL_MAP : LOCATION_MODEL;
            matrixStackIn.push();
            matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
            blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(matrixStackIn.getLast(), bufferIn.getBuffer(Atlases.getSolidBlockType()), (BlockState)null, modelmanager.getModel(modelresourcelocation), 1.0F, 1.0F, 1.0F, packedLightIn, OverlayTexture.NO_OVERLAY);
            matrixStackIn.pop();
        }

        ItemStack itemstack = entityIn.getDisplayedItem();

        if (!itemstack.isEmpty())
        {
            boolean flag1 = itemstack.getItem() instanceof FilledMapItem;

            if (flag)
            {
                matrixStackIn.translate(0.0D, 0.0D, 0.5D);
            }
            else
            {
                matrixStackIn.translate(0.0D, 0.0D, 0.4375D);
            }

            int i = flag1 ? entityIn.getRotation() % 4 * 2 : entityIn.getRotation();
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((float)i * 360.0F / 8.0F));

            if (!Reflector.postForgeBusEvent(Reflector.RenderItemInFrameEvent_Constructor, entityIn, this, matrixStackIn, bufferIn, packedLightIn))
            {
                if (flag1)
                {
                    matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
                    float f = 0.0078125F;
                    matrixStackIn.scale(0.0078125F, 0.0078125F, 0.0078125F);
                    matrixStackIn.translate(-64.0D, -64.0D, 0.0D);
                    MapData mapdata = ReflectorForge.getMapData(itemstack, entityIn.world);
                    matrixStackIn.translate(0.0D, 0.0D, -1.0D);

                    if (mapdata != null)
                    {
                        this.mc.gameRenderer.getMapItemRenderer().renderMap(matrixStackIn, bufferIn, mapdata, true, packedLightIn);
                    }
                }
                else
                {
                    matrixStackIn.scale(0.5F, 0.5F, 0.5F);

                    if (this.isRenderItem(entityIn))
                    {
                        this.itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
                    }
                }
            }
        }

        matrixStackIn.pop();
    }

    public Vector3d getRenderOffset(ItemFrameEntity entityIn, float partialTicks)
    {
        return new Vector3d((double)((float)entityIn.getHorizontalFacing().getXOffset() * 0.3F), -0.25D, (double)((float)entityIn.getHorizontalFacing().getZOffset() * 0.3F));
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getEntityTexture(ItemFrameEntity entity)
    {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }

    protected boolean canRenderName(ItemFrameEntity entity)
    {
        if (Minecraft.isGuiEnabled() && !entity.getDisplayedItem().isEmpty() && entity.getDisplayedItem().hasDisplayName() && this.renderManager.pointedEntity == entity)
        {
            double d0 = this.renderManager.squareDistanceTo(entity);
            float f = entity.isDiscrete() ? 32.0F : 64.0F;
            return d0 < (double)(f * f);
        }
        else
        {
            return false;
        }
    }

    protected void renderName(ItemFrameEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        super.renderName(entityIn, entityIn.getDisplayedItem().getDisplayName(), matrixStackIn, bufferIn, packedLightIn);
    }

    private boolean isRenderItem(ItemFrameEntity p_isRenderItem_1_)
    {
        if (Shaders.isShadowPass)
        {
            return false;
        }
        else
        {
            if (!Config.zoomMode)
            {
                Entity entity = this.mc.getRenderViewEntity();
                double d0 = p_isRenderItem_1_.getDistanceSq(entity.getPosX(), entity.getPosY(), entity.getPosZ());

                if (d0 > itemRenderDistanceSq)
                {
                    return false;
                }
            }

            return true;
        }
    }

    public static void updateItemRenderDistance()
    {
        Minecraft minecraft = Minecraft.getInstance();
        double d0 = Config.limit(minecraft.gameSettings.fov, 1.0D, 120.0D);
        double d1 = Math.max(6.0D * (double)minecraft.getMainWindow().getHeight() / d0, 16.0D);
        itemRenderDistanceSq = d1 * d1;
    }
}
