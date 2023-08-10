package net.optifine.entity.model;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.client.renderer.entity.model.FoxModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterFox extends ModelAdapter
{
    private static Map<String, Integer> mapPartFields = null;

    public ModelAdapterFox()
    {
        super(EntityType.FOX, "fox", 0.4F);
    }

    public Model makeModel()
    {
        return new FoxModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof FoxModel))
        {
            return null;
        }
        else
        {
            FoxModel foxmodel = (FoxModel)model;
            Map<String, Integer> map = getMapPartFields();

            if (map.containsKey(modelPart))
            {
                int i = map.get(modelPart);
                return (ModelRenderer)Reflector.getFieldValue(foxmodel, Reflector.ModelFox_ModelRenderers, i);
            }
            else
            {
                return null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return getMapPartFields().keySet().toArray(new String[0]);
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
            mapPartFields.put("head", 0);
            mapPartFields.put("body", 4);
            mapPartFields.put("leg1", 5);
            mapPartFields.put("leg2", 6);
            mapPartFields.put("leg3", 7);
            mapPartFields.put("leg4", 8);
            mapPartFields.put("tail", 9);
            return mapPartFields;
        }
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        FoxRenderer foxrenderer = new FoxRenderer(entityrenderermanager);
        foxrenderer.entityModel = (FoxModel)modelBase;
        foxrenderer.shadowSize = shadowSize;
        return foxrenderer;
    }
}
