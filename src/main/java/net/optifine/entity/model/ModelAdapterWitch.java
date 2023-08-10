package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.WitchRenderer;
import net.minecraft.client.renderer.entity.model.WitchModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterWitch extends ModelAdapterVillager
{
    public ModelAdapterWitch()
    {
        super(EntityType.WITCH, "witch", 0.5F);
    }

    public Model makeModel()
    {
        return new WitchModel(0.0F);
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof WitchModel))
        {
            return null;
        }
        else
        {
            WitchModel witchmodel = (WitchModel)model;
            return modelPart.equals("mole") ? (ModelRenderer)Reflector.getFieldValue(witchmodel, Reflector.ModelWitch_mole) : super.getModelRenderer(witchmodel, modelPart);
        }
    }

    public String[] getModelRendererNames()
    {
        String[] astring = super.getModelRendererNames();
        return (String[])Config.addObjectToArray(astring, "mole");
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        WitchRenderer witchrenderer = new WitchRenderer(entityrenderermanager);
        witchrenderer.entityModel = (WitchModel)modelBase;
        witchrenderer.shadowSize = shadowSize;
        return witchrenderer;
    }
}
