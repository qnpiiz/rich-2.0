package net.minecraft.client.renderer.model;

import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public interface IUnbakedModel
{
    Collection<ResourceLocation> getDependencies();

    Collection<RenderMaterial> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors);

    @Nullable
    IBakedModel bakeModel(ModelBakery modelBakeryIn, Function<RenderMaterial, TextureAtlasSprite> spriteGetterIn, IModelTransform transformIn, ResourceLocation locationIn);
}
