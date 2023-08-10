package net.optifine.entity.model;

import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.EnchantmentTableTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterBook extends ModelAdapter
{
    public ModelAdapterBook()
    {
        super(TileEntityType.ENCHANTING_TABLE, "enchanting_book", 0.0F, new String[] {"book"});
    }

    public Model makeModel()
    {
        return new BookModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof BookModel))
        {
            return null;
        }
        else
        {
            BookModel bookmodel = (BookModel)model;

            if (modelPart.equals("cover_right"))
            {
                return (ModelRenderer)Reflector.ModelBook_ModelRenderers.getValue(bookmodel, 0);
            }
            else if (modelPart.equals("cover_left"))
            {
                return (ModelRenderer)Reflector.ModelBook_ModelRenderers.getValue(bookmodel, 1);
            }
            else if (modelPart.equals("pages_right"))
            {
                return (ModelRenderer)Reflector.ModelBook_ModelRenderers.getValue(bookmodel, 2);
            }
            else if (modelPart.equals("pages_left"))
            {
                return (ModelRenderer)Reflector.ModelBook_ModelRenderers.getValue(bookmodel, 3);
            }
            else if (modelPart.equals("flipping_page_right"))
            {
                return (ModelRenderer)Reflector.ModelBook_ModelRenderers.getValue(bookmodel, 4);
            }
            else if (modelPart.equals("flipping_page_left"))
            {
                return (ModelRenderer)Reflector.ModelBook_ModelRenderers.getValue(bookmodel, 5);
            }
            else
            {
                return modelPart.equals("book_spine") ? (ModelRenderer)Reflector.ModelBook_ModelRenderers.getValue(bookmodel, 6) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"cover_right", "cover_left", "pages_right", "pages_left", "flipping_page_right", "flipping_page_left", "book_spine"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntityRenderer tileentityrenderer = tileentityrendererdispatcher.getRenderer(TileEntityType.ENCHANTING_TABLE);

        if (!(tileentityrenderer instanceof EnchantmentTableTileEntityRenderer))
        {
            return null;
        }
        else
        {
            if (tileentityrenderer.getType() == null)
            {
                tileentityrenderer = new EnchantmentTableTileEntityRenderer(tileentityrendererdispatcher);
            }

            if (!Reflector.TileEntityEnchantmentTableRenderer_modelBook.exists())
            {
                Config.warn("Field not found: TileEntityEnchantmentTableRenderer.modelBook");
                return null;
            }
            else
            {
                Reflector.setFieldValue(tileentityrenderer, Reflector.TileEntityEnchantmentTableRenderer_modelBook, modelBase);
                return tileentityrenderer;
            }
        }
    }
}
