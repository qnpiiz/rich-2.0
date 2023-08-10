package net.optifine.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelRendererUtils
{
    public static ModelRenderer getModelRenderer(Iterator<ModelRenderer> iterator, int index)
    {
        if (iterator == null)
        {
            return null;
        }
        else if (index < 0)
        {
            return null;
        }
        else
        {
            for (int i = 0; i < index; ++i)
            {
                if (!iterator.hasNext())
                {
                    return null;
                }

                ModelRenderer modelrenderer = iterator.next();
            }

            return !iterator.hasNext() ? null : iterator.next();
        }
    }

    public static ModelRenderer getModelRenderer(ImmutableList<ModelRenderer> models, int index)
    {
        if (models == null)
        {
            return null;
        }
        else if (index < 0)
        {
            return null;
        }
        else
        {
            return index >= models.size() ? null : models.get(index);
        }
    }
}
