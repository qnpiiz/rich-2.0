package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LlamaRenderer;
import net.minecraft.client.renderer.entity.model.LlamaModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterLlama extends ModelAdapter
{
    public ModelAdapterLlama()
    {
        super(EntityType.LLAMA, "llama", 0.7F);
    }

    public ModelAdapterLlama(EntityType entityType, String name, float shadowSize)
    {
        super(entityType, name, shadowSize);
    }

    public Model makeModel()
    {
        return new LlamaModel(0.0F);
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof LlamaModel))
        {
            return null;
        }
        else
        {
            LlamaModel llamamodel = (LlamaModel)model;

            if (modelPart.equals("head"))
            {
                return (ModelRenderer)Reflector.ModelLlama_ModelRenderers.getValue(llamamodel, 0);
            }
            else if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.ModelLlama_ModelRenderers.getValue(llamamodel, 1);
            }
            else if (modelPart.equals("leg1"))
            {
                return (ModelRenderer)Reflector.ModelLlama_ModelRenderers.getValue(llamamodel, 2);
            }
            else if (modelPart.equals("leg2"))
            {
                return (ModelRenderer)Reflector.ModelLlama_ModelRenderers.getValue(llamamodel, 3);
            }
            else if (modelPart.equals("leg3"))
            {
                return (ModelRenderer)Reflector.ModelLlama_ModelRenderers.getValue(llamamodel, 4);
            }
            else if (modelPart.equals("leg4"))
            {
                return (ModelRenderer)Reflector.ModelLlama_ModelRenderers.getValue(llamamodel, 5);
            }
            else if (modelPart.equals("chest_right"))
            {
                return (ModelRenderer)Reflector.ModelLlama_ModelRenderers.getValue(llamamodel, 6);
            }
            else
            {
                return modelPart.equals("chest_left") ? (ModelRenderer)Reflector.ModelLlama_ModelRenderers.getValue(llamamodel, 7) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head", "body", "leg1", "leg2", "leg3", "leg4", "chest_right", "chest_left"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        LlamaRenderer llamarenderer = new LlamaRenderer(entityrenderermanager);
        llamarenderer.entityModel = (LlamaModel)modelBase;
        llamarenderer.shadowSize = shadowSize;
        return llamarenderer;
    }
}
