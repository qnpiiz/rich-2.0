package net.optifine.entity.model;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.client.renderer.entity.model.PhantomModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterPhantom extends ModelAdapter
{
    private static Map<String, Integer> mapPartFields = null;

    public ModelAdapterPhantom()
    {
        super(EntityType.PHANTOM, "phantom", 0.75F);
    }

    public Model makeModel()
    {
        return new PhantomModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof PhantomModel))
        {
            return null;
        }
        else
        {
            PhantomModel phantommodel = (PhantomModel)model;
            Map<String, Integer> map = getMapPartFields();

            if (map.containsKey(modelPart))
            {
                int j = map.get(modelPart);
                return (ModelRenderer)Reflector.getFieldValue(phantommodel, Reflector.ModelPhantom_ModelRenderers, j);
            }
            else
            {
                if (modelPart.equals("head"))
                {
                    int i = map.get("body");
                    ModelRenderer modelrenderer = (ModelRenderer)Reflector.getFieldValue(phantommodel, Reflector.ModelPhantom_ModelRenderers, i);

                    if (modelrenderer != null)
                    {
                        return modelrenderer.getChild(1);
                    }
                }

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
            mapPartFields.put("body", 0);
            mapPartFields.put("left_wing", 1);
            mapPartFields.put("left_wing_tip", 2);
            mapPartFields.put("right_wing", 3);
            mapPartFields.put("right_wing_tip", 4);
            mapPartFields.put("tail", 5);
            mapPartFields.put("tail2", 6);
            return mapPartFields;
        }
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        PhantomRenderer phantomrenderer = new PhantomRenderer(entityrenderermanager);
        phantomrenderer.entityModel = (PhantomModel)modelBase;
        phantomrenderer.shadowSize = shadowSize;
        return phantomrenderer;
    }
}
