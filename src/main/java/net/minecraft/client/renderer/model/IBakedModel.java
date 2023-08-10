package net.minecraft.client.renderer.model;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.extensions.IForgeBakedModel;

public interface IBakedModel extends IForgeBakedModel
{
    List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand);

    boolean isAmbientOcclusion();

    boolean isGui3d();

    boolean isSideLit();

    boolean isBuiltInRenderer();

    TextureAtlasSprite getParticleTexture();

default ItemCameraTransforms getItemCameraTransforms()
    {
        return ItemCameraTransforms.DEFAULT;
    }

    ItemOverrideList getOverrides();
}
