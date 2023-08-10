package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.AbstractRepairContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class AbstractRepairScreen<T extends AbstractRepairContainer> extends ContainerScreen<T> implements IContainerListener
{
    private ResourceLocation guiTexture;

    public AbstractRepairScreen(T container, PlayerInventory playerInventory, ITextComponent title, ResourceLocation guiTexture)
    {
        super(container, playerInventory, title);
        this.guiTexture = guiTexture;
    }

    protected void initFields()
    {
    }

    protected void init()
    {
        super.init();
        this.initFields();
        this.container.addListener(this);
    }

    public void onClose()
    {
        super.onClose();
        this.container.removeListener(this);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.disableBlend();
        this.renderNameField(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    protected void renderNameField(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(this.guiTexture);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        this.blit(matrixStack, i + 59, j + 20, 0, this.ySize + (this.container.getSlot(0).getHasStack() ? 0 : 16), 110, 16);

        if ((this.container.getSlot(0).getHasStack() || this.container.getSlot(1).getHasStack()) && !this.container.getSlot(2).getHasStack())
        {
            this.blit(matrixStack, i + 99, j + 45, this.xSize, 0, 28, 21);
        }
    }

    /**
     * update the crafting window inventory with the items in the list
     */
    public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList)
    {
        this.sendSlotContents(containerToSend, 0, containerToSend.getSlot(0).getStack());
    }

    /**
     * Sends two ints to the client-side Container. Used for furnace burning time, smelting progress, brewing progress,
     * and enchanting level. Normally the first int identifies which variable to update, and the second contains the new
     * value. Both are truncated to shorts in non-local SMP.
     */
    public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue)
    {
    }

    /**
     * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
     * contents of that slot.
     */
    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack)
    {
    }
}
