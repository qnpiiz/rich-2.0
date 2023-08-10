package net.optifine.entity.model;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.StriderRenderer;
import net.minecraft.client.renderer.entity.model.StriderModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterStrider extends ModelAdapter
{
    private static Map<String, Integer> mapParts = makeMapParts();

    public ModelAdapterStrider()
    {
        super(EntityType.STRIDER, "strider", 0.5F);
    }

    public Model makeModel()
    {
        return new StriderModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof StriderModel))
        {
            return null;
        }
        else
        {
            StriderModel stridermodel = (StriderModel)model;

            if (mapParts.containsKey(modelPart))
            {
                int i = mapParts.get(modelPart);
                return (ModelRenderer)Reflector.getFieldValue(stridermodel, Reflector.ModelStrider_ModelRenderers, i);
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
        map.put("right_leg", 0);
        map.put("left_leg", 1);
        map.put("body", 2);
        map.put("hair_right_bottom", 3);
        map.put("hair_right_middle", 4);
        map.put("hair_right_top", 5);
        map.put("hair_left_top", 6);
        map.put("hair_left_middle", 7);
        map.put("hair_left_bottom", 8);
        return map;
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        StriderRenderer striderrenderer = new StriderRenderer(entityrenderermanager);
        striderrenderer.entityModel = (StriderModel)modelBase;
        striderrenderer.shadowSize = shadowSize;
        return striderrenderer;
    }
}
