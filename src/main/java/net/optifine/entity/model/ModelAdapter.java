package net.optifine.entity.model;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.util.Either;

public abstract class ModelAdapter
{
    private Either<EntityType, TileEntityType> type;
    private String name;
    private float shadowSize;
    private String[] aliases;

    public ModelAdapter(EntityType entityType, String name, float shadowSize)
    {
        this(Either.makeLeft(entityType), name, shadowSize, (String[])null);
    }

    public ModelAdapter(EntityType entityType, String name, float shadowSize, String[] aliases)
    {
        this(Either.makeLeft(entityType), name, shadowSize, aliases);
    }

    public ModelAdapter(TileEntityType tileEntityType, String name, float shadowSize)
    {
        this(Either.makeRight(tileEntityType), name, shadowSize, (String[])null);
    }

    public ModelAdapter(TileEntityType tileEntityType, String name, float shadowSize, String[] aliases)
    {
        this(Either.makeRight(tileEntityType), name, shadowSize, aliases);
    }

    public ModelAdapter(Either<EntityType, TileEntityType> type, String name, float shadowSize, String[] aliases)
    {
        this.type = type;
        this.name = name;
        this.shadowSize = shadowSize;
        this.aliases = aliases;
    }

    public Either<EntityType, TileEntityType> getType()
    {
        return this.type;
    }

    public String getName()
    {
        return this.name;
    }

    public String[] getAliases()
    {
        return this.aliases;
    }

    public float getShadowSize()
    {
        return this.shadowSize;
    }

    public abstract Model makeModel();

    public abstract ModelRenderer getModelRenderer(Model var1, String var2);

    public abstract String[] getModelRendererNames();

    public abstract IEntityRenderer makeEntityRender(Model var1, float var2);

    public ModelRenderer[] getModelRenderers(Model model)
    {
        String[] astring = this.getModelRendererNames();
        List<ModelRenderer> list = new ArrayList<>();

        for (int i = 0; i < astring.length; ++i)
        {
            String s = astring[i];
            ModelRenderer modelrenderer = this.getModelRenderer(model, s);

            if (modelrenderer != null)
            {
                list.add(modelrenderer);
            }
        }

        return list.toArray(new ModelRenderer[list.size()]);
    }
}
