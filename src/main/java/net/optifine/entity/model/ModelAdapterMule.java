package net.optifine.entity.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ChestedHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.HorseArmorChestsModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterMule extends ModelAdapterHorse
{
    public ModelAdapterMule()
    {
        super(EntityType.MULE, "mule", 0.75F);
    }

    public Model makeModel()
    {
        return new HorseArmorChestsModel(0.92F);
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof HorseArmorChestsModel))
        {
            return null;
        }
        else
        {
            HorseArmorChestsModel horsearmorchestsmodel = (HorseArmorChestsModel)model;

            if (modelPart.equals("left_chest"))
            {
                return (ModelRenderer)Reflector.ModelHorseChests_ModelRenderers.getValue(horsearmorchestsmodel, 0);
            }
            else
            {
                return modelPart.equals("right_chest") ? (ModelRenderer)Reflector.ModelHorseChests_ModelRenderers.getValue(horsearmorchestsmodel, 1) : super.getModelRenderer(model, modelPart);
            }
        }
    }

    public String[] getModelRendererNames()
    {
        List<String> list = new ArrayList<>(Arrays.asList(super.getModelRendererNames()));
        list.add("left_chest");
        list.add("right_chest");
        return list.toArray(new String[list.size()]);
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        ChestedHorseRenderer chestedhorserenderer = new ChestedHorseRenderer(entityrenderermanager, 0.92F);
        chestedhorserenderer.entityModel = (EntityModel)modelBase;
        chestedhorserenderer.shadowSize = shadowSize;
        return chestedhorserenderer;
    }
}
