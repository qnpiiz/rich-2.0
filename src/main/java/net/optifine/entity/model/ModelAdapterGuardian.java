package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.GuardianRenderer;
import net.minecraft.client.renderer.entity.model.GuardianModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterGuardian extends ModelAdapter
{
    public ModelAdapterGuardian()
    {
        super(EntityType.GUARDIAN, "guardian", 0.5F);
    }

    public ModelAdapterGuardian(EntityType entityType, String name, float shadowSize)
    {
        super(entityType, name, shadowSize);
    }

    public Model makeModel()
    {
        return new GuardianModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof GuardianModel))
        {
            return null;
        }
        else
        {
            GuardianModel guardianmodel = (GuardianModel)model;

            if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.getFieldValue(guardianmodel, Reflector.ModelGuardian_body);
            }
            else if (modelPart.equals("eye"))
            {
                return (ModelRenderer)Reflector.getFieldValue(guardianmodel, Reflector.ModelGuardian_eye);
            }
            else
            {
                String s = "spine";

                if (modelPart.startsWith(s))
                {
                    ModelRenderer[] amodelrenderer1 = (ModelRenderer[])Reflector.getFieldValue(guardianmodel, Reflector.ModelGuardian_spines);

                    if (amodelrenderer1 == null)
                    {
                        return null;
                    }
                    else
                    {
                        String s3 = modelPart.substring(s.length());
                        int j = Config.parseInt(s3, -1);
                        --j;
                        return j >= 0 && j < amodelrenderer1.length ? amodelrenderer1[j] : null;
                    }
                }
                else
                {
                    String s1 = "tail";

                    if (modelPart.startsWith(s1))
                    {
                        ModelRenderer[] amodelrenderer = (ModelRenderer[])Reflector.getFieldValue(guardianmodel, Reflector.ModelGuardian_tail);

                        if (amodelrenderer == null)
                        {
                            return null;
                        }
                        else
                        {
                            String s2 = modelPart.substring(s1.length());
                            int i = Config.parseInt(s2, -1);
                            --i;
                            return i >= 0 && i < amodelrenderer.length ? amodelrenderer[i] : null;
                        }
                    }
                    else
                    {
                        return null;
                    }
                }
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"body", "eye", "spine1", "spine2", "spine3", "spine4", "spine5", "spine6", "spine7", "spine8", "spine9", "spine10", "spine11", "spine12", "tail1", "tail2", "tail3"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        GuardianRenderer guardianrenderer = new GuardianRenderer(entityrenderermanager);
        guardianrenderer.entityModel = (GuardianModel)modelBase;
        guardianrenderer.shadowSize = shadowSize;
        return guardianrenderer;
    }
}
