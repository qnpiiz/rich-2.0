package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.util.Either;

public abstract class TileEntityRenderer<T extends TileEntity> implements IEntityRenderer
{
    protected final TileEntityRendererDispatcher renderDispatcher;
    private TileEntityType type = null;
    private ResourceLocation locationTextureCustom = null;

    public TileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        this.renderDispatcher = rendererDispatcherIn;
    }

    public abstract void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn);

    public boolean isGlobalRenderer(T te)
    {
        return false;
    }

    public Either<EntityType, TileEntityType> getType()
    {
        return this.type == null ? null : Either.makeRight(this.type);
    }

    public void setType(Either<EntityType, TileEntityType> p_setType_1_)
    {
        this.type = p_setType_1_.getRight().get();
    }

    public ResourceLocation getLocationTextureCustom()
    {
        return this.locationTextureCustom;
    }

    public void setLocationTextureCustom(ResourceLocation p_setLocationTextureCustom_1_)
    {
        this.locationTextureCustom = p_setLocationTextureCustom_1_;
    }
}
