package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.layers.ShulkerColorLayer;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class ShulkerRenderer extends MobRenderer<ShulkerEntity, ShulkerModel<ShulkerEntity>>
{
    public static final ResourceLocation field_204402_a = new ResourceLocation("textures/" + Atlases.DEFAULT_SHULKER_TEXTURE.getTextureLocation().getPath() + ".png");
    public static final ResourceLocation[] SHULKER_ENDERGOLEM_TEXTURE = Atlases.SHULKER_TEXTURES.stream().map((p_229125_0_) ->
    {
        return new ResourceLocation("textures/" + p_229125_0_.getTextureLocation().getPath() + ".png");
    }).toArray((p_229124_0_) ->
    {
        return new ResourceLocation[p_229124_0_];
    });

    public ShulkerRenderer(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new ShulkerModel<>(), 0.0F);
        this.addLayer(new ShulkerColorLayer(this));
    }

    public Vector3d getRenderOffset(ShulkerEntity entityIn, float partialTicks)
    {
        int i = entityIn.getClientTeleportInterp();

        if (i > 0 && entityIn.isAttachedToBlock())
        {
            BlockPos blockpos = entityIn.getAttachmentPos();
            BlockPos blockpos1 = entityIn.getOldAttachPos();
            double d0 = (double)((float)i - partialTicks) / 6.0D;
            d0 = d0 * d0;
            double d1 = (double)(blockpos.getX() - blockpos1.getX()) * d0;
            double d2 = (double)(blockpos.getY() - blockpos1.getY()) * d0;
            double d3 = (double)(blockpos.getZ() - blockpos1.getZ()) * d0;
            return new Vector3d(-d1, -d2, -d3);
        }
        else
        {
            return super.getRenderOffset(entityIn, partialTicks);
        }
    }

    public boolean shouldRender(ShulkerEntity livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ)
    {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ))
        {
            return true;
        }
        else
        {
            if (livingEntityIn.getClientTeleportInterp() > 0 && livingEntityIn.isAttachedToBlock())
            {
                Vector3d vector3d = Vector3d.copy(livingEntityIn.getAttachmentPos());
                Vector3d vector3d1 = Vector3d.copy(livingEntityIn.getOldAttachPos());

                if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y, vector3d.z)))
                {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getEntityTexture(ShulkerEntity entity)
    {
        return entity.getColor() == null ? field_204402_a : SHULKER_ENDERGOLEM_TEXTURE[entity.getColor().getId()];
    }

    protected void applyRotations(ShulkerEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
    {
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw + 180.0F, partialTicks);
        matrixStackIn.translate(0.0D, 0.5D, 0.0D);
        matrixStackIn.rotate(entityLiving.getAttachmentFacing().getOpposite().getRotation());
        matrixStackIn.translate(0.0D, -0.5D, 0.0D);
    }
}
