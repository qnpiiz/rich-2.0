package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TranslationTextComponent;

public class InventoryScreen extends DisplayEffectsScreen<PlayerContainer> implements IRecipeShownListener
{
    private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");

    /** The old x position of the mouse pointer */
    private float oldMouseX;

    /** The old y position of the mouse pointer */
    private float oldMouseY;
    private final RecipeBookGui recipeBookGui = new RecipeBookGui();
    private boolean removeRecipeBookGui;
    private boolean widthTooNarrow;
    private boolean buttonClicked;

    public InventoryScreen(PlayerEntity player)
    {
        super(player.inventoryContainer, player.inventory, new TranslationTextComponent("container.crafting"));
        this.passEvents = true;
        this.titleX = 97;
    }

    public void tick()
    {
        if (this.mc.playerController.isInCreativeMode())
        {
            this.mc.displayGuiScreen(new CreativeScreen(this.mc.player));
        }
        else
        {
            this.recipeBookGui.tick();
        }
    }

    protected void init()
    {
        if (this.mc.playerController.isInCreativeMode())
        {
            this.mc.displayGuiScreen(new CreativeScreen(this.mc.player));
        }
        else
        {
            super.init();
            this.widthTooNarrow = this.width < 379;
            this.recipeBookGui.init(this.width, this.height, this.mc, this.widthTooNarrow, this.container);
            this.removeRecipeBookGui = true;
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
            this.children.add(this.recipeBookGui);
            this.setFocusedDefault(this.recipeBookGui);
            this.addButton(new ImageButton(this.guiLeft + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) ->
            {
                this.recipeBookGui.initSearchBar(this.widthTooNarrow);
                this.recipeBookGui.toggleVisibility();
                this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
                ((ImageButton)button).setPosition(this.guiLeft + 104, this.height / 2 - 22);
                this.buttonClicked = true;
            }));
        }
    }

    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y)
    {
        this.font.func_243248_b(matrixStack, this.title, (float)this.titleX, (float)this.titleY, 4210752);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.hasActivePotionEffects = !this.recipeBookGui.isVisible();

        if (this.recipeBookGui.isVisible() && this.widthTooNarrow)
        {
            this.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
            this.recipeBookGui.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        else
        {
            this.recipeBookGui.render(matrixStack, mouseX, mouseY, partialTicks);
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            this.recipeBookGui.func_230477_a_(matrixStack, this.guiLeft, this.guiTop, false, partialTicks);
        }

        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
        this.recipeBookGui.func_238924_c_(matrixStack, this.guiLeft, this.guiTop, mouseX, mouseY);
        this.oldMouseX = (float)mouseX;
        this.oldMouseY = (float)mouseY;
    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        drawEntityOnScreen(i + 51, j + 75, 30, (float)(i + 51) - this.oldMouseX, (float)(j + 75 - 50) - this.oldMouseY, this.mc.player);
    }

    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, LivingEntity livingEntity)
    {
        float f = (float)Math.atan((double)(mouseX / 40.0F));
        float f1 = (float)Math.atan((double)(mouseY / 40.0F));
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)posX, (float)posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        matrixstack.scale((float)scale, (float)scale, (float)scale);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.multiply(quaternion1);
        matrixstack.rotate(quaternion);
        float f2 = livingEntity.renderYawOffset;
        float f3 = livingEntity.rotationYaw;
        float f4 = livingEntity.rotationPitch;
        float f5 = livingEntity.prevRotationYawHead;
        float f6 = livingEntity.rotationYawHead;
        livingEntity.renderYawOffset = 180.0F + f * 20.0F;
        livingEntity.rotationYaw = 180.0F + f * 40.0F;
        livingEntity.rotationPitch = -f1 * 20.0F;
        livingEntity.rotationYawHead = livingEntity.rotationYaw;
        livingEntity.prevRotationYawHead = livingEntity.rotationYaw;
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        quaternion1.conjugate();
        entityrenderermanager.setCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        RenderSystem.runAsFancy(() ->
        {
            entityrenderermanager.renderEntityStatic(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
        });
        irendertypebuffer$impl.finish();
        entityrenderermanager.setRenderShadow(true);
        livingEntity.renderYawOffset = f2;
        livingEntity.rotationYaw = f3;
        livingEntity.rotationPitch = f4;
        livingEntity.prevRotationYawHead = f5;
        livingEntity.rotationYawHead = f6;
        RenderSystem.popMatrix();
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
            return this.widthTooNarrow && this.recipeBookGui.isVisible() ? false : super.mouseClicked(mouseX, mouseY, button);
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (this.buttonClicked)
        {
            this.buttonClicked = false;
            return true;
        }
        else
        {
            return super.mouseReleased(mouseX, mouseY, button);
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
        if (this.removeRecipeBookGui)
        {
            this.recipeBookGui.removed();
        }

        super.onClose();
    }

    public RecipeBookGui getRecipeGui()
    {
        return this.recipeBookGui;
    }
}
