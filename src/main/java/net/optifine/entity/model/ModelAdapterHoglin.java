package net.optifine.entity.model;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.HoglinRenderer;
import net.minecraft.client.renderer.entity.model.BoarModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterHoglin extends ModelAdapter
{
    private static Map<String, Integer> mapParts = makeMapParts();

    public ModelAdapterHoglin()
    {
        super(EntityType.HOGLIN, "hoglin", 0.7F);
    }

    public ModelAdapterHoglin(EntityType entityType, String name, float shadowSize)
    {
        super(entityType, name, shadowSize);
    }

    public Model makeModel()
    {
        return new BoarModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof BoarModel))
        {
            return null;
        }
        else
        {
            BoarModel boarmodel = (BoarModel)model;

            if (mapParts.containsKey(modelPart))
            {
                int i = mapParts.get(modelPart);
                return (ModelRenderer)Reflector.getFieldValue(boarmodel, Reflector.ModelBoar_ModelRenderers, i);
            }
            else
            {
                return null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return mapParts.keySet().toArray(new String[0]);
    }

    private static Map<String, Integer> makeMapParts()
    {
        Map<String, Integer> map = new HashMap<>();
        map.put("head", 0);
        map.put("right_ear", 1);
        map.put("left_ear", 2);
        map.put("body", 3);
        map.put("front_right_leg", 4);
        map.put("front_left_leg", 5);
        map.put("back_right_leg", 6);
        map.put("back_left_leg", 7);
        map.put("mane", 8);
        return map;
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        HoglinRenderer hoglinrenderer = new HoglinRenderer(entityrenderermanager);
        hoglinrenderer.entityModel = (BoarModel)modelBase;
        hoglinrenderer.shadowSize = shadowSize;
        return hoglinrenderer;
    }
}
