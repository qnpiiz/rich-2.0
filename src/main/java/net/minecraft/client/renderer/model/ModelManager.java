package net.minecraft.client.renderer.model;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.SpriteMap;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.fluid.FluidState;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class ModelManager extends ReloadListener<ModelBakery> implements AutoCloseable
{
    private Map<ResourceLocation, IBakedModel> modelRegistry;
    @Nullable
    private SpriteMap atlases;
    private final BlockModelShapes modelProvider;
    private final TextureManager textureManager;
    private final BlockColors blockColors;
    private int maxMipmapLevel;
    private IBakedModel defaultModel;
    private Object2IntMap<BlockState> stateModelIds;

    public ModelManager(TextureManager textureManagerIn, BlockColors blockColorsIn, int maxMipmapLevelIn)
    {
        this.textureManager = textureManagerIn;
        this.blockColors = blockColorsIn;
        this.maxMipmapLevel = maxMipmapLevelIn;
        this.modelProvider = new BlockModelShapes(this);
    }

    public IBakedModel getModel(ModelResourceLocation modelLocation)
    {
        return this.modelRegistry.getOrDefault(modelLocation, this.defaultModel);
    }

    public IBakedModel getMissingModel()
    {
        return this.defaultModel;
    }

    public BlockModelShapes getBlockModelShapes()
    {
        return this.modelProvider;
    }

    /**
     * Performs any reloading that can be done off-thread, such as file IO
     */
    protected ModelBakery prepare(IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        profilerIn.startTick();
        ModelBakery modelbakery = new ModelBakery(resourceManagerIn, this.blockColors, profilerIn, this.maxMipmapLevel);
        profilerIn.endTick();
        return modelbakery;
    }

    protected void apply(ModelBakery objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        profilerIn.startTick();
        profilerIn.startSection("upload");

        if (this.atlases != null)
        {
            this.atlases.close();
        }

        this.atlases = objectIn.uploadTextures(this.textureManager, profilerIn);
        this.modelRegistry = objectIn.getTopBakedModels();
        this.stateModelIds = objectIn.getStateModelIds();
        this.defaultModel = this.modelRegistry.get(ModelBakery.MODEL_MISSING);
        profilerIn.endStartSection("cache");
        this.modelProvider.reloadModels();
        profilerIn.endSection();
        profilerIn.endTick();
    }

    public boolean needsRenderUpdate(BlockState oldState, BlockState newState)
    {
        if (oldState == newState)
        {
            return false;
        }
        else
        {
            int i = this.stateModelIds.getInt(oldState);

            if (i != -1)
            {
                int j = this.stateModelIds.getInt(newState);

                if (i == j)
                {
                    FluidState fluidstate = oldState.getFluidState();
                    FluidState fluidstate1 = newState.getFluidState();
                    return fluidstate != fluidstate1;
                }
            }

            return true;
        }
    }

    public AtlasTexture getAtlasTexture(ResourceLocation locationIn)
    {
        return this.atlases.getAtlasTexture(locationIn);
    }

    public void close()
    {
        if (this.atlases != null)
        {
            this.atlases.close();
        }
    }

    public void setMaxMipmapLevel(int levelIn)
    {
        this.maxMipmapLevel = levelIn;
    }
}
