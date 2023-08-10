package net.minecraft.client.gui.advancements;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class AdvancementTabGui extends AbstractGui
{
    private final Minecraft minecraft;
    private final AdvancementsScreen screen;
    private final AdvancementTabType type;
    private final int index;
    private final Advancement advancement;
    private final DisplayInfo display;
    private final ItemStack icon;
    private final ITextComponent title;
    private final AdvancementEntryGui root;
    private final Map<Advancement, AdvancementEntryGui> guis = Maps.newLinkedHashMap();
    private double scrollX;
    private double scrollY;
    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxY = Integer.MIN_VALUE;
    private float fade;
    private boolean centered;

    public AdvancementTabGui(Minecraft minecraft, AdvancementsScreen screen, AdvancementTabType type, int index, Advancement advancement, DisplayInfo displayInfo)
    {
        this.minecraft = minecraft;
        this.screen = screen;
        this.type = type;
        this.index = index;
        this.advancement = advancement;
        this.display = displayInfo;
        this.icon = displayInfo.getIcon();
        this.title = displayInfo.getTitle();
        this.root = new AdvancementEntryGui(this, minecraft, advancement, displayInfo);
        this.addGuiAdvancement(this.root, advancement);
    }

    public Advancement getAdvancement()
    {
        return this.advancement;
    }

    public ITextComponent getTitle()
    {
        return this.title;
    }

    public void renderTabSelectorBackground(MatrixStack matrixStack, int offsetX, int offsetY, boolean isSelected)
    {
        this.type.renderTabSelectorBackground(matrixStack, this, offsetX, offsetY, isSelected, this.index);
    }

    public void drawIcon(int offsetX, int offsetY, ItemRenderer renderer)
    {
        this.type.drawIcon(offsetX, offsetY, this.index, renderer, this.icon);
    }

    public void drawTabBackground(MatrixStack matrixStack)
    {
        if (!this.centered)
        {
            this.scrollX = (double)(117 - (this.maxX + this.minX) / 2);
            this.scrollY = (double)(56 - (this.maxY + this.minY) / 2);
            this.centered = true;
        }

        RenderSystem.pushMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
        RenderSystem.depthFunc(518);
        fill(matrixStack, 234, 113, 0, 0, -16777216);
        RenderSystem.depthFunc(515);
        ResourceLocation resourcelocation = this.display.getBackground();

        if (resourcelocation != null)
        {
            this.minecraft.getTextureManager().bindTexture(resourcelocation);
        }
        else
        {
            this.minecraft.getTextureManager().bindTexture(TextureManager.RESOURCE_LOCATION_EMPTY);
        }

        int i = MathHelper.floor(this.scrollX);
        int j = MathHelper.floor(this.scrollY);
        int k = i % 16;
        int l = j % 16;

        for (int i1 = -1; i1 <= 15; ++i1)
        {
            for (int j1 = -1; j1 <= 8; ++j1)
            {
                blit(matrixStack, k + 16 * i1, l + 16 * j1, 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }

        this.root.drawConnectionLineToParent(matrixStack, i, j, true);
        this.root.drawConnectionLineToParent(matrixStack, i, j, false);
        this.root.drawAdvancement(matrixStack, i, j);
        RenderSystem.depthFunc(518);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
        RenderSystem.depthFunc(515);
        RenderSystem.popMatrix();
    }

    public void drawTabTooltips(MatrixStack matrixStack, int mouseX, int mouseY, int width, int height)
    {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0F, 0.0F, 200.0F);
        fill(matrixStack, 0, 0, 234, 113, MathHelper.floor(this.fade * 255.0F) << 24);
        boolean flag = false;
        int i = MathHelper.floor(this.scrollX);
        int j = MathHelper.floor(this.scrollY);

        if (mouseX > 0 && mouseX < 234 && mouseY > 0 && mouseY < 113)
        {
            for (AdvancementEntryGui advancemententrygui : this.guis.values())
            {
                if (advancemententrygui.isMouseOver(i, j, mouseX, mouseY))
                {
                    flag = true;
                    advancemententrygui.drawAdvancementHover(matrixStack, i, j, this.fade, width, height);
                    break;
                }
            }
        }

        RenderSystem.popMatrix();

        if (flag)
        {
            this.fade = MathHelper.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        }
        else
        {
            this.fade = MathHelper.clamp(this.fade - 0.04F, 0.0F, 1.0F);
        }
    }

    public boolean isInsideTabSelector(int offsetX, int offsetY, double mouseX, double mouseY)
    {
        return this.type.inInsideTabSelector(offsetX, offsetY, this.index, mouseX, mouseY);
    }

    @Nullable
    public static AdvancementTabGui create(Minecraft minecraft, AdvancementsScreen screen, int tabIndex, Advancement advancement)
    {
        if (advancement.getDisplay() == null)
        {
            return null;
        }
        else
        {
            for (AdvancementTabType advancementtabtype : AdvancementTabType.values())
            {
                if (tabIndex < advancementtabtype.getMax())
                {
                    return new AdvancementTabGui(minecraft, screen, advancementtabtype, tabIndex, advancement, advancement.getDisplay());
                }

                tabIndex -= advancementtabtype.getMax();
            }

            return null;
        }
    }

    public void dragSelectedGui(double dragX, double dragY)
    {
        if (this.maxX - this.minX > 234)
        {
            this.scrollX = MathHelper.clamp(this.scrollX + dragX, (double)(-(this.maxX - 234)), 0.0D);
        }

        if (this.maxY - this.minY > 113)
        {
            this.scrollY = MathHelper.clamp(this.scrollY + dragY, (double)(-(this.maxY - 113)), 0.0D);
        }
    }

    public void addAdvancement(Advancement advancement)
    {
        if (advancement.getDisplay() != null)
        {
            AdvancementEntryGui advancemententrygui = new AdvancementEntryGui(this, this.minecraft, advancement, advancement.getDisplay());
            this.addGuiAdvancement(advancemententrygui, advancement);
        }
    }

    private void addGuiAdvancement(AdvancementEntryGui gui, Advancement advancement)
    {
        this.guis.put(advancement, gui);
        int i = gui.getX();
        int j = i + 28;
        int k = gui.getY();
        int l = k + 27;
        this.minX = Math.min(this.minX, i);
        this.maxX = Math.max(this.maxX, j);
        this.minY = Math.min(this.minY, k);
        this.maxY = Math.max(this.maxY, l);

        for (AdvancementEntryGui advancemententrygui : this.guis.values())
        {
            advancemententrygui.attachToParent();
        }
    }

    @Nullable
    public AdvancementEntryGui getAdvancementGui(Advancement advancement)
    {
        return this.guis.get(advancement);
    }

    public AdvancementsScreen getScreen()
    {
        return this.screen;
    }
}
