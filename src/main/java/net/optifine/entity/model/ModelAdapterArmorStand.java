package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.ArmorStandArmorModel;
import net.minecraft.client.renderer.entity.model.ArmorStandModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterArmorStand extends ModelAdapterBiped
{
    public ModelAdapterArmorStand()
    {
        super(EntityType.ARMOR_STAND, "armor_stand", 0.0F);
    }

    public Model makeModel()
    {
        return new ArmorStandModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof ArmorStandModel))
        {
            return null;
        }
        else
        {
            ArmorStandModel armorstandmodel = (ArmorStandModel)model;

            if (modelPart.equals("right"))
            {
                return (ModelRenderer)Reflector.getFieldValue(armorstandmodel, Reflector.ModelArmorStand_ModelRenderers, 0);
            }
            else if (modelPart.equals("left"))
            {
                return (ModelRenderer)Reflector.getFieldValue(armorstandmodel, Reflector.ModelArmorStand_ModelRenderers, 1);
            }
            else if (modelPart.equals("waist"))
            {
                return (ModelRenderer)Reflector.getFieldValue(armorstandmodel, Reflector.ModelArmorStand_ModelRenderers, 2);
            }
            else
            {
                return modelPart.equals("base") ? (ModelRenderer)Reflector.getFieldValue(armorstandmodel, Reflector.ModelArmorStand_ModelRenderers, 3) : super.getModelRenderer(armorstandmodel, modelPart);
            }
        }
    }

    public String[] getModelRendererNames()
    {
        String[] astring = super.getModelRendererNames();
        return (String[])Config.addObjectsToArray(astring, new String[] {"right", "left", "waist", "base"});
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        ArmorStandRenderer armorstandrenderer = new ArmorStandRenderer(entityrenderermanager);
        armorstandrenderer.entityModel = (ArmorStandArmorModel)modelBase;
        armorstandrenderer.shadowSize = shadowSize;
        return armorstandrenderer;
    }
}
