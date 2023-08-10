package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RecipeWidget extends Widget
{
    private static final ResourceLocation RECIPE_BOOK = new ResourceLocation("textures/gui/recipe_book.png");
    private static final ITextComponent field_243412_b = new TranslationTextComponent("gui.recipebook.moreRecipes");
    private RecipeBookContainer<?> field_203401_p;
    private RecipeBook book;
    private RecipeList list;
    private float time;
    private float animationTime;
    private int currentIndex;

    public RecipeWidget()
    {
        super(0, 0, 25, 25, StringTextComponent.EMPTY);
    }

    public void func_203400_a(RecipeList p_203400_1_, RecipeBookPage p_203400_2_)
    {
        this.list = p_203400_1_;
        this.field_203401_p = (RecipeBookContainer)p_203400_2_.func_203411_d().player.openContainer;
        this.book = p_203400_2_.func_203412_e();
        List < IRecipe<? >> list = p_203400_1_.getRecipes(this.book.func_242141_a(this.field_203401_p));

        for (IRecipe<?> irecipe : list)
        {
            if (this.book.isNew(irecipe))
            {
                p_203400_2_.recipesShown(list);
                this.animationTime = 15.0F;
                break;
            }
        }
    }

    public RecipeList getList()
    {
        return this.list;
    }

    public void setPosition(int p_191770_1_, int p_191770_2_)
    {
        this.x = p_191770_1_;
        this.y = p_191770_2_;
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (!Screen.hasControlDown())
        {
            this.time += partialTicks;
        }

        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(RECIPE_BOOK);
        int i = 29;

        if (!this.list.containsCraftableRecipes())
        {
            i += 25;
        }

        int j = 206;

        if (this.list.getRecipes(this.book.func_242141_a(this.field_203401_p)).size() > 1)
        {
            j += 25;
        }

        boolean flag = this.animationTime > 0.0F;

        if (flag)
        {
            float f = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * (float)Math.PI));
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
            RenderSystem.scalef(f, f, 1.0F);
            RenderSystem.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
            this.animationTime -= partialTicks;
        }

        this.blit(matrixStack, this.x, this.y, i, j, this.width, this.height);
        List < IRecipe<? >> list = this.getOrderedRecipes();
        this.currentIndex = MathHelper.floor(this.time / 30.0F) % list.size();
        ItemStack itemstack = list.get(this.currentIndex).getRecipeOutput();
        int k = 4;

        if (this.list.hasSingleResultItem() && this.getOrderedRecipes().size() > 1)
        {
            minecraft.getItemRenderer().renderItemAndEffectIntoGUI(itemstack, this.x + k + 1, this.y + k + 1);
            --k;
        }

        minecraft.getItemRenderer().renderItemAndEffectIntoGuiWithoutEntity(itemstack, this.x + k, this.y + k);

        if (flag)
        {
            RenderSystem.popMatrix();
        }
    }

    private List < IRecipe<? >> getOrderedRecipes()
    {
        List < IRecipe<? >> list = this.list.getDisplayRecipes(true);

        if (!this.book.func_242141_a(this.field_203401_p))
        {
            list.addAll(this.list.getDisplayRecipes(false));
        }

        return list;
    }

    public boolean isOnlyOption()
    {
        return this.getOrderedRecipes().size() == 1;
    }

    public IRecipe<?> getRecipe()
    {
        List < IRecipe<? >> list = this.getOrderedRecipes();
        return list.get(this.currentIndex);
    }

    public List<ITextComponent> getToolTipText(Screen p_191772_1_)
    {
        ItemStack itemstack = this.getOrderedRecipes().get(this.currentIndex).getRecipeOutput();
        List<ITextComponent> list = Lists.newArrayList(p_191772_1_.getTooltipFromItem(itemstack));

        if (this.list.getRecipes(this.book.func_242141_a(this.field_203401_p)).size() > 1)
        {
            list.add(field_243412_b);
        }

        return list;
    }

    public int getWidth()
    {
        return 25;
    }

    protected boolean isValidClickButton(int button)
    {
        return button == 0 || button == 1;
    }
}
