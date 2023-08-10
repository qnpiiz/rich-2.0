package net.optifine.entity.model;

import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.Either;

public interface IEntityRenderer
{
    Either<EntityType, TileEntityType> getType();

    void setType(Either<EntityType, TileEntityType> var1);

    ResourceLocation getLocationTextureCustom();

    void setLocationTextureCustom(ResourceLocation var1);
}
