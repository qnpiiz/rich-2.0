package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.CodRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.CodModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterCod extends ModelAdapter
{
    public ModelAdapterCod()
    {
        super(EntityType.COD, "cod", 0.3F);
    }

    public Model makeModel()
    {
        return new CodModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof CodModel))
        {
            return null;
        }
        else
        {
            CodModel codmodel = (CodModel)model;

            if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.ModelCod_ModelRenderers.getValue(codmodel, 0);
            }
            else if (modelPart.equals("fin_back"))
            {
                return (ModelRenderer)Reflector.ModelCod_ModelRenderers.getValue(codmodel, 1);
            }
            else if (modelPart.equals("head"))
            {
                return (ModelRenderer)Reflector.ModelCod_ModelRenderers.getValue(codmodel, 2);
            }
            else if (modelPart.equals("nose"))
            {
                return (ModelRenderer)Reflector.ModelCod_ModelRenderers.getValue(codmodel, 3);
            }
            else if (modelPart.equals("fin_right"))
            {
                return (ModelRenderer)Reflector.ModelCod_ModelRenderers.getValue(codmodel, 4);
            }
            else if (modelPart.equals("fin_left"))
            {
                return (ModelRenderer)Reflector.ModelCod_ModelRenderers.getValue(codmodel, 5);
            }
            else
            {
                return modelPart.equals("tail") ? (ModelRenderer)Reflector.ModelCod_ModelRenderers.getValue(codmodel, 6) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"body", "fin_back", "head", "nose", "fin_right", "fin_left", "tail"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        CodRenderer codrenderer = new CodRenderer(entityrenderermanager);
        codrenderer.entityModel = (CodModel)modelBase;
        codrenderer.shadowSize = shadowSize;
        return codrenderer;
    }
}
