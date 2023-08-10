package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.SmithingTableContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class SmithingTableScreen extends AbstractRepairScreen<SmithingTableContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("textures/gui/container/smithing.png");

    public SmithingTableScreen(SmithingTableContainer container, PlayerInventory playerInventory, ITextComponent title)
    {
        super(container, playerInventory, title, GUI_TEXTURE);
        this.titleX = 60;
        this.titleY = 18;
    }

    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y)
    {
        RenderSystem.disableBlend();
        super.drawGuiContainerForegroundLayer(matrixStack, x, y);
    }
}
