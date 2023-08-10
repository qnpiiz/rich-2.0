package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.HorseInventoryContainer;
import net.minecraft.util.ResourceLocation;

public class HorseInventoryScreen extends ContainerScreen<HorseInventoryContainer>
{
    private static final ResourceLocation HORSE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/horse.png");

    /** The EntityHorse whose inventory is currently being accessed. */
    private final AbstractHorseEntity horseEntity;

    /** The mouse x-position recorded during the last rendered frame. */
    private float mousePosx;

    /** The mouse y-position recorded during the last renderered frame. */
    private float mousePosY;

    public HorseInventoryScreen(HorseInventoryContainer p_i51084_1_, PlayerInventory p_i51084_2_, AbstractHorseEntity p_i51084_3_)
    {
        super(p_i51084_1_, p_i51084_2_, p_i51084_3_.getDisplayName());
        this.horseEntity = p_i51084_3_;
        this.passEvents = false;
    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(HORSE_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);

        if (this.horseEntity instanceof AbstractChestedHorseEntity)
        {
            AbstractChestedHorseEntity abstractchestedhorseentity = (AbstractChestedHorseEntity)this.horseEntity;

            if (abstractchestedhorseentity.hasChest())
            {
                this.blit(matrixStack, i + 79, j + 17, 0, this.ySize, abstractchestedhorseentity.getInventoryColumns() * 18, 54);
            }
        }

        if (this.horseEntity.func_230264_L__())
        {
            this.blit(matrixStack, i + 7, j + 35 - 18, 18, this.ySize + 54, 18, 18);
        }

        if (this.horseEntity.func_230276_fq_())
        {
            if (this.horseEntity instanceof LlamaEntity)
            {
                this.blit(matrixStack, i + 7, j + 35, 36, this.ySize + 54, 18, 18);
            }
            else
            {
                this.blit(matrixStack, i + 7, j + 35, 0, this.ySize + 54, 18, 18);
            }
        }

        InventoryScreen.drawEntityOnScreen(i + 51, j + 60, 17, (float)(i + 51) - this.mousePosx, (float)(j + 75 - 50) - this.mousePosY, this.horseEntity);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.mousePosx = (float)mouseX;
        this.mousePosY = (float)mouseY;
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }
}
