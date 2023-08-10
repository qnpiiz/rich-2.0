package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EnderCrystalRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;

public class ModelAdapterEnderCrystal extends ModelAdapter
{
    public ModelAdapterEnderCrystal()
    {
        this("end_crystal");
    }

    protected ModelAdapterEnderCrystal(String name)
    {
        super(EntityType.END_CRYSTAL, name, 0.5F);
    }

    public Model makeModel()
    {
        return new EnderCrystalModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof EnderCrystalModel))
        {
            return null;
        }
        else
        {
            EnderCrystalModel endercrystalmodel = (EnderCrystalModel)model;

            if (modelPart.equals("cube"))
            {
                return endercrystalmodel.cube;
            }
            else if (modelPart.equals("glass"))
            {
                return endercrystalmodel.glass;
            }
            else
            {
                return modelPart.equals("base") ? endercrystalmodel.base : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"cube", "glass", "base"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        EntityRenderer entityrenderer = entityrenderermanager.getEntityRenderMap().get(EntityType.END_CRYSTAL);

        if (!(entityrenderer instanceof EnderCrystalRenderer))
        {
            Config.warn("Not an instance of RenderEnderCrystal: " + entityrenderer);
            return null;
        }
        else
        {
            EnderCrystalRenderer endercrystalrenderer = (EnderCrystalRenderer)entityrenderer;

            if (endercrystalrenderer.getType() == null)
            {
                endercrystalrenderer = new EnderCrystalRenderer(entityrenderermanager);
            }

            if (!(modelBase instanceof EnderCrystalModel))
            {
                Config.warn("Not a EnderCrystalModel model: " + modelBase);
                return null;
            }
            else
            {
                EnderCrystalModel endercrystalmodel = (EnderCrystalModel)modelBase;
                endercrystalrenderer = endercrystalmodel.updateRenderer(endercrystalrenderer);
                endercrystalrenderer.shadowSize = shadowSize;
                return endercrystalrenderer;
            }
        }
    }
}
