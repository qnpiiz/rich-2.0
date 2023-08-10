package net.optifine.entity.model;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.OcelotRenderer;
import net.minecraft.client.renderer.entity.model.OcelotModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterOcelot extends ModelAdapter
{
    private static Map<String, Integer> mapPartFields = null;

    public ModelAdapterOcelot()
    {
        super(EntityType.OCELOT, "ocelot", 0.4F);
    }

    protected ModelAdapterOcelot(EntityType type, String name, float shadowSize)
    {
        super(type, name, shadowSize);
    }

    public Model makeModel()
    {
        return new OcelotModel(0.0F);
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof OcelotModel))
        {
            return null;
        }
        else
        {
            OcelotModel ocelotmodel = (OcelotModel)model;
            Map<String, Integer> map = getMapPartFields();

            if (map.containsKey(modelPart))
            {
                int i = map.get(modelPart);
                return (ModelRenderer)Reflector.getFieldValue(ocelotmodel, Reflector.ModelOcelot_ModelRenderers, i);
            }
            else
            {
                return null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"back_left_leg", "back_right_leg", "front_left_leg", "front_right_leg", "tail", "tail2", "head", "body"};
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
            mapPartFields.put("back_left_leg", 0);
            mapPartFields.put("back_right_leg", 1);
            mapPartFields.put("front_left_leg", 2);
            mapPartFields.put("front_right_leg", 3);
            mapPartFields.put("tail", 4);
            mapPartFields.put("tail2", 5);
            mapPartFields.put("head", 6);
            mapPartFields.put("body", 7);
            return mapPartFields;
        }
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        OcelotRenderer ocelotrenderer = new OcelotRenderer(entityrenderermanager);
        ocelotrenderer.entityModel = (OcelotModel)modelBase;
        ocelotrenderer.shadowSize = shadowSize;
        return ocelotrenderer;
    }
}
