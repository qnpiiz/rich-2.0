package net.minecraftforge.client.extensions;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public interface IForgeTextureAtlasSprite
{
default boolean hasCustomLoader(IResourceManager manager, ResourceLocation location)
    {
        return false;
    }

default boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter)
    {
        return true;
    }

default Collection<ResourceLocation> getDependencies()
    {
        return ImmutableList.of();
    }
}
