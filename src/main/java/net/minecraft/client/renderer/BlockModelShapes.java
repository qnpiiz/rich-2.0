package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BlockModelShapes
{
    private final Map<BlockState, IBakedModel> bakedModelStore = Maps.newIdentityHashMap();
    private final ModelManager modelManager;

    public BlockModelShapes(ModelManager manager)
    {
        this.modelManager = manager;
    }

    public TextureAtlasSprite getTexture(BlockState state)
    {
        return this.getModel(state).getParticleTexture();
    }

    public IBakedModel getModel(BlockState state)
    {
        IBakedModel ibakedmodel = this.bakedModelStore.get(state);

        if (ibakedmodel == null)
        {
            ibakedmodel = this.modelManager.getMissingModel();
        }

        return ibakedmodel;
    }

    public ModelManager getModelManager()
    {
        return this.modelManager;
    }

    public void reloadModels()
    {
        this.bakedModelStore.clear();

        for (Block block : Registry.BLOCK)
        {
            block.getStateContainer().getValidStates().forEach((state) ->
            {
                IBakedModel ibakedmodel = this.bakedModelStore.put(state, this.modelManager.getModel(getModelLocation(state)));
            });
        }
    }

    public static ModelResourceLocation getModelLocation(BlockState state)
    {
        return getModelLocation(Registry.BLOCK.getKey(state.getBlock()), state);
    }

    public static ModelResourceLocation getModelLocation(ResourceLocation location, BlockState state)
    {
        return new ModelResourceLocation(location, getPropertyMapString(state.getValues()));
    }

    public static String getPropertyMapString(Map < Property<?>, Comparable<? >> propertyValues)
    {
        StringBuilder stringbuilder = new StringBuilder();

        for (Entry < Property<?>, Comparable<? >> entry : propertyValues.entrySet())
        {
            if (stringbuilder.length() != 0)
            {
                stringbuilder.append(',');
            }

            Property<?> property = entry.getKey();
            stringbuilder.append(property.getName());
            stringbuilder.append('=');
            stringbuilder.append(getPropertyValueString(property, entry.getValue()));
        }

        return stringbuilder.toString();
    }

    private static <T extends Comparable<T>> String getPropertyValueString(Property<T> property, Comparable<?> value)
    {
        return property.getName((T)value);
    }
}
