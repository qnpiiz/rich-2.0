package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CraftingScreen extends ContainerScreen<WorkbenchContainer> implements IRecipeShownListener
{
    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");
    private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
    private final RecipeBookGui recipeBookGui = new RecipeBookGui();
    private boolean widthTooNarrow;

    public CraftingScreen(WorkbenchContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
    }

    protected void init()
    {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookGui.init(this.width, this.height, this.mc, this.widthTooNarrow, this.container);
        this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
        this.children.add(this.recipeBookGui);
        this.setFocusedDefault(this.recipeBookGui);
        this.addButton(new ImageButton(this.guiLeft + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) ->
        {
            this.recipeBookGui.initSearchBar(this.widthTooNarrow);
            this.recipeBookGui.toggleVisibility();
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
            ((ImageButton)button).setPosition(this.guiLeft + 5, this.height / 2 - 49);
        }));
        this.titleX = 29;
    }

    public void tick()
    {
        super.tick();
        this.recipeBookGui.tick();
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);

        if (this.recipeBookGui.isVisible() && this.widthTooNarrow)
        {
            this.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
            this.recipeBookGui.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        else
        {
            this.recipeBookGui.render(matrixStack, mouseX, mouseY, partialTicks);
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            this.recipeBookGui.func_230477_a_(matrixStack, this.guiLeft, this.guiTop, true, partialTicks);
        }

        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
        this.recipeBookGui.func_238924_c_(matrixStack, this.guiLeft, this.guiTop, mouseX, mouseY);
    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
        int i = this.guiLeft;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
    }

    protected boolean isPointInRegion(int x, int y, int width, int height, double mouseX, double mouseY)
    {
        return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(x, y, width, height, mouseX, mouseY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.recipeBookGui.mouseClicked(mouseX, mouseY, button))
        {
            this.setListener(this.recipeBookGui);
            return true;
        }
        else
        {
            return this.widthTooNarrow && this.recipeBookGui.isVisible() ? true : super.mouseClicked(mouseX, mouseY, button);
        }
    }

    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton)
    {
        boolean flag = mouseX < (double)guiLeftIn || mouseY < (double)guiTopIn || mouseX >= (double)(guiLeftIn + this.xSize) || mouseY >= (double)(guiTopIn + this.ySize);
        return this.recipeBookGui.func_195604_a(mouseX, mouseY, this.guiLeft, this.guiTop, this.xSize, this.ySize, mouseButton) && flag;
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        this.recipeBookGui.slotClicked(slotIn);
    }

    public void recipesUpdated()
    {
        this.recipeBookGui.recipesUpdated();
    }

    public void onClose()
    {
        this.recipeBookGui.removed();
        super.onClose();
    }

    public RecipeBookGui getRecipeGui()
    {
        return this.recipeBookGui;
    }
}
