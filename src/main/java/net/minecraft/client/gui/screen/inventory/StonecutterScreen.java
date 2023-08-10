package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.StonecutterContainer;
import net.minecraft.item.crafting.StonecuttingRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class StonecutterScreen extends ContainerScreen<StonecutterContainer>
{
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/container/stonecutter.png");
    private float sliderProgress;

    /** Is {@code true} if the player clicked on the scroll wheel in the GUI. */
    private boolean clickedOnSroll;

    /**
     * The index of the first recipe to display.
     * The number of recipes displayed at any time is 12 (4 recipes per row, and 3 rows). If the player scrolled down
     * one row, this value would be 4 (representing the index of the first slot on the second row).
     */
    private int recipeIndexOffset;
    private boolean hasItemsInInputSlot;

    public StonecutterScreen(StonecutterContainer containerIn, PlayerInventory playerInv, ITextComponent titleIn)
    {
        super(containerIn, playerInv, titleIn);
        containerIn.setInventoryUpdateListener(this::onInventoryUpdate);
        --this.titleY;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        this.renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        int k = (int)(41.0F * this.sliderProgress);
        this.blit(matrixStack, i + 119, j + 15 + k, 176 + (this.canScroll() ? 0 : 12), 0, 12, 15);
        int l = this.guiLeft + 52;
        int i1 = this.guiTop + 14;
        int j1 = this.recipeIndexOffset + 12;
        this.func_238853_b_(matrixStack, x, y, l, i1, j1);
        this.drawRecipesItems(l, i1, j1);
    }

    protected void renderHoveredTooltip(MatrixStack matrixStack, int x, int y)
    {
        super.renderHoveredTooltip(matrixStack, x, y);

        if (this.hasItemsInInputSlot)
        {
            int i = this.guiLeft + 52;
            int j = this.guiTop + 14;
            int k = this.recipeIndexOffset + 12;
            List<StonecuttingRecipe> list = this.container.getRecipeList();

            for (int l = this.recipeIndexOffset; l < k && l < this.container.getRecipeListSize(); ++l)
            {
                int i1 = l - this.recipeIndexOffset;
                int j1 = i + i1 % 4 * 16;
                int k1 = j + i1 / 4 * 18 + 2;

                if (x >= j1 && x < j1 + 16 && y >= k1 && y < k1 + 18)
                {
                    this.renderTooltip(matrixStack, list.get(l).getRecipeOutput(), x, y);
                }
            }
        }
    }

    private void func_238853_b_(MatrixStack matrixStack, int x, int y, int p_238853_4_, int p_238853_5_, int p_238853_6_)
    {
        for (int i = this.recipeIndexOffset; i < p_238853_6_ && i < this.container.getRecipeListSize(); ++i)
        {
            int j = i - this.recipeIndexOffset;
            int k = p_238853_4_ + j % 4 * 16;
            int l = j / 4;
            int i1 = p_238853_5_ + l * 18 + 2;
            int j1 = this.ySize;

            if (i == this.container.getSelectedRecipe())
            {
                j1 += 18;
            }
            else if (x >= k && y >= i1 && x < k + 16 && y < i1 + 18)
            {
                j1 += 36;
            }

            this.blit(matrixStack, k, i1 - 1, 0, j1, 16, 18);
        }
    }

    private void drawRecipesItems(int left, int top, int recipeIndexOffsetMax)
    {
        List<StonecuttingRecipe> list = this.container.getRecipeList();

        for (int i = this.recipeIndexOffset; i < recipeIndexOffsetMax && i < this.container.getRecipeListSize(); ++i)
        {
            int j = i - this.recipeIndexOffset;
            int k = left + j % 4 * 16;
            int l = j / 4;
            int i1 = top + l * 18 + 2;
            this.mc.getItemRenderer().renderItemAndEffectIntoGUI(list.get(i).getRecipeOutput(), k, i1);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        this.clickedOnSroll = false;

        if (this.hasItemsInInputSlot)
        {
            int i = this.guiLeft + 52;
            int j = this.guiTop + 14;
            int k = this.recipeIndexOffset + 12;

            for (int l = this.recipeIndexOffset; l < k; ++l)
            {
                int i1 = l - this.recipeIndexOffset;
                double d0 = mouseX - (double)(i + i1 % 4 * 16);
                double d1 = mouseY - (double)(j + i1 / 4 * 18);

                if (d0 >= 0.0D && d1 >= 0.0D && d0 < 16.0D && d1 < 18.0D && this.container.enchantItem(this.mc.player, l))
                {
                    Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    this.mc.playerController.sendEnchantPacket((this.container).windowId, l);
                    return true;
                }
            }

            i = this.guiLeft + 119;
            j = this.guiTop + 9;

            if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + 54))
            {
                this.clickedOnSroll = true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (this.clickedOnSroll && this.canScroll())
        {
            int i = this.guiTop + 14;
            int j = i + 54;
            this.sliderProgress = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.sliderProgress = MathHelper.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.recipeIndexOffset = (int)((double)(this.sliderProgress * (float)this.getHiddenRows()) + 0.5D) * 4;
            return true;
        }
        else
        {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        if (this.canScroll())
        {
            int i = this.getHiddenRows();
            this.sliderProgress = (float)((double)this.sliderProgress - delta / (double)i);
            this.sliderProgress = MathHelper.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.recipeIndexOffset = (int)((double)(this.sliderProgress * (float)i) + 0.5D) * 4;
        }

        return true;
    }

    private boolean canScroll()
    {
        return this.hasItemsInInputSlot && this.container.getRecipeListSize() > 12;
    }

    protected int getHiddenRows()
    {
        return (this.container.getRecipeListSize() + 4 - 1) / 4 - 3;
    }

    /**
     * Called every time this screen's container is changed (is marked as dirty).
     */
    private void onInventoryUpdate()
    {
        this.hasItemsInInputSlot = this.container.hasItemsinInputSlot();

        if (!this.hasItemsInInputSlot)
        {
            this.sliderProgress = 0.0F;
            this.recipeIndexOffset = 0;
        }
    }
}
