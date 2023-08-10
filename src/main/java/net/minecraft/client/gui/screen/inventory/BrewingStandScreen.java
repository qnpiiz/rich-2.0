package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.BrewingStandContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class BrewingStandScreen extends ContainerScreen<BrewingStandContainer>
{
    private static final ResourceLocation BREWING_STAND_GUI_TEXTURES = new ResourceLocation("textures/gui/container/brewing_stand.png");
    private static final int[] BUBBLELENGTHS = new int[] {29, 24, 20, 16, 11, 6, 0};

    public BrewingStandScreen(BrewingStandContainer p_i51097_1_, PlayerInventory p_i51097_2_, ITextComponent p_i51097_3_)
    {
        super(p_i51097_1_, p_i51097_2_, p_i51097_3_);
    }

    protected void init()
    {
        super.init();
        this.titleX = (this.xSize - this.font.getStringPropertyWidth(this.title)) / 2;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BREWING_STAND_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        int k = this.container.func_216982_e();
        int l = MathHelper.clamp((18 * k + 20 - 1) / 20, 0, 18);

        if (l > 0)
        {
            this.blit(matrixStack, i + 60, j + 44, 176, 29, l, 4);
        }

        int i1 = this.container.func_216981_f();

        if (i1 > 0)
        {
            int j1 = (int)(28.0F * (1.0F - (float)i1 / 400.0F));

            if (j1 > 0)
            {
                this.blit(matrixStack, i + 97, j + 16, 176, 0, 9, j1);
            }

            j1 = BUBBLELENGTHS[i1 / 2 % 7];

            if (j1 > 0)
            {
                this.blit(matrixStack, i + 63, j + 14 + 29 - j1, 185, 29 - j1, 12, j1);
            }
        }
    }
}
