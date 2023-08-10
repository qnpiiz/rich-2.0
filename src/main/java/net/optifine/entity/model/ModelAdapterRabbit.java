package net.optifine.entity.model;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.client.renderer.entity.model.RabbitModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterRabbit extends ModelAdapter
{
    private static Map<String, Integer> mapPartFields = null;

    public ModelAdapterRabbit()
    {
        super(EntityType.RABBIT, "rabbit", 0.3F);
    }

    public Model makeModel()
    {
        return new RabbitModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof RabbitModel))
        {
            return null;
        }
        else
        {
            RabbitModel rabbitmodel = (RabbitModel)model;
            Map<String, Integer> map = getMapPartFields();

            if (map.containsKey(modelPart))
            {
                int i = map.get(modelPart);
                return (ModelRenderer)Reflector.getFieldValue(rabbitmodel, Reflector.ModelRabbit_ModelRenderers, i);
            }
            else
            {
                return null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"left_foot", "right_foot", "left_thigh", "right_thigh", "body", "left_arm", "right_arm", "head", "right_ear", "left_ear", "tail", "nose"};
    }

    private static Map<String, Integer> getMapPartFields()
    {
        if (mapPartFields != null)
        {
            return mapPartFields;
        }
        else
        {
            mapPartFields = new HashMap<>();
            mapPartFields.put("left_foot", 0);
            mapPartFields.put("right_foot", 1);
            mapPartFields.put("left_thigh", 2);
            mapPartFields.put("right_thigh", 3);
            mapPartFields.put("body", 4);
            mapPartFields.put("left_arm", 5);
            mapPartFields.put("right_arm", 6);
            mapPartFields.put("head", 7);
            mapPartFields.put("right_ear", 8);
            mapPartFields.put("left_ear", 9);
            mapPartFields.put("tail", 10);
            mapPartFields.put("nose", 11);
            return mapPartFields;
        }
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        RabbitRenderer rabbitrenderer = new RabbitRenderer(entityrenderermanager);
        rabbitrenderer.entityModel = (RabbitModel)modelBase;
        rabbitrenderer.shadowSize = shadowSize;
        return rabbitrenderer;
    }
}
