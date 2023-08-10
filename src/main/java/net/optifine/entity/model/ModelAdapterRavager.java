package net.optifine.entity.model;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.RavagerRenderer;
import net.minecraft.client.renderer.entity.model.RavagerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterRavager extends ModelAdapter
{
    private static Map<String, Integer> mapPartFields = null;

    public ModelAdapterRavager()
    {
        super(EntityType.RAVAGER, "ravager", 1.1F);
    }

    public Model makeModel()
    {
        return new RavagerModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof RavagerModel))
        {
            return null;
        }
        else
        {
            RavagerModel ravagermodel = (RavagerModel)model;
            Map<String, Integer> map = getMapPartFields();

            if (map.containsKey(modelPart))
            {
                int i = map.get(modelPart);
                return (ModelRenderer)Reflector.getFieldValue(ravagermodel, Reflector.ModelRavager_ModelRenderers, i);
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
            mapPartFields.put("jaw", 1);
            mapPartFields.put("body", 2);
            mapPartFields.put("leg1", 3);
            mapPartFields.put("leg2", 4);
            mapPartFields.put("leg3", 5);
            mapPartFields.put("leg4", 6);
            mapPartFields.put("neck", 7);
            return mapPartFields;
        }
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        RavagerRenderer ravagerrenderer = new RavagerRenderer(entityrenderermanager);
        ravagerrenderer.entityModel = (RavagerModel)modelBase;
        ravagerrenderer.shadowSize = shadowSize;
        return ravagerrenderer;
    }
}
