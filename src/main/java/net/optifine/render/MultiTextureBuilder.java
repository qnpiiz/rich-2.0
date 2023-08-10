package net.optifine.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.optifine.Config;
import net.optifine.util.IntArray;
import net.optifine.util.TextureUtils;

public class MultiTextureBuilder
{
    private int vertexCount;
    private RenderType blockLayer;
    private TextureAtlasSprite[] quadSprites;
    private boolean reorderingAllowed;
    private boolean[] drawnIcons = new boolean[256];
    private List<SpriteRenderData> spriteRenderDatas = new ArrayList<>();
    private IntArray vertexPositions = new IntArray(16);
    private IntArray vertexCounts = new IntArray(16);

    public MultiTextureData build(int vertexCountIn, RenderType blockLayerIn, TextureAtlasSprite[] quadSpritesIn)
    {
        if (quadSpritesIn == null)
        {
            return null;
        }
        else
        {
            this.vertexCount = vertexCountIn;
            this.blockLayer = blockLayerIn;
            this.quadSprites = quadSpritesIn;
            this.reorderingAllowed = this.blockLayer != RenderTypes.TRANSLUCENT;
            int i = Config.getTextureMap().getCountRegisteredSprites();

            if (this.drawnIcons.length <= i)
            {
                this.drawnIcons = new boolean[i + 1];
            }

            Arrays.fill(this.drawnIcons, false);
            this.spriteRenderDatas.clear();
            int j = 0;
            int k = -1;
            int l = this.vertexCount / 4;

            for (int i1 = 0; i1 < l; ++i1)
            {
                TextureAtlasSprite textureatlassprite = this.quadSprites[i1];

                if (textureatlassprite != null)
                {
                    int j1 = textureatlassprite.getIndexInMap();

                    if (!this.drawnIcons[j1])
                    {
                        if (textureatlassprite == TextureUtils.iconGrassSideOverlay)
                        {
                            if (k < 0)
                            {
                                k = i1;
                            }
                        }
                        else
                        {
                            i1 = this.drawForIcon(textureatlassprite, i1) - 1;
                            ++j;

                            if (this.reorderingAllowed)
                            {
                                this.drawnIcons[j1] = true;
                            }
                        }
                    }
                }
            }

            if (k >= 0)
            {
                this.drawForIcon(TextureUtils.iconGrassSideOverlay, k);
                ++j;
            }

            SpriteRenderData[] aspriterenderdata = this.spriteRenderDatas.toArray(new SpriteRenderData[this.spriteRenderDatas.size()]);
            return new MultiTextureData(aspriterenderdata);
        }
    }

    private int drawForIcon(TextureAtlasSprite sprite, int startQuadPos)
    {
        this.vertexPositions.clear();
        this.vertexCounts.clear();
        int i = -1;
        int j = -1;
        int k = this.vertexCount / 4;

        for (int l = startQuadPos; l < k; ++l)
        {
            TextureAtlasSprite textureatlassprite = this.quadSprites[l];

            if (textureatlassprite == sprite)
            {
                if (j < 0)
                {
                    j = l;
                }
            }
            else if (j >= 0)
            {
                this.draw(j, l);

                if (!this.reorderingAllowed)
                {
                    this.spriteRenderDatas.add(new SpriteRenderData(sprite, this.vertexPositions.toIntArray(), this.vertexCounts.toIntArray()));
                    return l;
                }

                j = -1;

                if (i < 0)
                {
                    i = l;
                }
            }
        }

        if (j >= 0)
        {
            this.draw(j, k);
        }

        if (i < 0)
        {
            i = k;
        }

        this.spriteRenderDatas.add(new SpriteRenderData(sprite, this.vertexPositions.toIntArray(), this.vertexCounts.toIntArray()));
        return i;
    }

    private void draw(int startQuadVertex, int endQuadVertex)
    {
        int i = endQuadVertex - startQuadVertex;

        if (i > 0)
        {
            int j = startQuadVertex * 4;
            int k = i * 4;
            this.vertexPositions.put(j);
            this.vertexCounts.put(k);
        }
    }
}
