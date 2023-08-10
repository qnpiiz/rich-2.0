package net.optifine.entity.model.anim;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;
import net.optifine.entity.model.CustomModelRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.expr.IExpression;
import net.optifine.util.Either;

public class ModelResolver implements IModelResolver
{
    private ModelAdapter modelAdapter;
    private Model model;
    private CustomModelRenderer[] customModelRenderers;
    private ModelRenderer thisModelRenderer;
    private ModelRenderer partModelRenderer;
    private IRenderResolver renderResolver;

    public ModelResolver(ModelAdapter modelAdapter, Model model, CustomModelRenderer[] customModelRenderers)
    {
        this.modelAdapter = modelAdapter;
        this.model = model;
        this.customModelRenderers = customModelRenderers;
        Either<EntityType, TileEntityType> either = modelAdapter.getType();

        if (either.getRight().isPresent())
        {
            this.renderResolver = new RenderResolverTileEntity();
        }
        else
        {
            this.renderResolver = new RenderResolverEntity();
        }
    }

    public IExpression getExpression(String name)
    {
        IExpression iexpression = this.getModelVariable(name);

        if (iexpression != null)
        {
            return iexpression;
        }
        else
        {
            IExpression iexpression1 = this.renderResolver.getParameter(name);
            return iexpression1 != null ? iexpression1 : null;
        }
    }

    public ModelRenderer getModelRenderer(String name)
    {
        if (name == null)
        {
            return null;
        }
        else if (name.indexOf(":") >= 0)
        {
            String[] astring = Config.tokenize(name, ":");
            ModelRenderer modelrenderer3 = this.getModelRenderer(astring[0]);

            for (int j = 1; j < astring.length; ++j)
            {
                String s = astring[j];
                ModelRenderer modelrenderer4 = modelrenderer3.getChildDeep(s);

                if (modelrenderer4 == null)
                {
                    return null;
                }

                modelrenderer3 = modelrenderer4;
            }

            return modelrenderer3;
        }
        else if (this.thisModelRenderer != null && name.equals("this"))
        {
            return this.thisModelRenderer;
        }
        else if (this.partModelRenderer != null && name.equals("part"))
        {
            return this.partModelRenderer;
        }
        else
        {
            ModelRenderer modelrenderer = this.modelAdapter.getModelRenderer(this.model, name);

            if (modelrenderer != null)
            {
                return modelrenderer;
            }
            else
            {
                for (int i = 0; i < this.customModelRenderers.length; ++i)
                {
                    CustomModelRenderer custommodelrenderer = this.customModelRenderers[i];
                    ModelRenderer modelrenderer1 = custommodelrenderer.getModelRenderer();

                    if (name.equals(modelrenderer1.getId()))
                    {
                        return modelrenderer1;
                    }

                    ModelRenderer modelrenderer2 = modelrenderer1.getChildDeep(name);

                    if (modelrenderer2 != null)
                    {
                        return modelrenderer2;
                    }
                }

                return null;
            }
        }
    }

    public ModelVariableFloat getModelVariable(String name)
    {
        String[] astring = Config.tokenize(name, ".");

        if (astring.length != 2)
        {
            return null;
        }
        else
        {
            String s = astring[0];
            String s1 = astring[1];
            ModelRenderer modelrenderer = this.getModelRenderer(s);

            if (modelrenderer == null)
            {
                return null;
            }
            else
            {
                ModelVariableType modelvariabletype = ModelVariableType.parse(s1);
                return modelvariabletype == null ? null : new ModelVariableFloat(name, modelrenderer, modelvariabletype);
            }
        }
    }

    public void setPartModelRenderer(ModelRenderer partModelRenderer)
    {
        this.partModelRenderer = partModelRenderer;
    }

    public void setThisModelRenderer(ModelRenderer thisModelRenderer)
    {
        this.thisModelRenderer = thisModelRenderer;
    }
}
