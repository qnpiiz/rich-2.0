package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LlamaSpitRenderer;
import net.minecraft.client.renderer.entity.model.LlamaSpitModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterLlamaSpit extends ModelAdapter
{
    public ModelAdapterLlamaSpit()
    {
        super(EntityType.LLAMA_SPIT, "llama_spit", 0.0F);
    }

    public Model makeModel()
    {
        return new LlamaSpitModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof LlamaSpitModel))
        {
            return null;
        }
        else
        {
            LlamaSpitModel llamaspitmodel = (LlamaSpitModel)model;
            return modelPart.equals("body") ? (ModelRenderer)Reflector.ModelLlamaSpit_renderer.getValue(llamaspitmodel) : null;
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"body"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        LlamaSpitRenderer llamaspitrenderer = new LlamaSpitRenderer(entityrenderermanager);

        if (!Reflector.RenderLlamaSpit_model.exists())
        {
            Config.warn("Field not found: RenderLlamaSpit.model");
            return null;
        }
        else
        {
            Reflector.setFieldValue(llamaspitrenderer, Reflector.RenderLlamaSpit_model, modelBase);
            llamaspitrenderer.shadowSize = shadowSize;
            return llamaspitrenderer;
        }
    }
}
