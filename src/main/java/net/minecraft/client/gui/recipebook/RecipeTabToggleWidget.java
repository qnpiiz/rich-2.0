package net.minecraft.client.gui.recipebook;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public class RecipeTabToggleWidget extends ToggleWidget
{
    private final RecipeBookCategories category;
    private float animationTime;

    public RecipeTabToggleWidget(RecipeBookCategories p_i51075_1_)
    {
        super(0, 0, 35, 27, false);
        this.category = p_i51075_1_;
        this.initTextureValues(153, 2, 35, 0, RecipeBookGui.RECIPE_BOOK);
    }

    public void startAnimation(Minecraft p_193918_1_)
    {
        ClientRecipeBook clientrecipebook = p_193918_1_.player.getRecipeBook();
        List<RecipeList> list = clientrecipebook.getRecipes(this.category);

        if (p_193918_1_.player.openContainer instanceof RecipeBookContainer)
        {
            for (RecipeList recipelist : list)
            {
                for (IRecipe<?> irecipe : recipelist.getRecipes(clientrecipebook.func_242141_a((RecipeBookContainer)p_193918_1_.player.openContainer)))
                {
                    if (clientrecipebook.isNew(irecipe))
                    {
                        this.animationTime = 15.0F;
                        return;
                    }
                }
            }
        }
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (this.animationTime > 0.0F)
        {
            float f = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * (float)Math.PI));
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
            RenderSystem.scalef(1.0F, f, 1.0F);
            RenderSystem.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
        }

        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(this.resourceLocation);
        RenderSystem.disableDepthTest();
        int i = this.xTexStart;
        int j = this.yTexStart;

        if (this.stateTriggered)
        {
            i += this.xDiffTex;
        }

        if (this.isHovered())
        {
            j += this.yDiffTex;
        }

        int k = this.x;

        if (this.stateTriggered)
        {
            k -= 2;
        }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.blit(matrixStack, k, this.y, i, j, this.width, this.height);
        RenderSystem.enableDepthTest();
        this.renderIcon(minecraft.getItemRenderer());

        if (this.animationTime > 0.0F)
        {
            RenderSystem.popMatrix();
            this.animationTime -= partialTicks;
        }
    }

    private void renderIcon(ItemRenderer p_193920_1_)
    {
        List<ItemStack> list = this.category.getIcons();
        int i = this.stateTriggered ? -2 : 0;

        if (list.size() == 1)
        {
            p_193920_1_.renderItemAndEffectIntoGuiWithoutEntity(list.get(0), this.x + 9 + i, this.y + 5);
        }
        else if (list.size() == 2)
        {
            p_193920_1_.renderItemAndEffectIntoGuiWithoutEntity(list.get(0), this.x + 3 + i, this.y + 5);
            p_193920_1_.renderItemAndEffectIntoGuiWithoutEntity(list.get(1), this.x + 14 + i, this.y + 5);
        }
    }

    public RecipeBookCategories func_201503_d()
    {
        return this.category;
    }

    public boolean func_199500_a(ClientRecipeBook p_199500_1_)
    {
        List<RecipeList> list = p_199500_1_.getRecipes(this.category);
        this.visible = false;

        if (list != null)
        {
            for (RecipeList recipelist : list)
            {
                if (recipelist.isNotEmpty() && recipelist.containsValidRecipes())
                {
                    this.visible = true;
                    break;
                }
            }
        }

        return this.visible;
    }
}
