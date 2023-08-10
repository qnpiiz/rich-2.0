package net.optifine.entity.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.entity.model.PiglinModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;

public class ModelAdapterPiglin extends ModelAdapterBiped
{
    public ModelAdapterPiglin()
    {
        super(EntityType.PIGLIN, "piglin", 0.5F);
    }

    protected ModelAdapterPiglin(EntityType type, String name, float shadowSize)
    {
        super(type, name, shadowSize);
    }

    public Model makeModel()
    {
        return new PiglinModel(0.0F, 64, 64);
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (model instanceof PiglinModel)
        {
            PiglinModel piglinmodel = (PiglinModel)model;

            if (modelPart.equals("left_ear"))
            {
                return piglinmodel.field_239115_a_;
            }

            if (modelPart.equals("right_ear"))
            {
                return piglinmodel.field_239116_b_;
            }
        }

        return super.getModelRenderer(model, modelPart);
    }

    public String[] getModelRendererNames()
    {
        List<String> list = new ArrayList<>(Arrays.asList(super.getModelRendererNames()));
        list.add("left_ear");
        list.add("right_ear");
        return list.toArray(new String[list.size()]);
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        PiglinRenderer piglinrenderer = new PiglinRenderer(entityrenderermanager, false);
        piglinrenderer.entityModel = (PiglinModel)modelBase;
        piglinrenderer.shadowSize = shadowSize;
        return piglinrenderer;
    }
}
