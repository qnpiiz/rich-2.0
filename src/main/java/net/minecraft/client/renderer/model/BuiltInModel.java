package net.minecraft.client.renderer.model;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

public class BuiltInModel implements IBakedModel
{
    private final ItemCameraTransforms cameraTransforms;
    private final ItemOverrideList overrides;
    private final TextureAtlasSprite sprite;
    private final boolean isSideLit;

    public BuiltInModel(ItemCameraTransforms cameraTransforms, ItemOverrideList overrides, TextureAtlasSprite spite, boolean isSideLit)
    {
        this.cameraTransforms = cameraTransforms;
        this.overrides = overrides;
        this.sprite = spite;
        this.isSideLit = isSideLit;
    }

    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
    {
        return Collections.emptyList();
    }

    public boolean isAmbientOcclusion()
    {
        return false;
    }

    public boolean isGui3d()
    {
        return true;
    }

    public boolean isSideLit()
    {
        return this.isSideLit;
    }

    public boolean isBuiltInRenderer()
    {
        return true;
    }

    public TextureAtlasSprite getParticleTexture()
    {
        return this.sprite;
    }

    public ItemCameraTransforms getItemCameraTransforms()
    {
        return this.cameraTransforms;
    }

    public ItemOverrideList getOverrides()
    {
        return this.overrides;
    }
}
