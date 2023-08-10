package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

public class SimpleBakedModel implements IBakedModel
{
    protected final List<BakedQuad> generalQuads;
    protected final Map<Direction, List<BakedQuad>> faceQuads;
    protected final boolean ambientOcclusion;
    protected final boolean gui3d;
    protected final boolean isSideLit;
    protected final TextureAtlasSprite texture;
    protected final ItemCameraTransforms cameraTransforms;
    protected final ItemOverrideList itemOverrideList;

    public SimpleBakedModel(List<BakedQuad> generalQuad, Map<Direction, List<BakedQuad>> faceQuads, boolean ambientOcclusion, boolean isSideLit, boolean gui3d, TextureAtlasSprite texture, ItemCameraTransforms cameraTransforms, ItemOverrideList itemOverrideList)
    {
        this.generalQuads = generalQuad;
        this.faceQuads = faceQuads;
        this.ambientOcclusion = ambientOcclusion;
        this.gui3d = gui3d;
        this.isSideLit = isSideLit;
        this.texture = texture;
        this.cameraTransforms = cameraTransforms;
        this.itemOverrideList = itemOverrideList;
    }

    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
    {
        return side == null ? this.generalQuads : this.faceQuads.get(side);
    }

    public boolean isAmbientOcclusion()
    {
        return this.ambientOcclusion;
    }

    public boolean isGui3d()
    {
        return this.gui3d;
    }

    public boolean isSideLit()
    {
        return this.isSideLit;
    }

    public boolean isBuiltInRenderer()
    {
        return false;
    }

    public TextureAtlasSprite getParticleTexture()
    {
        return this.texture;
    }

    public ItemCameraTransforms getItemCameraTransforms()
    {
        return this.cameraTransforms;
    }

    public ItemOverrideList getOverrides()
    {
        return this.itemOverrideList;
    }

    public static class Builder
    {
        private final List<BakedQuad> builderGeneralQuads = Lists.newArrayList();
        private final Map<Direction, List<BakedQuad>> builderFaceQuads = Maps.newEnumMap(Direction.class);
        private final ItemOverrideList builderItemOverrideList;
        private final boolean builderAmbientOcclusion;
        private TextureAtlasSprite builderTexture;
        private final boolean isSideLit;
        private final boolean builderGui3d;
        private final ItemCameraTransforms builderCameraTransforms;

        public Builder(BlockModel model, ItemOverrideList itemOverrideList, boolean gui3d)
        {
            this(model.isAmbientOcclusion(), model.getGuiLight().isSideLit(), gui3d, model.getAllTransforms(), itemOverrideList);
        }

        private Builder(boolean ambientOcclusion, boolean isSideLit, boolean isSideLit2, ItemCameraTransforms cameraTransforms, ItemOverrideList itemOverrideList)
        {
            for (Direction direction : Direction.values())
            {
                this.builderFaceQuads.put(direction, Lists.newArrayList());
            }

            this.builderItemOverrideList = itemOverrideList;
            this.builderAmbientOcclusion = ambientOcclusion;
            this.isSideLit = isSideLit;
            this.builderGui3d = isSideLit2;
            this.builderCameraTransforms = cameraTransforms;
        }

        public SimpleBakedModel.Builder addFaceQuad(Direction facing, BakedQuad quad)
        {
            this.builderFaceQuads.get(facing).add(quad);
            return this;
        }

        public SimpleBakedModel.Builder addGeneralQuad(BakedQuad quad)
        {
            this.builderGeneralQuads.add(quad);
            return this;
        }

        public SimpleBakedModel.Builder setTexture(TextureAtlasSprite texture)
        {
            this.builderTexture = texture;
            return this;
        }

        public IBakedModel build()
        {
            if (this.builderTexture == null)
            {
                throw new RuntimeException("Missing particle!");
            }
            else
            {
                return new SimpleBakedModel(this.builderGeneralQuads, this.builderFaceQuads, this.builderAmbientOcclusion, this.isSideLit, this.builderGui3d, this.builderTexture, this.builderCameraTransforms, this.builderItemOverrideList);
            }
        }
    }
}
