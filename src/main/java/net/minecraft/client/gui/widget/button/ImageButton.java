package net.minecraft.client.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ImageButton extends Button
{
    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffText;
    private final int textureWidth;
    private final int textureHeight;

    public ImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, Button.IPressable onPressIn)
    {
        this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, 256, 256, onPressIn);
    }

    public ImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, int p_i51135_9_, int p_i51135_10_, Button.IPressable onPressIn)
    {
        this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, p_i51135_9_, p_i51135_10_, onPressIn, StringTextComponent.EMPTY);
    }

    public ImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation resourceLocation, int textureWidth, int textureHeight, Button.IPressable onPress, ITextComponent title)
    {
        this(x, y, width, height, xTexStart, yTexStart, yDiffText, resourceLocation, textureWidth, textureHeight, onPress, field_238486_s_, title);
    }

    public ImageButton(int p_i242137_1_, int p_i242137_2_, int p_i242137_3_, int p_i242137_4_, int p_i242137_5_, int p_i242137_6_, int p_i242137_7_, ResourceLocation p_i242137_8_, int p_i242137_9_, int p_i242137_10_, Button.IPressable p_i242137_11_, Button.ITooltip p_i242137_12_, ITextComponent p_i242137_13_)
    {
        super(p_i242137_1_, p_i242137_2_, p_i242137_3_, p_i242137_4_, p_i242137_13_, p_i242137_11_, p_i242137_12_);
        this.textureWidth = p_i242137_9_;
        this.textureHeight = p_i242137_10_;
        this.xTexStart = p_i242137_5_;
        this.yTexStart = p_i242137_6_;
        this.yDiffText = p_i242137_7_;
        this.resourceLocation = p_i242137_8_;
    }

    public void setPosition(int xIn, int yIn)
    {
        this.x = xIn;
        this.y = yIn;
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(this.resourceLocation);
        int i = this.yTexStart;

        if (this.isHovered())
        {
            i += this.yDiffText;
        }

        RenderSystem.enableDepthTest();
        blit(matrixStack, this.x, this.y, (float)this.xTexStart, (float)i, this.width, this.height, this.textureWidth, this.textureHeight);

        if (this.isHovered())
        {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }
}
