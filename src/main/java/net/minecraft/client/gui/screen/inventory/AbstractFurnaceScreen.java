package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.recipebook.AbstractRecipeBookGui;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractFurnaceScreen<T extends AbstractFurnaceContainer> extends ContainerScreen<T> implements IRecipeShownListener
{
    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
    public final AbstractRecipeBookGui recipeGui;
    private boolean widthTooNarrowIn;
    private final ResourceLocation guiTexture;

    public AbstractFurnaceScreen(T screenContainer, AbstractRecipeBookGui recipeGuiIn, PlayerInventory inv, ITextComponent titleIn, ResourceLocation guiTextureIn)
    {
        super(screenContainer, inv, titleIn);
        this.recipeGui = recipeGuiIn;
        this.guiTexture = guiTextureIn;
    }

    public void init()
    {
        super.init();
        this.widthTooNarrowIn = this.width < 379;
        this.recipeGui.init(this.width, this.height, this.mc, this.widthTooNarrowIn, this.container);
        this.guiLeft = this.recipeGui.updateScreenPosition(this.widthTooNarrowIn, this.width, this.xSize);
        this.addButton(new ImageButton(this.guiLeft + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, BUTTON_TEXTURE, (button) ->
        {
            this.recipeGui.initSearchBar(this.widthTooNarrowIn);
            this.recipeGui.toggleVisibility();
            this.guiLeft = this.recipeGui.updateScreenPosition(this.widthTooNarrowIn, this.width, this.xSize);
            ((ImageButton)button).setPosition(this.guiLeft + 20, this.height / 2 - 49);
        }));
        this.titleX = (this.xSize - this.font.getStringPropertyWidth(this.title)) / 2;
    }

    public void tick()
    {
        super.tick();
        this.recipeGui.tick();
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);

        if (this.recipeGui.isVisible() && this.widthTooNarrowIn)
        {
            this.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
            this.recipeGui.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        else
        {
            this.recipeGui.render(matrixStack, mouseX, mouseY, partialTicks);
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            this.recipeGui.func_230477_a_(matrixStack, this.guiLeft, this.guiTop, true, partialTicks);
        }

        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
        this.recipeGui.func_238924_c_(matrixStack, this.guiLeft, this.guiTop, mouseX, mouseY);
    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(this.guiTexture);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);

        if (this.container.isBurning())
        {
            int k = this.container.getBurnLeftScaled();
            this.blit(matrixStack, i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        int l = this.container.getCookProgressionScaled();
        this.blit(matrixStack, i + 79, j + 34, 176, 14, l + 1, 16);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.recipeGui.mouseClicked(mouseX, mouseY, button))
        {
            return true;
        }
        else
        {
            return this.widthTooNarrowIn && this.recipeGui.isVisible() ? true : super.mouseClicked(mouseX, mouseY, button);
        }
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        this.recipeGui.slotClicked(slotIn);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return this.recipeGui.keyPressed(keyCode, scanCode, modifiers) ? false : super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton)
    {
        boolean flag = mouseX < (double)guiLeftIn || mouseY < (double)guiTopIn || mouseX >= (double)(guiLeftIn + this.xSize) || mouseY >= (double)(guiTopIn + this.ySize);
        return this.recipeGui.func_195604_a(mouseX, mouseY, this.guiLeft, this.guiTop, this.xSize, this.ySize, mouseButton) && flag;
    }

    public boolean charTyped(char codePoint, int modifiers)
    {
        return this.recipeGui.charTyped(codePoint, modifiers) ? true : super.charTyped(codePoint, modifiers);
    }

    public void recipesUpdated()
    {
        this.recipeGui.recipesUpdated();
    }

    public RecipeBookGui getRecipeGui()
    {
        return this.recipeGui;
    }

    public void onClose()
    {
        this.recipeGui.removed();
        super.onClose();
    }
}
