package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CRenameItemPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class AnvilScreen extends AbstractRepairScreen<RepairContainer>
{
    private static final ResourceLocation ANVIL_RESOURCE = new ResourceLocation("textures/gui/container/anvil.png");
    private static final ITextComponent field_243333_B = new TranslationTextComponent("container.repair.expensive");
    private TextFieldWidget nameField;

    public AnvilScreen(RepairContainer container, PlayerInventory playerInventory, ITextComponent title)
    {
        super(container, playerInventory, title, ANVIL_RESOURCE);
        this.titleX = 60;
    }

    public void tick()
    {
        super.tick();
        this.nameField.tick();
    }

    protected void initFields()
    {
        this.mc.keyboardListener.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.nameField = new TextFieldWidget(this.font, i + 62, j + 24, 103, 12, new TranslationTextComponent("container.repair"));
        this.nameField.setCanLoseFocus(false);
        this.nameField.setTextColor(-1);
        this.nameField.setDisabledTextColour(-1);
        this.nameField.setEnableBackgroundDrawing(false);
        this.nameField.setMaxStringLength(35);
        this.nameField.setResponder(this::renameItem);
        this.children.add(this.nameField);
        this.setFocusedDefault(this.nameField);
    }

    public void resize(Minecraft minecraft, int width, int height)
    {
        String s = this.nameField.getText();
        this.init(minecraft, width, height);
        this.nameField.setText(s);
    }

    public void onClose()
    {
        super.onClose();
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.mc.player.closeScreen();
        }

        return !this.nameField.keyPressed(keyCode, scanCode, modifiers) && !this.nameField.canWrite() ? super.keyPressed(keyCode, scanCode, modifiers) : true;
    }

    private void renameItem(String name)
    {
        if (!name.isEmpty())
        {
            String s = name;
            Slot slot = this.container.getSlot(0);

            if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && name.equals(slot.getStack().getDisplayName().getString()))
            {
                s = "";
            }

            this.container.updateItemName(s);
            this.mc.player.connection.sendPacket(new CRenameItemPacket(s));
        }
    }

    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y)
    {
        RenderSystem.disableBlend();
        super.drawGuiContainerForegroundLayer(matrixStack, x, y);
        int i = this.container.getMaximumCost();

        if (i > 0)
        {
            int j = 8453920;
            ITextComponent itextcomponent;

            if (i >= 40 && !this.mc.player.abilities.isCreativeMode)
            {
                itextcomponent = field_243333_B;
                j = 16736352;
            }
            else if (!this.container.getSlot(2).getHasStack())
            {
                itextcomponent = null;
            }
            else
            {
                itextcomponent = new TranslationTextComponent("container.repair.cost", i);

                if (!this.container.getSlot(2).canTakeStack(this.playerInventory.player))
                {
                    j = 16736352;
                }
            }

            if (itextcomponent != null)
            {
                int k = this.xSize - 8 - this.font.getStringPropertyWidth(itextcomponent) - 2;
                int l = 69;
                fill(matrixStack, k - 2, 67, this.xSize - 8, 79, 1325400064);
                this.font.func_243246_a(matrixStack, itextcomponent, (float)k, 69.0F, j);
            }
        }
    }

    public void renderNameField(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.nameField.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    /**
     * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
     * contents of that slot.
     */
    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack)
    {
        if (slotInd == 0)
        {
            this.nameField.setText(stack.isEmpty() ? "" : stack.getDisplayName().getString());
            this.nameField.setEnabled(!stack.isEmpty());
            this.setListener(this.nameField);
        }
    }
}
