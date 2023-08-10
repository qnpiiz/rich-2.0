package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ShulkerBulletRenderer;
import net.minecraft.client.renderer.entity.model.ShulkerBulletModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterShulkerBullet extends ModelAdapter
{
    public ModelAdapterShulkerBullet()
    {
        super(EntityType.SHULKER_BULLET, "shulker_bullet", 0.0F);
    }

    public Model makeModel()
    {
        return new ShulkerBulletModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof ShulkerBulletModel))
        {
            return null;
        }
        else
        {
            ShulkerBulletModel shulkerbulletmodel = (ShulkerBulletModel)model;
            return modelPart.equals("bullet") ? (ModelRenderer)Reflector.ModelShulkerBullet_renderer.getValue(shulkerbulletmodel) : null;
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"bullet"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        ShulkerBulletRenderer shulkerbulletrenderer = new ShulkerBulletRenderer(entityrenderermanager);

        if (!Reflector.RenderShulkerBullet_model.exists())
        {
            Config.warn("Field not found: RenderShulkerBullet.model");
            return null;
        }
        else
        {
            Reflector.setFieldValue(shulkerbulletrenderer, Reflector.RenderShulkerBullet_model, modelBase);
            shulkerbulletrenderer.shadowSize = shadowSize;
            return shulkerbulletrenderer;
        }
    }
}
